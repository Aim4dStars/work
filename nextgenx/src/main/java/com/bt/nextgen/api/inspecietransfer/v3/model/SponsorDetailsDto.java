package com.bt.nextgen.api.inspecietransfer.v3.model;

import com.bt.nextgen.config.JsonViews;
import com.fasterxml.jackson.annotation.JsonView;

public class SponsorDetailsDto {

    @JsonView(JsonViews.Write.class)
    private String pid;

    @JsonView(JsonViews.Write.class)
    private String pidName;

    @JsonView(JsonViews.Write.class)
    private String custodian;

    @JsonView(JsonViews.Write.class)
    private String hin;

    @JsonView(JsonViews.Write.class)
    private String srn;

    @JsonView(JsonViews.Write.class)
    private String accNumber;

    @JsonView(JsonViews.Write.class)
    private String sourceContainerId;

    public SponsorDetailsDto() {
        super();
    }

    public SponsorDetailsDto(String pid, String pidName, String hin) {
        this.pid = pid;
        this.pidName = pidName;
        this.hin = hin;
    }

    public SponsorDetailsDto(String srn) {
        this.srn = srn;
    }

    public SponsorDetailsDto(String custodian, String accNumber) {
        this.custodian = custodian;
        this.accNumber = accNumber;
    }

    public SponsorDetailsDto(String pid, String pidName, String hin, String custodian) {
        this.pid = pid;
        this.pidName = pidName;
        this.hin = hin;
        this.custodian = custodian;
    }

    public String getPid() {
        return pid;
    }

    public String getPidName() {
        return pidName;
    }

    public String getCustodian() {
        return custodian;
    }

    public String getHin() {
        return hin;
    }

    public String getSrn() {
        return srn;
    }

    public String getSourceContainerId() {
        return sourceContainerId;
    }

    public String getAccNumber() {
        return accNumber;
    }

    public String getKey() {
        StringBuilder builder = new StringBuilder();
        final String con = ":";

        if (pid != null) {
            builder.append("PID:").append(pid).append(con);
        }

        if (custodian != null) {
            builder.append("CUSTODIAN:").append(custodian).append(con);
        }

        if (hin != null)
            builder.append("HIN:").append(hin).append(con);
        if (srn != null)
            builder.append("SRN:").append(srn).append(con);
        if (accNumber != null)
            builder.append("ACCNUMBER:").append(accNumber).append(con);

        return builder.toString();
    }
}
