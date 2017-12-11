package com.bt.nextgen.service.avaloq.matchtfn;

import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.btfin.abs.trxservice.datavalid.v1_0.DataValidReq;
import com.btfin.abs.trxservice.datavalid.v1_0.DataValidRsp;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.bt.nextgen.service.avaloq.AvaloqUtils.makeDataValidationRequest;

/**
 * Created by L070354 on 13/07/2017.
 */

@Service
public class AvaloqMatchTFNIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements MatchTFNIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(AvaloqMatchTFNIntegrationServiceImpl.class);

    @Autowired
    private AvaloqGatewayHelperService webserviceClient;

    /**
     * Matches the TFN value against Avaloq(BTFG$COM.BTFIN.TRXSVC_DATA_VALID_V1 Template is used)
     *
     * @param personId  personId of existing customers(optional)
     * @param tfn       tfn to be associated with the customer(Mandatory)
     * @serviceErrors   error object
     *
     * @return boolean  indicates whether there has been a successful match or not
     */
    @Override
    public boolean doMatchTFN(final String personId, final String tfn, final ServiceErrors serviceErrors) {

        //TaxFileNumber is the mandatory input parameter.
        if(StringUtils.isBlank(tfn)){
            logger.error("Invalid Tax File Number parameter");
            serviceErrors.addError(new ServiceErrorImpl("Invalid Tax File Number parameter provided"));
            return false;
        }

        logger.debug("Entered TFNMatch Method");
        return new IntegrationSingleOperation <Boolean>("matchTFN", serviceErrors) {
            @Override
            public Boolean performOperation() {
                logger.info("Matching TFN for {}", personId);
                DataValidReq dataValidReq = makeDataValidationRequest(tfn, personId);
                DataValidRsp dataValidRsp = webserviceClient.sendToWebService(dataValidReq, AvaloqOperation.DATA_VALID_REQ, serviceErrors);
                if(serviceErrors.hasErrors()){
                    logger.error("Error in Match TFN Service");
                    return false;
                }
                logger.info("Matching TFN Service Response for {}",dataValidRsp.getData().getPersonTfnExist().isVal());
                return dataValidRsp.getData().getPersonTfnExist().isVal();
            }
        }.run();
    }
}
