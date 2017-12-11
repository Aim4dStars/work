package com.bt.nextgen.smsf.service;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.bt.nextgen.api.smsf.service.AccountingSoftwareDtoConverter;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.Field;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.smsf.model.AccountingSoftwareDto;
import com.bt.nextgen.api.smsf.service.AccountingSoftwareDtoServiceImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.SubAccountImpl;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.btfin.panorama.service.integration.account.SubAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;

@RunWith(MockitoJUnitRunner.class)
public class RetrieveAccountingSoftwareDtoServiceImplTest
{
	@Mock
	private AccountIntegrationService accountService;

	@InjectMocks
    AccountingSoftwareDtoServiceImpl accountingSoftwareDtoService;

	private AccountKey accountKey;
	private WrapAccountDetail accountDetail = new WrapAccountDetailImpl();
	List <SubAccount> list = new ArrayList <>();
	Collection<Code> categoryCode = new ArrayList<>();
	@Before
	public void setup()
	{
		accountKey = AccountKey.valueOf("plaintext");
		SubAccountImpl account = new SubAccountImpl();
		account.setAccntSoftware("class");
		account.setExternalAssetsFeedState("manual");
		account.setSubAccountType(ContainerType.EXTERNAL_ASSET);
		list.add(account);
		accountDetail.setSubAccounts(list);



		CodeImpl impl1 = new CodeImpl("51061", "BGL360", "BGL SF360", "bgl360");
		CodeImpl impl2 = new CodeImpl("51060", "CLASS", "Class Super", "class");

		categoryCode.add(impl1);
		categoryCode.add(impl2);

	}

	@Test
	public void getAccountingSoftware()
	{
		Mockito.when(accountService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
			.thenReturn(accountDetail);
		AccountingSoftwareDto dto = new AccountingSoftwareDto();
        AccountingSoftwareDtoConverter.convertToDto(accountDetail, dto, categoryCode);
		assertNotNull(dto);
		Assert.assertEquals(dto.getFeedStatus(), "manual");
		Assert.assertEquals(dto.getSoftwareName(), "class");
	}

	@Test
	public void getAccountingSoftware_WhenSubAccountIsNullForClosedAccounts()
	{
		accountDetail.setSubAccounts(null);
		Mockito.when(accountService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
				.thenReturn(accountDetail);
		AccountingSoftwareDto dto = new AccountingSoftwareDto();
		AccountingSoftwareDtoConverter.convertToDto(accountDetail, dto, categoryCode);
		assertNotNull(dto);
		Assert.assertEquals(dto.getFeedStatus(), null);
		Assert.assertEquals(dto.getSoftwareName(), null);
	}

}
