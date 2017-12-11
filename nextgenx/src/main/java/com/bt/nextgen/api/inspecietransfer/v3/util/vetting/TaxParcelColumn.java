package com.bt.nextgen.api.inspecietransfer.v3.util.vetting;

import java.util.ArrayList;
import java.util.List;

public enum TaxParcelColumn {
    ASSET_CODE(0, "ASX Security Code or APIR Code"),
    QUANTITY(1, "Quantity"),
    OWNER(2, "HIN or SRN or Account Number"),
    CUSTODIAN(3, "Platform Name or Managed Fund Custodian"),
    ACQUISITION_DATE(4, "Acquisition date"),
    BLANK1(5, null),
    ORIGINAL_COST_BASE(6, "Original Cost Base"),
    BLANK2(7, null),
    CGT_COST_BASE(8, "CGT Cost Base"),
    REDUCED_COST_BASE(9, "CGT Reduced Cost Base"),
    INDEXED_COST_BASE(10, "CGT Indexed Cost Base");
    
    private int columnIndex;
    private String columnHeader;
    
    private TaxParcelColumn(int columnIndex, String columnHeader) {
        this.columnIndex = columnIndex;
        this.columnHeader = columnHeader;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public String getColumnHeader() {
        return columnHeader;
    }

    public static List<Integer> getBlankColumnIndices() {
        List<Integer> blanks = new ArrayList<>();
        blanks.add(TaxParcelColumn.BLANK1.getColumnIndex());
        blanks.add(TaxParcelColumn.BLANK2.getColumnIndex());
        return blanks;
    }

    @Override
    public String toString() {
        return columnHeader;
    }
}