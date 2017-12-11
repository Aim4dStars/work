package com.bt.nextgen.api.logon.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

/**
 * Created by L070589 on 10/11/2014.
 */
public class LogonUpdatePasswordDto extends BaseDto implements KeyedDto<LogonDtoKey> {

    private LogonDtoKey key;
    private String newPassword;
    private String currentPassword;
    private String halgm;



    private boolean updateFlag;

    public LogonUpdatePasswordDto(LogonDtoKey key, String newPassword, String currentPassword, String halgm) {
        this.key = key;
        this.newPassword = newPassword;
        this.currentPassword = currentPassword;
        this.halgm = halgm;
    }
    public LogonUpdatePasswordDto() {

    }




    public String getNewPassword() {
        return newPassword;
    }



    public String getCurrentPassword() {
        return currentPassword;
    }


    public String getHalgm() {
        return halgm;
    }



    @Override
    public LogonDtoKey getKey() {
        return key;
    }

    public boolean isUpdateFlag() {
        return updateFlag;
    }

    public void setUpdateFlag(boolean updateFlag) {
        this.updateFlag = updateFlag;
    }
}
