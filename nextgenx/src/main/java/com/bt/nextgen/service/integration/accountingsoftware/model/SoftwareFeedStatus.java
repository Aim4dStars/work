package com.bt.nextgen.service.integration.accountingsoftware.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Representation of the Avaloq static code category <code>btfg$class_extl_hold_status</code><br>
 *
 * Also contains equivalent nextgen display value - which are fixed values independent of the abs internal id
 * which may change.
 */
public enum SoftwareFeedStatus {

	// First value is avaloq internal id
	// Second value is the nextgen display value
    AWAITING("aw", "awaiting"),
    RECEIVED("rcvd", "received"),
    MANUAL("manual", "manual"),
    STOP("stop", "stop"),
	REQUESTED("reqd", "requested"),
    UNKNOWN("unknown", "unknown");

    SoftwareFeedStatus(String value, String displayValue)
	{
        this.value = value;
		this.displayValue = displayValue;
    }
    private String value;

	// UI Display value
	private String displayValue;

	private static final Map<String, SoftwareFeedStatus> lookup = new HashMap<>();

	static
	{
		for (SoftwareFeedStatus status : SoftwareFeedStatus.values())
		{
			lookup.put(status.getValue(), status);
		}
	}



    public String getValue() {
        return value;
    }

	public String getDisplayValue() {
		return displayValue;
	}

	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}

	public static SoftwareFeedStatus fromValue(String value)
    {
        for (SoftwareFeedStatus status : SoftwareFeedStatus.values())
        {
			if (status.getDisplayValue().equalsIgnoreCase(value))
			{
				return status;
			}
        }
       return UNKNOWN;
    }

	public static SoftwareFeedStatus getDisplayValueFor(String status)
	{
		return lookup.get(status) != null ? lookup.get(status) : SoftwareFeedStatus.UNKNOWN;
	}

}
