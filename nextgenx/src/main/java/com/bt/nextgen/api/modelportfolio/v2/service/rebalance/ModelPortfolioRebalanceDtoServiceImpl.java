package com.bt.nextgen.api.modelportfolio.v2.service.rebalance;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.ModelPortfolioRebalanceDto;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.ModelPortfolioRebalanceTriggerDetailsDto;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.ModelPortfolioRebalanceTriggerDto;
import com.bt.nextgen.api.modelportfolio.v2.util.common.ModelPortfolioHelper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.modelportfolio.RebalanceAction;
import com.bt.nextgen.service.avaloq.modelportfolio.TriggerStatus;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementInterface;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioSummary;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioSummaryIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.common.IpsStatus;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalance;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalanceIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalanceTrigger;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalanceTriggerDetails;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelRebalanceStatus;
import com.btfin.panorama.core.conversion.CodeCategory;
import org.hamcrest.core.IsEqual;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.index;
import static ch.lambdaj.Lambda.max;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;

@Service("ModelPortfolioRebalanceDtoServiceV2")
@Transactional(value = "springJpaTransactionManager")
public class ModelPortfolioRebalanceDtoServiceImpl implements ModelPortfolioRebalanceDtoService {

    private static final String SCAN_TRIGGER = "Full Scan";

    @Autowired
    private ModelPortfolioRebalanceIntegrationService modelPortfolioRebalanceIntegrationService;

    @Autowired
    private ModelPortfolioSummaryIntegrationService modelPortfolioIntegrationService;

    @Autowired
    private ModelPortfolioHelper helper;

    @Autowired
    private InvestmentPolicyStatementIntegrationService investmentPolicyStatementIntegrationService;

    @Autowired
    private StaticIntegrationService staticIntegrationService;

    @Override
    public List<ModelPortfolioRebalanceDto> findAll(ServiceErrors serviceErrors) {

        BrokerKey broker = helper.getCurrentBroker(serviceErrors);

        List<ModelPortfolioSummary> allModels = new ArrayList<>(
                modelPortfolioIntegrationService.loadModels(broker, serviceErrors));
        List<ModelPortfolioRebalance> rebalanceResponse = modelPortfolioRebalanceIntegrationService
                .loadModelPortfolioRebalances(broker, serviceErrors);
        Map<IpsKey, ModelPortfolioRebalance> rebalanceMap = index(rebalanceResponse,
                on(ModelPortfolioRebalance.class).getIpsKey());
        Map<IpsKey, InvestmentPolicyStatementInterface> ipsMap = investmentPolicyStatementIntegrationService
                .getInvestmentPolicyStatements(new ArrayList<>(rebalanceMap.keySet()), serviceErrors);

        return buildModelPortfolioRebalanceDto(allModels, rebalanceMap, ipsMap, serviceErrors);
    }

    private List<ModelPortfolioRebalanceDto> buildModelPortfolioRebalanceDto(List<ModelPortfolioSummary> allModels,
            Map<IpsKey, ModelPortfolioRebalance> rebalanceMap, Map<IpsKey, InvestmentPolicyStatementInterface> ipsMap,
            ServiceErrors serviceErrors) {

        List<ModelPortfolioRebalanceDto> rebalanceDtos = new ArrayList<ModelPortfolioRebalanceDto>();
        for (ModelPortfolioSummary model : allModels) {
            if (IpsStatus.OPEN == model.getStatus() || IpsStatus.PENDING == model.getStatus()
                    || IpsStatus.CLOSED_TO_NEW == model.getStatus() || IpsStatus.SUSPENDED == model.getStatus()) {
                ModelPortfolioRebalance rebalance = rebalanceMap.get(model.getModelKey());
                ModelPortfolioRebalanceDto dto;
                if (rebalance == null) {
                    dto = toModelPortfolioRebalanceDto(model, ipsMap, serviceErrors);
                } else {
                    dto = toModelPortfolioRebalanceDto(model, rebalance, ipsMap, serviceErrors);
                }
                if (dto != null) {
                    rebalanceDtos.add(dto); // dto will be null if the ips is not cached
                }
            }
        }

        return rebalanceDtos;
    }

