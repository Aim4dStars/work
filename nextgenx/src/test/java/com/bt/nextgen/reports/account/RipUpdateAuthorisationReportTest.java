package com.bt.nextgen.reports.account;

import com.bt.nextgen.api.account.v3.model.WrapAccountDetailDto;
import com.bt.nextgen.api.account.v3.service.WrapAccountDetailDtoService;
import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.api.regularinvestment.v2.model.RegularInvestmentDto;
import com.bt.nextgen.api.regularinvestment.v2.service.AccountHelper;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.config.JsonObjectMapper;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RipUpdateAuthorisationReportTest {

    @InjectMocks
    private RipUpdateAuthorisationReport ripReport;

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

        when(cmsService.getContent("DS-IP-0080")).thenReturn("disclaimer");
        when(cmsService.getContent("DS-IP-0181")).thenReturn("superDisclaimer");

        when(cmsService.getContent("DS-IP-0090")).thenReturn("cancelDisclaimer");
        when(cmsService.getContent("DS-IP-0182")).thenReturn("superCancelDisclaimer");

        when(cmsService.getContent("DS-IP-0091")).thenReturn("suspendDisclaimer");
        when(cmsService.getContent("DS-IP-0183")).thenReturn("superSuspendDisclaimer");

        when(cmsService.getContent("DS-IP-0092")).thenReturn("renewDisclaimer");
        when(cmsService.getContent("DS-IP-0184")).thenReturn("superRenewDisclaimer");
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
        ripReport.init(params);

        String disclaimer = ripReport.getDescription(params);
        Assert.assertEquals("superDisclaimer", disclaimer);

        params.put("action", "suspend");
        disclaimer = ripReport.getDescription(params);
        Assert.assertEquals("superSuspendDisclaimer", disclaimer);

        params.put("action", "cancel");
        disclaimer = ripReport.getDescription(params);
        Assert.assertEquals("superCancelDisclaimer", disclaimer);

        params.put("action", "activate");
        disclaimer = ripReport.getDescription(params);
        Assert.assertEquals("superRenewDisclaimer", disclaimer);

        params.put("action", "unspecified");
        disclaimer = ripReport.getDescription(params);
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
        ripReport.init(params);

        String disclaimer = ripReport.getDescription(params);
        Assert.assertEquals("disclaimer", disclaimer);

        params.put("action", "suspend");
        disclaimer = ripReport.getDescription(params);
        Assert.assertEquals("suspendDisclaimer", disclaimer);

        params.put("action", "cancel");
        disclaimer = ripReport.getDescription(params);
        Assert.assertEquals("cancelDisclaimer", disclaimer);

        params.put("action", "activate");
        disclaimer = ripReport.getDescription(params);
        Assert.assertEquals("renewDisclaimer", disclaimer);

        params.put("action", "unknown");
        disclaimer = ripReport.getDescription(params);
        Assert.assertEquals("disclaimer", disclaimer);
    }
}
