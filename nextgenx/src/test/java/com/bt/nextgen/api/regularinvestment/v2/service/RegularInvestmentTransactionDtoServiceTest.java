package com.bt.nextgen.api.regularinvestment.v2.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.account.v2.model.BankAccountDto;
import com.bt.nextgen.api.movemoney.v2.model.DepositDto;
import com.bt.nextgen.api.movemoney.v2.service.DepositDtoService;
import com.bt.nextgen.api.regularinvestment.v2.model.RegularInvestmentDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.MoneyAccountIdentifierImpl;
import com.bt.nextgen.service.avaloq.regularinvestment.RegularInvestmentTransactionImpl;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.btfin.panorama.service.integration.RecurringFrequency;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.movemoney.RecurringDepositDetails;
import com.bt.nextgen.service.integration.regularinvestment.RIPRecurringFrequency;
import com.bt.nextgen.service.integration.regularinvestment.RIPStatus;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestmentDelegateService;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestmentTransaction;
import com.bt.nextgen.web.controller.cash.util.Attribute;

@RunWith(MockitoJUnitRunner.class)
public class RegularInvestmentTransactionDtoServiceTest {

    @InjectMocks
    private RegularInvestmentTransactionDtoServiceImpl regularInvestmentService;

    @Mock
    private DepositDtoService depositService;

    @Mock
    private RegularInvestmentDelegateService delegate;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private AccountHelper accountHelper;

    @Mock
    private DepositDtoHelper depositHelper;

    private List<RegularInvestmentTransaction> ripTransactions;

    private RegularInvestmentTransactionImpl ripTransaction;

    private RegularInvestmentTransactionImpl ripTransactionWithoutDD;

    private MoneyAccountIdentifier moneyAccIdentifier;

    private WrapAccountDetail accountDetail;

    @Before
    public void setup() throws Exception {
        ripTransaction = new RegularInvestmentTransactionImpl();
        ripTransaction.setAccountKey("1234");
        ripTransaction.setCurrExecStatus(RIPStatus.ACTIVE);
        ripTransaction.setDdAmount(new BigDecimal("10000"));
        ripTransaction.setDdFirstExecDate(new DateTime());
        ripTransaction.setDdFrequency(RecurringFrequency.Monthly);
        ripTransaction.setOrderGroupId("111111");
        ripTransaction.setPayerAccountId("123456");
        ripTransaction.setPayerAccountName("PayerName");
        ripTransaction.setPayerBSB("123456");
        ripTransaction.setRefDocId("ddref123");
        ripTransaction.setRipAmount(new BigDecimal("10000"));
        ripTransaction.setRipCashAccountId("120002832");
        ripTransaction.setRipFirstExecDate(new DateTime());
        ripTransaction.setRipStatus(RIPStatus.ACTIVE);
        ripTransaction.setRipFrequency(RIPRecurringFrequency.Monthly);
        ripTransaction.setRipNextExecDate(new DateTime());
        ripTransaction.setTransactionDate(new DateTime());

        DateTime lastUpdateDate = DateTime.now();
        ripTransaction.setLastUpdateDate(lastUpdateDate);
        ripTransaction.setOwnerName("ownerName");
        ripTransaction.setOrderType("orderType");
        ripTransaction.setDescription("description");
        ripTransaction.setRipCurrExecDate(lastUpdateDate);
        ripTransaction.setRipLastExecDate(lastUpdateDate.minusMonths(1));
        ripTransaction.setDdNextExecDate(lastUpdateDate.plusMonths(1));
        ripTransaction.setDdLastExecDate(lastUpdateDate);

        ripTransactions = new ArrayList<>();

        ripTransactionWithoutDD = new RegularInvestmentTransactionImpl();
        ripTransactionWithoutDD.setAccountKey("1234");
        ripTransactionWithoutDD.setCurrExecStatus(RIPStatus.ACTIVE);
        ripTransactionWithoutDD.setOrderGroupId("111111");
        ripTransactionWithoutDD.setRipAmount(new BigDecimal("10000"));
        ripTransactionWithoutDD.setRipCashAccountId("120002832");
        ripTransactionWithoutDD.setRipFirstExecDate(new DateTime());
        ripTransactionWithoutDD.setRipStatus(RIPStatus.ACTIVE);
        ripTransactionWithoutDD.setRipFrequency(RIPRecurringFrequency.Monthly);
        ripTransactionWithoutDD.setRipNextExecDate(lastUpdateDate.plusMonths(2));
        ripTransactionWithoutDD.setTransactionDate(lastUpdateDate);
        ripTransactionWithoutDD.setRipLastExecDate(lastUpdateDate.plusMonths(4));

        ripTransactions.add(ripTransaction);
        ripTransactions.add(ripTransactionWithoutDD);

        ripTransactions.add(ripTransaction);

        moneyAccIdentifier = new MoneyAccountIdentifierImpl();
        moneyAccIdentifier.setMoneyAccountId("1234");

        accountDetail = Mockito.mock(WrapAccountDetail.class);
        Mockito.when(accountDetail.getAdviserKey()).thenReturn(BrokerKey.valueOf("brokerKey"));
        Mockito.when(accountDetail.isOpen()).thenReturn(false);
        Mockito.when(accountDetail.getAdminFeeRate()).thenReturn(new BigDecimal("9.98"));
        Mockito.when(accountDetail.isHasMinCash()).thenReturn(false);
    }

