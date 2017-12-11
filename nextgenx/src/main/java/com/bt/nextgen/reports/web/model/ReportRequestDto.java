package com.bt.nextgen.reports.web.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import com.bt.nextgen.config.JsonViews;

public class ReportRequestDto {
    @JsonProperty("r")
    @JsonView(JsonViews.Write.class)
    private String reportId;

    @JsonProperty("p")
    @JsonView(JsonViews.Write.class)
    private Map<String, Object> params;

    public String getReportId() {
        return reportId;
    }

    public Map<String, Object> getParams() {
        return params;
    }
}
