package com.bt.nextgen.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorLog extends ClientLog
{
	private String file;
	private Integer lineNumber;
	private Integer charNumber;
	private String name;
	private String readyState;
	private Integer statusCode;
	private String errorStack;
	private String reason;

	public String getFile() {
		return file;
	}

	public Integer getLineNumber() {
		return lineNumber;
	}

	public Integer getCharNumber() {
		return charNumber;
	}

	public String getName() {
		return name;
	}

	public String getReadyState() {
		return readyState;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public String getErrorStack() {
		return errorStack;
	}

	public String getReason() {
		return reason;
	}
}
