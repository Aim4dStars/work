package com.bt.nextgen.service.integration.messages;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.bt.nextgen.service.integration.asset.Term;

/**
 * 
 * @author F121237
 * 
 * Converter defined for converting String to Term type with specific pattern
 *
 */
@Component
public class TermConverter implements Converter <String, Term>
{
	@Override
	public Term convert(String source)
	{
		if (source != null)
		{
			return new Term(source);
		}

		return null;
	}
}
