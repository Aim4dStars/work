package com.bt.nextgen.api.account.v1.model.inspecietransfer;

/**
 * @deprecated Use V2
 */
@Deprecated
public class SponsorDetailsDtoImpl {

    protected String accNumber;
    protected String custodian;
    protected String hin;
    protected String pid;
    protected String srn;
    protected String pidName;

    public String getAccNumber() {
        return accNumber;
    }

    public void setAccNumber(String accNumber) {
        this.accNumber = accNumber;
    }

    public String getCustodian() {
        return custodian;
    }

    public void setCustodian(String custodian) {
        this.custodian = custodian;
    }

    public String getHin() {
        return hin;
    }

    public void setHin(String hin) {
        this.hin = hin;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getSrn() {
        return srn;
    }

    public void setSrn(String srn) {
        this.srn = srn;
    }

    public String getPidName() {
        return pidName;
    }

    public void setPidName(String pidName) {
        this.pidName = pidName;
    }
}
