package com.bt.nextgen.api.statements.service;

import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlTokenInterface;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.cmis.CmisDocumentImpl;
import com.bt.nextgen.service.cmis.CmisDocumentIntegrationServiceImpl;
import com.bt.nextgen.service.cmis.DocumentConverter;
import com.bt.nextgen.service.integration.financialdocument.Document;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.oasis_open.docs.ns.cmis.core._200908.CmisObjectType;
import org.oasis_open.docs.ns.cmis.core._200908.CmisPropertiesType;
import org.oasis_open.docs.ns.cmis.core._200908.CmisPropertyId;
import org.oasis_open.docs.ns.cmis.messaging._200908.CreateDocumentResponse;
import org.oasis_open.docs.ns.cmis.messaging._200908.GetObjectByPathResponse;

/**
 * Created by L075208 on 22/11/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class CmisDocumentIntegrationServiceImplTest {

    @InjectMocks
    private CmisDocumentIntegrationServiceImpl cmisDocumentIntegrationService;

    @Mock
    private WebServiceProvider provider;

    @Mock
    private BankingAuthorityService userSamlService;

    @Mock
    private DocumentConverter converter;

    @Test
    public void testCreateDocument(){

        CmisDocumentImpl document = new CmisDocumentImpl();

        document.setDocumentType("STM");
        document.setDocumentName("abc");
        document.setDocumentTitleCode("SPCENT");

        CreateDocumentResponse response = new CreateDocumentResponse();
        response.setObjectId("abc");

        GetObjectByPathResponse  response1 = new GetObjectByPathResponse();
        CmisObjectType cmisObjectType = new CmisObjectType();
        CmisPropertyId cmisProperty = new CmisPropertyId();
        cmisProperty.setPropertyDefinitionId("cmis:objectId");
        cmisProperty.getValue().add("1213234");

        CmisPropertiesType cmisPropertiesType = new CmisPropertiesType();
        cmisPropertiesType.getProperty().add(cmisProperty);

        cmisObjectType.setProperties(cmisPropertiesType);
        response1.setObject(cmisObjectType);

        Mockito.when(provider.sendWebServiceWithSecurityHeader(Mockito.any(SamlTokenInterface.class), Mockito.eq("cmisGetPath"),Mockito.anyObject())).thenReturn(response1);

        Mockito.when(provider.sendWebServiceWithSecurityHeader(Mockito.any(SamlTokenInterface.class),Mockito.eq("cmisCreateObject"),Mockito.anyObject())).thenReturn(response);

        Document document1 = cmisDocumentIntegrationService.createNewDocument(document);

        Assert.assertNotNull(document1);


        // the folder path needs to be differrent for statements other than ""SPCENT"
        document.setDocumentTitleCode("IMRCTI");
        document1 = cmisDocumentIntegrationService.createNewDocument(document);
        Assert.assertNotNull(document1);

        //testing doctitlecode SPFEPK
        document.setDocumentTitleCode("SPFEPK");
        document1 = cmisDocumentIntegrationService.createNewDocument(document);
        Assert.assertNotNull(document1);

        //testing doctitlecode SPPWPK
        document.setDocumentTitleCode("SPPWPK");
        document1 = cmisDocumentIntegrationService.createNewDocument(document);
        Assert.assertNotNull(document1);

        //testing doctitlecode SPRBEN
        document.setDocumentTitleCode("SPRBEN");
        document1 = cmisDocumentIntegrationService.createNewDocument(document);
        Assert.assertNotNull(document1);

        //testing doctitlecode SPEXIT
        document.setDocumentTitleCode("SPEXIT");
        document1 = cmisDocumentIntegrationService.createNewDocument(document);
        Assert.assertNotNull(document1);

        document.setDocumentTitleCode(null);
        document1 = cmisDocumentIntegrationService.createNewDocument(document);
        Assert.assertNotNull(document1);

        document.setDocumentTitleCode("CMASTM");
        document1 = cmisDocumentIntegrationService.createNewDocument(document);
        Assert.assertNotNull(document1);
    }
}
