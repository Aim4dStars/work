package com.bt.nextgen.api.subscriptions.service;

import com.bt.nextgen.api.subscriptions.model.Offer;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class SubscriptionsTest {

    @Test
    public void testGetServiceOffers() {
        List<Offer> services = Subscriptions.getOffers();
        assertTrue(services.contains(Subscriptions.FA.getOffer()));
    }

    @Test
    public void tetGetType() {
        assertEquals("FA", Subscriptions.FA.getType());
    }

    @Test
    public void tetGetOfferFromWorkFlowName() {
        Offer offer = Subscriptions.getOffer(Subscriptions.FA.getWorkFlowType());
        assertEquals("Fund administration",offer.getName() );
    }

    @Test
    public void testGetOfferFromOderType() {
        Offer offer = Subscriptions.getOfferFromOderType(Subscriptions.FA.getOrderType());
        assertEquals("Fund administration",offer.getName() );
    }

}