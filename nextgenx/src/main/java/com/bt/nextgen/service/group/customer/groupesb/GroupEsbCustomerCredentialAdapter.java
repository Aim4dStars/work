package com.bt.nextgen.service.group.customer.groupesb;

import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.CredentialGroup;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.LifeCycleStatus;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.RetrieveChannelAccessCredentialResponse;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.UserCredentialDocument;
import com.btfin.panorama.core.security.Roles;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.web.ApiFormatter;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.integration.customer.ChannelType;
import com.btfin.panorama.core.security.integration.customer.CredentialType;
import com.btfin.panorama.core.security.integration.customer.CustomerCredentialInformation;
import com.btfin.panorama.core.security.integration.customer.UserGroup;
import com.bt.nextgen.service.integration.IntegrationServiceUtil;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.btfin.panorama.core.security.UserAccountStatus;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by m035652 on 23/01/14.
 */
@SuppressWarnings({"squid:S1200","squid:S00116","findbugs:SS_SHOULD_BE_STATIC","squid:RedundantThrowsDeclarationCheck","squid:MethodCyclomaticComplexity","squid:S00112","squid:S1155","squid:S1168"})
public class GroupEsbCustomerCredentialAdapter implements CustomerCredentialInformation
{
    private static final Logger logger = LoggerFactory.getLogger(GroupEsbCustomerCredentialAdapter.class);

	private static final String SVC_311_V4_ENABLED = "svc.311.v4.enabled";
    private RetrieveChannelAccessCredentialResponse credentialResponseV4;
	private au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v5.svc0311.RetrieveChannelAccessCredentialResponse credentialResponseV5;
	private au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v3.svc0311.RetrieveChannelAccessCredentialResponse credentialResponseV3;

    private static String TEST_USERNAME_ID = "fake-userid-for-test";


    private List<UserCredentialDocument> credentialDocumentsV4;
	private List<au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v3.svc0311.UserCredentialDocument> credentialDocumentsV3;
	private List<au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v5.svc0311.UserCredentialDocument> credentialDocumentsV5;

    private UserCredentialDocument primaryCredentialsV4;
	private au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v5.svc0311.UserCredentialDocument primaryCredentialsV5;
	private au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v3.svc0311.UserCredentialDocument primaryCredentialsV3;

    private List<UserAccountStatus> statuses;

    public static String LEVEL_SUCCESS = "Success";
    
