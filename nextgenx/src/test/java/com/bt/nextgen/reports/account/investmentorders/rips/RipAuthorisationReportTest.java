package com.bt.nextgen.reports.account.investmentorders.rips;

import com.bt.nextgen.api.account.v2.model.BankAccountDto;
import com.bt.nextgen.api.account.v3.service.WrapAccountDetailDtoService;
import com.bt.nextgen.api.asset.model.ManagedFundAssetDto;
import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.api.regularinvestment.v2.model.RegularInvestmentDto;
import com.bt.nextgen.api.regularinvestment.v2.service.AccountHelper;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.config.JsonObjectMapper;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.reports.account.investmentorders.ordercapture.OrderItemData;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationServiceFactory;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.options.model.OptionKey;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class RipAuthorisationReportTest {

    @InjectMocks
    private RipAuthorisationReport ripAuthorisationReport;

    @Mock
    @Qualifier("jsonObjectMapper")
    private ObjectMapper mapper;

    @Mock
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Mock
    private AssetDtoConverter assetDtoConverter;

    @Mock
    private AccountHelper accHelper;

    @Mock
    private OptionsService optionsService;

    @Mock
    private CmsService cmsService;

    @Mock
    private WrapAccountDetailDtoService accountDetailDtoService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private AccountIntegrationServiceFactory accountIntegrationServiceFactory;

    private static final String DECLARATION = "DS-IP-0080";
    private static final String SUPER_DECLARATION = "DS-IP-0181";
    private String stringifiedJsonObject = "{\"orders\":[{\"orderType\":\"buy\",\"amount\":500,\"asset\":{\"assetId\":\"111940\",\"type\":\"ManagedPortfolioAsset\"},\"sellAll\":false,\"assetType\":\"Managed portfolio\",\"distributionMethod\":null,\"units\":null,\"fundsAllocation\":[{\"accountId\":\"70D9686153B398C4CF2BD8D77C984DFC957E1E3D7F64659A\",\"allocation\":1}]},{\"orderType\":\"buy\",\"amount\":500,\"asset\":{\"assetId\":\"547640\",\"type\":\"ManagedPortfolioAsset\"},\"sellAll\":false,\"assetType\":\"Managed portfolio\",\"distributionMethod\":null,\"units\":null,\"fundsAllocation\":[{\"accountId\":\"70D9686153B398C4CF2BD8D77C984DFC957E1E3D7F64659A\",\"allocation\":1}]}],\"depositDetails\":{\"isRecurring\":true,\"amount\":1000,\"description\":\"description\",\"frequency\":\"Monthly\",\"transactionDate\":\"31 May 2017\",\"repeatEndDate\":null,\"fromPayDto\":{\"accountId\":\"123456\",\"accountName\":\"Self\",\"nickname\":null,\"code\":\"736-013\",\"payeeType\":null,\"primary\":true},\"toPayeeDto\":{\"accountId\":\"120112289\",\"accountName\":\"ABC Pmgmt\",\"code\":\"262786\"}},\"investmentStartDate\":\"2017-05-30T16:00:00.000Z\",\"investmentEndDate\":null,\"frequency\":\"Monthly\",\"status\":null}";
    private String stringifiedJsonObjectNoRegularDeposit = "{\"orders\":[{\"orderType\":\"buy\",\"amount\":500,\"asset\":{\"assetId\":\"111940\",\"type\":\"ManagedPortfolioAsset\"},\"sellAll\":false,\"assetType\":\"Managed portfolio\",\"distributionMethod\":null,\"units\":null,\"fundsAllocation\":[{\"accountId\":\"70D9686153B398C4CF2BD8D77C984DFC957E1E3D7F64659A\",\"allocation\":1}]},{\"orderType\":\"buy\",\"amount\":500,\"asset\":{\"assetId\":\"547640\",\"type\":\"ManagedPortfolioAsset\"},\"sellAll\":false,\"assetType\":\"Managed portfolio\",\"distributionMethod\":null,\"units\":null,\"fundsAllocation\":[{\"accountId\":\"70D9686153B398C4CF2BD8D77C984DFC957E1E3D7F64659A\",\"allocation\":1}]}],\"investmentStartDate\":\"2017-05-30T16:00:00.000Z\",\"investmentEndDate\":\"2017-05-30T16:00:00.000Z\",\"frequency\":\"Monthly\",\"status\":null}";

    @Before
    public void setup() throws JsonParseException, JsonMappingException, IOException {
        JsonObjectMapper objectMapper = new JsonObjectMapper();
        RegularInvestmentDto dto = objectMapper.readValue(stringifiedJsonObject, RegularInvestmentDto.class);
        Mockito.when(mapper.readValue(Mockito.any(String.class), Mockito.any(Class.class))).thenReturn(dto);

        Map<String, Asset> assetMap = new HashMap<>();
        Asset asset = Mockito.mock(Asset.class);
        Mockito.when(asset.getAssetCode()).thenReturn("BT001");
        Mockito.when(asset.getAssetName()).thenReturn("BT Conservative Managed Portfolio");
        assetMap.put("547640", asset);
        Mockito.when(assetService.loadAssets(Mockito.anyList(), Mockito.any(ServiceErrors.class))).thenReturn(assetMap);

        ManagedFundAssetDto assetDto = Mockito.mock(ManagedFundAssetDto.class);
        Mockito.when(assetDto.getAssetCode()).thenReturn("BT001");
        Mockito.when(assetDto.getAssetName()).thenReturn("BT Conservative Managed fund");
        Mockito.when(assetDtoConverter.toAssetDto(Mockito.any(Asset.class), Mockito.any(TermDepositAssetDetail.class)))
                .thenReturn(assetDto);

        BankAccountDto bankAccountDto = Mockito.mock(BankAccountDto.class);
        Mockito.when(bankAccountDto.getBsb()).thenReturn("262-786");
        Mockito.when(bankAccountDto.getAccountNumber()).thenReturn("120112289");
        Mockito.when(accHelper.getBankAccountDto(Mockito.any(WrapAccountIdentifierImpl.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(bankAccountDto);

        WrapAccountDetail accountDetail = Mockito.mock(WrapAccountDetail.class);
        Mockito.when(accountDetail.getAccountName()).thenReturn("account name");
        Mockito.when(accountDetail.getAccountNumber()).thenReturn("02323222");

        Mockito.when(
                accountIntegrationService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(accountDetail);

        Mockito.when(accountIntegrationServiceFactory.getInstance(anyString())).thenReturn(accountIntegrationService);

        Mockito.when(cmsService.getContent(DECLARATION)).thenReturn(DECLARATION);
        Mockito.when(cmsService.getContent(SUPER_DECLARATION)).thenReturn(SUPER_DECLARATION);
    }

    @Test
    public void testGetGenericText() {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> dataCollections = new HashMap<>();

        Assert.assertEquals("Client authorisation - regular investment plan",
                ripAuthorisationReport.getReportType(params, dataCollections));

        Assert.assertEquals("Your client authorisation for a regular investment plan",
                ripAuthorisationReport.getReportTitle(params, dataCollections));
    }

    @Test
    public void testGetDeclaration_whenDeclarationRequestedForInvestmentAccount_thenItMatchesTheCurrentAccountType() {
        Map<String, Object> params = new HashMap<>();
        params.put("account-id", EncodedString.fromPlainText("accountId").toString());

        Mockito.when(
                optionsService.hasFeature(Mockito.any(OptionKey.class), Mockito.any(AccountKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(true);

        Assert.assertEquals(DECLARATION, ripAuthorisationReport.getDeclaration(params));
    }

    @Test
    public void testGetDeclaration_whenDeclarationRequestedForSuperAccount_thenItMatchesTheCurrentAccountType() {
        Map<String, Object> params = new HashMap<>();
        params.put("account-id", EncodedString.fromPlainText("accountId").toString());

        Mockito.when(
                optionsService.hasFeature(Mockito.any(OptionKey.class), Mockito.any(AccountKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(false);

        Assert.assertEquals(SUPER_DECLARATION, ripAuthorisationReport.getDeclaration(params));
    }

    @Test
    public void testGetRegularInvestmentReportData_fromValidParams() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("account-id", EncodedString.fromPlainText("accountId").toString());
        params.put("investmentOrder", stringifiedJsonObject);

        Map<String, Object> dataCollections = new HashMap<>();

        Collection<?> ripData = ripAuthorisationReport.getData(params, dataCollections);

        RegularInvestmentReportData reportDataItem = (RegularInvestmentReportData) ripData.iterator().next();
        Assert.assertNotNull(reportDataItem);
        Assert.assertEquals("This regular investment plan is currently inactive", reportDataItem.getStatus());

        // Deposit details
        Assert.assertTrue(reportDataItem.getShowDepositDetails());
        Assert.assertEquals("123456 Self", reportDataItem.getDepositPayer());
        Assert.assertEquals("BSB 736-013 Account no. 123456", reportDataItem.getDepositPayerCashAccount());
        Assert.assertEquals("Deposit <b>$1,000.00</b> starting <b>31 May 2017</b>, monthly", reportDataItem.getDepositDetails());
        Assert.assertEquals("ABC Pmgmt investment account 120112289", reportDataItem.getDepositPayee());
        Assert.assertEquals("BSB 262-786 Account no. 120112289", reportDataItem.getDepositPayeeCashAccount());

        // Investment details
        Assert.assertFalse(reportDataItem.getShowDateRange());
        Assert.assertEquals("From 30 May 2017 to -", reportDataItem.getInvestmentDateRange());

        Assert.assertEquals("account name 02323222", reportDataItem.getInvestmentAccount());
        Assert.assertEquals("BSB 262-786 Account no. 120112289", reportDataItem.getInvestmentCashAccount());
        Assert.assertEquals("Invest <b>$1,000.00</b> starting <b>30 May 2017</b>, monthly", reportDataItem.getInvestmentDetails());

        Assert.assertEquals("$1,000.00", reportDataItem.getInvestmentAmount());

        Assert.assertEquals(2, reportDataItem.getOrders().size());
        OrderItemData order = reportDataItem.getOrders().get(0);
        Assert.assertEquals("BT001", order.getAssetCode());
        Assert.assertEquals("BT Conservative Managed fund", order.getRawAssetName());
        Assert.assertEquals("$500.00", order.getRawAmount());
    }

    @Test
    public void testGetRegularInvestmentReportData_WhenNoDepositDetails_ThenDepositFieldsNull() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("account-id", EncodedString.fromPlainText("accountId").toString());
        params.put("investmentOrder", stringifiedJsonObjectNoRegularDeposit);

        Map<String, Object> dataCollections = new HashMap<>();

        JsonObjectMapper objectMapper = new JsonObjectMapper();
        RegularInvestmentDto dto = objectMapper.readValue(stringifiedJsonObjectNoRegularDeposit, RegularInvestmentDto.class);
        Mockito.when(mapper.readValue(Mockito.any(String.class), Mockito.any(Class.class))).thenReturn(dto);

        Collection<?> ripData = ripAuthorisationReport.getData(params, dataCollections);

        RegularInvestmentReportData reportData = (RegularInvestmentReportData) ripData.iterator().next();
        Assert.assertFalse(reportData.getShowDepositDetails());
        Assert.assertNull(reportData.getDepositPayer());
        Assert.assertNull(reportData.getDepositPayerCashAccount());
        Assert.assertNull(reportData.getDepositDetails());
        Assert.assertNull(reportData.getDepositPayee());
        Assert.assertNull(reportData.getDepositPayeeCashAccount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetRegularInvestmentReportData_IllegalJSONMessage() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("account-id", EncodedString.fromPlainText("accountId").toString());
        params.put("investmentOrder", stringifiedJsonObject);

        Map<String, Object> dataCollections = new HashMap<>();

        Mockito.when(mapper.readValue(anyString(), Mockito.any(Class.class))).thenThrow(IOException.class);
        
        Collection<?> ripData = ripAuthorisationReport.getData(params, dataCollections);
    }
}
