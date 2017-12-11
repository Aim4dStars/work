package com.bt.nextgen.api.statements.service;

import com.bt.nextgen.service.cmis.constants.DocumentCategories;
import com.bt.nextgen.service.cmis.constants.VisibilityRoles;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 *
 */
public class VisibilityRolesTest {

    @Test
    public void getSupportedClass()
    {
      List<String> adviserSupportedClasses= VisibilityRoles.ADVISER.getSupportedClass();
        Assert.assertEquals(5,adviserSupportedClasses.size());
        Assert.assertTrue(adviserSupportedClasses.contains(DocumentCategories.ADVICE.getDocumentClass()));
        Assert.assertTrue(adviserSupportedClasses.contains(DocumentCategories.SMSF.getDocumentClass()));
        Assert.assertTrue( adviserSupportedClasses.contains(DocumentCategories.TAX.getDocumentClass()));
        Assert.assertTrue(adviserSupportedClasses.contains(DocumentCategories.CORRESPONDENCE.getDocumentClass()));
        Assert.assertTrue(adviserSupportedClasses.contains(DocumentCategories.OTHER.getDocumentClass()));
        Assert.assertTrue(adviserSupportedClasses.contains(DocumentCategories.INVESTMENTS.getDocumentClass()));
        Assert.assertTrue(adviserSupportedClasses.contains(DocumentCategories.STATEMENTS.getDocumentClass()));
        Assert.assertTrue(adviserSupportedClasses.contains(DocumentCategories.TAX_SUPER.getDocumentClass()));

        List<String> accountantSupportedClasses= VisibilityRoles.ACCOUNTANT.getSupportedClass();
        Assert.assertEquals(5,accountantSupportedClasses.size());
        Assert.assertTrue(accountantSupportedClasses.contains(DocumentCategories.ADVICE.getDocumentClass()));
        Assert.assertTrue(accountantSupportedClasses.contains(DocumentCategories.SMSF.getDocumentClass()));
        Assert.assertTrue(accountantSupportedClasses.contains(DocumentCategories.TAX.getDocumentClass()));
        Assert.assertTrue(accountantSupportedClasses.contains(DocumentCategories.CORRESPONDENCE.getDocumentClass()));
        Assert.assertTrue(accountantSupportedClasses.contains(DocumentCategories.OTHER.getDocumentClass()));
        Assert.assertTrue(accountantSupportedClasses.contains(DocumentCategories.INVESTMENTS.getDocumentClass()));
        Assert.assertTrue(accountantSupportedClasses.contains(DocumentCategories.STATEMENTS.getDocumentClass()));
        Assert.assertTrue(accountantSupportedClasses.contains(DocumentCategories.TAX_SUPER.getDocumentClass()));

        List<String> investorSupportedClasses= VisibilityRoles.INVESTOR.getSupportedClass();
        Assert.assertEquals(5,investorSupportedClasses.size());
        Assert.assertTrue(investorSupportedClasses.contains(DocumentCategories.ADVICE.getDocumentClass()));
        Assert.assertTrue(investorSupportedClasses.contains(DocumentCategories.SMSF.getDocumentClass()));
        Assert.assertTrue(investorSupportedClasses.contains(DocumentCategories.TAX.getDocumentClass()));
        Assert.assertTrue(investorSupportedClasses.contains(DocumentCategories.CORRESPONDENCE.getDocumentClass()));
        Assert.assertTrue(investorSupportedClasses.contains(DocumentCategories.OTHER.getDocumentClass()));
        Assert.assertTrue(investorSupportedClasses.contains(DocumentCategories.INVESTMENTS.getDocumentClass()));
        Assert.assertTrue(investorSupportedClasses.contains(DocumentCategories.STATEMENTS.getDocumentClass()));
        Assert.assertTrue(investorSupportedClasses.contains(DocumentCategories.TAX_SUPER.getDocumentClass()));

        List<String> panoramaSupportedClasses= VisibilityRoles.PANORAMA.getSupportedClass();
        Assert.assertEquals(8,panoramaSupportedClasses.size());
        Assert.assertTrue(panoramaSupportedClasses.contains(DocumentCategories.ADVICE.getDocumentClass()));
        Assert.assertTrue(panoramaSupportedClasses.contains(DocumentCategories.SMSF.getDocumentClass()));
        Assert.assertTrue(panoramaSupportedClasses.contains(DocumentCategories.TAX.getDocumentClass()));
        Assert.assertTrue(panoramaSupportedClasses.contains(DocumentCategories.CORRESPONDENCE.getDocumentClass()));
        Assert.assertTrue(panoramaSupportedClasses.contains(DocumentCategories.OTHER.getDocumentClass()));
        Assert.assertTrue(panoramaSupportedClasses.contains(DocumentCategories.INVESTMENTS.getDocumentClass()));
        Assert.assertTrue(panoramaSupportedClasses.contains(DocumentCategories.STATEMENTS.getDocumentClass()));
        Assert.assertTrue(panoramaSupportedClasses.contains(DocumentCategories.OFFLINEAPPROVAL.getDocumentClass()));
        Assert.assertTrue(panoramaSupportedClasses.contains(DocumentCategories.TAX_SUPER.getDocumentClass()));
    }

