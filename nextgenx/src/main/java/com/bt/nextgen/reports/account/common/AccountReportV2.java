package com.bt.nextgen.reports.account.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import com.bt.nextgen.api.portfolio.v3.model.valuation.ParameterisedDatedValuationKey;
import com.bt.nextgen.core.reporting.BaseReportV2;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.account.AccountIntegrationServiceFactory;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.client.ClientIntegrationServiceFactory;
import com.bt.nextgen.service.integration.options.model.OptionKey;
import com.bt.nextgen.service.integration.options.service.OptionNames;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.account.PersonRelation;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.service.integration.broker.Broker;

@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
public abstract class AccountReportV2 extends BaseReportV2 {
    private static final Logger logger = LoggerFactory.getLogger(AccountReportV2.class);
    private static final String ACCOUNT_ID = "account-id";
    private static final String ACCOUNT_DATA_KEY = "AccountReportV2.accountData.";
    private static final String ADVISER_DATA_KEY = "AccountReportV2.adviserData.";
    private static final String PRODUCT_DATA_KEY = "AccountReportV2.productData.";
    private static final String USER_EXPERIENCE_DATA_KEY = "AccountReportV2.userExperienceData.";
    private static final String TRUSTEE_DISCLAIMER_CONTENT = "DS-IP-0146";
    private static final String SERVICE_TYPE = "serviceType";

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Autowired
    private AccountIntegrationServiceFactory accountIntegrationServiceFactory;

    @Autowired
    private ClientIntegrationServiceFactory clientIntegrationServiceFactory;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    private BrokerHelperService brokerHelperService;

    @Autowired
    private OptionsService optionsService;

    @ReportBean("accountHeader")
    public AccountHeaderReportData getAccountHeader(Map<String, Object> params, Map<String, Object> dataCollections) {
        AccountKey accountKey = getAccountKey(params);
        WrapAccountDetail account = getAccount(accountKey, dataCollections, getServiceType(params));
        Product product = getProduct(accountKey, params, dataCollections);
        AccountHeaderReportData header = new AccountHeaderReportData();
        header.setAccount(account);
        header.setProduct(product);
        header.setLogo(getReportLogo(params));
        header.setDisplayBsbAndAccountNumber(getBsbAccount(params));
        return header;
    }

    @ReportBean("accountFooter")
    public AccountFooterReportData getAccountFooter(Map<String, Object> params, Map<String, Object> dataCollections) {
        final AccountKey accountKey = getAccountKey(params);
        final AccountFooterReportData footer = new AccountFooterReportData();
        final String serviceType = getServiceType(params);
        final WrapAccountDetail account = getAccount(accountKey, dataCollections, serviceType);
        footer.setAccount(account);

        final ServiceErrors failFastErrors = new FailFastErrorsImpl();

        // Set adviser details for non-Direct accounts only
        if (!UserExperience.DIRECT.equals(getUserExperience(params, dataCollections))) {
            final Broker adviser = getAdviser(accountKey, params, dataCollections);
            final Broker dealer = brokerIntegrationService.getBroker(adviser.getDealerKey(), failFastErrors);
            final BrokerUser adviserUser = brokerIntegrationService.getAdviserBrokerUser(adviser.getKey(), failFastErrors);
            footer.setDealer(dealer);
            footer.setAdviserUser(adviserUser);
            footer.setIconAdviser(this.getAdviserIcon(params));
        }

        final ClientKey primaryClientKey = findPrimaryClient(account);
        final ClientDetail clientDetail =
                clientIntegrationServiceFactory.getInstance(serviceType).loadClientDetails(primaryClientKey, failFastErrors);
        footer.setClient(clientDetail);

        final Product product = getProduct(accountKey, params, dataCollections);
        footer.setProduct(product);

        final String summaryDescription = getSummaryDescription(params, dataCollections);
        final String summaryValue = getSummaryValue(params, dataCollections);

        footer.setSummaryDescription(summaryDescription);
        footer.setSummaryValue(summaryValue);
        footer.setReportTitle(getReportType(params, dataCollections));
        footer.setIconAccount(this.getAccountIcon(params));
        footer.setIconContact(this.getContactIcon(params));
        footer.setFooterBackgroundPortrait(this.getReportFatFooterPortrait(params));
        footer.setFooterBackgroundLandscape(this.getReportFatFooterLandscape(params));
        footer.setDisplayBsbAndAccountNumber(getBsbAccount(params));
        return footer;
    }

    private ClientKey findPrimaryClient(WrapAccountDetail account) {
        for (Entry<ClientKey, PersonRelation> relation : account.getAssociatedPersons().entrySet()) {
            if (relation.getValue().isPrimaryContact()) {
                return relation.getKey();
            }
        }
        return null;
    }

