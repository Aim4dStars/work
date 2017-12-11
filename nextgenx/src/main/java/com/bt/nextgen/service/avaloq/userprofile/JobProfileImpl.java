package com.bt.nextgen.service.avaloq.userprofile;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import org.joda.time.DateTime;

@SuppressWarnings("serial")
@ServiceBean(xpath="user")
public class JobProfileImpl implements JobProfile, java.io.Serializable
{
	private JobKey jobId;
	
	@ServiceElement(xpath="user_head_list/user_head/person_job/*/*/id")
	private String personJobId;
	
	@ServiceElement(xpath="user_head_list/user_head/sec_user_id/val")
	private String profileId;
	
	@ServiceElement(xpath="user_head_list/user_head/job_type/val", staticCodeCategory="JOB_TYPE")
	private JobRole jobRole;

	@ServiceElement(xpath = "user_head_list/user_head/close_date/val", converter = DateTimeTypeConverter.class)
	private DateTime closeDate;
	
    @ServiceElement(xpath = "user_head_list/user_head/job_user_exper/val", staticCodeCategory = "USER_EXPERIENCE")
    private UserExperience userExperience;

	@Override
    public JobRole getJobRole()
	{
		return jobRole;
	}

	public void setJobRole(JobRole jobRole)
	{
		this.jobRole = jobRole;
	}
	
	public void setJob(JobKey jobId)
	{
		this.jobId = jobId;
	}

	@Override
    public String getPersonJobId()
	{
		return personJobId;
	}

	public void setPersonJobId(String personJobId)
	{
		this.personJobId = personJobId;
	}

	public void setProfileId(String profileId)
	{
		this.profileId = profileId;
	}

	@Override
    public JobKey getJob()
	{
		if (jobId == null)
		{
			return JobKey.valueOf(personJobId);
		}
		return this.jobId;
	}

	@Override
    public String getProfileId()
	{
		return this.profileId;
	}

	public void setCloseDate(DateTime closeDate) {
		this.closeDate = closeDate;
	}

	@Override
	public DateTime getCloseDate() {
		return this.closeDate;
	}

    @Override
    public UserExperience getUserExperience() {
        return userExperience;
    }

    public void setUserExperience(UserExperience userExperience) {
        this.userExperience = userExperience;
    }
}
