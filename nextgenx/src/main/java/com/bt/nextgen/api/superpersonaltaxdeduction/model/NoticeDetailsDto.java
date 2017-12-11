package com.bt.nextgen.api.superpersonaltaxdeduction.model;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

public class NoticeDetailsDto extends NoticeDto {

    private List<NoticeDto> pastNotices;
    public List<NoticeDto> getPastNotices() {
        return pastNotices;
    }

    public void setPastNotices(List<NoticeDto> pastNotices) {
        this.pastNotices = pastNotices;
    }

    public void updatePastNotices(NoticeDto pastNotice) {
        if (isEmpty(this.pastNotices)) {
            this.pastNotices = new ArrayList<>();
        }
        pastNotices.add(pastNotice);
    }
}
