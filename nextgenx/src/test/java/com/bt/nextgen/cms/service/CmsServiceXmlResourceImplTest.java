package com.bt.nextgen.cms.service;

import com.bt.nextgen.cms.CmsEntry;
import com.btfin.panorama.core.security.Roles;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.security.profile.Profile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.web.RequestQuery;
import com.bt.nextgen.util.SamlUtil;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class CmsServiceXmlResourceImplTest
{
	@InjectMocks
    CmsServiceXmlResourceImpl cfi = new CmsServiceXmlResourceImpl(
            Arrays.asList(new DefaultResourceLoader().getResource("classpath:cms-test.xml"),
                    new DefaultResourceLoader().getResource("classpath:cms-errors-test.xml")));

	@Mock
	UserProfileService userProfileService;

    @Mock
    RequestQuery requestQuery;

    @Before
	public void setup()
	{

	}

	public void getTestingAuthenticationToken()
	{
		TestingAuthenticationToken authentication = new TestingAuthenticationToken("testadv",
			"60000021",
			Roles.ROLE_ADVISER.name());
		Profile dummyProfile = new Profile(new SamlToken(SamlUtil.loadSaml()));
		authentication.setDetails(dummyProfile);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	@Test
	public void testGetContent() throws Exception
	{
		CmsEntry cmsEntry = cfi.getRawContent("key1");
		assertThat(cmsEntry.getValue(), is("Here is my content"));
		assertThat(cmsEntry.getMetaData("attributeTest"), is("test123"));
		assertThat(cmsEntry.getMetaData("attribute2Test"), is("123test"));

	}

	@Ignore("sammutj - TODO fix this, currently the cms setting isn't plugged into CFI effectively")
	@Test
	public void loadFileContent()
	{
		CmsEntry cmsEntry = cfi.getRawContent("file");
		assertThat(cmsEntry.getMetaData("type"), is("url"));
		assertThat(cmsEntry.getValue(), is("hello world!"));

	}

	@Test
	public void testMessageWithContactPlaceholderDefault()
	{
		String message = cfi.getContent("ContactUs");
		assertThat(message, is("Please call us on 1300 881 716"));
	}

	@Test
	public void testMessageWithContactPlaceholderForAdviser()
	{
		Mockito.when(userProfileService.getPrimaryRole()).thenReturn(Roles.ROLE_ADVISER);
		String message = cfi.getContent("ContactUs");
		assertThat(message, is("Please call us on 1300 784 207"));
	}

	@Test
	public void testMessageWithContactPlaceholderForInvestor()
	{
		Mockito.when(userProfileService.getPrimaryRole()).thenReturn(Roles.ROLE_INVESTOR);
		String message = cfi.getContent("ContactUs");
		assertThat(message, is("Please call us on 1300 881 716"));
	}

	@Test
	public void test_reLoadCmsContent()
	{
		CmsService.STATUS message = cfi.reLoadCmsContent();
		assertThat(message, is(CmsService.STATUS.SUCCESS));

		CmsEntry cmsEntry = cfi.getRawContent("key1");
		assertThat(cmsEntry.getValue(), is("Here is my content"));
		assertThat(cmsEntry.getMetaData("attributeTest"), is("test123"));
		assertThat(cmsEntry.getMetaData("attribute2Test"), is("123test"));
	}

	@Test
	public void test_IfNoKeyFound_getContent_ReturnsNullValue()
	{
		assertThat(cfi.getContent("keyNotInCms"), IsNull.nullValue());
	}

	@Test
	public void testGetErrorCodeEntrySet() throws JAXBException, IOException, XMLStreamException
	{
        Set<Entry<String, CmsEntryJaxb>> errorCodeEntrySet = cfi.getErrorCodeEntrySet();
        for (Entry<String, CmsEntryJaxb> entry : errorCodeEntrySet) {
            String key = entry.getKey();
            CmsEntry cmsEntry = cfi.getRawContent(key);
            assertThat(cmsEntry.getValue(), is(entry.getValue().getValue()));
        }
        // String[] args = null;
        // String str = cfi.getDynamicContent("err00007", args);
        // assertThat(str, is("This amount exceeds your daily payment limit"));
	}

	@Test
	public void testgetDynamicContent()
	{
		{
			String[] args = null;
			//Err.IP-0049 Error code not present in cms-test.xml
			String str = cfi.getDynamicContent("Err.IP-0049", args);
			assertNull(str);
		}
		{
			String[] args = null;
			String str = cfi.getDynamicContent("Err.IP-0040", args);
			assertThat(str, is("Invalid asset code"));
		}
		{
			String[] args =
			{
				"Arugment1", "Arugment2"
			};
			String str = cfi.getDynamicContent("Err.IP-0040", args);
			assertThat(str, is("Invalid asset code"));
		}
		{
			String[] args =
			{
				"Arugment1"
			};
			String str = cfi.getDynamicContent("Err.IP-0041", args);
			assertThat(str, is("Invalid asset code for Arugment1"));
		}
		{
			String[] args =
			{
				"Arugment1", "Arugment2"
			};
			String str = cfi.getDynamicContent("Err.IP-0041", args);
			assertThat(str, is("Invalid asset code for Arugment1"));
		}
		{
			String[] args = null;
			String str = cfi.getDynamicContent("Err.IP-0042", args);
			assertThat(str, is("Invalid asset code for {1} and {2}"));
		}
		{
			String[] args =
			{
				"Arugment1", "Arugment2"
			};
			String str = cfi.getDynamicContent("Err.IP-0042", args);
			assertThat(str, is("Invalid asset code for Arugment1 and Arugment2"));
		}
		{
			String[] args =
			{
				"Arugment1", "Arugment2", "Arugment3", "Arugment4"
			};
			String str = cfi.getDynamicContent("Err.IP-0042", args);
			assertThat(str, is("Invalid asset code for Arugment1 and Arugment2"));
		}
		{
			String[] args =
			{
				"Arugment1", "Arugment2"
			};
			String str = cfi.getDynamicContent("Err.IP-0043", args);
			assertThat(str, is("Invalid asset code for Arugment2"));
		}
		{
			String[] args =
			{
				"Arugment1", "Arugment2"
			};
			String str = cfi.getDynamicContent("Err.IP-0044", args);
			assertThat(str, is("Invalid asset code for Arugment1 and Arugment1"));
		}
	}

	@Test
	public void testResolveContactDetails_whenTrusteeOrServiceOps_thenProductionSupportTeam()
	{
		final String placeHolderString = "RCD - [CONTACT_NUMBER_PLACEHOLDER]";
		final String passValue = "RCD - Production Support team";

		Roles roles = Roles.ROLE_TRUSTEE;
		String contactDetails = cfi.resolveContactDetails(placeHolderString, roles);
		assertThat(contactDetails, is(passValue));

		roles = Roles.ROLE_SERVICE_OP;
		contactDetails = cfi.resolveContactDetails(placeHolderString, roles);
		assertThat(contactDetails, is(passValue));
	}

    @Test
    public void testGetErrorCodeEntrySet_whenHasValidationFile_thenValidationsLoaded() {
        CmsServiceXmlResourceImpl cfi = new CmsServiceXmlResourceImpl(
                Arrays.asList(new DefaultResourceLoader().getResource("classpath:cms-test-validations.xml")));

        assertThat(cfi.getContent("err00017"), is("Please enter or select an account"));
    }

}
