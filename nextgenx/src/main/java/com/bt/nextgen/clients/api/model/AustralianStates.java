package com.bt.nextgen.clients.api.model;

public enum AustralianStates
{
	Australian_Capital_Territory("Australian Capital Territory", "ACT"),

	NEW_SOUTH_WALES("NEW SOUTH WALES", "NSW"),

	VICTORIA("VICTORIA", "VIC"),

	Western_Australia("Western Australia", "WA"),

	Northern_Territory("Northern Territory", "NT"),

	South_Australia("South Australia", "SA"),

	Tasmania("Tasmania", "TAS"),

	Queensland("Queensland", "QLD");

	private AustralianStates(String state, String code)
	{
		this.state = state;
		this.code = code;
	}

	private String code;
	private String state;

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public String getState()
	{
		return state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public static AustralianStates getStateEnumFromCode(String code)
	{
		AustralianStates[] states = AustralianStates.values();
		for (AustralianStates state : states)
		{
			if (state.getCode().equalsIgnoreCase(code))
				return state;
		}
		return null;
	}

	public static AustralianStates getStateEnumFromFullName(String name)
	{
		AustralianStates[] states = AustralianStates.values();
		for (AustralianStates state : states)
		{
			if (state.getState().equalsIgnoreCase(name))
				return state;
		}
		return null;
	}

	public static boolean isAustralianStateFromCode(String code)
	{
		AustralianStates[] states = AustralianStates.values();
		for (AustralianStates state : states)
		{
			if (state.getCode().equalsIgnoreCase(Australian_Capital_Territory.getCode()))
				continue;
			if (state.getCode().equalsIgnoreCase(code))
				return true;
		}

		return false;
	}

	public static boolean isAustralianStateFullName(String stateName)
	{
		AustralianStates[] states = AustralianStates.values();
		for (AustralianStates state : states)
		{
			if (state.getState().equalsIgnoreCase(Australian_Capital_Territory.getState()))
				continue;
			if (state.getState().equalsIgnoreCase(stateName))
				return true;
		}
		return false;
	}

    public static boolean isAustralianStateFullListFromCode(String code)
    {
        AustralianStates[] states = AustralianStates.values();
        for (AustralianStates state : states)
        {
            if (state.getCode().equalsIgnoreCase(code))
                return true;
        }

        return false;
    }

    public static boolean isAustralianStateFullListFullName(String stateName)
    {
        AustralianStates[] states = AustralianStates.values();
        for (AustralianStates state : states)
        {
            if (state.getState().equalsIgnoreCase(stateName))
                return true;
        }
        return false;
    }

    public static AustralianStates getAustralianStateByNameOrCode(String stateNameOrCode) {
        if(isAustralianStateFullListFromCode(stateNameOrCode)) {
            return getStateEnumFromCode(stateNameOrCode);
        } else if(isAustralianStateFullListFullName(stateNameOrCode)) {
            return getStateEnumFromFullName(stateNameOrCode);
        }
        return null;
    }
}