    @Test
    public void testSearch_ripWithDD() {
        Mockito.when(delegate.loadRegularInvestments(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class),
                Mockito.anyString())).thenReturn(ripTransactions);
        Mockito.when(depositService.getMoneyAccountIdentifier(Mockito.any(DepositDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(moneyAccIdentifier);
        Mockito.when(
                accountIntegrationService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(accountDetail);
        BankAccountDto bankDto = new BankAccountDto();
        bankDto.setAccountNumber("accountNumber");
        bankDto.setBsb("BSB");
        bankDto.setName("Name");
        Mockito.when(accountHelper.getBankAccountDetails(Mockito.anyString(), Mockito.any(ServiceErrors.class)))
                .thenReturn(bankDto);

        Mockito.when(depositHelper.constructDepositDto(Mockito.any(RegularInvestmentTransaction.class),
                Mockito.any(RecurringDepositDetails.class), Mockito.any(DepositDto.class))).thenReturn(new DepositDto());

        List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        String accountId = EncodedString.fromPlainText("accountId").toString();
        ApiSearchCriteria criteria = new ApiSearchCriteria(Attribute.ACCOUNT_ID, accountId);
        criteriaList.add(criteria);
        List<RegularInvestmentDto> ripDtos = regularInvestmentService.search(criteriaList, null);
        Assert.assertFalse(ripDtos.isEmpty());
        Assert.assertEquals(ripDtos.size(), ripTransactions.size());
        Assert.assertEquals(EncodedString.toPlainText(ripDtos.get(0).getAccountKey().getAccountId()),
                ripTransactions.get(0).getAccountKey());
        Assert.assertEquals(ripDtos.get(0).getInvestmentStartDate(), ripTransactions.get(0).getRipFirstExecDate());
        Assert.assertEquals(ripDtos.get(0).getInvestmentEndDate(), ripTransactions.get(0).getDDLastExecDate());
        Assert.assertEquals(ripDtos.get(0).getNextDueDate(), ripTransactions.get(0).getDDNextExecDate());

    }

    @Test
    public void testSearch_ripWithoutDD() {
        Mockito.when(delegate.loadRegularInvestments(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class),
                Mockito.anyString())).thenReturn(ripTransactions);
        Mockito.when(depositService.getMoneyAccountIdentifier(Mockito.any(DepositDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(moneyAccIdentifier);
        Mockito.when(
                accountIntegrationService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(accountDetail);
        BankAccountDto bankDto = new BankAccountDto();
        bankDto.setAccountNumber("accountNumber");
        bankDto.setBsb("BSB");
        bankDto.setName("Name");
        Mockito.when(accountHelper.getBankAccountDetails(Mockito.anyString(), Mockito.any(ServiceErrors.class)))
                .thenReturn(bankDto);
        List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        String accountId = EncodedString.fromPlainText("accountId").toString();
        ApiSearchCriteria criteria = new ApiSearchCriteria(Attribute.ACCOUNT_ID, accountId);
        criteriaList.add(criteria);
        List<RegularInvestmentDto> ripDtos = regularInvestmentService.search(criteriaList, null);
        Assert.assertFalse(ripDtos.isEmpty());
        Assert.assertEquals(ripDtos.size(), ripTransactions.size());
        Assert.assertEquals(EncodedString.toPlainText(ripDtos.get(1).getAccountKey().getAccountId()),
                ripTransactions.get(1).getAccountKey());
        Assert.assertEquals(ripDtos.get(1).getInvestmentStartDate(), ripTransactions.get(1).getRipFirstExecDate());
        Assert.assertEquals(ripDtos.get(1).getInvestmentEndDate(), ripTransactions.get(1).getRipLastExecDate());
        Assert.assertEquals(ripDtos.get(1).getNextDueDate(), ripTransactions.get(1).getRipNextExecDate());

    }

    @Test
    public void testSuspendedRipStatus_WhenExecStatusFailed() {

        ripTransaction.setCurrExecStatus(RIPStatus.FAILED);
        ripTransaction.setRipStatus(RIPStatus.SUSPENDED);
        Mockito.when(delegate.loadRegularInvestments(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class),
                Mockito.anyString())).thenReturn(ripTransactions);
        Mockito.when(depositService.getMoneyAccountIdentifier(Mockito.any(DepositDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(moneyAccIdentifier);
        Mockito.when(
                accountIntegrationService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(accountDetail);

        BankAccountDto bankDto = new BankAccountDto();
        bankDto.setAccountNumber("accountNumber");
        bankDto.setBsb("BSB");
        bankDto.setName("Name");
        Mockito.when(accountHelper.getBankAccountDetails(Mockito.anyString(), Mockito.any(ServiceErrors.class)))
                .thenReturn(bankDto);
        List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        String accountId = EncodedString.fromPlainText("accountId").toString();
        ApiSearchCriteria criteria = new ApiSearchCriteria(Attribute.ACCOUNT_ID, accountId);
        criteriaList.add(criteria);
        List<RegularInvestmentDto> ripDtos = regularInvestmentService.search(criteriaList, null);
        Assert.assertEquals(RIPStatus.SUSPENDED.getDisplayName(), ripDtos.get(0).getRipStatus());
    }

    @Test
    public void testActiveRipStatus_WhenExecStatusFailed() {

        ripTransaction.setCurrExecStatus(RIPStatus.FAILED);
        ripTransaction.setRipStatus(RIPStatus.ACTIVE);
        Mockito.when(delegate.loadRegularInvestments(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class),
                Mockito.anyString())).thenReturn(ripTransactions);
        Mockito.when(depositService.getMoneyAccountIdentifier(Mockito.any(DepositDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(moneyAccIdentifier);
        Mockito.when(
                accountIntegrationService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(accountDetail);

        BankAccountDto bankDto = new BankAccountDto();
        bankDto.setAccountNumber("accountNumber");
        bankDto.setBsb("BSB");
        bankDto.setName("Name");
        Mockito.when(accountHelper.getBankAccountDetails(Mockito.anyString(), Mockito.any(ServiceErrors.class)))
                .thenReturn(bankDto);
        List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        String accountId = EncodedString.fromPlainText("accountId").toString();
        ApiSearchCriteria criteria = new ApiSearchCriteria(Attribute.ACCOUNT_ID, accountId);
        criteriaList.add(criteria);
        List<RegularInvestmentDto> ripDtos = regularInvestmentService.search(criteriaList, null);
        Assert.assertEquals(RIPStatus.FAILED.getDisplayName(), ripDtos.get(0).getRipStatus());
    }

}
