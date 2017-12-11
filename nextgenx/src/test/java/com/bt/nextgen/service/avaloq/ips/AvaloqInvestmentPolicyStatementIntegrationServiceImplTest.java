package com.bt.nextgen.service.avaloq.ips;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementInterface;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by F058391 on 2/06/2017.
 */
public class AvaloqInvestmentPolicyStatementIntegrationServiceImplTest extends BaseSecureIntegrationTest {

    @Autowired
    InvestmentPolicyStatementIntegrationService integrationService;

    @Test
    public void testLoadInvestmentPolicyStatement_whenValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<InvestmentPolicyStatementInterface> ipsList = integrationService.loadInvestmentPolicyStatement(serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertNotNull(ipsList);
        for (InvestmentPolicyStatementInterface ips : ipsList) {
            Assert.assertNotNull(ips);
        }
    }
}