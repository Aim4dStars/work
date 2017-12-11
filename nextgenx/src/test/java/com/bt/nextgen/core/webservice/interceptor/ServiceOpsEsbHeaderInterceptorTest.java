package com.bt.nextgen.core.webservice.interceptor;

import au.com.westpac.gn.utility.xsd.esbheader.v3.ChannelAttributes;
import au.com.westpac.gn.utility.xsd.esbheader.v3.CodeValue;
import au.com.westpac.gn.utility.xsd.esbheader.v3.Location;
import au.com.westpac.gn.utility.xsd.esbheader.v3.LocationType;
import au.com.westpac.gn.utility.xsd.esbheader.v3.Requester;
import au.com.westpac.gn.utility.xsd.esbheader.v3.RequesterType;
import com.bt.nextgen.core.security.profile.InvestorProfileService;
import com.bt.nextgen.core.security.profile.UserProfileServiceSpringImpl;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static au.com.westpac.gn.utility.xsd.esbheader.v3.ChannelType.ONLINE;
import static au.com.westpac.gn.utility.xsd.esbheader.v3.LocationType.BSB;
import static au.com.westpac.gn.utility.xsd.esbheader.v3.LocationType.MACHINE_NAME;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by l079353 on 3/07/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class ServiceOpsEsbHeaderInterceptorTest {

    private static final String UPS_BEAN_NAME = UserProfileServiceSpringImpl.BEAN_NAME;

    @Mock
    private AvaloqBankingAuthorityService avaloqBankingAuthorityService;

    @Mock
    private InvestorProfileService profileService;

    @Mock
    private ApplicationContext application;

    private ServiceOpsEsbHeaderInterceptor interceptor;

    private HttpServletRequest request;

    @Before
    public void setUp() throws Exception {
        when(application.getBean(UPS_BEAN_NAME, AvaloqBankingAuthorityService.class)).thenReturn(avaloqBankingAuthorityService);
        when(application.getBean(UPS_BEAN_NAME, InvestorProfileService.class)).thenReturn(profileService);
        interceptor = new ServiceOpsEsbHeaderInterceptor();
        interceptor.setApplicationContext(application);
        configureSamlTokenForBTUser();
        when(profileService.isExistingAvaloqUser()).thenReturn(true);
        createMockRequest();
    }

    private HttpServletRequest createMockRequest(){
        request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        return request;
    }

    private void configureSamlTokenForBTUser() {
        SamlToken token = new SamlToken(SamlUtil.loadSaml("saml-sample.xml"));
        when(profileService.getSamlToken()).thenReturn(token);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createGroupESBMessageHeaderWithBrandSiloFromRequestHeader() {
        configureProfileId("profileId");
        configureGcmId("gcmId");
        configureDealerGroupBroker("WPAC");
        request.setAttribute(Constants.REQUEST_ATTR_KEY_BRAND_SILO, "WPAC");
        final ChannelAttributes channel = interceptor.createGroupESBMessageHeader().getChannelAttributes();
        assertChannelAttributes(channel, "WPAC", "WPAC", "032909");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createGroupESBMessageHeaderWithBrandSiloFromInvestorForWestpacAdviser() {
        configureProfileId("profileId");
        configureGcmId("gcmId");
        configureDealerGroupBroker("WPAC");
        configureDealerGroupBrandSilo("WPAC");
        final ChannelAttributes channel = interceptor.createGroupESBMessageHeader().getChannelAttributes();
        assertChannelAttributes(channel, "WPAC", "WPAC", "032909");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createGroupESBMessageHeaderWithBTPLBrandSiloForServiceOps() {
        configureProfileId("profileId");
        configureGcmId("gcmId");
        request.setAttribute("silo-movement", "BTPL");
        final ChannelAttributes channel = interceptor.createGroupESBMessageHeader().getChannelAttributes();
        assertChannelAttributes(channel, "BTPL", "WPAC", "032949");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createGroupESBMessageHeaderWithWPACBrandSiloForServiceOps() {
        configureProfileId("profileId");
        configureGcmId("gcmId");
        request.setAttribute("silo-movement", "WPAC");
        final ChannelAttributes channel = interceptor.createGroupESBMessageHeader().getChannelAttributes();
        assertChannelAttributes(channel, "WPAC", "WPAC", "032909");
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

    private void configureDealerGroupBrandSilo(String brandSilo) {
        when(profileService.getDealerGroupBrandSilo()).thenReturn(brandSilo);
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

    private static void assertChannelAttributes(ChannelAttributes channel, String brandSilo, String organisationId, String bsb) {
        assertThat(channel.getBrandSilo(), is(brandSilo));
        assertThat(channel.getOrganisationId(), is(organisationId));
        assertThat(channel.getChannelType(), is(ONLINE));
        assertNotNull(channel.getOriginatingSystemId());
        assertThat(channel.getLocation(), contains(location(bsb, BSB), location(MACHINE_NAME)));
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

}