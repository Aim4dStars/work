package com.bt.nextgen.service.integration.financialdocument;

public enum FinancialDocumentType {
    ANNUAL_INVESTMENT_STATEMENT("Annual investment statement", "STMANN"),
    ANNUAL_TAX_STATEMENT("Annual tax statement", "STMTAX"),
    PAYG_STATEMENT("PAYG statement", "PYGSTM"),
    EXIT_STATEMENT("Exit statement", "EXTSTM"),
    FEE_REVENUE_STATEMENT("Fee revenue statement", "FRSSTM"),
    QUARTERLY_INVESTMENT_STATEMENT("Quarterly investment statement", "QTRSTM"),
    IMMODEL("IM Model Report", "IMMODEL"),
    IMMODELALL("All IM Model Report", "IMMODELALL"),
    UNKNOWN("Unknown", "UNKNOWN");

    private String description;
    private String code;

    private FinancialDocumentType(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }

    public static FinancialDocumentType forCode(String code) {
        for (FinancialDocumentType type : FinancialDocumentType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return FinancialDocumentType.UNKNOWN;
    }

}
