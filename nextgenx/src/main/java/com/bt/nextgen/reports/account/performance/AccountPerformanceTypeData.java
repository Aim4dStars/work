package com.bt.nextgen.reports.account.performance;

import java.util.ArrayList;
import java.util.List;

public class AccountPerformanceTypeData {
    private final String type;
    private final List<String> headers = new ArrayList<>();
    private final List<PerformanceRowData> rows = new ArrayList<PerformanceRowData>();

    public AccountPerformanceTypeData(String type) {
        this.type = type;
    }

    protected void addColumnHeader(String data) {
        headers.add(data);
    }

    protected void addRow(PerformanceRowData data) {
        rows.add(data);
    }

    public List<String> getHeaders() {
        return headers;
    }

    public List<PerformanceRowData> getRows() {
        return rows;
    }
    
    public String getType() {
        return type;
    }
    
}