	public GroupEsbCustomerCredentialAdapter(RetrieveChannelAccessCredentialResponse credentialResponse,
											  String requiredCustomerId, ServiceErrors serviceErrors) throws RuntimeException
{
	if(credentialResponse ==null )
	{
		serviceErrors.addError(new ServiceErrorImpl("The Credential Response was null"));
	}
	else if (requiredCustomerId == null)
	{
		serviceErrors.addError(new ServiceErrorImpl("No customer ID , cannot find the credential"));
	}
	else
	{

		this.credentialResponseV4 = credentialResponse;


		if (credentialResponse.getServiceStatus() == null
				|| !LEVEL_SUCCESS.equalsIgnoreCase(credentialResponse.getServiceStatus()
				.getStatusInfo()
				.get(0)
				.getLevel()
				.value()))
		{
			serviceErrors.addError(new ServiceErrorImpl("The credential query failed"));
		}else if (credentialResponse.getUserCredential() == null || credentialResponse.getUserCredential().size() == 0)
		{
			serviceErrors.addError(new ServiceErrorImpl("No user found with this credential"));
		}else
		{
			this.credentialDocumentsV4 = credentialResponse.getUserCredential();

			for (UserCredentialDocument doc : this.credentialDocumentsV4)
			{
				if ((doc.getUserName().getHasAlternateUserNameAlias() != null
					&& requiredCustomerId.equals(doc.getUserName().getHasAlternateUserNameAlias().getUserId()))
					|| (Properties.getSafeBoolean("gesb-retrieve-credentials.webservice.filestub") && TEST_USERNAME_ID.equals(doc.getUserName().getUserId())))
				{
                    logger.info("Pan number of customer matched to the Credential Response V4. Gcm Id{}", requiredCustomerId);

					if(TEST_USERNAME_ID.equals(doc.getUserName().getUserId())) {
						doc.getUserName().getHasAlternateUserNameAlias().setUserId(requiredCustomerId);
					}

					this.primaryCredentialsV4 = doc;
					break;
				}
				else
				{
					serviceErrors.addError(new ServiceErrorImpl("Unable to match customer {} to credential. Check getHasAlternateUserNameAlias field.", requiredCustomerId));
					logger.error("Unable to match customer {} to credential. Check getHasAlternateUserNameAlias field, requiredCustomerId)");
					throw new RuntimeException("Unable to match customer to credential. Check getHasAlternateUserNameAlias field");
				}
			}

			if (primaryCredentialsV4 == null)
			{
				serviceErrors.addError(new ServiceErrorImpl("Failed to find a matching credential for Id: " + requiredCustomerId
						+ " in credential response"));
			}
			else
			{
				List <LifeCycleStatus> lifeCycleStatusList = primaryCredentialsV4.getLifecycleStatus();
				statuses = new ArrayList <>();

				for (LifeCycleStatus lifeCycleStatus : lifeCycleStatusList)
				{
					String status = lifeCycleStatus.getStatus();
					statuses.add(UserAccountStatus.valueOf(status.toUpperCase()));
				}
				logger.info("Statuses found are: {}", statuses);
				Collections.sort(statuses);
			}
		}
	}

}



	//for version5 implementing same method
	public GroupEsbCustomerCredentialAdapter(au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v5.svc0311.RetrieveChannelAccessCredentialResponse credentialResponse,
											 String requiredCustomerId, ServiceErrors serviceErrors) throws RuntimeException
	{
		if(credentialResponse ==null )
		{
			serviceErrors.addError(new ServiceErrorImpl("The Credential Response was null"));
		}
		else if (requiredCustomerId == null)
		{
			serviceErrors.addError(new ServiceErrorImpl("No customer ID , cannot find the credential"));
		}
		else
		{

			this.credentialResponseV5 = credentialResponse;


			if (credentialResponse.getServiceStatus() == null
					|| !LEVEL_SUCCESS.equalsIgnoreCase(credentialResponse.getServiceStatus()
					.getStatusInfo()
					.get(0)
					.getLevel()
					.value()))
			{
				serviceErrors.addError(new ServiceErrorImpl("The credential query failed"));
			}else if (credentialResponse.getUserCredential() == null || credentialResponse.getUserCredential().size() == 0)
			{
				serviceErrors.addError(new ServiceErrorImpl("No user found with this credential"));
			}else
			{
				this.credentialDocumentsV5 = credentialResponse.getUserCredential();

				for (au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v5.svc0311.UserCredentialDocument doc : this.credentialDocumentsV5)
				{
					if (doc.getSourceSystem().equalsIgnoreCase("WRAP") && null!=doc.getUserName().getUserId())
					{
						logger.info("There is PPID Associated with this user and its value is {}",doc.getUserName().getUserId());
						this.primaryCredentialsV5 = doc;
						break;
					}

				}

				if (primaryCredentialsV5 == null)
				{
					serviceErrors.addError(new ServiceErrorImpl("Failed to find a matching credential for Id: " + requiredCustomerId
							+ " in credential response"));
				}

			}
		}

	}


