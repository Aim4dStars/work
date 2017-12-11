package com.bt.nextgen.service.security;

import au.com.rsa.ps.smsotp.SMSOTPAuthenticationRequest;
import au.com.rsa.ps.smsotp.SMSOTPChallengeRequest;

import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.api.safi.model.EventModel;
import com.rsa.csd.ws.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.ws.security.util.Base64;
import java.io.BufferedReader;
import java.io.FileReader;
import com.bt.nextgen.service.security.model.HttpRequestParams;
import com.bt.nextgen.core.util.Properties;

@Component
public class SmsHelperServiceImpl implements SmsHelperService
{
	private static final Logger logger = LoggerFactory.getLogger(SmsHelperServiceImpl.class);
	//Cash 5
	//public static final String ORG_NAME = "WPACBTNG";
	public static final String ORG_NAME = "BTNG";
	public static final String WEBSERVICE_VERSION = "6.0";
	//TODO: These fields will be provided by SAFI team to BT NextGen.
	public static final String CALLER_CREDENTIAL = "123";
	public static final String CALLER_ID = "12";
	
	//TODO: Replace device id with callout to avaloq service
	public static final String DEVICE_ID = "1f06f4f2-788b-4cba-94d2-b883badd3c1e";
	//TODO: Replace avaloq customer number with actual avaloq call
	public static final String AVALOQ_CUSTOMER_NUMBER = "60000021";
	
	@Autowired
	UserProfileService userProfileService;

	public GenericActionTypeList setAnalyzeActionTypeListRequest()
	{
		GenericActionTypeList genericActionTypeList = new GenericActionTypeList();
		GenericActionType genericActionType = GenericActionType.SET_USER_STATUS;
		genericActionTypeList.getGenericActionTypes().add(genericActionType);
		return genericActionTypeList;
	}
	
	public IdentificationData setAnalyzeIdentificationData()
	{
		IdentificationData identificationData = new IdentificationData();
		identificationData.setClientSessionId("");
		identificationData.setClientTransactionId("");
		identificationData.setOrgName(ORG_NAME);
		identificationData.setUserName("");
		identificationData.setUserStatus(UserStatus.VERIFIED);
		identificationData.setUserType(WSUserType.PERSISTENT);
		return identificationData;
	}
	
	public IdentificationData setIdentificationData()
	{
		IdentificationData identificationData = new IdentificationData();
		identificationData.setOrgName("");
		identificationData.setSessionId("");
		identificationData.setTransactionId("");
		identificationData.setUserName("");
		identificationData.setUserType(WSUserType.PERSISTENT);
		return identificationData;
	}
	

	/**
	 * @param requestType One of: ANALZE, CREATEUSER, NOTIFY, or QUERY
	 */
	public MessageHeader setMessageHeader(RequestType requestType)
	{
		MessageHeader messageHeader = new MessageHeader();
		messageHeader.setApiType(APIType.DIRECT_SOAP_API);
		messageHeader.setRequestType(requestType);
		messageHeader.setVersion(WEBSERVICE_VERSION);
		return messageHeader;
	}	
	
	//Cash 5
	/*public SecurityHeader setSecurityHeader()
	{
		SecurityHeader securityHeader = new SecurityHeader();
		securityHeader.setCallerCredential(CALLER_CREDENTIAL);
		securityHeader.setCallerId(CALLER_ID);
		securityHeader.setMethod(AuthorizationMethod.PASSWORD);
		return securityHeader;
	}*/
	
	public SecurityHeader setSecurityHeader()
	{
		SecurityHeader securityHeader = new SecurityHeader();
		securityHeader.setCallerCredential(Properties.getString("safi.password"));
		securityHeader.setCallerId(Properties.getString("safi.username"));
		securityHeader.setMethod(AuthorizationMethod.PASSWORD);
		return securityHeader;
	}
	
	
	public DeviceRequest setAnalyzeDeviceRequest()
	{
		DeviceRequest deviceRequest = new DeviceRequest();
		deviceRequest.setDevicePrint("");
		deviceRequest.setDeviceTokenCookie("");
		deviceRequest.setDeviceTokenFSO("");
		deviceRequest.setHttpAccept("");
		deviceRequest.setHttpAcceptChars("");
		deviceRequest.setHttpAcceptEncoding("");
		deviceRequest.setHttpAcceptLanguage("");
		deviceRequest.setHttpReferrer("");
		deviceRequest.setIpAddress("");
		deviceRequest.setUserAgent("");
		
		return deviceRequest;
	}
	
