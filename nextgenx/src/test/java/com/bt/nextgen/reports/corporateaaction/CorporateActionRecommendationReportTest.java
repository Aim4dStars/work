package com.bt.nextgen.reports.corporateaaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDtoKey;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionDetailsDtoService;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.reports.corporateaction.CorporateActionRecommendationReport;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionRecommendationReportTest {
    @InjectMocks
    private CorporateActionRecommendationReport corporateActionRecommendationReport;

    @Mock
    private CorporateActionDetailsDtoService corporateActionDetailsDtoService;

    @Mock
    private CmsService cmsService;

    @Mock
    private CorporateActionDetailsDto corporateActionDetailsDto;

    @Mock
    private CorporateActionAccountDetailsDto accountDetailsDto;

    private Map<String, String> params = new HashMap<>();

    @Before
    public void setup() {
        params.put("ca-id", EncodedString.fromPlainText("0").toString());
        params.put(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, EncodedString.fromPlainText("0").toString());
        params.put(UriMappingConstants.CORPORATE_ACTION_SUMMARY_URI_MAPPING, "Summary string");

        when(accountDetailsDto.getAccountKey()).thenReturn(EncodedString.fromPlainText("0").toString());
        when(corporateActionDetailsDto.getAccounts()).thenReturn(Arrays.asList(accountDetailsDto));
        when(corporateActionDetailsDto.getSummary()).thenReturn(Arrays.asList("Summary"));
        when(corporateActionDetailsDto.getCorporateActionPrice()).thenReturn(BigDecimal.valueOf(0.59));
        when(corporateActionDetailsDto.getOptions()).thenReturn(new ArrayList<CorporateActionOptionDto>());
        when(corporateActionDetailsDtoService.find(any(CorporateActionDtoKey.class), any(ServiceErrors.class))).thenReturn
                (corporateActionDetailsDto);

        when(cmsService.getContent(anyString())).thenReturn("content");
    }

    @Test
    public void testInit() {
        corporateActionRecommendationReport.init(params);
    }

    @Test
    public void testDescription() {
        assertEquals("content", corporateActionRecommendationReport.getDescription(params));
    }

    @Test
    public void testReportName() {
        assertEquals("Client authorisation", corporateActionRecommendationReport.getReportName(params));
    }

    @Test
    public void testSubReportName() {
        assertEquals("Corporate action recommendation", corporateActionRecommendationReport.getSubReportName(params));
    }

    @Test
    public void testCorporateActionDetail() {
        assertNotNull(corporateActionRecommendationReport.getCorporateActionDetail(params));
    }

    @Test
    public void testCorporateActionAccountDetails() {
        assertNotNull(corporateActionRecommendationReport.getCorporateActionAccountDetails(params));

        when(accountDetailsDto.getAccountKey()).thenReturn(EncodedString.fromPlainText("1").toString());
        assertNull(corporateActionRecommendationReport.getCorporateActionAccountDetails(params));
    }

    @Test
    public void testCorporateActionSummary() {
        List<String> summaryList = corporateActionRecommendationReport.getCorporateActionSummary(params);

        assertEquals("Summary", summaryList.get(0));
    }

    @Test
    public void testCorporateActionOption() {
        assertNotNull(corporateActionRecommendationReport.getCorporateActionOption(params));
    }

    @Test
    public void testGetCorporateActionRecommendedSummary_whenMultiBlock_thenReturnMultiBlockText() {
        params.put(UriMappingConstants.CORPORATE_ACTION_SUMMARY_URI_MAPPING, "Multi-block");

        when(corporateActionDetailsDto.getCorporateActionType()).thenReturn(CorporateActionType.MULTI_BLOCK.name());

        List<String> recommendedSummary = corporateActionRecommendationReport.getCorporateActionRecommendedSummary(params);

        assertEquals("Multi-block", recommendedSummary.get(0));
    }

    @Test
    public void testGetCorporateActionRecommendedSummary_whenFullExercise_thenReturnFullExerciseRightsText() {
        params.put(UriMappingConstants.CORPORATE_ACTION_SUMMARY_URI_MAPPING, "Full Exercise");
        params.put(UriMappingConstants.CORPORATE_ACTION_OPTION_OVERSUBSCRIBE_MAPPING, "20");
        params.put(UriMappingConstants.CORPORATE_ACTION_OPTION_URI_MAPPING, "3");
        params.put(UriMappingConstants.CORPORATE_ACTION_OPTION_UNITS_MAPPING, "10");

        when(corporateActionDetailsDto.getCorporateActionType()).thenReturn(CorporateActionType.EXERCISE_RIGHTS.name());

        List<String> recommendedSummary = corporateActionRecommendationReport.getCorporateActionRecommendedSummary(params);

        assertEquals("Full Exercise - 30 unit(s) @ $0.59 = $17.70  Additional new shares 20 unit(s)", recommendedSummary.get(0));

        when(corporateActionDetailsDto.getCorporateActionType()).thenReturn(CorporateActionType.PRO_RATA_PRIORITY_OFFER.name());
        recommendedSummary = corporateActionRecommendationReport.getCorporateActionRecommendedSummary(params);
        assertEquals("Full Exercise - 30 unit(s) @ $0.59 = $17.70  Additional new shares 20 unit(s)", recommendedSummary.get(0));

        when(corporateActionDetailsDto.getCorporateActionType()).thenReturn(CorporateActionType.EXERCISE_CALL_OPTION.name());
        recommendedSummary = corporateActionRecommendationReport.getCorporateActionRecommendedSummary(params);
        assertEquals("Full Exercise - 30 unit(s) @ $0.59 = $17.70  Additional new shares 20 unit(s)", recommendedSummary.get(0));

        when(corporateActionDetailsDto.getCorporateActionType()).thenReturn(CorporateActionType.EXERCISE_RIGHTS_WITH_OPT.name());
        recommendedSummary = corporateActionRecommendationReport.getCorporateActionRecommendedSummary(params);
        assertEquals("Full Exercise - 30 unit(s) @ $0.59 = $17.70  Additional new shares 20 unit(s)", recommendedSummary.get(0));

        params.put(UriMappingConstants.CORPORATE_ACTION_OPTION_OVERSUBSCRIBE_MAPPING, "0");
        recommendedSummary = corporateActionRecommendationReport.getCorporateActionRecommendedSummary(params);
        assertEquals("Full Exercise - 10 unit(s) @ $0.59 = $5.90", recommendedSummary.get(0));

        when(corporateActionDetailsDto.getOversubscribe()).thenReturn(Boolean.TRUE);
        recommendedSummary = corporateActionRecommendationReport.getCorporateActionRecommendedSummary(params);
        assertEquals("Full Exercise - 10 unit(s) @ $0.59 = $5.90  Additional new shares 0 unit", recommendedSummary.get(0));
    }

    @Test
    public void testGetCorporateActionRecommendedSummary_whenPartialExercise_thenReturnPartialExerciseText() {
        params.put(UriMappingConstants.CORPORATE_ACTION_SUMMARY_URI_MAPPING, "Partial");
        params.put(UriMappingConstants.CORPORATE_ACTION_OPTION_URI_MAPPING, "2");
        params.put(UriMappingConstants.CORPORATE_ACTION_OPTION_UNITS_MAPPING, "10");

        when(corporateActionDetailsDto.getCorporateActionType()).thenReturn(CorporateActionType.EXERCISE_RIGHTS.name());

        List<String> recommendedSummary = corporateActionRecommendationReport.getCorporateActionRecommendedSummary(params);

        assertEquals("Partial - 10 unit(s) @ $0.59 = $5.90", recommendedSummary.get(0));
    }

    @Test
    public void testGetCorporateActionRecommendedSummary_whenFullExerciseButNoPrice_thenReturnFullExerciseRightsText() {
        params.put(UriMappingConstants.CORPORATE_ACTION_SUMMARY_URI_MAPPING, "Full Exercise");
        params.put(UriMappingConstants.CORPORATE_ACTION_OPTION_OVERSUBSCRIBE_MAPPING, "20");
        params.put(UriMappingConstants.CORPORATE_ACTION_OPTION_URI_MAPPING, "3");
        params.put(UriMappingConstants.CORPORATE_ACTION_OPTION_UNITS_MAPPING, "10");

        when(corporateActionDetailsDto.getCorporateActionPrice()).thenReturn(null);
        when(corporateActionDetailsDto.getCorporateActionType()).thenReturn(CorporateActionType.EXERCISE_RIGHTS.name());

        List<String> recommendedSummary = corporateActionRecommendationReport.getCorporateActionRecommendedSummary(params);

        assertEquals("Full Exercise - 30 unit(s) @ $0 = $0  Additional new shares 20 unit(s)", recommendedSummary.get(0));
    }

    @Test
    public void testGetCorporateActionRecommendedSummary_whenLapsedExercise_thenReturnLapsedExerciseText() {
        params.put(UriMappingConstants.CORPORATE_ACTION_SUMMARY_URI_MAPPING, "Lapse");
        params.put(UriMappingConstants.CORPORATE_ACTION_OPTION_URI_MAPPING, "1");

        when(corporateActionDetailsDto.getCorporateActionType()).thenReturn(CorporateActionType.EXERCISE_RIGHTS.name());

        List<String> recommendedSummary = corporateActionRecommendationReport.getCorporateActionRecommendedSummary(params);

        assertEquals("Lapse", recommendedSummary.get(0));
    }

    @Test
    public void testGetCorporateActionRecommendedSummary_whenBuyback_thenReturnBuyBackText() {
        params.put(UriMappingConstants.CORPORATE_ACTION_SUMMARY_URI_MAPPING, "Full,Partial,Lapse");
        params.put(UriMappingConstants.CORPORATE_ACTION_OPTION_UNITS_MAPPING, "10, 20, 30");
        params.put(UriMappingConstants.CORPORATE_ACTION_OPTION_URI_MAPPING, "3,2,1");
        params.put(UriMappingConstants.CORPORATE_ACTION_ID_URI_MAPPING, "078F653237B8A38CC39C2AACD451BE37F611F7B06204C2DA");

        when(corporateActionDetailsDto.getCorporateActionType()).thenReturn(CorporateActionType.BUY_BACK.name());

        List<String> recommendedSummary = corporateActionRecommendationReport.getCorporateActionRecommendedSummary(params);
        assertEquals(recommendedSummary.get(0), "Full - 10 units ");
    }

    @Test
    public void testGetCorporateActionRecommendedTitle_whenNotBuyBack_thenReturnStandardText() {
        params.put(UriMappingConstants.CORPORATE_ACTION_TITLE_URI_MAPPING, "test");

        when(corporateActionDetailsDto.getCorporateActionType()).thenReturn(CorporateActionType.MULTI_BLOCK.name());

        assertEquals("test", corporateActionRecommendationReport.getCorporateActionRecommendedTitle(params).get(0));
    }

    @Test
    public void testGetCorporateActionRecommendedTitle_whenBuyBack_thenReturnBuyBackText() {
        params.put(UriMappingConstants.CORPORATE_ACTION_TITLE_URI_MAPPING, "Option A, Option B");
        params.put(UriMappingConstants.CORPORATE_ACTION_OPTION_UNITS_MAPPING, "10");
        params.put(UriMappingConstants.CORPORATE_ACTION_OPTION_URI_MAPPING, "3");
        params.put(UriMappingConstants.CORPORATE_ACTION_ID_URI_MAPPING, "078F653237B8A38CC39C2AACD451BE37F611F7B06204C2DA");

        when(corporateActionDetailsDto.getCorporateActionType()).thenReturn(CorporateActionType.BUY_BACK.name());

        List<String> titleString = corporateActionRecommendationReport.getCorporateActionRecommendedTitle(params);
        assertEquals(titleString.get(0), "Option A");
        assertEquals(titleString.get(1), "Option B");
    }

    @Test
    public void testGetCorporateActionMinimumPrice_whenBuyBack_thenReturnMinimumPrice() {
        params.put(UriMappingConstants.CORPORATE_ACTION_OPTION_MINIMUM_PRICE, "50");

        when(corporateActionDetailsDto.getCorporateActionType()).thenReturn(CorporateActionType.BUY_BACK.name());

        assertEquals("50", corporateActionRecommendationReport.getCorporateActionMinimumPrice(params));
    }

    @Test
    public void testGetCorporateActionMinimumPrice_whenNotBuyBack_thenReturnEmptyString() {
        when(corporateActionDetailsDto.getCorporateActionType()).thenReturn(CorporateActionType.MULTI_BLOCK.name());

        assertEquals("", corporateActionRecommendationReport.getCorporateActionMinimumPrice(params));
    }
}
