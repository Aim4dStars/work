package com.bt.nextgen.service.integration.bgp;

import org.joda.time.DateTime;

public interface BackGroundProcess {

    String getBGPInstance();

    String getBGPId();

    String getBGPName();

    boolean isBGPValid();

    String getSID();

    DateTime getCurrentTime();
}
