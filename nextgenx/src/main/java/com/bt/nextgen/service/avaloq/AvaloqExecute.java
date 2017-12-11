package com.bt.nextgen.service.avaloq;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.integration.search.PersonSearchRequest;

public interface AvaloqExecute
{

        public <T> T executeReportRequest(AvaloqReportRequest request);
        
        public <T> T executeSearchOperationRequest(PersonSearchRequest request, Class <T> responseType, ServiceErrors serviceErrors);
        
        public <T> T executeReportRequestToDomain(AvaloqReportRequest request, Class <T> responseType, ServiceErrors serviceErrors);

}
