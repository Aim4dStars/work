package com.bt.nextgen.service.avaloq.userprofile;

/**
 * This class is responsible for returning the available profiles for the logged in user.
 */
import java.util.List;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.userprofile.AvailableProfiles;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;

@ServiceBean(xpath="/")
public class AvailableProfilesImpl extends AvaloqBaseResponseImpl implements AvailableProfiles
{
	@ServiceElementList(xpath="//data/user_list/user", type=JobProfileImpl.class)
	private List<JobProfile> profiles;

	public List <JobProfile> getJobProfiles()
	{
		return profiles;
	}
	
}
