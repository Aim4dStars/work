package com.bt.nextgen.reports.account.performance;

import java.util.List;

public class PerformanceRowData {
    public enum RowType {
        DATA,
        SUBDATA,
        SUBTOTAL,
        TOTAL
    }

    private RowType rowType;
    private List<String> data;

    public PerformanceRowData(RowType rowType, List<String> data) {
        super();
        this.rowType = rowType;
        this.data = data;
    }

    public String getRowTypeName() {
        return rowType.name();
    }


    private String getData(int period) {
        if (period < data.size()) {
            return data.get(period);
        }
        return null;
    }

    public String getDescription() {
        return getData(0);
    }

    public String getDataPeriod1() {
        return getData(1);
    }

    public String getDataPeriod2() {
        return getData(2);
    }

    public String getDataPeriod3() {
        return getData(3);
    }

    public String getDataPeriod4() {
        return getData(4);
    }

    public String getDataPeriod5() {
        return getData(5);
    }

    public String getDataPeriod6() {
        return getData(6);
    }

    public String getDataPeriod7() {
        return getData(7);
    }

    public String getDataPeriod8() {
        return getData(8);
    }

    public String getDataPeriod9() {
        return getData(9);
    }

    public String getDataPeriod10() {
        return getData(10);
    }

    public String getDataPeriod11() {
        return getData(11);
    }

    public String getDataPeriod12() {
        return getData(12);
    }

    public String getDataPeriod13() {
        return getData(13);
    }

    public String getDataPeriod14() {
        return getData(14);
    }

    public String getDataPeriod15() {
        return getData(15);
    }

    public String getDataPeriod16() {
        return getData(16);
    }

    public String getDataPeriod17() {
        return getData(17);
    }

    public String getDataPeriod18() {
        return getData(18);
    }

    public String getDataPeriod19() {
        return getData(19);
    }

    public String getDataPeriod20() {
        return getData(20);
    }
}
