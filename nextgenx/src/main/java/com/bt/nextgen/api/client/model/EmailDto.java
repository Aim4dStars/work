package com.bt.nextgen.api.client.model;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.PriorityLevel;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.integration.domain.AddressMedium;


public class EmailDto extends BaseDto
{
	private String email;
	private String modificationSeq;
	private boolean preferred;
	private String emailType;
	private AddressKey emailKey;
	private boolean gcmMastered;
	private String emailActionCode;
	private PriorityLevel emailPriority;
	private String oldAddress;

	private AddressMedium addressMedium;

	public AddressKey getEmailKey()
	{
		return emailKey;
	}

	public void setEmailKey(AddressKey addressKey)
	{
		this.emailKey = addressKey;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getModificationSeq()
	{
		return modificationSeq;
	}

	public void setModificationSeq(String modificationSeq)
	{
		this.modificationSeq = modificationSeq;
	}

	public boolean isPreferred()
	{
		return preferred;
	}

	public void setPreferred(boolean preferred)
	{
		this.preferred = preferred;
	}

	public String getEmailType()
	{
		return emailType;
	}

	public void setEmailType(String emailType)
	{
		this.emailType = emailType;
	}

	public boolean getGcmMastered() {
		return gcmMastered;
	}

	public void setGcmMastered(boolean gcmMastered) {
		this.gcmMastered = gcmMastered;
	}

	public String getEmailActionCode() {
		return emailActionCode;
	}

	public void setEmailActionCode(String emailActionCode) {
		this.emailActionCode = emailActionCode;
	}

	public PriorityLevel getEmailPriority() {
		return emailPriority;
	}

	public void setEmailPriority(PriorityLevel emailPriority) {
		this.emailPriority = emailPriority;
	}

	public AddressMedium getAddressMedium() {
		return addressMedium;
	}

	public void setAddressMedium(AddressMedium addressMedium) {
		this.addressMedium = addressMedium;
	}

	public String getOldAddress() {
		return oldAddress;
	}

	public void setOldAddress(String oldAddress) {
		this.oldAddress = oldAddress;
	}
}
