package com.bt.nextgen.api.product.service;

import com.bt.nextgen.api.fees.v1.model.LicenseAdviserFeeDto;
import com.bt.nextgen.api.fees.v1.service.LicenseAdviserFeeService;
import com.bt.nextgen.api.product.model.ProductFeeDto;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Deprecated
@Service
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
    public ProductFeeDto findProductFee(String adviserPositionId, String productId, ServiceErrors serviceErrors) {
        Broker dealerGroup = isLicenseeFeeActiveForDealerGroup(adviserPositionId, serviceErrors);
        boolean licenseeFeeActiveForDealerGroup=dealerGroup.isLicenseeFeeActive();
        Product product = productIntegrationService.getProductDetail(ProductKey.valueOf(productId), serviceErrors);

        ProductFeeDto productFeeDto = new ProductFeeDto();
        productFeeDto.setLicenseeFeeActive(licenseeFeeActiveForDealerGroup && product.isLicenseeFeeActive());
        productFeeDto.setFeeComponents(productFeeComponentService.getProductFeeComponents(product, serviceErrors));
        if(licenseeFeeActiveForDealerGroup && product.isLicenseeFeeActive()){
            LicenseAdviserFeeDto licenseAdviserFeeDto = licenseAdviserFeeService.findLicenseAdviserFee(adviserPositionId,productId,serviceErrors);
           if(null!=licenseAdviserFeeDto && null!=licenseAdviserFeeDto.getFeeComponentType()) {
               productFeeDto.setLicenseFeeForDealerGroup(licenseAdviserFeeDto.getFeeComponentType());
           }
        }
        return productFeeDto;
    }

    private Broker isLicenseeFeeActiveForDealerGroup(String adviserPositionId, ServiceErrors serviceErrors) {
        Broker adviser = brokerIntegrationService.getBroker(BrokerKey.valueOf(adviserPositionId), serviceErrors);
        Broker dealerGroup = brokerIntegrationService.getBroker(adviser.getDealerKey(), serviceErrors);
        return dealerGroup;
    }
}
