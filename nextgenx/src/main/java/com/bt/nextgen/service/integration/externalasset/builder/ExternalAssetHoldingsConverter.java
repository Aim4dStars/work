package com.bt.nextgen.service.integration.externalasset.builder;

import com.bt.nextgen.api.smsf.model.*;
import com.bt.nextgen.service.integration.externalasset.model.ExternalAsset;
import com.bt.nextgen.service.integration.externalasset.model.OffPlatformExternalAsset;
import com.bt.nextgen.service.integration.externalasset.model.OnPlatformExternalAsset;
import com.bt.nextgen.api.smsf.constants.AssetClass;
import com.bt.nextgen.api.smsf.constants.AssetType;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Converts an external holdings bean into corresponding DTO
 */
@SuppressWarnings({"findbugs:DLS_DEAD_LOCAL_STORE_OF_NULL","squid:MethodCyclomaticComplexity"})
public final class ExternalAssetHoldingsConverter
{
    private ExternalAssetHoldingsConverter()
    {

    }

	/**
	 * Convert {@link AssetHoldings} to {@link com.bt.nextgen.api.smsf.model.ExternalAssetHoldingsValuationDto}
	 * @param assetHoldings
	 * @return
	 */
    public static ExternalAssetHoldingsValuationDto toExternalAssetHoldingsValuationDto(AssetHoldings assetHoldings)
    {
        ExternalAssetHoldingsValuationDto assetHoldingsValDto = new ExternalAssetHoldingsValuationDto();

        assetHoldingsValDto.setTotalMarketValue(assetHoldings.getTotalMarketValue());
        assetHoldingsValDto.setPercentageOfPortfolio(assetHoldings.getPercentageTotal());
        List<ExternalAssetClassValuationDto> externalAssetClassValuationDto = new ArrayList<>();
        ExternalAssetClassValuationDto assetClassValDto = null;

        for (AssetClassValuation assetClassVal : assetHoldings.getAssetClassValuations())
        {
            assetClassValDto = toExternalAssetClassValuationDto(assetClassVal, assetHoldings);
            externalAssetClassValuationDto.add(assetClassValDto);

        }

		assetHoldingsValDto.setDataFeedLastImportDate(assetHoldings.getDataFeedLastImportDate() != null ? assetHoldings.getDataFeedLastImportDate().toString() : "");
        assetHoldingsValDto.setValuationByAssetClass(externalAssetClassValuationDto);

        verifyAssetsRoundingRule(externalAssetClassValuationDto);

        return assetHoldingsValDto;
    }


