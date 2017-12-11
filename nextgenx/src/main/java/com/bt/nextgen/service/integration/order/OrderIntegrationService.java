package com.bt.nextgen.service.integration.order;

import com.bt.nextgen.core.web.model.SearchParameters;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.broker.BrokerKey;

import java.math.BigInteger;
import java.util.List;

/**
 * Interface to load all of the details of an order
 */
public interface OrderIntegrationService {
    /**
     * Loads order groups for a single acccount id.
     * 
     * @param accountId
     *            - plain text avaloq id of the account to load.
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during
     *            loading of the order groups from the end point.
     * @return The collection of order groups for an account, if no order groups
     *         for the account are found then an empty list is returned.
     */
    public List<OrderGroup> loadOrderGroups(String accountId, ServiceErrors serviceErrors);

    /**
     * Loads all order groups based on OE position Ids.
     * 
     * @param brokerKeys
     *            - Input parameter which use to pass User's OE ids.
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during
     *            loading of the order groups from the end point.
     * @return The collection of all order groups, if no order groups are found
     *         then an empty list is returned.
     */
    public List<OrderGroup> loadOrderGroups(List<BrokerKey> brokerKeys, ServiceErrors serviceErrors);

    /**
     * Cancel the order (change status to cancelled) for the specified order id.
     * 
     * @param order
     *            Id - avaloq id of the order to be cancelled.
     * @param trans
     *            seq id - avaloq trans id of the order to be cancelled.
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during
     *            change status of the order from the end point.
     */
    public void cancelOrder(BigInteger orderId, BigInteger tranSeqId, ServiceErrors serviceErrors);

    /**
     * Loads an indiviual order group. Only the details of the order are
     * returned, warnings and errors for the group are not.
     * 
     * @param order
     *            Id - avaloq id of the order to be cancelled.
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during
     *            change status of the order from the end point.
     */
    public OrderGroup loadOrderGroup(AccountKey accountKey, String orderId, ServiceErrors serviceErrors);

    /**
     * Deletes an order group. If the order cannot be deleted then an
     * ValidationException is thrown.
     * 
     * @param order
     *            Id - avaloq id of the order to be cancelled.
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during
     *            change status of the order from the end point.
     */
    public void deleteOrderGroup(String orderId, ServiceErrors serviceErrors);

    /**
     * Submits an order group. If warnings are present then the warnings list of
     * the order group is populated. If errors or fatals are prestent then
     * ValidationExceptions are thrown and the order is not submitted.
     * 
     * @param order
     *            Id - avaloq id of the order to be cancelled.
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during
     *            change status of the order from the end point.
     */
    public OrderGroup submitOrderGroup(OrderGroup orderGroup, ServiceErrors serviceErrors);

    /**
     * Validates an order group. If warnings are present then the warnings list
     * of the order group is populated. If errors or fatals are prestent then
     * ValidationExceptions are thrown.
     * 
     * @param order
     *            Id - avaloq id of the order to be cancelled.
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during
     *            change status of the order from the end point.
     */
    public OrderGroup validateOrderGroup(OrderGroup orderGroup, ServiceErrors serviceErrors);

    /**
     * Saves an indiviual order group. Only the details of the order are
     * returned, warnings and errors for the group are not.
     * 
     * @param order
     *            Id - avaloq id of the order to be cancelled.
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during
     *            change status of the order from the end point.
     */
    public OrderGroup saveOrderGroup(OrderGroup orderGroup, ServiceErrors serviceErrors);

    /**
     * Loads orders for the current user. Search paramaters can be used to
     * restrict the date range
     * 
     * @param search
     *            - search params to load the orders for: from date and to date.
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during
     *            loading of the orders from the end point.
     * @return The collection of orders. If no orders are found for the search
     *         params then an empty list is returned.
     */
    public List<Order> loadOrders(SearchParameters search, ServiceErrors serviceErrors);

    /**
     * Loads a specified order
     * 
     * @param search
     *            - search params to load the orders for: account id, order id,
     *            from date and to date.
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during
     *            loading of the orders from the end point.
     * @return The collection of orders. If no orders are found for the search
     *         params then an empty list is returned.
     */
    public List<Order> loadOrder(String orderId, ServiceErrors serviceErrors);

    /**
     * Loads orders in progress orders for the current user
     * 
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during
     *            loading of the orders from the end point.
     * @return The collection of orders. If no orders are found for the search
     *         params then an empty list is returned.
     */
    public List<OrderInProgress> loadInProgressOrders(String accountId, ServiceErrors serviceErrors);

    /**
     * Update a LS order. Includes cancellation.
     * 
     * @param order
     *            the order to be updated.
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during
     *            change status of the order from the end point.
     */
    public void updateStexOrder(Order order, ServiceErrors serviceErrors);

    /**
     * load transaction data for the specified order id.
     * 
     * @param order
     *            Id - avaloq id of the order to be cancelled.
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during
     * 
     * @return The collection of order transactions
     */
    public List<OrderTransaction> loadTransactionData(String orderId, ServiceErrors serviceErrors);
    
    /**
     * load all orders for the order id or external ref id
     * 
     * @param order
     *            Id - avaloq id or external ref id of the order
     *            
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during
     * 
     * @return The collection of orders. If no orders are found for the order id or external ref id
     *         then an empty list is returned.
     */
    public List<Order> searchOrders(String orderId, ServiceErrors serviceErrors);
    
}
