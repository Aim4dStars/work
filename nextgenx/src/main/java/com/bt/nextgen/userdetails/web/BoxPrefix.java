package com.bt.nextgen.userdetails.web;

/**
 * Enumeration contains name, code_id and UI value.
 */
public enum BoxPrefix 
{
	CARE_PO("Care Of Post Office","2","C/O"), 
	CMA("Community Mail Agent","3","CMA"),
	CMB("Community Mail Bag","4","CMB"),
	CPA("Community Postal Agent","12","CPA"),
	GPO_BOX("General Post Office Box","1","GPO Box"), 
	LOCKED_BAG("Locked Bag","5","Locked Bag"),
	MS("Mail Service","6","MS"),
	PO_BOX("Post Office Box","7","PO Box"),
	PRIVATE_BAG("Private Bag","8","Private Bag"),
	RSD("Roadside Delivery","11","RSD"),
	RMB("Roadside Mail Bag/Box","9","RMB"),
	RMS("Roadside Mail Service","10","RMS");

	private String name;
	private String code;
	private String displayName;
	
	private BoxPrefix(final String name, final String code, final String displayName)
    {
        this.name = name;
        this.code = code;
        this.displayName = displayName;
    }

	public String getName()
	{
		return this.name;
	}

	public String getCode()
	{
		return this.code;
	}
	
	public String getDisplayName()
	{
		return this.displayName;
	}
}
