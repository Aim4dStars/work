package com.bt.nextgen.api.product.v1.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.product.v1.model.AccountProductDocumentDto;
import com.bt.nextgen.api.product.v1.model.ProductDto;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.bt.nextgen.service.avaloq.product.ProductLevel;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userprofile.JobProfileImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountProductDocumentsDtoServiceTest {

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private BrokerHelperService brokerHelperService;

    @InjectMocks
    private AccountProductDocumentDtoServiceImpl accountProductDocumentDtoServiceImpl;

    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private ProductDtoConverter productDtoConverter;

    private ServiceErrors serviceErrors;
    private ProductDto productDto;

    @Before
    public void setup() throws Exception {
        serviceErrors = new ServiceErrorsImpl();
        final Broker dealerGroup = getDealerGroup();
        final List<Product> productList = getProductList();
        final JobProfileImpl jobProfile = new JobProfileImpl();
        jobProfile.setJobRole(JobRole.ADVISER);
        final UserProfile userProfile = new UserProfileAdapterImpl(null, jobProfile);
        Mockito.when(
                brokerHelperService.getDealerGroupForInvestor(Mockito.any(WrapAccount.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(dealerGroup);
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(userProfile);
        Mockito.when(accountService.loadWrapAccountWithoutContainers(
                Mockito.any(com.bt.nextgen.service.integration.account.AccountKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(new WrapAccountImpl());
        Mockito.when(productIntegrationService.getProductDetail(Mockito.any(ProductKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(productList.get(1));
        Mockito.when(productIntegrationService.getDealerGroupProductList(Mockito.any(BrokerKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(productList);

        productDto = mock(ProductDto.class);
        when(productDto.getKey()).thenReturn(new com.bt.nextgen.api.product.v1.model.ProductKey(EncodedString.fromPlainText("product1").toString()));
        when(productDto.getProductName()).thenReturn("White Label 060f52dc6d17421eaf1632ac9efae210");
        when(productDtoConverter.convert(Mockito.any(Product.class))).thenReturn(productDto);
    }

    @Test
    public void testFind_whenCalledWithAnAccountKey_thenTheReturnedDtoKeyMatches() {
        final AccountKey encodedAccountKey = new AccountKey(EncodedString.fromPlainText("asdf").toString());
        final AccountProductDocumentDto productDocumentDto = accountProductDocumentDtoServiceImpl.find(encodedAccountKey, serviceErrors);
        assertThat(productDocumentDto.getKey(), Matchers.equalTo(encodedAccountKey));
        assertEquals(productDocumentDto.getProductList().size(), 2);
        assertEquals(productDocumentDto.getProductList().get(0).getKey(), productDto.getKey());
        assertEquals(productDocumentDto.getProductList().get(0).getProductName(), productDto.getProductName());
        assertEquals(productDocumentDto.getBrandList().size(), 0);
        assertEquals(productDocumentDto.getDocumentTags().size(), 3);
    }

    private List<Product> getProductList() {
        final List<Product> products = new ArrayList<>();

        final ProductImpl product1 = new ProductImpl();
        final ProductImpl product2 = new ProductImpl();
        final ProductImpl product3 = new ProductImpl();

        product1.setProductKey(ProductKey.valueOf("1234"));
        product1.setShortName("PROD.WL.060F52DC6D17421EAF1632AC9EFAE210");
        product1.setProductName("Asset Administrator");
        product1.setProductLevel(ProductLevel.WHITE_LABEL);
        product1.setParentProductId("1");

        product2.setProductKey(ProductKey.valueOf("5678"));
        product2.setShortName("PROD.WL.35D1B6570418");
        product2.setProductName("BT Panorama");
        product2.setProductLevel(ProductLevel.WHITE_LABEL);
        product2.setParentProductId("2");

        product3.setProductKey(ProductKey.valueOf("Offer1234"));
        product3.setShortName("PROD.OFFER.IFAO");
        product3.setProductName("IFA Open");
        product3.setProductLevel(ProductLevel.OFFER);
        product3.setDirect(true);
        product3.setParentProductId("5678");

        products.add(product1);
        products.add(product2);
        products.add(product3);

        return products;

    }

    private Broker getDealerGroup() {
        final Broker broker = Mockito.mock(Broker.class);
        when(broker.getDealerKey()).thenReturn(BrokerKey.valueOf("1234"));
        return broker;
    }

}
