package com.bt.nextgen.service.avaloq.bgp;


import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.bgp.BackGroundProcess;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

@ServiceBean(xpath = "dm", type = ServiceBeanType.CONCRETE)
public class BackGroundProcessImpl implements BackGroundProcess {
    private static final String XML_HEADER = "dm_head_list/dm_head/";


    @NotNull
    @ServiceElement(xpath = XML_HEADER + "bgp_instn/val")
    private String bgpInstance;

    @NotNull
    @ServiceElement(xpath = XML_HEADER + "bgp_id/val")
    private String bgpId;

    @NotNull
    @ServiceElement(xpath = XML_HEADER + "bgp_name/val")
    private String bgpName;

    @NotNull
    @ServiceElement(xpath = XML_HEADER + "is_valid/val")
    private boolean isValid;

    @ServiceElement(xpath = XML_HEADER + "sid/val")
    private String sid;

    @ServiceElement(xpath = XML_HEADER + "this_time/val", converter = DateTimeTypeConverter.class)
    private DateTime thisTime;

    @Override
    public String getBGPInstance() {
        return bgpInstance;
    }

    @Override
    public String getBGPId() {
        return bgpId;
    }

    @Override
    public String getBGPName() {
        return bgpName;
    }

    @Override
    public boolean isBGPValid() {
        return isValid;
    }

    @Override
    public String getSID() {
        return sid;
    }

    @Override
    public DateTime getCurrentTime() {
        return thisTime;
    }
}
