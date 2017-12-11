package com.bt.nextgen.core.security.api.model;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import com.bt.nextgen.core.api.model.BaseDto;

/**
 * DTO used by the permissions logic in the UI to enable/disable/hide 
 * components based on the presence/absence of named permissions in this Object.
 * @author M013938
 */
public class PermissionListDto extends BaseDto {

	private final Map<String, Object> permissionList = new TreeMap<>();

	public PermissionListDto(Object... nvPairs) {
		if ((nvPairs.length & 1) == 1) {
			throw new IllegalArgumentException(
					"Name/value pairs must have even size");
		}
		for (int i = 0; i < nvPairs.length; i += 2) {
			permissionList.put(nvPairs[i].toString(), nvPairs[i + 1]);
		}
	}

	/**
	 * Add role assignment flags to the list. Role IDs are transformed
	 * from upper-case with underscores to lowercase with tildes, as this
	 * appears to be the preferred format for the UI.
	 * e.g. {@code ROLE_ADVISER ==> role~adviser}
	 * @param roles the role assignment flags to be added.
	 */
	public void addRoles(Collection<RoleDto> roles) {
		for (RoleDto role : roles) {
			String key = role.getKey().getRoleId();
			key = key.toLowerCase().replaceFirst("_", "~");
			permissionList.put(key, role.isAssigned());
		}
	}

	/**
	 * Bean getter for the permission list.
	 * @return permission list.
	 */
	public Map<String, Object> getPermissionList() {
		return permissionList;
	}
}
