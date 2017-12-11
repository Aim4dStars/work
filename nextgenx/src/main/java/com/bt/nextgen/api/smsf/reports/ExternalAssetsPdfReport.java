package com.bt.nextgen.api.smsf.reports;

import com.bt.nextgen.api.smsf.constants.AssetClass;
import com.bt.nextgen.api.smsf.constants.AssetType;
import com.bt.nextgen.api.smsf.constants.PropertyType;
import com.bt.nextgen.api.smsf.model.AssetHoldings;
import com.bt.nextgen.api.smsf.model.ExternalAssetClassValuationDto;
import com.bt.nextgen.api.smsf.model.ExternalAssetDto;
import com.bt.nextgen.api.smsf.model.ExternalAssetHoldingsValuationDto;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.reporting.ReportUtils;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.reports.account.AccountReport;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.externalasset.builder.DateTimeConverter;
import com.bt.nextgen.service.integration.externalasset.builder.ExternalAssetHoldingsConverter;
import com.bt.nextgen.service.integration.externalasset.service.ExternalAssetIntegrationService;
import com.btfin.panorama.core.util.StringUtil;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * Class to generated pdf external assets report
 */

@SuppressWarnings({"squid:S1172"})
@Report(value = "externalAssetsReport", filename = "External assets")
public class ExternalAssetsPdfReport  extends AccountReport {

    private static final String DISCLAIMER_CONTENT = "DS-IP-0062";

    @Autowired
    private ContentDtoService contentService;

    @Autowired
    private ExternalAssetIntegrationService externalAssetIntegrationService;

    /**
     * Convert date in string format "2015-07-15" to "15 Jul 2015"
     * @param inputDate input date to be converted
     */
    private String setDateFormatToDisplayFormat(String inputDate) {
        String toDate = null;
        if (StringUtils.isNotBlank(inputDate) && inputDate.length()>9) {
            String dateToConvert  = inputDate.substring(0,10);
            DateTimeConverter dateTimeConverter = new DateTimeConverter();
            DateTime dateTime = dateTimeConverter.convert(dateToConvert);
            toDate = ApiFormatter.asShortDate(dateTime);
        }
        return toDate;
    }

    private void setClassOrder(ExternalAssetHoldingsValuationDto externalAssetHoldingsValDto, Map <String, ExternalAssetClassValuationDto>classOrder) {

        List <ExternalAssetClassValuationDto> classValuationDtoList = new ArrayList<>();

        for (AssetClass assetClass: AssetClass.values()){
            String classId = assetClass.getCode();
            if (classOrder.get(classId)!=null) {
                classValuationDtoList.add(classOrder.get(classId));
            }
        }
        externalAssetHoldingsValDto.setValuationByAssetClass(classValuationDtoList);
    }
    private void setPdfDetails(ExternalAssetHoldingsValuationDto externalAssetHoldingsValDto, Map<String, ExternalAssetHoldingsValuationDto> reportNameMap) {

        BigDecimal hundred = new BigDecimal("100");

        Map <String, ExternalAssetClassValuationDto>classOrder = new HashMap();

        for ( ExternalAssetClassValuationDto externalAssetClassValuationDto : externalAssetHoldingsValDto.getValuationByAssetClass() ) {
            classOrder.put(externalAssetClassValuationDto.getAssetClass(), externalAssetClassValuationDto);
            String classDesc = StringUtil.toProperCase(AssetClass.getByCode(externalAssetClassValuationDto.getAssetClass()).getDescription());
            externalAssetClassValuationDto.setAssetClass(classDesc);
            externalAssetClassValuationDto.setPercentageOfPortfolio(externalAssetClassValuationDto.getPercentageOfPortfolio().multiply(hundred));

            String classType = externalAssetClassValuationDto.getAssetClass();

            for (ExternalAssetDto externalAssetDto : externalAssetClassValuationDto.getAssetList()){
                if(StringUtils.isNotBlank(externalAssetDto.getPropertyType())){
                    String property = PropertyType.getByCode(externalAssetDto.getPropertyType().toLowerCase()).getShortDesc();
                    externalAssetDto.setPositionName(property + " - " + externalAssetDto.getPositionName());
                }
                else if (StringUtils.isNotBlank(externalAssetDto.getPositionCode())){
                    externalAssetDto
                            .setPositionName(externalAssetDto.getPositionCode() + " • " + externalAssetDto.getPositionName());
                }
                BigDecimal percent = new BigDecimal(externalAssetDto.getPercentageTotal());
                String percentage = ApiFormatter.asDecimal(percent.multiply(hundred));
                externalAssetDto.setPercentageTotal(percentage); //multiply 100
                externalAssetDto.setValueDate(setDateFormatToDisplayFormat(externalAssetDto.getValueDate()));
                externalAssetDto.setMaturityDate(setDateFormatToDisplayFormat(externalAssetDto.getMaturityDate()));
                if (StringUtils.isNotBlank(externalAssetDto.getQuantity())) {
                    externalAssetDto.setQuantity(ApiFormatter.asIntegerString(new BigDecimal(externalAssetDto.getQuantity())));
                }
                if (StringUtils.isNotBlank(externalAssetDto.getMarketValue())) {
                    BigDecimal marketValue = new BigDecimal(externalAssetDto.getMarketValue());
                    externalAssetDto.setMarketValue((String)ReportUtils.toCurrencyString(marketValue));
                }
            }
            if ( classType.equals(AssetClass.CASH.getDescription())) {
                setClassSectionValuationDto(externalAssetHoldingsValDto, externalAssetClassValuationDto, reportNameMap);
            }
        }
        setClassOrder(externalAssetHoldingsValDto,classOrder);
        reportNameMap.put("all",externalAssetHoldingsValDto);

    }

