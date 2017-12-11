package com.bt.nextgen.api.supermatch.v1.util;

import com.bt.nextgen.api.supermatch.v1.model.RolloverDetailsDto;
import com.bt.nextgen.api.supermatch.v1.model.SuperMatchDto;
import com.bt.nextgen.api.supermatch.v1.model.SuperMatchDtoKey;
import com.bt.nextgen.api.supermatch.v1.model.SuperMatchFundDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.btesb.supermatch.model.MemberImpl;
import com.bt.nextgen.service.btesb.supermatch.model.SuperFundAccountImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.supermatch.Member;
import com.bt.nextgen.service.integration.supermatch.SuperFundAccount;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ch.lambdaj.Lambda.collect;
import static ch.lambdaj.Lambda.on;

/**
 * Utility class to create requests for super match service
 */
@Component
public class SuperMatchDtoHelper {

    //Values taken from ns.btfin_com.party.v3_0.CustomerNoBaseIssuerType
    private static final String ISSUER_WESTPAC_LEGACY = "WestpacLegacy";

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    private UserProfileService profileService;

    @Autowired
    @Qualifier("avaloqClientIntegrationService")
    private ClientIntegrationService clientIntegrationService;

    /**
     * Returns the super fund account for the CIS key
     *
     * @param customerId       - customer Identifier
     * @param superMatchDtoKey - super match key {@link SuperMatchDtoKey}
     * @param serviceErrors    - Object to capture service errors
     */
    public SuperFundAccount getSuperFundAccount(String customerId, SuperMatchDtoKey superMatchDtoKey, ServiceErrors serviceErrors) {
        final MemberImpl member = new MemberImpl();
        member.setCustomerId(customerId);
        member.setIssuer(ISSUER_WESTPAC_LEGACY);

        String accountNumber = null;
        String productUsi = null;

        // if account based retrieve is called, we need to set USI and Account number
        final AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(superMatchDtoKey.getAccountId()));
        final WrapAccountDetail account = accountIntegrationService.loadWrapAccountDetail(accountKey, serviceErrors);
        if (account != null) {
            accountNumber = account.getAccountNumber();
            productUsi = getProductUsi(account, serviceErrors);
        }

        final SuperFundAccountImpl superFundAccount = new SuperFundAccountImpl();
        superFundAccount.setAccountNumber(accountNumber);
        superFundAccount.setUsi(productUsi);
        superFundAccount.setMembers(Collections.<Member>singletonList(member));
        return superFundAccount;
    }

    /**
     * Creates request for roll over
     *
     * @param superMatchDto - Object of {@link SuperMatchDto}, sent by UI for triggering rollover
     */
    public List<SuperFundAccount> createUpdateRollOverRequest(SuperMatchDto superMatchDto) {
        final List<SuperFundAccount> rolloverFundAccounts = new ArrayList<>();
        SuperFundAccountImpl superFundAccount;
        for (SuperMatchFundDto fund : superMatchDto.getSuperMatchFundList()) {
            for (RolloverDetailsDto rollOverDetails : fund.getRolloverDetails()) {
                superFundAccount = new SuperFundAccountImpl();
                superFundAccount.setAccountNumber(fund.getAccountNumber());
                superFundAccount.setUsi(fund.getUsi());
                superFundAccount.setRolloverStatus(true);
                superFundAccount.setRolloverAmount(rollOverDetails.getRolloverAmount());
                rolloverFundAccounts.add(superFundAccount);
            }
        }

        return rolloverFundAccounts;
    }

    /**
     * Gets client details for the current user
     *
     * @param serviceErrors - Object to capture service errors
     */
    public Client getClient(ServiceErrors serviceErrors) {
        final ClientKey clientKey = profileService.getActiveProfile().getClientKey();
        return clientIntegrationService.loadClientDetails(clientKey, serviceErrors);
    }

    /**
     * Sets the additional member information
     *
     * @param superFundAccount - Super fund account
     * @param client           - Client data
     * @param emailAddress     - email address to send notification
     */
    public void setMemberDetails(SuperFundAccount superFundAccount, Client client, String emailAddress) {
        final MemberImpl member = (MemberImpl) superFundAccount.getMembers().get(0);
        member.setFirstName(client.getFirstName());
        member.setLastName(client.getLastName());
        member.setDateOfBirth(client.getDateOfBirth());

        final List<String> emailAddresses = emailAddress != null ? Collections.singletonList(emailAddress) :
                collect(client.getEmails(), on(Email.class).getEmail());
        member.setEmailAddresses(emailAddresses);
    }

    private String getProductUsi(WrapAccountDetail account, ServiceErrors serviceErrors) {
        final Product product = productIntegrationService.getProductDetail(account.getProductKey(), serviceErrors);
        if (product.isSuper()) {
            final Product parentProduct = productIntegrationService.getProductDetail(product.getParentProductKey(), serviceErrors);
            return parentProduct.getProductUsi();
        }
        return null;
    }
}
