package com.bt.nextgen.service.avaloq.transaction;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;

@ServiceBean(xpath = "loc_list")
public class ValidationLocation {

    @ServiceElement(xpath = "loc")
    private String loc;

    public ValidationLocation() {
        super();
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String location) {
        this.loc = location;
    }

}