	public CredentialChallengeRequestList setCredentialChallengeRequestList()
	{
		CredentialChallengeRequestList credentialChallengeRequestList = new CredentialChallengeRequestList();
		AcspChallengeRequestData acspChallengeRequestData = new AcspChallengeRequestData();
		SMSOTPChallengeRequest sMSOTPChallengeRequest = new SMSOTPChallengeRequest();
		sMSOTPChallengeRequest.setDeviceId("");
		sMSOTPChallengeRequest.setNetworkId("");
		sMSOTPChallengeRequest.setInitDevice(true);
		sMSOTPChallengeRequest.setTransactionId("");
		sMSOTPChallengeRequest.setOrganisationId("");
		sMSOTPChallengeRequest.setBrandSilo("");
		sMSOTPChallengeRequest.setRequestingUserId("");
		sMSOTPChallengeRequest.setMessageText("");
		sMSOTPChallengeRequest.setSamlAssertion("");
		
		AcspChallengeRequest payload = sMSOTPChallengeRequest;
		acspChallengeRequestData.setPayload(payload);
		credentialChallengeRequestList.setAcspChallengeRequestData(acspChallengeRequestData);
		
		return credentialChallengeRequestList;
	}
	
	public DeviceRequest setDeviceRequest()
	{
		DeviceRequest deviceRequest = new DeviceRequest();
		deviceRequest.setDevicePrint("");
		deviceRequest.setDeviceTokenFSO("");
		deviceRequest.setIpAddress("");
		deviceRequest.setUserAgent("");
		
		return deviceRequest;
	}
	
	public CredentialDataList setCredentialDataList(String smsCode)
	{
		CredentialDataList CredentialDataList = new CredentialDataList();
		AcspAuthenticationRequestData acspAuthenticationRequestData = new AcspAuthenticationRequestData();
		SMSOTPAuthenticationRequest sMSOTPAuthenticationRequest = new SMSOTPAuthenticationRequest();
		sMSOTPAuthenticationRequest.setSmsOTP(smsCode);
		AcspAuthenticationRequest acspAuthenticationRequest = sMSOTPAuthenticationRequest;
		acspAuthenticationRequestData.setPayload(acspAuthenticationRequest);
		CredentialDataList.setAcspAuthenticationRequestData(acspAuthenticationRequestData);
		
		return CredentialDataList;
	}
	
	//Cash 5
	/*public EventDataList setEventDataList(EventModel eventModel)
	{
		EventDataList eventDataList = new EventDataList();
		EventData eventData = new EventData();
		ClientDefinedFact clientDefinedFactAccountType = new ClientDefinedFact();
		ClientDefinedFact clientDefinedFactPayeeType = new ClientDefinedFact();
		FactList factList = new FactList();
		
		clientDefinedFactAccountType.setName("");
		clientDefinedFactAccountType.setValue("");
		clientDefinedFactAccountType.setDataType(DataType.STRING);
		
		factList.getFact().add(clientDefinedFactAccountType);
		
		clientDefinedFactPayeeType.setName("");
		clientDefinedFactPayeeType.setValue("");
		clientDefinedFactPayeeType.setDataType(DataType.STRING);
		
		factList.getFact().add(clientDefinedFactPayeeType);

		eventData.setClientDefinedAttributeList(factList);
		eventData.setClientDefinedEventType("");
		eventData.setEventDescription("");
		eventData.setEventType(EventType.PAYMENT);
		
		eventDataList.getEventData().add(eventData);
		
		return eventDataList;
	}	*/
		
