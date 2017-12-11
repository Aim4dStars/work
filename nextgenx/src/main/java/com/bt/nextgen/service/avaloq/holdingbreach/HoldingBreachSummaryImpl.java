package com.bt.nextgen.service.avaloq.holdingbreach;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.integration.holdingbreach.HoldingBreach;
import com.bt.nextgen.service.integration.holdingbreach.HoldingBreachSummary;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

@ServiceBean(xpath = "/", type = ServiceBeanType.CONCRETE)
public class HoldingBreachSummaryImpl implements HoldingBreachSummary {
    @ServiceElement(xpath = "//data/top/top_head_list/top_head/eval_date/val", converter = DateTimeTypeConverter.class)
    private DateTime reportDate;

    @ServiceElementList(xpath = "//data/top/oe_list/oe/bp_list/bp", type = HoldingBreachImpl.class)
    private List<HoldingBreach> holdingBreaches;

    @Override
    public DateTime getReportDate() {
        return reportDate;
    }

    public void setReportDate(DateTime reportDate) {
        this.reportDate = reportDate;
    }

    @Override
    public List<HoldingBreach> getHoldingBreaches() {
        return holdingBreaches != null ? holdingBreaches : new ArrayList<HoldingBreach>();
    }

    public void setHoldingBreaches(List<HoldingBreach> holdingBreaches) {
        this.holdingBreaches = holdingBreaches;
    }
}
