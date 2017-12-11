package com.bt.nextgen.reports.taxdeduction;

import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by L067218 on 14/12/2016.
 */
@Component
public class PersonalTaxDeductionPdfHelper {
    @Autowired
    private ProductIntegrationService productIntegrationService;

    public String getUsi(WrapAccountDetail accountDetail, ServiceErrors serviceErrors) {
        // get the white label's product
        final Product whiteLabelProduct = productIntegrationService.getProductDetail(accountDetail.getProductKey(),
                serviceErrors);
        // get the private label's product (parent product)
        final Product privateLabelProduct = productIntegrationService.getProductDetail(
                whiteLabelProduct.getParentProductKey(), serviceErrors);

        return privateLabelProduct.getProductUsi() != null ? privateLabelProduct.getProductUsi() : "";
    }
}