	public EventDataList setEventDataList(EventModel eventModel)
	{
		EventDataList eventDataList = new EventDataList();
		EventData eventData = new EventData();
		ClientDefinedFact clientDefinedFactAccountType = new ClientDefinedFact();
		
		FactList factList = new FactList();
		
		
		if (eventModel.getPayeeType() != null)
		{
			ClientDefinedFact clientDefinedFactPayeeType = createFact("BT_ACCT_TYPE", "IDPS"); //eventModel.getPayeeType().toString());	
			factList.getFact().add(clientDefinedFactPayeeType);			
		}	
		
		if (eventModel.getClientDefinedEventType() != null && eventModel.getClientDefinedEventType().equalsIgnoreCase("CHANGE_DAILY_LIMIT"))
		{
			ClientDefinedFact clientDefinedFactPayeeType = createFact("BT_PAYEE_TYPE", "ANY");	
			factList.getFact().add(clientDefinedFactPayeeType);	
		}

		eventData.setClientDefinedAttributeList(factList);		
		//eventData.setClientDefinedEventType(EventType.PAYMENT);
		setEventType(eventData, eventModel.getClientDefinedEventType().toString());
		
		eventDataList.getEventData().add(eventData);
		
		/*
		if (eventModel.getClientDefinedEventType().equalsIgnoreCase("PAYMENT"))
		{
			EventData transactionEventData = new EventData();
			transactionEventData.setTransactionData(setTransactionData(eventModel));
			eventDataList.getEventData().add(transactionEventData);
		}*/
		
		return eventDataList;
	}
	
	public IdentificationData setAnalyzeIdentificationData(String clientSessionId, String clientTransactionId)
	{		
		IdentificationData identificationData = setBaseIdentificationData();	
		identificationData.setClientSessionId(clientSessionId);
		identificationData.setClientTransactionId(clientTransactionId);		
		identificationData.setUserName(userProfileService.getGcmId());
		
		return identificationData;
	}
	
	// Creates identification data for SAFI AUTHENTICATE requests
		public IdentificationData setIdentificationData(String sessionId, String transactionId, String... userName)
		{
			IdentificationData identificationData = setBaseIdentificationData();

			if(userName != null && userName.length > 0 && StringUtils.isNotEmpty(userName[0]))
			{
				identificationData.setUserName(userName[0]);
			}
			else
			{
				identificationData.setUserName(userProfileService.getAvaloqId());
			}
			
			//IdentificationData.setClientSessionId("");		 
					
			// For challenge () call which is performed as a result risk assessment outcome, i.e. CHALLENGE outcome from previous analyze call, this field value has to be populated with value from previous analyze() call which resulted in policy action CHALLENGE.
			// For challenge () call without an analyze call done earlier, this field is not required.
			// For authenticate() call, this field value has to be populated with value from previous challenge () call.
			identificationData.setSessionId(sessionId);		
			identificationData.setTransactionId(transactionId);		
			
			return identificationData;
		}
		
		public IdentificationData setBaseIdentificationData()
		{
			IdentificationData identificationData = new IdentificationData();
			identificationData.setOrgName(ORG_NAME);
			identificationData.setUserType(WSUserType.PERSISTENT);		
			identificationData.setUserStatus(UserStatus.VERIFIED);		
			return identificationData;
		}	
		
		
		
		private DeviceRequest setDeviceRequestBase(HttpRequestParams requestParams)
		{
			DeviceRequest deviceRequest = new DeviceRequest();
			deviceRequest.setHttpAccept(requestParams.getHttpAccept());
			deviceRequest.setHttpAcceptChars(requestParams.getHttpAcceptChars());
			deviceRequest.setHttpAcceptEncoding(requestParams.getHttpAcceptEncoding());
			deviceRequest.setHttpAcceptLanguage(requestParams.getHttpAcceptLanguage());
			deviceRequest.setHttpReferrer(requestParams.getHttpReferrer());
			deviceRequest.setIpAddress(requestParams.getHttpOriginatingIpAddress());
			deviceRequest.setUserAgent(requestParams.getHttpUserAgent());
			
			return deviceRequest;
		}
		
		public DeviceRequest setAnalyzeDeviceRequest(HttpRequestParams requestParams, String deviceToken)
		{
			DeviceRequest deviceRequest = setDeviceRequestBase(requestParams);
			deviceRequest.setDevicePrint(deviceToken);
			//deviceRequest.setDeviceTokenCookie("");	
			return deviceRequest;
		}
		
		public DeviceRequest setChallengeDeviceRequest(HttpRequestParams requestParams, String devicePrint, String deviceToken)
		{
			DeviceRequest deviceRequest = setDeviceRequestBase(requestParams);
			deviceRequest.setDevicePrint(devicePrint);
			deviceRequest.setDeviceTokenCookie(deviceToken);	
			return deviceRequest;
		}
		
		public DeviceRequest setAuthenticateDeviceRequest(HttpRequestParams requestParams, String devicePrint, String deviceToken)
		{
			DeviceRequest deviceRequest = setDeviceRequestBase(requestParams);
			deviceRequest.setDevicePrint(devicePrint);
			deviceRequest.setDeviceTokenCookie(deviceToken);	
			return deviceRequest;
		}
		