    private ModelPortfolioRebalanceDto toModelPortfolioRebalanceDto(ModelPortfolioSummary model,
            ModelPortfolioRebalance rebalance, Map<IpsKey, InvestmentPolicyStatementInterface> ipsMap,
            ServiceErrors serviceErrors) {
        if (ipsMap.get(rebalance.getIpsKey()) == null) {
            return null;
        }

        ModelPortfolioKey rebalanceKey = new ModelPortfolioKey(rebalance.getIpsKey().getId());
        String ipsStatus = staticIntegrationService.loadCode(CodeCategory.IPS_STATUS, rebalance.getIpsStatus(), serviceErrors)
                .getName();

        InvestmentPolicyStatementInterface ips = ipsMap.get(model.getModelKey());
        String modelName = ips == null ? "INVESTMENT_NAME" : ips.getInvestmentName();
        String modelCode = ips == null ? "CODE" : ips.getCode();

        ModelPortfolioRebalanceDto rebalanceDto = new ModelPortfolioRebalanceDto(rebalanceKey, rebalance, ipsStatus, model,
                modelName, modelCode);

        addFullScanTrigger(model, rebalance, rebalanceDto, serviceErrors);

        for (ModelPortfolioRebalanceTrigger rebalanceTrigger : rebalance.getRebalanceTriggers()) {
            rebalanceDto.getRebalanceTriggers().add(toModelPortfolioRebalanceTriggerDto(rebalanceTrigger, serviceErrors));
        }

        return rebalanceDto;
    }

    private ModelPortfolioRebalanceDto toModelPortfolioRebalanceDto(ModelPortfolioSummary model,
            Map<IpsKey, InvestmentPolicyStatementInterface> ipsMap, ServiceErrors serviceErrors) {
        if (ipsMap.get(model.getModelKey()) == null) {
            return null;
        }

        ModelPortfolioKey modelKey = new ModelPortfolioKey(model.getModelKey().getId());

        InvestmentPolicyStatementInterface ips = ipsMap.get(model.getModelKey());
        String modelName = ips == null ? "INVESTMENT_NAME" : ips.getInvestmentName();
        String modelCode = ips == null ? "CODE" : ips.getCode();

        ModelPortfolioRebalanceDto rebalanceDto = new ModelPortfolioRebalanceDto(modelKey, model, modelName, modelCode);

        addFullScanTrigger(model, null, rebalanceDto, serviceErrors);

        return rebalanceDto;
    }

    private ModelPortfolioRebalanceTriggerDto toModelPortfolioRebalanceTriggerDto(ModelPortfolioRebalanceTrigger rebalanceTrigger,
            ServiceErrors serviceErrors) {
        String triggerType = staticIntegrationService
                .loadCode(CodeCategory.REBALANCE_TRIGGER_GROUP, rebalanceTrigger.getTriggerType(), serviceErrors).getName();

        ModelPortfolioRebalanceTriggerDto rebalanceTriggerDto = new ModelPortfolioRebalanceTriggerDto(
                rebalanceTrigger.getStatus().getDescription(), triggerType, rebalanceTrigger.getMostRecentTriggerDate(),
                rebalanceTrigger.getTotalAccountsCount(), rebalanceTrigger.getTotalRebalancesCount());

        for (ModelPortfolioRebalanceTriggerDetails rebalanceTriggerDetails : rebalanceTrigger.getRebalanceTriggerDetails()) {
            rebalanceTriggerDto.getRebalanceTriggerDetails()
                    .add(toModelPortfolioRebalanceTriggerDetailsDto(rebalanceTriggerDetails));
        }

        return rebalanceTriggerDto;
    }

