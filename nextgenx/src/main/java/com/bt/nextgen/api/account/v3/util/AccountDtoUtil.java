package com.bt.nextgen.api.account.v3.util;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.service.avaloq.account.PensionType;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.account.PensionAccountDetail;
import com.btfin.panorama.service.integration.account.WrapAccount;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.util.CollectionUtils;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by F030695 on 24/03/2017.
 */
public class AccountDtoUtil {
    private static final Logger logger = getLogger(AccountDtoUtil.class);

    private AccountDtoUtil() {
    }

    /**
     * Sets the account structure type and description for it
     *
     * @param accountDto  - Account Dto
     * @param wrapAccount - WrapAccount object from integration response
     */
    public static void setAccountTypeAndDescription(AccountDto accountDto, WrapAccount wrapAccount) {
        accountDto.setAccountType(wrapAccount.getAccountStructureType().name());
        accountDto.setAccountTypeDescription(wrapAccount.getAccountStructureType().name());

        if (wrapAccount.getSuperAccountSubType() != null) {
            accountDto.setAccountSubType(wrapAccount.getSuperAccountSubType().name());
        }

        if (wrapAccount instanceof PensionAccountDetail) {
            PensionAccountDetail pensionAccount = (PensionAccountDetail) wrapAccount;
            if (pensionAccount.getPensionType() != null) {
                switch (pensionAccount.getPensionType()) {
                    case TTR:
                        accountDto.setAccountTypeDescription(PensionType.TTR.getLabel());
                        break;
                    case TTR_RETIR_PHASE:
                        accountDto.setAccountTypeDescription(PensionType.TTR_RETIR_PHASE.getLabel());
                        break;
                    default:
                        accountDto.setAccountTypeDescription(PensionType.STANDARD.getLabel());
                }
            }
        }
        if (accountDto.getAccountTypeDescription().equals(AccountStructureType.SUPER.name())) {
            accountDto.setAccountTypeDescription(StringUtils.capitalize(AccountStructureType.SUPER.name().toLowerCase()));
        }
    }

    /**
     * Sets the adviser permissions in the argument accountDto object
     *
     * @param accountDto
     * @param wrapAccount
     */
    public static void setAdviserPermissions(AccountDto accountDto, WrapAccount wrapAccount) {
        if (wrapAccount.getAdviserPersonId() != null) {
            if (!CollectionUtils.isEmpty(wrapAccount.getAdviserPermissions())) {
                final com.bt.nextgen.api.client.util.PermissionConverter permissionConverter = new com.bt.nextgen.api.client.util.PermissionConverter(
                        wrapAccount.getAdviserPermissions(), true);
                accountDto.setAdviserPermission(permissionConverter.getAccountPermission().getAdviserPermissionDesc());
            } else {
                logger.error("Adviser permission is blank for Account {}", wrapAccount.getAccountKey().getId());
            }
        } else {
            logger.error("Adviser position is blank for Account {}", wrapAccount.getAccountKey().getId());
        }
    }
}