		public CredentialChallengeRequestList setCredentialChallengeRequestList(String clientTransactionId) throws Exception
		{
			CredentialChallengeRequestList credentialChallengeRequestList = new CredentialChallengeRequestList();
			AcspChallengeRequestData acspChallengeRequestData = new AcspChallengeRequestData();
			SMSOTPChallengeRequest sMSOTPChallengeRequest = new SMSOTPChallengeRequest();
			
			// Safi device id or the avaloq device id		
			sMSOTPChallengeRequest.setDeviceId(userProfileService.getSafiDeviceIdentifier().getSafiDeviceId()); //(userProfileService.getPerson().getSafiDeviceId());
			logger.info("SAFI device id: {}", userProfileService.getSafiDeviceIdentifier().getSafiDeviceId());
			
			sMSOTPChallengeRequest.setNetworkId(userProfileService.getAvaloqId());
			sMSOTPChallengeRequest.setInitDevice(false);										// true for registration, false for other activities
			sMSOTPChallengeRequest.setTransactionId(clientTransactionId);						// Unique transaction id for this 2fa challenge
			sMSOTPChallengeRequest.setOrganisationId(ORG_NAME);
			sMSOTPChallengeRequest.setBrandSilo(ORG_NAME);
			sMSOTPChallengeRequest.setRequestingUserId(ORG_NAME);								// The Avaloq Customer Number of the user performing this challenge request. 
			sMSOTPChallengeRequest.setMessageText("Your SMS security code to approve your online transaction is SMSOTP");
					
			sMSOTPChallengeRequest.setSamlAssertion(getBase64EncodedSamlToken(userProfileService.getSamlToken().getToken()));
			
			AcspChallengeRequest payload = sMSOTPChallengeRequest;
			acspChallengeRequestData.setPayload(payload);
			credentialChallengeRequestList.setAcspChallengeRequestData(acspChallengeRequestData);
			
			return credentialChallengeRequestList;
		}
		
		/**
		 * Read in saml token and convert into base64 encoded string
		 * @return
		 * @throws Exception
		 */
		private String getBase64EncodedSamlToken() throws Exception
		{		
			BufferedReader br = new BufferedReader(new FileReader("C:\\Development\\nextgen\\src\\main\\resources\\saml-sample.xml"));
			String samlTokenString = "";
			
		    try 
		    {
		        StringBuilder sb = new StringBuilder();
		        String line = br.readLine();

		        while (line != null) 
		        {
		            sb.append(line);
		            sb.append('\n');
		            line = br.readLine();
		        }
		        
		        samlTokenString = sb.toString();
		    } 
		    catch(Exception e)
		    {
		    	logger.error("Unable to generate base64 encoded saml token", e);
		    	throw e;
		    }
		    finally 
		    {
		        br.close();
		    }
		    
			String encodedSamlToken = Base64.encode(samlTokenString.getBytes());
			return encodedSamlToken;
		}
		
		
		private String getBase64EncodedSamlToken(String token) throws Exception
		{
			String encodedSamlToken = Base64.encode(token.getBytes());
			return encodedSamlToken;		
		}		
		
		
		
