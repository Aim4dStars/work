package com.bt.nextgen.service.gesb.arrangementreporting.v2;

import au.com.westpac.gn.arrangementreporting.services.arrangementreporting.xsd.retrieveiptaxregistration.v2.svc0610.RetrieveDemandDepositArrangementDetailsResponse;
import au.com.westpac.gn.arrangementreporting.services.arrangementreporting.xsd.retrieveiptaxregistration.v2.svc0610.RetrieveIPTaxRegistrationRequest;
import au.com.westpac.gn.common.xsd.identifiers.v1.IdentificationScheme;
import au.com.westpac.gn.common.xsd.identifiers.v1.InvolvedPartyIdentifier;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.config.WebServiceProviderConfig;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.bt.nextgen.core.service.ErrorHandlerUtil;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.ServiceConstants;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.soap.client.SoapFaultClientException;

import javax.annotation.Resource;

/**
 * Created by M040398 (Florin.Adochiei@btfinancialgroup.com) on 19/05/2017.
 */
@Service
public class RetrieveTFNServiceImpl implements RetrieveTFNService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetrieveTFNServiceImpl.class);

    @Autowired
    private WebServiceProvider provider;

    @Resource(name = "userDetailsService")
    private BankingAuthorityService userSamlService;

    @Autowired
    private CmsService cmsService;

    @Override
    public String getTFN(String cisKey, ServiceErrors serviceErrors) {
        RetrieveIPTaxRegistrationRequest requestPayload = new RetrieveIPTaxRegistrationRequest();
        InvolvedPartyIdentifier party = new InvolvedPartyIdentifier();
        party.setIdentificationScheme(IdentificationScheme.CIS_KEY);
        party.setInvolvedPartyId(cisKey);
        requestPayload.setInvolvedPartyIdentifier(party);

        CorrelatedResponse correlatedResponse;
        try {
            LOGGER.info("Calling web service to retrieve TFN details");
            correlatedResponse = provider.sendWebServiceWithSecurityHeaderAndResponseCallback(userSamlService.getSamlToken(),
                    WebServiceProviderConfig.GROUP_ESB_RETRIEVE_TFN.getConfigName(), requestPayload, serviceErrors);
        } catch (SoapFaultClientException sfe) {
            LOGGER.error("SoapFaultException while retrieving TFN details",sfe);
            return null;
        }

        RetrieveDemandDepositArrangementDetailsResponse response = (RetrieveDemandDepositArrangementDetailsResponse) correlatedResponse.getResponseObject();
        String status = response.getServiceStatus().getStatusInfo().get(0).getLevel().toString();

        if (!status.equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
            LOGGER.error("There was an error retrieving a TFN from svc0610");
            return null;
        }
        LOGGER.info("Successfully retrieved TFN from svc0610");
        return
                response.getRegistrationIdentifier().getRegistrationNumber();
    }
}
