package com.bt.nextgen.service.avaloq.bgp;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.bgp.BackGroundProcess;
import com.bt.nextgen.service.integration.bgp.BackGroundProcessService;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import org.joda.time.DateTime;

import java.util.List;

@ServiceBean(xpath = "/", type = ServiceBeanType.CONCRETE)
public class BackGroundProcessServiceImpl extends AvaloqBaseResponseImpl implements BackGroundProcessService {

    @ServiceElement(xpath = "//metadata/current_of_time/val", converter = DateTimeTypeConverter.class)
    private DateTime currentTime;

    @ServiceElementList(xpath = "//data/dm_list/dm", type = BackGroundProcessImpl.class)
    private List<BackGroundProcess> backGroundProcess;

    @Override
    public List<BackGroundProcess> getBackGroundProcesses() {
        return backGroundProcess;
    }

    @Override
    public DateTime getCurrentTime() {
        return currentTime;
    }
}