    private ModelPortfolioRebalanceTriggerDetailsDto toModelPortfolioRebalanceTriggerDetailsDto(
            ModelPortfolioRebalanceTriggerDetails rebalanceTriggerDetails) {
        return new ModelPortfolioRebalanceTriggerDetailsDto(rebalanceTriggerDetails.getTranasactionDate(),
                rebalanceTriggerDetails.getTrigger(), rebalanceTriggerDetails.getTotalAccountsCount());
    }

    @Override
    public ModelPortfolioRebalanceDto submit(ModelPortfolioRebalanceDto rebalance, ServiceErrors serviceErrors) {
        RebalanceAction action = RebalanceAction.forCode(rebalance.getStatus());
        if (action == null) {
            throw new BadRequestException(ApiVersion.CURRENT_VERSION, " valid action must be supplied");
        }

        BrokerKey investmentManager = helper.getCurrentBroker(serviceErrors);
        ModelPortfolioRebalance rebal;
        IpsKey key = IpsKey.valueOf(rebalance.getKey().getModelId());
        if (action == RebalanceAction.SUBMIT) {
            rebal = modelPortfolioRebalanceIntegrationService.submitModelPortfolioRebalance(investmentManager, key,
                    serviceErrors);
        } else {
            rebal = modelPortfolioRebalanceIntegrationService.updateModelPortfolioRebalance(investmentManager, key, action,
                    serviceErrors);
        }

        Map<IpsKey, InvestmentPolicyStatementInterface> ipsMap = investmentPolicyStatementIntegrationService
                .getInvestmentPolicyStatements(Collections.singletonList(key), serviceErrors);

        List<ModelPortfolioSummary> allModels = new ArrayList<>(
                modelPortfolioIntegrationService.loadModels(investmentManager, serviceErrors));
        ModelPortfolioSummary model = selectFirst(allModels,
                having(on(ModelPortfolioSummary.class).getModelKey(), IsEqual.equalTo(key)));
        ModelPortfolioRebalanceDto result;
        if (rebal == null) {
            result = toModelPortfolioRebalanceDto(model, ipsMap, serviceErrors);
        } else {
            result = toModelPortfolioRebalanceDto(model, rebal, ipsMap, serviceErrors);
        }
        result.setRebalanceStatus(ModelRebalanceStatus.PROCESSING);
        return result;

    }

    private void addFullScanTrigger(ModelPortfolioSummary model, ModelPortfolioRebalance rebalance,
            ModelPortfolioRebalanceDto rebalanceDto, ServiceErrors serviceErrors) {
        if (!model.getHasScanTrigger()) {
            return; // no full scan on the model
        }
        Code code = staticIntegrationService.loadCodeByName(CodeCategory.REBALANCE_TRIGGER_GROUP, SCAN_TRIGGER, serviceErrors);

        ModelPortfolioRebalanceTrigger scanTrigger = null;
        DateTime lastRebalance = null;
        if (rebalance != null) {
            scanTrigger = selectFirst(rebalance.getRebalanceTriggers(),
                    having(on(ModelPortfolioRebalanceTrigger.class).getTriggerType(), IsEqual.equalTo(code.getCodeId())));
            lastRebalance = max(rebalance.getRebalanceTriggers(),
                    on(ModelPortfolioRebalanceTrigger.class).getMostRecentTriggerDate());
        }

        if (scanTrigger != null) {
            return; // a true full scan exists, no fake needs to be inserted.
        }

        String triggerType = staticIntegrationService
                .loadCodeByName(CodeCategory.REBALANCE_TRIGGER_GROUP, SCAN_TRIGGER, serviceErrors).getName();

        ModelPortfolioRebalanceTriggerDto rebalanceTriggerDto = new ModelPortfolioRebalanceTriggerDto(
                TriggerStatus.ORDERS_READY.getDescription(), triggerType, lastRebalance, 0, 0);
        rebalanceDto.getRebalanceTriggers().add(rebalanceTriggerDto);
    }
}
