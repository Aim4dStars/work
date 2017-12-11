package com.bt.nextgen.api.movemoney.v2.service;

import com.bt.nextgen.api.transaction.model.TransactionDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.util.ApplicationProperties;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.transaction.SavedPayment;
import com.bt.nextgen.service.avaloq.transaction.SavedPaymentImpl;
import com.bt.nextgen.service.avaloq.transaction.TransactionFrequency;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.movemoney.IndexationType;
import com.bt.nextgen.service.integration.movemoney.PensionPaymentType;
import com.bt.nextgen.service.integration.movemoney.SavedPaymentIntegrationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType.STRING;
import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation.EQUALS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by L067218 on 7/02/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class SavedPaymentDtoServiceTest {
    @InjectMocks
    private SavedPaymentDtoServiceImpl savedPaymentDtoServiceImpl;

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private SavedPaymentIntegrationService savedPaymentIntegrationService;

    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private WrapAccount account;

    @Mock
    private ServiceErrors serviceErrors;


    @Before
    public void setup() {
        reset(applicationProperties, savedPaymentIntegrationService);
    }


    @Test
    public void search() {
        final List<SavedPayment> pensionPayments = new ArrayList<>();
        final List<ApiSearchCriteria> criteriaList = Arrays.asList(new ApiSearchCriteria("account-id", EQUALS,
                "12345", STRING));

        pensionPayments.add(makePensionPayments("123456789", "Not Submitted",
                "Pension payment", new BigDecimal(1000), null, "2", "1222", "Once",
                IndexationType.CPI, PensionPaymentType.MINIMUM_AMOUNT));
        pensionPayments.add(makePensionPayments("123456790", "Not Submitted",
                "Lump sum withdrawal", new BigDecimal(2000), null, "2", "1220", null, null, null));
        pensionPayments.add(makePensionPayments("123456791", "Not Submitted",
                "Pension payment", new BigDecimal(150), new BigDecimal(100), "2", "1220", "Once", IndexationType.DOLLAR, null));
        pensionPayments.add(makePensionPayments("123456792", "Not Submitted",
                "Pension payment", new BigDecimal(111), new BigDecimal("1.23"), "2", "1220", "Quarterly",
                IndexationType.PERCENTAGE, null));

        when(accountService.loadWrapAccount(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(account);
        when(account.getAccountNumber()).thenReturn("12345645");
        when(savedPaymentIntegrationService.loadSavedPensionPayments(anyString(), anyList(),
                any(ServiceErrors.class))).thenReturn(pensionPayments);

        final List<TransactionDto> result = savedPaymentDtoServiceImpl.search(criteriaList, serviceErrors);

        verify(accountService).loadWrapAccount(any(AccountKey.class), any(ServiceErrors.class));
        verify(savedPaymentIntegrationService).loadSavedPensionPayments(anyString(), anyList(),
                any(ServiceErrors.class));

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(pensionPayments.size()));

        for (int i = 0; i < result.size(); i++) {
            checkPayment("payment " + i, result.get(i), (SavedPaymentImpl) pensionPayments.get(i));
        }
    }

    @Test
    public void searchWithAccountNull() {
        final List<ApiSearchCriteria> criteriaList = new ArrayList<>();

        criteriaList.add(new ApiSearchCriteria("account-id", EQUALS, null, STRING));

        account = null;
        when(accountService.loadWrapAccount(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(account);

        final List<TransactionDto> result = savedPaymentDtoServiceImpl.search(criteriaList, serviceErrors);

        verify(accountService).loadWrapAccount(any(AccountKey.class), any(ServiceErrors.class));
        verify(savedPaymentIntegrationService, never()).loadSavedPensionPayments(anyString(), anyList(),
                any(ServiceErrors.class));

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(0));
    }

    @Test
    public void searchWithEmptyCriteriaList() {
        final List<ApiSearchCriteria> criteriaList = new ArrayList<>();

        criteriaList.add(new ApiSearchCriteria("account-id", EQUALS, null, STRING));

        account = null;
        when(accountService.loadWrapAccount(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(account);

        final List<TransactionDto> result = savedPaymentDtoServiceImpl.search(criteriaList, serviceErrors);

        verify(accountService).loadWrapAccount(any(AccountKey.class), any(ServiceErrors.class));
        verify(savedPaymentIntegrationService, never()).loadSavedPensionPayments(anyString(), anyList(),
                any(ServiceErrors.class));

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(0));
    }

    @Test
    public void searchWithDifferentAccIdParam() {
        final List<ApiSearchCriteria> criteriaList = new ArrayList<>();

        criteriaList.add(new ApiSearchCriteria("accid", EQUALS, "12345", STRING));

        account = null;
        when(accountService.loadWrapAccount(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(account);

        final List<TransactionDto> result = savedPaymentDtoServiceImpl.search(criteriaList, serviceErrors);

        verify(accountService).loadWrapAccount(any(AccountKey.class), any(ServiceErrors.class));
        verify(savedPaymentIntegrationService, never()).loadSavedPensionPayments(anyString(), anyList(),
                any(ServiceErrors.class));

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(0));
    }


    private void checkPayment(String infoStr, TransactionDto dto, SavedPaymentImpl savedPayment) {
        final String expectedFrequency = savedPayment.getFrequency() == null ? "" : savedPayment.getFrequency().getDescription();
        final String expectedIndexationType = savedPayment.getPensionIndexationType() == null ? null : savedPayment.getPensionIndexationType().getLabel();
        final String expectedPaymentType = savedPayment.getPensionPaymentType() == null ? null : savedPayment.getPensionPaymentType().getLabel();

        assertThat(infoStr + " transactionStatus", dto.getTransactionStatus(), equalTo(savedPayment.getTransactionStatus()));
        assertThat(infoStr + " receiptNumber", dto.getRecieptNumber(), equalTo(new BigDecimal(savedPayment.getTransactionId())));
        assertThat(infoStr + " netAmount", dto.getNetAmount(), equalTo(savedPayment.getAmount()));
        assertThat(infoStr + " stordPosId", new EncodedString(dto.getStordPosId()).plainText(), equalTo(savedPayment.getStordPos()));
        assertThat(infoStr + " transSeqNo", dto.getTransSeqNo(), equalTo(savedPayment.getTransSeqNo()));
        assertThat(infoStr + " frequency", dto.getFrequency(), equalTo(expectedFrequency));
        assertThat(infoStr + " orderType", dto.getOrderType(), equalTo(savedPayment.getOrderType()));
        assertThat(infoStr + " indexationType", dto.getIndexationType(), equalTo(expectedIndexationType));
        assertThat(infoStr + " pensionPaymentType", dto.getPensionPaymentType(), equalTo(expectedPaymentType));

        if (dto.getIndexationType() == null) {
            assertThat(infoStr + " indexationType", dto.getIndexationType(), nullValue());
        }
        else {
            assertThat(infoStr + " indexationType", dto.getIndexationType(),
                    equalTo(savedPayment.getPensionIndexationType().getLabel()));
        }

        if (savedPayment.getPensionIndexationType() == IndexationType.PERCENTAGE) {
            assertThat(infoStr + " indexationPercent", dto.getIndexationAmount(), equalTo(savedPayment.getPensionIndexationPercent()));
        }
        else {
            assertThat(infoStr + " indexationAmount", dto.getIndexationAmount(), equalTo(savedPayment.getPensionIndexationAmount()));
        }
    }


    private SavedPaymentImpl makePensionPayments(String transactionId, String status, String orderType,
                                                 BigDecimal amount, BigDecimal indexationAmount, String transSeqNo,
                                                 String storedpos, String frequency, IndexationType type,
                                                 PensionPaymentType pensionPaymentType) {
        final SavedPaymentImpl savedPayment = new SavedPaymentImpl();

        savedPayment.setTransactionId(transactionId);
        savedPayment.setTransactionStatus(status);
        savedPayment.setOrderType(orderType);
        savedPayment.setAmount(amount);
        savedPayment.setPensionIndexationAmount(indexationAmount);
        savedPayment.setTransSeqNo(transSeqNo);
        savedPayment.setStordPos(storedpos);
        savedPayment.setFrequency(frequency != null ? TransactionFrequency.valueOf(frequency) : null);
        savedPayment.setPensionIndexationType(type);
        if (pensionPaymentType != null) {
            savedPayment.setPensionPaymentType(pensionPaymentType);
        }

        return savedPayment;
    }
}
