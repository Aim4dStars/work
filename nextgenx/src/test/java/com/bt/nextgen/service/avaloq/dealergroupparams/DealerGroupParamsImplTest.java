package com.bt.nextgen.service.avaloq.dealergroupparams;

import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class DealerGroupParamsImplTest {

    @InjectMocks
    private DefaultResponseExtractor<DealerGroupParamsResponseImpl> defaultResponseExtractor;

    @Test
    public void directContainerShouldContainBTCashAsset() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource(
                "/webservices/response/DealerDefaultParametersResponse.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(DealerGroupParamsResponseImpl.class);
        DealerGroupParamsResponseImpl response = defaultResponseExtractor.extractData(content);

        assertNotNull(response);
        assertNotNull(response.getCustomerAccountObjects());

        List<DealerGroupParams> paramList = response.getCustomerAccountObjects();
        Assert.assertTrue(paramList.size() == 1);

        DealerGroupParamsImpl param = (DealerGroupParamsImpl) paramList.get(0);
        Assert.assertEquals("552999", param.getId());
        Assert.assertEquals(BigDecimal.valueOf(10000), param.getMinimumInitialInvestmentAmount());
        Assert.assertEquals(BigDecimal.valueOf(2.5), param.getMinimumCashAllocationPercentage());
        Assert.assertEquals(BigDecimal.valueOf(0.2), param.getMinimumTradePercentageScan());
        Assert.assertEquals(BigDecimal.valueOf(20), param.getMinimumTradeAmountScan());
        Assert.assertEquals(BigDecimal.valueOf(0.2), param.getMinimumTradePercentage());
        Assert.assertEquals(BigDecimal.valueOf(20), param.getMinimumTradeAmount());
        Assert.assertEquals(BigDecimal.valueOf(0.5), param.getToleranceAbsolutePercentage());
        Assert.assertEquals(BigDecimal.valueOf(10), param.getToleranceRelativePercentage());
        Assert.assertEquals(BigDecimal.valueOf(5), param.getToleranceThersholdPercentage());
        Assert.assertEquals(BigDecimal.valueOf(0), param.getMinimumWithdrawAmount());
        Assert.assertEquals(BigDecimal.valueOf(0), param.getMinimumContributionAmount());
        Assert.assertEquals(BigDecimal.valueOf(90), param.getMaximumPartRedemptionPercentage());
        Assert.assertEquals(BigDecimal.valueOf(500), param.getPPMinimumTradeAmount());
        Assert.assertEquals(BigDecimal.valueOf(2), param.getPPDefaultAssetTolerance());
        Assert.assertEquals(BigDecimal.valueOf(1000), param.getPPMinimumInvestmentAmount());
        Assert.assertEquals(false, param.getIsSuperProduct());
        Assert.assertEquals(false, param.getIsTmpProduct());
    }

    @Test
    public void testGetCustomerAccountingObjects_whenNull_thenEmptyListReturned() {
        DealerGroupParamsResponse response = Mockito.mock(DealerGroupParamsResponse.class);
        Assert.assertTrue(response.getCustomerAccountObjects().isEmpty());
    }

}
