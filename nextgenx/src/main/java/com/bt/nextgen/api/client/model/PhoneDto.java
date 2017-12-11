package com.bt.nextgen.api.client.model;

import com.bt.nextgen.core.api.model.BaseDto;


public class PhoneDto extends BaseDto
{
	private boolean preferred;
	private String number;
	private String countryCode;
	private String areaCode;
	private String modificationSeq;
	private String phoneType;
	private AddressKey phoneKey;
    private String requestedAction;
    private boolean gcmPhone;
	private String fullPhoneNumber;
	private String phoneCategory;

	public AddressKey getPhoneKey()
	{
		return phoneKey;
	}

	public void setPhoneKey(AddressKey addressKey)
	{
		this.phoneKey = addressKey;
	}

	public boolean isPreferred()
	{
		return preferred;
	}

	public void setPreferred(boolean preferred)
	{
		this.preferred = preferred;
	}

	public String getNumber()
	{
		return number;
	}

	public void setNumber(String number)
	{
		this.number = number;
	}

	public String getPhoneType()
	{
		return phoneType;
	}

	public void setPhoneType(String phoneType)
	{
		this.phoneType = phoneType;
	}

	public String getCountryCode()
	{
		return countryCode;
	}

	public void setCountryCode(String countryCode)
	{
		this.countryCode = countryCode;
	}

	public String getAreaCode()
	{
		return areaCode;
	}

	public void setAreaCode(String areaCode)
	{
		this.areaCode = areaCode;
	}

	public String getModificationSeq()
	{
		return modificationSeq;
	}

	public void setModificationSeq(String modificationSeq)
	{
		this.modificationSeq = modificationSeq;
	}

    public String getRequestedAction() {
        return requestedAction;
    }

    public void setRequestedAction(String requestedAction) {
        this.requestedAction = requestedAction;
    }


    public boolean isGcmPhone() {
        return gcmPhone;
    }

    public void setGcmPhone(boolean gcmPhone) {
        this.gcmPhone = gcmPhone;
    }


	public String getFullPhoneNumber() {
		return fullPhoneNumber;
	}

	public void setFullPhoneNumber(String fullPhoneNumber) {
		this.fullPhoneNumber = fullPhoneNumber;
	}

	public String getPhoneCategory() {
		return phoneCategory;
	}

	public void setPhoneCategory(String phoneCategory) {
		this.phoneCategory = phoneCategory;
	}
}
