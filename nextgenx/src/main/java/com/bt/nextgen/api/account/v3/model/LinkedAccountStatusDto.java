package com.bt.nextgen.api.account.v3.model;

import com.bt.nextgen.service.integration.payeedetails.LinkedAccountStatus;

/**
 * Created by L078480 on 27/07/2017.
 */
public class LinkedAccountStatusDto {
    private LinkedAccountStatus linkedAccountStatus;
    private boolean genCode;
    private boolean vfyCode;
    private boolean directDebit;
    private boolean gracePeriod;


    public LinkedAccountStatus getLinkedAccountStatus() {
        return linkedAccountStatus;
    }

    public void setLinkedAccountStatus(LinkedAccountStatus linkedAccountStatus) {
        this.linkedAccountStatus = linkedAccountStatus;
    }

    public void setGenCode(boolean generateCode) {
        genCode = generateCode;
    }

    public void setVfyCode(boolean verifyCode) {
        vfyCode = verifyCode;
    }

    public boolean isGenCode() {
        return genCode;
    }

    public boolean isVfyCode() {
        return vfyCode;
    }

    public boolean isDirectDebit() {
        return directDebit;
    }

    public void setDirectDebit(boolean directDebit) {
        this.directDebit = directDebit;
    }

    public boolean isGracePeriod() {
        return gracePeriod;
    }

    public void setGracePeriod(boolean gracePeriod) {
        this.gracePeriod = gracePeriod;
    }
}
