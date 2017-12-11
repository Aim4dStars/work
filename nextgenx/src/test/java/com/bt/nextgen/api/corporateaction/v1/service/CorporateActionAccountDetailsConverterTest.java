package com.bt.nextgen.api.corporateaction.v1.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.integration.domain.InvestorDetail;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSavedDetails;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionConverterFactory;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionResponseConverterService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountBalance;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccountParticipationStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionTransactionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionTransactionStatus;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.domain.RegisteredEntity;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.bt.nextgen.service.integration.userinformation.ClientKey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionAccountDetailsConverterTest {
    @InjectMocks
    private CorporateActionAccountDetailsConverter converter;

    @Mock
    private CorporateActionAccountHelper corporateActionAccountHelper;

    @Mock
    private CorporateActionConverterFactory corporateActionConverterFactory;

    @Mock
    private CorporateActionResponseConverterService responseConverterService;

    @Mock
    private CorporateActionHelper corporateActionHelper;

    @Mock
    private CorporateActionContext context;

    @Mock
    private CorporateActionDetails corporateActionDetails;

    @Mock
    private CorporateActionSupplementaryDetails corporateActionSupplementaryDetails;

    @Mock
    private CorporateActionAccount corporateActionAccount;

    @Mock
    private CorporateActionSavedDetails corporateActionSavedDetails;

    @Mock
    private CorporateActionClientAccountDetails corporateActionClientAccountDetails;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private WrapAccountDetail wrapAccount;

    @Mock
    private Client owner;

    @Mock
    private RegisteredEntity registeredEntity;

    @Mock
    private InvestorDetail linkedClient;

    @Mock
    private ClientKey linkedClientKey;

    private Map<AccountKey, WrapAccount> accountsMap = new HashMap<>();

    private Map<AccountKey, AccountBalance> accountBalancesMap = new HashMap<>();

    @Before
    public void setup() {
        // Accounts
        when(wrapAccount.getAccountKey()).thenReturn(AccountKey.valueOf("0"));
        when(wrapAccount.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        when(wrapAccount.getAccountName()).thenReturn("Account name");
        when(wrapAccount.getAccountNumber()).thenReturn("1234567");

        when(corporateActionClientAccountDetails.getAccountsMap()).thenReturn(accountsMap);

        ClientKey ownerClientKey = mock(ClientKey.class);
        when(ownerClientKey.getId()).thenReturn("10");
        when(owner.getClientKey()).thenReturn(ownerClientKey);
        when(owner.getFullName()).thenReturn("Client name");
        when(wrapAccount.getAccountOwners()).thenReturn(Arrays.asList(ownerClientKey));
        when(wrapAccount.getOwners()).thenReturn(Arrays.asList(owner));

        when(wrapAccount.getPrimaryContactPersonId()).thenReturn(ownerClientKey);

        when(corporateActionAccount.getAccountId()).thenReturn("0");
        when(corporateActionAccount.getPositionId()).thenReturn("0");

        accountsMap.put(wrapAccount.getAccountKey(), wrapAccount);

        when(corporateActionSupplementaryDetails.getClientAccountDetails()).thenReturn(corporateActionClientAccountDetails);
        when(accountIntegrationService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(wrapAccount);

        linkedClientKey = mock(ClientKey.class);
        when(linkedClientKey.getId()).thenReturn("55");
        when(linkedClient.getClientKey()).thenReturn(linkedClientKey);
        when(linkedClient.getFullName()).thenReturn("Linked client full name");

        ClientKey registeredEntityKey = mock(ClientKey.class);
        when(registeredEntityKey.getId()).thenReturn("30");
        when(registeredEntity.getClientKey()).thenReturn(registeredEntityKey);
        when(registeredEntity.getFullName()).thenReturn("Registered entity client name");
        when(registeredEntity.getLinkedClients()).thenReturn(Arrays.asList(linkedClient));

        // Account balances
        AccountBalance accountBalance = mock(AccountBalance.class);
        when(accountBalance.getPortfolioValue()).thenReturn(BigDecimal.TEN);
        when(accountBalance.getAvailableCash()).thenReturn(BigDecimal.TEN);
        accountBalancesMap.put(wrapAccount.getAccountKey(), accountBalance);

        when(corporateActionClientAccountDetails.getAccountBalancesMap()).thenReturn(accountBalancesMap);

        // Other services
        when(corporateActionAccountHelper.getAdviserName(any(BrokerKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn("Adviser name");
        when(corporateActionAccountHelper.getPortfolioName(any(ProductKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn("Portfolio name");

        when(corporateActionHelper.getAccountTypeDescription(any(WrapAccount.class))).thenReturn("Super");
        when(corporateActionAccountHelper.getPreferredAddress(anyListOf(Address.class)))
                .thenReturn("12 Bramdean Crescent, Canning Vale, WA 6155");
        when(corporateActionAccountHelper.getPreferredPhone(anyListOf(Phone.class))).thenReturn("(01) 2345 6789");
        when(corporateActionAccountHelper.getPreferredEmail(anyListOf(Email.class))).thenReturn("me@email.com");

        when(corporateActionConverterFactory.getResponseConverterService(any(CorporateActionDetails.class)))
                .thenReturn(responseConverterService);

        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);
        when(context.isDealerGroupOrInvestmentManager()).thenReturn(Boolean.FALSE);
    }

    @Test
    public void testCreateAccountDetailsDto_whenThereIsNoAccount_thenReturnNull() {
        when(accountIntegrationService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(null);

        CorporateActionAccountDetailsDto accountDetailsDto = converter.createAccountDetailsDto(context,
                corporateActionSupplementaryDetails, corporateActionAccount, corporateActionSavedDetails, null);

        assertNull(accountDetailsDto);
    }

    @Test
    public void testCreateAccountDetailsDto_whenThereIsAMatchingAccount_thenReturnPopulatedDto() {
        when(corporateActionAccount.getEligibleQuantity()).thenReturn(BigDecimal.valueOf(10));
        when(corporateActionAccount.getAvailableQuantity()).thenReturn(BigDecimal.valueOf(10));

        CorporateActionAccountDetailsDto accountDetailsDto = converter.createAccountDetailsDto(context,
                corporateActionSupplementaryDetails, corporateActionAccount, corporateActionSavedDetails, null);

        verify(responseConverterService).setCorporateActionAccountDetailsDtoParams(any(CorporateActionContext.class),
                any(corporateActionAccount.getClass()), any(CorporateActionAccountDetailsDtoParams.class));

        assertEquals("1234567", accountDetailsDto.getAccountId());
        assertEquals("0", EncodedString.toPlainText(accountDetailsDto.getAccountKey()));
        assertEquals("Account name", accountDetailsDto.getAccountName());
        assertEquals("Super", accountDetailsDto.getAccountType());
        assertEquals("Adviser name", accountDetailsDto.getAdviserName());
        assertEquals("me@email.com", accountDetailsDto.getClientEmail());
        assertEquals("(01) 2345 6789", accountDetailsDto.getClientPhone());
        assertEquals("Client name", accountDetailsDto.getClientName());
        assertEquals("Portfolio name", accountDetailsDto.getPortfolio());
        assertEquals("0", accountDetailsDto.getPositionId());
        assertEquals("10", accountDetailsDto.getClientId());
        assertEquals(null, accountDetailsDto.getTransactionNumber());
        assertEquals(null, accountDetailsDto.getTransactionDescription());
        assertEquals(null, accountDetailsDto.getTransactionStatus());
        assertEquals(BigDecimal.TEN, accountDetailsDto.getCash());
        assertEquals(CorporateActionAccountParticipationStatus.NOT_SUBMITTED, accountDetailsDto.getElectionStatus());
        assertEquals(CorporateActionAccountParticipationStatus.NOT_SUBMITTED.getId(), accountDetailsDto.getElectionStatus().getId());
        assertEquals(CorporateActionAccountParticipationStatus.NOT_SUBMITTED.getCode(), accountDetailsDto.getElectionStatus().getCode());
        assertEquals((Integer) 10, accountDetailsDto.getHolding());
        assertEquals(null, accountDetailsDto.getOriginalHolding());
        assertEquals(BigDecimal.TEN, accountDetailsDto.getPortfolioValue());
        assertEquals(null, accountDetailsDto.getSavedElections());
        assertEquals(null, accountDetailsDto.getSelectedElections());
        assertEquals(null, accountDetailsDto.getSubmittedElections());
        assertEquals(false, accountDetailsDto.isPendingSell());
        assertEquals(false, accountDetailsDto.isTrusteeApproval());
    }

    @Test
    public void testCreateAccountDetailsDto_whenThereIsAMatchingAccountWithTransaction_thenReturnPopulatedDto() {
        CorporateActionTransactionDetails corporateActionTransactionDetails = mock(CorporateActionTransactionDetails.class);
        when(corporateActionTransactionDetails.getAccountId()).thenReturn("0");
        when(corporateActionTransactionDetails.getPositionId()).thenReturn("0");
        when(corporateActionTransactionDetails.getTransactionNumber()).thenReturn(0);
        when(corporateActionTransactionDetails.getTransactionDescription()).thenReturn("Transaction description");

        when(corporateActionClientAccountDetails.getAccountsMap()).thenReturn(accountsMap);
        when(corporateActionAccount.getElectionStatus()).thenReturn(CorporateActionAccountParticipationStatus.SUBMITTED);
        when(corporateActionSupplementaryDetails.getTransactionDetails()).thenReturn(Arrays.asList(corporateActionTransactionDetails));
        when(corporateActionSupplementaryDetails.getTransactionStatus()).thenReturn(CorporateActionTransactionStatus.PRE_EX_DATE);

        CorporateActionAccountDetailsDto accountDetailsDto = converter.createAccountDetailsDto(context,
                corporateActionSupplementaryDetails, corporateActionAccount, corporateActionSavedDetails, null);

        assertEquals(CorporateActionAccountParticipationStatus.SUBMITTED, accountDetailsDto.getElectionStatus());
        assertEquals((Integer) 0, accountDetailsDto.getTransactionNumber());
        assertEquals("Transaction description", accountDetailsDto.getTransactionDescription());
        assertEquals(CorporateActionTransactionStatus.POST_EX_DATE, accountDetailsDto.getTransactionStatus());
    }

    @Test
    public void testCreateAccountDetailsDto_whenThereIsAMatchingAccountAndThereIsAvailableCash_thenPopulateCashFromAccount() {
        when(corporateActionAccount.getEligibleQuantity()).thenReturn(BigDecimal.valueOf(10));
        when(corporateActionAccount.getAvailableCash()).thenReturn(BigDecimal.ONE);

        CorporateActionAccountDetailsDto accountDetailsDto = converter.createAccountDetailsDto(context,
                corporateActionSupplementaryDetails, corporateActionAccount, corporateActionSavedDetails, null);

        assertEquals(BigDecimal.ONE, accountDetailsDto.getCash());
    }

    @Test
    public void
    testCreateAccountDetailsDto_whenThereIsAMatchingAccountAndAvailableQuantityIsLessThanEligibleQuantity_thenSetPendingSellFlagToTrue() {
        when(corporateActionAccount.getEligibleQuantity()).thenReturn(BigDecimal.valueOf(10));
        when(corporateActionAccount.getAvailableQuantity()).thenReturn(BigDecimal.valueOf(5));

        CorporateActionAccountDetailsDto accountDetailsDto = converter.createAccountDetailsDto(context,
                corporateActionSupplementaryDetails, corporateActionAccount, corporateActionSavedDetails, null);

        assertEquals(true, accountDetailsDto.isPendingSell());
    }

    @Test
    public void
    testCreateAccountDetailsDto_whenThereIsAMatchingAccountAndAvailableQuantityOrEligibleQuantityIsNull_thenSetPendingSellFlagToFalse() {
        when(corporateActionAccount.getEligibleQuantity()).thenReturn(null);
        when(corporateActionAccount.getAvailableQuantity()).thenReturn(BigDecimal.valueOf(5));

        CorporateActionAccountDetailsDto accountDetailsDto = converter.createAccountDetailsDto(context,
                corporateActionSupplementaryDetails, corporateActionAccount, corporateActionSavedDetails, null);

        assertEquals(false, accountDetailsDto.isPendingSell());

        when(corporateActionAccount.getAvailableQuantity()).thenReturn(null);

        accountDetailsDto = converter.createAccountDetailsDto(context, corporateActionSupplementaryDetails, corporateActionAccount,
                corporateActionSavedDetails, null);

        assertEquals(false, accountDetailsDto.isPendingSell());

        when(corporateActionAccount.getEligibleQuantity()).thenReturn(null);
        when(corporateActionAccount.getAvailableQuantity()).thenReturn(null);

        accountDetailsDto = converter.createAccountDetailsDto(context, corporateActionSupplementaryDetails, corporateActionAccount,
                corporateActionSavedDetails, null);

        assertEquals(false, accountDetailsDto.isPendingSell());
    }

    @Test
    public void testCreateAccountDetailsDto_whenThereIsAMatchingAccountAndItIsASuperAccount_thenSetTrusteeApprovalFlagToTrue() {
        when(wrapAccount.getAccountStructureType()).thenReturn(AccountStructureType.SUPER);

        CorporateActionAccountDetailsDto accountDetailsDto = converter.createAccountDetailsDto(context,
                corporateActionSupplementaryDetails, corporateActionAccount, corporateActionSavedDetails, null);

        assertEquals(true, accountDetailsDto.isTrusteeApproval());
    }

    @Test
    public void testCreateAccountDetailsDto_whenOwnerIsNotPrimary_thenUsePrimaryInvestorDetail() {
        when(wrapAccount.getPrimaryContactPersonId()).thenReturn(linkedClientKey);

        List<Client> registeredEntities = new ArrayList<>();
        registeredEntities.add(registeredEntity);

        when(wrapAccount.getOwners()).thenReturn(registeredEntities);

        CorporateActionAccountDetailsDto accountDetailsDto = converter.createAccountDetailsDto(context,
                corporateActionSupplementaryDetails, corporateActionAccount, corporateActionSavedDetails, null);

        assertEquals("Linked client full name", accountDetailsDto.getClientName());
    }

    @Test
    public void testCreateAccountDetailsDto_whenOwnerIsNotPrimaryAndNoLinkedClients_thenUsePrimaryRelatedPersonDetail() {
        when(wrapAccount.getPrimaryContactPersonId()).thenReturn(linkedClientKey);

        when(registeredEntity.getLinkedClients()).thenReturn(null);

        List<ClientDetail> clientDetailList = new ArrayList<>();
        clientDetailList.add(linkedClient);

        when(registeredEntity.getRelatedPersons()).thenReturn(clientDetailList);

        List<Client> registeredEntities = new ArrayList<>();
        registeredEntities.add(registeredEntity);

        when(wrapAccount.getOwners()).thenReturn(registeredEntities);

        CorporateActionAccountDetailsDto accountDetailsDto = converter.createAccountDetailsDto(context,
                corporateActionSupplementaryDetails, corporateActionAccount, corporateActionSavedDetails, null);

        assertEquals("Linked client full name", accountDetailsDto.getClientName());
    }

    @Test
    public void testCreateAccountDetailsDto_whenOwnerIsNotPrimaryAndNoLinkedOrRelatedClients_thenNoContactDetailsExpected() {
        when(wrapAccount.getPrimaryContactPersonId()).thenReturn(linkedClientKey);

        when(registeredEntity.getLinkedClients()).thenReturn(null);
        when(registeredEntity.getRelatedPersons()).thenReturn(null);

        List<Client> registeredEntities = new ArrayList<>();
        registeredEntities.add(registeredEntity);

        when(wrapAccount.getOwners()).thenReturn(registeredEntities);

        CorporateActionAccountDetailsDto accountDetailsDto = converter.createAccountDetailsDto(context,
                corporateActionSupplementaryDetails, corporateActionAccount, corporateActionSavedDetails, null);

        assertEquals("Account name", accountDetailsDto.getClientName());
        assertNull(accountDetailsDto.getClientPhone());
        assertNull(accountDetailsDto.getClientAddress());
        assertNull(accountDetailsDto.getClientEmail());
    }

    @Test
    public void testCreateAccountDetailsDto_whenOwnerIsNotPrimaryAndIsOrganisation_thenUseAccountNameForClientName() {
        when(wrapAccount.getPrimaryContactPersonId()).thenReturn(linkedClientKey);
        when(wrapAccount.getAccountStructureType()).thenReturn(AccountStructureType.SMSF);

        List<Client> registeredEntities = new ArrayList<>();
        registeredEntities.add(registeredEntity);

        when(wrapAccount.getOwners()).thenReturn(registeredEntities);

        CorporateActionAccountDetailsDto accountDetailsDto = converter.createAccountDetailsDto(context,
                corporateActionSupplementaryDetails, corporateActionAccount, corporateActionSavedDetails, null);

        assertEquals("Account name", accountDetailsDto.getClientName());
    }

    @Test
    public void testCreateAccountDetailsDto_whenOwnerIsNotPrimaryAndOwnerIsNotRegisteredEntity_thenNoContactDetailsExpected() {
        ClientKey bogusClientKey = mock(ClientKey.class);
        when(bogusClientKey.getId()).thenReturn("666");

        when(wrapAccount.getPrimaryContactPersonId()).thenReturn(bogusClientKey);

        CorporateActionAccountDetailsDto accountDetailsDto = converter.createAccountDetailsDto(context,
                corporateActionSupplementaryDetails, corporateActionAccount, corporateActionSavedDetails, null);

        assertEquals("Account name", accountDetailsDto.getClientName());
        assertNull(accountDetailsDto.getClientPhone());
        assertNull(accountDetailsDto.getClientAddress());
        assertNull(accountDetailsDto.getClientEmail());
    }

    @Test
    public void testCreateAccountDetailsDto_whenThereIsAMatchingAccountAndUseApproverIsTrueButNoApprovers_thenUseApproverClientKey() {
        when(wrapAccount.getApprovers()).thenReturn(new ArrayList<ClientKey>());
        when(wrapAccount.getAccountStructureType()).thenReturn(AccountStructureType.Company);

        CorporateActionAccountDetailsDto accountDetailsDto = converter.createAccountDetailsDto(context,
                corporateActionSupplementaryDetails, corporateActionAccount, corporateActionSavedDetails, null);

        assertEquals("10", accountDetailsDto.getClientId());
    }

    @Test
    public void
    testCreateAccountDetailsDto_whenThereIsAMatchingAccountAndItIsADealerGroupOrInvestmentManager_thenTheClientIdIsValidAndNoContactDetailsPopulated() {
        when(wrapAccount.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        when(context.isDealerGroupOrInvestmentManager()).thenReturn(Boolean.TRUE);

        CorporateActionAccountDetailsDto accountDetailsDto = converter.createAccountDetailsDto(context,
                corporateActionSupplementaryDetails, corporateActionAccount, corporateActionSavedDetails, null);

        assertEquals("10", accountDetailsDto.getClientId());
        assertNull(accountDetailsDto.getClientPhone());
        assertNull(accountDetailsDto.getClientEmail());
        assertNull(accountDetailsDto.getClientAddress());
    }

    @Test(expected = NoSuchElementException.class)
    public void testCreateAccountDetailsDto_whenThereIsAMatchingAccountAnNoAccountOwners_thenThrowNoSuchElementException() {
        when(wrapAccount.getAccountOwners()).thenReturn(new ArrayList<ClientKey>());
        when(wrapAccount.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        when(context.getBrokerPositionId()).thenReturn(null);

        converter.createAccountDetailsDto(context, corporateActionSupplementaryDetails, corporateActionAccount,
                corporateActionSavedDetails, null);
    }
}
