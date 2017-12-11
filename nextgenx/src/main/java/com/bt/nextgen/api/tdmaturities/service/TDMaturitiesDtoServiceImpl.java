package com.bt.nextgen.api.tdmaturities.service;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ch.lambdaj.Lambda;

import com.bt.nextgen.api.portfolio.v3.service.TermDepositPresentationService;
import com.bt.nextgen.api.tdmaturities.model.TDMaturitiesDto;
import com.bt.nextgen.api.tdmaturities.model.TDMaturitiesStatus;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.domain.key.StringIdKey;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.avaloq.termdepositstatus.TermDepositMaturityRequestImpl;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.termdepositstatus.TermDepositMaturity;
import com.bt.nextgen.service.integration.termdepositstatus.TermDepositMaturityIntegrationService;
import com.bt.nextgen.service.integration.termdepositstatus.TermDepositMaturityResponse;
import com.bt.nextgen.service.integration.termdepositstatus.TermDepositMaturityStatus;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.Person;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import com.bt.nextgen.web.controller.cash.util.Attribute;

@Service
public class TDMaturitiesDtoServiceImpl implements TDMaturitiesDtoService {
    public static final String MULTI_ADVISER_SEARCH = "multiAdviserSearch";

    @Autowired
    private TermDepositMaturityIntegrationService termDepositMaturityIntegrationService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    private BrokerIntegrationService brokerService;

    @Autowired
    private BankDateIntegrationService bankDateIntegrationService;

    @Autowired
    private TermDepositPresentationService termDepositPresentationService;

    @Autowired
    private CmsService cmsService;

    @Autowired
    private OptionsService optionsService;

    @Override
    public List<TDMaturitiesDto> search(List<ApiSearchCriteria> criteriaList, final ServiceErrors serviceErrors) {
        TermDepositMaturityRequestImpl request = getRequestImpl(criteriaList, serviceErrors);
        TermDepositMaturityResponse response;
        if (request.getClientAccount() != null) {
            response = termDepositMaturityIntegrationService.loadTermDepositClient(request, serviceErrors);
        } else {
            response = termDepositMaturityIntegrationService.loadTermDepositAdviser(request, serviceErrors);
        }

        List<TermDepositMaturity> tdMaturityList = new ArrayList<>();
        if (response != null && response.getTermDepositMaturity() != null) {
            tdMaturityList = response.getTermDepositMaturity();
        }
        List<TDMaturitiesDto> result = toTDMaturitiesDto(tdMaturityList, serviceErrors);
        return filterTdMaturityList(result, request.getStartDate(), request.getEndDate());
    }

    protected List<TDMaturitiesDto> toTDMaturitiesDto(List<TermDepositMaturity> tdMaturityList, ServiceErrors serviceErrors) {
        List<TDMaturitiesDto> resultList = new ArrayList<>();

        for (TermDepositMaturity maturity : tdMaturityList) {
            String accountName = "";
            String productName = "";
            String adviserName = "";

            WrapAccount account = accountIntegrationService
                    .loadWrapAccountWithoutContainers(maturity.getAccount().getAccountKey(), serviceErrors);
            if (account != null) {
                accountName = account.getAccountName();
            }

            Product product = productIntegrationService.getProductDetail(maturity.getProduct().getProductKey(), serviceErrors);
            if (product != null) {
                productName = product.getProductName();
            }

            String avsrPersonId = maturity.getAdviserBaseDetails().getAdviserId();
            Person broker = null;
            String adviserId = null;
            if (avsrPersonId != null) {
                broker = brokerService.getPersonDetailsOfBrokerUser(ClientKey.valueOf(avsrPersonId), serviceErrors);
            }
            // Collection <Broker> brokers = brokerService.getBrokersForUser(userKey, serviceErrors);
            // Broker broker = brokers.iterator().next();
            if (broker != null) {
                adviserName = broker.getLastName() + ", " + broker.getFirstName();
                adviserId = broker.getBankReferenceId();
            }

            TDMaturitiesDto maturityDto = null;

            if (null != maturity.getTermDepositMaturityStatus() && maturity.getTermDepositMaturityStatus().size() > 0) {
                for (TermDepositMaturityStatus maturityItem : maturity.getTermDepositMaturityStatus()) {
                    String issuer = assetIntegrationService.getIssuerForBrand(maturityItem.getBrandId(), serviceErrors);

                    String brandName = cmsService.getContent(Constants.TD_BRAND_PREFIX + issuer);

                    if (brandName == null) {
                        brandName = issuer;
                    }                    

                    TermDepositPresentation tdPresentation = termDepositPresentationService.getTermDepositPresentation(maturity
                            .getAccount().getAccountKey(), maturityItem.getAssetId(), serviceErrors);

                    maturityDto = new TDMaturitiesDto(EncodedString.fromPlainText(maturity.getAccount().getAccountKey().getId())
                            .toString(), maturity.getAccount().getAccountNumber(), accountName, maturity.getAccount()
                            .getAccountStructureType().name(), adviserId, adviserName, maturity.getProduct().getProductKey()
                            .getId(), productName, issuer, brandName, maturityItem,
                            tdPresentation.getTerm(), getStatus(maturityItem.getCloseDate(),
                                    maturityItem.getMaturityDate()));
                    resultList.add(maturityDto);
                }
            }

        }

        return resultList;
    }

