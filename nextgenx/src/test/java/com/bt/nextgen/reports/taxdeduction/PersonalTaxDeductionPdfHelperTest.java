package com.bt.nextgen.reports.taxdeduction;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by L067218 on 14/12/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class PersonalTaxDeductionPdfHelperTest {
    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private ServiceErrors serviceErrors;

    @InjectMocks
    private PersonalTaxDeductionPdfHelper helper;


    @Test
    public void getUsi() {
        final String usi = "790281342";

        getUsi("null USI", null, "");
        getUsi("non-null USI", usi, usi);
    }


    private void getUsi(String infoStr, String usi, String expectedUsiLabel) {
        final WrapAccountDetail account = new WrapAccountDetailImpl();
        final Product whiteLabelProduct = makeProduct("1234", null);
        final Product privateLabelProduct = makeProduct("7890", usi);
        final String result;

        ((WrapAccountImpl) account).setProductKey(whiteLabelProduct.getProductKey());

        reset(productIntegrationService);

        when(productIntegrationService.getProductDetail(eq(whiteLabelProduct.getProductKey()), eq(serviceErrors)))
                .thenReturn(whiteLabelProduct);
        when(productIntegrationService.getProductDetail(eq(privateLabelProduct.getParentProductKey()),
                eq(serviceErrors)))
                .thenReturn(privateLabelProduct);

        result = helper.getUsi(account, serviceErrors);
        verify(productIntegrationService).getProductDetail(eq(whiteLabelProduct.getProductKey()), eq(serviceErrors));
        verify(productIntegrationService).getProductDetail(eq(privateLabelProduct.getParentProductKey()),
                eq(serviceErrors));

        assertThat(infoStr + " - USI", result, equalTo(expectedUsiLabel));
    }


    private Product makeProduct(String productId, String usi) {
        final ProductKey productKey = ProductKey.valueOf(productId);
        final ProductImpl retval = new ProductImpl();

        retval.setProductKey(productKey);
        retval.setProductUsi(usi);

        return retval;
    }
}
