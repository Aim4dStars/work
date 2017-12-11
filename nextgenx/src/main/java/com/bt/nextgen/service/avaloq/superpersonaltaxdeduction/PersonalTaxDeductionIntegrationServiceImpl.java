package com.bt.nextgen.service.avaloq.superpersonaltaxdeduction;

import com.bt.nextgen.api.superpersonaltaxdeduction.model.PersonalTaxDeductionNoticeTrxnDto;
import com.bt.nextgen.api.superpersonaltaxdeduction.util.PersonalTaxDeductionNoticesConverter;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.btfin.abs.trxservice.ausa.v1_0.AuSaReq;
import com.btfin.abs.trxservice.ausa.v1_0.AuSaRsp;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static com.bt.nextgen.reports.service.ReportGenerationServiceImpl.PARAM_RELV_DATE_FROM;
import static com.bt.nextgen.reports.service.ReportGenerationServiceImpl.PARAM_RELV_DATE_TO;
import static com.bt.nextgen.service.avaloq.Template.SUPER_PERSONAL_TAX_DEDUCTION_NOTICE;

/**
 * Created by L067218 on 10/10/2016.
 */
@Service
public class PersonalTaxDeductionIntegrationServiceImpl extends AbstractAvaloqIntegrationService
        implements PersonalTaxDeductionIntegrationService {
    /**
     * Executor for avaloq requests.
     */
    @Autowired
    private AvaloqExecute avaloqExecute;

    @Autowired
    private AvaloqGatewayHelperService webServiceClient;


    @Override
    public PersonalTaxDeduction getPersonalTaxDeductionNotices(final String accountNumber, final String docId, final DateTime financialYearStartDate,
                                                               final DateTime financialYearEndDate, final ServiceErrors serviceErrors) {
        if (accountNumber == null || financialYearStartDate == null || financialYearEndDate == null) {
            throw new IllegalArgumentException("Account key, start and end dates of financial year must be specified");
        }

        return new IntegrationSingleOperation<PersonalTaxDeduction>("getPersonalTaxDeductionNotices", serviceErrors) {
            @Override
            public PersonalTaxDeduction performOperation() {
                final AvaloqReportRequest avaloqReportRequest = new AvaloqReportRequest(SUPER_PERSONAL_TAX_DEDUCTION_NOTICE.getName())
                        .forBpNrList(Collections.singletonList(accountNumber))
                        .forDateTime(PARAM_RELV_DATE_FROM, financialYearStartDate)
                        .forDateTime(PARAM_RELV_DATE_TO, financialYearEndDate);

                if (docId != null) {
                    avaloqReportRequest.forDocumentIdList(Arrays.asList(docId));
                }

                return avaloqExecute.executeReportRequestToDomain(avaloqReportRequest, PersonalTaxDeductionImpl.class, serviceErrors);
            }
        }.run();
    }

    @Override
    public PersonalTaxDeductionNoticeTrxnDto createTaxDeductionNotice(final String accountNumber,
                                                                      final DateTime financialYearStartDate,
                                                                      final DateTime financialYearEndDate,
                                                                      final BigDecimal claimAmount) {
        return createOrVaryTaxDeductionNotice(accountNumber, null, financialYearStartDate, financialYearEndDate, claimAmount);
    }

    @Override
    public PersonalTaxDeductionNoticeTrxnDto varyTaxDeductionNotice(String accountNumber, String originalDocId,
                                                                    DateTime financialYearStartDate,
                                                                    DateTime financialYearEndDate, BigDecimal claimAmount) {
        return createOrVaryTaxDeductionNotice(accountNumber, originalDocId, financialYearStartDate, financialYearEndDate,
                    claimAmount);
    }


    public PersonalTaxDeductionNoticeTrxnDto createOrVaryTaxDeductionNotice(final String accountNumber,
                                                                            final String originalDocId,
                                                                            final DateTime financialYearStartDate,
                                                                            final DateTime financialYearEndDate,
                                                                            final BigDecimal claimAmount) {
        return new AbstractAvaloqIntegrationService.IntegrationSingleOperation<PersonalTaxDeductionNoticeTrxnDto>(
                    "createOrVaryTaxDeductionNotice", new ServiceErrorsImpl()) {
            @Override
            public PersonalTaxDeductionNoticeTrxnDto performOperation() {
                final PersonalTaxDeductionNoticesConverter converter = new PersonalTaxDeductionNoticesConverter();
                final AuSaReq taxNoticeRequest = converter.makePersonalTaxDeductionRequest(accountNumber,
                        originalDocId, financialYearStartDate.toDate(), financialYearEndDate.toDate(), claimAmount);
                final AuSaRsp taxNoticeResponse = webServiceClient.sendToWebService(taxNoticeRequest,
                        AvaloqOperation.AU_SA_REQ, new ServiceErrorsImpl());

                return converter.toPersonalTaxResponseDto(taxNoticeResponse);
            }
        }.run();
    }
}
