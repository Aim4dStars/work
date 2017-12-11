package com.bt.nextgen.service.avaloq.bgp;

import com.bt.nextgen.core.exception.ServiceException;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.integration.bgp.BackGroundProcess;
import com.bt.nextgen.service.integration.bgp.BackGroundProcessIntegrationService;
import com.bt.nextgen.service.integration.bgp.BackGroundProcessService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@EnableScheduling
@Service
public class BackGroundProcessIntegrationServiceImpl implements BackGroundProcessIntegrationService {

    private static Logger logger = LoggerFactory.getLogger(BackGroundProcessIntegrationServiceImpl.class);

    @Autowired
    private AvaloqExecute avaloqExecute;

    private AvaloqReportRequest reportRequest;

    @Value("#{'${bgp.essential.instances}'.split(',')}")
    private Set<String> bgpEssentialInstances;

    @Scheduled(cron = "${bgp.check.schedule}")
    public void checkBGPsOn() {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();

        try {
            List<BackGroundProcess> bgps = getBackGroundProcesses(serviceErrors);

            logger.info("Running BGP check");
            for (BackGroundProcess bgp : bgps) {
                logger.info("SplunkInfo: BGP Instance Info: |" + bgp.getBGPName() + "|" + bgp.getBGPId() + "|" + bgp.getBGPInstance() + "|" + bgp.getSID() + "|" + bgp.isBGPValid() + "|" + bgp.getCurrentTime() + "|");
                String bgpInstance = bgp.getBGPInstance();
                if (!bgp.isBGPValid() && bgpEssentialInstances.contains(bgpInstance)) {
                    logger.error("SplunkException: Failed BGP Instance " + bgp.getBGPName() + " " + bgp.getBGPId() + " " + bgpInstance);
                }
            }
        } catch (ServiceException e) {
            logger.error("SplunkException: Failed to retrieve BGP status", e);
        }
    }

    @Override
    public List<BackGroundProcess> getBackGroundProcesses(ServiceErrors serviceErrors) {
        reportRequest = new AvaloqReportRequest(Template.BACKGROUND_PROCESS.getName()).asApplicationUser();

        BackGroundProcessService response = avaloqExecute.executeReportRequestToDomain(reportRequest,
                BackGroundProcessServiceImpl.class, serviceErrors);

        return response.getBackGroundProcesses();
    }

    @Override
    public DateTime getCurrentTime(ServiceErrors serviceErrors) {
        reportRequest = new AvaloqReportRequest(Template.BACKGROUND_PROCESS.getName()).asApplicationUser();

        BackGroundProcessService response = avaloqExecute.executeReportRequestToDomain(reportRequest,
                BackGroundProcessServiceImpl.class, serviceErrors);

        return response.getCurrentTime();
    }

}
