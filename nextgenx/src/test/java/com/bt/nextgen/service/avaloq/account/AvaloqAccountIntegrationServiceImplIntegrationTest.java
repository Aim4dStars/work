package com.bt.nextgen.service.avaloq.account;

import ch.lambdaj.Lambda;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.domain.AddressImpl;
import com.bt.nextgen.service.avaloq.domain.CompanyImpl;
import com.bt.nextgen.service.avaloq.domain.EmailImpl;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.avaloq.domain.PhoneImpl;
import com.bt.nextgen.service.avaloq.domain.SmsfImpl;
import com.bt.nextgen.service.group.customer.BankingCustomerIdentifier;
import com.bt.nextgen.service.integration.account.AccountBalance;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.account.AvailableCash;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.btfin.panorama.service.integration.account.SubAccount;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.account.WrapAccountDetailResponse;
import com.bt.nextgen.service.integration.domain.AddressKey;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.ExemptionReason;
import com.bt.nextgen.service.integration.domain.Gender;
import com.bt.nextgen.service.integration.domain.IdentityVerificationStatus;
import com.bt.nextgen.service.integration.domain.InvestorRole;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.ClientType;
import org.hamcrest.MatcherAssert;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;


public class AvaloqAccountIntegrationServiceImplIntegrationTest extends BaseSecureIntegrationTest {

    @Value("${accountId}")
    protected String accountId;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService avaloqAccountIntegrationService;

    private DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    @Test
    @SecureTestContext
    public void testLoadAvailableCash_whenValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey
                .valueOf(accountId);
        AvailableCash availableCash = avaloqAccountIntegrationService.loadAvailableCash(accountKey, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertNotNull(availableCash);

        Assert.assertEquals("36846", availableCash.getAccountKey().getId());
        Assert.assertEquals(BigDecimal.valueOf(7995), availableCash.getAvailableCash());
        Assert.assertEquals(BigDecimal.valueOf(55985), availableCash.getPendingSells());
        Assert.assertEquals(BigDecimal.valueOf(0), availableCash.getQueuedBuys());
        Assert.assertEquals(BigDecimal.valueOf(-11075), availableCash.getPendingBuys());
    }

