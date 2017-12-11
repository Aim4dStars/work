package com.bt.nextgen.core.security.profile;

import com.btfin.panorama.core.security.Roles;
import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.btfin.panorama.core.security.integration.customer.ChannelType;
import com.btfin.panorama.core.security.integration.customer.CredentialType;
import com.btfin.panorama.core.security.integration.customer.CustomerCredentialInformation;
import com.btfin.panorama.core.security.integration.customer.UserGroup;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.btfin.panorama.core.security.integration.userinformation.UserInformation;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.btfin.panorama.core.security.UserAccountStatus;
import com.btfin.panorama.core.security.profile.UserProfile;
import org.joda.time.DateTime;

import java.util.List;

public class UserProfileAdapterImpl implements UserProfile
{

	private final UserInformation userInfo;
	private final JobProfile jobProfile;
	private CustomerCredentialInformation customerCredentialInformation;

	public UserProfileAdapterImpl(UserInformation userInfo, JobProfile jobProfile, CustomerCredentialInformation customerCredentialInformation)
	{
		this.userInfo = userInfo;
		this.jobProfile = jobProfile;
		this.customerCredentialInformation = customerCredentialInformation;
	}

	public UserProfileAdapterImpl(UserInformation userInfo, JobProfile jobProfile)
	{
		this.userInfo = userInfo;
		this.jobProfile = jobProfile;
	}


	@Override public String getBankReferenceId()
	{
		return  customerCredentialInformation.getBankReferenceId();
	}

	@Override
	public UserKey getBankReferenceKey()
	{
		return  customerCredentialInformation.getBankReferenceKey();
	}

    @Override
    public CISKey getCISKey() {
        return null;
    }

    @Override public ClientKey getClientKey()
	{
		return userInfo.getClientKey();
	}

	@Override public void setClientKey(ClientKey personId)
	{

	}

	@Override public List<FunctionalRole> getFunctionalRoles()
	{
		return userInfo.getFunctionalRoles();
	}

	@Override public void setFunctionalRoles(List<FunctionalRole> functionalRoles)
	{

	}

	@Override public JobRole getJobRole()
	{
		return jobProfile.getJobRole();
	}

	@Override public String getPersonJobId()
	{
		return jobProfile.getPersonJobId();
	}

	@Override
	public DateTime getCloseDate() {
		return jobProfile.getCloseDate();
	}

	@Override public JobKey getJob()
	{
		return userInfo.getJob();
	}

	@Override public String getProfileId()
	{
		return userInfo.getProfileId();
	}

	@Override public UserAccountStatus getPrimaryStatus()
	{
		return customerCredentialInformation.getPrimaryStatus();
	}

	@Override public CredentialType getCredentialType()
	{
		return customerCredentialInformation.getCredentialType();
	}

	@Override public List<Roles> getCredentialGroups()
	{
		return customerCredentialInformation.getCredentialGroups();
	}

	@Override public ChannelType getChannelType()
	{
		return customerCredentialInformation.getChannelType();
	}

	@Override public String getLastUsed()
	{
		return customerCredentialInformation.getLastUsed();
	}

	@Override public String getStartTimeStamp()
	{
		return customerCredentialInformation.getStartTimeStamp();
	}

	@Override public List<UserAccountStatus> getAllAccountStatusList()
	{
		return customerCredentialInformation.getAllAccountStatusList();
	}

	@Override public String getServiceLevel()
	{
		return customerCredentialInformation.getServiceLevel();
	}

	@Override public String getServiceStatusErrorCode()
	{
		return customerCredentialInformation.getServiceStatusErrorCode();
	}

	@Override public String getServiceStatusErrorDesc()
	{
		return customerCredentialInformation.getServiceStatusErrorDesc();
	}

	@Override public String getStatusInfo()
	{
		return customerCredentialInformation.getStatusInfo();
	}

	@Override public UserAccountStatus getUserAccountStatus()
	{
		return customerCredentialInformation.getUserAccountStatus();
	}

	@Override public DateTime getDate()
	{
		return customerCredentialInformation.getDate();
	}

	@Override
	public List<UserGroup> getUserGroup() {
		return customerCredentialInformation.getUserGroup();
	}

	@Override
	public String getUserReferenceId() {
		return customerCredentialInformation.getUserReferenceId();
	}

	@Override public String getCredentialId()
	{
		return  customerCredentialInformation.getCredentialId();
	}

	@Override public String getUsername()
	{
		return  customerCredentialInformation.getUsername();
	}

	@Override public void setCustomerCredentialInformation(CustomerCredentialInformation customerCredentialInformation)
	{
		this.customerCredentialInformation = customerCredentialInformation;
	}

    @Override
    public String getFullName() {
        return userInfo.getFullName();
    }

    @Override
    public String getFirstName() {
        return userInfo.getFirstName();
    }

    @Override
    public String getLastName() {
        return userInfo.getLastName();
    }

    @Override
    public String getSafiDeviceId() {
        return userInfo.getSafiDeviceId();
    }

	@Override
	public String getNameId()
	{
		return customerCredentialInformation.getNameId();
	}

	@Override
	public String getPpId() {
		return (customerCredentialInformation != null) ? customerCredentialInformation.getPpId() : null;
	}

	@Override
    public UserExperience getUserExperience() {
        return jobProfile.getUserExperience();
    }

	@Override
	public List<String> getUserRoles() {
		return userInfo.getUserRoles();
	}
}
