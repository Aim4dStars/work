package com.bt.nextgen.api.staticdata.service;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.annotation.Nullable;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.staticdata.model.StaticCodeDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.avaloq.code.FieldImpl;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.Field;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;

@RunWith(MockitoJUnitRunner.class)
public class StaticDataDtoServiceImplTest {
    @Mock
    private StaticIntegrationService staticService;

    @Mock
    private ServiceErrors serviceErrors;

    @InjectMocks
    StaticDataDtoServiceImpl staticDataDtoServiceImpl;

    @Test
    public void searchWithTwoCriteria() {
        final Code[] countries = {new CodeImpl("2200", "ML", "Mali"), new CodeImpl("2220", "NP", "Nepal")};
        final Code[] states = {new CodeImpl("5004", "NSW", "New South Wales"), new CodeImpl("5005", "NT", "Northern Territory")};

        when(staticService.loadCodes(CodeCategory.COUNTRY, serviceErrors)).thenReturn(asList(countries));
        when(staticService.loadCodes(CodeCategory.STATES, serviceErrors)).thenReturn(asList(states));

        ApiSearchCriteria[] searchCriteria = {new ApiSearchCriteria("STATES", "STATES"),
                new ApiSearchCriteria("country", "country")};
        List<StaticCodeDto> list = staticDataDtoServiceImpl.search(asList(searchCriteria), serviceErrors);

        assertNotNull(list);
        assertFalse(list.isEmpty());
        //Updated list size as the two countries will not be retrieved since they don't have btfg$im_code field set
        assertEquals(2, list.size());
    }

    @Test
    public void searchWithSortableCodes() {
        final Code[] titles = {
                new CodeImpl("1000", "DR", "Dr", "btfg$dr"),
                new CodeImpl("1004", "MS", "Ms", "btfg$ms"),
                new CodeImpl("1003", "MISS", "Miss", "btfg$miss"),
                new CodeImpl("1002", "MRS", "Mrs", "btfg$mrs"),
                new CodeImpl("1001", "MR", "Mr", "btfg$mr"),
                new CodeImpl("1009", "PROF", "Prof", "btfg$prof"),
                new CodeImpl("1005", "REV", "Rev", "btfg$rev")
        };
        when(staticService.loadCodes(CodeCategory.PERSON_TITLE, serviceErrors)).thenReturn(asList(titles));

        final ApiSearchCriteria criterion = new ApiSearchCriteria("category", "person_title");
        final List<StaticCodeDto> list = staticDataDtoServiceImpl.search(asList(criterion), serviceErrors);
        assertThat(list.size(), is(titles.length));
        final String[] expectedLabels = { "Mr", "Ms", "Mrs", "Miss", "Dr", "Prof", "Rev" };
        for (int i = 0; i < expectedLabels.length; i++) {
            assertThat(list.get(i).getLabel(), is(expectedLabels[i]));
        }
    }

    @Test
    public void searchWithPensionerConditionOfReleaseABSSortableCodes() {
        final Code[] pensionerConditionsOfReleases = {
                createNewCode("1000", "TURN_AGE_65", "Attaining Age 65", "turn_age_65", "001"), // padded order string
                createNewCode("1001", "DEATH", "Death", "death", "2"), // normal order string
                createNewCode("1002", "OTH", "Other condition of release", "oth", null), // single missing order string, defaults to 999999
                createNewCode("1002", "ZORK", "Zork zork", "oth", "zork"), // unparsable order string
                createNewCode("1003", "LESS_200_LOST_FOUND", "Lost member who is found", "less_200_lost_found", "000") }; // padded 0 string
        final String[] expectedLabels = {"Lost member who is found", "Attaining Age 65", "Death", "Zork zork", "Other condition of release"};
        testPensionConditionOfReleaseSortableCodes(pensionerConditionsOfReleases, expectedLabels);
    }

    @Test
    public void searchWithoutPensionerConditionOfReleaseABSSortableCodes() {
        final Code[] pensionerConditionsOfReleases = {
                createNewCode("1000", "TURN_AGE_65", "Attaining Age 65", "turn_age_65", null),
                createNewCode("1001", "DEATH", "Death", "death", null),
                createNewCode("1002", "LESS_200_LOST_FOUND", "Lost member who is found", "less_200_lost_found", null) };
        final String[] expectedLabels = {"Attaining Age 65", "Death", "Lost member who is found"};
        testPensionConditionOfReleaseSortableCodes(pensionerConditionsOfReleases, expectedLabels);
    }

    @Test
    public void searchRetrieveIMCodeForCountries() {
        CodeImpl country1 = new CodeImpl("2008", "EX", "Sint Eustatius");
        country1.addField("btfg$im_code", "BQ");

        CodeImpl country2 = new CodeImpl("2080", "BIOT", "British indian ocean territory");
        country2.addField("btfg$im_code", "IO");

        CodeImpl country3 = new CodeImpl("2112", "TP", "East Timor");
        country3.addField("btfg$im_code", "TL");

        CodeImpl country4 = new CodeImpl("2171", "USMOI", "United States Minor Outlying Islands");
        country4.addField("btfg$im_code", "UM");

        final Code[] countries = {country1, country2, country3, country4};

        when(staticService.loadCodes(CodeCategory.COUNTRY, serviceErrors)).thenReturn(asList(countries));

        ApiSearchCriteria[] searchCriteria = {new ApiSearchCriteria("country", "country")};
        List<StaticCodeDto> list = staticDataDtoServiceImpl.search(asList(searchCriteria), serviceErrors);

        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertEquals(4, list.size());
        assertEquals("BQ", list.get(0).getValue());
        assertEquals("IO", list.get(1).getValue());
        assertEquals("TL", list.get(2).getValue());
        assertEquals("UM", list.get(3).getValue());
    }
    
    private void testPensionConditionOfReleaseSortableCodes(final Code[] pensionerConditionsOfReleases, final String[] expectedLabels) {
        when(staticService.loadCodes(CodeCategory.PENSION_CONDITION_RELEASE, serviceErrors)).thenReturn(asList(pensionerConditionsOfReleases));
        final ApiSearchCriteria criterion = new ApiSearchCriteria("category", "pension_condition_release");
        final List<StaticCodeDto> list = staticDataDtoServiceImpl.search(asList(criterion), serviceErrors);
        assertThat(list.size(), is(pensionerConditionsOfReleases.length));
        for (int i = 0; i < expectedLabels.length; i++) {
            assertThat(list.get(i).getLabel(), is(expectedLabels[i]));
        }
    }

    private Code createNewCode(final String codeId, final String userId, final String name, final String intlId, @Nullable final String sortOrder) {
        final String abs_sort_order = StaticDataDtoServiceImpl.ABS_SORT_ORDER;
        Code pensionerCORCode = mock(CodeImpl.class);
        when(pensionerCORCode.getCodeId()).thenReturn(codeId);
        when(pensionerCORCode.getUserId()).thenReturn(userId);
        when(pensionerCORCode.getName()).thenReturn(name);
        when(pensionerCORCode.getIntlId()).thenReturn(intlId);
        if (sortOrder != null) {
            when(pensionerCORCode.getField(abs_sort_order)).thenReturn((Field) new FieldImpl(abs_sort_order, sortOrder));
        }
        return pensionerCORCode;
    }
}
