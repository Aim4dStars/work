package com.bt.nextgen.api.movemoney.v3.util;

import com.avaloq.abs.bb.fld_def.IdFld;
import com.bt.nextgen.api.account.v3.model.LinkedAccountStatusDto;
import com.bt.nextgen.api.util.IntegrationUtil;
import com.bt.nextgen.core.security.profile.InvestorProfileService;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.CustomerDataManagementIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.user.CISKey;
import com.btfin.panorama.service.integration.account.LinkedAccount;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.account.BankAccount;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.Field;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.payeedetails.LinkedAccountStatus;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by l078480 on 28/07/2017.
 */
@SuppressWarnings("squid:S1200")
public class DepositUtils {

    private static final String GEN_CODE="can_gen_code";
    private static final String VEFY_CODE="can_vfy_code";
    private static final String DIRECT_DEBIT = "can_direct_debit";
    private static final String GRACE_PERIOD = "is_grace_prd";
    private static final String CODE_STATUS ="+";

    private static final Logger LOGGER = getLogger(DepositUtils.class);

    public static LinkedAccountStatusDto linkedAccountStatus(LinkedAccount linkedAccountModel, StaticIntegrationService staticIntegrationService){
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        IdFld linkedIdField = new IdFld();
        String linkedAccountStatus =  linkedAccountModel.getLinkedAccountStatus()!=null ? linkedAccountModel.getLinkedAccountStatus() : "1";
        linkedIdField.setVal(linkedAccountStatus);
        Code linkedAccountStatusCode = staticIntegrationService.loadCode(CodeCategory.LINKED_ACCOUNT_STATUS,
                AvaloqGatewayUtil.asString(linkedIdField),
                serviceErrors);
        LinkedAccountStatusDto linkedaccountStatus = new LinkedAccountStatusDto();
        linkedaccountStatus.setLinkedAccountStatus(LinkedAccountStatus.forIntlId(linkedAccountStatusCode.getIntlId()));
        for(Field field: linkedAccountStatusCode.getFields()){
            boolean status = CODE_STATUS.equals(field.getValue()) ? true: false;
            String name = field.getName();
            if (GEN_CODE.equalsIgnoreCase(name))
                linkedaccountStatus.setGenCode(status);
            else if (VEFY_CODE.equalsIgnoreCase(name))
                linkedaccountStatus.setVfyCode(status);
            else if (DIRECT_DEBIT.equalsIgnoreCase(name))
                linkedaccountStatus.setDirectDebit(status);
            else if (GRACE_PERIOD.equalsIgnoreCase(name))
                linkedaccountStatus.setGracePeriod(status);
        }

        return linkedaccountStatus;
    }

    public static String getAccountType(String accountId, AccountIntegrationService accountService, ServiceErrors serviceErrors) {
        String accountType="";
        Map<AccountKey, WrapAccount> account = accountService.loadWrapAccountWithoutContainers(serviceErrors);
        for (Map.Entry<AccountKey, WrapAccount> entry : account.entrySet()) {
            if (entry.getValue().getAccountKey().getId().equals(accountId)) {
                accountType = entry.getValue().getAccountStructureType().toString();
            }
        }
        LOGGER.info("Accounttype to fetch BankAccounts:{}", accountType);
        return accountType;
    }

    public static List<com.bt.nextgen.service.avaloq.accountactivation.LinkedAccountStatus> populateAssociatedAccounts(String accountId,
                                                                                                                       AccountIntegrationService accountIntegrationService,
                                                                                                                       InvestorProfileService profileService,
                                                                                                                       CustomerDataManagementIntegrationService customerDataManagementIntegrationService,
                                                                                                                       ServiceErrors serviceErrors) {

        ServiceErrors servErrors =new ServiceErrorsImpl();
        List<com.bt.nextgen.service.avaloq.accountactivation.LinkedAccountStatus> associatedAccountsList = null;
        com.bt.nextgen.service.avaloq.accountactivation.LinkedAccountStatus associatedAccount = new com.bt.nextgen.service.avaloq.accountactivation.LinkedAccountStatus();
        String accountType = getAccountType(accountId, accountIntegrationService, servErrors);
        CISKey cisKey = profileService.getEffectiveProfile().getToken().getCISKey();
        LOGGER.info("Fetch BankAccounts for cisKey:{}", cisKey);
        if( (AccountStructureType.Individual.toString()).equalsIgnoreCase(accountType) && null!= cisKey ) {
            associatedAccountsList = new ArrayList<>();
            setAssociatedAccount(profileService, customerDataManagementIntegrationService, cisKey, servErrors, associatedAccountsList, associatedAccount);
        }
        return associatedAccountsList;
    }

    private static void setAssociatedAccount(InvestorProfileService profileService, CustomerDataManagementIntegrationService customerDataManagementIntegrationService, CISKey cisKey, ServiceErrors serviceErrors, List<com.bt.nextgen.service.avaloq.accountactivation.LinkedAccountStatus> associatedAccountsList, com.bt.nextgen.service.avaloq.accountactivation.LinkedAccountStatus associatedAccount) {
        LOGGER.info("Fetch BankAccounts for cisKeyId:{}", cisKey.getId());
        final List<BankAccount> bankAccountList = IntegrationUtil.getBankAccountList(cisKey, customerDataManagementIntegrationService, serviceErrors);
        if (null!=bankAccountList && !bankAccountList.isEmpty()) {
            LOGGER.info("BankAccounts size:{}", bankAccountList.size());
            for (BankAccount bankAccount: bankAccountList) {
                associatedAccount.setAccountNumber(bankAccount.getAccountNumber());
                associatedAccount.setBsb(bankAccount.getBsb());
                associatedAccount.setVerificationRequired(false);
                associatedAccountsList.add(associatedAccount);
            }
        }
    }

    private DepositUtils() {
        // Private Constructor - to hide the public implicit one
    }

}
