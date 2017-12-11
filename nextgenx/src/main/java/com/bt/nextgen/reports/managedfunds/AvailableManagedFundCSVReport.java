package com.bt.nextgen.reports.managedfunds;

import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;

import java.util.Map;

/**
 * Created by l078480 on 2/03/2017.
 */
@Report("availableManagedFundCsvReport")
public class AvailableManagedFundCSVReport extends AvailableManagedFundReport {

    @ReportBean("disclaimerForCsv")
    public String disclaimerForCsv(Map<String, String> params) {
        StringBuilder dealerBuilder=new StringBuilder();
        dealerBuilder.append("Disclaimer: ").append((getDisclaimer(params).replace("<p>","")).replace("</p>",""));
        return dealerBuilder.toString();
    }
}
