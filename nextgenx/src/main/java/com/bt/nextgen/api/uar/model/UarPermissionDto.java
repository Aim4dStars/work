package com.bt.nextgen.api.uar.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.util.List;

/**
 * Created by l069679 on 7/07/2016.
 */
public class UarPermissionDto extends BaseDto {

    private String roleName;
    private List<String> permissionValues;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<String> getPermissionValues() {
        return permissionValues;
    }

    public void setPermissionValues(List<String> permissionValues) {
        this.permissionValues = permissionValues;
    }
}
