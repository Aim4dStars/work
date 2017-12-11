package com.bt.nextgen.service.integration.financialdocument;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Set;

import static com.bt.nextgen.service.avaloq.userinformation.UserExperience.ADVISED;
import static com.bt.nextgen.service.avaloq.userinformation.UserExperience.DIRECT;
import static com.bt.nextgen.service.integration.account.AccountStructureType.Individual;
import static com.bt.nextgen.service.integration.account.AccountStructureType.Joint;
import static com.bt.nextgen.service.integration.account.AccountStructureType.SMSF;
import static com.bt.nextgen.service.integration.financialdocument.FinancialDocumentType.ANNUAL_INVESTMENT_STATEMENT;
import static com.bt.nextgen.service.integration.financialdocument.FinancialDocumentType.ANNUAL_TAX_STATEMENT;
import static com.bt.nextgen.service.integration.financialdocument.FinancialSupplementaryDocumentType.ANNUAL_AUDIT_REPORT;
import static com.bt.nextgen.service.integration.financialdocument.FinancialSupplementaryDocumentType.DIRECT_STATEMENT_GUIDE;
import static com.bt.nextgen.service.integration.financialdocument.FinancialSupplementaryDocumentType.DIRECT_TAX_GUIDE;
import static com.bt.nextgen.service.integration.financialdocument.FinancialSupplementaryDocumentType.SMSF_TAX_GUIDE;
import static com.bt.nextgen.service.integration.financialdocument.FinancialSupplementaryDocumentType.STATEMENT_GUIDE;
import static com.bt.nextgen.service.integration.financialdocument.FinancialSupplementaryDocumentType.TAX_GUIDE;
import static com.bt.nextgen.service.integration.financialdocument.FinancialSupplementaryDocumentType.getSupplementaryDocuments;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class FinancialSupplementaryDocumentTypeTest {

    private Set<FinancialSupplementaryDocumentType> docs;

    @Test
    public void getSupplementaryDocuments_AnnualTaxStatement_SMSF() {
        docs = getSupplementaryDocuments(ANNUAL_TAX_STATEMENT, SMSF, ADVISED);
        assertEquals(docs.size(), 1);
        assertTrue(docs.contains(SMSF_TAX_GUIDE));
    }

    @Test
    public void getSupplementaryDocuments_AnnualTaxStatement_Direct() {
        docs = getSupplementaryDocuments(ANNUAL_TAX_STATEMENT, Individual, DIRECT);
        assertEquals(docs.size(), 1);
        assertTrue(docs.contains(DIRECT_TAX_GUIDE));
    }

    @Test
    public void getSupplementaryDocuments_AnnualTaxStatement_Other() {
        docs = getSupplementaryDocuments(ANNUAL_TAX_STATEMENT, Joint, ADVISED);
        assertEquals(docs.size(), 1);
        assertTrue(docs.contains(TAX_GUIDE));
    }

    @Test
    public void getSupplementaryDocuments_AnnualInvestmentStatement() {
        docs = getSupplementaryDocuments(ANNUAL_INVESTMENT_STATEMENT, SMSF, ADVISED);
        assertEquals(docs.size(), 2);
        assertTrue(docs.contains(ANNUAL_AUDIT_REPORT));
        assertTrue(docs.contains(STATEMENT_GUIDE));
    }

    @Test
    public void getSupplementaryDocuments_AnnualInvestmentStatement_Direct() {
        docs = getSupplementaryDocuments(ANNUAL_INVESTMENT_STATEMENT, Individual, DIRECT);
        assertEquals(docs.size(), 2);
        assertTrue(docs.contains(ANNUAL_AUDIT_REPORT));
        assertTrue(docs.contains(DIRECT_STATEMENT_GUIDE));
    }
}
