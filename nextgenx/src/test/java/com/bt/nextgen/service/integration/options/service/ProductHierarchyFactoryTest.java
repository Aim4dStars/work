package com.bt.nextgen.service.integration.options.service;

import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.product.ProductLevel;
import com.bt.nextgen.service.integration.options.model.CategoryKey;
import com.bt.nextgen.service.integration.options.model.CategoryType;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ProductHierarchyFactoryTest {
    @InjectMocks
    public ProductHierarchyFactory productFactory;

    @Mock
    public ProductIntegrationService productService;

    @Before
    public void setUp() {
        final Product offerProduct = Mockito.mock(Product.class);
        Mockito.when(offerProduct.getProductLevel()).thenReturn(ProductLevel.OFFER);
        Mockito.when(offerProduct.getShortName()).thenReturn("id.ol");
        Mockito.when(offerProduct.getParentProductKey()).thenReturn(ProductKey.valueOf("wlProduct"));

        final Product wlProduct = Mockito.mock(Product.class);
        Mockito.when(wlProduct.getProductLevel()).thenReturn(ProductLevel.WHITE_LABEL);
        Mockito.when(wlProduct.getShortName()).thenReturn("id.wl");
        Mockito.when(wlProduct.getParentProductKey()).thenReturn(ProductKey.valueOf("plProduct"));

        final Product plProduct = Mockito.mock(Product.class);
        Mockito.when(plProduct.getProductLevel()).thenReturn(ProductLevel.PRIVATE_LABEL);
        Mockito.when(plProduct.getShortName()).thenReturn("id.pl");
        Mockito.when(plProduct.getParentProductKey()).thenReturn(ProductKey.valueOf("rootProduct"));

        final Product rootProduct = Mockito.mock(Product.class);
        Mockito.when(rootProduct.getShortName()).thenReturn("id.root");
        Mockito.when(rootProduct.getProductLevel()).thenReturn(ProductLevel.CATEGORY);

        Mockito.when(productService.getProductDetail(Mockito.any(ProductKey.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<Product>() {
                    @Override
                    public Product answer(InvocationOnMock invocation) throws Throwable {
                        String productId = ((ProductKey) invocation.getArguments()[0]).getId();
                        if ("offerProduct".equals(productId)) {
                            return offerProduct;
                        }
                        if ("wlProduct".equals(productId)) {
                            return wlProduct;
                        }
                        if ("plProduct".equals(productId)) {
                            return plProduct;
                        }
                        if ("rootProduct".equals(productId)) {
                            return rootProduct;
                        }
                        return null;
                    }
                });
    }

    @Test
    public void testBuildHierarchy_whenInvokedWithAFullHierarchy_thenTheHierarchyIsOrderedCorrectly() {
        List<CategoryKey> categories = productFactory.buildHierarchy(ProductKey.valueOf("wlProduct"), new FailFastErrorsImpl());
        Assert.assertEquals(3, categories.size());
        Assert.assertEquals(CategoryType.WHITE_LABEL, categories.get(0).getCategory());
        Assert.assertEquals(CategoryType.PRIVATE_LABEL, categories.get(1).getCategory());
        Assert.assertEquals(CategoryType.PRODUCT, categories.get(2).getCategory());
        Assert.assertEquals("id.wl", categories.get(0).getCategoryId());
        Assert.assertEquals("id.pl", categories.get(1).getCategoryId());
        Assert.assertEquals("id.root", categories.get(2).getCategoryId());

    }

    @Test
    public void testBuildHierarchy_whenInvokedWithAPartialHierarchy_thenTheHierarchyIsOrderedCorrectly() {
        List<CategoryKey> categories = productFactory.buildHierarchy(ProductKey.valueOf("plProduct"), new FailFastErrorsImpl());
        Assert.assertEquals(2, categories.size());
        Assert.assertEquals(CategoryType.PRIVATE_LABEL, categories.get(0).getCategory());
        Assert.assertEquals(CategoryType.PRODUCT, categories.get(1).getCategory());
        Assert.assertEquals("id.pl", categories.get(0).getCategoryId());
        Assert.assertEquals("id.root", categories.get(1).getCategoryId());
    }

    @Test
    public void testBuildHierarchy_whenAnUnmappedProductLevelIsEncountered_thenTheHierarchyOmitsThatLevel() {
        List<CategoryKey> categories = productFactory.buildHierarchy(ProductKey.valueOf("offerProduct"),
                new FailFastErrorsImpl());
        Assert.assertEquals(4, categories.size());
        Assert.assertEquals(CategoryType.OFFER, categories.get(0).getCategory());
        Assert.assertEquals(CategoryType.WHITE_LABEL, categories.get(1).getCategory());
        Assert.assertEquals(CategoryType.PRIVATE_LABEL, categories.get(2).getCategory());
        Assert.assertEquals(CategoryType.PRODUCT, categories.get(3).getCategory());
        Assert.assertEquals("id.ol", categories.get(0).getCategoryId());
        Assert.assertEquals("id.wl", categories.get(1).getCategoryId());
        Assert.assertEquals("id.pl", categories.get(2).getCategoryId());
        Assert.assertEquals("id.root", categories.get(3).getCategoryId());
    }

}
