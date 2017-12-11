package com.bt.nextgen.service.integration.search;

import com.bt.nextgen.service.integration.userinformation.ClientIdentifier;

import java.util.List;

public interface PersonResponse extends ClientIdentifier
{
	String getFirstName();

	void setFirstName(String firstName);

	String getMiddleName();

	void setMiddleName(String middleName);

	String getLastName();

	void setLastName(String lastName);

	String getFullName();

	void setFullName(String fullName);

	String getPrimaryEmail();

	void setPrimaryEmail(String primaryEmail);

	String getPrimaryMobile();

	void setPrimaryMobile(String primaryMobile);

	String getDomiSuburb();

	void setDomiSuburb(String domiSuburb);

	String getDomiState();

	void setDomiState(String domiState);

	String getOpenDate();

	void setOpenDate(String openDate);

    String getGcmId();

	void setGcmId(String gcmId);

	boolean isBenef();

	void setBenef(boolean isBenef);

	boolean isMember();

	void setMember(boolean member);

    String getAdviserPersonId();

    List<ProfileUserRole> getProfileUserRoles();

    void setProfileUserRoles(List<ProfileUserRole> profileUserRoles);

}
