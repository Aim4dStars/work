package com.bt.nextgen.service.prm.service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.prm.pojo.PrmDto;

import java.util.concurrent.Future;

/**
 * Created by l081361 on 7/09/2016.
 */
public interface PrmGESBConnectService {
    void submitRequestPrimary(PrmDto prmDto, ServiceErrors serviceErrors);
    Future<Void> submitRequest(PrmDto prmDto);
}