    /**
     * This method rounds off the total percentage to 100 if its more than 100 or less than 100 by a fraction of 0.49 max
     *
     *
     *
     * Rounding rule for percentage - Example 1
     Asset	$ Amount		Rounded Amount	Amount Displayed on UI
     Asset 1	$100		33.33	33.34	(increased by 0.01)
     Asset 2	$100		33.33	33.33
     Asset 3	$100		33.33	33.33
     Total	    $300		99.99	100

     In this example the total rounded amount is less than the “correct total” of 100% by 0.01 so the asset with the highest amount (asset #1) needs to be increased by that amount.

     Rounding rule for percentage -Example 2
     Asset	$ Amount		Rounded Amount	Amount Displayed on UI
     Asset 1	$100		33.33	33.32	(reduced by 0.01)
     Asset 2	$50		    16.67	16.67
     Asset 3	$50		    16.67	16.67
     Asset 4	$50		    16.67	16.67
     Asset 5	$50		    16.67	16.67
     Total	    $300		100.01	100

     In this example the total rounded amount exceeds the “correct total” of 100% by 0.01 so the asset with the highest amount (asset #1) needs to be reduced by that amount
     *
     * @param externalAssetClassValuationDto
     */
    public static void verifyAssetsRoundingRule(List<ExternalAssetClassValuationDto> externalAssetClassValuationDto) {

        BigDecimal hundred = new BigDecimal(100);
        BigDecimal fract = BigDecimal.ZERO;
        MathContext scale = new MathContext(4, RoundingMode.HALF_UP);
        ExternalAssetDto assetMaxValueDto = null;
        BigDecimal assetMaxValue = BigDecimal.ZERO;
        BigDecimal totalAssetValue = BigDecimal.ZERO;
        ExternalAssetClassValuationDto maxSectionClassDto = null;

        for (ExternalAssetClassValuationDto assetClassValuationDto: externalAssetClassValuationDto ) {
            List<ExternalAssetDto> externalAssetList = assetClassValuationDto.getAssetList();
            totalAssetValue = BigDecimal.ZERO;
            for (ExternalAssetDto externalAssetDto : externalAssetList) {
                BigDecimal sectionPercent = new BigDecimal(externalAssetDto.getPercentageTotal());
                totalAssetValue = totalAssetValue.add(sectionPercent,scale);    //Summing of all assets percent total to be updated as section percent
                BigDecimal marketValue = BigDecimal.ZERO;
                if (StringUtils.isNotBlank(externalAssetDto.getMarketValue())) {
                    marketValue = new BigDecimal(externalAssetDto.getMarketValue());
                }
                //Adding percentage of all assets
                fract = fract.add(sectionPercent.multiply(hundred).setScale(2, RoundingMode.HALF_UP));
                if (assetMaxValueDto == null) {
                    assetMaxValueDto = externalAssetDto;        //saving first asset with maximum marketvalue
                    assetMaxValue = marketValue;                // saving the marketvalue for further comparision
                    maxSectionClassDto = assetClassValuationDto; // saving the assetClassValuation for section percentage updation
                } else if (marketValue.compareTo(assetMaxValue) > 0) { // comparing maximuma marketvalue with current asset marketvalue
                    assetMaxValueDto = externalAssetDto;        //saving asset with maximum marketvalue
                    assetMaxValue = marketValue;                // saving the maximum marketvalue for further comparision
                    maxSectionClassDto = assetClassValuationDto;// saving the assetClassValuation for section percentage updation
                }
            }
            assetClassValuationDto.setPercentageOfPortfolio(totalAssetValue); //Total percentage summation of all assets percentage
        }
        BigDecimal fraction = hundred.subtract(fract,scale).divide(hundred);
        if (assetMaxValueDto!=null){
            resetPercentageValues(assetMaxValueDto, maxSectionClassDto, fraction);
        }
    }

    public static void resetPercentageValues(ExternalAssetDto assetMaxValueDto, ExternalAssetClassValuationDto maxSectionClassDto, BigDecimal fraction )
    {
        MathContext scale = new MathContext(4, RoundingMode.HALF_UP);
        BigDecimal assetPercent = new BigDecimal(assetMaxValueDto.getPercentageTotal());
        if (fraction.compareTo(assetPercent) < 0) { //Check if fraction is not greater than the percentage value of the maximum marketvalue asset
            assetMaxValueDto.setPercentageTotal(assetPercent.add(fraction, scale).toString()); //update asset percentage to make perfect 100
        }
        if (maxSectionClassDto!=null && fraction.compareTo(BigDecimal.ZERO)>0 && maxSectionClassDto.getPercentageOfPortfolio().compareTo(fraction)>0) { //update corresponding section percentage
            maxSectionClassDto.setPercentageOfPortfolio(maxSectionClassDto.getPercentageOfPortfolio().add(fraction, scale));
        }
    }

    public static ExternalAssetClassValuationDto toExternalAssetClassValuationDto(AssetClassValuation assetClassValuation, AssetHoldings assetHoldings)
    {
        ExternalAssetClassValuationDto assetClassValDto = new ExternalAssetClassValuationDto();

        if (assetClassValuation.getAssetClass() != null)
        {
            assetClassValDto.setAssetClass(assetClassValuation.getAssetClass().getCode());
        }
        else // Default to CASH -- Avaloq needs to provide -- workaround
        {
            assetClassValDto.setAssetClass(AssetClass.CASH.getCode());
        }

        assetClassValDto.setPercentageOfPortfolio(assetClassValuation.getPercentageTotal(assetHoldings.getTotalMarketValue()));
        assetClassValDto.setTotalMarketValue(assetClassValuation.getTotalMarketValue());
        List<ExternalAssetDto> externalAssetList = new ArrayList<>();
        ExternalAssetDto externalAssetDto = null;

        for (ExternalAsset externalAsset : assetClassValuation.getAssets())
        {
            externalAssetDto = toExternalAssetDto(externalAsset, assetHoldings);
            if(null !=externalAssetDto ){
                externalAssetList.add(externalAssetDto);
            }

        }

        Collections.sort(externalAssetList, new Comparator<ExternalAssetDto>() {
           public int compare(ExternalAssetDto a, ExternalAssetDto b)
           {
               return a.getPositionName().compareToIgnoreCase(b.getPositionName());
           }
        });

        assetClassValDto.setAssetList(externalAssetList);
        //verifyAssetsRoundingRule(externalAssetList);

        return assetClassValDto;
    }

