package com.bt.nextgen.service.avaloq.basil;

/**
 * Created by M035995 on 7/10/2016.
 */
public enum DocumentType {

    WELCOME_LETTER("APP", "Welcome Pack"),
    RENEWAL_LETTER("RENEW", "Renewal Pack"),
    CANCELLED_LETTER("CANCEL", "Cancellation Letter"),
    DISHONOUR_LETTER("DISHH", "Dishonour Letter"),
    DISHONOUR_LETTER_SECOND("DISHI", "Dishonour Letter"),
    LAPSED_LETTER("LPSCF4", "Cancellation Letter"),
    MANUAL_DISHONOUR_LETTER("MLDISH", "Dishonour Letter"),
    REINSTATEMENT_LETTER("REINST", "Reinstatement Letter");

    private String code;
    private String name;

    DocumentType(final String code, final String name) {
        this.code = code;
        this.name = name;
    }

    public static DocumentType findByCode(String code) {
        for (DocumentType documentType : DocumentType.values()) {
            if (documentType.getCode().equals(code)) {
                return documentType;
            }
        }
        return null;
    }

    /**
     * Validate if the document name is valid
     *
     * @param name name of the document.
     *
     * @return true if the document name is valid.
     */
    public static boolean isDocumentTypeValid(String name) {
        for (DocumentType documentType : DocumentType.values()) {
            if (documentType.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
