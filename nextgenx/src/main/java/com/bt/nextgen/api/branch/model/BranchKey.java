package com.bt.nextgen.api.branch.model;

public class BranchKey {
    private String bsb;

    public BranchKey(String bsb) {
        this.bsb = bsb;
    }

    public String getBsb() {
        return bsb;
    }

    public void setBsb(String bsb) {
        this.bsb = bsb;
    }
}
