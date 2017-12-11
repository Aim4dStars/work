package com.bt.nextgen.serviceops.model;

import com.bt.nextgen.service.group.customer.groupesb.RoleType;


/**
 * Created by L091297 on 08/06/2017.
 */
public class SiloMovementReqModel {
	private String key;
	private String fromSilo;
	private String toSilo;
	private RoleType personType;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getFromSilo() {
		return fromSilo;
	}
	public void setFromSilo(String fromSilo) {
		this.fromSilo = fromSilo;
	}
	public String getToSilo() {
		return toSilo;
	}
	public void setToSilo(String toSilo) {
		this.toSilo = toSilo;
	}
	public RoleType getPersonType() {
		return personType;
	}
	public void setPersonType(RoleType personType) {
		this.personType = personType;
	}

	
}