    /**
     * To test the subscription changes for wpl direct. Need to run individually
     * not in test-suit.
     */
    @Ignore
    @Test
    @SecureTestContext(username = "wpl-direct")
    public void testLoadWrapAccount() {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey
                .valueOf(accountId);
        WrapAccountDetailImpl account = (WrapAccountDetailImpl) avaloqAccountIntegrationService.loadWrapAccountDetail(accountKey,
                serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertNotNull(account);
        Assert.assertNotNull(account.getProductSubscription());
        Assert.assertEquals(1, account.getProductSubscription().size());
        Assert.assertEquals("111776", account.getProductSubscription().get(0).getSubscribedProductId());
        Assert.assertEquals("10-AUG-2015", account.getProductSubscription().get(0).getSubscribedDateFrom());
        Assert.assertEquals("10-JAN-2016", account.getProductSubscription().get(0).getSubscribedDateTo());

        Assert.assertEquals("31278", account.getAccountKey().getId());
        Assert.assertEquals("N person-120_4228", account.getAccountName());
        Assert.assertEquals("120000203", account.getAccountNumber());
        Assert.assertEquals(AccountStatus.ACTIVE, account.getAccountStatus());

        Assert.assertEquals(3, account.getLinkedAccounts().size());
        Assert.assertEquals(2, account.getSubAccounts().size());
    }

    @Test
    @SecureTestContext(username = "adviserTest")
    public void testLoadWrapAccount_PendingStatus() {

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey
                .valueOf(accountId);
        WrapAccountDetailImpl account = (WrapAccountDetailImpl) avaloqAccountIntegrationService.loadWrapAccountDetail(accountKey,
                serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertNotNull(account);

        Assert.assertEquals("69776", account.getAccountKey().getId());
        Assert.assertEquals("Demo Boson SMSF", account.getAccountName());
        Assert.assertEquals("120009279", account.getAccountNumber());
        Assert.assertEquals(AccountStatus.PEND_OPN, account.getAccountStatus());

        Assert.assertEquals(1, account.getLinkedAccounts().size());
        Assert.assertEquals(2, account.getSubAccounts().size());
        SmsfImpl accountOwner = (SmsfImpl) account.getOwners().get(0);
        Assert.assertEquals("69772", accountOwner.getClientKey().getId());
        Assert.assertEquals(1, accountOwner.getAddresses().size());
        Assert.assertEquals(0, accountOwner.getEmails().size());
        Assert.assertEquals(0, accountOwner.getPhones().size());
        Assert.assertEquals(3, accountOwner.getLinkedClients().size());
        Assert.assertEquals(1, accountOwner.getTrustees().size());

    }

    @Test
    @SecureTestContext(username = "adviser",customerId = "201101101", profileId = "testing123", jobId = "testing123", jobRole = "ADVISER")
    public void testLoadWrapAccount_whenValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey
                .valueOf(accountId);
        WrapAccountDetailImpl account = (WrapAccountDetailImpl) avaloqAccountIntegrationService.loadWrapAccountDetail(accountKey,
                serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertNotNull(account);

        Assert.assertEquals("69776", account.getAccountKey().getId());
        Assert.assertEquals("Demo Boson SMSF", account.getAccountName());
        Assert.assertEquals("120009279", account.getAccountNumber());
        Assert.assertEquals("2014-08-26", formatter.print(account.getOpenDate()));
        Assert.assertEquals(null, account.getClosureDate());
        // Assert.assertEquals("2014-08-26",
        // formatter.print(account.getSignDate()));
        Assert.assertEquals("79260", account.getAdviserKey().getId());
        Assert.assertEquals("66773", account.getAdviserPersonId().getId());
        Assert.assertEquals("79260", account.getAdviserPositionId().getId());
        Assert.assertEquals("262786", account.getBsb());
        Assert.assertEquals("220186", account.getBillerCode());
        Assert.assertEquals(AccountStructureType.SMSF, account.getAccountStructureType());
        Assert.assertEquals(CGTLMethod.MIN_GAIN, account.getcGTLMethod());
        Assert.assertEquals("69770", account.getPrimaryContactPersonId().getId());
        Assert.assertEquals(AccountStatus.ACTIVE, account.getAccountStatus());

        Assert.assertEquals(3, account.getSubAccounts().size());
        Map<SubAccountKey, SubAccountImpl> subAccountMap = new HashMap<SubAccountKey, SubAccountImpl>();
        subAccountMap = Lambda.index(account.getSubAccounts(), Lambda.on(SubAccountImpl.class).getSubAccountKey());
        SubAccountImpl subAccount = subAccountMap.get(SubAccountKey.valueOf("69777"));
        Assert.assertNotNull(subAccount);
        Assert.assertEquals("69777", subAccount.getSubAccountKey().getId());
        Assert.assertEquals(ContainerType.DIRECT, subAccount.getSubAccountType());
        Assert.assertEquals("65373", subAccount.getProductIdentifier().getProductKey().getId());

        Assert.assertEquals(1, account.getLinkedAccounts().size());
        LinkedAccountImpl linkedAccount = (LinkedAccountImpl) account.getLinkedAccounts().get(0);
        Assert.assertEquals("62111", linkedAccount.getBsb());
        Assert.assertEquals("123456789", linkedAccount.getAccountNumber());
        Assert.assertEquals("Linked Account Name 50002", linkedAccount.getName());

        SmsfImpl accountOwner = (SmsfImpl) account.getOwners().get(0);

        // OWNER details
        Assert.assertEquals(InvestorType.SMSF, accountOwner.getInvestorType());
        Assert.assertEquals(false, accountOwner.getTfnProvided());
        Assert.assertEquals(ExemptionReason.NO_EXEMPTION, accountOwner.getExemptionReason());
        Assert.assertEquals(IdentityVerificationStatus.Completed, accountOwner.getIdVerificationStatus());
        Assert.assertEquals("201624340", accountOwner.getCustomerId());
        Assert.assertEquals("2015-02-25", formatter.print(accountOwner.getOpenDate()));
        Assert.assertEquals("1", accountOwner.getModificationSeq());
        Assert.assertEquals("69772", accountOwner.getClientKey().getId());
        Assert.assertEquals("Demo Boson SMSF", accountOwner.getFullName());

        // Owner Addresses
        Assert.assertEquals(1, accountOwner.getAddresses().size());
        Assert.assertEquals(0, accountOwner.getEmails().size());
        Assert.assertEquals(0, accountOwner.getPhones().size());

        // Owner Address
        AddressImpl ownerAddress = (AddressImpl) accountOwner.getAddresses().get(0);
        Assert.assertEquals("69754", ownerAddress.getAddressKey().getId());
        Assert.assertEquals("275", ownerAddress.getStreetNumber());
        Assert.assertEquals("Kent", ownerAddress.getStreetName());
        Assert.assertEquals("Street", ownerAddress.getStreetType());
        Assert.assertEquals("Sydney", ownerAddress.getSuburb());
        Assert.assertEquals("New South Wales", ownerAddress.getState());
        Assert.assertEquals("2000", ownerAddress.getPostCode());
        Assert.assertEquals("au", ownerAddress.getCountryCode());
        Assert.assertEquals("Australia", ownerAddress.getCountry());
        Assert.assertEquals("1", ownerAddress.getModificationSeq());
        Assert.assertEquals(true, ownerAddress.isDomicile());
        Assert.assertEquals(false, ownerAddress.isMailingAddress());
        Assert.assertEquals(AddressMedium.POSTAL, ownerAddress.getAddressType());
        Assert.assertEquals(1, ownerAddress.getCategoryId());
        Assert.assertEquals(false, ownerAddress.isPreferred());
        Assert.assertEquals("btfg$au_nsw", ownerAddress.getStateCode());
        Assert.assertEquals("NSW", ownerAddress.getStateAbbr());

        // Account Linked Clients
        Assert.assertEquals(3, accountOwner.getLinkedClients().size());
        Map<ClientKey, IndividualDetailImpl> linkedAccountsMap = new HashMap<ClientKey, IndividualDetailImpl>();
        linkedAccountsMap = Lambda.index(accountOwner.getLinkedClients(), Lambda.on(IndividualDetailImpl.class).getClientKey());
        IndividualDetailImpl associatedPerson = linkedAccountsMap.get(ClientKey.valueOf("69770"));
        Assert.assertNotNull(associatedPerson);
        Assert.assertEquals("Mr", associatedPerson.getTitle());
        Assert.assertEquals("Greg", associatedPerson.getFirstName());
        Assert.assertEquals("Demo", associatedPerson.getMiddleName());
        Assert.assertEquals("Twist", associatedPerson.getLastName());
        Assert.assertEquals(null, associatedPerson.getPreferredName());
        Assert.assertEquals(Gender.MALE, associatedPerson.getGender());
        Assert.assertEquals("1987-10-10", formatter.print(associatedPerson.getDateOfBirth()));
        Assert.assertEquals("Australia", associatedPerson.getResiCountryForTax());
        Assert.assertEquals("2061", associatedPerson.getResiCountryCodeForTax());
        Assert.assertEquals(27, associatedPerson.getAge());
        Assert.assertEquals(false, associatedPerson.getTfnProvided());
        Assert.assertEquals(ExemptionReason.NO_EXEMPTION, associatedPerson.getExemptionReason());
        Assert.assertEquals(IdentityVerificationStatus.Completed, associatedPerson.getIdVerificationStatus());
        Assert.assertEquals(1, associatedPerson.getPersonRoles().size());
        Assert.assertEquals(InvestorRole.Director, associatedPerson.getPersonRoles().get(0));
        Assert.assertEquals(InvestorRole.Director, associatedPerson.getPersonAssociation());
        Assert.assertEquals("201624338", associatedPerson.getCustomerId());
        Assert.assertEquals("2015-02-25", formatter.print(associatedPerson.getOpenDate()));
        Assert.assertEquals("2", associatedPerson.getModificationSeq());
        Assert.assertEquals("69770", associatedPerson.getClientKey().getId());
        Assert.assertEquals("Greg Twist", associatedPerson.getFullName());
        Assert.assertEquals("Natural Person", ClientType.getDescription(associatedPerson.getClientType()));
        Assert.assertEquals(InvestorType.INDIVIDUAL, associatedPerson.getInvestorType());

        // Account Linked Clients Address
        Assert.assertEquals(1, associatedPerson.getAddresses().size());
        Assert.assertEquals("69764", associatedPerson.getAddresses().get(0).getAddressKey().getId());

        // Account Linked Clients Phone
        Assert.assertEquals(2, associatedPerson.getPhones().size());
        Map<AddressKey, PhoneImpl> phonesMap = new HashMap<AddressKey, PhoneImpl>();
        phonesMap = Lambda.index(associatedPerson.getPhones(), Lambda.on(PhoneImpl.class).getPhoneKey());
        PhoneImpl phone = phonesMap.get(AddressKey.valueOf("69765"));
        Assert.assertNotNull(phone);
        Assert.assertEquals("69765", phone.getPhoneKey().getId());
        Assert.assertEquals(AddressMedium.MOBILE_PHONE_PRIMARY, phone.getType());
        Assert.assertEquals("0414222333", phone.getNumber());
        Assert.assertEquals("1", phone.getModificationSeq());
        Assert.assertEquals(true, phone.isPreferred());

        // Account Linked Clients Phone
        phone = phonesMap.get(AddressKey.valueOf("69767"));
        Assert.assertNotNull(phone);
        Assert.assertEquals("69767", phone.getPhoneKey().getId());
        Assert.assertEquals(AddressMedium.BUSINESS_TELEPHONE, phone.getType());
        Assert.assertEquals("0414222333", phone.getNumber());
        Assert.assertEquals("1", phone.getModificationSeq());
        Assert.assertEquals(false, phone.isPreferred());

        Assert.assertEquals(1, associatedPerson.getEmails().size());
        EmailImpl email = (EmailImpl) associatedPerson.getEmails().get(0);
        Assert.assertEquals("69766", email.getEmailKey().getId());
        Assert.assertEquals(AddressMedium.EMAIL_PRIMARY, email.getType());
        Assert.assertEquals("abc@abc.com", email.getEmail());
        Assert.assertEquals("1", email.getModificationSeq());
        Assert.assertEquals(false, email.isPreferred());

        // Account Linked Clients
        associatedPerson = linkedAccountsMap.get(ClientKey.valueOf("69768"));
        Assert.assertNotNull(associatedPerson);
        Assert.assertEquals("69768", associatedPerson.getClientKey().getId());
        Assert.assertEquals("201624336", associatedPerson.getCustomerId());
        Assert.assertEquals(1, associatedPerson.getPersonRoles().size());
        Assert.assertEquals(InvestorRole.Member, associatedPerson.getPersonRoles().get(0));
        Assert.assertEquals(1, associatedPerson.getAddresses().size());
        Assert.assertEquals("69756", associatedPerson.getAddresses().get(0).getAddressKey().getId());
        Assert.assertEquals(2, associatedPerson.getPhones().size());
        Assert.assertEquals(1, associatedPerson.getEmails().size());
        Assert.assertEquals("69758", associatedPerson.getEmails().get(0).getEmailKey().getId());

        // Account Linked Clients
        associatedPerson = linkedAccountsMap.get(ClientKey.valueOf("69769"));
        Assert.assertNotNull(associatedPerson);
        Assert.assertEquals("69769", associatedPerson.getClientKey().getId());
        Assert.assertEquals("201624337", associatedPerson.getCustomerId());
        Assert.assertEquals(1, associatedPerson.getPersonRoles().size());
        Assert.assertEquals(InvestorRole.Member, associatedPerson.getPersonRoles().get(0));
        Assert.assertEquals(1, associatedPerson.getAddresses().size());
        Assert.assertEquals("69760", associatedPerson.getAddresses().get(0).getAddressKey().getId());
        Assert.assertEquals(2, associatedPerson.getPhones().size());
        Assert.assertEquals(1, associatedPerson.getEmails().size());
        Assert.assertEquals("69762", associatedPerson.getEmails().get(0).getEmailKey().getId());

        // Account Trustee
        Assert.assertEquals(1, accountOwner.getTrustees().size());
        CompanyImpl company = (CompanyImpl) accountOwner.getTrustees().get(0);
        Assert.assertEquals("My occupier name", accountOwner.getTrustees().get(0).getAddresses().get(0).getOccupierName());

        Assert.assertEquals("69771", company.getClientKey().getId());
        Assert.assertEquals("Demo Boson Corp Private Ltd", company.getAsicName());
        Assert.assertEquals(InvestorRole.Trustee, company.getPersonRoles().get(0));
        Assert.assertEquals("201624339", company.getCustomerId());
        Assert.assertEquals(null, company.getAcn());
        Assert.assertEquals(null, company.getAbn()); // value not present in xml
        //
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Assert.assertEquals("1987-10-10", dateFormatter.format(company.getRegistrationDate())); // value
                                                                                                // not
                                                                                                // present
                                                                                                // in
                                                                                                // xml
        Assert.assertEquals(null, company.getRegistrationState());
        Assert.assertEquals(null, company.getRegistrationStateCode());
        Assert.assertEquals(false, company.isRegistrationForGst());
        Assert.assertEquals(InvestorType.COMPANY, company.getInvestorType());
        Assert.assertEquals(false, company.getTfnProvided());
        Assert.assertEquals(ExemptionReason.NO_EXEMPTION, company.getExemptionReason());
        Assert.assertEquals(null, company.getSafiDeviceId());
        Assert.assertEquals(IdentityVerificationStatus.Completed, company.getIdVerificationStatus());
        Assert.assertEquals("2015-02-25", formatter.print(company.getOpenDate()));
        Assert.assertEquals("1", company.getModificationSeq());
        Assert.assertEquals("Demo Boson Corp Pty Ltd", company.getFullName());
        Assert.assertEquals("Legal Person", ClientType.getDescription(company.getClientType()));
        Assert.assertEquals(InvestorRole.Trustee, company.getAssocRoleId());
        Assert.assertEquals(1, company.getAddresses().size());
        Assert.assertEquals("69755", company.getAddresses().get(0).getAddressKey().getId());
        Assert.assertEquals(0, company.getEmails().size());
        Assert.assertEquals(0, company.getPhones().size());

    }



    @SecureTestContext
    @Test
    public void testLoadPendingAccountList() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Collection<WrapAccount> wrapAccounts = avaloqAccountIntegrationService.loadWrapAccounts(serviceErrors);
        assertThat(wrapAccounts.size(), equalTo(16));
        assertThat(serviceErrors.hasErrors(), equalTo(false));

        Iterator<WrapAccount> i = wrapAccounts.iterator();

        WrapAccount wrapAccount0 = i.next();
        assertThat(wrapAccount0.getAccountKey().getId(), equalTo("74611"));
        assertThat(wrapAccount0.getAccountNumber(), equalTo("120011366"));
        assertThat(wrapAccount0.getAccountStructureType(), equalTo(AccountStructureType.Individual));
        assertThat(wrapAccount0.getProductKey(), equalTo(ProductKey.valueOf("84963")));
        assertThat(wrapAccount0.getAccountStatus(), equalTo(AccountStatus.PEND_OPN));
        assertThat(wrapAccount0.isOpen(), equalTo(true));
        assertThat(wrapAccount0.getAdviserPersonId().getId(), equalTo("69791"));
        assertThat(wrapAccount0.getAdviserPositionId().getId(), equalTo("90251"));
        assertThat(wrapAccount0.getAdviserPermissions().size(), equalTo(1));
        assertThat(wrapAccount0.getAdviserPermissions().iterator().next().getIntlId(),
                equalTo(TransactionPermission.Payments_Deposits.getIntlId()));
        assertThat(wrapAccount0.getSubAccounts().size(), equalTo(2));

        WrapAccount wrapAccount1 = i.next();
        assertThat(wrapAccount1.getAccountKey().getId(), equalTo("11111"));
        assertThat(wrapAccount1.getAccountNumber(), equalTo("120000005"));
        assertThat(wrapAccount1.getAccountStructureType(), equalTo(AccountStructureType.Company));
        assertThat(wrapAccount1.getProductKey().getId(), equalTo("84967"));
        assertThat(wrapAccount1.getAccountStatus(), equalTo(AccountStatus.ACTIVE));
        assertThat(wrapAccount1.isOpen(), equalTo(true));
        assertThat(wrapAccount1.getAdviserPositionId().getId(), equalTo("90251"));
    }

    @Test
    @SecureTestContext
    public void whenLoadingAccountBalanceListWithValidResponse() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<AccountBalance> accountBalances = avaloqAccountIntegrationService.loadAccountBalances(serviceErrors);
        assertThat(accountBalances.size(), equalTo(3));
        assertThat(serviceErrors.hasErrors(), equalTo(false));

        Map<String, AccountBalance> keyAccountBalanceMap = Lambda
                .index(accountBalances, on(AccountBalance.class).getAccountKey());

        AccountBalance accountBalance = keyAccountBalanceMap.get("11263");

        assertThat("Account balance has correct Id", accountBalance.getAccountKey(), equalTo("11263"));
        assertThat("Account balance has correct available cash", accountBalance.getAvailableCash(),
                equalTo(BigDecimal.valueOf(49957101.75)));
        assertThat("Account balance has correct portfolio value", accountBalance.getPortfolioValue(),
                equalTo(BigDecimal.valueOf(101468594.75)));
    }

