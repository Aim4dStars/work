package com.bt.nextgen.service.avaloq.rules;

import com.bt.nextgen.service.ServiceErrors;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by M041926 on 23/09/2016.
 */
public interface AvaloqRulesIntegrationService {
    RuleImpl retrieveTwoFaRule(RuleType ruleType, Map<RuleCond, String> condStringMap, final ServiceErrors serviceErrors);
    Future<RuleUpdateStatus> updateAvaloqRuleAsync(String ruleId, Map<RuleUpdateParams, ?> parameters);
    RuleUpdateStatus updateAvaloqRule(String ruleId, Map<RuleUpdateParams, ?> parameters, final ServiceErrors serviceErrors);
}
