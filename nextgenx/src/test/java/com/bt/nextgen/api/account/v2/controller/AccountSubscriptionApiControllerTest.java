package com.bt.nextgen.api.account.v2.controller;

import com.bt.nextgen.api.account.v1.model.DepositDto;
import com.bt.nextgen.api.account.v1.service.DepositDtoService;
import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.account.v2.model.AccountSubscriptionDto;
import com.bt.nextgen.api.account.v2.model.InitialInvestmentAssetDto;
import com.bt.nextgen.api.account.v2.service.AccountSubscriptionDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.Asset;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.bt.nextgen.api.account.v2.model.AccountSubscription.SIMPLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountSubscriptionApiControllerTest {

    @InjectMocks
    private AccountSubscriptionApiController subscriptionApiController;

    @Mock
    private AccountSubscriptionDtoService subscriptionDtoService;

    @Mock
    private DepositDtoService depositDtoService;

    @Captor
    private ArgumentCaptor<com.bt.nextgen.api.account.v2.model.AccountSubscriptionDto> subscriptionDtoArgumentCaptor;

    @Captor
    private ArgumentCaptor<DepositDto> depositDtoArgumentCaptor;

    private String accountId;
    private List<InitialInvestmentAssetDto> initialInvestmentDtos;
    private String depositJson;

    @Before
    public void setUp() throws Exception {
        accountId = "FE5A9D833B86241F4767886F6D5ED0FB6E62F96DC31A6DDD";
        depositJson = "{\"isRecurring\":false,\"transactionDate\":\"07 Nov 2017\",\"toPayteeDto\":{\"accountId\":\"120002738\",\"accountName\":\"person-120_2723person-120_2723person-120_2723\",\"code\":\"262786\",\"primary\":false,\"fixedCRN\":false},\"description\":\"\",\"amount\":120,\"fromPayDto\":{\"accountId\":\"1234561\",\"accountName\":\"Linked Account Name 1\",\"nickname\":\"Linked Account Nick Name 1\",\"code\":\"032134\",\"primary\":true,\"fixedCRN\":false,\"type\":\"Deposit\",\"linkedAccountStatus\":{\"linkedAccountStatus\":\"VERIFIED\",\"genCode\":false,\"vfyCode\":false,\"directDebit\":true,\"gracePeriod\":false},\"label\":\"Linked Account Nick Name 1 032-134 1234561\"}}";

        Asset asset = mock(Asset.class);
        when(asset.getAssetId()).thenReturn("asset1");
        when(asset.getAssetCode()).thenReturn("code1");
        when(asset.getAssetName()).thenReturn("asset1");
        when(asset.getAssetType()).thenReturn(AssetType.MANAGED_FUND);

        initialInvestmentDtos = new ArrayList<>();
        initialInvestmentDtos.add(new InitialInvestmentAssetDto(asset, BigDecimal.TEN));
    }


    @Test
    public void updateSubscription() throws Exception {
        Mockito.when(subscriptionDtoService.update(Mockito.any(AccountSubscriptionDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(new AccountSubscriptionDto(new AccountKey(accountId), SIMPLE.getSubscriptionType(), initialInvestmentDtos));

        ApiResponse response = subscriptionApiController.updateSubscription(accountId, SIMPLE.getSubscriptionType(),
                initialInvestmentDtos.get(0).getAssetId(), BigDecimal.TEN.toPlainString(), null);

        Mockito.verify(subscriptionDtoService).update(Mockito.any(AccountSubscriptionDto.class), Mockito.any(ServiceErrorsImpl.class));
        Mockito.verify(subscriptionDtoService).update(subscriptionDtoArgumentCaptor.capture(), Mockito.any(ServiceErrorsImpl.class));

        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(subscriptionDtoArgumentCaptor.getAllValues().size(), 1);
        assertEquals(subscriptionDtoArgumentCaptor.getValue().getSubscriptionType(), SIMPLE.getSubscriptionType());
        assertEquals(subscriptionDtoArgumentCaptor.getValue().getInitialInvestments().size(), 1);
    }

    @Test
    public void updateSubscriptionWithDeposit() throws IOException {
        Mockito.when(subscriptionDtoService.update(Mockito.any(AccountSubscriptionDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(new AccountSubscriptionDto(new AccountKey(accountId), SIMPLE.getSubscriptionType(), initialInvestmentDtos));

        Mockito.when(depositDtoService.submit(Mockito.any(DepositDto.class), Mockito.any(ServiceErrors.class))).thenReturn(mock(DepositDto.class));

        ApiResponse response = subscriptionApiController.updateSubscription(accountId, SIMPLE.getSubscriptionType(),
                initialInvestmentDtos.get(0).getAssetId(), BigDecimal.TEN.toPlainString(), depositJson);

        Mockito.verify(subscriptionDtoService).update(Mockito.any(AccountSubscriptionDto.class), Mockito.any(ServiceErrorsImpl.class));
        Mockito.verify(subscriptionDtoService).update(subscriptionDtoArgumentCaptor.capture(), Mockito.any(ServiceErrorsImpl.class));
        Mockito.verify(depositDtoService).submit(depositDtoArgumentCaptor.capture(), Mockito.any(ServiceErrorsImpl.class));

        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(subscriptionDtoArgumentCaptor.getAllValues().size(), 1);
        assertEquals(subscriptionDtoArgumentCaptor.getValue().getSubscriptionType(), SIMPLE.getSubscriptionType());
        assertEquals(subscriptionDtoArgumentCaptor.getValue().getInitialInvestments().size(), 1);
        assertEquals(depositDtoArgumentCaptor.getValue().getAmount(), BigDecimal.valueOf(120));
        assertEquals(depositDtoArgumentCaptor.getValue().getToPayteeDto().getAccountName(), "person-120_2723person-120_2723person-120_2723");
        assertEquals(depositDtoArgumentCaptor.getValue().getFromPayDto().getAccountName(), "Linked Account Name 1");
    }
}