    public static ExternalAssetDto toExternalAssetDto(ExternalAsset externalAsset, AssetHoldings assetHoldings)
    {
        ExternalAssetDto externalAssetDto = new ExternalAssetDto();
        if(externalAsset.getMarketValue().compareTo(BigDecimal.ZERO)> 0) {
            if (externalAsset.getAssetClass() != null) {
                externalAssetDto.setAssetClass(externalAsset.getAssetClass().getCode());
            } else    // Default to CASH -- Avaloq needs to provide -- workaround
            {
                externalAssetDto.setAssetClass(AssetClass.CASH.getCode());
            }

            externalAssetDto.setAssetType(externalAsset.getAssetType().getCode());

            if (externalAsset instanceof OnPlatformExternalAsset && ((OnPlatformExternalAsset) externalAsset).getAssetKey() != null) {
                String underlyingAssetId = ((OnPlatformExternalAsset) externalAsset).getAssetKey().getId();
                externalAssetDto.setAssetId(underlyingAssetId);
                externalAssetDto.setAssetCode(((OnPlatformExternalAsset) externalAsset).getAssetCode());
            }

            externalAssetDto.setAssetName(externalAsset.getAssetName());
            externalAssetDto.setPositionId(externalAsset.getPositionIdentifier().getPositionId());
            externalAssetDto.setPositionName(externalAsset.getPositionName());
            externalAssetDto.setPositionCode(externalAsset.getPositionCode());
            externalAssetDto.setPercentageTotal(externalAsset.getPercentageTotal(assetHoldings.getTotalMarketValue()).toString());

            if (externalAsset instanceof OffPlatformExternalAsset && (((OffPlatformExternalAsset) externalAsset).getPropertyType() != null)) {
                externalAssetDto.setPropertyType(((OffPlatformExternalAsset) externalAsset).getPropertyType().getCode().toUpperCase());

            }

            setQuantityOnAssetClass(externalAsset, externalAssetDto);
            externalAssetDto.setMarketValue(externalAsset.getMarketValue().toString());
            externalAssetDto.setSource(externalAsset.getSource());
            externalAssetDto.setValueDate(externalAsset.getValueDate() != null ? externalAsset.getValueDate().toString() : "");
            externalAssetDto.setMaturityDate(externalAsset.getMaturityDate() != null ? externalAsset.getMaturityDate().toString() : "");
            return externalAssetDto;
        }
            return null;
    }


    private static void setQuantityOnAssetClass(ExternalAsset externalAsset, ExternalAssetDto externalAssetDto) {
        //quantity doesn't exists for direct property, cash, TD, MP and required LS, ILS, MF-- OTHERS -> optional
        //Default quantity one not to be displayed
        if (externalAsset.getAssetType().getCode().equals(AssetType.DIRECT_PROPERTY.getCode()) ||
                externalAsset.getAssetType().getCode().equals(AssetType.CASH.getCode()) ||
                externalAsset.getAssetType().getCode().equals(AssetType.MANAGED_PORTFOLIO.getCode()) ||
                externalAsset.getAssetType().getCode().equals(AssetType.TERM_DEPOSIT.getCode()))
        {
            externalAssetDto.setQuantity(null);
        }else {
            externalAssetDto.setQuantity(externalAsset.getQuantity().toString());
        }
    }
}
