package com.bt.nextgen.service.integration.externalassets.builder;

import com.bt.nextgen.api.smsf.model.*;
import com.bt.nextgen.service.avaloq.PositionIdentifierImpl;
import com.bt.nextgen.service.integration.externalasset.model.OnPlatformExternalAssetImpl;
import com.bt.nextgen.service.integration.externalasset.model.ExternalAsset;
import com.bt.nextgen.service.integration.externalasset.builder.ExternalAssetHoldingsConverter;
import com.bt.nextgen.api.smsf.constants.AssetClass;
import com.bt.nextgen.api.smsf.constants.AssetType;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ExternalAssetHoldingsConverterTest
{

    @Test
    public void assetClassOrder()
    {
        //Verify the assetClass order is maintained 
        AssetClass[] assetClasses =  AssetClass.values();
        assertTrue(assetClasses[0].equals(AssetClass.CASH));
        assertTrue(assetClasses[1].equals(AssetClass.AUSTRALIAN_FIXED_INTEREST));
        assertTrue(assetClasses[2].equals(AssetClass.INTERNATIONAL_FIXED_INTEREST));
        assertTrue(assetClasses[3].equals(AssetClass.AUSTRALIAN_FLOATING_RATE_INTEREST));
        assertTrue(assetClasses[4].equals(AssetClass.AUSTRALIAN_LISTED_SECURITIES));
        assertTrue(assetClasses[5].equals(AssetClass.INTERNATIONAL_LISTED_SECURITIES));
        assertTrue(assetClasses[6].equals(AssetClass.AUSTRALIAN_REAL_ESTATE));
        assertTrue(assetClasses[7].equals(AssetClass.INTERNATIONAL_REAL_ESTATE));
        assertTrue(assetClasses[8].equals(AssetClass.ALTERNATIVES));
        assertTrue(assetClasses[9].equals(AssetClass.DIVERSIFIED));
        assertTrue(assetClasses[10].equals(AssetClass.OTHER_ASSET));
    }

    private BigDecimal getPercentageAsset(BigDecimal totalPortfolioMarketValue, BigDecimal marketValue)
    {

        BigDecimal percentageTotal = BigDecimal.ZERO;

        try
        {
            if (totalPortfolioMarketValue.compareTo(BigDecimal.ZERO) >= 0 && marketValue != null)
            {
                percentageTotal = marketValue.divide(totalPortfolioMarketValue, 4, RoundingMode.HALF_UP);
            }
        }
        catch (ArithmeticException ae)
        {
            //logger.warn("Unable to calculate percentage total for asset classification. Defaulting to zero.", ae);
        }

        return percentageTotal;


    }

    @Test
    public void percentageRoundingSectionTest() {
        BigDecimal totalPortfolioMarketValue = new BigDecimal("2700");
        List<ExternalAssetClassValuationDto> externalAssetClassValuationDtoList = new ArrayList<>();

        ExternalAssetClassValuationDto externalAssetClassValuationDto = new ExternalAssetClassValuationDto();
        ExternalAssetClassValuationDto externalAssetClassValuationDto2 = new ExternalAssetClassValuationDto();
        ExternalAssetClassValuationDto externalAssetClassValuationDto3 = new ExternalAssetClassValuationDto();

        List<ExternalAssetDto> externalAssetList = new ArrayList<>();
        ExternalAssetDto externalAssetDto = new ExternalAssetDto();
        externalAssetDto.setMarketValue("900");
        externalAssetDto.setPercentageTotal(getPercentageAsset(totalPortfolioMarketValue,new BigDecimal("900")).toString());
        externalAssetList.add(externalAssetDto);

        List<ExternalAssetDto> externalAssetList2 = new ArrayList<>();
        externalAssetDto = new ExternalAssetDto();
        externalAssetDto.setMarketValue("900");
        externalAssetDto.setPercentageTotal(getPercentageAsset(totalPortfolioMarketValue,new BigDecimal("900")).toString());
        externalAssetList2.add(externalAssetDto);

        List<ExternalAssetDto> externalAssetList3 = new ArrayList<>();
        externalAssetDto = new ExternalAssetDto();
        externalAssetDto.setMarketValue("901");
        externalAssetDto.setPercentageTotal(getPercentageAsset(totalPortfolioMarketValue,new BigDecimal("900")).toString());
        externalAssetList3.add(externalAssetDto);

        externalAssetClassValuationDto.setAssetClass(AssetClass.CASH.getCode());
        externalAssetClassValuationDto.setAssetList(externalAssetList);
        externalAssetClassValuationDto.setPercentageOfPortfolio(getPercentageAsset(totalPortfolioMarketValue,new BigDecimal("2700")));
        externalAssetClassValuationDto2.setAssetClass(AssetClass.OTHER_ASSET.getCode());
        externalAssetClassValuationDto2.setAssetList(externalAssetList2);
        externalAssetClassValuationDto2.setPercentageOfPortfolio(getPercentageAsset(totalPortfolioMarketValue,new BigDecimal("2700")));
        externalAssetClassValuationDto3.setAssetClass(AssetClass.AUSTRALIAN_LISTED_SECURITIES.getCode());
        externalAssetClassValuationDto3.setAssetList(externalAssetList3);
        externalAssetClassValuationDto3.setPercentageOfPortfolio(getPercentageAsset(totalPortfolioMarketValue,new BigDecimal("2700")));

        externalAssetClassValuationDtoList.add(externalAssetClassValuationDto);
        externalAssetClassValuationDtoList.add(externalAssetClassValuationDto2);
        externalAssetClassValuationDtoList.add(externalAssetClassValuationDto3);
        ExternalAssetHoldingsConverter.verifyAssetsRoundingRule(externalAssetClassValuationDtoList);

        ExternalAssetClassValuationDto updatedClassValuation = null;
        for (ExternalAssetClassValuationDto externalAssetClassValuationDto1 : externalAssetClassValuationDtoList) {
             if(externalAssetClassValuationDto1.getAssetClass().equals(AssetClass.AUSTRALIAN_LISTED_SECURITIES.getCode())) {
                updatedClassValuation = externalAssetClassValuationDto1;
            }
        }
        assertEquals(updatedClassValuation.getPercentageOfPortfolio(),new BigDecimal("0.3334"));
        assertEquals(updatedClassValuation.getAssetList().get(0).getPercentageTotal(),"0.3334");

    }

    @Test
    public void percentagePositiveRoundingAssetTest() {
        BigDecimal totalPortfolioMarketValue = new BigDecimal("2700");
        List<ExternalAssetClassValuationDto> externalAssetClassValuationDtoList = new ArrayList<>();

        ExternalAssetClassValuationDto externalAssetClassValuationDto = new ExternalAssetClassValuationDto();

        List<ExternalAssetDto> externalAssetList = new ArrayList<>();
        ExternalAssetDto externalAssetDto = new ExternalAssetDto();
        externalAssetDto.setMarketValue("900");
        externalAssetDto.setPercentageTotal(getPercentageAsset(totalPortfolioMarketValue,new BigDecimal("900")).toString());
        externalAssetList.add(externalAssetDto);

        externalAssetDto = new ExternalAssetDto();
        externalAssetDto.setMarketValue("900");
        externalAssetDto.setPercentageTotal(getPercentageAsset(totalPortfolioMarketValue,new BigDecimal("900")).toString());
        externalAssetList.add(externalAssetDto);

        externalAssetDto = new ExternalAssetDto();
        externalAssetDto.setMarketValue("900");
        externalAssetDto.setPercentageTotal(getPercentageAsset(totalPortfolioMarketValue,new BigDecimal("900")).toString());
        externalAssetList.add(externalAssetDto);

        externalAssetClassValuationDto.setAssetList(externalAssetList);
        externalAssetClassValuationDto.setPercentageOfPortfolio(getPercentageAsset(totalPortfolioMarketValue,new BigDecimal("2700")));

        externalAssetClassValuationDtoList.add(externalAssetClassValuationDto);
        BigDecimal totalPercentActual = BigDecimal.ZERO;
        for (ExternalAssetClassValuationDto assetClassValDto : externalAssetClassValuationDtoList)
        {
            for (ExternalAssetDto actualExternalAssetDto:assetClassValDto.getAssetList()) {
                totalPercentActual = totalPercentActual.add(new BigDecimal(actualExternalAssetDto.getPercentageTotal()));

            }
        }

        ExternalAssetHoldingsConverter.verifyAssetsRoundingRule(externalAssetClassValuationDtoList);

        BigDecimal totalPercent = BigDecimal.ZERO;
        for (ExternalAssetClassValuationDto assetClassValDto : externalAssetClassValuationDtoList)
        {
            for (ExternalAssetDto newExternalAssetDto:assetClassValDto.getAssetList()) {
                totalPercent = totalPercent.add(new BigDecimal(newExternalAssetDto.getPercentageTotal()));

            }
        }

        assertNotEquals(totalPercentActual, new BigDecimal("1.0000"));
        assertEquals(totalPercent, new BigDecimal("1.0000"));

    }


    @Test
    public void percentageNegativeRoundingAssetTest() {
        BigDecimal totalPortfolioMarketValue = new BigDecimal("300");
        List<ExternalAssetClassValuationDto> externalAssetClassValuationDtoList = new ArrayList<>();

        ExternalAssetClassValuationDto externalAssetClassValuationDto = new ExternalAssetClassValuationDto();

        List<ExternalAssetDto> externalAssetList = new ArrayList<>();
        ExternalAssetDto externalAssetDto = new ExternalAssetDto();
        externalAssetDto.setMarketValue("100");
        externalAssetDto.setPercentageTotal(getPercentageAsset(totalPortfolioMarketValue,new BigDecimal(externalAssetDto.getMarketValue())).toString());
        externalAssetList.add(externalAssetDto);

        externalAssetDto = new ExternalAssetDto();
        externalAssetDto.setMarketValue("50");
        externalAssetDto.setPercentageTotal(getPercentageAsset(totalPortfolioMarketValue,new BigDecimal(externalAssetDto.getMarketValue())).toString());
        externalAssetList.add(externalAssetDto);

        externalAssetDto = new ExternalAssetDto();
        externalAssetDto.setMarketValue("50");
        externalAssetDto.setPercentageTotal(getPercentageAsset(totalPortfolioMarketValue,new BigDecimal(externalAssetDto.getMarketValue())).toString());
        externalAssetList.add(externalAssetDto);

        externalAssetDto = new ExternalAssetDto();
        externalAssetDto.setMarketValue("50");
        externalAssetDto.setPercentageTotal(getPercentageAsset(totalPortfolioMarketValue,new BigDecimal(externalAssetDto.getMarketValue())).toString());
        externalAssetList.add(externalAssetDto);

        externalAssetDto = new ExternalAssetDto();
        externalAssetDto.setMarketValue("50");
        externalAssetDto.setPercentageTotal(getPercentageAsset(totalPortfolioMarketValue,new BigDecimal(externalAssetDto.getMarketValue())).toString());
        externalAssetList.add(externalAssetDto);

        externalAssetClassValuationDto.setAssetList(externalAssetList);
        externalAssetClassValuationDto.setPercentageOfPortfolio(getPercentageAsset(totalPortfolioMarketValue,totalPortfolioMarketValue));

        externalAssetClassValuationDtoList.add(externalAssetClassValuationDto);
        BigDecimal totalPercentActual = BigDecimal.ZERO;
        for (ExternalAssetClassValuationDto assetClassValDto : externalAssetClassValuationDtoList)
        {
            for (ExternalAssetDto actualExternalAssetDto:assetClassValDto.getAssetList()) {
                totalPercentActual = totalPercentActual.add(new BigDecimal(actualExternalAssetDto.getPercentageTotal()));

            }
        }

        ExternalAssetHoldingsConverter.verifyAssetsRoundingRule(externalAssetClassValuationDtoList);

        BigDecimal totalPercent = BigDecimal.ZERO;
        for (ExternalAssetClassValuationDto assetClassValDto : externalAssetClassValuationDtoList)
        {
            for (ExternalAssetDto newExternalAssetDto:assetClassValDto.getAssetList()) {
                totalPercent = totalPercent.add(new BigDecimal(newExternalAssetDto.getPercentageTotal()));

            }
        }

        assertNotEquals(totalPercentActual, new BigDecimal("1.0000"));
        assertEquals(totalPercent, new BigDecimal("1.0000"));

    }


    @Test
    public void convertToExternalAssetDto()
    {
        AssetHoldings assetHoldings = Mockito.mock(AssetHoldings.class);
        Mockito.when(assetHoldings.getTotalMarketValue()).thenReturn(new BigDecimal(1000));
        ExternalAssetDto dto = ExternalAssetHoldingsConverter.toExternalAssetDto(getListedSecurityExternalAsset(), assetHoldings);

        assertEquals("79.0000", dto.getPercentageTotal());
        assertEquals("CBA Position", dto.getPositionCode());
        assertEquals("66700", dto.getPositionId());
        assertEquals("Commonwealth Bank of Australia", dto.getAssetName());
        assertEquals("ETRADE", dto.getSource());
        assertEquals("2015-05-15", new DateTime(dto.getValueDate()).toLocalDate().toString("yyyy-MM-dd"));
    }

    @Test
    public void convertToAssetClassValuationDto()
    {
        AssetHoldings assetHoldings = Mockito.mock(AssetHoldings.class);
        Mockito.when(assetHoldings.getTotalMarketValue()).thenReturn(new BigDecimal(1000));
        ExternalAssetDto dto = ExternalAssetHoldingsConverter.toExternalAssetDto(getListedSecurityExternalAsset(), assetHoldings);

        ExternalAssetClassValuationDto valuationDto = ExternalAssetHoldingsConverter.toExternalAssetClassValuationDto(getListedSecurityAssetClassValuation(), assetHoldings);
        assertEquals("59000", valuationDto.getTotalMarketValue().toString());
        assertEquals(new BigDecimal("64"), valuationDto.getPercentageOfPortfolio());
        assertEquals(1, valuationDto.getAssetList().size());
    }

    @Test
    public void convertToAssetClassValuationWithMarketValueDto()
    {
        AssetHoldings assetHoldings = Mockito.mock(AssetHoldings.class);
        Mockito.when(assetHoldings.getTotalMarketValue()).thenReturn(new BigDecimal(1000));
        ExternalAssetDto dto = ExternalAssetHoldingsConverter.toExternalAssetDto( getListedSecurityExternalAssetWithMarketValue(), assetHoldings);

        ExternalAssetClassValuationDto valuationDto = ExternalAssetHoldingsConverter.toExternalAssetClassValuationDto(getListedSecurityAssetClassValuationWithMarketValue(), assetHoldings);
        assertEquals("59000", valuationDto.getTotalMarketValue().toString());
        assertEquals(new BigDecimal("64"), valuationDto.getPercentageOfPortfolio());
        assertEquals(0, valuationDto.getAssetList().size());
    }

    @Test
    public void convertToAssetClassHoldingDto()
    {
        ExternalAssetHoldingsValuationDto holdings = ExternalAssetHoldingsConverter.toExternalAssetHoldingsValuationDto(getAssetHoldings());

        assertEquals(new BigDecimal(20000), holdings.getTotalMarketValue());
        assertEquals(new BigDecimal(50), holdings.getPercentageOfPortfolio());
        assertEquals(1, holdings.getValuationByAssetClass().size());
    }

    public ExternalAsset getListedSecurityExternalAsset()
    {
        ExternalAsset extAsset = new OnPlatformExternalAssetImpl();

        extAsset.setAssetType(AssetType.AUSTRALIAN_LISTED_SECURITIES);
        extAsset.setAssetClass(AssetClass.AUSTRALIAN_LISTED_SECURITIES);
        extAsset.setQuantity(new BigDecimal(100));
        extAsset.setValueDate(new DateTime().withDate(2015, 05, 15));
        extAsset.setPositionCode("CBA Position");
        extAsset.setPositionIdentifier(new PositionIdentifierImpl("66700"));
        extAsset.setAssetName("Commonwealth Bank of Australia");
        extAsset.setMarketValue(new BigDecimal(79000));
        extAsset.setSource("ETRADE");

        return extAsset;
    }

    public ExternalAsset getListedSecurityExternalAssetWithMarketValue()
    {
        ExternalAsset extAsset = new OnPlatformExternalAssetImpl();

        extAsset.setAssetType(AssetType.AUSTRALIAN_LISTED_SECURITIES);
        extAsset.setAssetClass(AssetClass.AUSTRALIAN_LISTED_SECURITIES);
        extAsset.setQuantity(new BigDecimal(100));
        extAsset.setValueDate(new DateTime().withDate(2015, 05, 15));
        extAsset.setPositionCode("CBA Position");
        extAsset.setPositionIdentifier(new PositionIdentifierImpl("66700"));
        extAsset.setAssetName("Commonwealth Bank of Australia");
        extAsset.setMarketValue(new BigDecimal(0));
        extAsset.setSource("ETRADE");

        return extAsset;
    }

    public AssetClassValuation getListedSecurityAssetClassValuation()
    {
        AssetClassValuation valuation = Mockito.mock(AssetClassValuation.class);

        List<ExternalAsset> extAssetList = new ArrayList();
        extAssetList.add(getListedSecurityExternalAsset());
        Mockito.when(valuation.getAssets()).thenReturn(extAssetList);
        Mockito.when(valuation.getAssetClass()).thenReturn(AssetClass.AUSTRALIAN_LISTED_SECURITIES);
        Mockito.when(valuation.getPercentageTotal(Mockito.any(BigDecimal.class))).thenReturn(new BigDecimal(64.00));
        Mockito.when(valuation.getTotalMarketValue()).thenReturn(new BigDecimal(59000));

        return valuation;
    }

    public AssetClassValuation getListedSecurityAssetClassValuationWithMarketValue()
    {
        AssetClassValuation valuation = Mockito.mock(AssetClassValuation.class);

        List<ExternalAsset> extAssetList = new ArrayList();
        extAssetList.add(getListedSecurityExternalAssetWithMarketValue());
        Mockito.when(valuation.getAssets()).thenReturn(extAssetList);
        Mockito.when(valuation.getAssetClass()).thenReturn(AssetClass.AUSTRALIAN_LISTED_SECURITIES);
        Mockito.when(valuation.getPercentageTotal(Mockito.any(BigDecimal.class))).thenReturn(new BigDecimal(64.00));
        Mockito.when(valuation.getTotalMarketValue()).thenReturn(new BigDecimal(59000));

        return valuation;
    }


    public AssetHoldings getAssetHoldings()
    {
        AssetHoldings holdings = Mockito.mock(AssetHoldings.class);
        Mockito.when(holdings.getTotalMarketValue()).thenReturn(new BigDecimal(20000));
        Mockito.when(holdings.getPercentageTotal()).thenReturn(new BigDecimal(50.00));

        List assetClassValuations = new ArrayList<>();
        assetClassValuations.add(getListedSecurityAssetClassValuation());
        Mockito.when(holdings.getAssetClassValuations()).thenReturn(assetClassValuations);

        List<ExternalAsset> externalAssets = new ArrayList<>();
        externalAssets.add(getListedSecurityExternalAsset());
        Mockito.when(holdings.getAllAssets()).thenReturn(externalAssets);

        return holdings;
    }


}