	public GroupEsbCustomerCredentialAdapter(au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v3.svc0311.RetrieveChannelAccessCredentialResponse credentialResponse,
											 String requiredCustomerId, ServiceErrors serviceErrors) throws RuntimeException
	{
		if(credentialResponse ==null )
		{
			serviceErrors.addError(new ServiceErrorImpl("The Credential Response was null"));
		}
		else if (requiredCustomerId == null)
		{
			serviceErrors.addError(new ServiceErrorImpl("No customer ID , cannot find the credential"));
		}
		else
		{

			this.credentialResponseV3 = credentialResponse;

			if (credentialResponse.getServiceStatus() == null
					|| !LEVEL_SUCCESS.equalsIgnoreCase(credentialResponse.getServiceStatus()
					.getStatusInfo()
					.get(0)
					.getLevel()
					.value()))
			{
				serviceErrors.addError(new ServiceErrorImpl("The credential query failed"));
			}else if (credentialResponse.getUserCredential() == null || credentialResponse.getUserCredential().size() == 0)
			{
				serviceErrors.addError(new ServiceErrorImpl("No user found with this credential"));
			}else
			{
				this.credentialDocumentsV3 = credentialResponse.getUserCredential();

				for (au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v3.svc0311.UserCredentialDocument doc : this.credentialDocumentsV3)
				{
					if ((requiredCustomerId.equals(doc.getUserName().getUserId()))
							|| (Properties.getSafeBoolean("gesb-retrieve-credentials.webservice.filestub") && TEST_USERNAME_ID.equals(doc.getUserName()
							.getUserId())))
					{
						logger.info("Pan number of customer matched to the Credential Response V3. User Id{}", requiredCustomerId);
						if(TEST_USERNAME_ID.equals(doc.getUserName().getUserId())) {
							doc.getUserName().setUserId(requiredCustomerId);
						}
						this.primaryCredentialsV3 = doc;
						break;
					}
				}

				if (primaryCredentialsV3 == null)
				{
					serviceErrors.addError(new ServiceErrorImpl("Failed to find a matching credential for Id: " + requiredCustomerId
							+ " in credential response"));
				}
				else
				{
					List <au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v3.svc0311.LifeCycleStatus> lifeCycleStatusList = primaryCredentialsV3.getLifecycleStatus();
					statuses = new ArrayList <>();

					for (au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v3.svc0311.LifeCycleStatus lifeCycleStatus : lifeCycleStatusList)
					{
						String status = lifeCycleStatus.getStatus();
						statuses.add(UserAccountStatus.valueOf(status.toUpperCase()));
					}
					Collections.sort(statuses);
				}
			}
		}

	}
    
    @Override
    public String getBankReferenceId()
	{
		if(Properties.getSafeBoolean(SVC_311_V4_ENABLED)){
			if (primaryCredentialsV4 == null || primaryCredentialsV4.getUserName() == null)
				return null;
			return primaryCredentialsV4.getUserName().getHasAlternateUserNameAlias().getUserId();
		}
		else{
			if (primaryCredentialsV3 == null || primaryCredentialsV3.getUserName() == null)
				return null;
			return primaryCredentialsV3.getUserName().getUserId();
		}
    }

	@Override
    public UserKey getBankReferenceKey()
	{
		if(getBankReferenceId()==null)
			return null;
		return UserKey.valueOf(getBankReferenceId());

	}

    @Override
    public CISKey getCISKey() {
        return null;
    }

    @Override
    public String getCredentialId()
	{
		if(Properties.getSafeBoolean(SVC_311_V4_ENABLED)){
			if (primaryCredentialsV4 == null || primaryCredentialsV4.getInternalIdentifier() == null)
				return null;
			return primaryCredentialsV4.getInternalIdentifier().getCredentialId();
		}
		else{
			if (primaryCredentialsV3 == null || primaryCredentialsV3.getInternalIdentifier() == null)
				return null;
			return primaryCredentialsV3.getInternalIdentifier().getCredentialId();
		}
    }

    @Override
    public String getUsername()
	{
		if(Properties.getSafeBoolean(SVC_311_V4_ENABLED)){
			if (primaryCredentialsV4 == null || primaryCredentialsV4.getUserName() == null)
				return null;
			return primaryCredentialsV4.getUserName().getUserAlias();
		}
		else{
			if (primaryCredentialsV3 == null || primaryCredentialsV3.getUserName() == null)
				return null;
			return primaryCredentialsV3.getUserName().getUserAlias();
		}
    }