    // TODO - this should be done via BeanFilter however the current implementation only
    // supports string comparison and I can't make a core change to enhance it this close to
    // release
    private List<TDMaturitiesDto> filterTdMaturityList(List<TDMaturitiesDto> tdMaturityList, final DateTime minDate,
            final DateTime maxDate) {
        Matcher<TDMaturitiesDto> minDateMatcher = new BaseMatcher<TDMaturitiesDto>() {
            @Override
            public boolean matches(Object item) {
                TDMaturitiesDto maturity = (TDMaturitiesDto) item;
                return maturity.getMaturityDate() != null && maturity.getMaturityDate().compareTo(minDate) >= 0
                        && maturity.getMaturityDate().compareTo(maxDate) <= 0;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("maturityDate in range(").appendValue(minDate).appendText(", ").appendValue(maxDate)
                        .appendText(")");
            }
        };
        return Lambda.filter(minDateMatcher, tdMaturityList);
    }

    private TermDepositMaturityRequestImpl getRequestImpl(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        TermDepositMaturityRequestImpl request = new TermDepositMaturityRequestImpl();

        DateTime startDate = null;
        DateTime endDate = null;

        for (ApiSearchCriteria parameter : criteriaList) {
            if (Attribute.START_DATE.equals(parameter.getProperty())) {
                startDate = new DateTime(parameter.getValue());
            } else if (Attribute.END_DATE.equals(parameter.getProperty())) {
                endDate = new DateTime(parameter.getValue());
            } else if (Attribute.ACCOUNT_ID.equals(parameter.getProperty())) {
                WrapAccountIdentifier clientAccount = new WrapAccountIdentifierImpl();
                clientAccount.setBpId(EncodedString.toPlainText(parameter.getValue()));
                request.setClientAccount(clientAccount);
            } else if (Attribute.ADVISER_ID.equals(parameter.getProperty())) {
                request.setOEIdentifier(EncodedString.toPlainText(parameter.getValue()));
            } else if (MULTI_ADVISER_SEARCH.equals(parameter.getProperty())) {
                request.setOeIdentifiers(findAdvisersForUser(serviceErrors));
            }
        }

        if (startDate == null) {
            startDate = new DateTime();
        }

        if (endDate == null) {
            endDate = new DateTime();
        }

        DateTime verificationDate = bankDateIntegrationService.getBankDate(serviceErrors);
        if (verificationDate == null) {
            verificationDate = new DateTime();
        }

        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setVerificationDate(verificationDate);

        return request;
    }

    protected String getStatus(DateTime closeDate, DateTime maturityDate) {
        DateTime today = new DateTime();
        if (closeDate == null) {
            if (today.isBefore(maturityDate)) {
                // TM
                return TDMaturitiesStatus.OPEN.getStatus();
            } else {
                // MT
                return TDMaturitiesStatus.MATURED.getStatus();
            }
        } else {
            if (today.isBefore(maturityDate) && today.isBefore(closeDate)) {
                // TMC , TCM
                return TDMaturitiesStatus.OPEN.getStatus();
            }
            if (maturityDate.isBefore(today) && today.isBefore(closeDate)) {
                // MTC
                return TDMaturitiesStatus.MATURED.getStatus();
            }
            // MCT CTM CMT
            return TDMaturitiesStatus.WITHDRAWN.getStatus();
        }
    }

    /**
     * This method is refactored/added to fix Cyclomatic Complexity error raised by Sonar
     * 
     * @param serviceErrors
     * @return List<String> - Returns the List of OE Identifiers for logged in user
     */
    private List<String> findAdvisersForUser(ServiceErrors serviceErrors) {
        List<Broker> brokers = brokerService.getBrokersForJob(userProfileService.getActiveProfile(), serviceErrors);
        List<BrokerKey> brokerKeys = new ArrayList<>();
        for (Broker broker : brokers) {
            brokerKeys.add(broker.getKey());
        }
        return Lambda.collect(brokerKeys, Lambda.on(StringIdKey.class).getId());
    }
}
