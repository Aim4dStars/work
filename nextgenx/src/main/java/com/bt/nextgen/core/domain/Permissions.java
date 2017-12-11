package com.bt.nextgen.core.domain;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

@XmlRootElement
public class Permissions 
{

	private List<Permission> permissionsList;

	private List<PermissionMapObj> permissionsMapList;
    
	public List<Permission> getPermissionsList() {
		return permissionsList;
	}
	public void setPermissionsList(List<Permission> permissionsList) {
		this.permissionsList = permissionsList;
	}

	public List<PermissionMapObj> getPermissionsMapList()
	{
		return permissionsMapList;
	}

	public void setPermissionsMapList(List<PermissionMapObj> permissionsMap)
	{
		this.permissionsMapList = permissionsMap;
	}

	public static class PermissionMapObj
	{
		@XmlAnyAttribute
		public Map<QName,String> meta;
		private Hashtable<String, String> permissionsMap;

		public Hashtable<String, String> getPermissionsMap() {
			return permissionsMap;
		}

		public void setPermissionsMap(Hashtable<String, String> permissionsMap) {
			this.permissionsMap = permissionsMap;
		}
		public String getMetaData(String name)
		{
			if(meta != null)
			{
				return meta.get(new QName(name));
			}
			else
			{
				return null;
			}
		}
	}
}
