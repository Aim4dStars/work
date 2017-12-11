package com.bt.nextgen.service.error;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;

public abstract class IntegrationException extends RuntimeException  
{

    private static final Logger logger = LoggerFactory.getLogger(IntegrationException.class);

	protected String system;
	protected String originatingSystem;
	protected String transactionId;
	protected String code;
	protected String description;

    protected static String serverIdentifier;

	private static String SEPARATOR = "-";


    /**
     * Method to generate the error identifier
     *
     * @return a string representing the system which errored, their code for the error, the UI Node this occurred on and the transaction ID of the failed message
     */
	public String getAbbreviatedErrorForDisplay()
	{
		String shortSystemName = "UKN";
		
		if (!StringUtils.isEmpty(getSystem()))
		{
			shortSystemName = getSystem().substring(0, 3);
		}

        StringBuffer displayError = new StringBuffer(shortSystemName).append(SEPARATOR);
        displayError.append(getCode()).append(SEPARATOR);
        displayError.append(getServerIdentifier()).append(SEPARATOR);
        displayError.append(getTransactionId());

		return displayError.toString();
	}
	
	public IntegrationException(String originatingSystem, String transactionId, String errorCode, String description, Throwable cause)
	{
		this(cause);
		this.originatingSystem = originatingSystem;
		this.transactionId = transactionId;
		this.code = errorCode;
		this.description = description;
	}	
	
	public IntegrationException(String originatingSystem, String transactionId, String errorCode, String description)
	{
		this.originatingSystem = originatingSystem;
		this.transactionId = transactionId;
		this.code = errorCode;
		this.description = description;
	}	
	
	public IntegrationException(String message) 
	{
		super(message);
	}

	public IntegrationException(String message, Throwable cause) 
	{
		super(message, cause);
	}

	public IntegrationException(Throwable cause) 
	{
		super(cause);
	}

	public IntegrationException(String message, Throwable cause,boolean enableSuppression, boolean writableStackTrace) 
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	

	public String getSystem() {
		return system;
	}

	public String getOriginatingSystem() {
		return originatingSystem;
	}
	public void setOriginatingSystem(String originatingSystem) {
		this.originatingSystem = originatingSystem;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

    /**
     * Method to load the identifier which this exception was generated on
     * @return a string which represents the server the ui
     */
    public String getServerIdentifier()
    {
        try
        {
            if(serverIdentifier ==null)
                serverIdentifier =parseMachineName(Inet4Address.getLocalHost().getHostName());

            return serverIdentifier;
        }
        catch(Exception err)
        {
            logger.warn("Loading server name failed, this will lead to unhelpful error reporting",err);
            return "";

        }
    }


    /**
     * Method to return an abbreviated version of the server name
     *
     * @param machineName The FQDN of the domain name which needs to be abbreviated
     * @return An abbreviated identified to display on the technical difficulties message
     * @throws Exception
     */
    public static String parseMachineName(String machineName)  throws Exception
    {

        String domainPart = (machineName.contains(".")) ? machineName.substring(machineName.indexOf(".")) : "";
        //Check that there is more than simply a '.' and then take the next character
        if(domainPart.length()>1)
            domainPart = domainPart.substring(1,2);

        if(machineName.contains("."))
            machineName = machineName.substring(0,machineName.indexOf("."));

        return machineName.substring(machineName.length()-3) + domainPart;
    }


}