    @SecureTestContext
    @Test
    public void testLoadContainers() {
        Map<AccountKey, List<SubAccount>> subAccounts = avaloqAccountIntegrationService.loadContainers(new ServiceErrorsImpl());
        assertThat(subAccounts.size(), equalTo(5));
        List<SubAccount> subAccount = subAccounts.get(AccountKey.valueOf("11263"));
        assertThat(subAccount.size(), equalTo(2));
        subAccount = subAccounts.get(AccountKey.valueOf("74611"));
        assertThat(subAccount.size(), equalTo(2));
    }

    @SecureTestContext
    @Test
    public void testLoadWrapAccountContainers() {
        Map<AccountKey, WrapAccount> accounts = avaloqAccountIntegrationService
                .loadWrapAccountWithoutContainers(new ServiceErrorsImpl());
        assertThat(accounts.size(), equalTo(16));
        WrapAccount account = accounts.get(AccountKey.valueOf("74611"));
        assertThat(account.getAccountNumber(), equalTo("120011366"));
        assertThat(account.getProductKey(), equalTo(ProductKey.valueOf("84963")));
        assertThat(account.getAccountOwners().size(), equalTo(1));
        assertThat(account.getAccountOwners().contains(ClientKey.valueOf("74609")), equalTo(true));
    }

