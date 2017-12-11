package com.bt.nextgen.core.reporting;

import java.io.InputStream;
import java.io.OutputStream;

import com.bt.nextgen.reports.web.model.ReportRequestPackDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;

public interface ReportPackService {
    void create(ReportRequestPackDto reportRequestPackDto, InputStream coverLetter, OutputStream outputStream);

    String getReportFileName(AccountKey accountKey);
}
