package com.bt.nextgen.reports.account;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.transactionhistory.model.CashTransactionHistoryDto;
import com.bt.nextgen.api.transactionhistory.service.CashTransactionHistoryReportService;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.avaloq.transactionhistory.TransactionHistoryImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PastTransactionReceiptTest
{
	@InjectMocks
	private PastTransactionReceipt pastTransactionReceipt;

	@Mock
	private ContentDtoService contentService;
	
	@Mock
    private UserProfileService userProfileService;
	
	@Mock
    private CmsService cmsService;

	@Mock
	private CashTransactionHistoryReportService cashTransactionHistoryReportService;

	@Before
	public void setup()
	{
		// Mock content service
		ContentDto content = new ContentDto(new ContentKey("MockKey"), "MockString");
		when(contentService.find((any(ContentKey.class)), any(ServiceErrorsImpl.class))).thenReturn(content);
	}

	@Test
	public void testRetrievePastTransaction() throws ParseException
	{
        CashTransactionHistoryDto transactionDto = new CashTransactionHistoryDto(new TransactionHistoryImpl());
		when(cashTransactionHistoryReportService.retrievePastTransaction(anyString(), anyString(), any(DateTime.class), any(DateTime.class), anyString())).thenReturn(transactionDto);
		Map <String, String> params = new HashMap <>();
		params.put("account-id", "975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0");
		params.put("startDate", "Fri Oct 13 2017");
		params.put("endDate", "2014-12-09");
		params.put("receiptNo", "DCA0F79903927AF7D8D1C670428E5E3978F6A1D47D099A10");

		CashTransactionHistoryDto pastTransactionDtolist = pastTransactionReceipt.retrievePastTransaction(params);

		assertNotNull(pastTransactionDtolist);
		assertEquals(pastTransactionDtolist, transactionDto);
	}

	@Test
	public void testGetMoreInfo()
	        throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		final Collection<AccountDto> accountDtos = CreateDummyAccountDtos();

        PastTransactionReceipt receipt = new PastTransactionReceipt() {
            public Collection<AccountDto> getAccount(Map<String, String> params) {
                return accountDtos;
            }
        };

        UserProfile activeProfile = mock(UserProfile.class);
        when(activeProfile.getJobRole()).thenReturn(JobRole.INVESTOR);
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(activeProfile);

        Mockito.when(cmsService.getDynamicContent(Mockito.any(String.class), Mockito.any(String[].class)))
                .thenReturn("MockString");

        Field userField = PastTransactionReceipt.class.getDeclaredField("userProfileService");
        userField.setAccessible(true);
        userField.set(receipt, userProfileService);

        Field cmsField = PastTransactionReceipt.class.getSuperclass().getSuperclass().getDeclaredField("cmsService");
        cmsField.setAccessible(true);
        cmsField.set(receipt, cmsService);

        String content = receipt.getMoreInfo(new HashMap<String, String>());

        assertEquals("MockString", content);
	}

	@Test
	public void testGetMoreInfoForAdviser()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		final Collection<AccountDto> accountDtos = CreateDummyAccountDtos();

		PastTransactionReceipt receipt = new PastTransactionReceipt() {
			public Collection<AccountDto> getAccount(Map<String, String> params) {
				return accountDtos;
			}
		};

		UserProfile activeProfile = mock(UserProfile.class);
		when(activeProfile.getJobRole()).thenReturn(JobRole.ADVISER);
		Mockito.when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
		Mockito.when(cmsService.getDynamicContent(Mockito.any(String.class), Mockito.any(String[].class)))
				.thenReturn("MockString");

		Field userField = PastTransactionReceipt.class.getDeclaredField("userProfileService");
		userField.setAccessible(true);
		userField.set(receipt, userProfileService);

		Field cmsField = PastTransactionReceipt.class.getSuperclass().getSuperclass().getDeclaredField("cmsService");
		cmsField.setAccessible(true);
		cmsField.set(receipt, cmsService);

		String content = receipt.getMoreInfo(new HashMap<String, String>());

		assertEquals("MockString", content);
	}

	@Test
	public void testGetDisclaimer()
	{
		Map <String, String> params = new HashMap <>();

		String content = pastTransactionReceipt.getDisclaimer(params);

		assertEquals("MockString", content);
	}

	@Test
	public void shouldGetReportName()
	{
		String content = pastTransactionReceipt.getReportName(new HashMap <String, String>());
		assertEquals("Past Transaction", content);
	}

	private Collection<AccountDto> CreateDummyAccountDtos() {
		final Collection<AccountDto> accountDtos = new ArrayList<AccountDto>();
		AccountKey accountKey = new AccountKey("accountId");
		AccountDto accountDto = new AccountDto(accountKey);
		accountDto.setAdviserName("adviser");
		accountDtos.add(accountDto);
		return accountDtos;
	}

}
