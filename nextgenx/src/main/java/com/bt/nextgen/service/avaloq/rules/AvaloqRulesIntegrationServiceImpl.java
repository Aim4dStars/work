package com.bt.nextgen.service.avaloq.rules;

import com.bt.nextgen.core.exception.ServiceException;
import com.bt.nextgen.service.AvaloqReportService;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.request.AvaloqReportRequestImpl;
import com.bt.nextgen.service.request.AvaloqRequest;
import com.btfin.abs.trxservice.farule.v1_0.Data;
import com.btfin.abs.trxservice.farule.v1_0.FaRuleReq;
import com.btfin.abs.trxservice.farule.v1_0.FaRuleRsp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by M041926 on 23/09/2016.
 */
@Service("avaloqRulesIntegrationService")
@EnableAsync
public class AvaloqRulesIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements AvaloqRulesIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(AvaloqRulesIntegrationServiceImpl.class);

    @Autowired
    private AvaloqReportService avaloqService;

    @Autowired
    private AvaloqGatewayHelperService webserviceClient;

    @Autowired
    private StaticIntegrationService staticIntegrationService;

    @Override
    public RuleImpl retrieveTwoFaRule(RuleType ruleType, final Map<RuleCond, String> condStringMap, final ServiceErrors serviceErrors) {

        final String typeId = staticIntegrationService.loadCodeByUserId(CodeCategory.RULE_TYPE, ruleType.getUserId(), serviceErrors).getCodeId();
        logger.info("Retrieving avaloq rule with typeId: {} condMap: {}", typeId, condStringMap);

        return new IntegrationSingleOperation<RuleImpl>("retrieveTwoFaRule", serviceErrors) {
            @Override
            public RuleImpl performOperation() {
                AvaloqRequest avaloqRequest = new AvaloqReportRequestImpl(RuleTemplate.AVALOQ_RULE).forParam(RuleGetParams.TYPE_ID, typeId);
                for (Map.Entry<RuleCond, String> entry : condStringMap.entrySet()) {
                    String condId = staticIntegrationService.loadCodeByUserId(CodeCategory.RULE_CONDITION, entry.getKey().getUserId(), serviceErrors).getCodeId();
                    avaloqRequest.forParam(entry.getKey().getCoditionId(), condId).forParam(entry.getKey().getConditionVal(), entry.getValue());
                }
                avaloqRequest.asApplicationUser();
                RuleImpl response = avaloqService.executeReportRequestToDomain(avaloqRequest, RuleImpl.class, serviceErrors);
                return response;
            }
        }.run();
    }

    @Override
    @Async
    public Future<RuleUpdateStatus> updateAvaloqRuleAsync(final String ruleId, final Map<RuleUpdateParams, ?> parameters) {
        final ServiceErrors serviceErrors = new FailFastErrorsImpl();
        RuleUpdateStatus status = null;
        try {
            status = updateAvaloqRule(ruleId, parameters, serviceErrors);
        } catch (ServiceException e) {
            logger.error(String.format("Failed to update registration 2FA rule with id:%s in avaloq.", ruleId), e);
        }
        return new AsyncResult<>(status);
    }

    @Override
    public RuleUpdateStatus updateAvaloqRule(final String ruleId, final Map<RuleUpdateParams, ?> parameters, final ServiceErrors serviceErrors) {

        RuleUpdateStatus resp = new IntegrationSingleOperation<RuleUpdateStatus>("updateAvaloqRuleAsync", serviceErrors) {
            @Override
            public RuleUpdateStatus performOperation() {
                Boolean status = (Boolean) parameters.get(RuleUpdateParams.STATUS);
                FaRuleReq faRuleRq = createFaRuleRequest(ruleId, status);
                FaRuleRsp resp = webserviceClient.sendSystemRequestToWebService(faRuleRq, AvaloqOperation.FA_RULE_REQ, serviceErrors);
                boolean isRuleFound = AvaloqGatewayUtil.asBoolean(resp.getData().getRuleFound());

                if (!isRuleFound) {
                    logger.error("Rule with id:{} does not exist in avaloq.", ruleId);
                }

                return new RuleUpdateStatus(AvaloqGatewayUtil.asString(resp.getData().getRuleId()), AvaloqGatewayUtil.asBoolean(resp.getData().getSucc()), isRuleFound);
            }
        }.run();

        return resp;
    }

    private static FaRuleReq createFaRuleRequest(String ruleId, Boolean status) {

        FaRuleReq rq = AvaloqObjectFactory.getFaRuleObjectFactory().createFaRuleReq();
        rq.setHdr(AvaloqGatewayUtil.createHdr());

        Data data = AvaloqObjectFactory.getFaRuleObjectFactory().createData();
        data.setRuleId(AvaloqGatewayUtil.createNumberVal(ruleId));
        data.setSucc(AvaloqGatewayUtil.createBoolVal(status));
        rq.setData(data);

        return rq;
    }
}