    @Override
    public UserAccountStatus getPrimaryStatus()
	{
        if(statuses==null||statuses.size()==0)
            return null;

        Collections.sort(statuses);
        return statuses.get(0);
    }

    @Override
    public CredentialType getCredentialType()
	{
		if(Properties.getSafeBoolean(SVC_311_V4_ENABLED)){
			if (primaryCredentialsV4 == null || primaryCredentialsV4.getCredentialType() == null)
				return null;
			return CredentialType.fromRawType(primaryCredentialsV4.getCredentialType());
		}
		else{
			if (primaryCredentialsV3 == null || primaryCredentialsV3.getCredentialType() == null)
				return null;
			return CredentialType.fromRawType(primaryCredentialsV3.getCredentialType());
		}
    }

	@Override
	public List<Roles> getCredentialGroups()
	{
		if(Properties.getSafeBoolean(SVC_311_V4_ENABLED)) {
			if (primaryCredentialsV4 == null || CollectionUtils.isEmpty(primaryCredentialsV4.getCredentialGroup()))
				return null;
            return getCredentialRoles(primaryCredentialsV4.getCredentialGroup());
		}
		else{
			if (primaryCredentialsV3 == null || CollectionUtils.isEmpty(primaryCredentialsV3.getCredentialGroup()))
				return null;
			return new ArrayList(Arrays.asList(Roles.fromRawName(primaryCredentialsV3.getCredentialGroup().get(0).getCredentialGroupType())));
		}
	}

    public List<Roles> getCredentialRoles(List<CredentialGroup> credentialGroups){
        List<Roles> roles = new ArrayList<>();
        for(CredentialGroup group : credentialGroups){
            try {
                roles.add(Roles.fromRawName(group.getCredentialGroupType()));
            }catch(IllegalArgumentException e){
                // log here
                logger.debug("could not found received role (SVC0311) :{}", group.getCredentialGroupType(), e);
            }
        }
        return roles;
    }

    public Roles getCredentialGroupType()
	{
		if(Properties.getSafeBoolean(SVC_311_V4_ENABLED)) {
			if (primaryCredentialsV4 == null || CollectionUtils.isEmpty(primaryCredentialsV4.getCredentialGroup()))
				return null;
			return Roles.fromRawName(primaryCredentialsV4.getCredentialGroup().get(0).getCredentialGroupType());
		}else{
			if (primaryCredentialsV3 == null || CollectionUtils.isEmpty(primaryCredentialsV3.getCredentialGroup()))
				return null;
			return Roles.fromRawName(primaryCredentialsV3.getCredentialGroup().get(0).getCredentialGroupType());

		}
    }

    @Override
    public ChannelType getChannelType()
	{
		if(Properties.getSafeBoolean(SVC_311_V4_ENABLED)) {
			if (primaryCredentialsV4 == null || primaryCredentialsV4.getChannel() == null
					|| primaryCredentialsV4.getChannel().getChannelType() == null)
				return null;
			return ChannelType.fromRawType(primaryCredentialsV4.getChannel().getChannelType());
		}else{
			if (primaryCredentialsV3 == null || primaryCredentialsV3.getChannel() == null
					|| primaryCredentialsV3.getChannel().getChannelType() == null)
				return null;
			return ChannelType.fromRawType(primaryCredentialsV3.getChannel().getChannelType());
		}
    }

    @Override
    public String getLastUsed()
	{
		if(Properties.getSafeBoolean(SVC_311_V4_ENABLED)) {
			if (primaryCredentialsV4.getLastUsedDate() != null)
				return ApiFormatter.asShortDate(IntegrationServiceUtil.toDate(primaryCredentialsV4.getLastUsedDate()));
			else
				return "";
		}else{
			if (primaryCredentialsV3.getLastUsedDate() != null)
				return ApiFormatter.asShortDate(IntegrationServiceUtil.toDate(primaryCredentialsV3.getLastUsedDate()));
			else
				return "";
		}
    }

