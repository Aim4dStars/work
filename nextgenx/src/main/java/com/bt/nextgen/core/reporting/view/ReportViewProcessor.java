package com.bt.nextgen.core.reporting.view;

import net.sf.jasperreports.engine.design.JasperDesign;

public interface ReportViewProcessor
{
    JasperDesign process(ReportView reportView);
}
