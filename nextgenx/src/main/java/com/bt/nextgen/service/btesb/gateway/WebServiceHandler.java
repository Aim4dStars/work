package com.bt.nextgen.service.btesb.gateway;


import java.util.Map;

public interface WebServiceHandler
{
    /**
     * Send the request payload to the remote system and parses the response into a domain bean.
     *
     * @param serviceKey
     * @param requestPayload
     * @param responseType
     * @param serviceErrors
     * @param <T>
     * @return
     */
    public <T> T sendToWebServiceAndParseResponseToDomain(String serviceKey, Object requestPayload, Class<T> responseType,
                                                          com.bt.nextgen.service.ServiceErrors serviceErrors);


    /**
     * Return all Namespaces that may be required by the parser during parsing into the domain object.
     *
     * @return
     */
    public Map<String, String> getNamespaceMap();
}