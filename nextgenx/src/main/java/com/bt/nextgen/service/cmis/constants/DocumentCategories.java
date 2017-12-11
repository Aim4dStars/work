package com.bt.nextgen.service.cmis.constants;

import com.bt.nextgen.core.util.Properties;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * DocumentCategories maps Acronym, document class mapping.
 * It also has utility method which returns file folder at Filenet system. All the folder are defined in properties files
 */
public enum DocumentCategories {
    SMSF("SMSF", DocumentConstants.DOCUMENT_CLASS_SMSF, "SMSF", new String[]{"Company", "SMSF Fund Administration", "SMSF Fund Establishment", "SMSF General"}),
    STATEMENTS("STM", DocumentConstants.DOCUMENT_CLASS_STATEMENT, "Statements"),
    TAX("TAX", DocumentConstants.DOCUMENT_CLASS_OTHER_DOCUMENTS, "Tax returns"),
    TAX_SUPER("TAXSUPER", DocumentConstants.DOCUMENT_CLASS_OFFLINE, "Tax"),
    OTHER("OTHER", DocumentConstants.DOCUMENT_CLASS_OTHER_DOCUMENTS, "Other"),
    INVESTMENTS("INV", DocumentConstants.DOCUMENT_CLASS_OTHER_DOCUMENTS, "Investments", new String[]{"Corporate Actions", "Transaction Confirmations", "Asset Transfers"}),
    ADVICE("ADVICE", DocumentConstants.DOCUMENT_CLASS_OTHER_DOCUMENTS, "Advice Documentation"),
    IMMODELREPORT("IMMODELRPT", DocumentConstants.DOCUMENT_CLASS_MODELREPORT, "Model Report"),
    CORRESPONDENCE("CORRO", DocumentConstants.DOCUMENT_CLASS_CORROADHOC, "Correspondence"),
    EMAIL("EMAIL", DocumentConstants.DOCUMENT_CLASS_CORROADHOC, "Email"),
    FAX("FAX", DocumentConstants.DOCUMENT_CLASS_CORROADHOC, "Fax"),
    SCANNED("SCANNED", DocumentConstants.DOCUMENT_CLASS_CORROADHOC, "Scanned"),
    POBOX("POBOX", DocumentConstants.DOCUMENT_CLASS_CORROPOBOX, "PO Box"),
    OFFLINEAPPROVAL("APPROVAL", DocumentConstants.DOCUMENT_CLASS_OFFLINE, "Approval"),
    CHALLENGER("CHALLENGERCORRO", null, "Challenger Correspondence");

    private String code;
    private String documentClass;
    private String displayName;
    private String[] subCatogories;
    private static final String BASE_PATH = "cmis.folder.";

    DocumentCategories(String code, String documentClass, String displayName) {
        this(code, documentClass, displayName, null);
    }

    DocumentCategories(String code, String documentClass, String displayName, String[] subCategories) {
        this.code = code;
        this.documentClass = documentClass;
        this.displayName = displayName;
        this.subCatogories = subCategories;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static DocumentCategories forCode(String code) {
        for (DocumentCategories documentCategories : DocumentCategories.values()) {
            if (code.equals(documentCategories.code)) {
                return documentCategories;
            }
        }
        return null;
    }

    public String getFolder() {
        String path = Properties.getString(BASE_PATH + name().toLowerCase());
        if (path == null) {
            return Properties.getString(BASE_PATH + "default");
        }
        return path;
    }

    public String getDocumentClass() {
        return this.documentClass;
    }

    public List<String> getSubCatogories() {
        if (subCatogories == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(subCatogories);
    }
}
