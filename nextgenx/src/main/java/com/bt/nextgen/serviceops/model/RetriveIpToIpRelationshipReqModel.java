/**
 * 
 */
package com.bt.nextgen.serviceops.model;

/**
 * @author L081050
 * 
 */
public class RetriveIpToIpRelationshipReqModel {
	private String silo;
	private String cisKey;
	private String roleType;

	public String getSilo() {
		return silo;
	}

	public void setSilo(String silo) {
		this.silo = silo;
	}

	public String getCisKey() {
		return cisKey;
	}

	public void setCisKey(String cisKey) {
		this.cisKey = cisKey;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

}
