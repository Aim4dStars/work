package com.bt.nextgen.service.avaloq.modelportfolio.rebalance;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.client.BrokerKeyConverter;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceOrderDetails;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceOrderGroup;
import org.joda.time.DateTime;

import java.util.List;

@ServiceBean(xpath = "rebal_det_head")
public class RebalanceOrderGroupImpl implements RebalanceOrderGroup {

    @ServiceElement(xpath = "model_name/val")
    private String modelName;

    @ServiceElement(xpath = "model_sym/val")
    private String modelSymbol;

    @ServiceElement(xpath = "avsr_id/val", converter = BrokerKeyConverter.class)
    private BrokerKey adviser;

    @ServiceElement(xpath = "rebal_date/val", converter = DateTimeTypeConverter.class)
    private DateTime rebalanceDate;

    @ServiceElementList(xpath = "det_list/det", type = RebalanceOrderDetailsImpl.class)
    private List<RebalanceOrderDetails> orderDetails;

    @ServiceElement(xpath = "rebal_det_doc_id/val")
    private String rebalDetDocId;

    @Override
    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @Override
    public String getModelSymbol() {
        return modelSymbol;
    }

    public void setModelSymbol(String modelSymbol) {
        this.modelSymbol = modelSymbol;
    }

    @Override
    public BrokerKey getAdviser() {
        return adviser;
    }

    public void setAdviser(BrokerKey adviser) {
        this.adviser = adviser;
    }

    @Override
    public DateTime getRebalanceDate() {
        return rebalanceDate;
    }

    public void setRebalanceDate(DateTime rebalanceDate) {
        this.rebalanceDate = rebalanceDate;
    }

    @Override
    public List<RebalanceOrderDetails> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<RebalanceOrderDetails> orderDetails) {
        this.orderDetails = orderDetails;
    }

    @Override
    public String getRebalDetDocId() {
        return rebalDetDocId;
    }

    public void setRebalDetDocId(String rebalDetDocId) {
        this.rebalDetDocId = rebalDetDocId;
    }

}