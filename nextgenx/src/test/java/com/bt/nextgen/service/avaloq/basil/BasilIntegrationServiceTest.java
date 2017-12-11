package com.bt.nextgen.service.avaloq.basil;

import com.bt.nextgen.clients.util.JaxbUtil;
import com.btfin.panorama.core.security.avaloq.AvaloqBankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.security.saml.SamlTokenInterface;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.avaloq.UserCacheService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagereply.v1_0.SearchImagesResponseMsgType;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagerequest.v1_0.SearchImagesRequestMsgType;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BasilIntegrationServiceTest {

    @InjectMocks
    private BasilIntegrationServiceImpl basilIntegrationService;

    @Mock
    private WebServiceProvider webServiceProvider;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private AvaloqBankingAuthorityService userSamlService;

    @Mock
    public UserCacheService userCacheService;

    @Test
    public void testLoadPolicyDocuments() {
        SearchImagesResponseMsgType imageResponse = JaxbUtil.unmarshall("/webservices/response/SearchImagesResponseMessage_UT.xml",
                SearchImagesResponseMsgType.class);

        List<String> policies = new ArrayList<>();
        policies.add("C0159786");
        Set<String> portfolioIds = new HashSet<>();
        portfolioIds.add("B0159786A");

        Mockito.when(webServiceProvider.sendWebServiceWithSecurityHeader(Mockito.any(SamlTokenInterface.class),
                Matchers.anyString(),
                Mockito.any(SearchImagesRequestMsgType.class))).thenReturn(imageResponse);
        Mockito.when(userProfileService.getGcmId()).thenReturn("201602207");
        Mockito.when(userSamlService.getSamlToken()).thenReturn(new SamlToken(""));

        List<ImageDetails> imageDetails = basilIntegrationService.getInsuranceDocuments(policies, portfolioIds, new ServiceErrorsImpl());
        Assert.assertNotNull(imageDetails);

        ImageDetails imageDetail = imageDetails.get(0);
        Assert.assertEquals("3899803", imageDetail.getDocumentId());
        Assert.assertEquals("https://sybil.btfin.com:49011/basilservice/getdocument?id=3899803", imageDetail.getDocumentURL());

        Assert.assertNotNull(imageDetail.getDocumentPropertiesList());
        Assert.assertTrue(imageDetail.getDocumentPropertiesList().size()==3);
        DocumentProperties documentProperty1 = imageDetail.getDocumentPropertiesList().get(0);
        Assert.assertEquals(DocumentProperty.DOCUMENTTYPE, documentProperty1.getDocumentPropertyName());
        Assert.assertEquals("DISHA", documentProperty1.getDocumentPropertyStringValue());

        DocumentProperties documentProperty2 = imageDetail.getDocumentPropertiesList().get(1);
        Assert.assertEquals(DocumentProperty.SPOLICYID, documentProperty2.getDocumentPropertyName());
        Assert.assertEquals("10770048", documentProperty2.getDocumentPropertyStringValue());

        DocumentProperties documentProperty3 = imageDetail.getDocumentPropertiesList().get(2);
        Assert.assertEquals(DocumentProperty.EFFECTIVEDATE, documentProperty3.getDocumentPropertyName());
        Assert.assertEquals(new DateTime("2013-10-19"), documentProperty3.getDocumentPropertyDateValue());
    }

    @Test
    public void testGetCacheProfileKey() {
        final String profileId = "profile_123";
        when(userCacheService.getActiveProfileCacheKey()).thenReturn(profileId);
        assertThat(basilIntegrationService.getActiveProfileCacheKey(), equalTo(profileId));
    }

    @Test
    public void testgetSortedList() {
        List<String> inputList = new ArrayList<>(Arrays.asList("xyz", "abc", "pqr"));
        basilIntegrationService.getSortedList(inputList);
        Assert.assertEquals(inputList.get(0),"abc");
        Assert.assertEquals(inputList.get(1),"pqr");
        Assert.assertEquals(inputList.get(2),"xyz");

    }
}
