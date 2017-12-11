package com.bt.nextgen.api.fundpayment.service;

import com.bt.nextgen.api.fundpayment.model.Distribution;
import com.bt.nextgen.api.fundpayment.model.FundPaymentNoticeDto;
import com.bt.nextgen.api.fundpayment.model.FundPaymentNoticeSearchDtoKey;
import com.bt.nextgen.api.fundpayment.model.MitWhtDistributionComponentType;
import com.bt.nextgen.api.util.ApiConstants;
import com.bt.nextgen.api.util.SearchUtil;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.fundpaymentnotice.FundPaymentNoticeRequestImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetFundManager;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.fundpaymentnotice.DistributionDetails;
import com.bt.nextgen.service.integration.fundpaymentnotice.FundPaymentNotice;
import com.bt.nextgen.service.integration.fundpaymentnotice.FundPaymentNoticeIntegrationService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class FundPaymentNoticeSearchDtoServiceImpl implements FundPaymentNoticeSearchDtoService
{
    @Autowired
    private FundPaymentNoticeIntegrationService fundPaymentService;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Override public List<FundPaymentNoticeDto> search(FundPaymentNoticeSearchDtoKey key, List<ApiSearchCriteria> criteriaList,
        ServiceErrors serviceErrors)
    {
        FundPaymentNoticeRequestImpl request = new FundPaymentNoticeRequestImpl();
        if (key.getStartDate() != null && key.getEndDate() != null)
        {
            request.setStartDate(key.getStartDate());
            request.setEndDate(key.getEndDate());
        }
        return filterResults(toFundPaymentNotice(fundPaymentService.getFundPaymentNoticeDetails(request, serviceErrors),
            serviceErrors), criteriaList);
    }

    private List<FundPaymentNoticeDto> toFundPaymentNotice(List<FundPaymentNotice> responseList, ServiceErrors serviceErrors)
    {
        List<FundPaymentNoticeDto> fundNoticeList = new ArrayList<>();
        if (responseList != null && responseList.size() > 0)
        {
            DecimalFormat df = new DecimalFormat(ApiConstants.SIX_DECIMAL_PLACES);
            Map<String, Asset> assets = assetService.loadAssets(getAssetIdList(responseList), serviceErrors);
            for (FundPaymentNotice response : responseList)
            {
                Asset asset = assets.get(response.getAsset().getAssetId());
                if (asset != null && StringUtils.isNotBlank(asset.getAssetCode()))
                {
                    FundPaymentNoticeDto fundNotice = new FundPaymentNoticeDto();
                    fundNotice.setCode(asset.getAssetCode());
                    fundNotice.setFundName(asset.getAssetName());
                    if (asset instanceof AssetFundManager) {
                        fundNotice.setFundManager(((AssetFundManager) asset).getFundManager());
                    }
                    fundNotice.setIncomeTaxYear(response.getTaxYear());
                    fundNotice.setDistributionDate(response.getDistributionDate());
                    fundNotice.setDistributionAmount(df.format(response.getDistributionAmount()));
                    if (response.getDistributions() != null)
                    {
                        List<Distribution> list = new ArrayList<>();
                        BigDecimal mitWhtAmount = BigDecimal.ZERO;
                        for (DistributionDetails result : response.getDistributions())
                        {
                            BigDecimal amount = new BigDecimal(result.getDistributionComponentAmount());
                            String component = result.getDistributionComponent();
                            if (amount.compareTo(BigDecimal.ZERO) > 0)
                            {
                                if (MitWhtDistributionComponentType.isMitWhtType(component))
                                {
                                    mitWhtAmount = mitWhtAmount.add(amount);
                                }
                                list.add(new Distribution(component, df.format(amount)));
                            }
                        }
                        fundNotice.setDistributionList(list);
                        fundNotice.setMitWhtAmount(df.format(mitWhtAmount));
                        fundNotice.setAmitNotice(response.isAmitNotice());
                    }
                    fundNoticeList.add(fundNotice);
                }
            }
        }
        return fundNoticeList;
    }

    /*
     * List of all asset id's in the response list is compiled
     * and sent to asset integration service as part of one call
     */
    private List<String> getAssetIdList(List<FundPaymentNotice> responseList)
    {
        List<String> assetIdList = new ArrayList<>();
        for (FundPaymentNotice asset : responseList)
        {
            assetIdList.add(asset.getAsset().getAssetId());
        }
        return assetIdList;
    }

    private List<FundPaymentNoticeDto> filterResults(List<FundPaymentNoticeDto> results, List<ApiSearchCriteria> criteriaList)
    {
        for (Iterator<FundPaymentNoticeDto> iterator = results.iterator(); iterator.hasNext(); )
        {
            FundPaymentNoticeDto fundPayment = iterator.next();

            for (ApiSearchCriteria criteria : criteriaList)
            {
                boolean isFilter = false;
                String value = "";
                try
                {
                    value = BeanUtils.getProperty(fundPayment, criteria.getProperty());
                }
                catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
                {
                    //No property found, don't filter out
                    break;
                }
                switch (criteria.getOperation())
                {
                    case EQUALS:
                        isFilter = !criteria.getValue().equalsIgnoreCase(value);
                        break;
                    case STARTS_WITH:
                        isFilter = !SearchUtil.matches(SearchUtil.getPattern(criteria.getValue()), value);
                        break;
                }
                if (isFilter)
                {
                    iterator.remove();
                    break;
                }
            }
        }
        return results;
    }
}