    @Override
    public String getStartTimeStamp()
	{
		if(Properties.getSafeBoolean(SVC_311_V4_ENABLED)) {
			return ApiFormatter.asShortDate(IntegrationServiceUtil.toDate(primaryCredentialsV4.getLifecycleStatus().get(0).getStartTimestamp()));
		}
		else{
			return ApiFormatter.asShortDate(IntegrationServiceUtil.toDate(primaryCredentialsV3.getLifecycleStatus().get(0).getStartTimestamp()));
		}
    }

    @Override
    public List<UserAccountStatus> getAllAccountStatusList()
	{
        return statuses;
    }

	@Override
	public String getServiceLevel() 
	{
		if(Properties.getSafeBoolean(SVC_311_V4_ENABLED)) {
			return credentialResponseV4.getServiceStatus().getStatusInfo().get(0).getLevel().value();
		}else{
			return credentialResponseV3.getServiceStatus().getStatusInfo().get(0).getLevel().value();
		}
	}

	@Override
	public String getServiceStatusErrorCode() 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServiceStatusErrorDesc() 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStatusInfo() 
	{
		if(Properties.getSafeBoolean(SVC_311_V4_ENABLED)) {
			return credentialResponseV4.getServiceStatus().getStatusInfo().get(0).getCode();
		}
		else{
			return credentialResponseV3.getServiceStatus().getStatusInfo().get(0).getCode();
		}
	}

	@Override
    public UserAccountStatus getUserAccountStatus()
	{
		if(Properties.getSafeBoolean(SVC_311_V4_ENABLED)) {
			return UserAccountStatus.valueOf(primaryCredentialsV4.getLifecycleStatus().get(0).getStatus().toUpperCase());
		}
		else{
			return UserAccountStatus.valueOf(primaryCredentialsV3.getLifecycleStatus().get(0).getStatus().toUpperCase());
		}
    }
	
	@Override
	public DateTime getDate() 
	{
		if(Properties.getSafeBoolean(SVC_311_V4_ENABLED)) {
            if (primaryCredentialsV4 == null || primaryCredentialsV4.getLifecycleStatus() == null
                    || primaryCredentialsV4.getLifecycleStatus().get(0) == null) {
                logger.error("Problem in Credential Response, please check that pan number is coming correctly and in right tag");
                return null;
            }
			return IntegrationServiceUtil.convertToDateTime(primaryCredentialsV4.getLifecycleStatus().get(0).getStartTimestamp());
		}else{
			return IntegrationServiceUtil.convertToDateTime(primaryCredentialsV3.getLifecycleStatus().get(0).getStartTimestamp());
		}
	}

	//TODO : Check which field in svc311 will return this information
	@Override
	public List<UserGroup> getUserGroup() {
		//TODO : map to the correct field
		return new ArrayList<>();
	}

    /**
     * The mapping for the Z number
     */
	@Override
    @SuppressWarnings("checkstyle:com.puppycrawl.tools.checkstyle.checks.coding.IllegalCatchCheck")
	public String getUserReferenceId() {
        try {
            return primaryCredentialsV4.getUserName().getUserId();
        } catch (Exception ex) {
            logger.warn("The current user may not have the z number");
        }
        return Attribute.EMPTY_STRING;
	}

	/**
	 * SVC0311 does not provide nameId value
	 * @return
	 */
	@Override
    public String getNameId()
	{
		return null;
	}

	@Override
	public String getPpId() {
		if (primaryCredentialsV5 == null || primaryCredentialsV5.getUserName() == null)
			return null;
		return primaryCredentialsV5.getUserName().getUserId();
	}
}