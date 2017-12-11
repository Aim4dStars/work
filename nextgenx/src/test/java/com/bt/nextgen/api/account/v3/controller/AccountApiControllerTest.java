package com.bt.nextgen.api.account.v3.controller;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.account.v3.service.AccountDtoServiceImpl;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.bind.WebDataBinder;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.totallylazy.matchers.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;



@RunWith(MockitoJUnitRunner.class)
public class AccountApiControllerTest {


    @Mock
    private AccountDtoServiceImpl accountDtoService;

    @InjectMocks
    private AccountApiController accountApiController;

    @Mock
    WebDataBinder dataBinder;


    List<AccountDto> accountDtoList = new ArrayList<>();
    @Before
    public void setUp() throws Exception {


        AccountKey key1= new AccountKey("74611");
        AccountKey key2= new AccountKey("74612");
        AccountKey key3= new AccountKey("74613");

        AccountDto accountDto1 =new AccountDto(key1);
        AccountDto accountDto2 =new AccountDto(key2);
        AccountDto accountDto3 =new AccountDto(key3);

        accountDto1.setKey(key1);
        accountDto1.setAccountName("Test1");
        accountDto1.setAccountNumber("74611");
        accountDto1.setAccountStatus("Active");
        accountDto1.setAccountType("I");
        accountDto1.setAdviserDealerGroup("TestGroup");

        accountDto2.setKey(key2);
        accountDto2.setAccountName("Test2");
        accountDto2.setAccountNumber("74612");
        accountDto2.setAccountStatus("Active");
        accountDto2.setAccountType("I");
        accountDto2.setAdviserDealerGroup("TestGroup");

        accountDto3.setKey(key3);
        accountDto3.setAccountName("Test3");
        accountDto3.setAccountNumber("74613");
        accountDto3.setAccountStatus("Active");
        accountDto3.setAccountType("I");
        accountDto3.setAdviserDealerGroup("TestGroup");

        accountDtoList.add(accountDto1);
        accountDtoList.add(accountDto2);
        accountDtoList.add(accountDto3);
    }

    @Test
    public void getAccounts() {

        when(accountDtoService.findAll(any(ServiceErrors.class))).thenReturn(accountDtoList);

        ApiResponse response = accountApiController.getAccounts(null, null, null);
        ResultListDto<AccountDto> accountListDto = (ResultListDto<AccountDto>) response.getData();
        assertThat(accountListDto.getResultList().size(), is(3));
        assertThat(accountListDto.getResultList().get(0).getAccountName(), is("Test1"));
    }

    @Test
    public void UpdateBinder_test(){
        accountApiController.updateInitBinder(dataBinder);
        verify(dataBinder,times(1)).setAllowedFields("cGTLMethodId", "primaryContact", "modificationSeq", "statementPref", "cmaStatementPref");
    }

}
