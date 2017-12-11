package com.bt.nextgen.api.product.v1.service;

import com.bt.nextgen.api.fees.v1.model.LicenseAdviserFeeDto;
import com.bt.nextgen.api.fees.v1.service.LicenseAdviserFeeService;
import com.bt.nextgen.api.product.v1.model.ProductFeeComponentDto;
import com.bt.nextgen.api.product.v1.model.ProductFeeDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.integration.broker.BrokerType;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductFeeServiceTest {

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private ProductFeeComponentService productFeeComponentService;

    @Mock
    private LicenseAdviserFeeService licenseAdviserFeeService;

    @Mock
    private ServiceErrors serviceErrors;

    @InjectMocks
    private ProductFeeServiceImpl productFeeService;

    @Mock
    private Broker adviser;

    @Mock
    private Broker dealerGroup;

    @Mock
    private Product myProduct;

    @Mock
    private ProductFeeComponentDto productFeeComponentDto;

    private BrokerKey dealerKey;
    private String adviserId;
    private String productId;
    private LicenseAdviserFeeDto licenseAdviserFeeDto;

    @Before
    public void setUp() throws Exception {
        adviserId = "MY_ADVISER_ID";
        productId = "MY_PRODUCT_ID";
        dealerKey = BrokerKey.valueOf("MY_DEALER_KEY");
        final BrokerImpl dealerGroup = new BrokerImpl(BrokerKey.valueOf("66773"), BrokerType.ADVISER);
        dealerGroup.setParentKey(BrokerKey.valueOf("66773"));
        dealerGroup.setDealerKey(BrokerKey.valueOf("66778"));
        dealerGroup.setLicenseeFeeActive(true);
        licenseAdviserFeeDto = new LicenseAdviserFeeDto();
        when(adviser.getDealerKey()).thenReturn(dealerKey);
        when(brokerIntegrationService.getBroker(BrokerKey.valueOf(adviserId), serviceErrors)).thenReturn(adviser);
        when(brokerIntegrationService.getBroker(dealerKey, serviceErrors)).thenReturn(dealerGroup);
        when(productIntegrationService.getProductDetail(ProductKey.valueOf(productId), serviceErrors)).thenReturn(myProduct);
        when(productFeeComponentService.getProductFeeComponents(myProduct, serviceErrors))
                .thenReturn(Arrays.asList(productFeeComponentDto));
        when(licenseAdviserFeeService.findLicenseAdviserFee(adviser.getDealerKey().getId(), dealerKey.getId(), serviceErrors))
                .thenReturn(licenseAdviserFeeDto);
    }

    @Test
    public void findProductFee_shouldReturnIsLicenseeFeeActiveAsFalseWhenDealerGroupIsLicenseeFeeActiveIsFalseAndProductIsLicenseeFeeActiveIsTrue()
            throws Exception {
        when(dealerGroup.isLicenseeFeeActive()).thenReturn(false);
        when(myProduct.isLicenseeFeeActive()).thenReturn(true);

        final ProductFeeDto productFee = productFeeService.findProductFee(adviserId, productId, serviceErrors);
        assertTrue(productFee.isLicenseeFeeActive());
        assertThat(productFee.getFeeComponents(), hasSize(1));
        assertThat(productFee.getFeeComponents(), hasItem(productFeeComponentDto));
    }

    @Test
    public void findProductFee_shouldReturnIsLicenseeFeeActiveAsFalseWhenDealerGroupIsLicenseeFeeActiveIsFalseAndProductIsLicenseeFeeActiveIsFalse()
            throws Exception {
        when(dealerGroup.isLicenseeFeeActive()).thenReturn(false);
        when(myProduct.isLicenseeFeeActive()).thenReturn(false);

        final ProductFeeDto productFee = productFeeService.findProductFee(adviserId, productId, serviceErrors);
        assertFalse(productFee.isLicenseeFeeActive());
        assertThat(productFee.getFeeComponents(), hasSize(1));
        assertThat(productFee.getFeeComponents(), hasItem(productFeeComponentDto));
    }

    @Test
    public void findProductFee_shouldReturnIsLicenseeFeeActiveAsFalseWhenDealerGroupIsLicenseeFeeActiveIsTrueAndProductIsLicenseeFeeActiveIsFalse()
            throws Exception {
        when(dealerGroup.isLicenseeFeeActive()).thenReturn(true);
        when(myProduct.isLicenseeFeeActive()).thenReturn(false);

        final ProductFeeDto productFee = productFeeService.findProductFee(adviserId, productId, serviceErrors);
        assertFalse(productFee.isLicenseeFeeActive());
        assertThat(productFee.getFeeComponents(), hasSize(1));
        assertThat(productFee.getFeeComponents(), hasItem(productFeeComponentDto));
    }

    @Test
    public void findProductFee_shouldReturnIsLicenseeFeeActiveAsTrueWhenDealerGroupIsLicenseeFeeActiveIsTrueAndProductIsLicenseeFeeActiveIsTrue()
            throws Exception {
        when(dealerGroup.isLicenseeFeeActive()).thenReturn(true);
        when(myProduct.isLicenseeFeeActive()).thenReturn(true);

        final ProductFeeDto productFee = productFeeService.findProductFee(adviserId, productId, serviceErrors);
        assertTrue(productFee.isLicenseeFeeActive());
        assertThat(productFee.getFeeComponents(), hasSize(1));
        assertThat(productFee.getFeeComponents(), hasItem(productFeeComponentDto));
    }
}