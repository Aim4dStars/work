package com.bt.nextgen.service.avaloq.pension;

import com.bt.nextgen.api.pension.builder.PensionCommencementConverter;
import com.bt.nextgen.api.pension.model.PensionTrxnDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.ErrorConverter;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.btfin.abs.trxservice.ausapens.v1_0.AuSaPensReq;
import com.btfin.abs.trxservice.ausapens.v1_0.AuSaPensRsp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static com.bt.nextgen.service.avaloq.Template.SUPER_PENSION_COMMENCEMENT_STATUS;

/**
 * Implementation for pension commencement related services.
 */
@Service("pensionCommencementIntegrationServiceImpl")
public class PensionCommencementIntegrationServiceImpl extends AbstractAvaloqIntegrationService
        implements PensionCommencementIntegrationService {
    /**
     * Executor for avaloq requests.
     */
    @Autowired
    private AvaloqExecute avaloqExecute;

    @Autowired
    private AvaloqGatewayHelperService webServiceClient;

    @Autowired
    protected ErrorConverter errorConverter;

    @Override
    public PensionTrxnDto commencePension(final String accountNumber) {
        return new AbstractAvaloqIntegrationService.IntegrationSingleOperation<PensionTrxnDto>("commencePension",
                new ServiceErrorsImpl()) {
            @Override
            public PensionTrxnDto performOperation() {
                final PensionCommencementConverter converter = new PensionCommencementConverter();
                final AuSaPensReq pensionRequest = converter.makePensionCommencementRequest(accountNumber);
                final AuSaPensRsp pensionResponse = webServiceClient.sendToWebService(pensionRequest,
                        AvaloqOperation.AU_SA_PENS_REQ, new ServiceErrorsImpl());

                return converter.toPensionDetailsResponseDto(pensionResponse, errorConverter);
            }
        }.run();
    }


    @Override
    public boolean isPensionCommencementPending(final String accountNumber, final ServiceErrors serviceErrors) {
        if (accountNumber == null) {
            throw new IllegalArgumentException("Account number must be specified");
        }

        return new IntegrationSingleOperation<Boolean>("isPensionCommencementPending", serviceErrors) {
            @Override
            public Boolean performOperation() {
                return isPensionCommencementStatusPending(accountNumber, serviceErrors);
            }
        }.run();
    }

    private Boolean isPensionCommencementStatusPending(String accountNumber, ServiceErrors serviceErrors) {
        final AvaloqReportRequest request = new AvaloqReportRequest(SUPER_PENSION_COMMENCEMENT_STATUS.getName())
                .forBpNrList(Arrays.asList(accountNumber));
        final PensionCommencementStatus commencementStatus = avaloqExecute.executeReportRequestToDomain(request,
                PensionCommencementStatusImpl.class, serviceErrors);

        // only need to check for existence of an attribute in response to find out if commencement is in progress
        return commencementStatus != null && commencementStatus.getDocId() != null;
    }
}
