package com.bt.nextgen.service.avaloq.userinformation;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.btfin.panorama.core.security.integration.userinformation.UserInformation;
import org.joda.time.DateTime;

import java.util.List;

/**
 * This implementation acts as the mapping of USER_DET service to the UserInformation service.
 */
@ServiceBean(xpath = "/")
public class UserInformationImpl extends AvaloqBaseResponseImpl implements UserInformation {
	//private final Logger logger = LoggerFactory.getLogger(UserInformationImpl.class);

	@ServiceElementList(xpath = "//data/report/report_foot_list/report_foot/role/val", type = String.class)
	private List<String> userRoles;

	@ServiceElement(xpath = "//data/report/report_foot_list/report_foot/nat_person_id/val")
	private String personId;

	@ServiceElement(xpath = "//data/report/report_foot_list/report_foot/person_id/val")
	private String jobId;

	@ServiceElement(xpath = "//data/report/report_foot_list/report_foot/user_id/val")
	private String profileId;

	@ServiceElement(xpath = "//data/report/report_foot_list/report_foot/nat_person_first_name/val")
	private String firstName;

	@ServiceElement(xpath = "//data/report/report_foot_list/report_foot/nat_person_last_name/val")
	private String lastName;

	@ServiceElement(xpath = "//data/report/report_foot_list/report_foot/nat_person_full_name/val")
	private String fullName;

	@ServiceElement(xpath = "//data/report/report_foot_list/report_foot/nat_person_safi_device/val")
	private String safiDeviceId;

	private DateTime dateTime;

	private List<JobRole> avaloqRoles;

	private JobKey jobKey;

	private UserKey userKey;

	private List<FunctionalRole> functionalRoles;

	private String customerId;

	@Override
	//TODO fix the personId object to be a key (using a converter)
	public ClientKey getClientKey() {
		return ClientKey.valueOf(personId);
	}

	@Override
	public void setClientKey(ClientKey personId) {
		this.personId = personId.getId();
	}

	public JobKey getJob() {
		if (jobKey == null) {
			this.jobKey = JobKey.valueOf(jobId);
		}

		return jobKey;
	}

	@Override
	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	@Override
	public List<FunctionalRole> getFunctionalRoles() {
		return functionalRoles;
	}

	@Override
	public void setFunctionalRoles(List<FunctionalRole> functionalRoles) {
		this.functionalRoles = functionalRoles;
	}

	@Override
	public List<String> getUserRoles() {
		return userRoles;
	}

	public void setRoles(List<String> userRoles) {
		this.userRoles = userRoles;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public UserKey getCustomerKey() {
		if (customerId == null) {
			return null;
		}

		if (this.userKey == null) {
			this.userKey = UserKey.valueOf(customerId);
		}

		return this.userKey;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public void setJobKey(JobKey jobKey) {
		this.jobKey = jobKey;
	}

	@Override
	public String getFullName() {
		return fullName;
	}

	@Override
	public String getFirstName() {
		return firstName;
	}

	@Override
	public String getLastName() {
		return lastName;
	}

	@Override
	public String getSafiDeviceId() {
		return safiDeviceId;
	}
}