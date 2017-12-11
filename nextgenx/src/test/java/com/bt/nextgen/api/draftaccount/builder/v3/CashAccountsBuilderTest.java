package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.api.draftaccount.model.form.LinkedAccountsFormFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ns.btfin_com.product.common.cashaccount.v2_0.LinkedFinancialInstitutionType;
import ns.btfin_com.product.common.investmentaccount.v2_0.CashAccountsType;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class CashAccountsBuilderTest {

    private CashAccountsBuilder cashAccountsBuilder = new CashAccountsBuilder();

    @Test
    public void shouldBuildPaymentInstructionsForPrimaryAccount() throws IOException {
        String linkedAccountsJson = "{\n" +
                "        \"primaryLinkedAccount\": {\n" +
                "            \"accountname\": \"JD SMITH WESTPAC 1\",\n" +
                "            \"bsb\": \"123456\",\n" +
                "            \"accountnumber\": \"987654321\",\n" +
                "            \"nickname\": \"JDS WESTPAC 1@#\",\n" +
                "            \"directdebitamount\": \"500.00\",\n" +
                "            \"isAccountManuallyEntered\": \"true\"\n" +
                "        },\n" +
                "        \"otherLinkedAccount\": []\n" +
                "    }";

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> linkedAccountsMap = mapper.readValue(linkedAccountsJson, new TypeReference<Map<String, Object>>() {
        });

        CashAccountsType cashAccounts = cashAccountsBuilder.getCashAccounts(LinkedAccountsFormFactory.getNewLinkedAccountsForm(linkedAccountsMap));
        assertThat(cashAccounts.getCashAccount(), hasSize(1));
        List<LinkedFinancialInstitutionType> linkedFinancialInstitutions = cashAccounts.getCashAccount().get(0).getLinkedFinancialInstitutions().getLinkedFinancialInstitution();
        assertThat(linkedFinancialInstitutions, hasSize(1));

        validateLinkedFinancialInstitution(linkedFinancialInstitutions.get(0), "987654321", "123-456", "JD SMITH WESTPAC 1", "JDS WESTPAC 1@#", true);
        assertEquals(true, linkedFinancialInstitutions.get(0).isIsAccountManuallyEntered());
    }

    @Test
    public void shouldBuildPaymentInstructionsForOtherLinkedAccounts() throws IOException {
        String linkedAccountsJson = "{\n" +
                "        \"primaryLinkedAccount\": {\n" +
                "            \"accountname\": \"JD SMITH WESTPAC 1\",\n" +
                "            \"bsb\": \"123456\",\n" +
                "            \"accountnumber\": \"987654321\",\n" +
                "            \"nickname\": \"JDS WESTPAC 1@#\",\n" +
                "            \"directdebitamount\": \"500.00\"\n" +
                "        }," +
                "        \"otherLinkedAccount\": [\n" +
                "            {\n" +
                "                \"accountname\": \"JD SMITH WESTPAC 2\",\n" +
                "                \"bsb\": \"654321\",\n" +
                "                \"accountnumber\": \"12345678\",\n" +
                "                \"nickname\": \"JDS WESTPAC 2\",\n" +
                "                \"directdebitamount\": \"99.99\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"accountname\": \"JD SMITH ST. GEORGE 1\",\n" +
                "                \"bsb\": \"123654\",\n" +
                "                \"accountnumber\": \"919191919\",\n" +
                "                \"nickname\": \"JDS ST GEORGE 1\",\n" +
                "                \"directdebitamount\": \"1000.11\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"accountname\": \"JD SMITH ST. 2\",\n" +
                "                \"bsb\": \"123645\",\n" +
                "                \"accountnumber\": \"345345345\",\n" +
                "                \"nickname\": \"JDS ST GEORGE 2\",\n" +
                "                \"directdebitamount\": \"1.11\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"accountname\": \"JD SMITH ST. Junior\",\n" +
                "                \"bsb\": \"393939\",\n" +
                "                \"accountnumber\": \"939393939\",\n" +
                "                \"nickname\": \"JDS ST GEORGE Junior\",\n" +
                "                \"directdebitamount\": \"0.00\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }";

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> linkedAccountsMap = mapper.readValue(linkedAccountsJson, new TypeReference<Map<String, Object>>() {
        });
        CashAccountsType cashAccounts = cashAccountsBuilder.getCashAccounts(LinkedAccountsFormFactory.getNewLinkedAccountsForm(linkedAccountsMap));
        assertThat(cashAccounts.getCashAccount(), hasSize(1));
        List<LinkedFinancialInstitutionType> linkedFinancialInstitutions = cashAccounts.getCashAccount().get(0).getLinkedFinancialInstitutions().getLinkedFinancialInstitution();
        assertThat(linkedFinancialInstitutions, hasSize(5));


        validateLinkedFinancialInstitution(linkedFinancialInstitutions.get(1), "12345678", "654-321", "JD SMITH WESTPAC 2", "JDS WESTPAC 2", false);
        validateLinkedFinancialInstitution(linkedFinancialInstitutions.get(2), "919191919", "123-654", "JD SMITH ST. GEORGE 1", "JDS ST GEORGE 1", false);
        validateLinkedFinancialInstitution(linkedFinancialInstitutions.get(3), "345345345", "123-645", "JD SMITH ST. 2", "JDS ST GEORGE 2", false);
        validateLinkedFinancialInstitution(linkedFinancialInstitutions.get(4), "939393939", "393-939", "JD SMITH ST. Junior", "JDS ST GEORGE Junior", false);
    }

    private void validateLinkedFinancialInstitution(LinkedFinancialInstitutionType linkedFinancialInstitution, String accountNumber,
                                                    String bsb, String accountName, String alias, boolean isPrimary) {
        assertEquals(accountNumber, linkedFinancialInstitution.getAccountNumber());
        assertEquals(bsb, linkedFinancialInstitution.getBSB());
        assertEquals(accountName, linkedFinancialInstitution.getAccountName());
        assertEquals(alias, linkedFinancialInstitution.getAliasName());
        assertEquals(isPrimary, linkedFinancialInstitution.isIsPrimaryLinkedAccountFlag());
    }

}
