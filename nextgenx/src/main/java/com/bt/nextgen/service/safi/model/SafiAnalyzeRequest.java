package com.bt.nextgen.service.safi.model;

import com.bt.nextgen.api.safi.model.EventModel;
import com.bt.nextgen.service.security.model.HttpRequestParams;

public class SafiAnalyzeRequest implements TwoFactorAuthenticationBasicRequest {
	private HttpRequestParams httpRequestParams;
	private EventModel eventModel;

	
	@Override
	public HttpRequestParams getRequestParams() {
		return httpRequestParams;
	}

	@Override
	public void setHttpRequestParams(HttpRequestParams httpRequestParams) {
		this.httpRequestParams = httpRequestParams;
	}

	public EventModel getEventModel() {
		return eventModel;
	}

	public void setEventModel(EventModel eventModel) {
		this.eventModel = eventModel;
	}
}
