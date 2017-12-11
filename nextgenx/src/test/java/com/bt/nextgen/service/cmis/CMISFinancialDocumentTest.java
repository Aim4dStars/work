package com.bt.nextgen.service.cmis;

import com.bt.nextgen.clients.util.JaxbUtil;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.integration.broker.BrokerType;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocument;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentData;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentKey;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentType;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.util.SamlUtil;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.hamcrest.CoreMatchers;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.oasis_open.docs.ns.cmis.messaging._200908.GetContentStreamResponse;
import org.oasis_open.docs.ns.cmis.messaging._200908.Query;
import org.oasis_open.docs.ns.cmis.messaging._200908.QueryResponse;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CMISFinancialDocumentTest {
    private static final String ACCOUNT_ID_VALID = "validAccountKey";
    private static final String GCM_ID_VALID = "validUserKey";
    private static final String OE_ID_VALID = "validDealerGroupKey";
    private static final String ACCOUNT_ID_INVALID = "invalidAccountKey";
    private static final FinancialDocumentKey DOCUMENT_KEY = FinancialDocumentKey.valueOf("documentKey");

    private static final String ACCOUNT_NUMBER = "accountNumber";

    @InjectMocks
    private CMISFinanacialDocumentIntegrationServiceImpl documentService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private WebServiceProvider provider;

    @Mock
    private BankingAuthorityService userSamlService;

    @Mock
    private UserProfileService userProfileService;

    private Collection<FinancialDocumentType> types;

    @Before
    public void setup() throws Exception {
        setupAccountMock();
        types = new ArrayList<>();
        types.add(FinancialDocumentType.ANNUAL_INVESTMENT_STATEMENT);
    }

    private void setupAccountMock() {
        WrapAccountImpl wrapAccount = new WrapAccountImpl();
        wrapAccount.setAccountKey(AccountKey.valueOf(ACCOUNT_ID_VALID));
        wrapAccount.setAccountNumber(ACCOUNT_NUMBER);
        Map<AccountKey, WrapAccount> accounts = Collections.singletonMap(AccountKey.valueOf(ACCOUNT_ID_VALID),
                (WrapAccount) wrapAccount);

        Mockito.when(accountIntegrationService.loadWrapAccountWithoutContainers(Mockito.any(ServiceErrors.class))).thenReturn(
                accounts);
    }

    private void setupQueryResponseMock_singleInvalidResponse() {
        QueryResponse response = JaxbUtil.unmarshall("/webservices/response/CMISQuerySingleInvalidAccount_UT.xml",
                QueryResponse.class);
        Mockito.when(
                provider.sendWebServiceWithSecurityHeader(Mockito.any(SamlToken.class), Mockito.any(String.class),
                        Mockito.any(Query.class))).thenReturn(response);
    }

    private void setupQueryResponseMock_singleValidResponse() {
        Answer<Object> answer = new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object args[] = invocation.getArguments();
                if ("cmisObject".equals(args[0]) || "cmisObject".equals(args[1])) {
                    return JaxbUtil.unmarshall("/webservices/response/CMISDocument_UT.xml", GetContentStreamResponse.class);
                } else if ("cmisQuery".equals(args[0]) || "cmisQuery".equals(args[1])) {
                    return JaxbUtil.unmarshall("/webservices/response/CMISQuerySingleValidAccount_UT.xml", QueryResponse.class);
                }
                return null;
            }
        };

        Mockito.when(provider.sendWebService(Mockito.any(String.class), Mockito.any(Query.class))).thenAnswer(answer);
        Mockito.when(
                provider.sendWebServiceWithSecurityHeader(Mockito.any(SamlToken.class), Mockito.any(String.class),
                        Mockito.any(Query.class))).thenAnswer(answer);

    }

    @Test
    public void testLoadDocuments_whenSuppliedWithAccountWithoutAccess_thenNoDocumentsReturned() {
        AccountKey key = AccountKey.valueOf(ACCOUNT_ID_INVALID);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Collection<FinancialDocument> docs = documentService.loadDocuments(key, types, new DateTime(), new DateTime(),
                Constants.RELATIONSHIP_TYPE_ACCOUNT, serviceErrors);
        Assert.assertEquals(0, docs.size());
        Assert.assertFalse(serviceErrors.hasErrors());
    }

    @Test
    public void testLoadDocuments_whenSuppliedWithMixedModeAccountListWithoutAccess_thenNoDocumentsReturned() {
        List<AccountKey> accountKeys = new ArrayList<>();
        accountKeys.add(AccountKey.valueOf(ACCOUNT_ID_VALID));
        accountKeys.add(AccountKey.valueOf(ACCOUNT_ID_INVALID));
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Collection<FinancialDocument> docs = documentService.loadDocuments(accountKeys, types, new DateTime(), new DateTime(),
                Constants.RELATIONSHIP_TYPE_ACCOUNT, serviceErrors);
        Assert.assertEquals(0, docs.size());
        Assert.assertFalse(serviceErrors.hasErrors());
    }

    @Test
    public void testLoadDocuments_whenSuppliedWithAnGcmIdWithAccess_thenMatchingDocumentsReturned() {
        UserKey userKey = UserKey.valueOf(GCM_ID_VALID);

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        setupQueryResponseMock_singleValidResponse();
        Collection<FinancialDocument> docs = documentService.loadDocuments(userKey, types, new DateTime(), new DateTime(),
                Constants.RELATIONSHIP_TYPE_ADVISER, serviceErrors);
        Assert.assertTrue(docs.size() > 0);
        Assert.assertFalse(serviceErrors.hasErrors());
    }

    @Test
    public void testLoadDocuments_whenSuppliedWithAnDGIdWithAccess_thenMatchingDocumentsReturned() {
        BrokerKey brokerKey = BrokerKey.valueOf(OE_ID_VALID);
        List<String> relationshipTypes = new ArrayList<>();
        relationshipTypes.add(Constants.RELATIONSHIP_TYPE_DG);
        relationshipTypes.add(Constants.RELATIONSHIP_TYPE_INV_MGR);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        setupQueryResponseMock_singleValidResponse();
        Collection<FinancialDocument> docs = documentService.loadDocuments(brokerKey, types, new DateTime(), new DateTime(),
                relationshipTypes, serviceErrors);

        Assert.assertTrue(docs.size() > 0);
        Assert.assertFalse(serviceErrors.hasErrors());
    }

    @Test
    public void testLoadDocuments_whenSuppliedWithAnAccountWithAccess_thenMatchingDocumentsReturned() {
        AccountKey key = AccountKey.valueOf(ACCOUNT_ID_VALID);

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        setupQueryResponseMock_singleValidResponse();
        Collection<FinancialDocument> docs = documentService.loadDocuments(key, types, new DateTime(), new DateTime(),
                Constants.RELATIONSHIP_TYPE_ACCOUNT, serviceErrors);
        Assert.assertEquals(1, docs.size());
        Assert.assertFalse(serviceErrors.hasErrors());
        FinancialDocument doc = docs.iterator().next();
        Assert.assertEquals(ACCOUNT_ID_VALID, doc.getAccountKey().getId());
        Assert.assertEquals("idd_AF1B3E96-C563-4ECB-AB60-327456BD19E0", doc.getDocumentKey().getId());
        Assert.assertEquals(FinancialDocumentType.ANNUAL_INVESTMENT_STATEMENT, doc.getDocumentType());

        // Compare in epoch time to make tests working in all timezones
        Assert.assertEquals(1388494800000L, doc.getPeriodEndDate().toDate().getTime());
        Assert.assertEquals(1370005200000L, doc.getPeriodStartDate().toDate().getTime());
        Assert.assertEquals(BigInteger.valueOf(134257), doc.getSize());

    }

    @Test
    public void testLoadDocuments_whenDocumentDoNotYouHaveAccessTo_thenNoDocumentsReturned() {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        setupQueryResponseMock_singleInvalidResponse();
        FinancialDocumentData doc = documentService
                .loadDocument(DOCUMENT_KEY, Constants.RELATIONSHIP_TYPE_ACCOUNT, serviceErrors);
        Assert.assertNull(doc);
        Assert.assertFalse(serviceErrors.hasErrors());
    }

    @Ignore
    @Test
    public void testLoadDocuments_whenDocumentDoYouHaveAccessToForAdviser_thenValidDocumentReturned() {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        setupQueryResponseMock_singleValidResponse();

        Mockito.when(userProfileService.getUserId()).thenReturn(ACCOUNT_NUMBER);

        FinancialDocumentData doc = documentService
                .loadDocument(DOCUMENT_KEY, Constants.RELATIONSHIP_TYPE_ADVISER, serviceErrors);
        Assert.assertNotNull(doc);
        Assert.assertNotNull(doc.getData());
        Assert.assertEquals(DOCUMENT_KEY, doc.getDocumentKey());
        Assert.assertFalse(serviceErrors.hasErrors());
    }

    @Test
    public void testLoadDocuments_whenDocumentDoYouHaveAccessToForDealerGroup_thenValidDocumentReturned() {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        Broker broker = new BrokerImpl(BrokerKey.valueOf(ACCOUNT_NUMBER), BrokerType.DEALER);

        Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(broker);

        setupQueryResponseMock_singleValidResponse();
        FinancialDocumentData doc = documentService.loadDocument(DOCUMENT_KEY, Constants.RELATIONSHIP_TYPE_DG, serviceErrors);
        Assert.assertNotNull(doc);
        Assert.assertNotNull(doc.getData());
        Assert.assertEquals(DOCUMENT_KEY, doc.getDocumentKey());
        Assert.assertFalse(serviceErrors.hasErrors());
    }

    @Test
    public void testLoadDocuments_whenDocumentYouDoHaveAccessToForPortfolioManager_thenValidDocumentReturned() {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        setupQueryResponseMock_singleValidResponse();
        FinancialDocumentData doc = documentService
                .loadDocument(DOCUMENT_KEY, Constants.RELATIONSHIP_TYPE_INV_MGR, serviceErrors);
        Assert.assertNotNull(doc);
        Assert.assertNotNull(doc.getData());
        Assert.assertEquals(DOCUMENT_KEY, doc.getDocumentKey());
        Assert.assertFalse(serviceErrors.hasErrors());
    }

    @Test
    public void testLoadDocuments_whenServiceOpsUser_thenNoDocumentsReturned() {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        setupQueryResponseMock_singleValidResponse();
        Collection<FinancialDocument> docs = documentService.loadAllDocuments("120000286", Constants.RELATIONSHIP_TYPE_ACCOUNT,
                serviceErrors);
        Assert.assertEquals(1, docs.size());
        Assert.assertFalse(serviceErrors.hasErrors());
    }

    @Test
    public void testLoadDocument_whenServiceOpsUser() {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        setupQueryResponseMock_singleValidResponse();
        FinancialDocumentData doc = documentService.loadDocumentContent(DOCUMENT_KEY, serviceErrors);

        Assert.assertNotNull(doc);
        Assert.assertNotNull(doc.getData());
        Assert.assertEquals(DOCUMENT_KEY, doc.getDocumentKey());
        Assert.assertFalse(serviceErrors.hasErrors());
    }

    @Test
    public void testLoadIMDocument() {

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        SamlToken token = new SamlToken(SamlUtil.loadSaml());
        Mockito.when(userSamlService.getSamlToken()).thenReturn(token);
        Mockito.when(userProfileService.getUserId()).thenReturn(ACCOUNT_NUMBER);
        setupQueryResponseMock_singleValidIMModelResponse();
        DateTime endDate = new DateTime().withDate(2013, 6, 30);

        // We don't care what sort of document is returned, as long as it
        // returns 1.
        FinancialDocumentData data = documentService.loadIMDocument(FinancialDocumentType.IMMODEL.getCode(), "BTT003TAX",
                endDate, "217082760", "INVST_MGR", serviceErrors);

        Assert.assertTrue(data != null);
    }

    private void setupQueryResponseMock_singleValidIMModelResponse() {
        Answer<Object> answer = new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object args[] = invocation.getArguments();
                if ("cmisObject".equals(args[0]) || "cmisObject".equals(args[1])) {
                    return JaxbUtil.unmarshall("/webservices/response/CMISDocument_UT.xml", GetContentStreamResponse.class);
                } else if ("cmisQuery".equals(args[0]) || "cmisQuery".equals(args[1])) {
                    return JaxbUtil.unmarshall("/webservices/response/CMISQuerySingleValidIM_UT.xml", QueryResponse.class);
                }
                return null;
            }
        };

        Mockito.when(provider.sendWebService(Mockito.any(String.class), Mockito.any(Query.class))).thenAnswer(answer);
        Mockito.when(
                provider.sendWebServiceWithSecurityHeader(Mockito.any(SamlToken.class), Mockito.any(String.class),
                        Mockito.any(Query.class))).thenAnswer(answer);

    }

    @Test
    public void testLoadDocuments_whenSuppliedWithAnGcmId_RelationShipType() {
        UserKey userKey = UserKey.valueOf(GCM_ID_VALID);

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        setupQueryResponseMock_singleValidResponse();
        Collection<FinancialDocument> docs = documentService.loadDocuments(userKey, types, new DateTime(), new DateTime(),
                Constants.RELATIONSHIP_TYPE_ADVISER, serviceErrors);
        Assert.assertTrue(docs.size() > 0);
        Assert.assertFalse(serviceErrors.hasErrors());

        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        verify(provider, times(1)).sendWebServiceWithSecurityHeader(Mockito.any(SamlToken.class), Mockito.any(String.class),
                queryCaptor.capture());
        Assert.assertThat(queryCaptor.getValue().getStatement(),
                CoreMatchers.containsString("(PanoramaIPRelationshipType in ('AVSR_POS')"));
    }
}
