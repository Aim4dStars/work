package com.bt.nextgen.service.integration.additionalservices;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import com.bt.nextgen.service.integration.accountactivation.ApplicationIdentifier;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;

import java.util.List;

public interface AdditionalServicesIntegrationService {

    ApplicationDocument subscribe(ServiceErrors serviceErrors, ApplicationDocument applicationDocument);

    List<ApplicationDocument> loadApplications(ServiceErrors serviceErrors, WrapAccountIdentifier... wrapAccountList);

    List<ApplicationDocument> loadApplications(ServiceErrors serviceErrors, List<ApplicationIdentifier> applicationIdentifiers);
}