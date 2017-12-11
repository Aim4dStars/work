package com.bt.nextgen.service.avaloq;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.integration.search.PersonSearchRequest;
import com.btfin.abs.srchservice.v1_0.SrchReq;
import com.btfin.panorama.core.security.avaloq.AvaloqBankingAuthorityService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.bt.nextgen.service.avaloq.AvaloqUtils.makeUserSearchRequest;
import static com.bt.nextgen.service.request.AvaloqRequestUtil.clearRequestId;
import static com.bt.nextgen.service.request.AvaloqRequestUtil.recordRequestId;

@Service
public class AvaloqExecuteImpl implements AvaloqExecute {
    private static final Logger logger = LoggerFactory.getLogger(AvaloqExecuteImpl.class);

    private static final Marker REQUEST_TRACKING_MARKER = MarkerFactory.getMarker("REQTRAK");

    @Resource(name = "userDetailsService")
    private AvaloqBankingAuthorityService avaloqBankingAuthorityService;

    @Autowired
    private AvaloqGatewayHelperService webserviceClient;


    private StringBuffer extractRequestIdAndtemplate(AvaloqReportRequest request) {
        StringBuffer reqId = new StringBuffer().append("reqId: ")
                .append(request.getRequestObject() != null && request.getRequestObject().getHdr() != null
                        ? request.getRequestObject().getHdr().getReqId()
                        : "null")
                .append("; templ: ")
                .append(request.getRequestObject() != null && request.getRequestObject().getTask() != null
                        ? request.getRequestObject().getTask().getTempl()
                        : "null");
        return reqId;
    }

    private StringBuffer extractRequestIdAndtemplate(SrchReq request) {
        StringBuffer reqId = new StringBuffer().append("reqId: ")
                .append(request != null && request.getHdr() != null
                        ? request.getHdr().getReqId()
                        : "null")
                .append("; SearchType: ")
                .append(request != null && request.getData() != null
                        ? request.getData().getUserRole().getVal()
                        : "null");
        return reqId;
    }


    public <T> T executeReportRequest(AvaloqReportRequest request) {
        if (avaloqBankingAuthorityService != null && avaloqBankingAuthorityService.getEmulatedJobProfile() != null)
            request.emulatingUser(avaloqBankingAuthorityService.getEmulatedJobProfile().getProfileId());

        // Reusable string identifier for logging purposes
        StringBuffer reqId = extractRequestIdAndtemplate(request);

        logger.info(REQUEST_TRACKING_MARKER, "Avaloq Service START; {}", reqId);

        // This is just for logging if the service call is successful or not
        Exception exception = null;

        try {
            String requestId = getAvaloqRequestId(request);
            recordRequestId(requestId);

            if (request.isApplicationLevelRequest())
                return webserviceClient.sendSystemRequestToWebService(request.getRequestObject(), request.getOperation());
            else
                return webserviceClient.sendToWebService(request.getRequestObject(), request.getOperation());
        } catch (Exception e) {
            exception = e;
            logger.info(REQUEST_TRACKING_MARKER, "Avaloq Service END; status: FAILED; {}; exception: {} {}", reqId, e.getClass(), e.getMessage());
            throw e;
        } finally {
            clearRequestId();
            if (exception == null) {
                logger.info(REQUEST_TRACKING_MARKER, "Avaloq Service END; status: SUCCESSFUL; {}", reqId);
            }
        }
    }

    @Override
    public <T> T executeSearchOperationRequest(PersonSearchRequest request, Class<T> responseType, ServiceErrors serviceErrors) {
        try {
            final AvaloqOperation reportReq = AvaloqOperation.SRCH_REQ;
            SrchReq searchRq = makeUserSearchRequest(request.getSearchToken(),
                    request.getRoleType(),
                    request.getPersonTypeId());
            logger.info(REQUEST_TRACKING_MARKER, "Invoking ABS operation {}", reportReq);
            String reqId = searchRq.getHdr().getReqId();
            recordRequestId(reqId);
            return webserviceClient.sendToWebService(searchRq,
                    reportReq,
                    responseType,
                    serviceErrors);
        } finally {
            clearRequestId();
        }
    }

    public <T> T executeReportRequestToDomain(AvaloqReportRequest request, Class<T> responseType, ServiceErrors serviceErrors) {

        if (avaloqBankingAuthorityService != null && avaloqBankingAuthorityService.getEmulatedJobProfile() != null)
            request.emulatingUser(avaloqBankingAuthorityService.getEmulatedJobProfile().getProfileId());
        StringBuffer reqId = extractRequestIdAndtemplate(request);

        logger.info(REQUEST_TRACKING_MARKER, "Avaloq Service START; {}", reqId);
        // This is just for logging if the service call is successful or not
        Exception exception = null;
        try {
            String requestId = getAvaloqRequestId(request);
            recordRequestId(requestId);

            if (request.isApplicationLevelRequest()) {
                return webserviceClient.sendSystemRequestToWebService(request.getRequestObject(),
                        request.getOperation(),
                        responseType,
                        serviceErrors);
            } else {
                return webserviceClient.sendToWebService(request.getRequestObject(),
                        request.getOperation(),
                        responseType,
                        serviceErrors);
            }
        } catch (Exception e) {
            exception = e;
            logger.info(REQUEST_TRACKING_MARKER, "Avaloq Service END; status: FAILED; {}; exception: {} {}", reqId, e.getClass(), e.getMessage());
            throw e;
        } finally {
            clearRequestId();
            if (exception == null) {
                logger.info(REQUEST_TRACKING_MARKER, "Avaloq Service END; status: SUCCESSFUL; {}", reqId);
            }
        }

    }

    public static String getAvaloqRequestId(AvaloqReportRequest request) {
        String hdrReqId = request.getRequestObject() != null && request.getRequestObject().getHdr() != null
                ? request.getRequestObject().getHdr().getReqId() : "null";
        return hdrReqId;
    }
}
