package com.bt.nextgen.reports.web.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.BaseDto;

public class ReportRequestPackDto extends BaseDto {
    @JsonProperty("compress")
    @JsonView(JsonViews.Write.class)
    private boolean compressReports;

    @JsonProperty("reps")
    @JsonView(JsonViews.Write.class)
    private List<ReportRequestDto> reportRequestDtos;

    public boolean getCompressReports() {
        return compressReports;
    }

    public List<ReportRequestDto> getReportRequestDtos() {
        return reportRequestDtos;
    }
}
