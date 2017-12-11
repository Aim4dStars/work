package com.bt.nextgen.service.avaloq.gateway.businessunit;

import com.bt.nextgen.payments.domain.PayeeType;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: l053474
 * Date: 18/09/13
 * Time: 9:21 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Payee {

    public String getName() ;

    public String getNickname() ;

    public String getCode() ;

    public String getReference() ;

    public String getCurrency() ;

    public BigDecimal getLimit() ;

    public boolean isPrimary() ;

    public PayeeType getPayeeType() ;
}
