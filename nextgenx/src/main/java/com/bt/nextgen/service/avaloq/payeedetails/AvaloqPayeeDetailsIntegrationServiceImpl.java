package com.bt.nextgen.service.avaloq.payeedetails;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetails;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetailsIntegrationService;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@SuppressWarnings("squid:S1068") // please don't remove this. Needed to guarantee caching aspect
public class AvaloqPayeeDetailsIntegrationServiceImpl implements PayeeDetailsIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(AvaloqPayeeDetailsIntegrationServiceImpl.class);

    // TODO - XXX - This is to enforce that caching happens before autowiring in this instance. Improvement Required
    @Autowired
    private BeanFactoryCacheOperationSourceAdvisor waitForCachingAspect;

    @Autowired
    private AvaloqExecute avaloqExecute;

    /**
     * This service returns the Payee Details, MoneyAccount details, PayeeLimit details and PayeeAuthority list for the portfolio
     * It is the implementation of BTFG$UI_BP.BP#PAY_DET avaloq service.
     * 
     * @param WrapAccountIdentifier,
     *            ServiceErrors
     * @return payeeDetails
     */
    @Cacheable(key = "#identifier.getAccountIdentifier()", value = "com.bt.nextgen.service.avaloq.payeedetails.loadPayeeDetails")
    @Override
    public PayeeDetails loadPayeeDetails(WrapAccountIdentifier identifier, ServiceErrors serviceErrors) {
        try {
            List<String> accountId = new ArrayList<>();
            accountId.add(identifier.getAccountIdentifier());

            PayeeDetails payeeDetails = avaloqExecute.executeReportRequestToDomain(
                    new AvaloqReportRequest(Template.PAYEE_DETAILS.getName()).forAccountList(accountId), PayeeDetailsImpl.class,
                    serviceErrors);

            return PayeeDetailsConverter.setMoneyAccountDetailsAndSequence(payeeDetails);
        } catch (Exception e) {
            logger.error("Error loading payee details for account {} ", identifier.getAccountIdentifier(), e);
            return new PayeeDetailsImpl();
        }
    }

    @CacheEvict(key = "#identifier.getAccountIdentifier()", value = "com.bt.nextgen.service.avaloq.payeedetails.loadPayeeDetails")
    @Override
    public void clearCache(WrapAccountIdentifier identifier) {
        logger.info("LoadPayeeDetails cache has been cleared");
    }
}
