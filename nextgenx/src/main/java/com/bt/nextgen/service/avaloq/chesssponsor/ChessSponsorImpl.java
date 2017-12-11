package com.bt.nextgen.service.avaloq.chesssponsor;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.chessparameter.ChessSponsor;

/**
 * Created by l078480 on 21/06/2017.
 */
@ServiceBean(xpath = "bp", type = ServiceBeanType.CONCRETE)
public class ChessSponsorImpl implements ChessSponsor {

    @ServiceElement(xpath = "bp_head_list/bp_head/bp/val")
    private String sponsorName;

    @ServiceElement(xpath = "bp_head_list/bp_head/bp_clr_key/val")
    private String sponsorPid;

    @Override
    public String getSponsorName() {
        return sponsorName;
    }

    @Override
    public String getSponsorPid() {
        return sponsorPid;
    }


    @Override
    public void setSponsorName(String sponsorName) {
        this.sponsorName = sponsorName;
    }

    @Override
    public void setSponsorPid(String sponsorPid) {
        this.sponsorPid = sponsorPid;
    }


}
