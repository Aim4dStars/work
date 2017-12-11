package com.bt.nextgen.core.security.api.service;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.basil.BasilIntegrationService;
import com.bt.nextgen.service.avaloq.basil.DocumentProperties;
import com.bt.nextgen.service.avaloq.basil.DocumentPropertiesImpl;
import com.bt.nextgen.service.avaloq.basil.DocumentProperty;
import com.bt.nextgen.service.avaloq.basil.ImageDetails;
import com.bt.nextgen.service.avaloq.basil.ImageDetailsImpl;
import com.bt.nextgen.service.avaloq.insurance.model.Policy;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyImpl;
import com.bt.nextgen.service.avaloq.insurance.service.PolicyIntegrationService;

@RunWith(MockitoJUnitRunner.class)
public class BasilPermissionServiceTest {

    @InjectMocks
    private BasilPermissionServiceImpl service;

    @Mock
    private BasilIntegrationService basilIntegrationService;

    @Mock
    private PolicyIntegrationService policyIntegrationService;

    @Test
    public void testHasBasilDocumentAccessSuccess() throws Exception {
        List<Policy> policies = new ArrayList<>();
        PolicyImpl policyImpl = new PolicyImpl();
        policyImpl.setPolicyNumber("10770048");
        policyImpl.setPortfolioNumber("B0159786A");
        policies.add(policyImpl);

        Mockito.when(basilIntegrationService.getInsuranceDocuments(org.mockito.Matchers.anyList(), org.mockito.Matchers.anySet(), (ServiceErrors) org.mockito.Matchers.anyObject()))
            .thenReturn(getImageDetailsList());
        Mockito.when(policyIntegrationService.retrievePoliciesByAccountNumber(org.mockito.Matchers.anyString(), (ServiceErrors) org.mockito.Matchers.anyObject()))
            .thenReturn(policies);

        boolean hasAccess = service.hasBasilDocumentAccess("7E3C27BF5455E73B0812128BB67B940633168E37702DCB02", "2C1B269AD8AB5CC3");
        Assert.assertEquals(true, hasAccess);
    }

    @Test
    public void testHasBasilDocumentAccessFailure_AccessDenied() throws Exception {
        List<Policy> policies = new ArrayList<>();
        PolicyImpl policyImpl = new PolicyImpl();
        policyImpl.setPolicyNumber("10770048");
        policyImpl.setPortfolioNumber("B0159786A");
        policies.add(policyImpl);

        Mockito.when(basilIntegrationService.getInsuranceDocuments(org.mockito.Matchers.anyList(), org.mockito.Matchers.anySet(), (ServiceErrors) org.mockito.Matchers.anyObject()))
            .thenReturn(getImageDetailsList());
        Mockito.when(policyIntegrationService.retrievePoliciesByAccountNumber(org.mockito.Matchers.anyString(), (ServiceErrors) org.mockito.Matchers.anyObject()))
            .thenReturn(policies);

        boolean hasAccess = service.hasBasilDocumentAccess("7E3C27BF5455E73B0812128BB67B940633168E37702DCB02", "4E3F7BB898804C83");
        Assert.assertEquals(false, hasAccess);
    }

    private List<ImageDetails> getImageDetailsList() {
        List<ImageDetails> imageDetailsList = new ArrayList<>();
        List<DocumentProperties> documentProperties = new ArrayList<>();
        DocumentPropertiesImpl documentProperties1 = new DocumentPropertiesImpl();
        DocumentPropertiesImpl documentProperties2 = new DocumentPropertiesImpl();
        DocumentPropertiesImpl documentProperties3 = new DocumentPropertiesImpl();

        ImageDetailsImpl imageDetails1 = new ImageDetailsImpl();
        ImageDetailsImpl imageDetails2 = new ImageDetailsImpl();

        imageDetails1.setDocumentId("3899803"); //2C1B269AD8AB5CC3
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

        imageDetails2.setDocumentId("38998034"); //EBAAB0CE3F3938D8ABF258D1F37CB61F
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
        return imageDetailsList;
    }
}