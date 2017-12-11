package com.bt.nextgen.service.avaloq.userinformation;

import com.btfin.panorama.core.security.integration.userinformation.JobPermission;

import java.util.Collections;
import java.util.List;

public class JobPermissionImpl implements JobPermission {
	private List<JobRole> primaryRole;
	private List<FunctionalRole> functionalRoles;
	private List<Role> roles;

	@Override
	public List<String> getUserRoles() {
		return Collections.emptyList();
	}

	/**
	 * This method returns the Primary Role(s) of the logged in user.
	 * These are evaluated on the basis of primary role condition(s).
	 */
	public List<JobRole> getPrimaryRole() {
		return primaryRole;
	}

	public void setPrimaryRole(List<JobRole> primaryRole) {
		this.primaryRole = primaryRole;
	}

	/**
	 * This method returns the Functional Role(s) of the logged in user.
	 * These are evaluated on the basis of functional role condition(s).
	 */
	@Override
	public List<FunctionalRole> getFunctionalRoles() {
		return functionalRoles;
	}

	@Override
	public void setFunctionalRoles(List<FunctionalRole> functionalRoles) {
		this.functionalRoles = functionalRoles;
	}

	/**
	 * This method returns the Role(s) of the logged in user irrespective of the categorisation.
	 */
	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
}
