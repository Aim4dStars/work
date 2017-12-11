package com.bt.nextgen.api.chesssponsor.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by L078480 on 22/06/2017.
 */
public class ChessSponsorDataDto {
    @JsonProperty("sponsorname")
    private String sponsorName;

    public String getSponsorName() {
        return sponsorName;
    }

    public void setSponsorName(String sponsorName) {
        this.sponsorName = sponsorName;
    }

    public String getSponsorPid() {
        return sponsorPid;
    }

    public void setSponsorPid(String sponsorPid) {
        this.sponsorPid = sponsorPid;
    }
    @JsonProperty("sponsorpid")
    private String sponsorPid;

}
