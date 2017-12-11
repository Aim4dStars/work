package com.bt.nextgen.api.subscriptions.service;


import ch.lambdaj.Lambda;
import com.bt.nextgen.api.draftaccount.service.OrderType;
import com.bt.nextgen.api.subscriptions.model.Offer;

import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Enumeration provides available service (offers) provided by platform.
 * This should have coming form repository - (CMS, Avaloq or Database), may be in future. Current solution
 * does not provides any information about services (platform offerings).
 * Warning: do no use this to get the detail of the subscription, this class meant to be repository service
 */
public enum Subscriptions {
    FA("Fund administration", "opn_new_fa_aw_docm", "BT Fund Administration", OrderType.FundAdmin.getOrderType());
    private String name;
    private String workFlowType;
    private String productType;
    private String orderType;
    private static List<Offer> offers;

    Subscriptions(String name, String workFlow, String productType, String orderType) {
        this.name = name;
        this.workFlowType = workFlow;
        this.productType = productType;
        this.orderType = orderType;
    }

    public String getType() {
        return this.toString();
    }

    /**
     * Name of the Subscription
     *
     * @return name of the subscription
     */
    public String getName() {
        return name;
    }

    /**
     * Work type to intiate the subscription
     *
     * @return unique work flow id
     */
    public String getWorkFlowType() {
        return workFlowType;
    }

    /**
     * Product type of this subscription
     *
     * @return type of the product
     */
    public String getProductType() {
        return productType;
    }

    /**
     * @return
     */
    public String getOrderType() {
        return orderType;
    }

    /**
     * Bean for the offer
     *
     * @return offer detail bean
     */
    public Offer getOffer() {
        return new Offer(this);
    }

    /**
     * return offer object from work Flow Type
     * @param workFlowType
     * @return
     */
    public static Offer getOffer(String workFlowType) {
        return selectFirst(getOffers(), having(on(Offer.class).workFlowName(), equalTo(workFlowType)));
    }

    /**
     * Returns all the offers available to plateform
     *
     * @return list of offers
     */
    public static List<Offer> getOffers() {
        if (offers == null) {
            offers = Lambda.extractProperty(Subscriptions.values(), "offer");
        }
        return offers;
    }




    public static Offer getOfferFromOderType(String orderType) {
        return selectFirst(getOffers(), having(on(Offer.class).getOderType(), equalTo(orderType)));
    }
}