    private void setClassSectionValuationDto(ExternalAssetHoldingsValuationDto externalAssetHoldingsValDto, ExternalAssetClassValuationDto externalAssetClassValuationDto,  Map<String, ExternalAssetHoldingsValuationDto> reportNameMap) {
            ListIterator iterator = externalAssetClassValuationDto.getAssetList().listIterator();
            ExternalAssetHoldingsValuationDto cashTdHoldingsValuationDto = getHoldingObject(externalAssetHoldingsValDto);
            ExternalAssetHoldingsValuationDto cashOtherHoldingsValuationDto = getHoldingObject(externalAssetHoldingsValDto);
            List<ExternalAssetDto> cashExternalAssetDtos = new ArrayList<>();
            List<ExternalAssetDto> otherCashExternalAssetDtos = new ArrayList<>();
            while (iterator.hasNext()) {
                ExternalAssetDto externalAssetDto = (ExternalAssetDto) iterator.next();
                if (externalAssetDto.getAssetType().equals(AssetType.CASH.getCode()) || externalAssetDto.getAssetType().equals(AssetType.TERM_DEPOSIT.getCode())) {
                    cashExternalAssetDtos.add(externalAssetDto);
                }else{
                    otherCashExternalAssetDtos.add(externalAssetDto);
                }
                iterator.remove();
            }
            ExternalAssetClassValuationDto cashClassValuationDto = setClassValuationDto(externalAssetClassValuationDto, cashExternalAssetDtos);
            ExternalAssetClassValuationDto otherCashClassValuationDto = setClassValuationDto(externalAssetClassValuationDto, otherCashExternalAssetDtos);
            cashTdHoldingsValuationDto.getValuationByAssetClass().add(cashClassValuationDto);
            cashOtherHoldingsValuationDto.getValuationByAssetClass().add(otherCashClassValuationDto);
            reportNameMap.put("cashtd",cashTdHoldingsValuationDto);
            reportNameMap.put("othercash",cashOtherHoldingsValuationDto);
    }

    private ExternalAssetClassValuationDto setClassValuationDto(ExternalAssetClassValuationDto externalAssetClassValuationDto, List<ExternalAssetDto> externalAssetDtos) {
        ExternalAssetClassValuationDto classValuationDto = new ExternalAssetClassValuationDto();
        classValuationDto.setPercentageOfPortfolio(externalAssetClassValuationDto.getPercentageOfPortfolio());
        classValuationDto.setTotalMarketValue(externalAssetClassValuationDto.getTotalMarketValue());
        classValuationDto.setAssetClass(externalAssetClassValuationDto.getAssetClass());
        if (CollectionUtils.isNotEmpty(externalAssetDtos)) {
            classValuationDto.setAssetList(externalAssetDtos);
        } else {
            classValuationDto.setAssetList(new ArrayList<ExternalAssetDto>());
        }
        return classValuationDto;
    }


    private ExternalAssetHoldingsValuationDto getHoldingObject(ExternalAssetHoldingsValuationDto externalAssetHoldingsValDto) {
        ExternalAssetHoldingsValuationDto assetHoldingsValuationDto = new ExternalAssetHoldingsValuationDto();
        assetHoldingsValuationDto.setTotalMarketValue(externalAssetHoldingsValDto.getTotalMarketValue());
        assetHoldingsValuationDto.setPercentageOfPortfolio(externalAssetHoldingsValDto.getPercentageOfPortfolio());
        List<ExternalAssetClassValuationDto> externalAssetClassValuationDtos = new ArrayList<>();
        assetHoldingsValuationDto.setValuationByAssetClass(externalAssetClassValuationDtos);
        return assetHoldingsValuationDto;
    }


    @ReportBean("externalAssetsMapDto")
    public Map<String, ExternalAssetHoldingsValuationDto> getExternalAssetsHoldingValuation(Map<String, String> params) {

        String accountId = params.get(UriMappingConstants.ACCOUNT_ID_URI_MAPPING);
        Map<String, ExternalAssetHoldingsValuationDto> reportNameMap = new HashMap<>();

        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(accountId));
        List<AccountKey> accountKeys = new ArrayList<>();
        accountKeys.add(accountKey);

        AssetHoldings assetHoldings = externalAssetIntegrationService.getExternalAssets(accountKeys, new DateTime());
        ExternalAssetHoldingsValuationDto externalAssetHoldingsValDto = ExternalAssetHoldingsConverter.toExternalAssetHoldingsValuationDto(assetHoldings);
        setPdfDetails(externalAssetHoldingsValDto,reportNameMap);
        return reportNameMap;
    }


    @ReportBean("valuationDate")
    public DateTime getStartDate(Map <String, String> params)
    {
        return new DateTime();
    }

    @ReportBean("reportType")
    public String getReportName(Map <String, String> params)
    {
        return "External assets valuation";
    }

    @ReportBean("disclaimer")
    public String getDisclaimer(Map <String, String> params)
    {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        ContentKey key = new ContentKey(DISCLAIMER_CONTENT);
        ContentDto content = contentService.find(key, serviceErrors);
        return content!=null ? content.getContent() : "";
    }

    /**
     * @inheritDoc
     */
    @Override
    @PreAuthorize("@acctPermissionService.canTransact(#root.this.getAccountEncodedId(#params), 'account.portfolio.externalassets.view')")
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        return super.getData(params, dataCollections);
    }

}
