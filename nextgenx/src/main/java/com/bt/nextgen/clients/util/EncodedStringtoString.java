package com.bt.nextgen.clients.util;

import java.io.IOException;


import com.btfin.panorama.core.security.encryption.EncodedString;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class EncodedStringtoString extends JsonSerializer<EncodedString> {

	@Override
	public void serialize(EncodedString encodedString, JsonGenerator jsonGenerator, SerializerProvider provider)
			throws IOException, JsonProcessingException {
		jsonGenerator.writeString(encodedString.toString());
	}

}