    @Ignore
    @SecureTestContext(username = "explode", customerId = "201101101", profileId = "error123123", jobId = "error1231232", jobRole = "ADVISER" )
    @Test
    public void testLoadWrapAccountContainersError() {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Collection<WrapAccount> accounts = avaloqAccountIntegrationService.loadWrapAccounts(serviceErrors);
        MatcherAssert.assertThat(serviceErrors.hasErrors(), is(true));

    }

    @Test
    @SecureTestContext(username = "adviser",customerId = "201101101", profileId = "testing123", jobId = "testing123", jobRole = "ADVISER")
    public void testLoadWrapAccountDetails_WhenBpNumberPassed() {

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey
                .valueOf("120009279");
        WrapAccountDetailImpl account = (WrapAccountDetailImpl) avaloqAccountIntegrationService.loadWrapAccountDetailByAccountNo(accountKey,
                serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertNotNull(account);

        Assert.assertEquals("173536", account.getAccountKey().getId());
        Assert.assertEquals("First M. Last", account.getAccountName());
        Assert.assertEquals("120035886", account.getAccountNumber());
        Assert.assertEquals(null, account.getClosureDate());
        Assert.assertEquals("170482", account.getAdviserKey().getId());
        Assert.assertEquals("170491", account.getAdviserPersonId().getId());
        Assert.assertEquals("170482", account.getAdviserPositionId().getId());
        Assert.assertEquals("Roger DEMO AVSR Federer", account.getAdviserName());
        Assert.assertEquals(AccountStructureType.Individual, account.getAccountStructureType());
        Assert.assertEquals(AccountStatus.ACTIVE, account.getAccountStatus());
        Assert.assertEquals(account.getOwnerNames().size(), 1);
        Assert.assertEquals(account.getProductName(), "BT Panorama Investments");
    }
    @Test
    @SecureTestContext(username = "adviser",customerId = "201101101", profileId = "testing123", jobId = "testing123", jobRole = "ADVISER")
    public void testLoadAllWrapAccountsDetails_WhenGcmPassed() {

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        BankingCustomerIdentifier bankingCustomerIdentifier = new BankingCustomerIdentifier()
        {
            @Override
            public String getBankReferenceId()
            {
                return "201101101";
            }

            @Override
            public UserKey getBankReferenceKey()
            {
                return UserKey.valueOf("201101101");
            }

            @Override
            public CISKey getCISKey() {
                return null;
            }
        };

        WrapAccountDetailResponse accounts = avaloqAccountIntegrationService.loadWrapAccountDetailByGcm(bankingCustomerIdentifier,
                serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertNotNull(accounts);
        Assert.assertNotNull(accounts.getWrapAccountDetails());
        Assert.assertEquals(accounts.getWrapAccountDetails().size(), 4);

    }

    @Test
    @SecureTestContext(username = "accountbalance",customerId = "201635682", profileId = "5393", jobId = "testing123", jobRole = "ADVISER")
    public void whenLoadAccountBalancesList() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        List<AccountKey> accountKeyList = Arrays.asList(AccountKey.valueOf("71319"),
                AccountKey.valueOf("71335"), AccountKey.valueOf("71353"));
        List<AccountBalance> accountBalances = avaloqAccountIntegrationService.loadAccountBalances(accountKeyList, serviceErrors);
        assertThat(accountBalances.size(), equalTo(3));
        assertThat(serviceErrors.hasErrors(), equalTo(false));
    }

    @Test
    @SecureTestContext(username = "accountbalance",customerId = "201635682", profileId = "5393", jobId = "testing123", jobRole = "ADVISER")
    public void whenLoadAccountBalancesMap() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        List<AccountKey> accountKeyList = Arrays.asList(AccountKey.valueOf("71319"),
                AccountKey.valueOf("71335"), AccountKey.valueOf("71353"));
        Map<AccountKey,AccountBalance> accountBalancesMap = avaloqAccountIntegrationService.loadAccountBalancesMap(accountKeyList, serviceErrors);
          assertThat(accountBalancesMap.size(), equalTo(3));
        assertThat(accountBalancesMap.get(AccountKey.valueOf("71319")).getAvailableCash(), is(BigDecimal.valueOf(99.28)));
        assertThat(accountBalancesMap.get(AccountKey.valueOf("71335")).getAvailableCash(), is(BigDecimal.valueOf(151.08)));
        assertThat(accountBalancesMap.get(AccountKey.valueOf("71353")).getAvailableCash(), is(BigDecimal.valueOf(50.6)));
        assertThat(serviceErrors.hasErrors(), equalTo(false));
    }
}
