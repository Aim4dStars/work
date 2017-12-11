package com.bt.nextgen.service.integration.modelportfolio.orderstatus;

import com.bt.nextgen.service.integration.order.ExpiryMethod;
import com.bt.nextgen.service.integration.order.OrderStatus;
import com.bt.nextgen.service.integration.transactionfee.ExecutionType;
import com.btfin.panorama.service.integration.order.OrderType;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public interface ModelOrderDetails {

    public String getAssetCode();

    public String getAssetId();
    
    public String getAssetName();

    public String getIpsId();

    public String getIpsName();

    public String getIpsKey();

    public String getAccountNumber();

    public String getAccountName();

    public String getDocId();

    public OrderType getOrderType();

    public ExecutionType getExecType();

    public ExpiryMethod getExpiryType();

    public BigDecimal getOriginalQuantity();

    public BigDecimal getFillQuantity();

    public OrderStatus getStatus();
    
    public DateTime getOrderDate();

    public DateTime getTransactionDate();

    public DateTime getExpiryDate();

    public BigDecimal getNetAmount();

    public BigDecimal getEstimatedPrice();

    public BigDecimal getBrokerage();

    public BigDecimal getRemainingQuantity();

    public String getAdviserName();

    public String getDealerName();

}
