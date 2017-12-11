package com.bt.nextgen.reports.account;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.transactionhistory.model.CashTransactionHistoryDto;
import com.bt.nextgen.api.transactionhistory.service.CashTransactionHistoryDtoService;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.avaloq.transactionhistory.TransactionHistoryImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CashTransactionsTest {

    @InjectMocks
    private CashTransactions cashTransactions;

    @Mock
    private CashTransactionHistoryDtoService cashTransactionHistoryDtoService;

    @Mock
    private ContentDtoService contentService;
    
    @Mock
    private UserProfileService userProfileService;
    
    @Mock
    private CmsService cmsService;

    @Before
    public void setup() {
        // Mock content service
        ContentDto content = new ContentDto(new ContentKey("MockKey"), "MockString");
        when(contentService.find((any(ContentKey.class)), any(ServiceErrorsImpl.class))).thenReturn(content);
    }

    @Test
    public void testRetrieveCashTransactionDtos() throws ParseException {

        List<CashTransactionHistoryDto> transactionDtos = new ArrayList<CashTransactionHistoryDto>();

        TransactionHistoryImpl transaction = new TransactionHistoryImpl();
        transaction.setEvtId(99);
        transaction.setAccountId("69949");
        transaction.setDocId("198074");
        transaction.setMetaType("inpay");
        transaction.setOrderType("inpay.inpay#dd");
        transaction.setTransactionType("CREDIT");
        transaction.setEffectiveDate(new DateTime("2015-02-26"));
        transaction.setValDate(new DateTime("2015-02-27"));
        transaction.setClearDate(new DateTime("2015-03-03"));
        transaction.setBookingText("Direct Debit Deposit from deepshikha");
        transaction.setTransactionDescription("Rent 123");
        transaction.setPayerName("deepshikha");
        transaction.setPayeeName("Tom Demo Bertrand");
        transaction.setPayerBsb("012-003");
        transaction.setPayeeBsb("262-786");
        transaction.setPayerAccount("12345678");
        transaction.setPayeeAccount("120009311");
        transaction.setBalance(new BigDecimal(50045406.87));
        transaction.setAmount(new BigDecimal(123));
        transaction.setCleared(true);
        transaction.setSystemTransaction(false);
        
        CashTransactionHistoryDto transactionDto = new CashTransactionHistoryDto(transaction);
        transactionDtos.add(transactionDto);

        when(cashTransactionHistoryDtoService.search((Matchers.anyListOf(ApiSearchCriteria.class)), any(ServiceErrorsImpl.class)))
                .thenReturn(transactionDtos);

        List<ApiSearchCriteria> criteria = new ArrayList<ApiSearchCriteria>();

        criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS,
                "975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0", ApiSearchCriteria.OperationType.STRING));
        criteria.add(new ApiSearchCriteria(Attribute.FROM_DATE, ApiSearchCriteria.SearchOperation.EQUALS, "2014-09-09",
                ApiSearchCriteria.OperationType.STRING));
        criteria.add(new ApiSearchCriteria(Attribute.TO_DATE, ApiSearchCriteria.SearchOperation.EQUALS, "2014-12-09",
                ApiSearchCriteria.OperationType.STRING));

        Map<String, String> params = new HashMap<>();
        params.put("account-id", "975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0");
        params.put("startDate", "2014-09-09");
        params.put("endDate", "2014-12-09");

        List<CashTransactionHistoryDto> cashTransactionDtoList = (List<CashTransactionHistoryDto>) cashTransactions
                .retrieveCashTransactionDtos(params);

        assertNotNull(cashTransactionDtoList);
    }

    @Test
    public void testGetMoreInfo() 
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        
        Map<String, String> params = new HashMap<>();

        final Collection<AccountDto> accountDtos = new ArrayList<AccountDto>();
        AccountKey accountKey = new AccountKey("accountId");
        AccountDto accountDto = new AccountDto(accountKey);
        accountDto.setAdviserName("adviser");
        accountDtos.add(accountDto);

        CashTransactions cash = new CashTransactions() {
            public Collection<AccountDto> getAccount(Map<String, String> params) {
                return accountDtos;
            }
        };

        UserProfile activeProfile = mock(UserProfile.class);
        when(activeProfile.getJobRole()).thenReturn(JobRole.INVESTOR);
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        
        Mockito.when(cmsService.getDynamicContent(Mockito.any(String.class), Mockito.any(String[].class)))
                .thenReturn("MockString");

        Field userField = CashTransactions.class.getDeclaredField("userProfileService");
        userField.setAccessible(true);
        userField.set(cash, userProfileService);

        Field cmsField = CashTransactions.class.getSuperclass().getSuperclass().getDeclaredField("cmsService");
        cmsField.setAccessible(true);
        cmsField.set(cash, cmsService);

        String content = cash.getMoreInfo(params);

        assertEquals("MockString", content);
    }

    @Test
    public void testGetDisclaimer() {
        Map<String, String> params = new HashMap<>();
        String content = cashTransactions.getDisclaimer(params);

        assertEquals("MockString", content);
    }
}
