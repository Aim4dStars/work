package com.bt.nextgen.api.termdeposit.controller;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorAccountKey;
import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorDealerKey;
import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorKey;
import com.bt.nextgen.api.termdeposit.service.TermDepositCalculatorDtoService;
import com.bt.nextgen.api.termdeposit.service.TermDepositRateCalculatorDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByPartialKey;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class TermDepositsCalculatorApiController {

    public static final String PRODUCT_BADGE = "badge";
    public static final String AMOUNT = "amount";
    public static final String ACCOUNT_TYPE = "accountType";

    @Autowired
    private TermDepositCalculatorDtoService termDepositCalculatorDtoService;

    @Autowired
    private UserProfileService userProfileService;

    //This is the new service for calculating Term Deposit Rates.
    @Autowired
    private TermDepositRateCalculatorDtoService termDepositRateCalculatorService;

    @Autowired
    private FeatureTogglesService featureTogglesService;

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.TD_CALCULATOR, produces =
            "application/json")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ApiResponse getTermDepositCalculator(@RequestParam(value = PRODUCT_BADGE, required = false) String badge,
            @RequestParam(value = ACCOUNT_TYPE, required = false)String accountType,@RequestParam(AMOUNT) String amount,
            @RequestParam(value = UriMappingConstants.ACCOUNT_ID_URI_MAPPING, required = false) String accountId) {
        TermDepositCalculatorKey calculatorKey;
        final boolean termDepositToggle = featureTogglesService.findOne(new FailFastErrorsImpl())
                                                               .getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE);
        if (StringUtils.isNotEmpty(accountId)) {
            calculatorKey = new TermDepositCalculatorAccountKey(
                    ProductKey.valueOf(ConsistentEncodedString.toPlainText(badge)), amount, new AccountKey(accountId));
        } else {
            calculatorKey = new TermDepositCalculatorDealerKey(
                    ProductKey.valueOf(ConsistentEncodedString.toPlainText(badge)), amount,
                    userProfileService.getDealerGroupBroker().getDealerKey(),accountType);
        }
        if (termDepositToggle) {
            return new FindByPartialKey<>(ApiVersion.CURRENT_VERSION, termDepositRateCalculatorService, calculatorKey)
                    .performOperation();
        } else {
            return new FindByPartialKey<>(ApiVersion.CURRENT_VERSION, termDepositCalculatorDtoService, calculatorKey)
                    .performOperation();
        }
    }
}
