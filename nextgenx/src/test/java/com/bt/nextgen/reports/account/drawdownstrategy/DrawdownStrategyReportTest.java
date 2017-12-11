package com.bt.nextgen.reports.account.drawdownstrategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.drawdown.v2.model.DrawdownDetailsDto;
import com.bt.nextgen.api.drawdown.v2.service.DrawdownDetailsDtoService;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationServiceFactory;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.drawdownstrategy.DrawdownStrategy;

@RunWith(MockitoJUnitRunner.class)
public class DrawdownStrategyReportTest {
    // CMS
    private static final String DECLARATION = "DS-IP-0103";
    private static final String SUPER_DECLARATION = "DS-IP-0185";
    private static final String DRAWDOWN_STRATEGY_DESCRIPTION = "Ins-IP-0311";
    private static final String HIGH_VALUE_EXPLANATION_GRAPHIC = "drawdownExplanationHighValue";
    private static final String HIGH_VALUE_EXPLANATION_1 = "Ins-IP-0093";
    private static final String HIGH_VALUE_EXPLANATION_2 = "Ins-IP-0094";
    private static final String HIGH_VALUE_EXPLANATION_3 = "Ins-IP-0095";
    private static final String PRO_RATA_EXPLANATION_GRAPHIC = "drawdownExplanationProRata";
    private static final String PRO_RATA_EXPLANATION_1 = "Ins-IP-0096";
    private static final String PRO_RATA_EXPLANATION_2 = "Ins-IP-0097";
    private static final String PRO_RATA_EXPLANATION_3 = "Ins-IP-0098";
    @InjectMocks
    private DrawdownStrategyReport drawdownStrategyReport;
    @Mock
    private DrawdownStrategyReportDataConverter converter;
    @Mock
    private DrawdownDetailsDtoService drawdownDetailsDtoService;
    @Mock
    private JsonObjectMapper jsonObjectMapper;
    @Mock
    private CmsService cmsService;
    @Mock
    private AccountIntegrationService accountIntegrationService;
    @Mock
    private AccountIntegrationServiceFactory accountIntegrationServiceFactory;
    private String accountId = "975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0";
    private String priorityListJson = "{}";

    @Before
    public void setup() {
        Mockito.when(converter.toReportData(Mockito.any(DrawdownStrategy.class), Mockito.any(DrawdownDetailsDto.class)))
               .thenAnswer(new Answer<DrawdownStrategyReportData>() {

                   @Override
                   public DrawdownStrategyReportData answer(InvocationOnMock invocation) throws Throwable {
                       if (invocation.getArgumentAt(0, DrawdownStrategy.class).equals(DrawdownStrategy.ASSET_PRIORITY)) {
                           if (invocation.getArgumentAt(1, DrawdownDetailsDto.class) != null) {
                               return mockReportDataWithPriorityList();
                           }
                           return null;
                       } else {
                           return mockReportData(invocation.getArgumentAt(0, DrawdownStrategy.class));
                       }
                   }
               });

        Mockito.when(cmsService.getContent(Mockito.anyString())).thenAnswer(new Answer<String>() {

            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgumentAt(0, String.class);
            }
        });

