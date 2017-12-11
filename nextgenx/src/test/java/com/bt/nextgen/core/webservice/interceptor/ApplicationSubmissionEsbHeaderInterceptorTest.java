package com.bt.nextgen.core.webservice.interceptor;

import au.com.westpac.gn.utility.xsd.esbheader.v3.ChannelAttributes;
import au.com.westpac.gn.utility.xsd.esbheader.v3.CodeValue;
import au.com.westpac.gn.utility.xsd.esbheader.v3.Location;
import au.com.westpac.gn.utility.xsd.esbheader.v3.LocationType;
import au.com.westpac.gn.utility.xsd.esbheader.v3.Requester;
import au.com.westpac.gn.utility.xsd.esbheader.v3.RequesterType;
import com.bt.nextgen.core.security.profile.InvestorProfileService;
import com.bt.nextgen.core.security.profile.UserProfileServiceSpringImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;
import com.bt.nextgen.util.SamlUtil;
import com.btfin.panorama.core.security.avaloq.AvaloqBankingAuthorityService;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.ExternalBrokerKey;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static au.com.westpac.gn.utility.xsd.esbheader.v3.ChannelType.ONLINE;
import static au.com.westpac.gn.utility.xsd.esbheader.v3.LocationType.BSB;
import static au.com.westpac.gn.utility.xsd.esbheader.v3.LocationType.MACHINE_NAME;
import static au.com.westpac.gn.utility.xsd.esbheader.v3.RequesterType.CUSTOMER;
import static au.com.westpac.gn.utility.xsd.esbheader.v3.RequesterType.STAFF;
import static com.bt.nextgen.core.security.profile.UserProfileServiceSpringImpl.BRAND_SILO;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for the {@code ApplicationSubmissionEsbHeaderInterceptor}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ApplicationSubmissionEsbHeaderInterceptorTest {

    private static final String UPS_BEAN_NAME = UserProfileServiceSpringImpl.BEAN_NAME;

    @Mock
    private AvaloqBankingAuthorityService avaloqBankingAuthorityService;

    @Mock
    private InvestorProfileService profileService;

    @Mock
    private ServiceErrors serviceErrors;

    @Mock
    private ApplicationContext application;

    private MockHttpServletRequest request;

    private MockHttpSession session;

    private ApplicationSubmissionEsbHeaderInterceptor interceptor;

    private static void assertChannelAttributes(ChannelAttributes channel, String brandSilo, String organisationId, String bsb,
        String requesterId, RequesterType requesterType, String groupType) {
        assertThat(channel.getBrandSilo(), is(brandSilo));
        assertThat(channel.getOrganisationId(), is(organisationId));
        assertThat(channel.getChannelType(), is(ONLINE));
        assertNotNull(channel.getOriginatingSystemId());
        assertThat(channel.getSystemInfo().getCodeValue(), contains(codeValue("Destination", "OnboardingStatusUpdateAdapter")));
        assertThat(channel.getLocation(), contains(location(bsb, BSB), location(MACHINE_NAME)));
        assertThat(channel.getRequester(), contains(requester(requesterId, requesterType, groupType)));
    }

    private static void assertChannelAttributesForWPLInvestor(ChannelAttributes channel, String brandSilo, String bsb,
                                                              String requesterId, RequesterType requesterType) {
        assertThat(channel.getBrandSilo(), is(brandSilo));
        assertThat(channel.getOrganisationId(), is(brandSilo));
        assertThat(channel.getChannelType(), is(ONLINE));
        assertNotNull(channel.getOriginatingSystemId());
        assertThat(channel.getSystemInfo().getCodeValue(), contains(codeValue("Destination", "OnboardingStatusUpdateAdapter")));
        assertThat(channel.getLocation(), contains(location(bsb, BSB), location(MACHINE_NAME)));
        final List<Requester> requesters = channel.getRequester();
        final Requester requester = requesters.get(0);
        assertThat(requester.getRequesterId(), is(requesterId));
        assertThat(requester.getRequesterType(), is(requesterType));
    }

    private static Matcher<CodeValue> codeValue(final String code, final String value) {
        return new BaseMatcher<CodeValue>() {
            @Override
            public boolean matches(Object item) {
                final CodeValue codeValue = (CodeValue) item;
                return codeValue.getCode().equals(code) && codeValue.getValue().equals(value);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("CodeValue[code:" + code + "; value:" + value + "]");
            }
        };
    }

    private static Matcher<Requester> requester(final String id, final RequesterType type, final String groupType) {
        return new BaseMatcher<Requester>() {
            @Override
            public boolean matches(Object item) {
                final Requester requester = (Requester) item;
                return requester.getRequesterType().equals(type)
                        && requester.getRequesterId().equals(id)
                        && requester.getRequesterGroupType().equals(groupType);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Requester[id:").appendText(id).appendText(";type:").appendText(type.toString());
                if (groupType != null) {
                    description.appendText(";groupType:").appendText(groupType);
                }
                description.appendText("]");
            }
        };
    }

    public static Matcher<Location> location(final LocationType type) {
        return location(null, type);
    }

    public static Matcher<Location> location(final String id, final LocationType type) {
        return new BaseMatcher<Location>() {
            @Override
            public boolean matches(Object item) {
                final Location location = (Location) item;
                return (id == null || id.equals(location.getLocationId())) && type == location.getLocationType();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Location[");
                if (id != null) {
                    description.appendText("id:" + id + ";");
                }
                description.appendText("type:" + type + "]");
            }
        };
    }

    @Before
    public void initInterceptor() {
        when(application.getBean(UPS_BEAN_NAME, AvaloqBankingAuthorityService.class)).thenReturn(avaloqBankingAuthorityService);
        when(application.getBean(UPS_BEAN_NAME, InvestorProfileService.class)).thenReturn(profileService);
        interceptor = new ApplicationSubmissionEsbHeaderInterceptor();
        interceptor.setApplicationContext(application);
        configureSamlTokenForBTUser();
        when(profileService.isExistingAvaloqUser()).thenReturn(true);
        createMockRequest();
    }

    private HttpServletRequest createMockRequest(){
        request = new MockHttpServletRequest();
        session = new MockHttpSession();
        request.setSession(session);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        return request;
    }
    @Test
    @SuppressWarnings("unchecked")
    public void createGroupESBMessageHeaderWithBrandSiloFromInvestorForWestpacAdviser() {
        configureProfileId("profileId");
        configureGcmId("gcmId");
        configureDealerGroupBroker("WPAC");
        request.setAttribute(Constants.REQUEST_ATTR_KEY_BRAND_SILO, "WPAC");

        final ChannelAttributes channel = interceptor.createGroupESBMessageHeader().getChannelAttributes();
        assertChannelAttributes(channel, "WPAC", "WPAC", "032909", "gcmId", STAFF, "profileId");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createGroupESBMessageHeaderForWestpacAdviser() {
        configureProfileId("profileId");
        configureGcmId("gcmId");
        configureDealerGroupBroker("WPAC");
        session.setAttribute(BRAND_SILO,"WPAC");
        final ChannelAttributes channel = interceptor.createGroupESBMessageHeader().getChannelAttributes();
        assertChannelAttributes(channel, "WPAC", "WPAC", "032909", "gcmId", STAFF, "profileId");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createGroupESBMessageHeaderWithBrandSiloFromInvestorForWestpacBrandedAdviser() {
        configureProfileId("profileId2");
        configureGcmId("gcmId2");
        configureDealerGroupBroker("CE.IFA");
        request.setAttribute(Constants.REQUEST_ATTR_KEY_BRAND_SILO, "WPAC");

        final ChannelAttributes channel = interceptor.createGroupESBMessageHeader().getChannelAttributes();
        assertChannelAttributes(channel, "WPAC", "WPAC", "032909", "gcmId2", STAFF, "profileId2");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createGroupESBMessageHeaderForWestpacBrandedAdviser() {
        configureProfileId("profileId2");
        configureGcmId("gcmId2");
        configureDealerGroupBroker("CE.IFA");
        final ChannelAttributes channel = interceptor.createGroupESBMessageHeader().getChannelAttributes();
        assertChannelAttributes(channel, "WPAC", "WPAC", "032909", "gcmId2", STAFF, "profileId2");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createGroupESBMessageHeaderWithBrandSiloFromInvestorForAnotherWestpacBrandedAdviser() {
        configureProfileId("profileId3");
        configureGcmId("gcmId3");
        configureDealerGroupBroker("DG.IPTSDG");
        request.setAttribute(Constants.REQUEST_ATTR_KEY_BRAND_SILO, "WPAC");

        final ChannelAttributes channel = interceptor.createGroupESBMessageHeader().getChannelAttributes();
        assertChannelAttributes(channel, "WPAC", "WPAC", "032909", "gcmId3", STAFF, "profileId3");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createGroupESBMessageHeaderForAnotherWestpacBrandedAdviser() {
        configureProfileId("profileId3");
        configureGcmId("gcmId3");
        configureDealerGroupBroker("DG.IPTSDG");
        final ChannelAttributes channel = interceptor.createGroupESBMessageHeader().getChannelAttributes();
        assertChannelAttributes(channel, "WPAC", "WPAC", "032909", "gcmId3", STAFF, "profileId3");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createGroupESBMessageHeaderWithBrandSiloFromInvestorForNonWestpacAdviser() {
        configureProfileId("profileId");
        configureGcmId("gcmId");
        configureCisKey("cisKey");
        configureDealerGroupBroker("BTPL");
        request.setAttribute(Constants.REQUEST_ATTR_KEY_BRAND_SILO, "BTPL");
        final ChannelAttributes channel = interceptor.createGroupESBMessageHeader().getChannelAttributes();
        assertChannelAttributes(channel, "BTPL", "WPAC", "032949", "cisKey", STAFF, "profileId");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createGroupESBMessageHeaderForNonWestpacAdviser() {
        configureProfileId("profileId");
        configureGcmId("gcmId");
        configureCisKey("cisKey");
        configureDealerGroupBroker("BTPL");
        session.setAttribute(BRAND_SILO,"BTPL");
        final ChannelAttributes channel = interceptor.createGroupESBMessageHeader().getChannelAttributes();
        assertChannelAttributes(channel, "BTPL", "WPAC", "032949", "cisKey", STAFF, "profileId");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createGroupESBMessageHeaderWithBrandSiloFromInvestorForNonWestpacAdviserWithNullCISKey() {
        configureProfileId("profileId");
        configureGcmId("gcmId");
        configureCisKey(null);
        configureDealerGroupBroker("BTPL");
        request.setAttribute(Constants.REQUEST_ATTR_KEY_BRAND_SILO, "BTPL");

        final ChannelAttributes channel = interceptor.createGroupESBMessageHeader().getChannelAttributes();
        assertChannelAttributes(channel, "BTPL", "WPAC", "032949", "gcmId", STAFF, "profileId");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createGroupESBMessageHeaderForNonWestpacAdviserWithNullCISKey() {
        configureProfileId("profileId");
        configureGcmId("gcmId");
        configureCisKey(null);
        configureDealerGroupBroker("BTPL");
        session.setAttribute(BRAND_SILO,"BTPL");
        final ChannelAttributes channel = interceptor.createGroupESBMessageHeader().getChannelAttributes();
        assertChannelAttributes(channel, "BTPL", "WPAC", "032949", "gcmId", STAFF, "profileId");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createGroupESBMessageHeaderWithBrandSiloFromInvestorForAdviserWithNullDealerGroup() {
        configureProfileId("profileId");
        configureGcmId("gcmId");
        configureCisKey("cisKey");
        configureDealerGroupBroker(null);
        request.setAttribute(Constants.REQUEST_ATTR_KEY_BRAND_SILO, "BTPL");
        final ChannelAttributes channel = interceptor.createGroupESBMessageHeader().getChannelAttributes();
        assertChannelAttributes(channel, "BTPL", "WPAC", "032949", "cisKey", STAFF, "profileId");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createGroupESBMessageHeaderForAdviserWithNullDealerGroup() {
        configureProfileId("profileId");
        configureGcmId("gcmId");
        configureCisKey("cisKey");
        configureDealerGroupBroker(null);
        session.setAttribute(BRAND_SILO,"BTPL");
        final ChannelAttributes channel = interceptor.createGroupESBMessageHeader().getChannelAttributes();
        assertChannelAttributes(channel, "BTPL", "WPAC", "032949", "cisKey", STAFF, "profileId");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createGroupESBMessageHeaderWithBrandSiloFromInvestorForAdviserWithNullDealerGroupEbiKey() {
        configureProfileId("profileId");
        configureGcmId("gcmId");
        configureCisKey("cisKey");

        final Broker broker = mock(Broker.class);
        when(profileService.getDealerGroupBroker()).thenReturn(broker);
        when(broker.getParentEBIKey()).thenReturn(null);
        request.setAttribute(Constants.REQUEST_ATTR_KEY_BRAND_SILO, "BTPL");

        final ChannelAttributes channel = interceptor.createGroupESBMessageHeader().getChannelAttributes();
        assertChannelAttributes(channel, "BTPL", "WPAC", "032949", "cisKey", STAFF, "profileId");
    }


    @Test
    @SuppressWarnings("unchecked")
    public void createGroupESBMessageHeaderForAdviserWithNullDealerGroupEbiKey() {
        configureProfileId("profileId");
        configureGcmId("gcmId");
        configureCisKey("cisKey");

        final Broker broker = mock(Broker.class);
        when(profileService.getDealerGroupBroker()).thenReturn(broker);
        when(broker.getParentEBIKey()).thenReturn(null);
        session.setAttribute(Constants.REQUEST_ATTR_KEY_BRAND_SILO, "BTPL");
        final ChannelAttributes channel = interceptor.createGroupESBMessageHeader().getChannelAttributes();
        assertChannelAttributes(channel, "BTPL", "WPAC", "032949", "cisKey", STAFF, "profileId");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createGroupESBMessageHeaderWithBrandSiloFromInvestorForServiceOpsUser() {
        configureProfileId("serviceOps");
        configureGcmId("gcmId9");
        configureCisKey(null);
        when(profileService.isServiceOperator()).thenReturn(true);
        configureDealerGroupBroker(null);
        request.setAttribute(Constants.REQUEST_ATTR_KEY_BRAND_SILO, "WPAC");

        final ChannelAttributes channel = interceptor.createGroupESBMessageHeader().getChannelAttributes();
        assertChannelAttributes(channel, "WPAC", "WPAC", "032909", "gcmId9", STAFF, "serviceOps");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createGroupESBMessageHeaderForServiceOpsUser() {
        configureProfileId("serviceOps");
        configureGcmId("gcmId9");
        configureCisKey(null);
        when(profileService.isServiceOperator()).thenReturn(true);
        configureDealerGroupBroker(null);
        final ChannelAttributes channel = interceptor.createGroupESBMessageHeader().getChannelAttributes();
        assertChannelAttributes(channel, "WPAC", "WPAC", "032909", "gcmId9", STAFF, "serviceOps");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createGroupESBMessageHeaderWithBrandSiloForInvestorFromReqAttrForWPLUser() {///
        configureSamlTokenForWPLUser();
        request.setAttribute(Constants.REQUEST_ATTR_KEY_BRAND_SILO, "WPAC");

        final ChannelAttributes channel = interceptor.createGroupESBMessageHeader().getChannelAttributes();
        assertChannelAttributesForWPLInvestor(channel, "WPAC", "032909", "237329466", CUSTOMER);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createGroupESBMessageHeaderForWPLUser() {
        configureSamlTokenForWPLUser();
        final ChannelAttributes channel = interceptor.createGroupESBMessageHeader().getChannelAttributes();
        assertChannelAttributesForWPLInvestor(channel, "WPAC", "032909", "237329466", CUSTOMER);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createGroupESBMessageHeaderForWPLAndBTUser() {
        configureSamlTokenForWPLUser();
        configureGcmId("GCMWPL");
        configureProfileId("investor");
        final ChannelAttributes channel = interceptor.createGroupESBMessageHeader().getChannelAttributes();
        assertChannelAttributesForWPLInvestor(channel, "WPAC", "032909", "237329466", CUSTOMER);
    }

    @Test
    public void testRequestType_forWestpacAdviser() {
        configureProfileId("profileId");
        configureGcmId("gcmId");
        configureDealerGroupBroker("WPAC");
        configureJobRole(JobRole.ADVISER);
        final Requester requester = interceptor.requester(getChannelAttributes("WPAC"));
        assertEquals(requester.getRequesterType(), STAFF);
    }

    @Test
    public void testRequestType_forWPLInvestor() {
        configureSamlTokenForWPLUser();
        configureProfileId("profileId");
        configureGcmId("gcmId");
        configureDealerGroupBroker("WPAC");
        configureJobRole(JobRole.INVESTOR);
        final Requester requester = interceptor.requester(getChannelAttributes("WPAC"));
        assertEquals(requester.getRequesterType(), CUSTOMER);
    }

    @Test
    public void testRequestType_forServiceOps() {
        configureProfileId("profileId");
        configureGcmId("gcmId");
        configureDealerGroupBroker("WPAC");
        configureJobRole(JobRole.SERVICE_AND_OPERATION);

        final Requester requester = interceptor.requester(getChannelAttributes("WPAC"));
        assertEquals(requester.getRequesterType(), STAFF);
    }

    @Test
    public void testRequestType_forNonWestpacAdviser() {
        configureProfileId("profileId");
        configureGcmId("gcmId");
        configureDealerGroupBroker("BTPL");
        configureJobRole(JobRole.ADVISER);

        final Requester requester = interceptor.requester(getChannelAttributes("BTPL"));
        assertEquals(requester.getRequesterType(), STAFF);
    }

    @Test
    public void testRequestType_forBTPLInvestor() {
        configureProfileId("profileId");
        configureGcmId("gcmId");
        configureDealerGroupBroker("BTPL");
        configureJobRole(JobRole.INVESTOR);

        final Requester requester = interceptor.requester(getChannelAttributes("BTPL"));
        assertEquals(requester.getRequesterType(), CUSTOMER);
    }

    private void configureProfileId(String profileId) {
        final JobProfileIdentifier identifier = mock(JobProfileIdentifier.class);
        when(identifier.getProfileId()).thenReturn(profileId);
        when(avaloqBankingAuthorityService.getJobProfile()).thenReturn(identifier);
    }

    private void configureGcmId(String gcmId) {
        when(profileService.getGcmId()).thenReturn(gcmId);
    }

    private void configureCisKey(String cisKey) {
        final UserProfile profile = mock(UserProfile.class);
        when(profileService.getActiveProfile()).thenReturn(profile);
        when(profile.getCISKey()).thenReturn(CISKey.valueOf(cisKey));
    }

    private void configureDealerGroupBroker(String parentEBIKey) {
        Broker broker = null;
        if (parentEBIKey != null) {
            broker = mock(Broker.class);
            when(broker.getParentEBIKey()).thenReturn(ExternalBrokerKey.valueOf(parentEBIKey));
        }
        when(profileService.getDealerGroupBroker()).thenReturn(broker);
    }

    private void configureSamlTokenForBTUser() {
        SamlToken token = new SamlToken(SamlUtil.loadSaml("saml-sample.xml"));
        when(profileService.getSamlToken()).thenReturn(token);
    }

    private void configureSamlTokenForWPLUser() {
        SamlToken token = new SamlToken(SamlUtil.loadSaml("saml-sample-wpl.xml"));
        when(profileService.getSamlToken()).thenReturn(token);
    }

    private void configureJobRole(JobRole jobRole) {
        final UserProfile profile = mock(UserProfile.class);
        when(profileService.getActiveProfile()).thenReturn(profile);
        when(profile.getJobRole()).thenReturn(jobRole);
    }

    private ChannelAttributes getChannelAttributes(String brandSilo) {
        ChannelAttributes channelAttributes = new ChannelAttributes();
        channelAttributes.setBrandSilo(brandSilo);
        return channelAttributes;
    }

}
