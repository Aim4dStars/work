package com.bt.nextgen.service.btesb.base;

/**
 * Interface to ESB service
 * 
 * @author m143067
 *
 */
public interface RemoteService
{
	/**
	 * Send request to web-service
	 * 
	 * @param requestObject the object to marshall
	 * @param serviceKey the service key (e.g bt-esb, avaloq, etc)
	 * 
	 * @return a response object generated from XSD
	 */
	Object sendRequest(Object requestObject, String serviceKey);

	/**
	 * Send system request to web-service
	 * 
	 * @param requestObject the object to marshall
	 * @param serviceKey the service key (e.g bt-esb, avaloq, etc)
	 * 
	 * @return a response object generated from XSD
	 */
	Object sendSystemRequest(Object requestObject, String serviceKey);
}
