package com.bt.nextgen.service.avaloq.order;

import com.bt.nextgen.core.exception.ValidationException;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.core.validation.ValidationError.ErrorType;
import com.bt.nextgen.core.web.model.SearchCriteria;
import com.bt.nextgen.core.web.model.SearchCriteriaProvider;
import com.bt.nextgen.core.web.model.SearchParametersImpl;
import com.bt.nextgen.core.web.model.SearchParams;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.order.Order;
import com.bt.nextgen.service.integration.order.OrderGroup;
import com.bt.nextgen.service.integration.order.OrderInProgress;
import com.bt.nextgen.service.integration.order.OrderTransaction;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.core.validation.Validator;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class AvaloqOrderIntegrationServiceImplTest {
    @InjectMocks
    private AvaloqOrderIntegrationServiceImpl orderService;

    @Mock
    private AvaloqExecute avaloqExecute;

    @Mock
    private Validator validator;

    @Mock
    private OrderGroupReportConverter orderGroupReportConverter;

    @Mock
    private OrderGroupConverter orderGroupConverter;

    @Mock
    private OrderCancelConverter orderCancelConverter;

    @Mock
    private UpdateOrderConverter updateOrderConverter;

    @Mock
    private AvaloqGatewayHelperService webserviceClient;

    private OrderGroupImpl orderGroup1;
    private List<OrderGroupImpl> orderGroupList;
    private OrderResponseImpl orderResponse;

    @Before
    public void setUp() {
        orderGroup1 = new OrderGroupImpl();
        orderGroup1.setAccountKey(AccountKey.valueOf("accountKey"));
        orderGroup1.setOrderGroupId("1234");
        orderGroup1.setLastUpdateDate(new DateTime());
        orderGroup1.setOwner(ClientKey.valueOf("testClient"));
        orderGroup1.setOrderType("buy");
        orderGroup1.setReference("Bob's Transaction");

        orderGroupList = new ArrayList<>();
        orderGroupList.add(orderGroup1);

        List<Order> orders = new ArrayList<>();
        orders.add(new OrderImpl());
        orderResponse = Mockito.mock(OrderResponseImpl.class);
        Mockito.when(orderResponse.getOrders()).thenReturn(orders);

        Mockito.when(orderGroupReportConverter.toModel(any(com.avaloq.abs.screen_rep.hira.btfg$ui_doc_list_doc.Rep.class),
                any(ServiceErrors.class))).thenReturn(orderGroupList);

        com.avaloq.abs.screen_rep.hira.btfg$ui_doc_list_doc.Rep rep = new com.avaloq.abs.screen_rep.hira.btfg$ui_doc_list_doc.Rep();
        Mockito.when(avaloqExecute.executeReportRequest(any(AvaloqReportRequest.class))).thenReturn(rep);

        Mockito.doNothing().when(validator).validate(Mockito.anyCollection(), any(ServiceErrors.class));
    }

    @Test
    public void testLoadOrderGroups_whenAccountIdPassedInRequest_thenOrdersReturned() throws Exception {
        List<OrderGroup> response = orderService.loadOrderGroups("accountId", new ServiceErrorsImpl());
        Mockito.verify(avaloqExecute).executeReportRequest(any(AvaloqReportRequest.class));
        Mockito.verify(orderGroupReportConverter).toModel(any(com.avaloq.abs.screen_rep.hira.btfg$ui_doc_list_doc.Rep.class),
                any(ServiceErrors.class));
        Mockito.verify(validator).validate(Mockito.anyCollection(), any(ServiceErrors.class));
        assertThat(response.size(), is(1));
    }

    @Test
    public void testLoadOrderGroups_whenBrokerKeysPassedInRequest_thenOrdersReturned() throws Exception {
        List<BrokerKey> keys = new ArrayList<>();
        List<OrderGroup> response = orderService.loadOrderGroups(keys, new ServiceErrorsImpl());
        Mockito.verify(avaloqExecute).executeReportRequest(any(AvaloqReportRequest.class));
        Mockito.verify(orderGroupReportConverter).toModel(any(com.avaloq.abs.screen_rep.hira.btfg$ui_doc_list_doc.Rep.class),
                any(ServiceErrors.class));
        Mockito.verify(validator).validate(Mockito.anyCollection(), any(ServiceErrors.class));
        assertThat(response.size(), is(1));
    }

    @Test
    public void testLoadOrderGroup_whenValidRequest_thenServiceCalled() throws Exception {
        Mockito.when(webserviceClient.sendToWebService(Mockito.anyObject(), any(AvaloqOperation.class), any(ServiceErrors.class)))
                .thenReturn(new com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp());
        Mockito.when(orderGroupConverter.toOrderLoadRequest(Mockito.anyString(), any(ServiceErrors.class)))
                .thenReturn(new com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlReq());
        Mockito.when(orderGroupConverter.toLoadOrderResponse(Mockito.anyString(), any(BigInteger.class), any(AccountKey.class),
                any(com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp.class), any(ServiceErrors.class)))
                .thenReturn(new OrderGroupImpl());

        orderService.loadOrderGroup(AccountKey.valueOf("accountKey"), "orderId", new ServiceErrorsImpl());
        Mockito.verify(webserviceClient).sendToWebService(Mockito.anyObject(), any(AvaloqOperation.class),
                any(ServiceErrors.class));
        Mockito.verify(orderGroupConverter).toOrderLoadRequest(Mockito.anyString(), any(ServiceErrors.class));
        Mockito.verify(orderGroupConverter).toLoadOrderResponse(Mockito.anyString(), any(BigInteger.class), any(AccountKey.class),
                any(com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp.class), any(ServiceErrors.class));
    }

    @Test
    public void testLoadOrder_whenValidRequest_thenOrdersReturned() throws Exception {
        Mockito.when(avaloqExecute.executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class), Mockito.any(Class.class),
                Mockito.any(ServiceErrors.class))).thenReturn(orderResponse);

        List<Order> response = orderService.loadOrder("orderId", new ServiceErrorsImpl());
        Mockito.verify(avaloqExecute).executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class),
                Mockito.any(Class.class), Mockito.any(ServiceErrors.class));
        Mockito.verify(validator).validate(Mockito.anyCollection(), any(ServiceErrors.class));
        assertThat(response.size(), is(1));
    }

    @Test
    public void testLoadOrder_whenNullResponse_thenEmptyListReturned() throws Exception {
        Mockito.when(avaloqExecute.executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class), Mockito.any(Class.class),
                Mockito.any(ServiceErrors.class))).thenReturn(null);

        List<Order> response = orderService.loadOrder("orderId", new ServiceErrorsImpl());
        Mockito.verify(avaloqExecute).executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class),
                Mockito.any(Class.class), Mockito.any(ServiceErrors.class));
        assertThat(response.size(), is(0));
    }

    @Test
    public void testLoadOrder_whenNullResponseOrders_thenEmptyListReturned() throws Exception {
        Mockito.when(avaloqExecute.executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class), Mockito.any(Class.class),
                Mockito.any(ServiceErrors.class))).thenReturn(new OrderResponseImpl());

        List<Order> response = orderService.loadOrder("orderId", new ServiceErrorsImpl());
        Mockito.verify(avaloqExecute).executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class),
                Mockito.any(Class.class), Mockito.any(ServiceErrors.class));
        assertThat(response.size(), is(0));
    }

    @Test
    public void testLoadOrders_whenValidRequest_thenOrdersReturned() throws Exception {
        List<SearchCriteria> criterias = new ArrayList<>();
        SearchCriteriaProvider criteria = new SearchCriteriaProvider();
        criteria.setSearchKey(SearchParams.PORTFOLIO_ID);
        criteria.setSearchValue("1234");
        criterias.add(criteria);
        SearchParametersImpl search = Mockito.mock(SearchParametersImpl.class);
        Mockito.when(search.getSearchCriterias()).thenReturn(criterias);
        Mockito.doNothing().when(search).setSearchFor(any(Template.class));
        Mockito.when(search.getSearchFor()).thenReturn(Template.ORDERS);

        Mockito.when(avaloqExecute.executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class), Mockito.any(Class.class),
                Mockito.any(ServiceErrors.class))).thenReturn(orderResponse);

        List<Order> response = orderService.loadOrders(search, new ServiceErrorsImpl());
        Mockito.verify(avaloqExecute).executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class),
                Mockito.any(Class.class), Mockito.any(ServiceErrors.class));
        Mockito.verify(validator).validate(Mockito.anyCollection(), any(ServiceErrors.class));
        assertThat(response.size(), is(1));
    }

    @Test
    public void testLoadOrders_whenNullResponseOrders_thenEmptyListReturned() throws Exception {
        List<SearchCriteria> criterias = new ArrayList<>();
        SearchCriteriaProvider criteria = new SearchCriteriaProvider();
        criteria.setSearchKey(SearchParams.PORTFOLIO_ID);
        criteria.setSearchValue("1234");
        criterias.add(criteria);
        SearchParametersImpl search = Mockito.mock(SearchParametersImpl.class);
        Mockito.when(search.getSearchCriterias()).thenReturn(criterias);
        Mockito.doNothing().when(search).setSearchFor(any(Template.class));
        Mockito.when(search.getSearchFor()).thenReturn(Template.ORDERS);

        Mockito.when(avaloqExecute.executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class), Mockito.any(Class.class),
                Mockito.any(ServiceErrors.class))).thenReturn(new OrderResponseImpl());

        List<Order> response = orderService.loadOrders(search, new ServiceErrorsImpl());
        Mockito.verify(avaloqExecute).executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class),
                Mockito.any(Class.class), Mockito.any(ServiceErrors.class));
        assertThat(response.size(), is(0));
    }

    @Test
    public void testLoadOrders_whenNullResponse_thenEmptyListReturned() throws Exception {
        List<SearchCriteria> criterias = new ArrayList<>();
        SearchCriteriaProvider criteria = new SearchCriteriaProvider();
        criteria.setSearchKey(SearchParams.ORDER_ID);
        criteria.setSearchValue("1234");
        criterias.add(criteria);
        SearchParametersImpl search = Mockito.mock(SearchParametersImpl.class);
        Mockito.when(search.getSearchCriterias()).thenReturn(criterias);
        Mockito.doNothing().when(search).setSearchFor(any(Template.class));
        Mockito.when(search.getSearchFor()).thenReturn(Template.ORDERS);

        Mockito.when(avaloqExecute.executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class), Mockito.any(Class.class),
                Mockito.any(ServiceErrors.class))).thenReturn(null);

        List<Order> response = orderService.loadOrders(search, new ServiceErrorsImpl());
        Mockito.verify(avaloqExecute).executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class),
                Mockito.any(Class.class), Mockito.any(ServiceErrors.class));
        assertThat(response.size(), is(0));
    }

    @Test
    public void testSearchOrders_whenValidRequest_thenOrdersReturned() throws Exception {
        Mockito.when(avaloqExecute.executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class), Mockito.any(Class.class),
                Mockito.any(ServiceErrors.class))).thenReturn(orderResponse);

        List<Order> response = orderService.searchOrders("orderId", new ServiceErrorsImpl());
        Mockito.verify(avaloqExecute).executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class),
                Mockito.any(Class.class), Mockito.any(ServiceErrors.class));
        Mockito.verify(validator).validate(Mockito.anyCollection(), any(ServiceErrors.class));
        assertThat(response.size(), is(1));
    }

    @Test
    public void testSearchOrders_whenNullResponse_thenEmptyListReturned() throws Exception {
        Mockito.when(avaloqExecute.executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class), Mockito.any(Class.class),
                Mockito.any(ServiceErrors.class))).thenReturn(null);

        List<Order> response = orderService.searchOrders("orderId", new ServiceErrorsImpl());
        Mockito.verify(avaloqExecute).executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class),
                Mockito.any(Class.class), Mockito.any(ServiceErrors.class));
        assertThat(response.size(), is(0));
    }

    @Test
    public void testSearchOrders_whenNullResponseOrders_thenEmptyListReturned() throws Exception {
        OrderResponseImpl orderResponse = new OrderResponseImpl();
        Mockito.when(avaloqExecute.executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class), Mockito.any(Class.class),
                Mockito.any(ServiceErrors.class))).thenReturn(orderResponse);

        List<Order> response = orderService.searchOrders("orderId", new ServiceErrorsImpl());
        Mockito.verify(avaloqExecute).executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class),
                Mockito.any(Class.class), Mockito.any(ServiceErrors.class));
        assertThat(response.size(), is(0));
    }

    @Test
    public void testLoadInProgressOrders_whenValidRequest_thenOrdersReturned() throws Exception {
        List<OrderInProgress> orders = new ArrayList<>();
        orders.add(new OrderInProgressImpl());
        OrderInProgressResponseImpl orderInProgressResponse = Mockito.mock(OrderInProgressResponseImpl.class);
        Mockito.when(orderInProgressResponse.getOrders()).thenReturn(orders);
        Mockito.when(avaloqExecute.executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class), Mockito.any(Class.class),
                Mockito.any(ServiceErrors.class))).thenReturn(orderInProgressResponse);

        List<OrderInProgress> response = orderService.loadInProgressOrders("orderId", new ServiceErrorsImpl());
        Mockito.verify(avaloqExecute).executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class),
                Mockito.any(Class.class), Mockito.any(ServiceErrors.class));
        Mockito.verify(validator).validate(Mockito.anyCollection(), any(ServiceErrors.class));
        assertThat(response.size(), is(1));
    }

    @Test
    public void testLoadInProgressOrders_whenNullResponse_thenEmptyListReturned() throws Exception {
        Mockito.when(avaloqExecute.executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class), Mockito.any(Class.class),
                Mockito.any(ServiceErrors.class))).thenReturn(null);

        List<OrderInProgress> response = orderService.loadInProgressOrders("orderId", new ServiceErrorsImpl());
        Mockito.verify(avaloqExecute).executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class),
                Mockito.any(Class.class), Mockito.any(ServiceErrors.class));
        assertThat(response.size(), is(0));
    }

    @Test
    public void testLoadInProgressOrders_whenNullResponseOrders_thenEmptyListReturned() throws Exception {
        Mockito.when(avaloqExecute.executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class), Mockito.any(Class.class),
                Mockito.any(ServiceErrors.class))).thenReturn(new OrderInProgressResponseImpl());

        List<OrderInProgress> response = orderService.loadInProgressOrders("orderId", new ServiceErrorsImpl());
        Mockito.verify(avaloqExecute).executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class),
                Mockito.any(Class.class), Mockito.any(ServiceErrors.class));
        assertThat(response.size(), is(0));
    }

    @Test
    public void testLoadTransactionData_whenValidRequest_thenOrdersReturned() throws Exception {
        List<OrderTransaction> orders = new ArrayList<>();
        orders.add(new OrderTransactionImpl());
        OrderTransactionResponseImpl orderTransactionResponse = Mockito.mock(OrderTransactionResponseImpl.class);
        Mockito.when(orderTransactionResponse.getOrderTransactions()).thenReturn(orders);
        Mockito.when(avaloqExecute.executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class), Mockito.any(Class.class),
                Mockito.any(ServiceErrors.class))).thenReturn(orderTransactionResponse);

        List<OrderTransaction> response = orderService.loadTransactionData("orderId", new ServiceErrorsImpl());
        Mockito.verify(avaloqExecute).executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class),
                Mockito.any(Class.class), Mockito.any(ServiceErrors.class));
        Mockito.verify(validator).validate(Mockito.anyCollection(), any(ServiceErrors.class));
        assertThat(response.size(), is(1));
    }

    @Test
    public void testLoadTransactionData_whenNullResponse_thenEmptyListReturned() throws Exception {
        Mockito.when(avaloqExecute.executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class), Mockito.any(Class.class),
                Mockito.any(ServiceErrors.class))).thenReturn(null);

        List<OrderTransaction> response = orderService.loadTransactionData("orderId", new ServiceErrorsImpl());
        Mockito.verify(avaloqExecute).executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class),
                Mockito.any(Class.class), Mockito.any(ServiceErrors.class));
        assertThat(response.size(), is(0));
    }

    @Test
    public void testLoadTransactionData_whenNullResponseOrders_thenEmptyListReturned() throws Exception {
        Mockito.when(avaloqExecute.executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class), Mockito.any(Class.class),
                Mockito.any(ServiceErrors.class))).thenReturn(new OrderTransactionResponseImpl());

        List<OrderTransaction> response = orderService.loadTransactionData("orderId", new ServiceErrorsImpl());
        Mockito.verify(avaloqExecute).executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class),
                Mockito.any(Class.class), Mockito.any(ServiceErrors.class));
        assertThat(response.size(), is(0));
    }

    @Test
    public void testCancelOrder_whenValidRequest_thenServiceCalled() throws Exception {
        Mockito.when(webserviceClient.sendToWebService(Mockito.anyObject(), any(AvaloqOperation.class), any(ServiceErrors.class)))
                .thenReturn(new com.btfin.abs.trxservice.canceldoc.v1_0.CancelDocRsp());
        Mockito.when(
                orderCancelConverter.toOrderCancelRequest(any(BigInteger.class), any(BigInteger.class), any(ServiceErrors.class)))
                .thenReturn(new com.btfin.abs.trxservice.canceldoc.v1_0.CancelDocRsp());
        Mockito.doNothing().when(orderCancelConverter)
                .processCancelResponse(any(com.btfin.abs.trxservice.canceldoc.v1_0.CancelDocRsp.class));

        orderService.cancelOrder(BigInteger.valueOf(1234), BigInteger.valueOf(5), new ServiceErrorsImpl());
        Mockito.verify(orderCancelConverter).toOrderCancelRequest(any(BigInteger.class), any(BigInteger.class),
                any(ServiceErrors.class));
        Mockito.verify(orderCancelConverter)
                .processCancelResponse(any(com.btfin.abs.trxservice.canceldoc.v1_0.CancelDocRsp.class));
    }

    @Test
    public void testSaveOrderGroup_whenValidRequest_thenServiceCalled() throws Exception {
        Mockito.when(webserviceClient.sendToWebService(Mockito.anyObject(), any(AvaloqOperation.class), any(ServiceErrors.class)))
                .thenReturn(new com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp());
        Mockito.when(orderGroupConverter.toOrderSaveRequest(any(OrderGroup.class), any(ServiceErrors.class)))
                .thenReturn(new com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlReq());
        Mockito.when(orderGroupConverter.toSaveOrderResponse(Mockito.anyString(), any(BigInteger.class), any(AccountKey.class),
                any(com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp.class), any(ServiceErrors.class)))
                .thenReturn(new OrderGroupImpl());

        orderService.saveOrderGroup(new OrderGroupImpl(), new ServiceErrorsImpl());
        Mockito.verify(webserviceClient).sendToWebService(Mockito.anyObject(), any(AvaloqOperation.class),
                any(ServiceErrors.class));
        Mockito.verify(orderGroupConverter).toOrderSaveRequest(any(OrderGroup.class), any(ServiceErrors.class));
        Mockito.verify(orderGroupConverter).toSaveOrderResponse(Mockito.anyString(), any(BigInteger.class), any(AccountKey.class),
                any(com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp.class), any(ServiceErrors.class));
    }

    @Test
    public void testDeleteOrderGroup_whenValidRequest_thenServiceCalled() throws Exception {
        Mockito.when(webserviceClient.sendToWebService(Mockito.anyObject(), any(AvaloqOperation.class), any(ServiceErrors.class)))
                .thenReturn(new com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp());
        Mockito.when(orderGroupConverter.toOrderDeleteRequest(Mockito.anyString(), any(ServiceErrors.class)))
                .thenReturn(new com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlReq());
        Mockito.doNothing().when(orderGroupConverter)
                .processDeleteResponse(any(com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp.class), any(ServiceErrors.class));

        orderService.deleteOrderGroup("orderId", new ServiceErrorsImpl());
        Mockito.verify(webserviceClient).sendToWebService(Mockito.anyObject(), any(AvaloqOperation.class),
                any(ServiceErrors.class));
        Mockito.verify(orderGroupConverter).toOrderDeleteRequest(Mockito.anyString(), any(ServiceErrors.class));
        Mockito.verify(orderGroupConverter).processDeleteResponse(any(com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp.class),
                any(ServiceErrors.class));
    }

    @Test
    public void testValidateOrderGroup_whenValidRequest_thenServiceCalled() throws Exception {
        Mockito.when(webserviceClient.sendToWebService(Mockito.anyObject(), any(AvaloqOperation.class), any(ServiceErrors.class)))
                .thenReturn(new com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp());
        Mockito.when(orderGroupConverter.toOrderValidateRequest(any(OrderGroup.class), any(ServiceErrors.class)))
                .thenReturn(new com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlReq());
        Mockito.when(orderGroupConverter.toValidateOrderResponse(Mockito.anyString(), any(BigInteger.class),
                any(AccountKey.class), any(com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp.class), any(ServiceErrors.class)))
                .thenReturn(new OrderGroupImpl());

        orderService.validateOrderGroup(new OrderGroupImpl(), new ServiceErrorsImpl());
        Mockito.verify(webserviceClient).sendToWebService(Mockito.anyObject(), any(AvaloqOperation.class),
                any(ServiceErrors.class));
        Mockito.verify(orderGroupConverter).toOrderValidateRequest(any(OrderGroup.class), any(ServiceErrors.class));
        Mockito.verify(orderGroupConverter).toValidateOrderResponse(Mockito.anyString(), any(BigInteger.class),
                any(AccountKey.class), any(com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp.class), any(ServiceErrors.class));
    }

    @Test
    public void testSubmitOrderGroup_whenValidRequest_thenServiceCalled() throws Exception {
        Mockito.when(webserviceClient.sendToWebService(Mockito.anyObject(), any(AvaloqOperation.class), any(ServiceErrors.class)))
                .thenReturn(new com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp());
        Mockito.when(orderGroupConverter.toOrderSubmitRequest(any(OrderGroup.class), any(ServiceErrors.class)))
                .thenReturn(new com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlReq());
        Mockito.when(orderGroupConverter.toSubmitOrderResponse(Mockito.anyString(), any(BigInteger.class), any(AccountKey.class),
                any(com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp.class), any(ServiceErrors.class)))
                .thenReturn(new OrderGroupImpl());

        orderService.submitOrderGroup(new OrderGroupImpl(), new ServiceErrorsImpl());
        Mockito.verify(webserviceClient).sendToWebService(Mockito.anyObject(), any(AvaloqOperation.class),
                any(ServiceErrors.class));
        Mockito.verify(orderGroupConverter).toOrderSubmitRequest(any(OrderGroup.class), any(ServiceErrors.class));
        Mockito.verify(orderGroupConverter).toSubmitOrderResponse(Mockito.anyString(), any(BigInteger.class),
                any(AccountKey.class), any(com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp.class), any(ServiceErrors.class));
    }

    @Test
    public void testUpdateStexOrder_whenValidRequest_thenServiceCalled() throws Exception {
        Mockito.when(webserviceClient.sendToWebService(Mockito.anyObject(), any(AvaloqOperation.class), any(ServiceErrors.class)))
                .thenReturn(new com.btfin.abs.trxservice.stex.v1_0.StexRsp());
        Mockito.when(updateOrderConverter.toUpdateOrderRequest(any(Order.class)))
                .thenReturn(new com.btfin.abs.trxservice.stex.v1_0.StexReq());
        Mockito.when(updateOrderConverter.toValidationErrors(any(com.btfin.abs.trxservice.stex.v1_0.StexRsp.class)))
                .thenReturn(new ArrayList<ValidationError>());

        orderService.updateStexOrder(new OrderImpl(), new ServiceErrorsImpl());
        Mockito.verify(webserviceClient).sendToWebService(Mockito.anyObject(), any(AvaloqOperation.class),
                any(ServiceErrors.class));
        Mockito.verify(updateOrderConverter).toUpdateOrderRequest(any(Order.class));
        Mockito.verify(updateOrderConverter).toValidationErrors(any(com.btfin.abs.trxservice.stex.v1_0.StexRsp.class));
    }

    @Test
    public void testUpdateStexOrder_whenFatalError_thenServiceErrorsReturned() throws Exception {
        Mockito.when(webserviceClient.sendToWebService(Mockito.anyObject(), any(AvaloqOperation.class), any(ServiceErrors.class)))
                .thenReturn(new com.btfin.abs.trxservice.stex.v1_0.StexRsp());
        Mockito.when(updateOrderConverter.toUpdateOrderRequest(any(Order.class)))
                .thenReturn(new com.btfin.abs.trxservice.stex.v1_0.StexReq());

        List<ValidationError> validations = new ArrayList<ValidationError>();
        validations.add(new ValidationError("errorId", "field", "message", ErrorType.FATAL));
        Mockito.when(updateOrderConverter.toValidationErrors(any(com.btfin.abs.trxservice.stex.v1_0.StexRsp.class)))
                .thenReturn(validations);

        ServiceErrorsImpl errors = new ServiceErrorsImpl();
        orderService.updateStexOrder(new OrderImpl(), errors);
        assertThat(errors.getErrors().size(), is(1));
    }

    @Test
    public void testUpdateStexOrder_whenValidationsAndNoServiceErrors_thenValidationException() throws Exception {
        Mockito.when(webserviceClient.sendToWebService(Mockito.anyObject(), any(AvaloqOperation.class), any(ServiceErrors.class)))
                .thenReturn(new com.btfin.abs.trxservice.stex.v1_0.StexRsp());
        Mockito.when(updateOrderConverter.toUpdateOrderRequest(any(Order.class)))
                .thenReturn(new com.btfin.abs.trxservice.stex.v1_0.StexReq());

        List<ValidationError> validations = new ArrayList<ValidationError>();
        validations.add(new ValidationError("field", "message"));
        Mockito.when(updateOrderConverter.toValidationErrors(any(com.btfin.abs.trxservice.stex.v1_0.StexRsp.class)))
                .thenReturn(validations);

        try {
            orderService.updateStexOrder(new OrderImpl(), new ServiceErrorsImpl());
        } catch (ValidationException e) {
            assert (true);
            return;
        }
        fail("ValidationException not thrown");
    }
}
