package com.bt.nextgen.document.web.service;

import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentData;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentIntegrationService;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentKey;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class DocumentServiceTest {

    @InjectMocks
    private DocumentServiceImpl documentService;

    @Mock
    private FinancialDocumentIntegrationService documentIntegrationService;

    @Mock
    private UserProfileService userProfileService;

    @Test
    public void testLoadDocument_whenNonFrsDocument_thenCorrectParameters() {
        Mockito.when(
                documentIntegrationService.loadDocument(Mockito.any(FinancialDocumentKey.class), Mockito.anyString(),
                        Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<FinancialDocumentData>() {
            @Override
            public FinancialDocumentData answer(InvocationOnMock invocation) throws Throwable {
                FinancialDocumentKey key = (FinancialDocumentKey) invocation.getArguments()[0];
                String relationshipType = (String) invocation.getArguments()[1];

                Assert.assertEquals("statementId", key.getId());
                Assert.assertEquals(Constants.RELATIONSHIP_TYPE_ACCOUNT, relationshipType);

                return Mockito.mock(FinancialDocumentData.class);
            }
        });

        documentService.loadDocument("statementId", FinancialDocumentType.ANNUAL_TAX_STATEMENT);
    }

    @Test
    public void testLoadDocument_whenFrsDocumentForAdviserRole_thenCorrectParameters() {
        UserProfile activeProfile = Mockito.mock(UserProfile.class);
        Mockito.when(activeProfile.getJobRole()).thenReturn(JobRole.ADVISER);
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(activeProfile);

        Mockito.when(
                documentIntegrationService.loadDocument(Mockito.any(FinancialDocumentKey.class), Mockito.anyString(),
                        Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<FinancialDocumentData>() {
            @Override
            public FinancialDocumentData answer(InvocationOnMock invocation) throws Throwable {
                FinancialDocumentKey key = (FinancialDocumentKey) invocation.getArguments()[0];
                String relationshipType = (String) invocation.getArguments()[1];

                Assert.assertEquals("statementId", key.getId());
                Assert.assertEquals(Constants.RELATIONSHIP_TYPE_ADVISER, relationshipType);

                return Mockito.mock(FinancialDocumentData.class);
            }
        });

        documentService.loadDocument("statementId", FinancialDocumentType.FEE_REVENUE_STATEMENT);
    }

    @Test
    public void testLoadDocument_whenFrsDocumentForPortfolioManagerRole_thenCorrectParameters() {
        UserProfile activeProfile = Mockito.mock(UserProfile.class);
        Mockito.when(activeProfile.getJobRole()).thenReturn(JobRole.PORTFOLIO_MANAGER);
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        Mockito.when(userProfileService.isPortfolioManager()).thenReturn(Boolean.TRUE);
        Mockito.when(userProfileService.isInvestmentManager()).thenReturn(Boolean.FALSE);

        Mockito.when(
                documentIntegrationService.loadDocument(Mockito.any(FinancialDocumentKey.class), Mockito.anyString(),
                        Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<FinancialDocumentData>() {
            @Override
            public FinancialDocumentData answer(InvocationOnMock invocation) throws Throwable {
                FinancialDocumentKey key = (FinancialDocumentKey) invocation.getArguments()[0];
                String relationshipType = (String) invocation.getArguments()[1];

                Assert.assertEquals("statementId", key.getId());
                Assert.assertEquals(Constants.RELATIONSHIP_TYPE_INV_MGR, relationshipType);

                return Mockito.mock(FinancialDocumentData.class);
            }
        });

        documentService.loadDocument("statementId", FinancialDocumentType.FEE_REVENUE_STATEMENT);
    }

    @Test
    public void testLoadDocument_whenFrsDocumentForInvestmentManager_thenCorrectParameters() {
        UserProfile activeProfile = Mockito.mock(UserProfile.class);
        Mockito.when(activeProfile.getJobRole()).thenReturn(JobRole.DEALER_GROUP_MANAGER);
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        Mockito.when(userProfileService.isPortfolioManager()).thenReturn(Boolean.FALSE);
        Mockito.when(userProfileService.isInvestmentManager()).thenReturn(Boolean.TRUE);

        Mockito.when(
                documentIntegrationService.loadDocument(Mockito.any(FinancialDocumentKey.class), Mockito.anyString(),
                        Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<FinancialDocumentData>() {
            @Override
            public FinancialDocumentData answer(InvocationOnMock invocation) throws Throwable {
                FinancialDocumentKey key = (FinancialDocumentKey) invocation.getArguments()[0];
                String relationshipType = (String) invocation.getArguments()[1];

                Assert.assertEquals("statementId", key.getId());
                Assert.assertEquals(Constants.RELATIONSHIP_TYPE_INV_MGR, relationshipType);

                return Mockito.mock(FinancialDocumentData.class);
            }
        });

        documentService.loadDocument("statementId", FinancialDocumentType.FEE_REVENUE_STATEMENT);
    }

    @Test
    public void testLoadDocument_whenFrsDocumentForOtherRole_thenCorrectParameters() {
        UserProfile activeProfile = Mockito.mock(UserProfile.class);
        Mockito.when(activeProfile.getJobRole()).thenReturn(JobRole.DEALER_GROUP_MANAGER);
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        Mockito.when(userProfileService.isPortfolioManager()).thenReturn(Boolean.FALSE);
        Mockito.when(userProfileService.isInvestmentManager()).thenReturn(Boolean.FALSE);

        Mockito.when(
                documentIntegrationService.loadDocument(Mockito.any(FinancialDocumentKey.class), Mockito.anyString(),
                        Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<FinancialDocumentData>() {
            @Override
            public FinancialDocumentData answer(InvocationOnMock invocation) throws Throwable {
                FinancialDocumentKey key = (FinancialDocumentKey) invocation.getArguments()[0];
                String relationshipType = (String) invocation.getArguments()[1];

                Assert.assertEquals("statementId", key.getId());
                Assert.assertEquals(Constants.RELATIONSHIP_TYPE_DG, relationshipType);

                return Mockito.mock(FinancialDocumentData.class);
            }
        });

        documentService.loadDocument("statementId", FinancialDocumentType.FEE_REVENUE_STATEMENT);
    }

    @Test
    public void testLoadDocument_whenRequestingIMFrsDocumentAsDGRole_thenCorrectParameters() {
        UserProfile activeProfile = Mockito.mock(UserProfile.class);
        Mockito.when(activeProfile.getJobRole()).thenReturn(JobRole.DEALER_GROUP_MANAGER);
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        Mockito.when(userProfileService.isPortfolioManager()).thenReturn(Boolean.FALSE);
        Mockito.when(userProfileService.isInvestmentManager()).thenReturn(Boolean.FALSE);

        Mockito.when(
                documentIntegrationService.loadDocument(Mockito.any(FinancialDocumentKey.class), Mockito.anyString(),
                        Mockito.any(ServiceErrors.class))).thenReturn(null, Mockito.mock(FinancialDocumentData.class));

        FinancialDocumentData document = documentService.loadDocument("statementId", FinancialDocumentType.FEE_REVENUE_STATEMENT);

        Assert.assertNotNull(document);
    }

}
