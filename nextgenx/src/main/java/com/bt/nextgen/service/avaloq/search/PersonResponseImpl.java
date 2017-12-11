package com.bt.nextgen.service.avaloq.search;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.integration.search.ProfileUserRole;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.search.PersonResponse;

import java.util.List;

@ServiceBean(xpath="person")
public class PersonResponseImpl implements PersonResponse
{
   	@ServiceElement(xpath = "gcm_id/val")
	private String gcmId;
	
	@ServiceElement(xpath = "is_benef/val")
	private boolean isBenef;

	@ServiceElement(xpath = "is_mbr/val")
	private boolean member;

	@ServiceElement(xpath = "person/val")
	private String personId;
	
	@ServiceElement(xpath = "first_name/val")
	private String firstName;
	
	@ServiceElement(xpath = "middle_name/val")
	private String middleName;
	
	@ServiceElement(xpath = "last_name/val")
	private String lastName;

	@ServiceElement(xpath = "full_name/val")
	private String fullName;
	

	@ServiceElement(xpath = "primary_email/val")
	private String primaryEmail;
	
	@ServiceElement(xpath = "primary_mobile/val")
	private String primaryMobile;
	
	@ServiceElement(xpath = "domi_suburb/val")
	private String domiSuburb;
	
	@ServiceElement(xpath = "domi_state/val")
	private String domiState;
	
	@ServiceElement(xpath = "open_date/val")
	private String openDate;

    @ServiceElementList(xpath = "profile_list/profile",type= ProfileUserRoleImpl.class)
    private List<ProfileUserRole> profileUserRoles;

    @ServiceElement(xpath = "first_avsr_person_oe_id/val")
    private String adviserPersonId;


	public String getGcmId()
	{
		return gcmId;
	}

	public void setGcmId(String gcmId)
	{
		this.gcmId = gcmId;
	}

	public boolean isBenef()
	{
		return isBenef;
	}

	public void setBenef(boolean isBenef)
	{
		this.isBenef = isBenef;
	}

	public boolean isMember() {
		return member;
	}

	public void setMember(boolean member) {
		this.member = member;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getMiddleName()
	{
		return middleName;
	}

	public void setMiddleName(String middleName)
	{
		this.middleName = middleName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getFullName()
	{
		return fullName;
	}

	public void setFullName(String fullName)
	{
		this.fullName = fullName;
	}

	public String getPrimaryEmail()
	{
		return primaryEmail;
	}

	public void setPrimaryEmail(String primaryEmail)
	{
		this.primaryEmail = primaryEmail;
	}

	public String getPrimaryMobile()
	{
		return primaryMobile;
	}

	public void setPrimaryMobile(String primaryMobile)
	{
		this.primaryMobile = primaryMobile;
	}

	public String getDomiSuburb()
	{
		return domiSuburb;
	}

	public void setDomiSuburb(String domiSuburb)
	{
		this.domiSuburb = domiSuburb;
	}

	public String getDomiState()
	{
		return domiState;
	}

	public void setDomiState(String domiState)
	{
		this.domiState = domiState;
	}

	public String getOpenDate()
	{
		return openDate;
	}

    public void setOpenDate(String openDate)
	{
		this.openDate = openDate;
	}

    @Override
    public String getAdviserPersonId() {
        return adviserPersonId;
    }

    public void setAdviserPersonId(String adviserPersonId) {
        this.adviserPersonId = adviserPersonId;
    }

    @Override
	public ClientKey getClientKey()
	{
		return ClientKey.valueOf(personId);
	}

	@Override
	public void setClientKey(ClientKey clientKey)
	{
		this.personId = clientKey.getId();
	}

    @Override
    public List<ProfileUserRole> getProfileUserRoles() {
        return profileUserRoles;
    }
    @Override
    public void setProfileUserRoles(List<ProfileUserRole> profileUserRoles) {
        this.profileUserRoles = profileUserRoles;
    }

}
