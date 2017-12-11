package com.bt.nextgen.service.avaloq.account;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.avaloq.pasttransaction.TransactionOrderType;
import com.btfin.panorama.service.avaloq.pasttransaction.TransactionType;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.BillerRequest;
import com.bt.nextgen.service.integration.account.DeleteLinkedAccRequest;
import com.bt.nextgen.service.integration.account.LinkedAccRequest;
import com.bt.nextgen.service.integration.account.PayeeRequest;
import com.bt.nextgen.service.integration.account.UpdateAccountDetailResponse;
import com.bt.nextgen.service.integration.account.UpdatePaymentLimitRequest;
import com.bt.nextgen.service.integration.account.UpdatePrimContactRequest;
import com.bt.nextgen.service.integration.account.UpdateTaxPrefRequest;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class AvaloqAccountIntegrationServiceImplUpdateIntegrationTest extends BaseSecureIntegrationTest
{

        @Autowired
        AccountIntegrationService avaloqAccountIntegrationService;

        private BigDecimal modificationnumber = new BigDecimal(5);

        @Test
        @SecureTestContext
        public void testUpdateTaxPreference() throws Exception
        {
                ServiceErrors serviceErrors = new ServiceErrorsImpl();
                com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("76224");
                UpdateTaxPrefRequest request = new UpdateBPDetailsTestImpl();
                request.setAccountKey(accountKey);
                request.setCGTLMethod(CGTLMethod.MAX_GAIN);
                request.setModificationIdentifier(new BigDecimal(2));
                UpdateAccountDetailResponse response = avaloqAccountIntegrationService.updateTaxPreference(request, serviceErrors);
                testUpdateTaxPrefForPositiveRes(response, modificationnumber);
                assertThat(serviceErrors.getErrorList().iterator().hasNext(), is(false));
                //Testing for Negative Response
                accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("999888");
                request.setAccountKey(accountKey);
                response = avaloqAccountIntegrationService.updateTaxPreference(request, serviceErrors);
                testUpdateTaxPrefForErrorRes(response, modificationnumber);
                assertThat(serviceErrors.getErrorList().iterator().hasNext(), is(true));

        }

        @Test
        @SecureTestContext
        public void testUpdatePrimaryContact() throws Exception
        {
                ServiceErrors serviceErrors = new ServiceErrorsImpl();
                //com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("34714");--Trust
                //CLT30002Company
                com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("76224");
                UpdatePrimContactRequest request = new UpdateBPDetailsTestImpl();
                request.setAccountKey(accountKey);
                //request.setPrimaryContactPersonId(ClientKey.valueOf("34712"));76111
                request.setPrimaryContactPersonId(ClientKey.valueOf("76112"));
                request.setModificationIdentifier(new BigDecimal(3));
                UpdateAccountDetailResponse response = avaloqAccountIntegrationService.updatePrimaryContact(request, serviceErrors);
                testUpdatePrimContPositiveRes(response, modificationnumber);
                assertThat(serviceErrors.getErrorList().iterator().hasNext(), is(false));
                //Testing for Negative Response
                accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("999888");
                request.setAccountKey(accountKey);
                response = avaloqAccountIntegrationService.updatePrimaryContact(request, serviceErrors);
                testUpdateTaxPrefForErrorRes(response, modificationnumber);
                assertThat(serviceErrors.getErrorList().iterator().hasNext(), is(true));

        }

        @Test
        @SecureTestContext
        public void testAddLinkedAccount() throws Exception
        {
                ServiceErrors serviceErrors = new ServiceErrorsImpl();
                //com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("34714");--Trust
                //CLT30002Company
                com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("76224");
                LinkedAccountImpl account = new LinkedAccountImpl();
                account.setAccountNumber("333456791");
                account.setBsb("062111");
                //      account.setCurrencyId("1009");
                account.setCurrency(CurrencyType.SwissFranc);
                account.setName("Linked Account2 Name 112222");
                account.setNickName("Linked Account2 Name 11222222");
                account.setPrimary(true);
                LinkedAccRequest request = new UpdateBPDetailsTestImpl();
                request.setAccountKey(accountKey);
                request.setLinkedAccount(account);
                request.setModificationIdentifier(new BigDecimal(14));
                UpdateAccountDetailResponse response = avaloqAccountIntegrationService.addLinkedAccount(request, serviceErrors);
                testAddLinkedAccountPositiveRes(response, modificationnumber);
                assertThat(serviceErrors.getErrorList().iterator().hasNext(), is(false));
                //Testing for Negative Response
                accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("999888");
                request.setAccountKey(accountKey);
                response = avaloqAccountIntegrationService.addLinkedAccount(request, serviceErrors);
                testAddLinkedAccountForErrorRes(response, modificationnumber);
                assertThat(serviceErrors.getErrorList().iterator().hasNext(), is(true));

        }

        @Test
        @SecureTestContext
        public void testUpdateLinkedAccount() throws Exception
        {
                ServiceErrors serviceErrors = new ServiceErrorsImpl();
                //com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("34714");--Trust
                //CLT30002Company
                com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("76224");
                LinkedAccountImpl account = new LinkedAccountImpl();
                account.setAccountNumber("333456791");
                account.setBsb("062111");
                account.setName("Linked Account Name 30002-Test22222");
                account.setNickName("Linked Account Name 30002-Test2222");
                account.setCurrency(CurrencyType.SwissFranc);
                account.setPrimary(true);
                LinkedAccRequest request = new UpdateBPDetailsTestImpl();
                request.setAccountKey(accountKey);
                request.setLinkedAccount(account);
                request.setModificationIdentifier(new BigDecimal(14));
                UpdateAccountDetailResponse response = avaloqAccountIntegrationService.updateLinkedAccount(request, serviceErrors);
                testUpdateLinkedAccountPositiveRes(response, modificationnumber);
                assertThat(serviceErrors.getErrorList().iterator().hasNext(), is(false));
                //Testing for Negative Response
                accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("999888");
                request.setAccountKey(accountKey);
                response = avaloqAccountIntegrationService.addLinkedAccount(request, serviceErrors);
                testUpdateLinkedAccountErrorRes(response, modificationnumber);
                assertThat(serviceErrors.getErrorList().iterator().hasNext(), is(true));

        }

        @Test
        @SecureTestContext
        public void testDeleteLinkedAccount() throws Exception
        {
                ServiceErrors serviceErrors = new ServiceErrorsImpl();
                //com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("34714");--Trust
                //CLT30002Company
                com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("76224");
                LinkedAccountImpl account = new LinkedAccountImpl();
                account.setAccountNumber("123456791");
                account.setBsb("062111");
                DeleteLinkedAccRequest request = new UpdateBPDetailsTestImpl();
                request.setAccountKey(accountKey);
                request.setBankAccount(account);
                request.setModificationIdentifier(new BigDecimal(6));
                UpdateAccountDetailResponse response = avaloqAccountIntegrationService.deleteLinkedAccount(request, serviceErrors);
                testUpdateLinkedAccountPositiveRes(response, modificationnumber);
                assertThat(serviceErrors.getErrorList().iterator().hasNext(), is(false));
                //Testing for Negative Response
                accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("999888");
                request.setAccountKey(accountKey);
                response = avaloqAccountIntegrationService.deleteLinkedAccount(request, serviceErrors);
                testDeleteLinkedAccountErrorRes(response, modificationnumber);
                assertThat(serviceErrors.getErrorList().iterator().hasNext(), is(true));

        }

        @Test
        @SecureTestContext
        public void testAddRegPayeeAccount() throws Exception
        {
                ServiceErrors serviceErrors = new ServiceErrorsImpl();
                //com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("34714");--Trust
                //CLT.2085= Payee
                com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("76767");
                BankAccountImpl account = new BankAccountImpl();
                account.setAccountNumber("987654321");
                account.setBsb("012012");
                account.setName("Add Payee 1");
                account.setNickName("Add Payee Nickname 1");
                PayeeRequest request = new UpdatePayeeDetailsTestImpl();
                request.setAccountKey(accountKey);
                request.setBankAccount(account);
                request.setModificationIdentifier(new BigDecimal(16));
                UpdateAccountDetailResponse response = avaloqAccountIntegrationService.addNewRegPayeeDetail(request, serviceErrors);
                testUpdateAccountDetailsPositiveRes(response, modificationnumber);
                assertThat(serviceErrors.getErrorList().iterator().hasNext(), is(false));
                //Testing for Negative Response
                accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("999888");
                request.setAccountKey(accountKey);
                response = avaloqAccountIntegrationService.addNewRegPayeeDetail(request, serviceErrors);
                testUpdateAccountDetailsErrorRes(response, modificationnumber);
                assertThat(serviceErrors.getErrorList().iterator().hasNext(), is(true));

        }

        @Test
        @SecureTestContext
        public void testUpdateRegPayeeAccount() throws Exception
        {
                ServiceErrors serviceErrors = new ServiceErrorsImpl();
                //com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("34714");--Trust
                //CLT.2085= Payee
                com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("76767");
                BankAccountImpl account = new BankAccountImpl();
                account.setAccountNumber("987654321");
                account.setBsb("012012");
                account.setName("Macquarie Mortgages");
                account.setNickName("Macquarie Mortgages -Test");
                PayeeRequest request = new UpdatePayeeDetailsTestImpl();
                request.setAccountKey(accountKey);
                request.setBankAccount(account);
                request.setModificationIdentifier(new BigDecimal(14));
                UpdateAccountDetailResponse response = avaloqAccountIntegrationService.updateExistingPayeeDetail(request, serviceErrors);
                testUpdateAccountDetailsPositiveRes(response, modificationnumber);
                assertThat(serviceErrors.getErrorList().iterator().hasNext(), is(false));
                //Testing for Negative Response
                accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("999888");
                request.setAccountKey(accountKey);
                response = avaloqAccountIntegrationService.updateExistingPayeeDetail(request, serviceErrors);
                testUpdateAccountDetailsErrorRes(response, modificationnumber);
                assertThat(serviceErrors.getErrorList().iterator().hasNext(), is(true));

        }

        @Test
        @SecureTestContext
        public void testDeleteRegPayeeAccount() throws Exception
        {
                ServiceErrors serviceErrors = new ServiceErrorsImpl();
                //com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("34714");--Trust
                //CLT.2085= Payee
                com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("76767");
                BankAccountImpl account = new BankAccountImpl();
                account.setAccountNumber("10104937");
                account.setBsb("063184");
                PayeeRequest request = new UpdatePayeeDetailsTestImpl();
                request.setAccountKey(accountKey);
                request.setBankAccount(account);
                request.setModificationIdentifier(new BigDecimal(16));
                UpdateAccountDetailResponse response = avaloqAccountIntegrationService.deleteExistingPayeeDetail(request, serviceErrors);
                testUpdateAccountDetailsPositiveRes(response, modificationnumber);
                assertThat(serviceErrors.getErrorList().iterator().hasNext(), is(false));
                //Testing for Negative Response
                accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("999888");
                request.setAccountKey(accountKey);
                response = avaloqAccountIntegrationService.deleteExistingPayeeDetail(request, serviceErrors);
                testUpdateAccountDetailsErrorRes(response, modificationnumber);
                assertThat(serviceErrors.getErrorList().iterator().hasNext(), is(true));

        }

        @Test
        @SecureTestContext
        public void testAddBillerCode() throws Exception
        {
                ServiceErrors serviceErrors = new ServiceErrorsImpl();
                //com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("34714");--Trust
                //CLT.2000 Billers
                com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("76767");
                BillerImpl account = new BillerImpl();
                account.setCRN("987654321");
                account.setBillerCode("5009");
                account.setName("Macquarie Mortgages");
                account.setNickName("Macquarie Mortgages -Test");
                BillerRequest request = new UpdateBPDetailsTestImpl();
                request.setAccountKey(accountKey);
                request.setBillerDetail(account);
                request.setModificationIdentifier(new BigDecimal(20));
                UpdateAccountDetailResponse response = avaloqAccountIntegrationService.addNewBillerDetail(request, serviceErrors);
                testUpdateAccountDetailsPositiveRes(response, modificationnumber);
                assertThat(serviceErrors.getErrorList().iterator().hasNext(), is(false));
                //Testing for Negative Response
                accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("999888");
                request.setAccountKey(accountKey);
                response = avaloqAccountIntegrationService.addNewBillerDetail(request, serviceErrors);
                testUpdateAccountDetailsErrorRes(response, modificationnumber);
                assertThat(serviceErrors.getErrorList().iterator().hasNext(), is(true));

        }

        @Test
        @SecureTestContext
        public void testUpdateBillerCode() throws Exception
        {
                ServiceErrors serviceErrors = new ServiceErrorsImpl();
                //com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("34714");--Trust
                //CLT.2000 Billers
                com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("76767");
                BillerImpl account = new BillerImpl();
                account.setCRN("12345678");
                account.setBillerCode("5009");
                account.setName("Name testing 1 ");
                account.setNickName("next gen cash Testing ---2");
                BillerRequest request = new UpdateBPDetailsTestImpl();
                request.setAccountKey(accountKey);
                request.setBillerDetail(account);
                request.setModificationIdentifier(new BigDecimal(18));
                UpdateAccountDetailResponse response = avaloqAccountIntegrationService.updateExistingBillerDetail(request, serviceErrors);
                testUpdateAccountDetailsPositiveRes(response, modificationnumber);
                assertThat(serviceErrors.getErrorList().iterator().hasNext(), is(false));
                //Testing for Negative Response
                accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("999888");
                request.setAccountKey(accountKey);
                response = avaloqAccountIntegrationService.updateExistingBillerDetail(request, serviceErrors);
                testUpdateAccountDetailsErrorRes(response, modificationnumber);
                assertThat(serviceErrors.getErrorList().iterator().hasNext(), is(true));

        }

        @Test
        @SecureTestContext
        public void testDeleteBillerCode() throws Exception
        {
                ServiceErrors serviceErrors = new ServiceErrorsImpl();
                //com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("34714");--Trust
                //CLT.2000 Billers
                com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("76767");
                BillerImpl account = new BillerImpl();
                account.setCRN("987654321");
                account.setBillerCode("5009");
                BillerRequest request = new UpdateBPDetailsTestImpl();
                request.setAccountKey(accountKey);
                request.setBillerDetail(account);
                request.setModificationIdentifier(new BigDecimal(19));
                UpdateAccountDetailResponse response = avaloqAccountIntegrationService.deleteExistingBillerDetail(request, serviceErrors);
                testUpdateAccountDetailsPositiveRes(response, modificationnumber);
                assertThat(serviceErrors.getErrorList().iterator().hasNext(), is(false));
                //Testing for Negative Response
                accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("999888");
                request.setAccountKey(accountKey);
                response = avaloqAccountIntegrationService.deleteExistingBillerDetail(request, serviceErrors);
                testUpdateAccountDetailsErrorRes(response, modificationnumber);
                assertThat(serviceErrors.getErrorList().iterator().hasNext(), is(true));

        }

        @Test
        @SecureTestContext
        public void testUpdateTrXLimit() throws Exception
        {
                ServiceErrors serviceErrors = new ServiceErrorsImpl();
                //com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("34714");--Trust
                //CLT.2000 Billers
                com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("76767");

                UpdatePaymentLimitRequest request = new UpdateBPDetailsTestImpl();
                request.setAccountKey(accountKey);
                request.setAmount(new BigDecimal(10400));
                request.setBusinessTransactionType(TransactionType.PAY);
                request.setBusinessTransactionOrderType(TransactionOrderType.PAY_ANYONE);
                //request.setBusinessTransactionType(TransactionType.PAY);
                //      request.setBusinessTransactionOrderType(TransactionOrderType.BPAY);

                request.setCurrency(CurrencyType.AustralianDollar);
                request.setModificationIdentifier(new BigDecimal(13));
                UpdateAccountDetailResponse response = avaloqAccountIntegrationService.updatePaymentLimit(request, serviceErrors);
                testUpdateAccountDetailsPositiveRes(response, modificationnumber);
                assertThat(serviceErrors.getErrorList().iterator().hasNext(), is(false));
                //Testing for Negative Response
                accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf("999888");
                request.setAccountKey(accountKey);
                response = avaloqAccountIntegrationService.updatePaymentLimit(request, serviceErrors);
                testUpdateAccountDetailsErrorRes(response, modificationnumber);
                assertThat(serviceErrors.getErrorList().iterator().hasNext(), is(true));

        }

        protected UpdateAccountDetailResponse testUpdateTaxPrefForPositiveRes(UpdateAccountDetailResponse response,
                BigDecimal modificationnumber)
        {
                assertThat(response, is(notNullValue()));
                assertThat(response.getModificationIdentifier(), is(modificationnumber));
                assertThat(response.isUpdatedFlag(), is(true));
                return response;
        }

        protected UpdateAccountDetailResponse testUpdateTaxPrefForErrorRes(UpdateAccountDetailResponse response,
                BigDecimal modificationnumber)
        {
                assertThat(response, is(notNullValue()));
                assertThat(response.getModificationIdentifier(), is(modificationnumber));
                assertThat(response.isUpdatedFlag(), is(false));
                return response;
        }

        protected UpdateAccountDetailResponse testUpdatePrimContPositiveRes(UpdateAccountDetailResponse response,
                BigDecimal modificationnumber)
        {
                assertThat(response, is(notNullValue()));
                assertThat(response.getModificationIdentifier(), is(modificationnumber));
                assertThat(response.isUpdatedFlag(), is(true));
                return response;
        }

        protected UpdateAccountDetailResponse testUpdatePrimContErrorRes(UpdateAccountDetailResponse response,
                BigDecimal modificationnumber)
        {
                assertThat(response, is(notNullValue()));
                assertThat(response.getModificationIdentifier(), is(modificationnumber));
                assertThat(response.isUpdatedFlag(), is(false));
                return response;
        }

        protected UpdateAccountDetailResponse testAddLinkedAccountPositiveRes(UpdateAccountDetailResponse response,
                BigDecimal modificationnumber)
        {
                assertThat(response, is(notNullValue()));
                assertThat(response.getModificationIdentifier(), is(modificationnumber));
                assertThat(response.isUpdatedFlag(), is(true));
                return response;
        }

        protected UpdateAccountDetailResponse testUpdateLinkedAccountPositiveRes(UpdateAccountDetailResponse response,
                BigDecimal modificationnumber)
        {
                assertThat(response, is(notNullValue()));
                assertThat(response.getModificationIdentifier(), is(modificationnumber));
                assertThat(response.isUpdatedFlag(), is(true));
                return response;
        }

        protected UpdateAccountDetailResponse testUpdateLinkedAccountErrorRes(UpdateAccountDetailResponse response,
                BigDecimal modificationnumber)
        {
                assertThat(response, is(notNullValue()));
                assertThat(response.getModificationIdentifier(), is(modificationnumber));
                assertThat(response.isUpdatedFlag(), is(false));
                return response;
        }

        protected UpdateAccountDetailResponse testAddLinkedAccountForErrorRes(UpdateAccountDetailResponse response,
                BigDecimal modificationnumber)
        {
                assertThat(response, is(notNullValue()));
                assertThat(response.getModificationIdentifier(), is(modificationnumber));
                assertThat(response.isUpdatedFlag(), is(false));
                return response;
        }

        protected UpdateAccountDetailResponse testDeleteLinkedAccountErrorRes(UpdateAccountDetailResponse response,
                BigDecimal modificationnumber)
        {
                assertThat(response, is(notNullValue()));
                assertThat(response.getModificationIdentifier(), is(modificationnumber));
                assertThat(response.isUpdatedFlag(), is(false));
                return response;
        }

        protected UpdateAccountDetailResponse testUpdateAccountDetailsPositiveRes(UpdateAccountDetailResponse response,
                BigDecimal modificationnumber)
        {
                assertThat(response, is(notNullValue()));
                assertThat(response.getModificationIdentifier(), is(modificationnumber));
                assertThat(response.isUpdatedFlag(), is(true));
                return response;
        }

        protected UpdateAccountDetailResponse testUpdateAccountDetailsErrorRes(UpdateAccountDetailResponse response,
                BigDecimal modificationnumber)
        {
                assertThat(response, is(notNullValue()));
                assertThat(response.getModificationIdentifier(), is(modificationnumber));
                assertThat(response.isUpdatedFlag(), is(false));
                return response;
        }

}
