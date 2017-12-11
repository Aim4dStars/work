package com.bt.nextgen.service.integration.user.notices.model;

public enum NoticeType {
    TERMS_OF_USE("terms", "Terms of use"),
    /**
    * @deprecated product specific pds flags should be used
    */
    @Deprecated PDS("pds", "DO NOT USE: Product disclosure statement"),
    PDS_DIRECT("pds_directinvestor", "Product disclosure statement - direct investor"),
    PDS_DIRECT_SUPER("pds_directsuper", "Product disclosure statement - direct super/pension"),
    PDS_ADVISED_SUPER("pds_advisedsuper", "Product disclosure statement - advised super"),
    PDS_ADVISED_INVESTOR("pds_advisedinvestor", "Product disclosure statement - advised investor"),
    WHATS_NEW_DIRECT("whatsnew_directinvestor", "What's new - direct investor"),
    WHATS_NEW_DIRECT_SUPER("whatsnew_directsuper", "What's new - direct super/pension"),
    WHATS_NEW_ADVISED_SUPER("whatsnew_advisedsuper", "What's new - advised super"),
    WHATS_NEW_ADVISED_INVESTOR("whatsnew_advisedinvestor", "What's new - advised investor");

    private String id;
    private String displayText;

    NoticeType(final String noticeId, final String displayText) {
        this.id = noticeId;
        this.displayText = displayText;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(final String displayText) {
        this.displayText = displayText;
    }

    public static NoticeType forNoticeId(final String noticeId) {
        for (final NoticeType noticeType : NoticeType.values()) {
            if (noticeType.getId().equals(noticeId)) {
                return noticeType;
            }
        }
        return null;
    }
}
