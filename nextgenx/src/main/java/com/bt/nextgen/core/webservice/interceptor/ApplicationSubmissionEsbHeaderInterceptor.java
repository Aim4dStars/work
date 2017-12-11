package com.bt.nextgen.core.webservice.interceptor;

import au.com.westpac.gn.utility.xsd.esbheader.v3.ChannelAttributes;
import au.com.westpac.gn.utility.xsd.esbheader.v3.CodeValue;
import au.com.westpac.gn.utility.xsd.esbheader.v3.Location;
import au.com.westpac.gn.utility.xsd.esbheader.v3.Requester;
import au.com.westpac.gn.utility.xsd.esbheader.v3.SystemInfo;
import ch.lambdaj.Lambda;
import com.bt.nextgen.core.security.profile.InvestorProfileService;
import com.bt.nextgen.core.security.profile.UserProfileServiceSpringImpl;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.integration.user.CISKey;
import com.btfin.panorama.core.security.avaloq.AvaloqBankingAuthorityService;
import com.btfin.panorama.core.security.integration.customer.UserGroup;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.ExternalBrokerKey;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;
import java.util.List;

import static au.com.westpac.gn.utility.xsd.esbheader.v3.LocationType.BSB;
import static au.com.westpac.gn.utility.xsd.esbheader.v3.RequesterType.CUSTOMER;
import static au.com.westpac.gn.utility.xsd.esbheader.v3.RequesterType.STAFF;
import static java.util.Arrays.asList;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * ESB header interceptor for use with Client Application submission messages.
 * <p><b>NB:</b> do <i>not</i> use the Spring {@code @Autowired} annotations in this class (or any interceptors),
 * as they are initialised as {@code @Bean}-annotated POJOs from the WebServiceConfig, and this does not appear to
 * mesh well with the {@code @Autowired} annotation.</p>
 *
 * @see com.bt.nextgen.config.WebServiceConfig#applicationSubmissionEsbHeaderInterceptor()
 */
public class ApplicationSubmissionEsbHeaderInterceptor extends EsbHeaderAdderInterceptor implements ApplicationContextAware {

    public static final String BTPL_BRAND_SILO = "BTPL";

    private static final String WPAC_BSB_VALUE = "032909";

    private static final String BTPL_BSB_VALUE = "032949";

    private static final String CE_IFA = "CE.IFA";
    private static final String IPTSDG = "DG.IPTSDG";

    private static final Collection<String> WPAC_DG_EBI_KEYS = asList(WPAC_BRAND_SILO, CE_IFA, IPTSDG);

    private static final Logger LOGGER = getLogger(ApplicationSubmissionEsbHeaderInterceptor.class);

    public static final String DIRECT_DEALER_GROUP_PAN_NO = "direct.dealerGroup.panNo";

    private BeanFactory beans;

    private AvaloqBankingAuthorityService avaloqBankingAuthorityService;

    private UserProfileService profileService;

    /**
     * Save a reference to the application context. Note that this is all we do here, we do NOT go enquiring for
     * service beans at this point in the application lifecycle, as Spring auto-wiring has (apparently) yet to fully
     * complete and sort itself out at this stage.
     *
     * @param context the application context.
     */
    @Override
    public synchronized void setApplicationContext(ApplicationContext context) {
        this.beans = context;
    }

    /**
     * Need to load the BankingAuthorityService well after the application has started up, or the wrong bean gets
     * inserted by Spring auto-wiring SNAFU.
     *
     * @return the banking authority service bean.
     */
    private synchronized AvaloqBankingAuthorityService getBankingAuthorityService() {
        if (avaloqBankingAuthorityService == null) {
            avaloqBankingAuthorityService = beans.getBean(UserProfileServiceSpringImpl.BEAN_NAME, AvaloqBankingAuthorityService.class);
            LOGGER.info("Loaded bankingAuthorityService:{}", avaloqBankingAuthorityService);
        }
        return avaloqBankingAuthorityService;
    }

