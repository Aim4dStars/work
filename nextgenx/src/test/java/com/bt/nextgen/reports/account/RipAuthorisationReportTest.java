package com.bt.nextgen.reports.account;

import com.bt.nextgen.api.account.v2.model.BankAccountDto;
import com.bt.nextgen.api.account.v3.model.WrapAccountDetailDto;
import com.bt.nextgen.api.account.v3.service.WrapAccountDetailDtoService;
import com.bt.nextgen.api.asset.model.ManagedFundAssetDto;
import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.api.regularinvestment.v2.model.RegularInvestmentDto;
import com.bt.nextgen.api.regularinvestment.v2.service.AccountHelper;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.config.JsonObjectMapper;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
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
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    private WrapAccountDetailDtoService accountDetailDtoService;

    @Mock
    protected CmsService cmsService;

    private String stringifiedJsonObject;

    @Before
    public void setup() throws JsonParseException, JsonMappingException, IOException {
        stringifiedJsonObject = "{\"orders\":[{\"orderType\":\"buy\",\"amount\":100,\"asset\":{\"assetId\":\"111231\",\"type\":\"ManagedFundAsset\"},\"sellAll\":false,\"assetType\":\"Managed fund\",\"distributionMethod\":null,\"units\":null,\"fundsAllocation\":[{\"accountId\":\"B4A9B097FDD82FBCABA3FB4A1B71412EDDDD1F9D4ABF0734\",\"allocation\":1}]}],\"depositDetails\":null,\"investmentStartDate\":\"2017-01-11T16:00:00.000Z\",\"investmentEndDate\":\"2017-02-11T19:00:00.000Z\",\"frequency\":\"Monthly\",\"formattedStartDate\":\"12 Jan 2017\",\"formattedEndDate\":\"12 Feb 2017\",\"status\":null}";

        JsonObjectMapper objectMapper = new JsonObjectMapper();
        RegularInvestmentDto dto = objectMapper.readValue(stringifiedJsonObject, RegularInvestmentDto.class);
        Mockito.when(mapper.readValue(Mockito.any(String.class), Mockito.any(Class.class))).thenReturn(dto);

        Map<String, Asset> assetMap = new HashMap<>();
        Asset asset = Mockito.mock(Asset.class);
        assetMap.put("111231", asset);
        Mockito.when(assetService.loadAssets(Mockito.anyList(), Mockito.any(ServiceErrors.class))).thenReturn(assetMap);

        ManagedFundAssetDto assetDto = Mockito.mock(ManagedFundAssetDto.class);
        Mockito.when(assetDtoConverter.toAssetDto(Mockito.any(Asset.class), Mockito.any(TermDepositAssetDetail.class)))
                .thenReturn(assetDto);

        BankAccountDto bankAccountDto = Mockito.mock(BankAccountDto.class);
        Mockito.when(accHelper.getBankAccountDto(Mockito.any(WrapAccountIdentifierImpl.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(bankAccountDto);

        when(cmsService.getContent("DS-IP-0080")).thenReturn("invDisclaimer");
        when(cmsService.getContent("DS-IP-0181")).thenReturn("superDisclaimer");
    }

    @Test
    public void testGetRegularInvestmentDto_fromValidParams() throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("account-id", EncodedString.fromPlainText("accountId").toString());
        params.put("investmentOrder", stringifiedJsonObject);

        RegularInvestmentDto dto = ripAuthorisationReport.getRegularInvestment(params);

        Assert.assertNotNull(dto);
        Assert.assertEquals(1, dto.getOrders().size());
        Assert.assertEquals("buy", dto.getOrders().get(0).getOrderType());
        Assert.assertEquals(BigDecimal.valueOf(100), dto.getOrders().get(0).getAmount());

        Assert.assertEquals("Monthly", dto.getFrequency());
        Assert.assertEquals(new DateTime("2017-01-11T16:00:00.000Z").getMillis(), dto.getInvestmentStartDate().getMillis());
        Assert.assertEquals(new DateTime("2017-02-11T19:00:00.000Z").getMillis(), dto.getInvestmentEndDate().getMillis());
    }

    @Test
    public void testInit_forSuperAccount() throws IOException {

        WrapAccountDetailDto wrapDto = mock(WrapAccountDetailDto.class);
        when(wrapDto.getKey()).thenReturn(new com.bt.nextgen.api.account.v3.model.AccountKey("accountId"));
        when(wrapDto.getAccountName()).thenReturn("accountName");
        when(wrapDto.getAccountNumber()).thenReturn("accountNumber");
        when(wrapDto.getAccountType()).thenReturn("SUPER");
        when(wrapDto.getSettings()).thenReturn(Collections.EMPTY_LIST);
        when(wrapDto.getOwners()).thenReturn(Collections.EMPTY_LIST);

        when(accountDetailDtoService.search(Mockito.anyList(), any(ServiceErrors.class))).thenReturn(wrapDto);

        Map<String, String> params = new HashMap<>();
        params.put("account-id", EncodedString.fromPlainText("accountId").toString());
        params.put("investmentOrder", stringifiedJsonObject);
        ripAuthorisationReport.init(params);

        String disclaimer = ripAuthorisationReport.getDescription(params);
        Assert.assertEquals("superDisclaimer", disclaimer);
    }

    @Test
    public void testInit_forNonSuperAccount() throws IOException {

        WrapAccountDetailDto wrapDto = mock(WrapAccountDetailDto.class);
        when(wrapDto.getKey()).thenReturn(new com.bt.nextgen.api.account.v3.model.AccountKey("accountId"));
        when(wrapDto.getAccountName()).thenReturn("accountName");
        when(wrapDto.getAccountNumber()).thenReturn("accountNumber");
        when(wrapDto.getAccountType()).thenReturn("Individual");
        when(wrapDto.getSettings()).thenReturn(Collections.EMPTY_LIST);
        when(wrapDto.getOwners()).thenReturn(Collections.EMPTY_LIST);

        when(accountDetailDtoService.search(Mockito.anyList(), any(ServiceErrors.class))).thenReturn(wrapDto);

        Map<String, String> params = new HashMap<>();
        params.put("account-id", EncodedString.fromPlainText("accountId").toString());
        params.put("investmentOrder", stringifiedJsonObject);
        ripAuthorisationReport.init(params);

        String disclaimer = ripAuthorisationReport.getDescription(params);
        Assert.assertEquals("invDisclaimer", disclaimer);
    }
}
