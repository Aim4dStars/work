package com.bt.nextgen.reports.modelportfolio.orderstatus;

import com.bt.nextgen.api.modelportfolio.v2.model.orderstatus.ModelOrderDetailsDto;
import org.joda.time.DateTime;

import java.util.List;

public class ModelOrderReportData {
    
    private DateTime reportDate;
    private List<ModelOrderDetailsDto> orderDetails;

    public ModelOrderReportData(DateTime reportDate, List<ModelOrderDetailsDto> orderDetails) {
        this.reportDate = reportDate;
        this.orderDetails = orderDetails;
    }

    public List<ModelOrderDetailsDto> getOrderDetails() {
        return orderDetails;
    }

    public DateTime getReportDate() {
        return reportDate;
    }

}
