package com.bt.nextgen.api.policy.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.policy.model.PolicyDocumentDto;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.basil.*;
import com.bt.nextgen.service.avaloq.insurance.model.Policy;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyImpl;
import com.bt.nextgen.service.avaloq.insurance.service.PolicyIntegrationService;
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
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class PolicyDocumentDtoServiceImplTest {

    @InjectMocks
    private PolicyDocumentDtoServiceImpl policyDocumentDtoService;

    @Mock
    private BasilIntegrationService basilIntegrationService;

    @Mock
    private PolicyIntegrationService policyIntegrationService;

    @Test
    public void testSearch() {

        List<ImageDetails> imageDetailsList = new ArrayList<>();
        List<DocumentProperties> documentProperties = new ArrayList<>();
        DocumentPropertiesImpl documentProperties1 = new DocumentPropertiesImpl();
        DocumentPropertiesImpl documentProperties2 = new DocumentPropertiesImpl();
        DocumentPropertiesImpl documentProperties3 = new DocumentPropertiesImpl();

        ImageDetailsImpl imageDetails1 = new ImageDetailsImpl();
        ImageDetailsImpl imageDetails2 = new ImageDetailsImpl();

        imageDetails1.setDocumentId("3899803");
        imageDetails1.setDocumentURL("https://sybil.btfin.com:49011/basilservice/getdocument?id=3899803");
        imageDetails1.setDocumentEntryDate(new DateTime("2013-09-13"));

        documentProperties1.setDocumentPropertyName(DocumentProperty.DOCUMENTTYPE);
        documentProperties1.setDocumentPropertyStringValue("DISHH");
        documentProperties2.setDocumentPropertyName(DocumentProperty.SPOLICYID);
        documentProperties2.setDocumentPropertyStringValue("10770048");
        documentProperties3.setDocumentPropertyName(DocumentProperty.EFFECTIVEDATE);
        documentProperties3.setDocumentPropertyDateValue(new DateTime("2013-10-19"));

        documentProperties.add(documentProperties1);
        documentProperties.add(documentProperties2);
        documentProperties.add(documentProperties3);

        imageDetails1.setDocumentPropertiesList(documentProperties);

        imageDetails2.setDocumentId("38998034");
        imageDetails2.setDocumentURL("https://sybil.btfin.com:49011/basilservice/getdocument?id=3899804");
        imageDetails2.setDocumentEntryDate(new DateTime("2013-09-14"));

        documentProperties = new ArrayList<>();
        documentProperties1 = new DocumentPropertiesImpl();
        documentProperties2 = new DocumentPropertiesImpl();
        documentProperties3 = new DocumentPropertiesImpl();
        documentProperties1.setDocumentPropertyName(DocumentProperty.DOCUMENTTYPE);
        documentProperties1.setDocumentPropertyStringValue("APP");
        documentProperties2.setDocumentPropertyName(DocumentProperty.SPORTFOLIONUMBER);
        documentProperties2.setDocumentPropertyStringValue("B0159786A");
        documentProperties3.setDocumentPropertyName(DocumentProperty.EFFECTIVEDATE);
        documentProperties3.setDocumentPropertyDateValue(new DateTime("2013-10-20"));

        documentProperties.add(documentProperties1);
        documentProperties.add(documentProperties2);
        documentProperties.add(documentProperties3);

        imageDetails2.setDocumentPropertiesList(documentProperties);

        imageDetailsList.add(imageDetails1);
        imageDetailsList.add(imageDetails2);

        List<Policy> policies = new ArrayList<>();
        PolicyImpl policyImpl = new PolicyImpl();
        policyImpl.setPolicyNumber("10770048");
        policyImpl.setPortfolioNumber("B0159786A");
        policies.add(policyImpl);

        Mockito.when(basilIntegrationService.getInsuranceDocuments(Matchers.anyList(), Matchers.anySet(), (ServiceErrors) Matchers.anyObject()))
                .thenReturn(imageDetailsList);
        Mockito.when(policyIntegrationService.retrievePoliciesByAccountNumber(Matchers.anyString(), (ServiceErrors) Matchers.anyObject()))
                .thenReturn(policies);

        List<PolicyDocumentDto> policyDocumentDtos = policyDocumentDtoService.search(new AccountKey("7E3C27BF5455E73B0812128BB67B940633168E37702DCB02")
                , new ServiceErrorsImpl());
        Assert.assertNotNull(policyDocumentDtos);
        Assert.assertTrue(policyDocumentDtos.size()==2);

        PolicyDocumentDto policyDocumentDto1 = policyDocumentDtos.get(0);
        Assert.assertEquals("Dishonour Letter", policyDocumentDto1.getDocumentType());
        Assert.assertEquals("2C1B269AD8AB5CC3", policyDocumentDto1.getDocumentId());
        Assert.assertEquals(new DateTime("2013-10-19").toString(), policyDocumentDto1.getEffectiveDate());
        Assert.assertEquals("10770048", policyDocumentDto1.getPolicyOrPortfolioId());

        PolicyDocumentDto policyDocumentDto2 = policyDocumentDtos.get(1);
        Assert.assertEquals("Welcome Pack", policyDocumentDto2.getDocumentType());
        Assert.assertEquals("EBAAB0CE3F3938D8ABF258D1F37CB61F", policyDocumentDto2.getDocumentId());
        Assert.assertEquals(new DateTime("2013-10-20").toString(), policyDocumentDto2.getEffectiveDate());
        Assert.assertEquals("B0159786-A", policyDocumentDto2.getPolicyOrPortfolioId());
    }
}
