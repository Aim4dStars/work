package com.bt.nextgen.core.reporting;

import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;

public class TableContinuedControl extends JRDefaultScriptlet {
    private static final String CONTROL = "tableContinued";

    public void afterPageInit() throws JRScriptletException {
        super.afterPageInit();
        this.setVariableValue(CONTROL, Boolean.TRUE);
    }

    public void afterGroupInit(String groupName) throws JRScriptletException {
        super.afterGroupInit(groupName);
        this.setVariableValue(CONTROL, Boolean.FALSE);
    }
}