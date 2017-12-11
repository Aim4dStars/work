package com.bt.nextgen.api.transactioncategorisation.service;

import com.bt.nextgen.api.transactioncategorisation.model.TransactionCategoryDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.core.conversion.CodeCategory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransactionCategoryDtoServiceImplTest {
    @InjectMocks
    private TransactionCategoryDtoServiceImpl transactionCategoryDtoServiceImpl;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    Collection<Code> typeCodeList = new ArrayList<>();

    Collection<Code> subTypeCodeList = new ArrayList<>();
    ArrayList<ApiSearchCriteria> criteriaList = new ArrayList<ApiSearchCriteria>();
    ServiceErrors serviceErrors;

    @Before
    public void setUp() throws Exception {
        CodeImpl code1 = new CodeImpl("25", "ADMIN", "Administration", "admin");
        code1.addField("modal", "fund");
        code1.addField("txntype", "2");

        CodeImpl code2 = new CodeImpl("1", "CONTRI", "Contribution", "contri");
        code2.addField("modal", "member");
        code1.addField("txntype", "1");

        CodeImpl code3 = new CodeImpl("26", "INSUR", "Insurance", "insur");
        code3.addField("modal", "member");
        code1.addField("txntype", "2");

        CodeImpl code4 = new CodeImpl("21", "PENSION", "Pension", "pension");
        code4.addField("modal", "member");
        code1.addField("txntype", "1");

        CodeImpl code5 = new CodeImpl("27", "PRTY", "Property expense", "prty");
        code5.addField("modal", "fund");
        code1.addField("txntype", "2");

        CodeImpl code6 = new CodeImpl("24", "PURCH", "Asset purchase", "purch");
        code6.addField("modal", "fund");
        code1.addField("txntype", "2");

        CodeImpl code7 = new CodeImpl("28", "REGULTRY", "Tax / Regulatory", "regltry");
        code7.addField("modal", "fund");
        code1.addField("txntype", "2");

        CodeImpl code8 = new CodeImpl("29", "OTH_INCOME", "Other Income", "oth_income");
        code8.addField("modal", "fund");
        code1.addField("txntype", "1");

        typeCodeList.add(code1);
        typeCodeList.add(code2);
        typeCodeList.add(code3);
        typeCodeList.add(code4);
        typeCodeList.add(code5);
        typeCodeList.add(code6);
        typeCodeList.add(code7);
        typeCodeList.add(code8);

        CodeImpl subTypeCode1 = new CodeImpl("1", "EMPL", "Employer", "empl");
        subTypeCode1.addField("cash_cat_type_id", "1");
        CodeImpl subTypeCode2 = new CodeImpl("31", "ESTAB", "Establishment Fee", "estab");
        subTypeCode2.addField("cash_cat_type_id", "25");
        CodeImpl subTypeCode3 = new CodeImpl("30", "ADMIN", "Admin Fee", "admin");
        subTypeCode3.addField("cash_cat_type_id", "25");
        CodeImpl subTypeCode4 = new CodeImpl("32", "ACTUARY", "Actuary Fee", "actuary");
        subTypeCode4.addField("cash_cat_type_id", "25");

        CodeImpl subTypeCode5 = new CodeImpl("41", "LOAN_PAY", "Loan repayment", "loan_pay");
        subTypeCode5.addField("cash_cat_type_id", "27");
        CodeImpl subTypeCode6 = new CodeImpl("47", "UTIL", "Utilities", "util");
        subTypeCode6.addField("cash_cat_type_id", "27");
        CodeImpl subTypeCode7 = new CodeImpl("49", "INCOME_TAX", "Income tax", "income_tax");
        subTypeCode7.addField("cash_cat_type_id", "28");
        CodeImpl subTypeCode8 = new CodeImpl("40", "DISBRS", "Legal & disbursement", "disbrs");
        subTypeCode8.addField("cash_cat_type_id", "27");

        CodeImpl subTypeCode9 = new CodeImpl("66", "REGLTRY_OTH", "Other", "regltry_oth");
        subTypeCode9.addField("cash_cat_type_id", "28");
        CodeImpl subTypeCode10 = new CodeImpl("38", "INSUR", "Insurance", "insur");
        subTypeCode10.addField("cash_cat_type_id", "26");
        CodeImpl subTypeCode11 = new CodeImpl("25", "FIXED_INTR", "Fixed interest", "fixed_intr");
        subTypeCode11.addField("cash_cat_type_id", "24");
        CodeImpl subTypeCode12 = new CodeImpl("21", "PENSION", "Pension", "pension");
        subTypeCode12.addField("cash_cat_type_id", "21");

        CodeImpl subTypeCode13 = new CodeImpl("54", "DIV", "Dividend", "div");
        subTypeCode13.addField("cash_cat_type_id", "29");

        subTypeCodeList.add(subTypeCode1);
        subTypeCodeList.add(subTypeCode2);
        subTypeCodeList.add(subTypeCode3);
        subTypeCodeList.add(subTypeCode4);
        subTypeCodeList.add(subTypeCode5);
        subTypeCodeList.add(subTypeCode6);
        subTypeCodeList.add(subTypeCode7);
        subTypeCodeList.add(subTypeCode8);
        subTypeCodeList.add(subTypeCode9);
        subTypeCodeList.add(subTypeCode10);
        subTypeCodeList.add(subTypeCode11);
        subTypeCodeList.add(subTypeCode12);
        subTypeCodeList.add(subTypeCode13);

    }

    @Test
    public void getTransactionCategories() {
        when(staticIntegrationService.loadCodes(eq(CodeCategory.CASH_CATEGORY_TYPE), any(ServiceErrors.class))).thenReturn(typeCodeList);
        when(staticIntegrationService.loadCodes(eq(CodeCategory.CASH_CATEGORY_SUB_TYPE), any(ServiceErrors.class))).thenReturn(subTypeCodeList);

        List<TransactionCategoryDto> dtoList = transactionCategoryDtoServiceImpl.search(criteriaList, serviceErrors);
        assertNotNull(dtoList);

        Assert.assertEquals(dtoList.size(), 8);

        Assert.assertEquals(dtoList.get(0).getSubCategories().size(), 3);
        Assert.assertEquals(dtoList.get(1).getSubCategories().size(), 1);
        Assert.assertEquals(dtoList.get(2).getSubCategories().size(), 1);
        Assert.assertEquals(dtoList.get(3).getSubCategories().size(), 1);

        Assert.assertEquals(dtoList.get(4).getSubCategories().size(), 1);
        Assert.assertEquals(dtoList.get(5).getSubCategories().size(), 1);
        Assert.assertEquals(dtoList.get(6).getSubCategories().size(), 3);
        Assert.assertEquals(dtoList.get(7).getSubCategories().size(), 2);

        Assert.assertEquals(dtoList.get(0).getCategorisationLevel(), "fund");
        Assert.assertEquals(dtoList.get(0).getLabel(), "Administration");
        Assert.assertEquals(dtoList.get(0).getIntlId(), "admin");

        Assert.assertEquals(dtoList.get(0).getSubCategories().get(0).getValue(), "ACTUARY");
        Assert.assertEquals(dtoList.get(0).getSubCategories().get(0).getListName(), "cash_cat_subtype");
        Assert.assertEquals(dtoList.get(0).getSubCategories().get(0).getIntlId(), "actuary");
        Assert.assertEquals(dtoList.get(0).getSubCategories().get(0).getLabel(), "Actuary Fee");

        Assert.assertEquals(dtoList.get(0).getSubCategories().get(1).getValue(), "ADMIN");
        Assert.assertEquals(dtoList.get(0).getSubCategories().get(1).getListName(), "cash_cat_subtype");
        Assert.assertEquals(dtoList.get(0).getSubCategories().get(1).getIntlId(), "admin");
        Assert.assertEquals(dtoList.get(0).getSubCategories().get(1).getLabel(), "Admin Fee");

        Assert.assertEquals(dtoList.get(0).getSubCategories().get(2).getValue(), "ESTAB");
        Assert.assertEquals(dtoList.get(0).getSubCategories().get(2).getListName(), "cash_cat_subtype");
        Assert.assertEquals(dtoList.get(0).getSubCategories().get(2).getIntlId(), "estab");
        Assert.assertEquals(dtoList.get(0).getSubCategories().get(2).getLabel(), "Establishment Fee");

        Assert.assertEquals(dtoList.get(1).getCategorisationLevel(), "fund");
        Assert.assertEquals(dtoList.get(1).getLabel(), "Asset purchase");
        Assert.assertEquals(dtoList.get(1).getIntlId(), "purch");

        Assert.assertEquals(dtoList.get(1).getSubCategories().get(0).getValue(), "FIXED_INTR");
        Assert.assertEquals(dtoList.get(1).getSubCategories().get(0).getListName(), "cash_cat_subtype");
        Assert.assertEquals(dtoList.get(1).getSubCategories().get(0).getIntlId(), "fixed_intr");
        Assert.assertEquals(dtoList.get(1).getSubCategories().get(0).getLabel(), "Fixed interest");

        Assert.assertEquals(dtoList.get(2).getCategorisationLevel(), "member");
        Assert.assertEquals(dtoList.get(2).getLabel(), "Contribution");
        Assert.assertEquals(dtoList.get(2).getIntlId(), "contri");

        Assert.assertEquals(dtoList.get(2).getSubCategories().get(0).getValue(), "EMPL");
        Assert.assertEquals(dtoList.get(2).getSubCategories().get(0).getListName(), "cash_cat_subtype");
        Assert.assertEquals(dtoList.get(2).getSubCategories().get(0).getIntlId(), "empl");
        Assert.assertEquals(dtoList.get(2).getSubCategories().get(0).getLabel(), "Employer");

        Assert.assertEquals(dtoList.get(3).getCategorisationLevel(), "member");
        Assert.assertEquals(dtoList.get(3).getLabel(), "Insurance");
        Assert.assertEquals(dtoList.get(3).getIntlId(), "insur");

        Assert.assertEquals(dtoList.get(3).getSubCategories().get(0).getValue(), "INSUR");
        Assert.assertEquals(dtoList.get(3).getSubCategories().get(0).getListName(), "cash_cat_subtype");
        Assert.assertEquals(dtoList.get(3).getSubCategories().get(0).getIntlId(), "insur");
        Assert.assertEquals(dtoList.get(3).getSubCategories().get(0).getLabel(), "Insurance");

        Assert.assertEquals(dtoList.get(5).getCategorisationLevel(), "member");
        Assert.assertEquals(dtoList.get(5).getLabel(), "Pension");
        Assert.assertEquals(dtoList.get(5).getIntlId(), "pension");

        Assert.assertEquals(dtoList.get(5).getSubCategories().get(0).getValue(), "PENSION");
        Assert.assertEquals(dtoList.get(5).getSubCategories().get(0).getListName(), "cash_cat_subtype");
        Assert.assertEquals(dtoList.get(5).getSubCategories().get(0).getIntlId(), "pension");
        Assert.assertEquals(dtoList.get(5).getSubCategories().get(0).getLabel(), "Pension");

        Assert.assertEquals(dtoList.get(6).getCategorisationLevel(), "fund");
        Assert.assertEquals(dtoList.get(6).getLabel(), "Property expense");
        Assert.assertEquals(dtoList.get(6).getIntlId(), "prty");

        Assert.assertEquals(dtoList.get(6).getSubCategories().get(0).getValue(), "DISBRS");
        Assert.assertEquals(dtoList.get(6).getSubCategories().get(0).getListName(), "cash_cat_subtype");
        Assert.assertEquals(dtoList.get(6).getSubCategories().get(0).getIntlId(), "disbrs");
        Assert.assertEquals(dtoList.get(6).getSubCategories().get(0).getLabel(), "Legal & disbursement");

        Assert.assertEquals(dtoList.get(6).getSubCategories().get(1).getValue(), "LOAN_PAY");
        Assert.assertEquals(dtoList.get(6).getSubCategories().get(1).getListName(), "cash_cat_subtype");
        Assert.assertEquals(dtoList.get(6).getSubCategories().get(1).getIntlId(), "loan_pay");
        Assert.assertEquals(dtoList.get(6).getSubCategories().get(1).getLabel(), "Loan repayment");

        Assert.assertEquals(dtoList.get(6).getSubCategories().get(2).getValue(), "UTIL");
        Assert.assertEquals(dtoList.get(6).getSubCategories().get(2).getListName(), "cash_cat_subtype");
        Assert.assertEquals(dtoList.get(6).getSubCategories().get(2).getIntlId(), "util");
        Assert.assertEquals(dtoList.get(6).getSubCategories().get(2).getLabel(), "Utilities");

        Assert.assertEquals(dtoList.get(7).getCategorisationLevel(), "fund");
        Assert.assertEquals(dtoList.get(7).getLabel(), "Tax / Regulatory");
        Assert.assertEquals(dtoList.get(7).getIntlId(), "regltry");

        Assert.assertEquals(dtoList.get(7).getSubCategories().get(0).getValue(), "INCOME_TAX");
        Assert.assertEquals(dtoList.get(7).getSubCategories().get(0).getListName(), "cash_cat_subtype");
        Assert.assertEquals(dtoList.get(7).getSubCategories().get(0).getIntlId(), "income_tax");
        Assert.assertEquals(dtoList.get(7).getSubCategories().get(0).getLabel(), "Income tax");

        Assert.assertEquals(dtoList.get(7).getSubCategories().get(1).getValue(), "REGLTRY_OTH");
        Assert.assertEquals(dtoList.get(7).getSubCategories().get(1).getListName(), "cash_cat_subtype");
        Assert.assertEquals(dtoList.get(7).getSubCategories().get(1).getIntlId(), "regltry_oth");
        Assert.assertEquals(dtoList.get(7).getSubCategories().get(1).getLabel(), "Other");

    }
}
