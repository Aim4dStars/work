package com.bt.nextgen.service.avaloq.order;

import ch.lambdaj.Lambda;
import com.bt.nextgen.core.domain.key.StringIdKey;
import com.bt.nextgen.core.exception.ValidationException;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.core.validation.ValidationError.ErrorType;
import com.bt.nextgen.core.web.model.SearchCriteria;
import com.bt.nextgen.core.web.model.SearchParameters;
import com.bt.nextgen.core.web.model.SearchParametersImpl;
import com.bt.nextgen.core.web.model.SearchParams;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.order.Order;
import com.bt.nextgen.service.integration.order.OrderGroup;
import com.bt.nextgen.service.integration.order.OrderInProgress;
import com.bt.nextgen.service.integration.order.OrderIntegrationService;
import com.bt.nextgen.service.integration.order.OrderTransaction;
import com.btfin.panorama.core.validation.Validator;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service("avaloqOrderIntegrationService")
public class AvaloqOrderIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements OrderIntegrationService {
    @Autowired
    private AvaloqExecute avaloqExecute;

    @Autowired
    private Validator validator;

    @Autowired
    private OrderGroupReportConverter orderGroupReportConverter;

    @Autowired
    private OrderGroupConverter orderGroupConverter;

    @Autowired
    @Qualifier("orderCancelConverter")
    private OrderCancelConverter orderCancelConverter;

    @Autowired
    private UpdateOrderConverter updateOrderConverter;

    @Autowired
    private AvaloqGatewayHelperService webserviceClient;

    @Override
    /**
     * {@inheritDoc}
     */
    public List<OrderGroup> loadOrderGroups(final String accountId, final ServiceErrors serviceErrors) {
        final List<OrderGroup> savedOrders = new ArrayList<>();
        new IntegrationOperation("loadOrderGroups", serviceErrors) {
            @Override
            public void performOperation() {
                com.avaloq.abs.screen_rep.hira.btfg$ui_doc_list_doc.Rep report = avaloqExecute
                        .executeReportRequest(new AvaloqReportRequest(Template.ORDER_GROUP.getName()).forAccount(accountId));
                List<OrderGroupImpl> orderGroups = orderGroupReportConverter.toModel(report, serviceErrors);

                for (OrderGroup orderGroup : orderGroups) {
                    savedOrders.add(orderGroup);
                }

                validator.validate(savedOrders, serviceErrors);
            }

        }.run();

        return savedOrders;
    }

