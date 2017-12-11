package com.bt.nextgen.api.util;

import com.bt.nextgen.api.draftaccount.service.ClientApplicationApprovalDtoServiceImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.CustomerDataManagementIntegrationService;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementOperation;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequestImpl;
import com.bt.nextgen.service.group.customer.groupesb.RoleType;
import com.btfin.panorama.service.integration.account.BankAccount;
import com.bt.nextgen.service.integration.user.CISKey;
import com.btfin.panorama.core.security.profile.UserProfile;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by L069679 on 21/07/2017.
 */
public final class IntegrationUtil {


    private static final Logger LOGGER = getLogger(IntegrationUtil.class);

    private static final String BANK_ACCOUNT = "BANK_ACCOUNT";


    public static List<BankAccount> getBankAccountList(CISKey cisKey, CustomerDataManagementIntegrationService customerDataManagementIntegrationService, ServiceErrors serviceErrors) {
        final CustomerManagementRequest req = new CustomerManagementRequestImpl();
        req.setCISKey(cisKey);
        req.setInvolvedPartyRoleType(RoleType.INDIVIDUAL);
        return setOperationTypes(new String[]{BANK_ACCOUNT}, req, customerDataManagementIntegrationService,  serviceErrors);
    }

    private static List<BankAccount> setOperationTypes(String[] operationTypes, CustomerManagementRequest req, CustomerDataManagementIntegrationService customerDataManagementIntegrationService, ServiceErrors serviceErrors) {
        final List<CustomerManagementOperation> operations = new ArrayList<>();
        for (String operation : operationTypes) {
            switch (operation) {
                case BANK_ACCOUNT:
                    operations.add(CustomerManagementOperation.ARRANGEMENTS);
                    break;
                default:
                    LOGGER.error("Operation not found for: {}", operation);
                    break;
            }
        }
        req.setOperationTypes(operations);
        return customerDataManagementIntegrationService.retrieveCustomerInformation(req, Arrays.asList(BANK_ACCOUNT), serviceErrors).getBankAccounts();
    }

    private IntegrationUtil() {
    }
}
