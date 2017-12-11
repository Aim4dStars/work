package com.bt.nextgen.api.logon.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

/**
 * Created by L070589 on 11/11/2014.
 */
public class LogonUpdateUserNameDto extends BaseDto implements KeyedDto<LogonDtoKey> {

    private LogonDtoKey key;
    private String userName;
    private String newUserName;
     private boolean updateFlag;

    @Override
    public LogonDtoKey getKey() {
        return key;
    }

    public void setKey(LogonDtoKey key) {
        this.key = key;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNewUserName() {
        return newUserName;
    }

    public void setNewUserName(String newUserName) {
        this.newUserName = newUserName;
    }

    public boolean isUpdateFlag() {
        return updateFlag;
    }

    public void setUpdateFlag(boolean updateFlag) {
        this.updateFlag = updateFlag;
    }


}



