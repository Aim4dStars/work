package com.bt.nextgen.api.asset.service;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.api.account.v3.service.AccountDtoService;
import com.bt.nextgen.api.asset.model.AssetHoldersDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.asset.AccountPositionHolder;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class AssetHoldersDtoServiceTest {

    @InjectMocks
    private AssetHoldersDtoServiceImpl assetHoldersDtoService;

    @Mock
    private AccountDtoService accountDtoService;

    @Mock
    private AssetIntegrationService assetIntegrationService;

    private List<ApiSearchCriteria> criteriaList;
    private List<AccountDto> accounts;
    private ServiceErrors serviceErrors;
    private List<AccountPositionHolder> assetHolders;

    @Before
    public void setup() {
        accounts = generateAccountList();
        assetHolders = generateAccountHoldersList();
        criteriaList = new ArrayList<>();
        serviceErrors = new ServiceErrorsImpl();

        criteriaList.add(new ApiSearchCriteria("asset-ids", ApiSearchCriteria.SearchOperation.EQUALS, "asset-ids", ApiSearchCriteria.OperationType.STRING));
        criteriaList.add(new ApiSearchCriteria("price-date", ApiSearchCriteria.SearchOperation.EQUALS, new DateTime("2016-10-10").toString(), ApiSearchCriteria.OperationType.DATE));

        Mockito.when(accountDtoService.search(Mockito.anyList(), Mockito.any(ServiceErrors.class))).thenReturn(accounts);
        Mockito.when(assetIntegrationService.getAssetAccountHolders(Mockito.anyList(), Mockito.anyList(), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(assetHolders);
    }

    @Test
    public void testSearch() {
        List<AssetHoldersDto> assetHoldersDtoList = assetHoldersDtoService.search(criteriaList, serviceErrors);
        Assert.assertNotNull(assetHoldersDtoList);
        Assert.assertEquals(assetHoldersDtoList.size(), 1);
        Assert.assertNotNull(assetHoldersDtoList.get(0));
        Assert.assertEquals(assetHoldersDtoList.get(0).getAssetPrice(), BigDecimal.valueOf(10));
        Assert.assertEquals(assetHoldersDtoList.get(0).getAccount().getAccountName(), "AcctName4");
        Assert.assertEquals(assetHoldersDtoList.get(0).getAccount().getAccountNumber(), "AcctNumber4");
        Assert.assertEquals(assetHoldersDtoList.get(0).getAccount().getAccountStatus(), "AcctStatus4");
        Assert.assertEquals(assetHoldersDtoList.get(0).getMarketValue(), BigDecimal.valueOf(1000));
    }

    private List<AccountDto> generateAccountList() {
        List<AccountDto> accountDtos = new ArrayList<>();
        accountDtos.add(createAccountDto("AcctName1", "AcctNumber1", "AcctStatus1", "productName1"));
        accountDtos.add(createAccountDto("AcctName2", "AcctNumber2", "AcctStatus2", "productName2"));
        accountDtos.add(createAccountDto("AcctName3", "AcctNumber3", "AcctStatus3", "productName3"));
        accountDtos.add(createAccountDto("AcctName4", "AcctNumber4", "AcctStatus4", "productName4"));
        return accountDtos;
    }

    private AccountDto createAccountDto(String acctName, String acctNumber, String acctStatus, String productName) {
        AccountDto accountDto = Mockito.mock(AccountDto.class);
        Mockito.when(accountDto.getEncodedAccountKey()).thenReturn(EncodedString.fromPlainText("1234").toString());
        Mockito.when(accountDto.getAccountName()).thenReturn(acctName);
        Mockito.when(accountDto.getAccountNumber()).thenReturn(acctNumber);
        Mockito.when(accountDto.getAccountStatus()).thenReturn(acctStatus);
        Mockito.when(accountDto.getProduct()).thenReturn(productName);
        return accountDto;
    }

    private List<AccountPositionHolder> generateAccountHoldersList() {
        List<AccountPositionHolder> list = new ArrayList<>();
        AccountPositionHolder accountPositionHolder = Mockito.mock(AccountPositionHolder.class);

        Mockito.when(accountPositionHolder.getPrice()).thenReturn(BigDecimal.TEN);
        Mockito.when(accountPositionHolder.getPriceDate()).thenReturn(new DateTime("2016-10-10"));
        Mockito.when(accountPositionHolder.getUnits()).thenReturn(BigDecimal.ONE);
        Mockito.when(accountPositionHolder.getMarketValue()).thenReturn(BigDecimal.valueOf(1000));
        Mockito.when(accountPositionHolder.getAccountKey()).thenReturn(AccountKey.valueOf("1234"));

        list.add(accountPositionHolder);
        return list;
    }
}
