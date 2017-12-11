package com.bt.nextgen.service.avaloq.transactionfee;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class CacheManagedAvaloqTransactionFeeIntegrationServiceTest extends BaseSecureIntegrationTest {

    @Test
    @SecureTestContext
    public void testLoadAssets_whenNullsPassed_thenReturnsOnlyFeesWithNullSearchedForAttributes() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<AvaloqTransactionFee> transactionFees = transactionFeeService.loadDirectTransactionFees(null, null, null,
                serviceErrors);
        testAssertions(serviceErrors, transactionFees, 2, null, null, null);
    }

    @Test
    @SecureTestContext
    public void testLoadAssets_whenValuesThatWontBeFoundPassed_thenReturnsFeesWithNullSearchedForAttributes() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<AvaloqTransactionFee> transactionFees = transactionFeeService.loadDirectTransactionFees("can't", "find", "me",
                serviceErrors);

        testAssertions(serviceErrors, transactionFees, 2, null, null, null);

    }

    @Test
    @SecureTestContext
    public void testLoadAssets_whenValuesPassed_thenReturnsFeesWithNullOrMatchedSearchedForAttributes() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<AvaloqTransactionFee> transactionFees = transactionFeeService.loadDirectTransactionFees("152663", null, null,
                serviceErrors);
        testAssertions(serviceErrors, transactionFees, 2, "152663", null, null);

        transactionFees = transactionFeeService.loadDirectTransactionFees("152663", null, "101780", serviceErrors);
        testAssertions(serviceErrors, transactionFees, 2, "152663", null, "101780");

        transactionFees = transactionFeeService.loadDirectTransactionFees("152663", "124472", "101780", serviceErrors);
        testAssertions(serviceErrors, transactionFees, 2, "152663", "124472", "101780");
    }

    private void testAssertions(ServiceErrors serviceErrors, List<AvaloqTransactionFee> transactionFees, int expectedSize,
            String expectedProductId, String expectedDealerGroupId, String expectedAdviserId) {
        Assert.assertFalse(serviceErrors.hasErrors());
        assertThat(transactionFees, hasSize(expectedSize));
        for (AvaloqTransactionFee fee : transactionFees) {
            assertThat(fee.getProductId(), fee.getProductId() == null || fee.getProductId().equals(expectedProductId));
            assertThat(fee.getDealerGroupId(),
                    fee.getDealerGroupId() == null || fee.getDealerGroupId().equals(expectedDealerGroupId));
            assertThat(fee.getAdviserId(), fee.getAdviserId() == null || fee.getAdviserId().equals(expectedAdviserId));
        }

    }

}
