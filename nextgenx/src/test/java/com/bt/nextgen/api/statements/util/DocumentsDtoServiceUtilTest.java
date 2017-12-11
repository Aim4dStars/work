package com.bt.nextgen.api.statements.util;

import com.bt.nextgen.api.statements.model.SupplimentaryDocument;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.financialdocument.BaseCmisDocument;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentType;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DocumentsDtoServiceUtilTest {

    private String aemPath;
    private BaseCmisDocument document;
    private WrapAccount account;

    @Before
    public void setUp() throws Exception {
        aemPath = Properties.getString("aem.financialDocs.path");

        document = mock(BaseCmisDocument.class);
        when(document.getDocumentTitleCode()).thenReturn(FinancialDocumentType.ANNUAL_INVESTMENT_STATEMENT.getCode());
        when(document.getPeriodEndDate()).thenReturn(new DateTime("2015-08-17T20:27:05.000+11:00"));

        account = mock(WrapAccount.class);
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.SMSF);
    }

    @Test
    public void getSupplementaryDocuments_AIS() {
        List<String> name = new ArrayList<>();
        List<String> url = new ArrayList<>();
        List<SupplimentaryDocument> docs = DocumentsDtoServiceUtil.getSupplementaryDocuments(document, account, UserExperience.ADVISED);

        for (SupplimentaryDocument supplimentaryDocument : docs) {
            name.add(supplimentaryDocument.getName());
            url.add(supplimentaryDocument.getUrl());
        }

        assertEquals(docs.size(), 2);
        assertThat(name, contains("Annual audit report", "Statement guide"));
        assertThat(url, contains(aemPath + "Audit_Report_2014-2015.pdf", aemPath + "BT_Annual_Investor_Statement_Guide_2014-2015.pdf"));
    }

    @Test
    public void getSupplementaryDocuments_AIS_Direct() {
        List<String> name = new ArrayList<>();
        List<String> url = new ArrayList<>();
        List<SupplimentaryDocument> docs = DocumentsDtoServiceUtil.getSupplementaryDocuments(document, account, UserExperience.DIRECT);

        for (SupplimentaryDocument supplimentaryDocument : docs) {
            name.add(supplimentaryDocument.getName());
            url.add(supplimentaryDocument.getUrl());
        }

        assertEquals(docs.size(), 2);
        assertThat(name, contains("Annual audit report", "BT Invest statement guide"));
        assertThat(url, contains(aemPath + "Audit_Report_2014-2015.pdf", aemPath + "BT_Invest_Annual_Investor_Statement_Guide_2014-2015.pdf"));
    }

    @Test
    public void getSupplementaryDocuments_ATS_SMSF() {
        when(document.getDocumentTitleCode()).thenReturn(FinancialDocumentType.ANNUAL_TAX_STATEMENT.getCode());
        List<SupplimentaryDocument> docs = DocumentsDtoServiceUtil.getSupplementaryDocuments(document, account, UserExperience.ADVISED);

        assertEquals(docs.size(), 1);
        assertEquals(docs.get(0).getName(), "SMSF Tax guide");
        assertEquals(docs.get(0).getUrl(), aemPath + "BT_Panorama_SMSF_Tax_Guide_2014-2015.pdf");
    }

    @Test
    public void getSupplementaryDocuments_ATS_Other() {
        when(document.getDocumentTitleCode()).thenReturn(FinancialDocumentType.ANNUAL_TAX_STATEMENT.getCode());
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        List<SupplimentaryDocument> docs = DocumentsDtoServiceUtil.getSupplementaryDocuments(document, account, UserExperience.ADVISED);

        assertEquals(docs.size(), 1);
        assertEquals(docs.get(0).getName(), "Tax guide");
        assertEquals(docs.get(0).getUrl(), aemPath + "BT_Panorama_Tax_Guide_2014-2015.pdf");
    }

    @Test
    public void getSupplementaryDocuments_ATS_Direct() {
        when(document.getDocumentTitleCode()).thenReturn(FinancialDocumentType.ANNUAL_TAX_STATEMENT.getCode());
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        List<SupplimentaryDocument> docs = DocumentsDtoServiceUtil.getSupplementaryDocuments(document, account, UserExperience.DIRECT);

        assertEquals(docs.size(), 1);
        assertEquals(docs.get(0).getName(), "BT Invest tax guide");
        assertEquals(docs.get(0).getUrl(), aemPath + "BT_Invest_Tax_Guide_2014-2015.pdf");
    }

    @Test
    public void getSupplementaryDocuments_NoAccount() {
        when(document.getDocumentTitleCode()).thenReturn(FinancialDocumentType.ANNUAL_TAX_STATEMENT.getCode());
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        List<SupplimentaryDocument> docs = DocumentsDtoServiceUtil.getSupplementaryDocuments(document, null, UserExperience.DIRECT);

        assertEquals(docs.size(), 0);
    }

    @Test
    public void getSupplementaryDocuments_NoUserExp() {
        when(document.getDocumentTitleCode()).thenReturn(FinancialDocumentType.ANNUAL_TAX_STATEMENT.getCode());
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        List<SupplimentaryDocument> docs = DocumentsDtoServiceUtil.getSupplementaryDocuments(document, account, null);

        assertEquals(docs.size(), 0);
    }
}