        Mockito.when(accountIntegrationServiceFactory.getInstance(Mockito.anyString())).thenReturn(accountIntegrationService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetData_whenAccountIdNotProvided_thenThrowException() {
        Map<String, Object> params = new HashMap<>();
        params.put("account-id", null);
        params.put("drawdown", "asset_with_highest_price");
        drawdownStrategyReport.getData(params, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetData_whenDrawdownNotProvided_thenThrowException() {
        Map<String, Object> params = new HashMap<>();
        params.put("account-id", accountId);
        params.put("drawdown", null);
        drawdownStrategyReport.getData(params, null);
    }

    @Test
    public void testGetData_whenPriorityListNotRelevant_thenPriorityListNotPopulated() {
        Map<String, Object> params = new HashMap<>();
        params.put("account-id", accountId);
        params.put("drawdown", "prorata");

        List<DrawdownStrategyReportData> reportData = (List<DrawdownStrategyReportData>) drawdownStrategyReport.getData(params,
                null);

        Assert.assertEquals(DrawdownStrategy.PRORATA.getDisplayName(), reportData.get(0).getDrawdownStrategy());
        Assert.assertEquals(0, reportData.get(0).getAssetPriorityList().size());
    }

    @Test
    public void testGetData_whenAssetPriorityListNotProvided_thenLoadFromDto() {
        Map<String, Object> params = new HashMap<>();
        params.put("account-id", accountId);
        params.put("drawdown", "individual_assets");

        Mockito.when(drawdownDetailsDtoService.find(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class))).thenReturn(
                Mockito.mock(DrawdownDetailsDto.class));

        List<DrawdownStrategyReportData> reportData = (List<DrawdownStrategyReportData>) drawdownStrategyReport.getData(params,
                null);

        Assert.assertEquals(DrawdownStrategy.ASSET_PRIORITY.getDisplayName(), reportData.get(0).getDrawdownStrategy());
        Assert.assertEquals(1, reportData.get(0).getAssetPriorityList().size());
    }

    @Test
    public void testGetData_whenAssetPriorityListProvided_thenParseFromJson() throws JsonParseException, JsonMappingException,
            IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("account-id", accountId);
        params.put("drawdown", "individual_assets");
        params.put("priority-list", priorityListJson);

        Mockito.when(jsonObjectMapper.readValue(Mockito.anyString(), Mockito.any(Class.class))).thenReturn(
                Mockito.mock(DrawdownDetailsDto.class));

        List<DrawdownStrategyReportData> reportData = (List<DrawdownStrategyReportData>) drawdownStrategyReport.getData(params,
                null);

        Assert.assertEquals(DrawdownStrategy.ASSET_PRIORITY.getDisplayName(), reportData.get(0).getDrawdownStrategy());
        Assert.assertEquals(1, reportData.get(0).getAssetPriorityList().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetData_whenParseFails_thenExpectException() throws JsonParseException, JsonMappingException, IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("account-id", accountId);
        params.put("drawdown", "individual_assets");
        params.put("priority-list", priorityListJson);

        Mockito.when(jsonObjectMapper.readValue(Mockito.anyString(), Mockito.any(Class.class))).thenThrow(IOException.class);

        List<DrawdownStrategyReportData> reportData = (List<DrawdownStrategyReportData>) drawdownStrategyReport.getData(params,
                null);
    }

    @Test
    public void testGetDeclaration() {
        WrapAccountDetail account = Mockito.mock(WrapAccountDetail.class);
        Mockito.when(account.getAccountStructureType()).thenReturn(AccountStructureType.SUPER);
        Mockito.when(
                accountIntegrationService.loadWrapAccountDetail(
                        Mockito.any(com.bt.nextgen.service.integration.account.AccountKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(account);

        Map<String, Object> params = new HashMap<>();
        params.put("account-id", accountId);

        String declaration = drawdownStrategyReport.getDeclaration(params);
        Assert.assertEquals(SUPER_DECLARATION, declaration);

        Mockito.when(account.getAccountStructureType()).thenReturn(AccountStructureType.Trust);

        declaration = drawdownStrategyReport.getDeclaration(params);
        Assert.assertEquals(DECLARATION, declaration);
    }

    @Test
    public void testGetExplanations_whenPriorityListRelevant_thenExplanationsNotRelevant() {
        Map<String, String> params = new HashMap<>();
        params.put("drawdown", "individual_assets");

        Assert.assertNull(drawdownStrategyReport.getDrawdownExplanationGraphic(params));
        Assert.assertNull(drawdownStrategyReport.getExplanationStepOne(params));
        Assert.assertNull(drawdownStrategyReport.getExplanationStepTwo(params));
        Assert.assertNull(drawdownStrategyReport.getExplanationStepThree(params));
    }

    @Test
    public void testGetExplanations_whenHighValueStrategy_thenCorrectExplanationsReturned() {
        Map<String, String> params = new HashMap<>();
        params.put("drawdown", "asset_with_highest_price");

        Assert.assertEquals(HIGH_VALUE_EXPLANATION_1, drawdownStrategyReport.getExplanationStepOne(params));
        Assert.assertEquals(HIGH_VALUE_EXPLANATION_2, drawdownStrategyReport.getExplanationStepTwo(params));
        Assert.assertEquals(HIGH_VALUE_EXPLANATION_3, drawdownStrategyReport.getExplanationStepThree(params));
    }

    @Test
    public void testGetExplanations_whenProrataStrategy_thenCorrectExplanationsReturned() {
        Map<String, String> params = new HashMap<>();
        params.put("drawdown", "prorata");

        Assert.assertEquals(PRO_RATA_EXPLANATION_1, drawdownStrategyReport.getExplanationStepOne(params));
        Assert.assertEquals(PRO_RATA_EXPLANATION_2, drawdownStrategyReport.getExplanationStepTwo(params));
        Assert.assertEquals(PRO_RATA_EXPLANATION_3, drawdownStrategyReport.getExplanationStepThree(params));
    }

    private DrawdownStrategyReportData mockReportDataWithPriorityList() {
        AssetPriorityReportData priority = Mockito.mock(AssetPriorityReportData.class);
        Mockito.when(priority.getAssetTitle()).thenReturn("AssetTitle");
        Mockito.when(priority.getPriority()).thenReturn("1");
        Mockito.when(priority.getMarketValue()).thenReturn("-");

        List<AssetPriorityReportData> priorityList = new ArrayList<>();
        priorityList.add(priority);

        DrawdownStrategyReportData reportData = Mockito.mock(DrawdownStrategyReportData.class);
        Mockito.when(reportData.getDrawdownStrategy()).thenReturn(DrawdownStrategy.ASSET_PRIORITY.getDisplayName());
        Mockito.when(reportData.getAssetPriorityList()).thenReturn(priorityList);

        return reportData;
    }

    private DrawdownStrategyReportData mockReportData(DrawdownStrategy strategy) {
        DrawdownStrategyReportData reportData = Mockito.mock(DrawdownStrategyReportData.class);
        Mockito.when(reportData.getDrawdownStrategy()).thenReturn(strategy.getDisplayName());

        return reportData;
    }
}
