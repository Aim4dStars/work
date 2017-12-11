package com.bt.nextgen.serviceops.controller;

/**
 * Created by L072457 on 23/07/2015.
 */
public enum MimeType {

    TEXT_PLAIN("text/plain", "TXT"),
    TEXT_HTML("text/html", "HTM"),
    TEXT_CSV("text/csv", "CSV"),
    IMAGE_JPG("image/jpg", "JPG"),
    IMAGE_PNG("image/jpg", "PNG"),
    IMAGE_GIF("image/gif", "GIF"),
    APPLICATION_PDF("application/pdf", "PDF"),
    APPLICATION_MSWORD("application/msword", "DOC"),
    APPLICATION_MSWORD_X("application/msword", "DOCX"),
    DEFAULT("text/html", "");


    private String extension;
    private String mimType;

    private MimeType(String extension, String mimType) {

        this.extension = extension;
        this.mimType = mimType;
    }

    public String getMimType() {
        return mimType;
    }

    public String getExtension() {
        return extension;
    }

    public static MimeType forExtension(String ext)
    {
        for (MimeType type : MimeType.values())
        {
            if (type.getExtension().equalsIgnoreCase(ext))
            {
                return type;
            }
        }
        return MimeType.DEFAULT;
    }
}
