package com.bt.nextgen.reports.account.fees;

import com.bt.nextgen.api.client.model.ClientIdentificationDto;
import com.bt.nextgen.api.client.service.ClientListDtoService;
import com.bt.nextgen.core.reporting.BaseReport;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Report("clientList")
@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
public class ClientListCsvDownload extends BaseReport {


    @Autowired
    private ClientListDtoService clientListDtoService;


    @ReportBean("clientList")
    public Collection<ClientIdentificationDto> retrieveClientList(Map <String, String> params)
    {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        List<ClientIdentificationDto> clientIdentificationDtoList =  clientListDtoService.search(null, serviceErrors);
        return clientIdentificationDtoList;
    }

}