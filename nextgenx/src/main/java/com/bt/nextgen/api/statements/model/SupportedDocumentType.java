package com.bt.nextgen.api.statements.model;


import org.apache.commons.lang3.EnumUtils;

/**
 * This Enum contains entries of supported document type to upload in Document Library.
 */
public enum SupportedDocumentType {
    CSV("text/csv"),
    PDF("application/pdf"),
    MSG("application/vnd.ms-outlook"),
    JPG("image/jpeg"),
    TIF("image/tiff"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    XLS("application/vnd.ms-excel"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    DOC("application/msword"),
    PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation"),
    PPT("application/vnd.ms-powerpoint"),
    ZIP("application/zip"),
    PNG("image/png"),
    DEFAULT("application/octet-stream");

    private String mimetype;

    SupportedDocumentType(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getMimetype() {
        return mimetype;
    }


    public static SupportedDocumentType forCode(String value) {
        for (SupportedDocumentType supportedDocumentType : SupportedDocumentType.values()) {
            if (supportedDocumentType.getMimetype().equals(value)) {
                return supportedDocumentType;
            }
        }
        return DEFAULT;
    }

    public static SupportedDocumentType getFileExtension(String documentName) {
        String defaultType = "default";
        String extension = "";
        int dotPos = documentName.lastIndexOf(".");
        if (dotPos < 0) {
            extension = defaultType;
        } else {
            String fileExt = documentName.substring(dotPos + 1);
            if (fileExt.length() == 0) {
                extension = defaultType;
            } else {
                extension = fileExt;
            }
        }
        return EnumUtils.isValidEnum(SupportedDocumentType.class, extension.toUpperCase()) ?
                SupportedDocumentType.valueOf(extension.toUpperCase()) : DEFAULT;
    }


}
