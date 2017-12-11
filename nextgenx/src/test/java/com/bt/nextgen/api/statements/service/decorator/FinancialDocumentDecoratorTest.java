package com.bt.nextgen.api.statements.service.decorator;

import com.bt.nextgen.api.statements.decorator.FinancialDocumentDecorator;
import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.api.statements.model.SupplimentaryDocument;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.cmis.CmisDocumentImpl;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.account.WrapAccount;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static com.bt.nextgen.service.integration.financialdocument.FinancialDocumentType.ANNUAL_INVESTMENT_STATEMENT;
import static com.bt.nextgen.service.integration.financialdocument.FinancialDocumentType.ANNUAL_TAX_STATEMENT;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FinancialDocumentDecoratorTest {

    private FinancialDocumentDecorator decorator;
    private String aemPath;
    private WrapAccount account;

    @Before
    public void setUp() throws Exception {
        aemPath = Properties.getString("aem.financialDocs.path");

        account = mock(WrapAccount.class);
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
    }

    @Test
    public void testDecorate_when_documentType_isStatement() throws Exception {
        CmisDocumentImpl document = new CmisDocumentImpl();
        document.setEndDate("2015-08-17T20:27:05.000+11:00");
        document.setDocumentType("STM");
        document.setDocumentTitleCode("STMANN");
        decorator = new FinancialDocumentDecorator(null, document, null, account, UserExperience.ADVISED);
        DocumentDto dto = decorator.decorate();
        assertThat(dto.getSupplimentaryDocumentList().size(), is(2));
        List<String> name = new ArrayList<>();
        List<String> url = new ArrayList<>();
        for (SupplimentaryDocument supplimentaryDocument : dto.getSupplimentaryDocumentList()) {
            name.add(supplimentaryDocument.getName());
            url.add(supplimentaryDocument.getUrl());
        }
        assertThat(name, contains("Annual audit report", "Statement guide"));
        assertThat(url, contains(aemPath + "Audit_Report_2014-2015.pdf", aemPath + "BT_Annual_Investor_Statement_Guide_2014-2015.pdf"));

    }

    @Test
    public void testDecorate_when_documentType_isn_statement_not_allLink_found() throws Exception {
        CmisDocumentImpl document = new CmisDocumentImpl();
        document.setEndDate("2015-08-17T20:27:05.000+11:00");
        document.setDocumentType("STM");
        document.setDocumentTitleCode("STMANN");
        decorator = new FinancialDocumentDecorator(null, document, null, account, UserExperience.ADVISED);
        DocumentDto dto = decorator.decorate();
        assertThat(dto.getSupplimentaryDocumentList().size(), is(2));

    }

    @Test
    public void testDecorate_when_documentType_isnot_statement() throws Exception {
        CmisDocumentImpl document = new CmisDocumentImpl();
        document.setEndDate("17/08/2015 20:27:05");
        document.setDocumentType(ANNUAL_TAX_STATEMENT.getCode());
        decorator = new FinancialDocumentDecorator(null, document, null, account, UserExperience.ADVISED);
        DocumentDto dto = decorator.decorate();
        assertThat(dto.getSupplimentaryDocumentList(), nullValue());
    }

    @Test
    public void testDecorate_when_documentType_TaxStatement() throws Exception {
        CmisDocumentImpl document = new CmisDocumentImpl();
        document.setEndDate("17/08/2015 20:27:05");
        document.setDocumentType(ANNUAL_TAX_STATEMENT.getCode());
        decorator = new FinancialDocumentDecorator(null, document, null, account, UserExperience.ADVISED);
        DocumentDto dto = decorator.decorate();
        assertThat(dto.getSupplimentaryDocumentList(), nullValue());
    }


    @Test
    public void testDecorate_forSMSFAccount_when_documentType_isInvestmentStatement() throws Exception {
        WrapAccount account = mock(WrapAccount.class);
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.SMSF);

        CmisDocumentImpl document = new CmisDocumentImpl();
        document.setEndDate("2015-08-17T20:27:05.000+11:00");
        document.setDocumentType("STM");
        document.setDocumentTitleCode(ANNUAL_INVESTMENT_STATEMENT.getCode());
        decorator = new FinancialDocumentDecorator(null, document, null, account, UserExperience.ADVISED);
        DocumentDto dto = decorator.decorate();
        assertThat(dto.getSupplimentaryDocumentList().size(), is(2));
        List<String> name = new ArrayList<>();
        List<String> url = new ArrayList<>();
        for (SupplimentaryDocument supplimentaryDocument : dto.getSupplimentaryDocumentList()) {
            name.add(supplimentaryDocument.getName());
            url.add(supplimentaryDocument.getUrl());
        }
        assertThat(name, contains("Annual audit report", "Statement guide"));
        assertThat(url, contains(aemPath + "Audit_Report_2014-2015.pdf", aemPath + "BT_Annual_Investor_Statement_Guide_2014-2015.pdf"));
    }

    @Test
    public void testDecorate_forSMSFAccount_when_documentType_isTaxStatement() throws Exception {
        WrapAccount account = mock(WrapAccount.class);
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.SMSF);

        CmisDocumentImpl document = new CmisDocumentImpl();
        document.setEndDate("2015-08-17T20:27:05.000+11:00");
        document.setDocumentType("STM");
        document.setDocumentTitleCode(ANNUAL_TAX_STATEMENT.getCode());
        decorator = new FinancialDocumentDecorator(null, document, null, account, UserExperience.ADVISED);
        DocumentDto dto = decorator.decorate();
        assertThat(dto.getSupplimentaryDocumentList().size(), is(1));
        List<String> name = new ArrayList<>();
        List<String> url = new ArrayList<>();
        for (SupplimentaryDocument supplimentaryDocument : dto.getSupplimentaryDocumentList()) {
            name.add(supplimentaryDocument.getName());
            url.add(supplimentaryDocument.getUrl());
        }
        assertThat(name, contains("SMSF Tax guide"));
        assertThat(url, contains(aemPath + "BT_Panorama_SMSF_Tax_Guide_2014-2015.pdf"));
    }
}