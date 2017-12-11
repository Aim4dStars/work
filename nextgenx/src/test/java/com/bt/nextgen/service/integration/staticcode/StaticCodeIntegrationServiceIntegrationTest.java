package com.bt.nextgen.service.integration.staticcode;

import ch.lambdaj.Lambda;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.CodeCategoryInterface;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class StaticCodeIntegrationServiceIntegrationTest extends BaseSecureIntegrationTest
{

	private static final Logger logger = LoggerFactory.getLogger(StaticCodeIntegrationServiceIntegrationTest.class);

	@Autowired
	StaticIntegrationService staticCode;

	private ServiceErrors serviceErrors;

	@Before
	public void setup()
	{
		serviceErrors = new ServiceErrorsImpl();
	}

	//No longer supported
	@Ignore
	@Test
	public void testStaticCodeService() throws Exception
	{
		Map <CodeCategoryInterface, Map <String, Code>> codes = staticCode.loadCodes(serviceErrors);
		assertNotNull(codes);

		assertEquals(58, codes.size());
	}

	@Test
    @SecureTestContext
	public void testLoadCodes() throws Exception
	{
		Collection <Code> codes = staticCode.loadCodes(CodeCategory.ACCOUNT_STATUS, serviceErrors);
		logger.debug("{}: Static codes returned ", codes.size());
		assertNotNull(codes);
	}

	@Test
	@SecureTestContext
	public void testAccountStatusCodes() throws Exception
	{
		Collection <Code> codes = staticCode.loadCodes(CodeCategory.LINKED_ACCOUNT_STATUS, serviceErrors);
		logger.debug("{}: Static codes returned ", codes.size());
		assertNotNull(codes);
	}
	@Test
    @SecureTestContext
	public void testLoadCodesUserId() throws Exception
	{
		Code code = staticCode.loadCodeByUserId(CodeCategory.ACCOUNT_STATUS, "PEND_CLOSE", serviceErrors);
		assertNotNull(code);

		assertEquals("2751", code.getCodeId());
		assertEquals("PEND_CLOSE", code.getUserId());
		assertEquals("pend_close", code.getIntlId());
		assertEquals("Pending closure", code.getName());

	}
	@Test
    @SecureTestContext
	public void testLoadCodesCodeId() throws Exception
	{
		Code code = staticCode.loadCode(CodeCategory.ACCOUNT_STATUS, "2751", serviceErrors);
		assertNotNull(code);

		assertEquals("2751", code.getCodeId());
		assertEquals("PEND_CLOSE", code.getUserId());
		assertEquals("pend_close", code.getIntlId());
		assertEquals("Pending closure", code.getName());

	}

	@Test
	@SecureTestContext
	public void testLoadCodeAnzsicIndustry() throws Exception
	{
		Code code = staticCode.loadCode(CodeCategory.ANZSIC_INDUSTRY, "15220", serviceErrors);
		assertNotNull(code);

		assertEquals("15220", code.getCodeId());
		assertEquals("7412", code.getUserId());
		assertEquals("7412", code.getIntlId());
		assertEquals("Superannuation funds (7412)", code.getName());

	}

	@Test
	@SecureTestContext
	public void loadCodesAnzsicSubdivision() throws Exception {
		Collection<Code> codes = staticCode.loadCodes(CodeCategory.ANZSIC_SUBDIVISION, serviceErrors);
		assertNotNull(codes);
		assertEquals(42, codes.size());
	}

	@Test
	public void loadCodesPensionExemption() throws Exception {
		Collection<Code> codes = staticCode.loadCodes(CodeCategory.PENSION_EXEMPTION_REASON, serviceErrors);
		assertNotNull(codes);
		assertThat(codes.size(),is(3));
		Collection<Code> panoramaCodes = getPanoramaCodes(codes);
		assertThat(panoramaCodes.size(),is(1));
		Code panoramaCode = (Code)panoramaCodes.toArray()[0];
		assertThat(panoramaCode.getName(),is("Exempt as payee is a pensioner"));

	}

	private Collection<Code> getPanoramaCodes(Collection<Code> allCodes){
		final String PANORAMA_DISPLAY_FIELD = "btfg$is_panorama_val";
		return Lambda.filter(new LambdaMatcher<Code>() {
			@Override
			protected boolean matchesSafely(Code code) {
				return code.getField(PANORAMA_DISPLAY_FIELD).getValue().equals("+");
			}
		}, allCodes);
	}

	@Test
	public void loadCodesWithUnsupportedCodeCategory() throws Exception {
		final CodeCategoryInterface unsupported = new CodeCategoryInterface() {
			@Override
			public String getCode() {
				return "btfg$unsupported_code";
			}
		};
		Collection<Code> codes = staticCode.loadCodes(unsupported, serviceErrors);
		assertNotNull(codes);
		assertTrue(codes.isEmpty());
	}

	@Test
	@SecureTestContext
	public void testOtherTrustTypeName() throws Exception
	{
		Code code = staticCode.loadCodeByUserId(CodeCategory.TRUST_TYPE_DESC,"btfg$oth",null);
		assertNotNull(code);
		assertEquals("Other",code.getName());
	}


	@Test
	@SecureTestContext
	public void testFamilyTrustTypeName() throws Exception
	{
		Code code = staticCode.loadCodeByUserId(CodeCategory.TRUST_TYPE_DESC,"btfg$discrny_trust",null);
		assertNotNull(code);
		assertEquals("Discretionary/family trust",code.getName());
	}
}
