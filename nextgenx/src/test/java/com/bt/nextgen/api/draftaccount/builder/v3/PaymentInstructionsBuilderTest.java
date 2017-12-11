package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.api.draftaccount.model.form.ClientApplicationFormFactory;
import com.bt.nextgen.api.draftaccount.model.form.ILinkedAccountsForm;
import com.bt.nextgen.api.draftaccount.model.form.LinkedAccountsFormFactory;
import com.bt.nextgen.config.ApplicationContextProvider;
import com.bt.nextgen.config.JsonObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.PaymentInstructionType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.PaymentInstructionsType;
import ns.btfin_com.sharedservices.common.payment.v2_1.CreditDebitIndicatorType;
import ns.btfin_com.sharedservices.common.payment.v2_1.FinancialInstitutionAccountType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;


public class PaymentInstructionsBuilderTest {

    private PaymentInstructionsBuilder paymentInstructionsBuilder = new PaymentInstructionsBuilder();

    @Before
    public void setupServices() {
        ObjectMapper mapper = new JsonObjectMapper();
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        Mockito.when(applicationContext.getBean("jsonObjectMapper")).thenReturn(mapper);
        ApplicationContextProvider applicationContextProvider = new ApplicationContextProvider(null);
        applicationContextProvider.setApplicationContext(applicationContext);
    }

    @Test
    public void shouldBuildPaymentInstructionsForPrimaryAccount() throws IOException {
        String linkedAccountsJson = "{\n" +
                "        \"primaryLinkedAccount\": {\n" +
                "            \"accountname\": \"JD SMITH WESTPAC 1\",\n" +
                "            \"bsb\": \"123456\",\n" +
                "            \"accountnumber\": \"987654321\",\n" +
                "            \"nickname\": \"JDS WESTPAC 1@#\",\n" +
                "            \"directdebitamount\": \"500.00\"\n" +
                "        },\n" +
                "        \"otherLinkedAccount\": []\n" +
                "    }";
        ILinkedAccountsForm form = readLinkedAccountsForm(linkedAccountsJson);
        PaymentInstructionsType paymentInstructions = paymentInstructionsBuilder.getPaymentInstructions(form);
        assertThat(paymentInstructions.getPaymentInstruction(), hasSize(1));

        validatePaymentInstructionType(paymentInstructions.getPaymentInstruction().get(0), new BigDecimal("500.00"), "987654321", "123-456", "JD SMITH WESTPAC 1");
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
        ILinkedAccountsForm form = readLinkedAccountsForm(linkedAccountsJson);
        PaymentInstructionsType paymentInstructions = paymentInstructionsBuilder.getPaymentInstructions(form);
        assertThat(paymentInstructions.getPaymentInstruction(), hasSize(4));

        validatePaymentInstructionType(paymentInstructions.getPaymentInstruction().get(1), new BigDecimal("99.99"), "12345678", "654-321", "JD SMITH WESTPAC 2");
        validatePaymentInstructionType(paymentInstructions.getPaymentInstruction().get(2), new BigDecimal("1000.11"), "919191919", "123-654", "JD SMITH ST. GEORGE 1");
        validatePaymentInstructionType(paymentInstructions.getPaymentInstruction().get(3), new BigDecimal("1.11"), "345345345", "123-645", "JD SMITH ST. 2");
    }

    @Test
    public void shouldBuildPaymentInstructionsFromLinkedAccountsAndInvestmentChoice() throws IOException {
        String formData = "{\"accountType\": \"individual\"," +
                "   \"linkedaccounts\": {\n" +
                "      \"primaryLinkedAccount\" : {\n" +
                "      \"selectedBankAccount\": \"732095596137\",\n" +
                "      \"accountname\": \"Cash Account 1\",\n" +
                "      \"bsb\": \"732095\",\n" +
                "      \"accountnumber\": \"596137\",\n" +
                "      \"bsbValidationResultVal\": false\n" +
                "      }\n" +
                "    },\n" +
                "    \"investmentoptions\": {\n" +
                "      \"portfolioType\": \"moderate\",\n" +
                "      \"initialDeposit\": 12345.99,\n" +
                "      \"portfolioName\": \"BT Moderate Portfolio\"\n" +
                "    }}";

        Map<String, Object> map = readLinkedAccountsMap(formData);
        PaymentInstructionsType paymentInstructions = paymentInstructionsBuilder.getPaymentInstructionsForInvestmentAccount(ClientApplicationFormFactory.getNewClientApplicationForm(map));
        validatePaymentInstructionType(paymentInstructions.getPaymentInstruction().get(0), new BigDecimal("12345.99"), "596137", "732-095", "Cash Account 1");
    }

    private void validatePaymentInstructionType(PaymentInstructionType paymentType, BigDecimal paymentAmount, String accountNumber,
                                                String bsb, String accountName) {
        assertEquals(paymentAmount, paymentType.getPaymentAmount());
        assertEquals(CreditDebitIndicatorType.DEBIT, paymentType.getCreditDebitIndicator());
        FinancialInstitutionAccountType financialInstitutionAccount = paymentType.getDebtorAccount().getFinancialInstitutionAccount();
        assertEquals(accountNumber, financialInstitutionAccount.getAccountNumber());
        assertEquals(bsb, financialInstitutionAccount.getBSB());
        assertEquals(accountName, financialInstitutionAccount.getAccountName());

        assertNotNull(paymentType.getCreditorAccount());
        assertNotNull(paymentType.getCreditorAccount().getWorkingCashAccount());

        assertNotNull(paymentType.getPaymentType());
        assertNotNull(paymentType.getPaymentType().getDirectTransfer());

        assertNotNull(paymentType.getPaymentDate());
        assertNotNull(paymentType.getPaymentDate().getPaymentEffectiveDate());
    }

    private Map<String, Object> readLinkedAccountsMap(String formData) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(formData, new TypeReference<Map<String, Object>>(){});
    }

    private ILinkedAccountsForm readLinkedAccountsForm(String formData) throws IOException {
        return LinkedAccountsFormFactory.getNewLinkedAccountsForm(readLinkedAccountsMap(formData));
    }
}
