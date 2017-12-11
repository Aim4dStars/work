package com.bt.nextgen.api.trading.v1.service;

import com.bt.nextgen.api.trading.v1.model.TradableInvestmentOptionDto;
import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementInterface;
import com.bt.nextgen.service.integration.ips.IpsKey;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TradableInvestmentOptionsDtoServiceImpl implements TradableInvestmentOptionsDtoService {


    @Autowired
    private InvestmentPolicyStatementIntegrationService ipsService;

    @Autowired
    private TradableAssetsDtoService tradableAssetsDtoService;

    @Autowired
    private StaticIntegrationService staticIntegrationService;

    private static final Logger logger = LoggerFactory.getLogger(TradableInvestmentOptionsDtoServiceImpl.class);

    @Override
    public List<TradableInvestmentOptionDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        final List<TradeAssetDto> availableAssets = tradableAssetsDtoService.search(criteriaList, serviceErrors);
        final Map<IpsKey, InvestmentPolicyStatementInterface> ipsDetailsMap = ipsService
                .getInvestmentPolicyStatements(serviceErrors);
        if (CollectionUtils.isNotEmpty(availableAssets) && ipsDetailsMap != null) {
            return toInvestmentOptionDto(ipsDetailsMap, availableAssets, serviceErrors);
        }
        logger.info("{}  ");
        return new ArrayList<>();
    }

    /**
     * Converts the response from Avaloq to Dto object
     * 
     * @param ipsDetailsMap
     *            - List of investment options from Avaloq
     * @param serviceErrors
     *            - Service error
     * @return List of investment options Dto objects
     */
    private List<TradableInvestmentOptionDto> toInvestmentOptionDto(
            Map<IpsKey, InvestmentPolicyStatementInterface> ipsDetailsMap, List<TradeAssetDto> availableAssets,
            ServiceErrors serviceErrors) {
        List<TradableInvestmentOptionDto> tradableInvestmentOptionList = new ArrayList<>();
        for (TradeAssetDto tradeAssetDto : availableAssets) {
            final InvestmentPolicyStatementInterface ips = ipsDetailsMap.get(IpsKey.valueOf(tradeAssetDto.getAsset().getIpsId()));
            if (ips != null) {
                final Code investmentStyleCode = staticIntegrationService.loadCode(CodeCategory.IPS_INVESTMENT_STYLE,
                        ips.getInvestmentStyleId(), serviceErrors);
                tradableInvestmentOptionList.add(new TradableInvestmentOptionDto(tradeAssetDto, ips,
                        investmentStyleCode != null ? investmentStyleCode.getName() : null));
            }
        }

        return tradableInvestmentOptionList;
    }

}