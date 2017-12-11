package com.bt.nextgen.api.staticdata.controller;

import com.bt.nextgen.api.staticdata.model.StaticCodeDto;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.core.api.model.ApiError;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.Dto;
import com.bt.nextgen.core.api.model.ResultMapDto;
import com.btfin.panorama.core.security.Roles;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.security.profile.Profile;
import com.bt.nextgen.login.util.SamlUtil;
import com.btfin.panorama.core.conversion.CodeCategory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class StaticDataApiControllerIntegrationTest extends BaseSecureIntegrationTest
{
	private static final List<StaticCodeDto> PANORAMA_STATES = asList(
			new StaticCodeDto("5007", "Australia Capital Territory", "ACT", "btfg$au_act", "states"),
			new StaticCodeDto("5004", "New South Wales", "NSW", "btfg$au_nsw", "states"),
			new StaticCodeDto("5005", "Northern Territory", "NT", "btfg$au_nt", "states"),
			new StaticCodeDto("5002", "Queensland", "QLD", "btfg$au_qld", "states"),
			new StaticCodeDto("5006", "South Australia", "SA", "btfg$au_sa", "states"),
			new StaticCodeDto("5002", "Queensland", "QLD", "btfg$au_qld", "states"),
			new StaticCodeDto("5008", "Tasmania", "TAS", "btfg$au_tas", "states"),
			new StaticCodeDto("5001", "Victoria", "VIC", "btfg$au_vic", "states"),
			new StaticCodeDto("5003", "Western Australia", "WA", "btfg$au_wa", "states"));

	private static final List<StaticCodeDto> NON_PANORAMA_STATES = asList(
			new StaticCodeDto("5000", "Australian Antarctic Territory", "AAT", "btfg$au_aat", "states"),
			new StaticCodeDto("5009", "Foreign Driver's License", "FOR", "btfg$au_for", "states"));

    private static final StaticCodeDto CONDITION_OF_RELEASE = new StaticCodeDto("22", "Remove CoR", "REMV_COR", "remv_cor", "PENSION_CONDITION_RELEASE");

    private static final List<StaticCodeDto> PANORAMA_TIN_EXEMPTION_OPTIONS_IDENTIFICATION_PROVIDED = asList(new StaticCodeDto("1021", "TIN pending", "TIN_PEND", "btfg$tin_pend", "TIN_EXEMPTION_REASONS"),new StaticCodeDto("1020", "Under age", "UNDER_AGED", "btfg$under_aged", "TIN_EXEMPTION_REASONS"),
            new StaticCodeDto("1022", "TIN not issued", "TIN_NEVER_ISS", "btfg$tin_never_iss", "TIN_EXEMPTION_REASONS"));

    private static final List<StaticCodeDto> PANORAMA_TIN_EXEMPTION_OPTIONS_NO_IDENTIFICATION_PROVIDED = asList(new StaticCodeDto("1010", "Business Registration", "BR", "btfg$br", "TIN_EXEMPTION_REASONS"),new StaticCodeDto("1013", "Certificate of Incorporation", "CI", "btfg$ci", "TIN_EXEMPTION_REASONS"),
            new StaticCodeDto("1020", "Under age", "UNDER_AGED", "btfg$under_aged", "TIN_EXEMPTION_REASONS"),new StaticCodeDto("3", "Driver licence", "FA", "fa", "TIN_EXEMPTION_REASONS"),new StaticCodeDto("1019", "Others", "OT", "btfg$ot", "TIN_EXEMPTION_REASONS"));


    private static final int EXPECTED_COUNTRIES = 250;

	private static StaticDataApiController staticDataApiInitialiser = null;

	@Autowired
	private StaticDataApiController staticDataApiController;

	@Before
	public void setup()
	{
		if (staticDataApiController != staticDataApiInitialiser) {
			SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
			TestingAuthenticationToken authentication = new TestingAuthenticationToken("adviser",
				"adviser",
				Roles.ROLE_ADVISER.name());
			Profile dummyProfile = new Profile(new SamlToken(SamlUtil.loadSaml()));
			authentication.setDetails(dummyProfile);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			staticDataApiInitialiser = staticDataApiController;
		}
	}

	@Test
	public void getStaticCodesOnCriteriaTest()
	{
		String criteria = "[{prop:'STATES',op:'=',val:'STATES',type:'string'},{prop:'country',op:'=',val:'country',type:'string'}]";
		ApiResponse response = staticDataApiController.getStaticCodes(criteria, null, false, false);
		assertNotNull(response);
		final Dto data = response.getData();
		assertResponseMapEntry(response.getData(), "STATES", 10);
		assertResponseMapEntry(data, "country", EXPECTED_COUNTRIES);
	}

	@Test
	public void getCountryListTest()
	{
		String criteria = "[{prop:'country',op:'=',val:'country',type:'string'}]";
		ApiResponse response = staticDataApiController.getStaticCodes(criteria, null, false, false);
		assertNotNull(response);
		assertResponseMapEntry(response.getData(), "country", EXPECTED_COUNTRIES);
	}

	@Test
	public void getStatesTest()
	{
		String criteria = "[{prop:'STATES',op:'=',val:'STATES',type:'string'}]";
		ApiResponse response = staticDataApiController.getStaticCodes(criteria, null, false, false);
		assertNotNull(response);
		assertResponseMapEntry(response.getData(), "STATES", 10);
	}

	@Ignore
	public void getTaxOptionsTest()
	{
		String criteria = "[{prop:'TAX_OPTIONS',op:'=',val:'TAX_OPTIONS',type:'string']";
		ApiResponse response = staticDataApiController.getStaticCodes(criteria, null, false, false);
		assertNotNull(response);
		assertResponseMapEntry(response.getData(), "TAX_OPTIONS", 10);
	}

	@Test
	public void getTaxExemptionReasonsTest()
	{
		String criteria = "[{prop:'EXEMPTION_REASON',op:'=',val:'EXEMPTION_REASON',type:'string'}]";
		ApiResponse response = staticDataApiController.getStaticCodes(criteria, null, false, false);
		assertNotNull(response);
		assertResponseMapEntry(response.getData(), "EXEMPTION_REASON", 8);
	}

	@Test
	public void noCriteriaOrCategorySendsBadRequestError() {
		final ApiResponse response = staticDataApiController.getStaticCodes("[]", null, false, false);
		assertNotNull(response);
		assertNull(response.getData());
		final ApiError error = response.getError();
		assertNotNull(error);
		assertThat(error.getCode(), is(400));
	}

	@Test
     @SuppressWarnings("unchecked")
     public void getStatesViaCategoryWithPanoramaFlagOff() {
    final ApiResponse response = staticDataApiController.getStaticCodes(null, new String[]{ "states" }, false, false);
    assertNotNull(response);
    final List<StaticCodeDto> states = assertResponseMapEntry(response.getData(), "states", 10);

    for (StaticCodeDto state : PANORAMA_STATES) {
        assertTrue("Could not find " + state.getValue(), states.contains(state));
    }
    for (StaticCodeDto state : NON_PANORAMA_STATES) {
        assertTrue("Could not find " + state.getValue(), states.contains(state));
    }
}

    @Test
    @SuppressWarnings("unchecked")
    public void getStatesViaCategoryWithPanoramaFlagOn() {
        final ApiResponse response = staticDataApiController.getStaticCodes(null, new String[]{ "states" }, true, false);
        assertNotNull(response);
        final List<StaticCodeDto> states = assertResponseMapEntry(response.getData(), "states", 8);
        for (StaticCodeDto state : PANORAMA_STATES) {
            assertTrue("Could not find " + state.getValue(), states.contains(state));
        }
        for (StaticCodeDto state : NON_PANORAMA_STATES) {
            assertFalse("Not expecting to find " + state.getValue(), states.contains(state));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getTinExemptionOptionsWithPanoramaAndIdentificationFlagOn() {
        final ApiResponse response = staticDataApiController.getStaticCodes(null, new String[]{ "TIN_EXEMPTION_REASONS" }, true, true);
        assertNotNull(response);
        final List<StaticCodeDto> staticCodes = assertResponseMapEntry(response.getData(), "TIN_EXEMPTION_REASONS", 3);

        for (StaticCodeDto staticCode : PANORAMA_TIN_EXEMPTION_OPTIONS_IDENTIFICATION_PROVIDED) {
            assertTrue("Key Found " + staticCode.getValue(), staticCodes.contains(staticCode));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getTinExemptionOptionsWithPanoramaAndIdentificationFlagOff() {
        final ApiResponse response = staticDataApiController.getStaticCodes(null, new String[]{ "TIN_EXEMPTION_REASONS" }, true, false);
        assertNotNull(response);

        final List<StaticCodeDto> staticCodes = assertResponseMapEntry(response.getData(), "TIN_EXEMPTION_REASONS", 18);
        for (StaticCodeDto staticCode : PANORAMA_TIN_EXEMPTION_OPTIONS_IDENTIFICATION_PROVIDED) {
            assertTrue("Key Found " + staticCode.getValue(), staticCodes.contains(staticCode));
        }
        for(StaticCodeDto staticCode : PANORAMA_TIN_EXEMPTION_OPTIONS_NO_IDENTIFICATION_PROVIDED){
            assertTrue("Key Found" + staticCode.getValue(), staticCodes.contains(staticCode));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getConditionOfReleaseViaCategoryWithPanoramaFlagOff() {
        final ApiResponse response = staticDataApiController.getStaticCodes(null, new String[]{CodeCategory.PENSION_CONDITION_RELEASE.toString()}, false, false);
        assertNotNull(response);
        final List<StaticCodeDto> cor = assertResponseMapEntry(response.getData(), "PENSION_CONDITION_RELEASE", 18);
        assertTrue("Could find " + CONDITION_OF_RELEASE.getValue() + " when panorama flag is turned OFF", cor.contains(CONDITION_OF_RELEASE));

    }

    @Test
    @SuppressWarnings("unchecked")
    public void getConditionOfReleaseViaCategoryWithPanoramaFlagOn() {
        final ApiResponse response = staticDataApiController.getStaticCodes(null, new String[]{CodeCategory.PENSION_CONDITION_RELEASE.toString()}, true, false);
        assertNotNull(response);
        final List<StaticCodeDto> cor = assertResponseMapEntry(response.getData(), "PENSION_CONDITION_RELEASE", 8);
        assertFalse("Could not find " + CONDITION_OF_RELEASE.getValue() + " when panorama flag is turned ON", cor.contains(CONDITION_OF_RELEASE));
    }

	@Test
	public void getCountriesViaCategoryWithPanoramaFlagOn() {
		final ApiResponse response = staticDataApiController.getStaticCodes(null, new String[]{ "country" }, true, false);
		assertNotNull(response);
		assertResponseMapEntry(response.getData(), "country", EXPECTED_COUNTRIES);
	}

	@SuppressWarnings("unchecked")
	private static List<StaticCodeDto> assertResponseMapEntry(Dto data, String key, int expectedSize) {
		assertTrue(data instanceof ResultMapDto);
		final List<StaticCodeDto> list = (List<StaticCodeDto>) ((ResultMapDto) data).getResultMap().get(key);
		assertNotNull("No list found under " + key, list);
		assertThat("Expecting list of " + key + " to be size " + expectedSize, list.size(), is(expectedSize));
		return list;
	}
}
