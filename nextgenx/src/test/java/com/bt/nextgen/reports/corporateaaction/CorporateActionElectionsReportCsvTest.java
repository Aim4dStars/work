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
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountElectionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountElectionsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionBuyBackAccountElectionDtoImpl;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsBaseDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDtoKey;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionExerciseRightsAccountElectionDtoImpl;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionDetailsDtoService;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionConverterConstants;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.corporateaction.service.converter.AbstractCorporateActionConverterTest;
import com.bt.nextgen.reports.corporateaction.CorporateActionElectionCsvHelper;
import com.bt.nextgen.reports.corporateaction.CorporateActionElectionsReportCsv;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionExerciseRightsType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionType;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.bt.nextgen.service.integration.userinformation.ClientKey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionElectionsReportCsvTest extends AbstractCorporateActionConverterTest {
    @InjectMocks
    private CorporateActionElectionsReportCsv corporateActionElectionsReportCsv;

    @Mock
    private CorporateActionDetailsDtoService corporateActionDetailsDtoService;

    @Mock
    private CorporateActionElectionCsvHelper corporateActionElectionCsvHelper;

    @Mock
    private CorporateActionDetailsDto corporateActionDetailsDto;


    @Before
    public void setup() {
        when(corporateActionDetailsDtoService.find(any(CorporateActionDtoKey.class), any(ServiceErrors.class))).thenReturn
                (corporateActionDetailsDto);
    }

    @Test
    public void testInit() {
        Map<String, String> params = new HashMap<>();
        params.put("ca-id", EncodedString.fromPlainText("0").toString());

        corporateActionElectionsReportCsv.init(params);
    }

    @Test
    public void testGetCorporateActionDetails() {
        List<CorporateActionDetailsBaseDto> corporateActionDetails = corporateActionElectionsReportCsv.getCorporateActionDetails(null);

        assertNotNull(corporateActionDetails);
        assertEquals(1, corporateActionDetails.size());
    }

    @Test
    public void testGetCorporateActionAccountsList() {
        CorporateActionAccountDetailsDto accountDetailsDto1 = createAccountDetailsDto("1");
        CorporateActionAccountDetailsDto accountDetailsDto2 = createAccountDetailsDto("2");
        CorporateActionAccountDetailsDto accountDetailsDto3 = createAccountDetailsDto("3");

        when(corporateActionDetailsDto.getAccounts()).thenReturn(Arrays.asList(accountDetailsDto1, accountDetailsDto2, accountDetailsDto3));

        List<CorporateActionAccountDetailsDto> accountList = corporateActionElectionsReportCsv.getCorporateActionAccountsList(null);
        assertNotNull(accountList);
        assertEquals(3, accountList.size());
    }

    @Test
    public void testGetCorporateActionSubmittedOption_whenMultiBlock_thenReturnWithMultiBlockTitleSummary() {
        CorporateActionAccountDetailsDto accountDetailsDto = createAccountDetailsDto("1");

        CorporateActionAccountElectionsDto electionsDto = createStandardAccountElectionsDto(1);
        List<CorporateActionOptionDto> optionDtos = createMultiBlockOptionDtos();

        when(accountDetailsDto.getSubmittedElections()).thenReturn(electionsDto);
        when(corporateActionDetailsDto.getCorporateActionType()).thenReturn(CorporateActionType.MULTI_BLOCK.name());
        when(corporateActionDetailsDto.getOptions()).thenReturn(optionDtos);
        when(corporateActionDetailsDto.getAccounts()).thenReturn(Arrays.asList(accountDetailsDto));

        Map<CorporateActionAccountElectionsDto, String> titleSummaryMap = corporateActionElectionsReportCsv
                .getCorporateActionSubmittedOption(null);

        assertNotNull(titleSummaryMap);
        assertEquals(1, titleSummaryMap.size());
        assertEquals("Option A Buy $1,000", titleSummaryMap.values().iterator().next());
    }

    @Test
    public void testGetCorporateActionSubmittedOption_whenBuyBack_thenReturnWithBuyBackTitleSummary() {
        CorporateActionAccountDetailsDto accountDetailsDto = createAccountDetailsDto("1");

        CorporateActionAccountElectionsDto electionsDto = createBuyBackAccountElectionsDto();
        List<CorporateActionOptionDto> optionDtos = createBuyBackOptionDtos();

        when(accountDetailsDto.getSubmittedElections()).thenReturn(electionsDto);
        when(corporateActionDetailsDto.getCorporateActionType()).thenReturn(CorporateActionType.BUY_BACK.name());
        when(corporateActionDetailsDto.getOptions()).thenReturn(optionDtos);
        when(corporateActionDetailsDto.getAccounts()).thenReturn(Arrays.asList(accountDetailsDto));

        Map<CorporateActionAccountElectionsDto, String> titleSummaryMap = corporateActionElectionsReportCsv
                .getCorporateActionSubmittedOption(null);

        assertNotNull(titleSummaryMap);
        assertEquals(1, titleSummaryMap.size());
        assertEquals("Option A Buy $1,000 - 10 unit(s);Option B Buy $2,000 - 50 unit(s);", titleSummaryMap.values().iterator().next());
    }

    @Test
    public void testGetCorporateActionSubmittedOption_whenBuyBackButNoUnits_thenReturnWithBuyBackTitleSummary() {
        CorporateActionAccountDetailsDto accountDetailsDto = createAccountDetailsDto("1");

        CorporateActionAccountElectionsDto electionsDto = createBuyBackAccountElectionsDto();
        List<CorporateActionOptionDto> optionDtos = createBuyBackOptionDtos();

        CorporateActionAccountElectionDto electionDto = electionsDto.getOptions().get(0);

        when(electionDto.getUnits()).thenReturn(null);
        when(accountDetailsDto.getSubmittedElections()).thenReturn(electionsDto);
        when(corporateActionDetailsDto.getCorporateActionType()).thenReturn(CorporateActionType.BUY_BACK.name());
        when(corporateActionDetailsDto.getOptions()).thenReturn(optionDtos);
        when(corporateActionDetailsDto.getAccounts()).thenReturn(Arrays.asList(accountDetailsDto));

        Map<CorporateActionAccountElectionsDto, String> titleSummaryMap = corporateActionElectionsReportCsv
                .getCorporateActionSubmittedOption(null);

        assertNotNull(titleSummaryMap);
        assertEquals(1, titleSummaryMap.size());
        assertEquals("Option A Buy $1,000;Option B Buy $2,000 - 50 unit(s);", titleSummaryMap.values().iterator().next());
    }

    @Test
    public void testGetCorporateActionSubmittedOption_whenFullExerciseRights_thenReturnFullExerciseRightsTitleSummary() {
        CorporateActionAccountDetailsDto accountDetailsDto = createAccountDetailsDto("1");

        CorporateActionAccountElectionsDto electionsDto = createExerciseRightsAccountElectionsDto(CorporateActionExerciseRightsType.FULL,
                null, null);
        List<CorporateActionOptionDto> optionDtos = createExerciseRightsOptionDtos();

        when(accountDetailsDto.getHolding()).thenReturn(10);
        when(accountDetailsDto.getSubmittedElections()).thenReturn(electionsDto);
        when(corporateActionDetailsDto.getCorporateActionType()).thenReturn(CorporateActionType.EXERCISE_RIGHTS.name());
        when(corporateActionDetailsDto.getOptions()).thenReturn(optionDtos);
        when(corporateActionDetailsDto.getAccounts()).thenReturn(Arrays.asList(accountDetailsDto));
        when(corporateActionDetailsDto.getCorporateActionPrice()).thenReturn(BigDecimal.valueOf(0.59));
        when(corporateActionDetailsDto.getOversubscribe()).thenReturn(Boolean.FALSE);

        Map<CorporateActionAccountElectionsDto, String> titleSummaryMap = corporateActionElectionsReportCsv
                .getCorporateActionSubmittedOption(null);

        assertNotNull(titleSummaryMap);
        assertEquals(1, titleSummaryMap.size());
        assertEquals("Option A Full exercise - 10 unit(s) @ $0.59 = $5.90", titleSummaryMap.values().iterator().next());

        // Oversubscribe applicable and with oversubscribe
        electionsDto = createExerciseRightsAccountElectionsDto(CorporateActionExerciseRightsType.FULL, null, BigDecimal.TEN);
        when(accountDetailsDto.getSubmittedElections()).thenReturn(electionsDto);
        when(corporateActionDetailsDto.getOversubscribe()).thenReturn(Boolean.TRUE);

        titleSummaryMap = corporateActionElectionsReportCsv.getCorporateActionSubmittedOption(null);

        assertEquals("Option A Full exercise - 20 unit(s) @ $0.59 = $11.80 Additional new shares 10 unit(s)", titleSummaryMap.values()
                .iterator().next());

        // Oversubscribe applicable but without oversubscribe
        electionsDto = createExerciseRightsAccountElectionsDto(CorporateActionExerciseRightsType.FULL, null, null);
        when(accountDetailsDto.getSubmittedElections()).thenReturn(electionsDto);

        titleSummaryMap = corporateActionElectionsReportCsv.getCorporateActionSubmittedOption(null);

        assertEquals("Option A Full exercise - 10 unit(s) @ $0.59 = $5.90 Additional new shares 0 unit", titleSummaryMap.values()
                .iterator().next());

        // With oversubscribe but oversubscribe unavailable
        electionsDto = createExerciseRightsAccountElectionsDto(CorporateActionExerciseRightsType.FULL, null, BigDecimal.TEN);
        when(accountDetailsDto.getSubmittedElections()).thenReturn(electionsDto);
        when(corporateActionDetailsDto.getOversubscribe()).thenReturn(Boolean.FALSE);

        titleSummaryMap = corporateActionElectionsReportCsv.getCorporateActionSubmittedOption(null);

        assertEquals("Option A Full exercise - 20 unit(s) @ $0.59 = $11.80", titleSummaryMap.values().iterator().next());
    }

    @Test
    public void testGetCorporateActionSubmittedOption_whenPartialExerciseRights_thenReturnPartialExerciseRightsTitleSummary() {
        CorporateActionAccountDetailsDto accountDetailsDto = createAccountDetailsDto("1");

        CorporateActionAccountElectionsDto electionsDto = createExerciseRightsAccountElectionsDto(CorporateActionExerciseRightsType
                .PARTIAL, BigDecimal.TEN, null);
        List<CorporateActionOptionDto> optionDtos = createExerciseRightsOptionDtos();

        when(accountDetailsDto.getHolding()).thenReturn(10);
        when(accountDetailsDto.getSubmittedElections()).thenReturn(electionsDto);
        when(corporateActionDetailsDto.getCorporateActionType()).thenReturn(CorporateActionType.EXERCISE_RIGHTS.name());
        when(corporateActionDetailsDto.getOptions()).thenReturn(optionDtos);
        when(corporateActionDetailsDto.getAccounts()).thenReturn(Arrays.asList(accountDetailsDto));
        when(corporateActionDetailsDto.getCorporateActionPrice()).thenReturn(BigDecimal.valueOf(0.59));

        Map<CorporateActionAccountElectionsDto, String> titleSummaryMap = corporateActionElectionsReportCsv
                .getCorporateActionSubmittedOption(null);

        assertNotNull(titleSummaryMap);
        assertEquals(1, titleSummaryMap.size());
        assertEquals("Option B Partial exercise - 10 unit(s) @ $0.59 = $5.90", titleSummaryMap.values().iterator().next());

        // Saved elections
        when(accountDetailsDto.getSubmittedElections()).thenReturn(null);
        when(accountDetailsDto.getSavedElections()).thenReturn(electionsDto);

        titleSummaryMap = corporateActionElectionsReportCsv.getCorporateActionSubmittedOption(null);

        assertNotNull(titleSummaryMap);
        assertEquals(1, titleSummaryMap.size());
        assertEquals("Option B Partial exercise - 10 unit(s) @ $0.59 = $5.90", titleSummaryMap.values().iterator().next());

        // Pro rata priority offer
        when(corporateActionDetailsDto.getCorporateActionType()).thenReturn(CorporateActionType.PRO_RATA_PRIORITY_OFFER.name());

        titleSummaryMap = corporateActionElectionsReportCsv.getCorporateActionSubmittedOption(null);

        assertNotNull(titleSummaryMap);
        assertEquals(1, titleSummaryMap.size());
        assertEquals("Option B Partial exercise - 10 unit(s) @ $0.59 = $5.90", titleSummaryMap.values().iterator().next());

        // Exercise call option
        when(corporateActionDetailsDto.getCorporateActionType()).thenReturn(CorporateActionType.EXERCISE_CALL_OPTION.name());

        titleSummaryMap = corporateActionElectionsReportCsv.getCorporateActionSubmittedOption(null);

        assertNotNull(titleSummaryMap);
        assertEquals(1, titleSummaryMap.size());
        assertEquals("Option B Partial exercise - 10 unit(s) @ $0.59 = $5.90", titleSummaryMap.values().iterator().next());

        // Exercise rights with options
        when(corporateActionDetailsDto.getCorporateActionType()).thenReturn(CorporateActionType.EXERCISE_RIGHTS_WITH_OPT.name());

        titleSummaryMap = corporateActionElectionsReportCsv.getCorporateActionSubmittedOption(null);

        assertNotNull(titleSummaryMap);
        assertEquals(1, titleSummaryMap.size());
        assertEquals("Option B Partial exercise - 10 unit(s) @ $0.59 = $5.90", titleSummaryMap.values().iterator().next());
    }

    @Test
    public void testGetCorporateActionSubmittedOption_whenThereIsNoSubmittedOrSavedElection_thenReturnNoSummary() {
        CorporateActionAccountDetailsDto accountDetailsDto = createAccountDetailsDto("1");

        when(accountDetailsDto.getSubmittedElections()).thenReturn(null);
        when(accountDetailsDto.getSavedElections()).thenReturn(null);
        when(corporateActionDetailsDto.getAccounts()).thenReturn(Arrays.asList(accountDetailsDto));

        Map<CorporateActionAccountElectionsDto, String> titleSummaryMap = corporateActionElectionsReportCsv
                .getCorporateActionSubmittedOption(null);

        assertNotNull(titleSummaryMap);
        assertTrue(titleSummaryMap.isEmpty());
    }

    @Test
    public void testGetCorporateActionSubmittedOption_whenPartialExerciseRightsButNoUnits_thenReturnLapseExerciseRightsTitleSummary() {
        CorporateActionAccountDetailsDto accountDetailsDto = createAccountDetailsDto("1");

        CorporateActionAccountElectionsDto electionsDto = createExerciseRightsAccountElectionsDto(CorporateActionExerciseRightsType
                .PARTIAL, null, null);
        List<CorporateActionOptionDto> optionDtos = createExerciseRightsOptionDtos();

        when(accountDetailsDto.getHolding()).thenReturn(10);
        when(accountDetailsDto.getSubmittedElections()).thenReturn(electionsDto);
        when(corporateActionDetailsDto.getCorporateActionType()).thenReturn(CorporateActionType.EXERCISE_RIGHTS.name());
        when(corporateActionDetailsDto.getOptions()).thenReturn(optionDtos);
        when(corporateActionDetailsDto.getAccounts()).thenReturn(Arrays.asList(accountDetailsDto));

        Map<CorporateActionAccountElectionsDto, String> titleSummaryMap = corporateActionElectionsReportCsv
                .getCorporateActionSubmittedOption(null);

        assertNotNull(titleSummaryMap);
        assertEquals(1, titleSummaryMap.size());
        assertEquals("Option B Partial exercise", titleSummaryMap.values().iterator().next());
    }

    @Test
    public void testGetCorporateActionSubmittedOption_whenPartialExerciseRightsBuNoPrice_thenReturnPartialExerciseRightsTitleSummary() {
        CorporateActionAccountDetailsDto accountDetailsDto = createAccountDetailsDto("1");

        CorporateActionAccountElectionsDto electionsDto = createExerciseRightsAccountElectionsDto(CorporateActionExerciseRightsType
                .PARTIAL, BigDecimal.TEN, null);
        List<CorporateActionOptionDto> optionDtos = createExerciseRightsOptionDtos();

        when(accountDetailsDto.getHolding()).thenReturn(10);
        when(accountDetailsDto.getSubmittedElections()).thenReturn(electionsDto);
        when(corporateActionDetailsDto.getCorporateActionType()).thenReturn(CorporateActionType.EXERCISE_RIGHTS.name());
        when(corporateActionDetailsDto.getOptions()).thenReturn(optionDtos);
        when(corporateActionDetailsDto.getAccounts()).thenReturn(Arrays.asList(accountDetailsDto));
        when(corporateActionDetailsDto.getCorporateActionPrice()).thenReturn(null);

        Map<CorporateActionAccountElectionsDto, String> titleSummaryMap = corporateActionElectionsReportCsv
                .getCorporateActionSubmittedOption(null);

        assertNotNull(titleSummaryMap);
        assertEquals(1, titleSummaryMap.size());
        assertEquals("Option B Partial exercise - 10 unit(s) @ $0 = $0", titleSummaryMap.values().iterator().next());
    }

    @Test
    public void testGetCorporateActionSubmittedOption_whenLapseExerciseRights_thenReturnLapseExerciseRightsTitleSummary() {
        CorporateActionAccountDetailsDto accountDetailsDto = createAccountDetailsDto("1");

        CorporateActionAccountElectionsDto electionsDto = createExerciseRightsAccountElectionsDto(CorporateActionExerciseRightsType
                .LAPSE, BigDecimal.TEN, null);
        List<CorporateActionOptionDto> optionDtos = createExerciseRightsOptionDtos();

        when(accountDetailsDto.getSubmittedElections()).thenReturn(electionsDto);
        when(corporateActionDetailsDto.getCorporateActionType()).thenReturn(CorporateActionType.EXERCISE_RIGHTS.name());
        when(corporateActionDetailsDto.getOptions()).thenReturn(optionDtos);
        when(corporateActionDetailsDto.getAccounts()).thenReturn(Arrays.asList(accountDetailsDto));

        Map<CorporateActionAccountElectionsDto, String> titleSummaryMap = corporateActionElectionsReportCsv
                .getCorporateActionSubmittedOption(null);

        assertNotNull(titleSummaryMap);
        assertEquals(1, titleSummaryMap.size());
        assertEquals("Option C Lapse", titleSummaryMap.values().iterator().next());
    }

    @Test
    public void testGetOngoingAdviceFeeMap() {
        Map<String, BigDecimal> adviceFeeMap = new HashMap<>();

        CorporateActionAccountDetailsDto accountDetailsDto = createAccountDetailsDto("1");
        when(corporateActionDetailsDto.getAccounts()).thenReturn(Arrays.asList(accountDetailsDto));
        when(corporateActionElectionCsvHelper.getOngoingAdviceFee(any(CorporateActionAccountDetailsDto.class))).thenReturn(adviceFeeMap);

        assertNotNull(corporateActionElectionsReportCsv.getOngoingAdviceFeeMap(null));
    }

    private CorporateActionAccountDetailsDto createAccountDetailsDto(String accountKey) {
        CorporateActionAccountDetailsDto accountDetailsDto = mock(CorporateActionAccountDetailsDto.class);

        when(accountDetailsDto.getClientId()).thenReturn(accountKey);
        when(accountDetailsDto.getAccountKey()).thenReturn(EncodedString.fromPlainText(accountKey).toString());

        return accountDetailsDto;
    }

    private List<CorporateActionOptionDto> createMultiBlockOptionDtos() {
        List<CorporateActionOptionDto> optionDtos = new ArrayList<>();

        optionDtos.add(createOptionDtoMock(1, "Option A", "Buy $1,000", true, false));
        optionDtos.add(createOptionDtoMock(CorporateActionConverterConstants.OPTION_TAKE_NO_ACTION_ID, "Option B", "Do not participate",
                true, false));

        return optionDtos;
    }

    private List<CorporateActionOptionDto> createBuyBackOptionDtos() {
        List<CorporateActionOptionDto> optionDtos = new ArrayList<>();

        optionDtos.add(createOptionDtoMock(1, "Option A", "Buy $1,000", true, false));
        optionDtos.add(createOptionDtoMock(2, "Option B", "Buy $2,000", false, false));
        optionDtos.add(createOptionDtoMock(CorporateActionConverterConstants.OPTION_TAKE_NO_ACTION_ID, "Option C", "Do not participate",
                true, false));

        return optionDtos;
    }

    private List<CorporateActionOptionDto> createExerciseRightsOptionDtos() {
        List<CorporateActionOptionDto> optionDtos = new ArrayList<>();

        optionDtos.add(createOptionDtoMock(CorporateActionExerciseRightsType.FULL.getId(), "Option A", "Full exercise", true, false));
        optionDtos.add(createOptionDtoMock(CorporateActionExerciseRightsType.PARTIAL.getId(), "Option B", "Partial exercise", false,
                false));
        optionDtos.add(createOptionDtoMock(CorporateActionExerciseRightsType.LAPSE.getId(), "Option C", "Lapse", true, false));

        return optionDtos;
    }

    private CorporateActionAccountElectionsDto createStandardAccountElectionsDto(Integer optionId) {
        CorporateActionAccountElectionsDto accountElectionsDto = mock(CorporateActionAccountElectionsDto.class);

        CorporateActionAccountElectionDto accountElectionDto = mock(CorporateActionAccountElectionDto.class);

        when(accountElectionDto.getOptionId()).thenReturn(optionId);
        when(accountElectionsDto.getOptions()).thenReturn(Arrays.asList(accountElectionDto));
        when(accountElectionsDto.getPrimaryAccountElection()).thenReturn(accountElectionDto);

        return accountElectionsDto;
    }

    private CorporateActionAccountElectionsDto createBuyBackAccountElectionsDto() {
        CorporateActionAccountElectionsDto accountElectionsDto = mock(CorporateActionAccountElectionsDto.class);

        CorporateActionBuyBackAccountElectionDtoImpl accountElectionDto1 = mock(CorporateActionBuyBackAccountElectionDtoImpl.class);
        CorporateActionBuyBackAccountElectionDtoImpl accountElectionDto2 = mock(CorporateActionBuyBackAccountElectionDtoImpl.class);

        List<CorporateActionAccountElectionDto> accountElectionDtos = new ArrayList<>();
        accountElectionDtos.add(accountElectionDto1);
        accountElectionDtos.add(accountElectionDto2);

        when(accountElectionDto1.getOptionId()).thenReturn(1);
        when(accountElectionDto1.getUnits()).thenReturn(BigDecimal.TEN);
        when(accountElectionDto2.getOptionId()).thenReturn(2);
        when(accountElectionDto2.getUnits()).thenReturn(BigDecimal.valueOf(50));
        when(accountElectionsDto.getOptions()).thenReturn(accountElectionDtos);

        return accountElectionsDto;
    }

    private CorporateActionAccountElectionsDto createExerciseRightsAccountElectionsDto(CorporateActionExerciseRightsType type, BigDecimal
            units, BigDecimal oversubscribe) {
        CorporateActionAccountElectionsDto accountElectionsDto = mock(CorporateActionAccountElectionsDto.class);

        CorporateActionExerciseRightsAccountElectionDtoImpl accountElectionDto = mock(CorporateActionExerciseRightsAccountElectionDtoImpl
                .class);

        List<CorporateActionAccountElectionDto> accountElectionDtos = new ArrayList<>();
        accountElectionDtos.add(accountElectionDto);

        when(accountElectionDto.getOptionId()).thenReturn(type.getId());
        when(accountElectionDto.getUnits()).thenReturn(units);
        when(accountElectionDto.getOversubscribe()).thenReturn(oversubscribe);
        when(accountElectionsDto.getOptions()).thenReturn(accountElectionDtos);
        when(accountElectionsDto.getPrimaryAccountElection()).thenReturn(accountElectionDto);

        return accountElectionsDto;
    }
}
