package com.bt.nextgen.service.avaloq.beneficiary;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.beneficiary.builder.BeneficiariesDetailsConverter;
import com.bt.nextgen.service.AvaloqTransactionService;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.AvaloqUtils;
import com.bt.nextgen.service.avaloq.TransactionStatusImpl;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.request.AvaloqOperation;
import com.btfin.abs.trxservice.bp.v1_0.BpReq;
import com.btfin.abs.trxservice.bp.v1_0.BpRsp;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static com.bt.nextgen.service.avaloq.Template.SUPER_VIEW_BENEFICIARIES;

/**
 * {@inheritDoc}
 * Created by M035995 on 9/07/2016.
 */
@Service("BeneficiaryDetailsIntegrationServiceImpl")
public class BeneficiaryDetailsIntegrationServiceImpl extends AbstractAvaloqIntegrationService
        implements BeneficiaryDetailsIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(BeneficiaryDetailsIntegrationServiceImpl.class);

    /**
     * Executor for avaloq requests.
     */
    @Autowired
    private AvaloqExecute avaloqExecute;

    @Autowired
    private AvaloqTransactionService avaloqTransactionService;

    @Override
    public BeneficiaryDetailsResponseHolderImpl getBeneficiaryDetails(AccountKey accountKey, ServiceErrors serviceErrors) {
        if (null == accountKey || StringUtils.isBlank(accountKey.getAccountId())) {
            throw new IllegalArgumentException("AccountKey/AccountId must not be null");
        }
        List<String> accountIds = Arrays.asList(accountKey.getAccountId());
        return getBeneficiaryDetails(accountIds, serviceErrors);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BeneficiaryDetailsResponseHolderImpl getBeneficiaryDetails(List<String> accountIds, ServiceErrors serviceErrors) {

        if (!CollectionUtils.isNotEmpty(accountIds)) {
            throw new IllegalArgumentException("Account ids must be specified");
        }

        final AvaloqReportRequest avaloqReportRequest = new AvaloqReportRequest(SUPER_VIEW_BENEFICIARIES.getName()).
                forAccountList(accountIds);

        return avaloqExecute.executeReportRequestToDomain(avaloqReportRequest,
                BeneficiaryDetailsResponseHolderImpl.class, serviceErrors);
    }

    @Override
    public BeneficiaryDetailsResponseHolderImpl getBeneficiaryDetails(BrokerKey brokerKey, ServiceErrors serviceErrors) {
        logger.info("BeneficiaryDetailsIntegrationServiceImpl::getBeneficiaryDetails: method invoked");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        final AvaloqReportRequest avaloqReportRequest = new AvaloqReportRequest(SUPER_VIEW_BENEFICIARIES.getName()).
                searchingFor(BeneficiaryRequestBuilder.getBeneficiaryDetailsCriteriaRequest(brokerKey));
        BeneficiaryDetailsResponseHolderImpl response = avaloqExecute.executeReportRequestToDomain(avaloqReportRequest, BeneficiaryDetailsResponseHolderImpl.class, serviceErrors);

        stopWatch.stop();
        logger.info("BeneficiaryDetailsIntegrationServiceImpl::getBeneficiaryDetails: complete time taken = {} ms", stopWatch.getTime());
        return response;
    }

    @Override
    public TransactionStatus saveOrUpdate(final SaveBeneficiariesDetails benefDetails) {
        return new AbstractAvaloqIntegrationService.IntegrationSingleOperation<TransactionStatus>("saveOrUpdate", new ServiceErrorsImpl()) {
            @Override
            public TransactionStatus performOperation() {
                TransactionStatus status = new TransactionStatusImpl();
                BeneficiariesDetailsConverter converter = new BeneficiariesDetailsConverter();
                final BpReq bpReq = converter.makeBeneficiariesRequest(benefDetails);
                final BpRsp bpRes = avaloqTransactionService.executeTransactionRequest(bpReq, AvaloqOperation.BP_REQ, new ServiceErrorsImpl());

                //validate if the update is successful
                if (AvaloqUtils.validateAvaloqResponseForBPUpdate(bpRes)) {
                    boolean updatedflag = true;
                    status.setSuccessful(updatedflag);

                }
                return status;
            }
        }.run();
    }
}
