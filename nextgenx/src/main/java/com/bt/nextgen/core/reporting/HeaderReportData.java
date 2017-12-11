package com.bt.nextgen.core.reporting;

import net.sf.jasperreports.engine.Renderable;

public class HeaderReportData {
    private Renderable logo;

    public HeaderReportData(Renderable logo) {
        this.logo = logo;
    }

    public Renderable getLogo() {
        return logo;
    }
}
