package com.bt.nextgen.api.draftaccount.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.client.model.ClientIdentificationDto;
import com.bt.nextgen.api.client.model.ClientKey;
import com.bt.nextgen.api.client.model.IndividualDto;
import com.bt.nextgen.api.client.service.ClientListDtoService;
import com.bt.nextgen.api.draftaccount.model.Adviser;
import com.bt.nextgen.api.draftaccount.model.ApplicationClientStatus;
import com.bt.nextgen.api.draftaccount.model.HoldingApplicationClientDto;
import com.bt.nextgen.api.draftaccount.model.HoldingApplicationDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.accountactivation.AccActivationIntegrationService;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.btfin.panorama.core.security.avaloq.accountactivation.AssociatedPerson;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional(value = "springJpaTransactionManager")
public class HoldingApplicationDtoServiceImpl implements HoldingApplicationDtoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoldingApplicationDtoServiceImpl.class);

    @Autowired
    private AccountsPendingApprovalService accountsPendingApprovalService;

    @Autowired
    private ClientListDtoService clientListDtoService;

    @Autowired
    private AccActivationIntegrationService accActivationIntegrationService;

    @Autowired
    private BrokerIntegrationService brokerService;

    @Autowired
    private UserProfileService userProfileService;

    @Override
    public HoldingApplicationDto find(final AccountKey key, ServiceErrors serviceErrors) {
        List<WrapAccount> usersAccounts = accountsPendingApprovalService.getUserAccountsPendingApprovals(serviceErrors);
        WrapAccount account = Lambda.selectUnique(usersAccounts, Lambda.having(Lambda.on(WrapAccount.class).getAccountKey(),
            Matchers.is(key)));

        ApplicationDocument applicationDocument = getApplicationDocument(key, serviceErrors);
        List<AssociatedPerson> associatedPersons = HoldingApplicationHelper.getAssociatedPersons(account.getAccountStructureType(),
            applicationDocument);
        List<HoldingApplicationClientDto> holdingApplicationClientList = getHoldingApplicationClientList(associatedPersons, serviceErrors);
        Adviser adviser = getAdviserDetails(account.getAdviserPositionId(), serviceErrors);
        return new HoldingApplicationDto(key, new DateTime(applicationDocument.getAppSubmitDate()), account.getAccountName(),
            HoldingApplicationHelper.checkAccountTypeForNewSmsf(applicationDocument), holdingApplicationClientList, adviser, applicationDocument.getApprovalType());
    }

    private Adviser getAdviserDetails(BrokerKey brokerKey, ServiceErrors serviceErrors) {
        try {
            BrokerUser adviserDetails = brokerService.getAdviserBrokerUser(brokerKey, serviceErrors);
            if (adviserDetails != null) {
                return new Adviser(adviserDetails.getFirstName() + " " + adviserDetails.getLastName(),
                        adviserDetails.getCorporateName(), HoldingApplicationHelper.getPhoneByType(adviserDetails.getPhones(),
                                AddressMedium.BUSINESS_TELEPHONE), HoldingApplicationHelper.getEmailByType(
                                adviserDetails.getEmails(), AddressMedium.EMAIL_PRIMARY));
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            //no single matching adviser found - return no adviser details
            LOGGER.error("Error loading adviser details for broker key: {}. {}", brokerKey.getId(), e);
        }
        return null;
    }

    private ApplicationDocument getApplicationDocument(AccountKey key, ServiceErrors serviceErrors) {
        WrapAccountIdentifier wrapAccountIdentifier = new WrapAccountIdentifierImpl();
        wrapAccountIdentifier.setBpId(key.getId());
        UserProfile activeProfile = userProfileService.getActiveProfile();
        List<ApplicationDocument> applicationDocuments = accActivationIntegrationService.loadAccApplicationForPortfolio(
            Arrays.asList(wrapAccountIdentifier),activeProfile.getJobRole(),activeProfile.getClientKey(), serviceErrors);
        return applicationDocuments.get(0);
    }

    private List<HoldingApplicationClientDto> getHoldingApplicationClientList(
        List<AssociatedPerson> associatedPersons, final ServiceErrors serviceErrors) {
        List<HoldingApplicationClientDto> holdingApplicationClientList = new ArrayList<>();

        for (AssociatedPerson associatedPerson : associatedPersons) {
            ClientIdentificationDto clientDto = clientListDtoService.find(new ClientKey(EncodedString.fromPlainText(
                associatedPerson.getClientKey().getId()).toString()), serviceErrors);
            if (clientDto instanceof IndividualDto) {
                holdingApplicationClientList.add(new HoldingApplicationClientDto((IndividualDto) clientDto,
                    associatedPerson.isHasToAcceptTnC(), ApplicationClientStatus.getStatus(associatedPerson.isHasToAcceptTnC(),
                        associatedPerson.isRegisteredOnline(), associatedPerson.isHasApprovedTnC())));
            }
        }
        HoldingApplicationHelper.sortHoldingApplicationClientList(holdingApplicationClientList);
        return holdingApplicationClientList;
    }
}
