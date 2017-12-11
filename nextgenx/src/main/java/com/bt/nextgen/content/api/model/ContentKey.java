package com.bt.nextgen.content.api.model;

public class ContentKey {
    private String contentId;

    public ContentKey(String contentId) {
        super();
        this.contentId = contentId;
    }

    public String getContentId() {
        return contentId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ContentKey) || contentId == null || ((ContentKey) obj).getContentId() == null) {
            return false;
        }
        return contentId.equals(((ContentKey) obj).getContentId());
    }
    
    @Override
    public int hashCode() {
        int hash = 1;
        if (contentId != null) {
            hash = contentId.hashCode();
        }
        return hash;
    }
}
