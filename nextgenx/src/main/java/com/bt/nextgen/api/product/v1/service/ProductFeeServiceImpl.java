package com.bt.nextgen.api.product.v1.service;

import com.bt.nextgen.api.fees.v1.model.LicenseAdviserFeeDto;
import com.bt.nextgen.api.fees.v1.service.LicenseAdviserFeeService;
import com.bt.nextgen.api.product.v1.model.ProductFeeDto;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("ProductFeeServiceV1")
public class ProductFeeServiceImpl implements ProductFeeService {

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    private ProductFeeComponentService productFeeComponentService;

    @Autowired
    private LicenseAdviserFeeService licenseAdviserFeeService;

    @Override
    public ProductFeeDto findProductFee(final String adviserPositionId, final String productId,
            final ServiceErrors serviceErrors) {
        final Broker dealerGroup = isLicenseeFeeActiveForDealerGroup(adviserPositionId, serviceErrors);
        final boolean licenseeFeeActiveForDealerGroup = dealerGroup.isLicenseeFeeActive();
        final Product product = productIntegrationService.getProductDetail(ProductKey.valueOf(productId), serviceErrors);

        final ProductFeeDto productFeeDto = new ProductFeeDto();
        productFeeDto.setLicenseeFeeActive(licenseeFeeActiveForDealerGroup && product.isLicenseeFeeActive());
        productFeeDto.setFeeComponents(productFeeComponentService.getProductFeeComponents(product, serviceErrors));
        if (licenseeFeeActiveForDealerGroup && product.isLicenseeFeeActive()) {
            final LicenseAdviserFeeDto licenseAdviserFeeDto = licenseAdviserFeeService.findLicenseAdviserFee(adviserPositionId,
                    productId, serviceErrors);
            if (null != licenseAdviserFeeDto && null != licenseAdviserFeeDto.getFeeComponentType()) {
                productFeeDto.setLicenseFeeForDealerGroup(licenseAdviserFeeDto.getFeeComponentType());
            }
        }
        return productFeeDto;
    }

    private Broker isLicenseeFeeActiveForDealerGroup(final String adviserPositionId, final ServiceErrors serviceErrors) {
        final Broker adviser = brokerIntegrationService.getBroker(BrokerKey.valueOf(adviserPositionId), serviceErrors);
        final Broker dealerGroup = brokerIntegrationService.getBroker(adviser.getDealerKey(), serviceErrors);
        return dealerGroup;
    }
}
