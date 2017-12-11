package com.bt.nextgen.api.corporateaction.v1.service;

import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.integration.domain.InvestorDetail;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.hamcrest.core.IsEqual;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSavedDetails;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionConverterFactory;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionResponseConverterService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountBalance;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccountParticipationStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionTransactionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionTransactionStatus;
import com.bt.nextgen.service.integration.domain.RegisteredEntity;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientKey;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.core.IsEqual.equalTo;

@Service
public class CorporateActionAccountDetailsConverter {
    @Autowired
    private CorporateActionAccountHelper corporateActionAccountHelper;

    @Autowired
    private CorporateActionConverterFactory corporateActionConverterFactory;

    @Autowired
    private CorporateActionHelper corporateActionHelper;

    @Autowired
    @Qualifier("cacheAvaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    public CorporateActionAccountDetailsDto createAccountDetailsDto(final CorporateActionContext context,
                                                                    final CorporateActionSupplementaryDetails supplementaryDetails,
                                                                    final CorporateActionAccount caa,
                                                                    final CorporateActionSavedDetails savedDetails,
                                                                    final ServiceErrors serviceErrors) {
        WrapAccountDetail account;

        if (context.isDealerGroupOrInvestmentManager()) {
            account = (WrapAccountDetail) supplementaryDetails.getClientAccountDetails().getAccountsMap()
                                                              .get(AccountKey.valueOf(caa.getAccountId()));
        } else {
            account = accountIntegrationService.loadWrapAccountDetail(AccountKey.valueOf(caa.getAccountId()), serviceErrors);
        }

        if (account != null) {
            Integer transactionNumber = null;
            String transactionDescription = null;
            CorporateActionTransactionStatus postTransactionStatus = supplementaryDetails.getTransactionStatus();

            CorporateActionTransactionDetails transactionDetails = selectFirst(supplementaryDetails.getTransactionDetails(),
                    having(on(CorporateActionTransactionDetails.class).getPositionId(), IsEqual.equalTo(caa.getPositionId())));

            if (transactionDetails != null) {
                transactionNumber = transactionDetails.getTransactionNumber();
                transactionDescription = transactionDetails.getTransactionDescription();
                postTransactionStatus = CorporateActionTransactionStatus.POST_EX_DATE;
            }

            AccountBalance accountBalance =
                    supplementaryDetails.getClientAccountDetails().getAccountBalancesMap().get(AccountKey.valueOf(caa.getAccountId()));

            CorporateActionAccountDetailsDtoParams params = new CorporateActionAccountDetailsDtoParams();

            setClientAccountDetails(context, account, params);
            params.setPositionId(caa.getPositionId());
            params.setPortfolioName(corporateActionAccountHelper.getPortfolioName(account.getProductKey(), serviceErrors));
            params.setAdviserName(corporateActionAccountHelper.getAdviserName(caa.getAdviserId(), serviceErrors));
            params.setCash(caa.getAvailableCash() == null ? accountBalance.getAvailableCash() : caa.getAvailableCash());
            params.setElectionStatus(caa.getElectionStatus() != null ? caa.getElectionStatus()
                                                                     : CorporateActionAccountParticipationStatus.NOT_SUBMITTED);
            params.setHolding(getHolding(caa));
            params.setTransactionNumber(transactionNumber).setTransactionDescription(transactionDescription);
            params.setTransactionStatus(postTransactionStatus);

            params.setSubmittedElections(corporateActionAccountHelper.getSubmittedElections(context, caa));
            params.setSavedElections(corporateActionAccountHelper.getSavedElections(context, account.getAccountNumber(), savedDetails));
            params.setPortfolioValue(accountBalance.getPortfolioValue());
            params.setPendingSell(hasPendingSell(caa));

            params.setAccountType(corporateActionHelper.getAccountTypeDescription(account));

            // Trustee approval is currently applicable to super (which includes pension)
            params.setTrusteeApproval(isSuper(account));

            CorporateActionResponseConverterService responseConverter =
                    corporateActionConverterFactory.getResponseConverterService(context.getCorporateActionDetails());

            responseConverter.setCorporateActionAccountDetailsDtoParams(context, caa, params);

            return new CorporateActionAccountDetailsDto(context.getBrokerPositionId(), params);
        }

        return null;
    }

    private void setClientAccountDetails(CorporateActionContext context, WrapAccountDetail account,
                                         CorporateActionAccountDetailsDtoParams params) {
        params.setAccountId(account.getAccountNumber());
        params.setAccountName(account.getAccountName());
        params.setAccountKey(EncodedString.fromPlainText(account.getAccountKey().getId()).toString());
        params.setClientId(account.getAccountOwners().iterator().next().getId());
        params.setClientName(account.getAccountName());

        if (!context.isDealerGroupOrInvestmentManager()) {
            ClientKey primaryContactClientKey = account.getPrimaryContactPersonId();
            params.setClientId(primaryContactClientKey.getId());

            Client primaryContact =
                    selectFirst(account.getOwners(), having(on(Client.class).getClientKey(), equalTo(primaryContactClientKey)));

            if (primaryContact == null) {
                for (Client owner : account.getOwners()) {
                    if (owner instanceof RegisteredEntity) {
                        RegisteredEntity registeredEntity = (RegisteredEntity) owner;

                        primaryContact = selectFirst(registeredEntity.getLinkedClients(),
                                having(on(InvestorDetail.class).getClientKey(), equalTo(primaryContactClientKey)));

                        if (primaryContact == null) {
                            primaryContact = selectFirst(registeredEntity.getRelatedPersons(),
                                    having(on(InvestorDetail.class).getClientKey(), equalTo(primaryContactClientKey)));
                        }

                        if (primaryContact != null) {
                            break;
                        }
                    }
                }
            }

            setPrimaryContactDetails(account, primaryContact, params);
        }
    }

    private void setPrimaryContactDetails(WrapAccountDetail account, Client primaryContact, CorporateActionAccountDetailsDtoParams params) {
        if (primaryContact != null) {
            if (AccountStructureType.Individual.equals(account.getAccountStructureType())) {
                params.setClientName(primaryContact.getFullName());
            }

            params.setClientAddress(corporateActionAccountHelper.getPreferredAddress(primaryContact.getAddresses()));
            params.setClientEmail(corporateActionAccountHelper.getPreferredEmail(primaryContact.getEmails()));
            params.setClientPhone(corporateActionAccountHelper.getPreferredPhone(primaryContact.getPhones()));
        }
    }

    public boolean isSuper(WrapAccount account) {
        return AccountStructureType.SUPER.equals(account.getAccountStructureType());
    }

    private Integer getHolding(CorporateActionAccount caa) {
        // Additional logic will go here for pending sell work
        return caa.getEligibleQuantity() != null ? caa.getEligibleQuantity().intValue() : 0;
    }

    private boolean hasPendingSell(CorporateActionAccount caa) {
        return caa.getAvailableQuantity() != null && caa.getEligibleQuantity() != null ?
               caa.getAvailableQuantity().compareTo(caa.getEligibleQuantity()) < 0  : false;
    }
}
