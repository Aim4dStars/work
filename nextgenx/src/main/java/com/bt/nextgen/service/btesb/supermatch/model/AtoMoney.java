package com.bt.nextgen.service.btesb.supermatch.model;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;

import java.math.BigDecimal;

/**
 * Money items for the monies held by ATO
 */
@ServiceBean(xpath = "/", type = ServiceBeanType.CONCRETE)
public class AtoMoney {

    @ServiceElement(xpath = "Balance", converter = BigDecimalConverter.class)
    private BigDecimal balance;

    @ServiceElement(xpath = "local-name(Category/..)")
    private String category;

    public BigDecimal getBalance() {
        return balance;
    }

    public String getCategory() {
        return category;
    }
}
