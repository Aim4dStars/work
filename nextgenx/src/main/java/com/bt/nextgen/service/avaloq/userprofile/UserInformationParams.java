package com.bt.nextgen.service.avaloq.userprofile;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqType;

public enum UserInformationParams implements AvaloqParameter {

    PARAM_AUTH_KEY("auth_key", AvaloqType.PARAM_AUTH_KEY),
	PARAM_PERSON_ID("person_id", AvaloqType.PARAM_ID_FIELD),
	PARAM_BANK_REF_GCM_ID("person_id", AvaloqType.PARAM_USER_ID);

	private String param;
	private AvaloqType type;

	private UserInformationParams(String param, AvaloqType type)
	{
		this.param = param;
		this.type = type;
	}

	public String getName()
	{
		return param;
	}
	
	@Override
	public String getParamName() {
		return param;
	}

	@Override
	public AvaloqType getParamType() {
		return type;
	}

}