    // allow child classes to access this as it is a common requirement for reports
    protected AccountKey getAccountKey(Map<String, Object> params) {
        String encoded = (String) params.get(ACCOUNT_ID);
        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(encoded));
        return accountKey;
    }


    // allow child classes to access this as it is a common requirement for reports
    protected WrapAccountDetail getAccount(AccountKey key, Map<String, Object> dataCollections, String serviceType) {
        String cacheKey = ACCOUNT_DATA_KEY + key.getId();
        synchronized (dataCollections) {
            WrapAccountDetail account = (WrapAccountDetail) dataCollections.get(cacheKey);
            if (account == null) {
                ServiceErrors serviceErrors = new ServiceErrorsImpl();
                account = accountIntegrationServiceFactory.getInstance(serviceType).loadWrapAccountDetail(key, serviceErrors);
                dataCollections.put(cacheKey, account);
                if (serviceErrors.hasErrors()) {
                    logger.error("Errors during creation of base account report: {}", serviceErrors.getErrorList());
                }
            }
            return account;
        }
    }

    // allow child classes to access this as it is a common requirement for reports
    protected Broker getAdviser(AccountKey key, Map<String, Object> params, Map<String, Object> dataCollections) {
        String cacheKey = ADVISER_DATA_KEY + key.getId();
        synchronized (dataCollections) {
            Broker adviser = (Broker) dataCollections.get(cacheKey);
            if (adviser == null) {
                ServiceErrors serviceErrors = new ServiceErrorsImpl();
                adviser = brokerIntegrationService.getBroker(this.getAccount(key, dataCollections, getServiceType(params)).getAdviserKey(),
                        serviceErrors);
                dataCollections.put(cacheKey, adviser);
                if (serviceErrors.hasErrors()) {
                    logger.error("Errors during creation of base account report: {}", serviceErrors.getErrorList());
                }
            }
            return adviser;
        }

    }

    // allow child classes to access this as it is a common requirement for reports
    protected Product getProduct(AccountKey key, Map<String, Object> params, Map<String, Object> dataCollections) {
        String cacheKey = PRODUCT_DATA_KEY + key.getId();
        synchronized (dataCollections) {
            Product product = (Product) dataCollections.get(cacheKey);
            if (product == null) {
                ServiceErrors serviceErrors = new ServiceErrorsImpl();
                product = productIntegrationService
                        .getProductDetail(this.getAccount(key, dataCollections, getServiceType(params)).getProductKey(),
                                serviceErrors);
                dataCollections.put(cacheKey, product);
                if (serviceErrors.hasErrors()) {
                    logger.error("Errors during creation of base account report: {}", serviceErrors.getErrorList());
                }
            }
            return product;
        }
    }

    public String getSummaryDescription(Map<String, Object> params, Map<String, Object> dataCollections) {
        return "";
    }

    public String getSummaryValue(Map<String, Object> params, Map<String, Object> dataCollections) {
        return "";
    }

    public abstract String getReportType(Map<String, Object> params, Map<String, Object> dataCollections);

    @ReportBean("reportFileName")
    public String getReportFileName(Map<String, Object> params, Map<String, Object> dataCollections) {
        AccountKey accountKey = getAccountKey(params);
        WrapAccountDetail account = getAccount(accountKey, dataCollections, getServiceType(params));
        StringBuilder filename = new StringBuilder(account.getAccountNumber());
        filename.append(" - ");
        filename.append(getReportType(params, dataCollections).replaceAll("\\(", "").replaceAll("\\)", ""));
        return filename.toString();
    }

    public Boolean getIsTrusteeDisclaimerRequired(Map<String, Object> params) {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        AccountKey accountKey = getAccountKey(params);
        Boolean trusteeDisclaimerOption = optionsService.hasFeature(OptionKey.valueOf(OptionNames.BTTRUSTEE_DISCLAIMER),
                accountKey, serviceErrors);
        return trusteeDisclaimerOption;
    }

    @ReportBean("trusteeDisclaimer")
    public String getTrusteeDisclaimer(Map<String, Object> params) {
        Boolean isTrusteeDisclaimerRequired = getIsTrusteeDisclaimerRequired(params);
        if (isTrusteeDisclaimerRequired) {
            return getContent(TRUSTEE_DISCLAIMER_CONTENT);
        }
        return null;
    }

    public boolean getBsbAccount(Map<String, Object> params) {
        return false;
    }

    /**
     * For use by spring security annotations. For general code, the method you're looking for is @see getAccountKey()
     *
     * @return
     */
    public Object getAccountEncodedId(Map<String, Object> params) {
        return params.get(ACCOUNT_ID);
    }

    protected String getServiceType(Map<String, Object> params) {
        return (String) params.get(SERVICE_TYPE);
    }

    protected ParameterisedDatedValuationKey createParameterisedDatedValuationKey(String accountId, DateTime effectiveDate,
                                                                                  Boolean includeExternalAssets,
                                                                                  Map<String, Object> params) {
        Map<String, String> paramsMap = new HashMap<>(params.size());

        for (String key : params.keySet()) {
            Object value = params.get(key);

            // We do not want non "params"
            if (value != null && value instanceof String) {
                paramsMap.put(key, (String) value);
            }
        }

        return new ParameterisedDatedValuationKey(accountId, effectiveDate, includeExternalAssets, paramsMap);
    }

    /**
     * Returns the userExperience {@link UserExperience} for the current account. To be used by child classes
     */
    protected UserExperience getUserExperience(Map<String, Object> params,  Map<String, Object> dataCollections) {
        final AccountKey key = getAccountKey(params);
        final String cacheKey = USER_EXPERIENCE_DATA_KEY + key.getId();
        synchronized (dataCollections) {
            UserExperience userExperience = (UserExperience) dataCollections.get(cacheKey);
            if (userExperience == null) {
                userExperience = brokerHelperService.getUserExperience(getAccount(key, dataCollections, getServiceType(params)), new ServiceErrorsImpl());
                if (userExperience != null) {
                    dataCollections.put(cacheKey, userExperience);
                }
            }
            return userExperience;
        }
    }
}
