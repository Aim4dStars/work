package com.bt.nextgen.api.order.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.core.validation.ValidationError.ErrorType;

public class OrderGroupDtoErrorMapperTest
{
	private OrderGroupDtoErrorMapper mapper = new OrderGroupDtoErrorMapperImpl();
	List <ValidationError> errors;
	List <DomainApiErrorDto> apiWarnings;

	@Before
	public void setup() throws Exception
	{
		errors = new ArrayList <>();
		errors.add(new ValidationError("errorId1", "DWS1111", "message1", ErrorType.WARNING));
		errors.add(new ValidationError("errorId2", "ABC1234", "message2", ErrorType.ERROR));
		errors.add(new ValidationError("errorId3", "BT2222", "message3", ErrorType.ERROR));
		errors.add(new ValidationError("errorId4", "BR0001", "message4", ErrorType.WARNING));

		apiWarnings = new ArrayList <>();
		apiWarnings.add(new DomainApiErrorDto("errorId1", "DWS1111", "message1", DomainApiErrorDto.ErrorType.WARNING));
		apiWarnings.add(new DomainApiErrorDto("errorId2", "ABC1234", "message2", DomainApiErrorDto.ErrorType.ERROR));
		apiWarnings.add(new DomainApiErrorDto("errorId3", null, null, null));
		apiWarnings.add(new DomainApiErrorDto("errorId4", null, null, null));
	}

	@Test
	public void testMap_sizeMatches()
	{
		List <DomainApiErrorDto> apiErrors = mapper.map(errors);
		assertEquals(errors.size(), apiErrors.size());
	}

	@Test
	public void testMap_valuesMatch()
	{
		List <DomainApiErrorDto> apiErrors = mapper.map(errors);
		assertEquals(errors.get(0).getErrorId(), apiErrors.get(0).getErrorId());
		assertEquals(errors.get(0).getField(), apiErrors.get(0).getDomain());
		assertEquals(errors.get(0).getMessage(), apiErrors.get(0).getMessage());
		assertEquals(DomainApiErrorDto.ErrorType.WARNING.toString(), apiErrors.get(0).getErrorType());
		assertEquals(DomainApiErrorDto.ErrorType.ERROR.toString(), apiErrors.get(1).getErrorType());
	}

	@Test
	public void testMap_fieldMapping()
	{
		List <DomainApiErrorDto> apiErrors = mapper.map(errors);
		assertEquals("DWS1111", apiErrors.get(0).getDomain());
		assertEquals("ABC1234", apiErrors.get(1).getDomain());
		assertEquals("BT2222", apiErrors.get(2).getDomain());
		assertEquals("BR0001", apiErrors.get(3).getDomain());
	}

	@Test
	public void testMapWarnings_whenNullWarnings_thenNullValidations()
	{
		List <ValidationError> warnings = mapper.mapWarnings(null);
		assertNull(warnings);
	}

	@Test
	public void testMapWarnings_sizeMatches()
	{
		List <ValidationError> warnings = mapper.mapWarnings(apiWarnings);
		assertEquals(apiWarnings.size(), warnings.size());
	}

	@Test
	public void testMapWarnings_valuesMatch()
	{
		List <ValidationError> warnings = mapper.mapWarnings(apiWarnings);
		assertEquals(apiWarnings.get(0).getErrorId(), warnings.get(0).getErrorId());
		assertEquals(apiWarnings.get(0).getDomain(), warnings.get(0).getField());
		assertEquals(apiWarnings.get(0).getMessage(), warnings.get(0).getMessage());
		assertEquals(ErrorType.WARNING, warnings.get(0).getType());
		assertEquals(ErrorType.ERROR, warnings.get(1).getType());
	}
}
