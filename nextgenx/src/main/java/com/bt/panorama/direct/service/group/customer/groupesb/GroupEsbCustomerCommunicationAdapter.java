package com.bt.panorama.direct.service.group.customer.groupesb;

import au.com.westpac.gn.communicationmanagement.services.communicationdispatch.xsd.generatecommunicationdetails.v1.svc0019.GenerateCommunicationDetailsResponse;
import au.com.westpac.gn.utility.xsd.statushandling.v1.Level;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;


public class GroupEsbCustomerCommunicationAdapter {

    private GenerateCommunicationDetailsResponse generateCommunicationDetailsResponse;

    public GroupEsbCustomerCommunicationAdapter(GenerateCommunicationDetailsResponse response, ServiceErrors serviceErrors){
        if (response == null)
            serviceErrors.addError(new ServiceErrorImpl("The GenerateCommunicationDetailsResponse Response was null"));

        this.generateCommunicationDetailsResponse = response;
    }

    public String getServiceLevel(){
        return generateCommunicationDetailsResponse.getServiceStatus().getStatusInfo().get(0).getLevel().toString();
    }


    public String getServiceStatusErrorCode(){
        return generateCommunicationDetailsResponse.getServiceStatus()
                .getStatusInfo()
                .get(0)
                .getStatusDetail()
                .get(0)
                .getProviderErrorDetail()
                .get(0)
                .getProviderErrorCode();
    }

    public String getServiceStatusErrorDesc(){
        // TODO
        return null;
    }

    public String getServiceStatus(){
        ServiceStatus status=  generateCommunicationDetailsResponse.getServiceStatus();
        if (null != status && null != status.getStatusInfo() && !status.getStatusInfo().isEmpty()){
            Level level = status.getStatusInfo().get(0).getLevel();
            switch (level)
            {
                case ERROR:
                    return status.getStatusInfo().get(0).
                            getStatusDetail().get(0).getProviderErrorDetail().get(0).
                            getProviderErrorCode();
                default:
                    break;
            }
        }
        return "SUCCESS";
    }
}
