package com.bt.nextgen.core.web;

import static com.bt.nextgen.core.util.SETTINGS.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import javax.servlet.http.HttpServletRequest;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.core.util.Properties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;

import com.bt.nextgen.util.SamlUtil;
import org.springframework.web.context.request.ServletRequestAttributes;


public class RequestQueryTest extends BaseSecureIntegrationTest
{



    MockHttpServletRequest request = new MockHttpServletRequest();

    MockHttpServletRequest requestAuthenticated = new MockHttpServletRequest();


    @Autowired
    RequestQuery requestQuery;

	private String[] adviserAuthority = {"ADVISER"};
	private String[] investorAuthority = {"INVESTOR"};

	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
        request.addHeader(SECURITY_HEADER_XFORWARDHOST.value(), "localhost");
        request.addHeader(SAML_HEADER_WBC.value(),getAdviserSaml());
        ServletRequestAttributes attrs= new ServletRequestAttributes(request);
		RequestContextHolder.setRequestAttributes(attrs);
        requestAuthenticated.addHeader(SECURITY_HEADER_XFORWARDHOST.value(), "localhost");
        requestAuthenticated.addHeader(SAML_HEADER_WBC.value(),getAdviserSaml());

		// do you when's on attrs
	}

	private HttpServletRequest createAuthenticatedAdviserRequest()
	{
		MockHttpServletRequest adviserRequest = new MockHttpServletRequest();
		adviserRequest.addHeader(SECURITY_HEADER_XFORWARDHOST.value(),"panoramaadviser.com.au");
		adviserRequest.addHeader(SAML_HEADER_WBC.value(),getAdviserSaml());
		return adviserRequest;
	}

	private HttpServletRequest createAuthenticatedInvestorRequest()
	{
		MockHttpServletRequest investorRequest = new MockHttpServletRequest();
		investorRequest.addHeader(SECURITY_HEADER_XFORWARDHOST.value(),"panoramainvestor.com.au");
		investorRequest.addHeader(SAML_HEADER_WBC.value(),getInvestorSaml());
		return investorRequest;
	}



	private String getAdviserSaml()
	{
		return SamlUtil.loadSaml("adviser", adviserAuthority, "123123123");
	}

	private String getInvestorSaml()
	{
		return SamlUtil.loadSaml("investor", investorAuthority, "321321321");
	}


	@Test
	public void testIsWebSealRequestProd() throws Exception
	{
         java.util.Properties properties =Properties.all();
        properties.setProperty("environment","PROD");
        boolean flag=requestQuery.isWebSealRequest();
        assertThat(flag, is(true));
        properties.setProperty("environment","DEV");
        Assert.assertEquals(properties.getProperty("environment"),"DEV");
	}

    @Test
    public void testIsWebSealRequestDev() throws Exception
    {
        boolean flag=requestQuery.isWebSealRequest();
        assertThat(flag, is(false));
    }

	@Test
	public void testGetOriginalHost() throws Exception
	{
        String host=requestQuery.getOriginalHost();
        assertThat(host, is("localhost"));
	}

	@Test
	public void testGetSamlToken() throws Exception
	{
        SamlToken token =requestQuery.getSamlToken();
         assertThat(token.getBankReferenceId(), is("123123123"));
        assertThat(token.getAvaloqId(), is("123123123"));
        assertThat(token.getUsername(), is("adviser"));
        assertThat(token.getCredentialType().name(), is("ONL"));

	}

	@Test
	public void testIsInvestorOnInvestorSite() throws Exception
	{
        ServletRequestAttributes attrs= new ServletRequestAttributes(createAuthenticatedInvestorRequest());
        RequestContextHolder.setRequestAttributes(attrs);

        Assert.assertTrue(requestQuery.isInvestorOnInvestorSite());
	}

	@Test
	public void testIsAdviserOnAdviserSite() throws Exception
	{
        ServletRequestAttributes attrs= new ServletRequestAttributes(createAuthenticatedAdviserRequest());
        RequestContextHolder.setRequestAttributes(attrs);
        requestQuery.isAdviserOnAdviserSite();
        Assert.assertTrue(requestQuery.isAdviserOnAdviserSite());
	}

	@Test
	public void testIsUserAuthenticated() throws Exception
	{
       /* ServletRequestAttributes attrAuthenticated= new ServletRequestAttributes(requestAuthenticated);
        requestAuthenticated.addHeader(SECURITY_HEADER_USERNAME.value(),"213456789");
        RequestContextHolder.setRequestAttributes(attrAuthenticated);
        boolean flag=requestQuery.isUserAuthenticated();
        Assert.assertTrue(flag);

        ServletRequestAttributes attr= new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attr);
        request.addHeader(SECURITY_HEADER_USERNAME.value(),"unauthenticated");
        flag=requestQuery.isUserAuthenticated();
        Assert.assertFalse(flag);*/



	}
}