    /**
     * Need to load the UserProfileService well after the application has started up, or the wrong bean gets
     * inserted by Spring auto-wiring SNAFU.
     *
     * @return the user profile service bean.
     */
    private synchronized UserProfileService getProfileService() {
        if (profileService == null) {
            profileService = beans.getBean(UserProfileServiceSpringImpl.BEAN_NAME, InvestorProfileService.class);
            LOGGER.info("Loaded profileService:{}", profileService);
        }
        return profileService;
    }

    @Override
    protected void setChannelAttributes(ChannelAttributes channel) {
        super.setChannelAttributes(channel);
        final Location location;
        if(isWPLInvestor() || isOperator() || isWestpacAdviser()) {
            location = location(WPAC_BSB_VALUE, BSB);
        }  else {
            LOGGER.info("Non-Westpac adviser, using \"{}\" Org ID", WPAC_BRAND_SILO);
            location = location(BTPL_BSB_VALUE, BSB);
            channel.setOrganisationId(WPAC_BRAND_SILO);
        }
        LOGGER.info("Adviser, using \"{}\" Brand silo", channel.getBrandSilo());
        channel.getLocation().add(0, location);
        channel.setSystemInfo(systemInfo(codeValue("Destination", "OnboardingStatusUpdateAdapter")));
    }

    private boolean isOperator() {
        return getProfileService().isServiceOperator();
    }

    private boolean isWestpacAdviser() {
        //TODO - UPS REFACTOR1 - This doesn't have a context (it is related to the OE super dealer group of the adviser and shoudn't vary between investors or advisers)
        final Broker dealerGroup = getProfileService().getDealerGroupBroker();
        if (dealerGroup != null) {
            final ExternalBrokerKey parentEBIKey = dealerGroup.getParentEBIKey();
            if(parentEBIKey != null){
                LOGGER.info("Parent EBI key {}", parentEBIKey.getId());
                return WPAC_DG_EBI_KEYS.contains(parentEBIKey.getId());
            }
            LOGGER.info("Parent EBI key not found");
        }
        return false;
    }

    /**
     * Override the requester attribute to return staff or customer.
     *
     * @param channel current set of channel attributes.
     * @return the approriate requester type.
     */
    @Override
    protected Requester requester(ChannelAttributes channel) {
        final Requester requester = super.requester(channel);
        final String gcmId = getProfileService().getGcmId();
        if (BTPL_BRAND_SILO.equals(channel.getBrandSilo())) {
            // External adviser
            final UserProfile profile = getProfileService().getActiveProfile();
            final CISKey cisKey = profile.getCISKey();
            final String requesterId;
            if (cisKey != null) {
                requesterId = cisKey.getId();
            } else {
                LOGGER.warn("Null CIS key for user with GCM ID:{}; using GCM ID instead", gcmId);
                requesterId = gcmId;
            }
            requester.setRequesterId(requesterId);
            requester.setRequesterType(JobRole.INVESTOR == profile.getJobRole() ? CUSTOMER : STAFF);
            requester.setRequesterGroupType(getBankingAuthorityService().getJobProfile().getProfileId());
        } else {
            if (isWPLInvestor()) {
                requester.setRequesterId(Properties.getString(DIRECT_DEALER_GROUP_PAN_NO));
                requester.setRequesterType(CUSTOMER);
            } else {
                requester.setRequesterId(gcmId);
                requester.setRequesterGroupType(getBankingAuthorityService().getJobProfile().getProfileId());
                requester.setRequesterType(STAFF);
            }
        }
        return requester;
    }

    protected final SystemInfo systemInfo(CodeValue... codeValues) {
        final SystemInfo systemInfo = factory.createSystemInfo();
        systemInfo.getCodeValue().addAll(asList(codeValues));
        return systemInfo;
    }

    private boolean isWPLInvestor() {
        final List<UserGroup> userGroup = getProfileService().getSamlToken().getUserGroup();
        return Lambda.exists(userGroup, new LambdaMatcher<UserGroup>() {
            @Override
            protected boolean matchesSafely(UserGroup userGroup) {
                return userGroup.equals(UserGroup.WPL_USER);
            }
        });
    }

    protected final CodeValue codeValue(String code, String value) {
        final CodeValue codeValue = factory.createCodeValue();
        codeValue.setCode(code);
        codeValue.setValue(value);
        return codeValue;
    }
}
