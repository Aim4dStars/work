package com.bt.nextgen.api.uar.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.integration.user.UserKey;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by l069679 on 7/07/2016.
 */
public class UarDetailsDto extends BaseDto implements KeyedDto<UserKey> {
    private UserKey key;
    private String uarDate;
    private List<UarDto> uarComponent;
    private List<UarPermissionDto> userPermissions;
    private BigDecimal docId;
    private String submitStatus;

    public BigDecimal getDocId() {
        return docId;
    }

    public void setDocId(BigDecimal docId) {
        this.docId = docId;
    }

    public String getSubmitStatus() {
        return submitStatus;
    }

    public void setSubmitStatus(String submitStatus) {
        this.submitStatus = submitStatus;
    }

    @Override
    public UserKey getKey() {
        return key;
    }

    public void setKey(UserKey key) {
        this.key = key;
    }

    public List<UarDto> getUarComponent() {
        return uarComponent;
    }

    public void setUarComponent(List<UarDto> uarComponent) {
        this.uarComponent = uarComponent;
    }

    public List<UarPermissionDto> getUserPermissions() {
        return userPermissions;
    }

    public void setUserPermissions(List<UarPermissionDto> userPermissions) {
        this.userPermissions = userPermissions;
    }

    public String getUarDate() {
        return uarDate;
    }

    public void setUarDate(String uarDate) {
        this.uarDate = uarDate;
    }
}
