package com.bt.nextgen.api.account.v3.controller;

import com.bt.nextgen.api.account.v3.model.AccountCashSweepDto;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.account.v3.model.AccountSubscriptionDto;
import com.bt.nextgen.api.account.v3.model.CashSweepInvestmentDto;
import com.bt.nextgen.api.account.v3.model.DirectOffer;
import com.bt.nextgen.api.account.v3.model.InitialInvestmentDto;
import com.bt.nextgen.api.account.v3.service.AccountCashSweepDtoService;
import com.bt.nextgen.api.account.v3.service.AccountSubscriptionDtoService;
import com.bt.nextgen.api.movemoney.v2.model.DepositDto;
import com.bt.nextgen.api.movemoney.v2.service.DepositDtoService;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.config.SecureJsonObjectMapper;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.Asset;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectReader;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountSubscriptionApiControllerTest {

    @InjectMocks
    private AccountSubscriptionApiController subscriptionApiController;

    @Mock
    private AccountSubscriptionDtoService subscriptionDtoService;

    @Mock
    private AccountCashSweepDtoService accountCashSweepDtoService;

    @Mock
    private DepositDtoService depositDtoService;

    @Mock
    private SecureJsonObjectMapper mapper;

    @Captor
    private ArgumentCaptor<AccountCashSweepDto> cashSweepDtoArgumentCaptor;

    @Captor
    private ArgumentCaptor<AccountSubscriptionDto> subscriptionDtoArgumentCaptor;

    @Captor
    private ArgumentCaptor<DepositDto> depositDtoArgumentCaptor;

    private String accountId;
    private List<InitialInvestmentDto> initialInvestmentDtos;
    private List<CashSweepInvestmentDto> cashSweepInvestmentDtos;
    private String investmentList;
    private String depositJson;

    @Before
    public void setUp() throws Exception {
        accountId = "FE5A9D833B86241F4767886F6D5ED0FB6E62F96DC31A6DDD";
        investmentList = "[{\"asset\": {\"type\": \"Asset\",\"assetId\": \"376413\"},\"allocationPercent\": 55}, " +
                "{\"asset\": {\"type\": \"Asset\",\"assetId\": \"376414\"},\"allocationPercent\": 45}]";

        depositJson = "{\"isRecurring\":false,\"transactionDate\":\"07 Nov 2017\",\"toPayeeDto\":{\"accountId\":\"120002738\",\"accountName\":\"person-120_2723person-120_2723person-120_2723\",\"code\":\"262786\",\"primary\":false,\"fixedCRN\":false},\"description\":\"\",\"amount\":120,\"fromPayDto\":{\"accountId\":\"1234561\",\"accountName\":\"Linked Account Name 1\",\"nickname\":\"Linked Account Nick Name 1\",\"code\":\"032134\",\"primary\":true,\"fixedCRN\":false,\"type\":\"Deposit\",\"linkedAccountStatus\":{\"linkedAccountStatus\":\"VERIFIED\",\"genCode\":false,\"vfyCode\":false,\"directDebit\":true,\"gracePeriod\":false},\"label\":\"Linked Account Nick Name 1 032-134 1234561\"}}";

        ObjectReader objectReader = mock(ObjectReader.class);
        when(objectReader.forType(any(TypeReference.class))).thenReturn(objectReader);
        when(mapper.readerWithView(JsonViews.Write.class)).thenReturn(objectReader);

        when(objectReader.readValue(investmentList)).thenReturn(new SecureJsonObjectMapper().readerWithView(JsonViews.Write.class)
                .forType(new TypeReference<List<CashSweepInvestmentDto>>() {
                }).readValue(investmentList));

        when(objectReader.readValue(depositJson)).thenReturn(new SecureJsonObjectMapper().readerWithView(JsonViews.Write.class)
                .forType(new TypeReference<DepositDto>() {
                }).readValue(depositJson));

        Asset asset = mock(Asset.class);
        when(asset.getAssetId()).thenReturn("asset1");
        when(asset.getAssetCode()).thenReturn("code1");
        when(asset.getAssetName()).thenReturn("asset1");
        when(asset.getAssetType()).thenReturn(AssetType.MANAGED_FUND);

        initialInvestmentDtos = new ArrayList<>();
        initialInvestmentDtos.add(new InitialInvestmentDto(asset, BigDecimal.TEN));

        cashSweepInvestmentDtos = new ArrayList<>();
        cashSweepInvestmentDtos.add(new CashSweepInvestmentDto(asset, BigDecimal.TEN));
    }

    @Test
    public void getSubscriptionDetails() throws Exception {
        Mockito.when(subscriptionDtoService.find(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(new AccountSubscriptionDto(new AccountKey(accountId), DirectOffer.ACTIVE.getSubscriptionType(), initialInvestmentDtos));
        ApiResponse response = subscriptionApiController.getSubscriptionDetails(accountId);
        assertNotNull(response);
        assertNotNull(response.getData());
        AccountSubscriptionDto accountSubscriptionDto = (AccountSubscriptionDto) response.getData();
        assertEquals(accountSubscriptionDto.getSubscriptionType(), DirectOffer.ACTIVE.getSubscriptionType());
        assertEquals(accountSubscriptionDto.getInitialInvestments().size(), 1);
        assertEquals(accountSubscriptionDto.getInitialInvestments().get(0).getAsset().getAssetId(), "asset1");
        assertEquals(accountSubscriptionDto.getInitialInvestments().get(0).getAmount(), BigDecimal.TEN);
    }

    @Test
    public void updateSubscription() throws Exception {
        Mockito.when(subscriptionDtoService.update(Mockito.any(AccountSubscriptionDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(new AccountSubscriptionDto(new AccountKey(accountId), DirectOffer.SIMPLE.getSubscriptionType(), initialInvestmentDtos));

        ApiResponse response = subscriptionApiController.updateSubscription(accountId, DirectOffer.SIMPLE.getSubscriptionType(),
                initialInvestmentDtos.get(0).getAsset().getAssetId(), BigDecimal.TEN.toPlainString(), null);

        Mockito.verify(subscriptionDtoService).update(Mockito.any(AccountSubscriptionDto.class), Mockito.any(ServiceErrorsImpl.class));
        Mockito.verify(subscriptionDtoService).update(subscriptionDtoArgumentCaptor.capture(), Mockito.any(ServiceErrorsImpl.class));

        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(subscriptionDtoArgumentCaptor.getAllValues().size(), 1);
        assertEquals(subscriptionDtoArgumentCaptor.getValue().getSubscriptionType(), DirectOffer.SIMPLE.getSubscriptionType());
        assertEquals(subscriptionDtoArgumentCaptor.getValue().getInitialInvestments().size(), 1);
    }

    @Test
    public void updateSubscriptionWithDeposit() throws IOException {
        Mockito.when(subscriptionDtoService.update(Mockito.any(AccountSubscriptionDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(new AccountSubscriptionDto(new AccountKey(accountId), DirectOffer.SIMPLE.getSubscriptionType(), initialInvestmentDtos));

       Mockito.when(depositDtoService.submit(Mockito.any(DepositDto.class), Mockito.any(ServiceErrors.class))).thenReturn(mock(DepositDto.class));

        ApiResponse response = subscriptionApiController.updateSubscription(accountId, DirectOffer.SIMPLE.getSubscriptionType(),
                initialInvestmentDtos.get(0).getAsset().getAssetId(), BigDecimal.TEN.toPlainString(), depositJson);

        Mockito.verify(subscriptionDtoService).update(Mockito.any(AccountSubscriptionDto.class), Mockito.any(ServiceErrorsImpl.class));
        Mockito.verify(subscriptionDtoService).update(subscriptionDtoArgumentCaptor.capture(), Mockito.any(ServiceErrorsImpl.class));
        Mockito.verify(depositDtoService).submit(depositDtoArgumentCaptor.capture(), Mockito.any(ServiceErrorsImpl.class));

        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(subscriptionDtoArgumentCaptor.getAllValues().size(), 1);
        assertEquals(subscriptionDtoArgumentCaptor.getValue().getSubscriptionType(), DirectOffer.SIMPLE.getSubscriptionType());
        assertEquals(subscriptionDtoArgumentCaptor.getValue().getInitialInvestments().size(), 1);
        assertEquals(depositDtoArgumentCaptor.getValue().getAmount(), BigDecimal.valueOf(120));
    }

    @Test
    public void getCashSweepDetails() throws Exception {
        Mockito.when(accountCashSweepDtoService.find(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(new AccountCashSweepDto(new AccountKey(accountId), BigDecimal.TEN, Boolean.TRUE, BigDecimal.TEN, cashSweepInvestmentDtos));
        ApiResponse response = subscriptionApiController.getCashSweepDetails(accountId);

        assertNotNull(response);
        assertNotNull(response.getData());
        AccountCashSweepDto cashSweepDto = (AccountCashSweepDto) response.getData();
        assertEquals(cashSweepDto.isCashSweepAllowed(), Boolean.TRUE);
        assertEquals(cashSweepDto.getCashSweepInvestments().size(), 1);
        assertEquals(cashSweepDto.getCashSweepInvestments().get(0).getAsset().getAssetId(), "asset1");
        assertEquals(cashSweepDto.getCashSweepInvestments().get(0).getAllocationPercent(), BigDecimal.TEN);
    }

    @Test
    public void updateCashSweepInvestments() throws Exception {
        Mockito.when(accountCashSweepDtoService.update(Mockito.any(AccountCashSweepDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(new AccountCashSweepDto(new AccountKey(accountId), BigDecimal.TEN, Boolean.TRUE, BigDecimal.TEN, cashSweepInvestmentDtos));
        ApiResponse response = subscriptionApiController.updateCashSweepInvestments(accountId, Boolean.TRUE, BigDecimal.valueOf(200), investmentList);

        Mockito.verify(accountCashSweepDtoService).update(Mockito.any(AccountCashSweepDto.class), Mockito.any(ServiceErrorsImpl.class));
        Mockito.verify(accountCashSweepDtoService).update(cashSweepDtoArgumentCaptor.capture(), Mockito.any(ServiceErrorsImpl.class));

        assertNotNull(response);
        assertNotNull(response.getData());

        AccountCashSweepDto capturedCashSweepDto = cashSweepDtoArgumentCaptor.getValue();

        assertEquals(cashSweepDtoArgumentCaptor.getAllValues().size(), 1);
        assertEquals(capturedCashSweepDto.isCashSweepAllowed(), Boolean.TRUE);
        assertEquals(capturedCashSweepDto.getMinCashSweepAmount(), BigDecimal.valueOf(200));
        assertEquals(capturedCashSweepDto.getCashSweepInvestments().size(), 2);
        assertEquals(capturedCashSweepDto.getCashSweepInvestments().get(0).getAsset().getAssetId(), "376413");
        assertEquals(capturedCashSweepDto.getCashSweepInvestments().get(0).getAllocationPercent(), BigDecimal.valueOf(55));
        assertEquals(capturedCashSweepDto.getCashSweepInvestments().get(1).getAsset().getAssetId(), "376414");
        assertEquals(capturedCashSweepDto.getCashSweepInvestments().get(1).getAllocationPercent(), BigDecimal.valueOf(45));
    }
}