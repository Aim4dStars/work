package com.bt.nextgen.api.statements.service;

import com.bt.nextgen.api.statements.model.StatementDto;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.exception.NotAllowedException;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.bt.nextgen.service.cmis.FinancialDocumentImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocument;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentIntegrationService;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentKey;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentType;
import com.bt.nextgen.service.integration.financialdocument.FinancialSupplementaryDocumentType;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bt.nextgen.service.avaloq.userinformation.UserExperience.ADVISED;

@RunWith(MockitoJUnitRunner.class)
public class StatementDtoServiceTest {

    @InjectMocks
    private StatementDtoServiceImpl statementDtoService;

    @Mock
    private BrokerIntegrationService brokerService;

    @Mock
    private BrokerHelperService brokerHelperService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private FinancialDocumentIntegrationService documentService;

    @Mock
    private InvestmentPolicyStatementIntegrationService invPolicyService;

    @Mock
    CmsService cmsService;

    Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
    Collection<FinancialDocument> documents = new ArrayList<>();
    private ProductImpl product;
    private Collection<Broker> brokers;
    private ServiceErrors serviceErrors;

    @Before
    public void setup() throws Exception {
        WrapAccountImpl account = new WrapAccountImpl();
        account.setAccountName("Wrap Account Name 1");
        account.setAccountKey(AccountKey.valueOf("36846"));
        account.setAdviserPersonId(ClientKey.valueOf("121251"));
        account.setProductKey(ProductKey.valueOf("productId"));

        accountMap.put(account.getAccountKey(), account);

        Mockito.when(accountIntegrationService.loadWrapAccount(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class))).thenReturn(account);

        product = new ProductImpl();
        product.setProductKey(ProductKey.valueOf("productId"));
        product.setProductName("Invesment 1");

