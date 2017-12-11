package com.bt.nextgen.api.statements.service.decorator;

import com.bt.nextgen.api.statements.decorator.RoleDecorator;
import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.service.cmis.CmisDocumentImpl;
import com.bt.nextgen.service.cmis.constants.VisibilityRoles;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by L062605 on 19/08/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class RoleDecoratorTest {

    RoleDecorator roleDecorator;


    @Test
    public void decoratorTest(){

        CmisDocumentImpl cmisDocument = new CmisDocumentImpl();
        cmisDocument.setUploadedRole(VisibilityRoles.ADVISER.name());
        roleDecorator = new RoleDecorator(new DocumentDto(), cmisDocument);
        DocumentDto dto = roleDecorator.decorate();
        Assert.assertEquals(VisibilityRoles.ADVISER.getDescription(), dto.getUploadedRole());
        
        cmisDocument.setUploadedRole(null);
        roleDecorator = new RoleDecorator(new DocumentDto(), cmisDocument);
         dto = roleDecorator.decorate();
        Assert.assertEquals(VisibilityRoles.PANORAMA.getDescription(), dto.getUploadedRole());



    }
}
