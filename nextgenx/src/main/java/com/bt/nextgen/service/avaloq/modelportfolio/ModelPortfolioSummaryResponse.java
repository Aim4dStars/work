package com.bt.nextgen.service.avaloq.modelportfolio;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioSummary;

import java.util.List;

@ServiceBean(xpath = "/", type = ServiceBeanType.CONCRETE)
public class ModelPortfolioSummaryResponse {

    @ServiceElementList(xpath = "//data/report/ips_list/ips[ips_head_list/ips_head/ips_sym/val != ''] | //data/report/report_foot_list/report_foot[ips_sym/val != '']", type = ModelPortfolioSummaryImpl.class)
    private List<ModelPortfolioSummary> summary;

    public List<ModelPortfolioSummary> getSummary() {
        return summary;
    }

    public void setSummary(List<ModelPortfolioSummary> summary) {
        this.summary = summary;
    }

}