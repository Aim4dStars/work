package com.bt.nextgen.service.avaloq.order;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.core.web.model.SearchCriteria;
import com.bt.nextgen.core.web.model.SearchParametersImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.integration.Origin;
import com.bt.nextgen.service.integration.order.Order;
import com.bt.nextgen.service.integration.order.OrderDetail;
import com.bt.nextgen.service.integration.order.OrderStatus;
import com.bt.nextgen.service.integration.order.OrderType;
import com.btfin.panorama.core.validation.Validator;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;

public class OrderImplIntegrationTest extends BaseSecureIntegrationTest {
    @Autowired
    private Validator validator;

    @Autowired
    AvaloqOrderIntegrationServiceImpl orderService;

    private ServiceErrors serviceErrors;

    @Before
    public void setup() {
        serviceErrors = new ServiceErrorsImpl();
    }

    @Test
    public void testValidation_whenOrderIdIsNull_thenServiceErrors() {
        OrderImpl order = new OrderImpl();
        order.setOrderType(OrderType.APPLICATION);
        order.setAccountId("2222");
        order.setAssetId("3333");
        order.setOrigin(Origin.WEB_UI);
        order.setCreateDate(new DateTime());
        order.setAmount(new BigDecimal("123456.99"));
        order.setStatus(OrderStatus.IN_PROGRESS);
        order.setCancellable(Boolean.TRUE);
        order.setContractNotes(Boolean.FALSE);
        order.setDetails(new ArrayList<OrderDetail>());
        validator.validate(order, serviceErrors);
        Assert.assertTrue(serviceErrors.hasErrors());
        Assert.assertEquals("orderId may not be null", serviceErrors.getErrorList().iterator().next().getReason());
    }

    @Test
    public void testValidation_whenAccountIdIsNull_thenServiceErrors() {
        OrderImpl order = new OrderImpl();
        order.setOrderId("1111");
        order.setOrderType(OrderType.APPLICATION);
        order.setAssetId("3333");
        order.setOrigin(Origin.WEB_UI);
        order.setCreateDate(new DateTime());
        order.setAmount(new BigDecimal("123456.99"));
        order.setStatus(OrderStatus.IN_PROGRESS);
        order.setCancellable(Boolean.TRUE);
        order.setContractNotes(Boolean.FALSE);
        order.setDetails(new ArrayList<OrderDetail>());
        validator.validate(order, serviceErrors);
        Assert.assertTrue(serviceErrors.hasErrors());
        Assert.assertEquals("accountId may not be null", serviceErrors.getErrorList().iterator().next().getReason());
    }

    @Test
    @Ignore("error is disabled to accomodate bad data in avaloq")
    public void testValidation_whenAssetIdIsNull_thenServiceErrors() {
        OrderImpl order = new OrderImpl();
        order.setOrderId("1111");
        order.setOrderType(OrderType.APPLICATION);
        order.setAccountId("2222");
        order.setOrigin(Origin.WEB_UI);
        order.setCreateDate(new DateTime());
        order.setAmount(new BigDecimal("123456.99"));
        order.setStatus(OrderStatus.IN_PROGRESS);
        order.setCancellable(Boolean.TRUE);
        order.setContractNotes(Boolean.FALSE);
        order.setDetails(new ArrayList<OrderDetail>());

        validator.validate(order, serviceErrors);
        Assert.assertTrue(serviceErrors.hasErrors());
        Assert.assertEquals("assetId may not be null", serviceErrors.getErrorList().iterator().next().getReason());
    }

    @Test
    @Ignore("error is disabled to accomodate bad data in avaloq")
    public void testValidation_whenOriginIsNull_thenServiceErrors() {
        OrderImpl order = new OrderImpl();
        order.setOrderId("1111");
        order.setOrderType(OrderType.APPLICATION);
        order.setAccountId("2222");
        order.setAssetId("3333");
        order.setCreateDate(new DateTime());
        order.setAmount(new BigDecimal("123456.99"));
        order.setStatus(OrderStatus.IN_PROGRESS);
        order.setCancellable(Boolean.TRUE);
        order.setContractNotes(Boolean.FALSE);
        order.setDetails(new ArrayList<OrderDetail>());

        validator.validate(order, serviceErrors);
        Assert.assertTrue(serviceErrors.hasErrors());
        Assert.assertEquals("origin may not be null", serviceErrors.getErrorList().iterator().next().getReason());
    }

