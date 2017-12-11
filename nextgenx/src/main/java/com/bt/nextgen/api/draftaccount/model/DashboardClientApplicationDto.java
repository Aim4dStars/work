package com.bt.nextgen.api.draftaccount.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.util.List;


public class DashboardClientApplicationDto extends BaseDto {

    private long totalNumberOfDraftApplications;
    private List<DashboardClientApplicationDetailsDto> draftClientApplications;

    public long getTotalNumberOfDraftApplications() {
        return totalNumberOfDraftApplications;
    }

    public void setTotalNumberOfDraftApplications(long totalNumberOfDraftApplications) {
        this.totalNumberOfDraftApplications = totalNumberOfDraftApplications;
    }

    public List<DashboardClientApplicationDetailsDto> getDraftClientApplications() {
        return draftClientApplications;
    }

    public void setDraftClientApplications(List<DashboardClientApplicationDetailsDto> draftClientApplications) {
        this.draftClientApplications = draftClientApplications;
    }
}
