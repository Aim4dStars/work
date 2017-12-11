package com.bt.nextgen.service.group.customer.groupesb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by L069679 on 16/01/2017.
 */
public class CustomerRawDataImpl implements CustomerRawData {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerRawDataImpl.class);
    private String rawResponse;
    private Object responseObject;

    public String getRawResponse() {
        return rawResponse;
    }

    public void setRawResponse(String rawResponse) {
        this.rawResponse = rawResponse;
    }

    public CustomerRawDataImpl(Object rawResponse) throws JsonProcessingException {
        this.rawResponse = getJson(rawResponse);
		this.responseObject = rawResponse;    }

    public static String getJson(Object obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        String jsonReq = null;
        try {
            jsonReq = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error Processing Json", e);
        }
        return jsonReq;
    }

	@Override
	public Object getResponseObject() {
		return responseObject;
	}

	@Override
	public void setResponseObject(Object responseObject) {
		this.responseObject = responseObject;
	}
    
}