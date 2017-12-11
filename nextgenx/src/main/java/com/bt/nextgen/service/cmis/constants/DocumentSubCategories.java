package com.bt.nextgen.service.cmis.constants;

/**
 * Created by L075208 on 23/03/2016.
 */
public enum DocumentSubCategories {

ASSETS("ASSETS","Assets"),
PENSIONS("PENSIONS","Pensions"),
EXPENSES("EXPENSES","Expenses"),
FINANCIAL_STATEMENTS("FNSTMTAX","Financial Statements and Tax Documents"),
INCOME("INCOME","Income"),
LIABILITIES("LIABS","Liabilities"),
GENERAL("GENERAL","General"),
MASTER_DOCUMENTS("MSTDOCS","Master Documents"),
CONTRIBUTIONS("CONTRIBS","Contributions");

    private String code;
    private String displayName;

    DocumentSubCategories(String code, String displayName) {
        this.code=code;
        this.displayName=displayName;
    }
    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static DocumentSubCategories forCode(String code) {
        for (DocumentSubCategories documentSubCategories : DocumentSubCategories.values()) {
            if (documentSubCategories.code.equals(code)) {
                return documentSubCategories;
            }
        }
        return null;
    }
}
