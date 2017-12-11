package com.bt.nextgen.payments.service;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Matchers.anyString;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.naming.spi.DirStateFactory.Result;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.bt.nextgen.clients.util.JaxbUtil;
import com.bt.nextgen.payments.domain.CRNType;
import com.bt.nextgen.payments.domain.IcrnJaxb;
import com.bt.nextgen.payments.domain.IcrnResponse;
import com.bt.nextgen.payments.repository.BpayBiller;
import com.bt.nextgen.payments.repository.BpayBillerCodeRepository;

@RunWith(MockitoJUnitRunner.class)
public class CrnValidationServiceImplTest
{
	private static final String FILE_NAME = "/webservices/response/IcrnResponse.xml";

	@InjectMocks
	CrnValidationServiceImpl crnValidationRepository;
	
	@Mock
	BpayBillerCodeRepository bpayBillerRepo;
	
	private CrnValidationServiceCompatible mockCompatible;
	
	@Before
	public void setUp() throws Exception
	{
		crnValidationRepository = new CrnValidationServiceImpl();
		bpayBillerRepo = Mockito.mock(BpayBillerCodeRepository.class);
		mockCompatible = Mockito.mock(CrnValidationServiceCompatible.class);

		ReflectionTestUtils.setField(crnValidationRepository, "bpayBillerCodeRepository", bpayBillerRepo);
	}

	@Test
	public void testHasValidBpayCrn_WithNullBillerCode()
	{
		mockCompatible = mock(CrnValidationServiceCompatible.class);
		when(mockCompatible.getBillerCode()).thenReturn("");
		assertThat(crnValidationRepository.hasValidBpayCrn(mockCompatible), is(false));
	}
	
	@Test
	public void testHasValidBpayCrn_WithNullBiller()
	{
		mockCompatible = mock(CrnValidationServiceCompatible.class);
		when(bpayBillerRepo.load(anyString())).thenReturn(null);
		boolean response = crnValidationRepository.hasValidBpayCrn(mockCompatible);
		assertThat(response, is(false));
		Mockito.verify(mockCompatible).getBillerCode();
	}


	@Test
	public void testHasValidBpayCrn_ValidNonIcrn()
	{
		final String billerCode = "0000001008";

		mockCompatible = mock(CrnValidationServiceCompatible.class);
		when(mockCompatible.getBillerCode()).thenReturn(billerCode);
		when(mockCompatible.getCustomerReference()).thenReturn("4557016834016904");

		BpayBiller bpayBiller = new BpayBiller();
		bpayBiller.setBillerCode(billerCode);
		bpayBiller.setCrnCheckDigitRoutine("MOD10V01");
		bpayBiller.setCrnFixedDigitsMask("                    ");
		bpayBiller.setCrnValidLengths("            Y       ");
		when(bpayBillerRepo.load(Matchers.any(String.class))).thenReturn(bpayBiller);

		assertThat(crnValidationRepository.hasValidBpayCrn(mockCompatible), is(false));
	}
	
	@Test
	public void testHasValidBpayCrn_TypeIcrn_WithValidIcrnNumber()
	{
		final String billerCode = "3111";
		mockCompatible = mock(CrnValidationServiceCompatible.class);
		when(mockCompatible.getBillerCode()).thenReturn(billerCode);
		when(mockCompatible.getCustomerReference()).thenReturn("10000002");

		BpayBiller bpayBiller = new BpayBiller();
		bpayBiller.setBillerCode(billerCode);
		bpayBiller.setCrnCheckDigitRoutine("MOD10V19");
		bpayBiller.setCrnFixedDigitsMask("4");
		bpayBiller.setCrnValidLengths("10");
		bpayBiller.setCrnType(CRNType.ICRN);
		when(bpayBillerRepo.load(Matchers.any(String.class))).thenReturn(bpayBiller);

		assertThat(crnValidationRepository.hasValidBpayCrn(mockCompatible), is(true));
	}
	
	@Test
	public void testHasValidBpayCrn_TypeIcrn_WithInvalidIcrnNumber()
	{
		final String billerCode = "3111";
		mockCompatible = mock(CrnValidationServiceCompatible.class);
		when(mockCompatible.getBillerCode()).thenReturn(billerCode);
		when(mockCompatible.getCustomerReference()).thenReturn("66788");

		BpayBiller bpayBiller = new BpayBiller();
		bpayBiller.setBillerCode(billerCode);
		bpayBiller.setCrnCheckDigitRoutine("MOD10V19");
		bpayBiller.setCrnFixedDigitsMask("4");
		bpayBiller.setCrnValidLengths("10");
		bpayBiller.setCrnType(CRNType.ICRN);
		when(bpayBillerRepo.load(Matchers.any(String.class))).thenReturn(bpayBiller);

		assertThat(crnValidationRepository.hasValidBpayCrn(mockCompatible), is(false));
	}

	@Test
	public void billerNotFoundIsInvalid()
	{
		when(mockCompatible.getBillerCode()).thenReturn("");

		assertThat(crnValidationRepository.hasValidBpayCrn(mockCompatible), is(false));
	}


	//This method is here for some JAXB stuff
	private void testGenerateIcrnResponseFile() throws Exception
	{
		IcrnJaxb icrnJaxb = new IcrnJaxb();
		icrnJaxb.setNumber("10000002");

		List<IcrnJaxb> icrnJaxbList = new ArrayList<IcrnJaxb>();
		icrnJaxbList.add(icrnJaxb);
		IcrnResponse icrnResponse = new IcrnResponse();
		icrnResponse.setIcrnList(icrnJaxbList);
		try (OutputStream stream = new FileOutputStream(
			new File(CrnValidationServiceImplTest.class.getResource(FILE_NAME).getFile())))
		{
			JaxbUtil.marshall(stream, IcrnResponse.class, icrnResponse);
		}
	}
	
	@Ignore
	@Test
	public void testNonIcrnIsValid()
	{
		final String billerCode = "3111";

		when(mockCompatible.getBillerCode()).thenReturn(billerCode);
		when(mockCompatible.getCustomerReference()).thenReturn("4444444442");

		BpayBiller bpayBiller = new BpayBiller();
		bpayBiller.setBillerCode(billerCode);
		bpayBiller.setCrnCheckDigitRoutine("XADD00014 ");
		bpayBiller.setCrnFixedDigitsMask("4");
		bpayBiller.setCrnValidLengths("            Y       ");
		when(bpayBillerRepo.load(Matchers.any(String.class))).thenReturn(bpayBiller);

		assertThat(crnValidationRepository.hasValidBpayCrn(mockCompatible), is(true));
	}
}
