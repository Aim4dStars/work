package com.bt.nextgen.api.smsf.service;

import com.bt.nextgen.api.smsf.model.AccountingSoftwareDto;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.account.SubAccountImpl;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.btfin.panorama.service.integration.account.SubAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.accountingsoftware.model.AccountingSoftware;
import com.bt.nextgen.service.integration.accountingsoftware.model.AccountingSoftwareImpl;
import com.bt.nextgen.service.integration.accountingsoftware.model.AccountingSoftwareType;
import com.bt.nextgen.service.integration.accountingsoftware.model.SoftwareFeedStatus;
import com.bt.nextgen.service.integration.accountingsoftware.service.AccountingSoftwareIntegrationService;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.service.exception.ServiceException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * Test cases for Accounting Software service
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountingSoftwareDtoServiceImplTest {

    @InjectMocks
    public AccountingSoftwareDtoServiceImpl accountingSoftwareDtoService;
    @Mock
    public AccountingSoftwareIntegrationService accountingSoftwareIntegrationService;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    private AccountingSoftwareImpl accountingSoftware = new AccountingSoftwareImpl();
    ServiceErrors serviceErrors;
    String  accountId;
    AccountKey key;


    @Mock
    private AccountIntegrationService accountService;
    private AccountKey accountKey;
    private WrapAccountDetail accountDetail = new WrapAccountDetailImpl();
    List <SubAccount> list = new ArrayList <>();
    Collection<Code> categoryCode = new ArrayList<>();

    @Before
    public void setup() throws Exception {

        List<ValidationError> errorList = new ArrayList<ValidationError>();
        accountId = "C3C8CCCBD738549FE60DF103B3A12E3A96826AD73B30B0B4"; //31747
        key = AccountKey.valueOf(accountId);
        serviceErrors = new ServiceErrorsImpl();

        CodeImpl impl1 = new CodeImpl("51061", "BGL360", "BGL SF360", "bgl360");
        CodeImpl impl2 = new CodeImpl("51060", "CLASS", "Class Super", "class");

        categoryCode.add(impl1);
        categoryCode.add(impl2);

        when(staticIntegrationService.loadCodes(eq(CodeCategory.EXT_HOLDING_SRC), any(ServiceErrors.class))).thenReturn(categoryCode);


    }

   private WrapAccountDetail getAccountNoAccountantLinked() {
       WrapAccountDetail accountDetail = new WrapAccountDetailImpl();

       accountKey = AccountKey.valueOf("31747");
       accountDetail.setAccountKey(accountKey);
       SubAccountImpl subAccount = new SubAccountImpl();
       subAccount.setAccntSoftware("class");
       subAccount.setExternalAssetsFeedState("Manual");
       subAccount.setSubAccountType(ContainerType.EXTERNAL_ASSET);
       list.add(subAccount);
       accountDetail.setSubAccounts(list);

       return accountDetail;
   }

    private WrapAccountDetail getAccountWithNoAccountingSoftware() {
        WrapAccountDetail accountDetail = new WrapAccountDetailImpl();

        accountKey = AccountKey.valueOf("31747");
        accountDetail.setAccountKey(accountKey);
        SubAccountImpl subAccount = new SubAccountImpl();
        //account.setAccntSoftware("Class");
        subAccount.setExternalAssetsFeedState("Manual");
        subAccount.setSubAccountType(ContainerType.EXTERNAL_ASSET);

        list.add(subAccount);
        accountDetail.setSubAccounts(list);
        ((WrapAccountDetailImpl)accountDetail).setAccntPersonId(ClientKey.valueOf("45612"));

        return accountDetail;
    }

    private WrapAccountDetail getAccountWithNoAccountantLinkedNoAccountingSoftware() {
        WrapAccountDetail accountDetail = new WrapAccountDetailImpl();

        accountKey = AccountKey.valueOf("31747");
        accountDetail.setAccountKey(accountKey);
        SubAccountImpl subAccount = new SubAccountImpl();
        //account.setAccntSoftware("Class");
        subAccount.setExternalAssetsFeedState("Manual");
        subAccount.setSubAccountType(ContainerType.EXTERNAL_ASSET);
        list.add(subAccount);
        accountDetail.setSubAccounts(list);

        return accountDetail;
    }

    @After
    public void tearDown() throws Exception {
    }

    private AccountingSoftware getAccountingSoftwareAwaitingState(){
        accountingSoftware.setSoftwareFeedStatus(SoftwareFeedStatus.AWAITING);
        accountingSoftware.setSoftwareName(AccountingSoftwareType.CLASS);
        return accountingSoftware;
    }

    private AccountingSoftware getAccountingSoftwareManualState(){
        accountingSoftware.setKey(AccountKey.valueOf("31747"));
        accountingSoftware.setSoftwareFeedStatus(SoftwareFeedStatus.MANUAL);
        accountingSoftware.setSoftwareName(AccountingSoftwareType.CLASS);
        return accountingSoftware;
    }

    @Test
    public void testUpdateNoAccountantLinked() throws Exception {

        Mockito.when(accountingSoftwareIntegrationService.update(Mockito.any(AccountingSoftware.class),
                Mockito.any(ServiceErrors.class))).thenReturn(getAccountingSoftwareAwaitingState());
        Mockito.when(accountService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(getAccountNoAccountantLinked());

        AccountingSoftwareDto accountingSoftwareDto =new AccountingSoftwareDto();
        accountingSoftwareDto.setKey(new com.bt.nextgen.api.account.v2.model.AccountKey("C3C8CCCBD738549FE60DF103B3A12E3A96826AD73B30B0B4"));
        accountingSoftwareDto.setStatus(true);
        accountingSoftwareDto.setSoftwareName("class");
        accountingSoftwareDto.setFeedStatus("awaiting");

        accountingSoftwareDto=accountingSoftwareDtoService.update(accountingSoftwareDto,serviceErrors);
        assertNotNull(accountingSoftwareDto);
        Assert.assertEquals(SoftwareFeedStatus.MANUAL.getValue(), accountingSoftwareDto.getFeedStatus());
        Assert.assertEquals(accountingSoftware.getSoftwareName().getValue(), accountingSoftwareDto.getSoftwareName());
        assertNull(accountingSoftwareDto.getWarnings());

    }




    @Test
    public void testUpdateNoAccountingSoftwareExists() throws Exception {


        Mockito.when(accountingSoftwareIntegrationService.update(Mockito.any(AccountingSoftware.class),
                Mockito.any(ServiceErrors.class))).thenReturn(getAccountingSoftwareAwaitingState());
        Mockito.when(accountService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(getAccountWithNoAccountantLinkedNoAccountingSoftware());

        //Input DTO
        AccountingSoftwareDto accountingSoftwareDto =new AccountingSoftwareDto();
        accountingSoftwareDto.setKey(new com.bt.nextgen.api.account.v2.model.AccountKey("C3C8CCCBD738549FE60DF103B3A12E3A96826AD73B30B0B4"));
        accountingSoftwareDto.setStatus(true);
        accountingSoftwareDto.setSoftwareName("class");
        accountingSoftwareDto.setFeedStatus("awaiting");

        accountingSoftwareDto=accountingSoftwareDtoService.update(accountingSoftwareDto,serviceErrors);
        assertNotNull(accountingSoftwareDto);
        Assert.assertEquals(SoftwareFeedStatus.MANUAL.getValue(), accountingSoftwareDto.getFeedStatus());
        assertNull(accountingSoftwareDto.getSoftwareName());
        assertNull(accountingSoftwareDto.getWarnings());
    }

    @Test
    public void testUpdateNoAccountingSoftwareExists_NoAccountantLinked_ToManual() throws Exception {
        Mockito.when(accountingSoftwareIntegrationService.update(Mockito.any(AccountingSoftware.class),
                Mockito.any(ServiceErrors.class))).thenReturn(getAccountingSoftwareManualState());
        Mockito.when(accountService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(getAccountWithNoAccountantLinkedNoAccountingSoftware());

        //Input DTO
        AccountingSoftwareDto accountingSoftwareDto =new AccountingSoftwareDto();
        accountingSoftwareDto.setKey(new com.bt.nextgen.api.account.v2.model.AccountKey("C3C8CCCBD738549FE60DF103B3A12E3A96826AD73B30B0B4"));
        accountingSoftwareDto.setStatus(true);
        accountingSoftwareDto.setSoftwareName("class");
        accountingSoftwareDto.setFeedStatus("manual");

        accountingSoftwareDto=accountingSoftwareDtoService.update(accountingSoftwareDto,serviceErrors);
        assertNotNull(accountingSoftwareDto);
        Assert.assertEquals(SoftwareFeedStatus.MANUAL.getValue(), accountingSoftwareDto.getFeedStatus());
        Assert.assertEquals(accountingSoftware.getSoftwareName().getValue(), accountingSoftwareDto.getSoftwareName());
        assertNull(accountingSoftwareDto.getWarnings());
    }

    @Test
    public void testUpdateNoAccountingSoftwareExists_NoAccountantLinked_ToAwaiting() throws Exception {
        Mockito.when(accountingSoftwareIntegrationService.update(Mockito.any(AccountingSoftware.class),
                Mockito.any(ServiceErrors.class))).thenReturn(getAccountingSoftwareManualState());
        Mockito.when(accountService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(getAccountWithNoAccountantLinkedNoAccountingSoftware());

        //Input DTO
        AccountingSoftwareDto accountingSoftwareDto =new AccountingSoftwareDto();
        accountingSoftwareDto.setKey(new com.bt.nextgen.api.account.v2.model.AccountKey("C3C8CCCBD738549FE60DF103B3A12E3A96826AD73B30B0B4"));
        accountingSoftwareDto.setStatus(true);
        accountingSoftwareDto.setSoftwareName("class");
        accountingSoftwareDto.setFeedStatus("awaiting");

        accountingSoftwareDto=accountingSoftwareDtoService.update(accountingSoftwareDto,serviceErrors);
        assertNotNull(accountingSoftwareDto);
        Assert.assertEquals(SoftwareFeedStatus.MANUAL.getValue(), accountingSoftwareDto.getFeedStatus());
        assertNull(accountingSoftwareDto.getSoftwareName());
        assertNull(accountingSoftwareDto.getWarnings());
    }


    @Test(expected = ServiceException.class)
    public void testUpdateAccountingSoftwareExists_AccountantLinked_ToInvalidState() throws Exception {
/*        Mockito.when(accountingSoftwareIntegrationService.update(Mockito.any(AccountingSoftware.class),
                Mockito.any(ServiceErrors.class))).thenReturn(getAccountingSoftwareManualState());*/
        Mockito.when(accountService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(getAccountWithNoAccountantLinkedNoAccountingSoftware());

        //Input DTO
        AccountingSoftwareDto accountingSoftwareDto =new AccountingSoftwareDto();
        accountingSoftwareDto.setKey(new com.bt.nextgen.api.account.v2.model.AccountKey("C3C8CCCBD738549FE60DF103B3A12E3A96826AD73B30B0B4"));
        accountingSoftwareDto.setStatus(true);
        accountingSoftwareDto.setSoftwareName("class");
        accountingSoftwareDto.setFeedStatus("invalidState");

        accountingSoftwareDto=accountingSoftwareDtoService.update(accountingSoftwareDto,serviceErrors);
        Assert.assertTrue(serviceErrors.hasErrors());
    }


}