    @Test
    public void getRequiredValueOfValues() {
        VisibilityRoles.valueOf("ADVISER");
        VisibilityRoles.valueOf("INVESTOR");
        VisibilityRoles.valueOf("INVESTMENT_MANAGER");
        VisibilityRoles.valueOf("PANORAMA");
        VisibilityRoles.valueOf("CHALLENGER");
    }

    @Test
    public void getSupportedCategories()
    {
        List<String> adviserSupportedCategories= VisibilityRoles.ADVISER.getSupportedCategories();
        Assert.assertEquals(10,adviserSupportedCategories.size());
        Assert.assertTrue(adviserSupportedCategories.contains(DocumentCategories.ADVICE.getCode()));
        Assert.assertTrue(adviserSupportedCategories.contains(DocumentCategories.SMSF.getCode()));
        Assert.assertTrue(adviserSupportedCategories.contains(DocumentCategories.TAX.getCode()));
        Assert.assertTrue(adviserSupportedCategories.contains(DocumentCategories.CORRESPONDENCE.getCode()));
        Assert.assertTrue(adviserSupportedCategories.contains(DocumentCategories.OTHER.getCode()));
        Assert.assertTrue(adviserSupportedCategories.contains(DocumentCategories.INVESTMENTS.getCode()));
        Assert.assertTrue(adviserSupportedCategories.contains(DocumentCategories.STATEMENTS.getCode()));
        Assert.assertTrue(adviserSupportedCategories.contains(DocumentCategories.TAX_SUPER.getCode()));
        Assert.assertTrue(adviserSupportedCategories.contains(DocumentCategories.CHALLENGER.getCode()));

        List<String> accountantSupportedCategories= VisibilityRoles.ACCOUNTANT.getSupportedCategories();
        Assert.assertEquals(7, accountantSupportedCategories.size());
        Assert.assertFalse(accountantSupportedCategories.contains(DocumentCategories.ADVICE.getCode()));
        Assert.assertTrue(accountantSupportedCategories.contains(DocumentCategories.SMSF.getCode()));
        Assert.assertTrue(accountantSupportedCategories.contains(DocumentCategories.TAX.getCode()));
        Assert.assertTrue(accountantSupportedCategories.contains(DocumentCategories.CORRESPONDENCE.getCode()));
        Assert.assertTrue(accountantSupportedCategories.contains(DocumentCategories.OTHER.getCode()));
        Assert.assertTrue(accountantSupportedCategories.contains(DocumentCategories.INVESTMENTS.getCode()));
        Assert.assertTrue(accountantSupportedCategories.contains(DocumentCategories.STATEMENTS.getCode()));
        Assert.assertTrue(accountantSupportedCategories.contains(DocumentCategories.TAX_SUPER.getCode()));
        Assert.assertFalse(accountantSupportedCategories.contains(DocumentCategories.ADVICE.getCode()));
        Assert.assertFalse(accountantSupportedCategories.contains(DocumentCategories.IMMODELREPORT.getCode()));
        Assert.assertFalse(accountantSupportedCategories.contains(DocumentCategories.EMAIL.getCode()));
        Assert.assertFalse(accountantSupportedCategories.contains(DocumentCategories.FAX.getCode()));
        Assert.assertFalse(accountantSupportedCategories.contains(DocumentCategories.SCANNED.getCode()));
        Assert.assertFalse(accountantSupportedCategories.contains(DocumentCategories.POBOX.getCode()));

        List<String> investorSupportedCategories= VisibilityRoles.INVESTOR.getSupportedCategories();
        Assert.assertEquals(10, investorSupportedCategories.size());
        Assert.assertTrue(investorSupportedCategories.contains(DocumentCategories.ADVICE.getCode()));
        Assert.assertTrue(investorSupportedCategories.contains(DocumentCategories.SMSF.getCode()));
        Assert.assertTrue(investorSupportedCategories.contains(DocumentCategories.TAX.getCode()));
        Assert.assertTrue(investorSupportedCategories.contains(DocumentCategories.CORRESPONDENCE.getCode()));
        Assert.assertTrue(investorSupportedCategories.contains(DocumentCategories.OTHER.getCode()));
        Assert.assertTrue(investorSupportedCategories.contains(DocumentCategories.INVESTMENTS.getCode()));
        Assert.assertTrue(investorSupportedCategories.contains(DocumentCategories.STATEMENTS.getCode()));
        Assert.assertTrue(investorSupportedCategories.contains(DocumentCategories.TAX_SUPER.getCode()));
        Assert.assertTrue(investorSupportedCategories.contains(DocumentCategories.CHALLENGER.getCode()));
        Assert.assertFalse(investorSupportedCategories.contains(DocumentCategories.IMMODELREPORT.getCode()));
        Assert.assertFalse(investorSupportedCategories.contains(DocumentCategories.EMAIL.getCode()));
        Assert.assertFalse(investorSupportedCategories.contains(DocumentCategories.FAX.getCode()));
        Assert.assertFalse(investorSupportedCategories.contains(DocumentCategories.SCANNED.getCode()));
        Assert.assertFalse(investorSupportedCategories.contains(DocumentCategories.POBOX.getCode()));

        List<String> panoramaSupportedCategories= VisibilityRoles.PANORAMA.getSupportedCategories();
        Assert.assertEquals(15, panoramaSupportedCategories.size());
        Assert.assertTrue(panoramaSupportedCategories.contains(DocumentCategories.ADVICE.getCode()));
        Assert.assertTrue(panoramaSupportedCategories.contains(DocumentCategories.SMSF.getCode()));
        Assert.assertTrue(panoramaSupportedCategories.contains(DocumentCategories.TAX.getCode()));
        Assert.assertTrue(panoramaSupportedCategories.contains(DocumentCategories.CORRESPONDENCE.getCode()));
        Assert.assertTrue(panoramaSupportedCategories.contains(DocumentCategories.OTHER.getCode()));
        Assert.assertTrue(panoramaSupportedCategories.contains(DocumentCategories.INVESTMENTS.getCode()));
        Assert.assertTrue(panoramaSupportedCategories.contains(DocumentCategories.STATEMENTS.getCode()));
        Assert.assertTrue(panoramaSupportedCategories.contains(DocumentCategories.IMMODELREPORT.getCode()));
        Assert.assertTrue(panoramaSupportedCategories.contains(DocumentCategories.EMAIL.getCode()));
        Assert.assertTrue(panoramaSupportedCategories.contains(DocumentCategories.FAX.getCode()));
        Assert.assertTrue(panoramaSupportedCategories.contains(DocumentCategories.SCANNED.getCode()));
        Assert.assertTrue(panoramaSupportedCategories.contains(DocumentCategories.POBOX.getCode()));
        Assert.assertTrue(panoramaSupportedCategories.contains(DocumentCategories.OFFLINEAPPROVAL.getCode()));
        Assert.assertTrue(panoramaSupportedCategories.contains(DocumentCategories.TAX_SUPER.getCode()));
        Assert.assertTrue(investorSupportedCategories.contains(DocumentCategories.CHALLENGER.getCode()));
    }
    @Test
    public void testIsDeleteAllowed()
    {
        //possible deletions for advisor
        VisibilityRoles roles=VisibilityRoles.ADVISER;
       boolean is= roles.isDeleteAllowed("adviser");
        Assert.assertTrue(is);

       boolean  is1=roles.isDeleteAllowed("Panorama");
        Assert.assertFalse(is1);

        boolean is2=roles.isDeleteAllowed("Investor");
        Assert.assertTrue(is2);

        boolean is3=roles.isDeleteAllowed("Accountant");
        Assert.assertTrue(is3);

        //possible deletions for investor
         roles=VisibilityRoles.INVESTOR;
        boolean is4= roles.isDeleteAllowed("adviser");
        Assert.assertFalse(is4);

        boolean  is5=roles.isDeleteAllowed("Panorama");
        Assert.assertFalse(is5);

        boolean is6=roles.isDeleteAllowed("Investor");
        Assert.assertTrue(is6);

        boolean is7=roles.isDeleteAllowed("Accountant");
        Assert.assertFalse(is7);

        //possible deletions for accountant
        roles=VisibilityRoles.ACCOUNTANT;
        boolean is8= roles.isDeleteAllowed("adviser");
        Assert.assertFalse(is8);

        boolean  is9=roles.isDeleteAllowed("Panorama");
        Assert.assertFalse(is9);

        boolean is10=roles.isDeleteAllowed("Investor");
        Assert.assertFalse(is10);

        boolean is11=roles.isDeleteAllowed("Accountant");
        Assert.assertTrue(is11);

        //possible deletions for panorama
        roles=VisibilityRoles.PANORAMA;
        boolean is12= roles.isDeleteAllowed("adviser");
        Assert.assertTrue(is12);

        boolean  is13=roles.isDeleteAllowed("Panorama");
        Assert.assertTrue(is13);

        boolean is14=roles.isDeleteAllowed("Investor");
        Assert.assertTrue(is14);

        boolean is15=roles.isDeleteAllowed("Accountant");
        Assert.assertTrue(is15);

    }
}