		public CredentialDataList setCredentialDataList(String smsCode, SafiAnalyzeResult safiResponse) throws Exception
		{
			CredentialDataList CredentialDataList = new CredentialDataList();
			AcspAuthenticationRequestData acspAuthenticationRequestData = new AcspAuthenticationRequestData();
			SMSOTPAuthenticationRequest sMSOTPAuthenticationRequest = new SMSOTPAuthenticationRequest();
			sMSOTPAuthenticationRequest.setSmsOTP(smsCode);
			
			try
			{
				sMSOTPAuthenticationRequest.setDeviceId(userProfileService.getSafiDeviceIdentifier().getSafiDeviceId());
				sMSOTPAuthenticationRequest.setSamlAssertion(getBase64EncodedSamlToken(userProfileService.getSamlToken().getToken()));
			}
			catch (IllegalStateException ise)
			{
				logger.info("User session not present -- retrieving safi device id and saml assertion from previous analyze call");
				sMSOTPAuthenticationRequest.setDeviceId(safiResponse.getDeviceId());
				sMSOTPAuthenticationRequest.setSamlAssertion(safiResponse.getSamlAssertion());
			}

			sMSOTPAuthenticationRequest.setTransactionId(safiResponse.getTransactionId());
			AcspAuthenticationRequest acspAuthenticationRequest = sMSOTPAuthenticationRequest;
			acspAuthenticationRequestData.setPayload(acspAuthenticationRequest);
			CredentialDataList.setAcspAuthenticationRequestData(acspAuthenticationRequestData);
			
			return CredentialDataList;
		}
		
		
		/**
		 * Generates a credential data object with a custom saml token.
		 * This is applicable if you are unauthenticated (first time registration, forgot password) and are attempting to authenticate to SAFI
		 * @param smsCode
		 * @param safiResponse
		 * @param samlToken
		 * @return
		 * @throws Exception
		 */
		public CredentialDataList setCredentialDataList(String smsCode, SafiAnalyzeResult safiResponse, String samlToken, String transactionId) throws Exception
		{
			CredentialDataList CredentialDataList = setCredentialDataList(smsCode, safiResponse);
			SMSOTPAuthenticationRequest sMSOTPAuthenticationRequest = (SMSOTPAuthenticationRequest) CredentialDataList.getAcspAuthenticationRequestData().getPayload();
			sMSOTPAuthenticationRequest.setSamlAssertion(samlToken);
			sMSOTPAuthenticationRequest.setTransactionId(transactionId);
			AcspAuthenticationRequest acspAuthenticationRequest = sMSOTPAuthenticationRequest;
			AcspAuthenticationRequestData acspAuthenticationRequestData = new AcspAuthenticationRequestData();
			acspAuthenticationRequestData.setPayload(acspAuthenticationRequest);
			
			CredentialDataList.setAcspAuthenticationRequestData(acspAuthenticationRequestData);
			
			return CredentialDataList;
		}
		
		
		public ClientDefinedFact createFact(String name, String value)
		{
			ClientDefinedFact newFact = new ClientDefinedFact();
			newFact.setName(name);
			newFact.setValue(value);
			newFact.setDataType(DataType.STRING);
			return newFact;
		}
		
		
		
		
		private TransactionData setTransactionData(EventModel model)
		{
			TransactionData transactionData = new TransactionData();

			Amount amount = new Amount();
			
			if (model.getAmount() != null && NumberUtils.isNumber(model.getAmount()))
			{	
				Double inAmount = new Double(model.getAmount());
				amount.setAmount(inAmount.longValue());
				amount.setCurrency("AUD");
				transactionData.setAmount(amount);			
			}	
			
			return transactionData;
		}
		
		
		
		private EventData setEventType(EventData eventData, String eventTypeCode)
		{
			if (eventTypeCode.equalsIgnoreCase("PAYMENT"))
			{
				eventData.setEventType(EventType.PAYMENT);
			}
			else if (eventTypeCode != null && eventTypeCode.equalsIgnoreCase("FORGOTTEN_PASSWORD"))
			{
				eventData.setEventType(EventType.CHANGE_PASSWORD);
				eventData.setClientDefinedEventType("FORGOTTEN_PASSWORD");
			}
			else if (eventTypeCode != null && eventTypeCode.equalsIgnoreCase("ADD_PAYEE"))
			{
				eventData.setEventType(EventType.ADD_PAYEE);
			}	
			else if (eventTypeCode != null && eventTypeCode.equalsIgnoreCase("CHANGE_DAILY_LIMIT"))
			{
				eventData.setEventType(EventType.CLIENT_DEFINED);
				eventData.setClientDefinedEventType("CHANGE_DAILY_LIMIT");
			}		
			else
			{
				eventData.setEventType(EventType.CLIENT_DEFINED);
			}
			
			return eventData;
		}
		
		public RunRiskType setRunRiskType()
		{
			RunRiskType runRiskType = RunRiskType.ALL;	
			return runRiskType;
		}
		
		public ChannelIndicatorType setChannelIndicatorType()
		{
			ChannelIndicatorType channelIndicatorType = ChannelIndicatorType.WEB;
			return channelIndicatorType;
		}
		
		
		
		ChannelIndicatorType channelIndicatorType = ChannelIndicatorType.WEB;

		
		public String decodeDeviceTokenString(String encodedToken) throws UnsupportedEncodingException
		{
			String decodedToken;
			
			decodedToken = URLDecoder.decode(encodedToken, "UTF-8");
			
			return decodedToken;
		}
		
		

}
