package com.bt.nextgen.service.security.model;

public class HttpRequestParams 
{
	String httpAccept = "";
	String httpAcceptChars = "";
	String httpAcceptEncoding = "";
	String httpAcceptLanguage = "";
	String httpReferrer = "";
	String httpOriginatingIpAddress = "";
	String httpUserAgent = "";
	String httpXForwardedHost = "";
	
		
	public String getHttpAccept() {
		return httpAccept;
	}
	public void setHttpAccept(String httpAccept) {
		this.httpAccept = httpAccept;
	}
	public String getHttpAcceptChars() {
		return httpAcceptChars;
	}
	public void setHttpAcceptChars(String httpAcceptChars) {
		this.httpAcceptChars = httpAcceptChars;
	}
	public String getHttpAcceptEncoding() {
		return httpAcceptEncoding;
	}
	public void setHttpAcceptEncoding(String httpAcceptEncoding) {
		this.httpAcceptEncoding = httpAcceptEncoding;
	}
	public String getHttpAcceptLanguage() {
		return httpAcceptLanguage;
	}
	public void setHttpAcceptLanguage(String httpAcceptLanguage) {
		this.httpAcceptLanguage = httpAcceptLanguage;
	}
	public String getHttpReferrer() {
		return httpReferrer;
	}
	public void setHttpReferrer(String httpReferrer) {
		this.httpReferrer = httpReferrer;
	}
	public String getHttpOriginatingIpAddress() {
		return httpOriginatingIpAddress;
	}
	public void setHttpOriginatingIpAddress(String httpOriginatingIpAddress) {
		this.httpOriginatingIpAddress = httpOriginatingIpAddress;
	}
	public String getHttpUserAgent() {
		return httpUserAgent;
	}
	public void setHttpUserAgent(String httpUserAgent) {
		this.httpUserAgent = httpUserAgent;
	}
	public String getHttpXForwardedHost() {
		return httpXForwardedHost;
	}
	public void setHttpXForwardedHost(String httpXForwardedHost) {
		this.httpXForwardedHost = httpXForwardedHost;
	}		
}

