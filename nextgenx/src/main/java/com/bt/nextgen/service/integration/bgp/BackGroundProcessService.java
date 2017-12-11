package com.bt.nextgen.service.integration.bgp;

import org.joda.time.DateTime;

import java.util.List;

public interface BackGroundProcessService {

    List<BackGroundProcess> getBackGroundProcesses();

    DateTime getCurrentTime();

}