        Mockito.when(productIntegrationService.getProductDetail(Mockito.any(ProductKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(product);

        brokers = new ArrayList<Broker>();
        BrokerUser broker = Mockito.mock(BrokerUser.class);
        Mockito.when(broker.isRegisteredOnline()).thenReturn(false);
        Mockito.when(broker.getFirstName()).thenReturn("Stephen");
        Mockito.when(broker.getLastName()).thenReturn("Dorai");
        Mockito.when(broker.getAge()).thenReturn(0);
        Mockito.when(broker.isRegistrationOnline()).thenReturn(false);

        Mockito.when(userProfileService.getUserId()).thenReturn("201601682");
        Mockito.when(brokerService.getBrokerUser(Mockito.any(UserKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(broker);

        Mockito.when(brokerHelperService.getUserExperience(Mockito.any(WrapAccount.class), Mockito.any(ServiceErrors.class))).thenReturn(ADVISED);

        FinancialDocumentImpl financialDocument = new FinancialDocumentImpl();
        financialDocument.setAccountKey(AccountKey.valueOf("36846"));
        financialDocument.setDocumentKey(FinancialDocumentKey.valueOf("36846"));
        financialDocument.setDocumentType(FinancialDocumentType.ANNUAL_INVESTMENT_STATEMENT);
        financialDocument.setPeriodStartDate(new DateTime());
        financialDocument.setPeriodEndDate(new DateTime());
        financialDocument.setSize(new BigInteger("1024"));

        documents.add(financialDocument);
    }

    @Test
    public void testSearch_whenSearchExecuted_matchingDocumentsReturned() {
        FinancialDocumentImpl financialDocument = new FinancialDocumentImpl();

        financialDocument.setDocumentKey(FinancialDocumentKey.valueOf("asdfkj"));
        financialDocument.setAccountKey(AccountKey.valueOf("36846"));
        financialDocument.setDocumentType(FinancialDocumentType.FEE_REVENUE_STATEMENT);
        financialDocument.setPeriodStartDate(new DateTime());
        financialDocument.setPeriodEndDate(new DateTime());
        financialDocument.setSize(new BigInteger("1024"));
        List<FinancialDocument> financialDocumentList = new ArrayList<>();
        financialDocumentList.add(financialDocument);

        List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        ApiSearchCriteria criteria = new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS,
                "975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0", OperationType.STRING);
        criteriaList.add(criteria);

        criteria = new ApiSearchCriteria(Attribute.END_DATE, SearchOperation.EQUALS, "2014-12-01", OperationType.STRING);
        criteriaList.add(criteria);

        criteria = new ApiSearchCriteria(Attribute.END_DATE, SearchOperation.EQUALS, "2014-12-01", OperationType.STRING);
        criteriaList.add(criteria);

        criteria = new ApiSearchCriteria(Attribute.DOCUMENT_TYPES, SearchOperation.EQUALS, "PYGSTM,STMANN", OperationType.STRING);
        criteriaList.add(criteria);

        Mockito.when(
                documentService.loadDocuments(Mockito.any(AccountKey.class), Mockito.any(Collection.class),
                        Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(String.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(financialDocumentList);

        List<StatementDto> statements = statementDtoService.search(criteriaList, serviceErrors);

        Assert.assertNotNull(statements);
        Assert.assertEquals(1, statements.size());
    }

    @Test
    public void testToStatementMaturitiesDto_sizeMatches() {
        List<StatementDto> statementDtos = statementDtoService.toStatementDto(documents, serviceErrors);
        Assert.assertEquals(1, statementDtos.size());
        StatementDto statementDto = statementDtos.get(0);

        Assert.assertEquals("36846", EncodedString.toPlainText(statementDto.getAccountKey().getAccountId()));
        Assert.assertEquals("Wrap Account Name 1", statementDto.getAccountName());
        Assert.assertEquals("121251", statementDto.getAdviserId());
        Assert.assertEquals("productId", statementDto.getProductId());
        Assert.assertEquals("Invesment 1", statementDto.getProductName());
        Assert.assertEquals(1024, statementDto.getSize().intValue());
        Assert.assertEquals(FinancialDocumentType.ANNUAL_INVESTMENT_STATEMENT.getDescription(), statementDto.getStatementType());
        Assert.assertEquals(FinancialSupplementaryDocumentType.ANNUAL_AUDIT_REPORT.getDescription(), statementDto
                .getSupplimentaryDocuments().get(0).getName());
        Assert.assertEquals("/content/dam/secure/pdfs/financial/Audit_Report_2017-2017.pdf", statementDto.getSupplimentaryDocuments().get(0).getUrl());

    }

    @Test
    public void testFeeRevenueStatement() {
        List<FinancialDocument> documents = new ArrayList<>();

        FinancialDocumentImpl financialDocument = new FinancialDocumentImpl();
        financialDocument.setDocumentKey(FinancialDocumentKey.valueOf("36846"));
        financialDocument.setDocumentType(FinancialDocumentType.FEE_REVENUE_STATEMENT);
        financialDocument.setPeriodStartDate(new DateTime());
        financialDocument.setPeriodEndDate(new DateTime());
        financialDocument.setSize(new BigInteger("1024"));

        documents.add(financialDocument);

        List<StatementDto> statementDtos = statementDtoService.toStatementDtoForFeeRevenue(documents);
        Assert.assertEquals(1, statementDtos.size());
        StatementDto statementDto = statementDtos.get(0);

        Assert.assertNotNull(statementDto.getStatementKey());
        Assert.assertEquals(1024, statementDto.getSize().intValue());
        Assert.assertEquals(FinancialDocumentType.FEE_REVENUE_STATEMENT.getDescription(), statementDto.getStatementType());
    }

    @Test
    public void testSearch() {
        Mockito.when(
                documentService.loadDocuments(Mockito.any(AccountKey.class),
                        Mockito.anyCollectionOf(FinancialDocumentType.class), Mockito.any(DateTime.class),
                        Mockito.any(DateTime.class), Mockito.any(String.class), Mockito.any(ServiceErrors.class))).thenReturn(
                documents);

        ApiSearchCriteria accountIdCri = new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, EncodedString
                .fromPlainText("36846").toString(), OperationType.STRING);

        ApiSearchCriteria startDate = new ApiSearchCriteria(Attribute.START_DATE, SearchOperation.EQUALS,
                new DateTime().toString(), OperationType.DATE);
        ApiSearchCriteria endDate = new ApiSearchCriteria(Attribute.END_DATE, SearchOperation.EQUALS, new DateTime().toString(),
                OperationType.DATE);

        ApiSearchCriteria documentTypes = new ApiSearchCriteria(Attribute.DOCUMENT_TYPES, SearchOperation.EQUALS,
                "STMANN,STMTAX,PYGSTM,QTRSTM", OperationType.STRING);

        List<ApiSearchCriteria> criteriaList = new ArrayList<ApiSearchCriteria>();

        criteriaList.add(accountIdCri);
        criteriaList.add(startDate);
        criteriaList.add(endDate);
        criteriaList.add(documentTypes);

        List<StatementDto> statementDtos = statementDtoService.search(criteriaList, serviceErrors);
        Assert.assertEquals(1, statementDtos.size());
        StatementDto statementDto = statementDtos.get(0);

        Assert.assertEquals("36846", EncodedString.toPlainText(statementDto.getAccountKey().getAccountId()));
        Assert.assertEquals("Wrap Account Name 1", statementDto.getAccountName());
        Assert.assertEquals("121251", statementDto.getAdviserId());
        Assert.assertEquals("productId", statementDto.getProductId());
        Assert.assertEquals("Invesment 1", statementDto.getProductName());
        Assert.assertEquals(1024, statementDto.getSize().intValue());
        Assert.assertEquals(FinancialDocumentType.ANNUAL_INVESTMENT_STATEMENT.getDescription(), statementDto.getStatementType());
        Assert.assertEquals(FinancialSupplementaryDocumentType.ANNUAL_AUDIT_REPORT.getDescription(), statementDto
                .getSupplimentaryDocuments().get(0).getName());
        Assert.assertEquals("/content/dam/secure/pdfs/financial/Audit_Report_2017-2017.pdf", statementDto.getSupplimentaryDocuments().get(0).getUrl());

    }

    @Test
    public void test_getFeeRevenueStatements_forAdviser() {
        // user profile service -- adviser, DG, IM
        Mockito.when(userProfileService.isAdviser()).thenReturn(Boolean.TRUE);
        Broker broker = Mockito.mock(Broker.class);
        Mockito.when(broker.getBankReferenceKey()).thenReturn(UserKey.valueOf("userId"));
        Mockito.when(brokerService.getBroker(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class))).thenReturn(broker);

        DateTime startDate = DateTime.now();
        DateTime endDate = DateTime.now();
        Mockito.when(
                documentService.loadDocuments(UserKey.valueOf("userId"),
                        Collections.singletonList(FinancialDocumentType.FEE_REVENUE_STATEMENT), startDate, endDate,
                        Constants.RELATIONSHIP_TYPE_ADVISER, serviceErrors)).thenReturn(documents);
        List<StatementDto> results = statementDtoService.getFeeRevenueStatements(startDate, endDate,
                Collections.singletonList(FinancialDocumentType.FEE_REVENUE_STATEMENT), serviceErrors);
        Assert.assertEquals(1, documents.size());
    }

    @Test
    public void test_getFeeRevenueStatements_forInvestmentManagerOrDealerGroup() {
        // user profile service -- adviser, DG, IM
        List<String> relationshipTypes = new ArrayList<>();
        relationshipTypes.add(Constants.RELATIONSHIP_TYPE_DG);
        relationshipTypes.add(Constants.RELATIONSHIP_TYPE_INV_MGR);

        Mockito.when(userProfileService.isDealerGroup()).thenReturn(Boolean.FALSE);
        Mockito.when(userProfileService.isInvestmentManager()).thenReturn(Boolean.TRUE);

        BrokerKey imKey = BrokerKey.valueOf("imId");
        Broker imBroker = Mockito.mock(Broker.class);
        Mockito.when(imBroker.getKey()).thenReturn(imKey);
        BrokerKey dealerKey = BrokerKey.valueOf("dealerId");
        Broker dealerBroker = Mockito.mock(Broker.class);
        Mockito.when(dealerBroker.getKey()).thenReturn(dealerKey);

        // Investment Manager
        Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(imBroker);
        DateTime startDate = DateTime.now();
        DateTime endDate = DateTime.now();
        Collection<FinancialDocument> emptyList = new ArrayList<>();
        Mockito.when(
                documentService.loadDocumentsForBrokers(Collections.singletonList(imKey),
                        Collections.singletonList(FinancialDocumentType.FEE_REVENUE_STATEMENT), startDate, endDate,
                        relationshipTypes, serviceErrors)).thenReturn(emptyList);

        // Dealer Group
        Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(dealerBroker);

        List<BrokerKey> brokerKeyList = new ArrayList<>();
        brokerKeyList.add(dealerKey);
        brokerKeyList.add(imKey);
        Mockito.when(
                invPolicyService.getInvestmentManagerFromModel(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(Collections.singletonList(imKey));
        Mockito.when(
                documentService.loadDocumentsForBrokers(brokerKeyList,
                        Collections.singletonList(FinancialDocumentType.FEE_REVENUE_STATEMENT), startDate, endDate,
                        relationshipTypes, serviceErrors)).thenReturn(documents);

        List<StatementDto> results = statementDtoService.getFeeRevenueStatements(startDate, endDate,
                Collections.singletonList(FinancialDocumentType.FEE_REVENUE_STATEMENT), serviceErrors);
        Assert.assertEquals(0, results.size());

        Mockito.when(userProfileService.isDealerGroup()).thenReturn(Boolean.TRUE);
        Mockito.when(userProfileService.isInvestmentManager()).thenReturn(Boolean.FALSE);
        results = statementDtoService.getFeeRevenueStatements(startDate, endDate,
                Collections.singletonList(FinancialDocumentType.FEE_REVENUE_STATEMENT), serviceErrors);
        Assert.assertEquals(1, results.size());
    }

    @Test
    public void test_getFeeRevenueStatements_forPortfolioManager() {
        List<String> relationshipTypes = new ArrayList<>();
        relationshipTypes.add(Constants.RELATIONSHIP_TYPE_INV_MGR);

        // user profile service -- adviser, DG, IM
        Mockito.when(userProfileService.isAdviser()).thenReturn(Boolean.FALSE);
        Mockito.when(userProfileService.isInvestmentManager()).thenReturn(Boolean.FALSE);
        Mockito.when(userProfileService.isPortfolioManager()).thenReturn(Boolean.TRUE);

        Broker broker = Mockito.mock(Broker.class);
        Mockito.when(broker.getBankReferenceKey()).thenReturn(UserKey.valueOf("userId"));
        Mockito.when(brokerService.getBroker(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class))).thenReturn(broker);

        BrokerKey imKey = BrokerKey.valueOf("imId");
        Broker imBroker = Mockito.mock(Broker.class);
        Mockito.when(imBroker.getKey()).thenReturn(imKey);
        Mockito.when(userProfileService.getInvestmentManager(Mockito.any(ServiceErrors.class))).thenReturn(imBroker);
        DateTime startDate = DateTime.now();
        DateTime endDate = DateTime.now();

        Mockito.when(
                documentService.loadDocumentsForBrokers(Collections.singletonList(imKey),
                        Collections.singletonList(FinancialDocumentType.FEE_REVENUE_STATEMENT), startDate, endDate,
                        relationshipTypes, serviceErrors)).thenReturn(documents);

        List<StatementDto> results = statementDtoService.getFeeRevenueStatements(startDate, endDate,
                Collections.singletonList(FinancialDocumentType.FEE_REVENUE_STATEMENT), serviceErrors);
        Assert.assertEquals(1, results.size());
    }

    @Test(expected = NotAllowedException.class)
    public void test_getFeeRevenueStatements_forInvalidBroker() {
        Mockito.when(userProfileService.isAdviser()).thenReturn(Boolean.FALSE);
        Mockito.when(userProfileService.isDealerGroup()).thenReturn(Boolean.FALSE);
        Mockito.when(userProfileService.isInvestmentManager()).thenReturn(Boolean.FALSE);
        statementDtoService.getFeeRevenueStatements(DateTime.now(), DateTime.now(),
                Collections.singletonList(FinancialDocumentType.FEE_REVENUE_STATEMENT), serviceErrors);
    }
}
