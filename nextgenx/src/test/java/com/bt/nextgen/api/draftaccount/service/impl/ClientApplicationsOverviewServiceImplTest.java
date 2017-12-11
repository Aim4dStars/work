package com.bt.nextgen.api.draftaccount.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bt.nextgen.api.account.v3.util.AccountProductsHelper;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userprofile.JobProfileImpl;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.draftaccount.model.ClientOverviewDto;
import com.bt.nextgen.core.security.profile.InvestorProfileService;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.account.AccountBalanceImpl;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.AccountSubType;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.accountactivation.ApplicationDocumentImpl;
import com.btfin.panorama.core.security.avaloq.accountactivation.AssociatedPerson;
import com.bt.nextgen.service.avaloq.accountactivation.AssociatedPersonImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.client.ClientIdentifier;
import com.bt.nextgen.service.avaloq.client.ClientIdentifierImpl;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.integration.account.AccountBalance;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.accountactivation.AccActivationIntegrationService;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientApplicationsOverviewServiceImplTest {

    @Mock
    private AccActivationIntegrationService accActivationIntegrationService;

    @Mock
    private InvestorProfileService profileService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private BrokerHelperService brokerHelperService;

    @InjectMocks
    private ClientApplicationsOverviewServiceImpl service;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private AccountProductsHelper accountProductsHelper;

    UserProfile activeProfile;


    private UserProfile person;
    private ApplicationDocument document;
    private AssociatedPerson associatedPerson;
    private ProductImpl product;
    private ClientKey clientKey;

    @Before
    public void setUp() throws Exception {
        clientKey = ClientKey.valueOf("123333");
        person = createPerson(clientKey);
        document = new ApplicationDocumentImpl();
        associatedPerson = createAssociatedPerson(clientKey);
        document.setPersonDetails(Arrays.asList(associatedPerson));
        product = new ProductImpl();
        product.setProductName("MY PRODUCT");
        when(brokerHelperService.getUserExperience(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn(UserExperience.ADVISED);
        when(profileService.getActiveProfile()).thenReturn(person);
        when(productIntegrationService.getProductDetail(any(ProductKey.class), any(ServiceErrors.class))).thenReturn(product);
        when(accActivationIntegrationService
            .loadAccApplicationForPortfolio(any(WrapAccountIdentifier.class), any(ClientKey.class), any(JobRole.class), any(ServiceErrors.class)))
            .thenReturn(associatedPerson);

        activeProfile = getProfile(JobRole.ADVISER, "job id 1", "client1");
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        when(accountProductsHelper.getAccountFeatureKey(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn("advised.individual");
    }

    private AssociatedPerson createAssociatedPerson(ClientKey clientKey) {
        AssociatedPerson person = new AssociatedPersonImpl();
        person.setClientKey(clientKey);
        person.setHasApprovedTnC(false);
        person.setHasToAcceptTnC(true);
        return person;
    }

    @Test
    public void shouldReturnAccountsOfTheFoundClient() {
        BigDecimal availableCash = new BigDecimal(5000);
        BigDecimal portfolioValue = new BigDecimal(2000);
        WrapAccount wrapAccount = createWrapAccountWithBalance(AccountStatus.ACTIVE, availableCash, portfolioValue,
            AccountStructureType.Individual, null);
        List<ClientOverviewDto> result = service.findAll(new ServiceErrorsImpl());
        assertThat(result.size(), is(1));
        ClientOverviewDto clientOverviewDto = result.get(0);
        assertThat(clientOverviewDto.getAccountNumber(), is(wrapAccount.getAccountNumber()));
        assertThat(EncodedString.toPlainText(clientOverviewDto.getAccountId()), is(wrapAccount.getAccountKey().getId()));
        assertThat(EncodedString.toPlainText(clientOverviewDto.getEncodedAccountNumber()), is(wrapAccount.getAccountNumber()));
        assertThat(clientOverviewDto.getAccountName(), is(wrapAccount.getAccountName()));
        assertThat(clientOverviewDto.getAccountStatus(), is(ClientOverviewDto.Status.ACTIVE.toString()));
        assertThat(clientOverviewDto.getAccountType(), is(wrapAccount.getAccountStructureType().name()));
        assertNull(clientOverviewDto.getSuperAccountSubType());
        assertThat(EncodedString.toPlainText(clientOverviewDto.getClientId()), is(clientKey.getId()));
        assertThat(clientOverviewDto.getProduct(), is(product.getProductName()));
        assertThat(clientOverviewDto.getAvailableCash(), is(availableCash));
        assertThat(clientOverviewDto.getPortfolioValue(), is(portfolioValue));
        assertThat(clientOverviewDto.isDirect(), is(false));
        assertThat(clientOverviewDto.getUserExperience(), is(UserExperience.ADVISED.getDisplayName()));
        assertThat(clientOverviewDto.getTypeId(), is("advised.individual"));
    }

    @Test
    public void shouldReturnDirectFlagForDirectUserExp() {
        when(brokerHelperService.getUserExperience(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn(UserExperience.DIRECT);

        WrapAccount wrapAccount = createWrapAccountWithBalance(AccountStatus.ACTIVE, new BigDecimal(2000), new BigDecimal(2000),
            AccountStructureType.Individual, null);
        List<ClientOverviewDto> result = service.findAll(new ServiceErrorsImpl());

        assertThat(result.size(), is(1));
        ClientOverviewDto clientOverviewDto = result.get(0);
        assertThat(clientOverviewDto.getAccountNumber(), is(wrapAccount.getAccountNumber()));
        assertThat(EncodedString.toPlainText(clientOverviewDto.getAccountId()), is(wrapAccount.getAccountKey().getId()));
        assertThat(EncodedString.toPlainText(clientOverviewDto.getEncodedAccountNumber()), is(wrapAccount.getAccountNumber()));
        assertThat(clientOverviewDto.isDirect(), is(true));
        assertThat(clientOverviewDto.getUserExperience(), is(UserExperience.DIRECT.getDisplayName()));
        assertThat(clientOverviewDto.getTypeId(), is("advised.individual"));
    }

    @Test
    public void shouldNotReturnAccountsOfTheFoundClientIfAccountIsDiscarded() {
        BigDecimal availableCash = new BigDecimal(5000);
        BigDecimal portfolioValue = new BigDecimal(2000);
        createWrapAccountWithBalance(AccountStatus.DISCARD, availableCash, portfolioValue, AccountStructureType.Individual, null);
        List<ClientOverviewDto> result = service.findAll(new ServiceErrorsImpl());
        assertThat(result, empty());
    }

    @Test
    public void shouldSetStatusToActiveWhenAccountStatusIsActive() {
        createWrapAccount(AccountStatus.ACTIVE, AccountStructureType.Individual, null);
        List<ClientOverviewDto> result = service.findAll(new ServiceErrorsImpl());
        ClientOverviewDto clientOverviewDto = result.get(0);
        assertThat(clientOverviewDto.getAccountStatus(), is(ClientOverviewDto.Status.ACTIVE.toString()));
    }

    @Test
    public void shouldSetStatusToCloseWhenAccountStatusIsClose() {
        createWrapAccount(AccountStatus.CLOSE, AccountStructureType.Individual, null);
        List<ClientOverviewDto> result = service.findAll(new ServiceErrorsImpl());
        ClientOverviewDto clientOverviewDto = result.get(0);
        assertThat(clientOverviewDto.getAccountStatus(), is(ClientOverviewDto.Status.CLOSED.toString()));
    }

    @Test
    public void shouldSetStatusToCloseWhenAccountStatusIsPendClose() {
        createWrapAccount(AccountStatus.PEND_CLOSE, AccountStructureType.Individual, null);
        List<ClientOverviewDto> result = service.findAll(new ServiceErrorsImpl());
        ClientOverviewDto clientOverviewDto = result.get(0);
        assertThat(clientOverviewDto.getAccountStatus(), is(ClientOverviewDto.Status.CLOSED.toString()));
    }

    @Test
    public void shouldSetStatusToCloseWhenAccountStatusIsPendCloseFees() {
        createWrapAccount(AccountStatus.PEND_CLOSE_FEES, AccountStructureType.Individual, null);
        List<ClientOverviewDto> result = service.findAll(new ServiceErrorsImpl());
        ClientOverviewDto clientOverviewDto = result.get(0);
        assertThat(clientOverviewDto.getAccountStatus(), is(ClientOverviewDto.Status.CLOSED.toString()));
    }

    @Test
    public void shouldSetStatusToCloseWhenAccountStatusIsPendCloseCashout() {
        createWrapAccount(AccountStatus.PEND_CLOSE_CASHOUT, AccountStructureType.Individual, null);
        List<ClientOverviewDto> result = service.findAll(new ServiceErrorsImpl());
        ClientOverviewDto clientOverviewDto = result.get(0);
        assertThat(clientOverviewDto.getAccountStatus(), is(ClientOverviewDto.Status.CLOSED.toString()));
    }

    @Test
    public void shouldSetStatusToCloseWhenAccountStatusIsPendCloseIntr() {
        createWrapAccount(AccountStatus.PEND_CLOSE_INTR, AccountStructureType.Individual, null);
        List<ClientOverviewDto> result = service.findAll(new ServiceErrorsImpl());
        ClientOverviewDto clientOverviewDto = result.get(0);
        assertThat(clientOverviewDto.getAccountStatus(), is(ClientOverviewDto.Status.CLOSED.toString()));
    }

    @Test
    public void shouldSetStatusToPendingApprovalWhenAccountIsPendingAndUserIsApproverAndHasApproved() {
        createWrapAccount(AccountStatus.PEND_OPN, AccountStructureType.Individual, null);
        associatedPerson.setHasToAcceptTnC(true);
        associatedPerson.setHasApprovedTnC(true);
        List<ClientOverviewDto> result = service.findAll(new ServiceErrorsImpl());
        ClientOverviewDto clientOverviewDto = result.get(0);
        assertThat(clientOverviewDto.getAccountStatus(), is(ClientOverviewDto.Status.PENDING_APPROVAL.toString()));
    }

    @Test
    public void shouldSetStatusToPendingYourApprovalWhenAccountIsPendingAndUserIsApproverAndHasNotApproved() {
        createWrapAccount(AccountStatus.PEND_OPN, AccountStructureType.Individual, null);
        associatedPerson.setHasToAcceptTnC(true);
        associatedPerson.setHasApprovedTnC(false);
        List<ClientOverviewDto> result = service.findAll(new ServiceErrorsImpl());
        ClientOverviewDto clientOverviewDto = result.get(0);
        assertThat(clientOverviewDto.getAccountStatus(), is(ClientOverviewDto.Status.PENDING_YOUR_APPROVAL.toString()));
    }

    @Test
    public void shouldNotReturnAccountsOfTheFoundClientIfAccountIsInCompanySetupInProgressState() {
        BigDecimal availableCash = new BigDecimal(5000);
        BigDecimal portfolioValue = new BigDecimal(2000);
        createWrapAccountWithBalance(AccountStatus.COMPANY_SETUP_IN_PROGRESS, availableCash, portfolioValue,
            AccountStructureType.Individual, null);
        List<ClientOverviewDto> result = service.findAll(new ServiceErrorsImpl());
        assertThat(result, empty());
    }

    @Test
    public void testAccountTypeDisplay_forIndividualAccount() {
        createWrapAccount(AccountStatus.PEND_OPN, AccountStructureType.Individual, null);
        List<ClientOverviewDto> result = service.findAll(new ServiceErrorsImpl());
        assertEquals(result.get(0).getAccountType(), "Individual");
    }

    @Test
    public void testAccountTypeDisplay_forSuperAccumulationAccount() {
        createWrapAccount(AccountStatus.PEND_OPN, AccountStructureType.SUPER, AccountSubType.ACCUMULATION);
        List<ClientOverviewDto> result = service.findAll(new ServiceErrorsImpl());
        assertEquals(result.get(0).getAccountType(), "Super");
    }

    @Test
    public void testAccountTypeDisplay_forSuperPensionAccount() {
        createWrapAccount(AccountStatus.PEND_OPN, AccountStructureType.SUPER, AccountSubType.PENSION);
        List<ClientOverviewDto> result = service.findAll(new ServiceErrorsImpl());
        assertEquals(result.get(0).getAccountType(), "Pension");
    }

    private UserProfile createPerson(ClientKey clientKey) {
        UserInformationImpl person = new UserInformationImpl();
        person.setClientKey(clientKey);
        return new UserProfileAdapterImpl(person, null);
    }

    private WrapAccountImpl createWrapAccount(AccountStatus status, AccountStructureType accountType, AccountSubType subAccountType) {
        return createWrapAccountWithBalance(status, new BigDecimal(5000), new BigDecimal(2000), accountType, subAccountType);
    }

    private WrapAccountImpl createWrapAccountWithBalance(AccountStatus status, BigDecimal availableCash, BigDecimal portfolioValue,
                                                         AccountStructureType accountType, AccountSubType subAccountType) {
        WrapAccountImpl account = getWrapAccount(status, accountType, subAccountType);

        when(accountService.loadOnlineWrapAccounts(any(ServiceErrors.class))).thenReturn(Arrays.asList((WrapAccount) account));

        AccountBalanceImpl accountBalance = new AccountBalanceImpl();
        accountBalance.setAvailableCash(availableCash);
        accountBalance.setPortfolioValue(portfolioValue);
        accountBalance.setKey(AccountKey.valueOf(account.getAccountKey().getId()));
        when(accountService.loadAccountBalances(any(ServiceErrors.class))).thenReturn(Arrays.asList((AccountBalance) accountBalance));
        return account;
    }

    private WrapAccountImpl getWrapAccount(AccountStatus status, AccountStructureType accountType, AccountSubType subAccountType) {
        WrapAccountImpl account = new WrapAccountImpl();
        account.setAccountKey(AccountKey.valueOf("2222"));
        account.setAccountStatus(status);
        account.setAccountNumber("333333");
        account.setAccountName("AccName");
        account.setAccountStructureType(accountType);
        account.setSuperAccountSubType(subAccountType);
        account.setProductKey(ProductKey.valueOf("PRODUCT_KEY"));
        List<ClientIdentifier> list = new ArrayList<ClientIdentifier>();
        list.add(new ClientIdentifierImpl(clientKey.getId()));
        account.setOwnerClientKeys(list);
        return account;
    }

    private UserProfile getProfile(final JobRole role, final String jobId, final String customerId) {
        UserInformationImpl user = new UserInformationImpl();
        user.setClientKey(ClientKey.valueOf(customerId));

        JobProfileImpl job = new JobProfileImpl();
        job.setJobRole(role);
        job.setJob(JobKey.valueOf(jobId));

        UserProfile profile = new UserProfileAdapterImpl(user, job);
        return profile;
    }
}