    /**
     * Loads all order groups based on OE position Ids.
     *
     * @param brokerKeys
     *            - Input parameter which use to pass User's OE ids.
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during loading of the order groups from the end point.
     * @return The collection of all order groups, if no order groups are found then an empty list is returned.
     */
    @Override
    public List<OrderGroup> loadOrderGroups(final List<BrokerKey> brokerKeys, final ServiceErrors serviceErrors) {
        final List<OrderGroup> savedOrders = new ArrayList<>();
        new IntegrationOperation("loadOrderGroups", serviceErrors) {
            @Override
            public void performOperation() {
                List<String> oeIds = Lambda.collect(brokerKeys, Lambda.on(StringIdKey.class).getId());
                com.avaloq.abs.screen_rep.hira.btfg$ui_doc_list_doc.Rep report = avaloqExecute
                        .executeReportRequest(new AvaloqReportRequest(Template.ADVISER_ORDER_GROUP.getName()).forOeIds(oeIds));
                List<OrderGroupImpl> orderGroups = orderGroupReportConverter.toModel(report, serviceErrors);

                for (OrderGroup orderGroup : orderGroups) {
                    savedOrders.add(orderGroup);
                }

                validator.validate(savedOrders, serviceErrors);
            }
        }.run();

        return savedOrders;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public OrderGroup saveOrderGroup(final OrderGroup orderGroup, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<OrderGroup>("saveOrderGroup", serviceErrors) {
            @Override
            public OrderGroup performOperation() {
                com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp orderRsp = webserviceClient.sendToWebService(
                        orderGroupConverter.toOrderSaveRequest(orderGroup, serviceErrors), AvaloqOperation.TRX_BDL_REQ,
                        serviceErrors);
                OrderGroupImpl response = orderGroupConverter.toSaveOrderResponse(orderGroup.getOrderGroupId(),
                        orderGroup.getTransactionSeq(),
                        orderGroup.getAccountKey(), orderRsp, serviceErrors);
                return response;
            }
        }.run();
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public OrderGroup validateOrderGroup(final OrderGroup orderGroup, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<OrderGroup>("validateOrderGroup", serviceErrors) {
            @Override
            public OrderGroup performOperation() {
                com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlReq orderReq = orderGroupConverter.toOrderValidateRequest(orderGroup,
                        serviceErrors);
                com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp orderRsp = webserviceClient.sendToWebService(orderReq,
                        AvaloqOperation.TRX_BDL_REQ, serviceErrors);
                OrderGroupImpl response = orderGroupConverter.toValidateOrderResponse(orderGroup.getOrderGroupId(),
                        orderGroup.getTransactionSeq(), orderGroup.getAccountKey(), orderRsp, serviceErrors);
                return response;
            }
        }.run();
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public OrderGroup submitOrderGroup(final OrderGroup orderGroup, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<OrderGroup>("submitOrderGroup", serviceErrors) {
            @Override
            public OrderGroup performOperation() {
                com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlReq orderReq = orderGroupConverter.toOrderSubmitRequest(orderGroup,
                        serviceErrors);
                com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp orderRsp = webserviceClient.sendToWebService(orderReq,
                        AvaloqOperation.TRX_BDL_REQ, serviceErrors);
                OrderGroupImpl response = orderGroupConverter.toSubmitOrderResponse(orderGroup.getOrderGroupId(),
                        orderGroup.getTransactionSeq(),
                        orderGroup.getAccountKey(), orderRsp, serviceErrors);
                return response;
            }
        }.run();
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void deleteOrderGroup(final String orderId, final ServiceErrors serviceErrors) {
        new IntegrationOperation("deleteOrderGroup", serviceErrors) {
            @Override
            public void performOperation() {
                com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp orderRsp = webserviceClient.sendToWebService(
                        orderGroupConverter.toOrderDeleteRequest(orderId, serviceErrors), AvaloqOperation.TRX_BDL_REQ,
                        serviceErrors);
                orderGroupConverter.processDeleteResponse(orderRsp, serviceErrors);
            }
        }.run();
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public OrderGroup loadOrderGroup(final AccountKey accountKey, final String orderId, final ServiceErrors serviceErrors) {

        return new IntegrationSingleOperation<OrderGroup>("loadOrderGroup", serviceErrors) {
            @Override
            public OrderGroup performOperation() {
                com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp orderRsp = webserviceClient.sendToWebService(
                        orderGroupConverter.toOrderLoadRequest(orderId, serviceErrors), AvaloqOperation.TRX_BDL_REQ,
                        serviceErrors);
                return orderGroupConverter.toLoadOrderResponse(orderId, null, accountKey, orderRsp, serviceErrors);
            }
        }.run();
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void cancelOrder(final BigInteger orderId, final BigInteger lastTranSeqId, final ServiceErrors serviceErrors) {
        new IntegrationOperation("cancelOrder", serviceErrors) {
            @Override
            public void performOperation() {
                com.btfin.abs.trxservice.canceldoc.v1_0.CancelDocRsp response = webserviceClient.sendToWebService(
                        orderCancelConverter.toOrderCancelRequest(orderId, lastTranSeqId, serviceErrors),
                        AvaloqOperation.CANCEL_DOC_REQ, serviceErrors);
                orderCancelConverter.processCancelResponse(response);
            }

        }.run();
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public void updateStexOrder(final Order order, final ServiceErrors serviceErrors) {
        new IntegrationOperation("cancelStexOrder", serviceErrors) {
            @Override
            public void performOperation() {
                com.btfin.abs.trxservice.stex.v1_0.StexRsp response = webserviceClient.sendToWebService(
                        updateOrderConverter.toUpdateOrderRequest(order), AvaloqOperation.STEX_REQ, serviceErrors);
                List<ValidationError> validations = updateOrderConverter.toValidationErrors(response);
                for (ValidationError validation : validations) {
                    if (ErrorType.FATAL.equals(validation.getType())) {
                        serviceErrors
                                .addError(new ServiceErrorImpl("Update order return a fatal error: " + validation.getMessage()));
                    }
                }
                if (!serviceErrors.hasErrors() && !validations.isEmpty()) {
                    throw new ValidationException(validations, "Update order failed validation.");
                }
            }

        }.run();
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public List<Order> loadOrders(final SearchParameters search, final ServiceErrors serviceErrors) {
        final List<Order> orders = new ArrayList<>();
        new IntegrationOperation("loadOrders", serviceErrors) {
            @Override
            public void performOperation() {
                Template template = Template.ORDERS;

                List<SearchCriteria> criteriaList = search.getSearchCriterias();
                for (SearchCriteria criteria : criteriaList) {
                    SearchParams param = criteria.getSearchKey();
                    if (param == SearchParams.PORTFOLIO_ID) {
                        template = Template.ORDERS_ACCOUNT;
                        break;
                    }
                }

                ((SearchParametersImpl) search).setSearchFor(template);
                OrderResponseImpl response = avaloqExecute.executeReportRequestToDomain(new AvaloqReportRequest(search),
                        OrderResponseImpl.class, serviceErrors);

                if (response != null && response.getOrders() != null) {
                    orders.addAll(response.getOrders());
                    validator.validate(orders, serviceErrors);
                }
            }
        }.run();

        return orders;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public List<Order> loadOrder(final String orderId, final ServiceErrors serviceErrors) {
        final List<Order> orders = new ArrayList<>();
        new IntegrationOperation("loadOrder", serviceErrors) {
            @Override
            public void performOperation() {
                OrderResponseImpl response = avaloqExecute.executeReportRequestToDomain(
                        new AvaloqReportRequest(Template.ORDER.getName()).forDocumentIdList(Collections.singletonList(orderId)),
                        OrderResponseImpl.class, serviceErrors);

                if (response != null && response.getOrders() != null) {
                    orders.addAll(response.getOrders());
                    validator.validate(orders, serviceErrors);
                }
            }
        }.run();

        return orders;
    }
    
    @Override
    /**
     * {@inheritDoc}
     */
    public List<Order> searchOrders(final String orderId, final ServiceErrors serviceErrors) {
        final List<Order> orders = new ArrayList<>();
        new IntegrationOperation("searchOrders", serviceErrors) {
            @Override
            public void performOperation() {
                OrderResponseImpl response = avaloqExecute.executeReportRequestToDomain(
                        new AvaloqReportRequest(Template.ORDER.getName()).forDocumentIdList(Collections.singletonList(orderId))
                                .forExternalRefId(Collections.singletonList(orderId)),
                        OrderResponseImpl.class, serviceErrors);

                if (response != null && response.getOrders() != null) {
                    orders.addAll(response.getOrders());
                    validator.validate(orders, serviceErrors);
                }
            }
        }.run();

        return orders;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public List<OrderInProgress> loadInProgressOrders(final String accountId, final ServiceErrors serviceErrors) {
        final List<OrderInProgress> orders = new ArrayList<>();
        new IntegrationOperation("loadInProgressOrders", serviceErrors) {
            @Override
            public void performOperation() {
                OrderInProgressResponseImpl response = avaloqExecute.executeReportRequestToDomain(
                        new AvaloqReportRequest(Template.ORDERS_IN_PROGRESS.getName()).forAccount(accountId),
                        OrderInProgressResponseImpl.class, serviceErrors);

                if (response != null && response.getOrders() != null) {
                    orders.addAll(response.getOrders());
                    validator.validate(orders, serviceErrors);
                }
            }
        }.run();

        return orders;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public List<OrderTransaction> loadTransactionData(final String orderId, final ServiceErrors serviceErrors) {
        final List<OrderTransaction> orderTransactions = new ArrayList<>();
        new IntegrationOperation("loadTransactionData", serviceErrors) {
            @Override
            public void performOperation() {
                OrderTransactionResponseImpl response = avaloqExecute.executeReportRequestToDomain(
                        new AvaloqReportRequest(Template.ORDERS_TRANSACTION_DATA.getName()).forDocumentIdList(
                                Collections.singletonList(orderId)),
                        OrderTransactionResponseImpl.class, serviceErrors);

                if (response != null && response.getOrderTransactions() != null) {
                    orderTransactions.addAll(response.getOrderTransactions());
                    validator.validate(orderTransactions, serviceErrors);
                }
            }
        }.run();

        return orderTransactions;
    }
}
