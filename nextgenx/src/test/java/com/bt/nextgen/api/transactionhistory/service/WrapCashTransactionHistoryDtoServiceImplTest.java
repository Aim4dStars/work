package com.bt.nextgen.api.transactionhistory.service;

import com.bt.nextgen.api.transactionhistory.model.CashTransactionHistoryDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.transactionhistory.TransactionHistoryImpl;
import com.bt.nextgen.service.integration.Origin;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.base.SystemType;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationType;
import com.bt.nextgen.service.integration.transaction.TransactionIntegrationService;
import com.bt.nextgen.service.integration.transactionhistory.BTOrderType;
import com.bt.nextgen.service.integration.transactionhistory.TransactionHistory;
import com.bt.nextgen.service.integration.transactionhistory.TransactionSubType;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by L070353 on 26/07/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class WrapCashTransactionHistoryDtoServiceImplTest {

    @InjectMocks
    private WrapCashTransactionHistoryDtoServiceImpl cashTransactionDtoService;

    @Mock
    @Qualifier("ThirdPartyTransactionIntegrationService")
    private TransactionIntegrationService transactionIntegrationService;

    @Mock
    @Qualifier("cacheAvaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Before
    public void setUp() throws Exception {

        WrapAccountDetail mockedWrapAccountDetail = Mockito.mock(WrapAccountDetail.class);
        Mockito.when(mockedWrapAccountDetail.getAccountStructureType()).thenReturn(AccountStructureType.SMSF);
        Mockito.when(accountService.loadWrapAccountDetail(Mockito.any(com.bt.nextgen.service.integration.account.AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(mockedWrapAccountDetail);
    }

    @Test
    public void testGetCashTransactionHistory() {
        final List<TransactionHistory> transactions = makeTransactionHistories();
        final List<ApiSearchCriteria> criteriaList = makeApiSearchCriteria();
        final List<CashTransactionHistoryDto> dtos;
        int index;

        when(transactionIntegrationService.loadCashTransactionHistory(any(String.class),
                any(DateTime.class),
                any(DateTime.class),
                any(ServiceErrors.class))).thenReturn(transactions);

        dtos = cashTransactionDtoService.search(criteriaList, new ServiceErrorsImpl());

        assertThat("dtos not null", dtos, notNullValue());
        assertThat("dtos size", dtos.size(), equalTo(transactions.size()));

        index = 0;
        checkDto("dto[" + index + "]", dtos.get(index), transactions.get(index), "lump_sum", "Deposit", null, null);

        index = 1;
        checkDto("dto[" + index + "]", dtos.get(index), transactions.get(index), "cannotbemarked", "Reversal - Deposit", null, null);

        index = 2;
        checkDto("dto[" + index + "]", dtos.get(index), transactions.get(index), "cannotbemarked", "Deposit", null, null);

        index = 3;
        checkDto("dto[" + index + "]", dtos.get(index), transactions.get(index), "unmarked", "Payment", null, null);

        index = 4;
        checkDto("dto[" + index + "]", dtos.get(index), transactions.get(index), "", "Contribution", "Personal", null);
    }

    private List<TransactionHistory> makeTransactionHistories() {
        final List<TransactionHistory> retval = new ArrayList<TransactionHistory>();

        // deposit
        retval.add(makeTransactionHistory(CashCategorisationType.LUMP_SUM,
                "69949", new BigDecimal(2344), new BigDecimal(1234),
                new BigDecimal(10000), "Direct Debit Deposit", new DateTime("2015-02-01"), true, "type",
                "198074", new DateTime("2015-01-31"), 1, "inpay", "inpay.inpay#dd", BTOrderType.DEPOSIT,
                "120009311", "262-786", "Tom Demo Bertrand", "12345", "0123", "12345678", "012-003", "deepshikha",
                false, "Optional desc", new DateTime("2015-01-31"), null, "Advice description", Origin.WEB_UI, null, null));

        // reversed deposit
        retval.add(makeTransactionHistory(null, "69950", new BigDecimal(2344), null,
                new BigDecimal(10000), "Cancelled Debit Deposit", new DateTime("2015-02-01"), true, null,
                "198074", new DateTime("2015-01-31"), -1, "inpay", "inpay.inpay#dd", BTOrderType.DEPOSIT,
                "120009311", "262-786", "Tom Demo Bertrand", "12345", "0123", "12345678", "012-003", "Tom",
                false, "Deposit", new DateTime("2015-01-31"), "-36", null, null, null, null));

        // original deposit
        retval.add(makeTransactionHistory(CashCategorisationType.CONTRIBUTION,
                "69950", new BigDecimal(2344), null,
                new BigDecimal(10000), "Debit Deposit", new DateTime("2015-02-01"), true, null,
                "198074", new DateTime("2015-01-31"), 1, "inpay", "inpay.inpay#dd", BTOrderType.DEPOSIT,
                "120009311", "262-786", "Tom Demo Bertrand", "12345", "0123", "12345678", "012-003", "Tom",
                false, "Deposit", new DateTime("2015-01-31"), "36", null, null, null, null));

        // payment
        retval.add(makeTransactionHistory(null, "99808", new BigDecimal(223), new BigDecimal(2333),
                new BigDecimal(20001), "Direct Credit", new DateTime("2015-02-01"), true, "type3",
                "3123", new DateTime("2015-02-27"), 1, "outpay", "outpay.outpay#dd", BTOrderType.PAYMENT,
                "120002211", "262-555", "Tom Demo Bertrand2", "4567", "543", "97876655", "012-029", "deepshikha2",
                false, "Optional desc2", new DateTime("2015-03-31"), null, "Advice description2", Origin.WEB_UI, null, null));

        // contribution
        retval.add(makeTransactionHistory(null, "99808", new BigDecimal(223), new BigDecimal(2333),
                new BigDecimal(20001), "Direct Credit", new DateTime("2015-02-01"), true, "type3",
                "3123", new DateTime("2015-02-27"), 1, "outpay", "outpay.outpay#dd", BTOrderType.CONTRIBUTION,
                "120002211", "262-555", "Tom Demo Bertrand2", "4567", "543", "97876655", "012-029", "deepshikha2",
                false, "Optional desc2", new DateTime("2015-03-31"), null, "Advice description2", Origin.WEB_UI, "Personal", null));

        return retval;
    }

    private List<ApiSearchCriteria> makeApiSearchCriteria() {
        final List<ApiSearchCriteria> criteriaList = new ArrayList<ApiSearchCriteria>();

        criteriaList.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID,
                ApiSearchCriteria.SearchOperation.EQUALS,
                EncodedString.fromPlainText("123").toString(),
                ApiSearchCriteria.OperationType.STRING));

        criteriaList.add(new ApiSearchCriteria(Attribute.START_DATE,
                ApiSearchCriteria.SearchOperation.EQUALS,
                "2017-10-27",
                ApiSearchCriteria.OperationType.DATE));

        criteriaList.add(new ApiSearchCriteria(Attribute.END_DATE,
                ApiSearchCriteria.SearchOperation.EQUALS,
                "2017-10-27",
                ApiSearchCriteria.OperationType.DATE));
        return criteriaList;
    }

    private void checkDto(String infoStr, CashTransactionHistoryDto dto, TransactionHistory expected,
                          String expectedCategory,
                          String expectedDetailDescription, String expectedSubTypeDescription, String expThirdPartySystem) {
        final String debitOrCredit = expected.getAmount().compareTo(ZERO) < 0 ? "DEBIT" : "CREDIT";
        final String cashCategorisationType = expected.getCashCategorisationType() == null ? null
                : expected.getCashCategorisationType().getDisplayCode();

        assertThat(infoStr + " accountId", dto.getAccountId(), equalTo(expected.getAccountId()));
        assertThat(infoStr + " receiptAmount", dto.getReceiptAmount(), equalTo(expected.getNetAmount()));
        assertThat(infoStr + " netAmount", dto.getNetAmount(), equalTo(expected.getAmount()));
        assertThat(infoStr + " balance", dto.getBalance(), equalTo(expected.getBalance()));
        assertThat(infoStr + " descriptionFirst", dto.getDescriptionFirst(), equalTo(expected.getBookingText()));
        assertThat(infoStr + " clearDate", dto.getClearDate(), equalTo(expected.getClearDate()));
        assertThat(infoStr + " cleared", dto.getCleared(), equalTo(expected.isCleared()));
        assertThat(infoStr + " transactionCode", dto.getTransactionCode(), equalTo(expected.getTransactionType()));
        assertThat(infoStr + " docId", dto.getDocId(), equalTo(expected.getDocId()));
        assertThat(infoStr + " effectiveDate", dto.getEffectiveDate(), equalTo(expected.getEffectiveDate()));
        assertThat(infoStr + " evtId", dto.getEvtId(), equalTo(expected.getEvtId()));
        assertThat(infoStr + " metaType", dto.getMetaType(), equalTo(expected.getMetaType()));
        assertThat(infoStr + " orderType", dto.getOrderType(), equalTo(expected.getOrderType()));
        assertThat(infoStr + " transactionType", dto.getTransactionType(), equalTo(expected.getBTOrderType().getDisplayId()));
        assertThat(infoStr + " payeeAccount", dto.getPayeeAccount(), equalTo(expected.getPayeeAccount()));
        assertThat(infoStr + " payeeBsb", dto.getPayeeBsb(), equalTo(expected.getPayeeBsb()));
        assertThat(infoStr + " payeeName", dto.getPayeeName(), equalTo(expected.getPayeeName()));
        assertThat(infoStr + " payeeBillerCode", dto.getPayeeBillerCode(), equalTo(expected.getPayeeBillerCode()));
        assertThat(infoStr + " payeeCustrRef", dto.getPayeeCustrRef(), equalTo(expected.getPayeeCustrRef()));
        assertThat(infoStr + " payerAccount", dto.getPayerAccount(), equalTo(expected.getPayerAccount()));
        assertThat(infoStr + " payerBsb", dto.getPayerBsb(), equalTo(expected.getPayerBsb()));
        assertThat(infoStr + " payerName", dto.getPayerName(), equalTo(expected.getPayerName()));
        assertThat(infoStr + " systemTransaction", dto.getSystemTransaction(), equalTo(expected.isSystemTransaction()));
        assertThat(infoStr + " descriptionSecond", dto.getDescriptionSecond(), equalTo(expected.getTransactionDescription()));
        assertThat(infoStr + " valDate", dto.getValDate(), equalTo(expected.getValDate()));
        assertThat(infoStr + " description", dto.getDocDescription(), equalTo(expected.getDocDescription()));
        assertThat(infoStr + " origin", dto.getOrigin(), equalTo(expected.getOrigin() == null ? null
                : expected.getOrigin().getName()));

        assertThat(infoStr + " categoryType", dto.getCashCategoryType(), equalTo(expectedCategory));
        assertThat(infoStr + " debitOrCredit", dto.getDebitOrCredit(), equalTo(debitOrCredit));
        assertThat(infoStr + " detailDescription", dto.getDetailDescription(), equalTo(expectedDetailDescription));
        assertThat(infoStr + " transactionSubType", dto.getTransactionSubType(), equalTo(expectedSubTypeDescription));
        assertThat(infoStr + " thirdPartySystem", dto.getThirdPartySystem(), equalTo(expThirdPartySystem));
    }

    private TransactionHistory makeTransactionHistory(CashCategorisationType cashCategorisationType,
                                                      String accountId, BigDecimal amount, BigDecimal netAmount,
                                                      BigDecimal balance, String bookingText, DateTime clearDate,
                                                      boolean cleared, String type, String docId,
                                                      DateTime effectiveDate, int evtId, String metaType,
                                                      String orderType,
                                                      BTOrderType btOrderType, String payeeAccount, String payeeBsb,
                                                      String payeeName, String payeeBillerCode, String payeeCustrRef,
                                                      String payerAccount, String payerBsb, String payerName,
                                                      boolean systemTransaction, String transactionDescription,
                                                      DateTime valDate, String status, String docDescription,
                                                      Origin origin, String subtypeDescription, String wrapSystem) {
        final TransactionHistoryImpl transactionHistory = new TransactionHistoryImpl();

        transactionHistory.setCashCategorisationType(cashCategorisationType);
        transactionHistory.setAccountId(accountId);
        transactionHistory.setAmount(amount);
        transactionHistory.setNetAmount(netAmount);
        transactionHistory.setBalance(balance);
        transactionHistory.setBookingText(bookingText);
        transactionHistory.setClearDate(clearDate);
        transactionHistory.setCleared(cleared);
        transactionHistory.setTransactionType(type);
        transactionHistory.setDocId(docId);
        transactionHistory.setEffectiveDate(effectiveDate);
        transactionHistory.setEvtId(evtId);
        transactionHistory.setMetaType(metaType);
        transactionHistory.setOrderType(orderType);
        transactionHistory.setBTOrderType(btOrderType);
        transactionHistory.setPayeeAccount(payeeAccount);
        transactionHistory.setPayeeBsb(payeeBsb);
        transactionHistory.setPayeeName(payeeName);
        transactionHistory.setPayeeBillerCode(payeeBillerCode);
        transactionHistory.setPayeeCustrRef(payeeCustrRef);
        transactionHistory.setPayerAccount(payerAccount);
        transactionHistory.setPayerBsb(payerBsb);
        transactionHistory.setPayerName(payerName);
        transactionHistory.setSystemTransaction(systemTransaction);
        transactionHistory.setTransactionDescription(transactionDescription);
        transactionHistory.setValDate(valDate);
        transactionHistory.setDocDescription(docDescription);
        transactionHistory.setStatus(status);
        transactionHistory.setOrigin(origin);
        transactionHistory.setThirdPartySystem(wrapSystem);

        TransactionSubType subtype = mock(TransactionSubType.class);
        when(subtype.getTransactionSubTypeDescription()).thenReturn(subtypeDescription);
        transactionHistory.setTransactionSubTypes(asList(subtype));

        return transactionHistory;
    }


    @Test
    public void testGetCashTransactionHistory_WRAPSMSF() {
        final List<TransactionHistory> transactions = makeTransactionHistories();

        // payment, unmarked wrap system
        transactions.add(makeTransactionHistory(null, "99808", new BigDecimal(223), new BigDecimal(2333),
                new BigDecimal(20001), "Direct Credit", new DateTime("2015-02-01"), true, "type3",
                "3123", new DateTime("2015-02-27"), 1, "outpay", "outpay.outpay#dd", BTOrderType.PAYMENT,
                "120002211", "262-555", "Tom Demo Bertrand2", "4567", "543", "97876655", "012-029", "deepshikha2",
                false, "Optional desc2", new DateTime("2015-03-31"), null, "Advice description2", Origin.WEB_UI, null, SystemType.WRAP.getName()));


        final List<ApiSearchCriteria> criteriaList = makeApiSearchCriteria();
        final List<CashTransactionHistoryDto> dtos;
        int index;



        when(transactionIntegrationService.loadCashTransactionHistory(any(String.class),
                any(DateTime.class),
                any(DateTime.class),
                any(ServiceErrors.class))).thenReturn(transactions);

        dtos = cashTransactionDtoService.search(criteriaList, new ServiceErrorsImpl());

        assertThat("dtos not null", dtos, notNullValue());
        assertThat("dtos size", dtos.size(), equalTo(transactions.size()));

        index = 0;
        checkDto("dto[" + index + "]", dtos.get(index), transactions.get(index), "lump_sum", "Deposit", null, null);

        index = 1;
        checkDto("dto[" + index + "]", dtos.get(index), transactions.get(index), "cannotbemarked", "Reversal - Deposit", null, null);

        index = 2;
        checkDto("dto[" + index + "]", dtos.get(index), transactions.get(index), "cannotbemarked", "Deposit", null, null);

        index = 3;
        checkDto("dto[" + index + "]", dtos.get(index), transactions.get(index), "unmarked", "Payment", null, null);

        index = 4;
        checkDto("dto[" + index + "]", dtos.get(index), transactions.get(index), "", "Contribution", "Personal", null);

        index = 5;
        checkDto("dto[" + index + "]", dtos.get(index), transactions.get(index), "unmarked", "Payment", null, SystemType.WRAP.getName());
    }
}