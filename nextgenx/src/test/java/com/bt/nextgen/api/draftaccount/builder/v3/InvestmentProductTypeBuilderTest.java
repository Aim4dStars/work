package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.IInvestmentChoiceForm;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AvaloqAssetIntegrationServiceImpl;
import com.bt.nextgen.service.avaloq.product.ProductDetailImpl;
import com.bt.nextgen.service.avaloq.product.ProductLevel;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import ns.btfin_com.product.common.investmentaccount.v2_0.AccountInvestmentProductType;
import ns.btfin_com.product.common.investmentproduct.v1_1.InvestmentCodeIssuerType;
import ns.btfin_com.product.common.investmentproduct.v1_1.ProductType;
import ns.btfin_com.product.common.investmentproduct.v1_1.ServiceOfferCodeIssuerType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InvestmentProductTypeBuilderTest {

    @Mock
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @InjectMocks
    InvestmentProductTypeBuilder investmentProductTypeBuilder;


    Broker broker;

    String assetId = "assetId";

    @Before
    public void setUp() {
        Asset asset1 = mock(Asset.class);
        when(asset1.getAssetName()).thenReturn("BT Moderate Portfolio");
        when(asset1.getAssetCode()).thenReturn("ACode");
        when(asset1.getAssetId()).thenReturn(assetId);

        Asset asset2 = mock(Asset.class);
        when(asset2.getAssetName()).thenReturn("Null asset code");
        when(asset2.getAssetCode()).thenReturn(null);
        when(asset2.getAssetId()).thenReturn("something");

        final List<Product> productList =
                Arrays.asList(createProduct(ProductLevel.OFFER, "Offer Active", "product"),
                        createProduct(ProductLevel.WHITE_LABEL, "BT Invest", "product"),
                        createProduct(ProductLevel.OFFER, "Offer Simple", "ServiceOfferCode"));

        when(productIntegrationService.getDealerGroupProductList(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(productList);
        when(assetIntegrationService.loadAvailableAssets(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(Arrays.asList(asset2, asset1));
        broker = mock(Broker.class);
    }

    @Test
    public void shouldReturnAccountInvestmentProductType() {
        IInvestmentChoiceForm investmentChoiceForm = mock(IInvestmentChoiceForm.class);
        when(investmentChoiceForm.getPortfolioType()).thenReturn("ACode");
        IClientApplicationForm clientApplicationForm = mock(IClientApplicationForm.class);
        when(clientApplicationForm.hasInvestmentChoice()).thenReturn(true);
        when(clientApplicationForm.getInvestmentChoice()).thenReturn(investmentChoiceForm);

        AccountInvestmentProductType accountInvestmentProductType = investmentProductTypeBuilder.getAccountInvestmentProductType(clientApplicationForm, broker);

        assertThat(accountInvestmentProductType.getInvestmentCode(), is(assetId));
        assertThat(accountInvestmentProductType.getInvestmentCodeIssuer(), is(InvestmentCodeIssuerType.AVALOQ));
        assertThat(accountInvestmentProductType.getType(), is(ProductType.MANAGED_PORTFOLIO));
        assertThat(accountInvestmentProductType.getServiceOfferCode(), is("ServiceOfferCode"));
        assertThat(accountInvestmentProductType.getServiceOfferCodeIssuer(), is(ServiceOfferCodeIssuerType.AVALOQ));
    }

    @Test
    public void shouldReturnNullIsNoInvestmentOptionExists() {
        IClientApplicationForm clientApplicationForm = mock(IClientApplicationForm.class);
        when(clientApplicationForm.getInvestmentChoice()).thenReturn(null);

        AccountInvestmentProductType accountInvestmentProductType = investmentProductTypeBuilder.getAccountInvestmentProductType(clientApplicationForm, broker);
        assertNull(accountInvestmentProductType);
    }

    @Test
    public void shouldReturnNullIfInvestmentOptionIsDecideLater() {
        IClientApplicationForm clientApplicationForm = mock(IClientApplicationForm.class);
        IInvestmentChoiceForm investmentChoiceForm = mock(IInvestmentChoiceForm.class);
        when(investmentChoiceForm.getPortfolioType()).thenReturn("0");
        when(clientApplicationForm.getInvestmentChoice()).thenReturn(null);

        AccountInvestmentProductType accountInvestmentProductType = investmentProductTypeBuilder.getAccountInvestmentProductType(clientApplicationForm, broker);
        assertNull(accountInvestmentProductType);
    }

    private Product createProduct(ProductLevel productLevel, String productName, String productCode) {
        ProductDetailImpl product = new ProductDetailImpl();
        product.setProductKey(ProductKey.valueOf(productCode));
        product.setProductId(productCode);
        product.setProductName(productName);
        product.setProductLevel(productLevel);
        return product;
    }
}