    @Test
    public void testValidation_whenLastUpdateIsNull_thenServiceErrors() {
        OrderImpl order = new OrderImpl();
        order.setOrderId("1111");
        order.setOrderType(OrderType.APPLICATION);
        order.setAccountId("2222");
        order.setAssetId("3333");
        order.setOrigin(Origin.WEB_UI);
        order.setAmount(new BigDecimal("123456.99"));
        order.setStatus(OrderStatus.IN_PROGRESS);
        order.setCancellable(Boolean.TRUE);
        order.setContractNotes(Boolean.FALSE);
        order.setDetails(new ArrayList<OrderDetail>());

        validator.validate(order, serviceErrors);
        Assert.assertTrue(serviceErrors.hasErrors());
        Assert.assertEquals("createDate may not be null", serviceErrors.getErrorList().iterator().next().getReason());
    }

    @Test
    public void testValidation_whenStatusIsNull_thenServiceErrors() {
        OrderImpl order = new OrderImpl();
        order.setOrderId("1111");
        order.setOrderType(OrderType.APPLICATION);
        order.setAccountId("2222");
        order.setAssetId("3333");
        order.setOrigin(Origin.WEB_UI);
        order.setCreateDate(new DateTime());
        order.setAmount(new BigDecimal("123456.99"));
        order.setCancellable(Boolean.TRUE);
        order.setContractNotes(Boolean.FALSE);
        order.setDetails(new ArrayList<OrderDetail>());

        validator.validate(order, serviceErrors);
        Assert.assertTrue(serviceErrors.hasErrors());
        Assert.assertEquals("status may not be null", serviceErrors.getErrorList().iterator().next().getReason());
    }

    @Test
    public void testValidation_whenCancellableIsNull_thenServiceErrors() {
        OrderImpl order = new OrderImpl();
        order.setOrderId("1111");
        order.setOrderType(OrderType.APPLICATION);
        order.setAccountId("2222");
        order.setAssetId("3333");
        order.setOrigin(Origin.WEB_UI);
        order.setCreateDate(new DateTime());
        order.setAmount(new BigDecimal("123456.99"));
        order.setStatus(OrderStatus.IN_PROGRESS);
        order.setContractNotes(Boolean.FALSE);
        order.setDetails(new ArrayList<OrderDetail>());

        validator.validate(order, serviceErrors);
        Assert.assertTrue(serviceErrors.hasErrors());
        Assert.assertEquals("cancellable may not be null", serviceErrors.getErrorList().iterator().next().getReason());
    }

    @Test
    public void testValidation_whenObjectIsNull_thenServiceErrors() {
        OrderImpl order = null;

        validator.validate(order, serviceErrors);
        Assert.assertTrue(serviceErrors.hasErrors());
        Assert.assertEquals("Failed to validate - object is null", serviceErrors.getErrorList().iterator().next().getReason());
    }

    @Test
    public void testValidation_whenValid_thenNoServiceError() {
        OrderImpl order = new OrderImpl();
        order.setOrderId("1111");
        order.setOrderType(OrderType.APPLICATION);
        order.setAccountId("2222");
        order.setAssetId("3333");
        order.setOrigin(Origin.WEB_UI);
        order.setCreateDate(new DateTime());
        order.setAmount(new BigDecimal("123456.99"));
        order.setStatus(OrderStatus.IN_PROGRESS);
        order.setCancellable(Boolean.TRUE);
        order.setContractNotes(Boolean.FALSE);
        order.setDetails(new ArrayList<OrderDetail>());
        validator.validate(order, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
    }

    @Test
    public void testResponse() {
        SearchParametersImpl search = Mockito.mock(SearchParametersImpl.class);
        Mockito.when(search.getSearchCriterias()).thenReturn(new ArrayList<SearchCriteria>());
        Mockito.doNothing().when(search).setSearchFor(any(Template.class));
        Mockito.when(search.getSearchFor()).thenReturn(Template.ORDERS);

        List<Order> orders = orderService.loadOrders(search, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertEquals(orders.size(), 11);

        Assert.assertEquals(orders.get(9).getAccountId(), "405946");
        Assert.assertEquals(orders.get(9).getAssetId(), "110450");
        Assert.assertEquals(orders.get(9).getBrokerName(), "UBS AG AUSTRALIA BRANCH");
        Assert.assertEquals(orders.get(9).getAmount(), new BigDecimal("-353"));
        Assert.assertEquals(orders.get(9).getBrokerage(), new BigDecimal("-12.5"));
    }
}
