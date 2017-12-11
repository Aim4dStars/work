package com.bt.nextgen.reports.account;



import com.bt.nextgen.api.account.v2.model.AccountDto;
import com.bt.nextgen.api.account.v2.service.AccountDtoService;
import com.bt.nextgen.core.reporting.BaseReport;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: L069552
 * Date: 6/04/16
 * Time: 4:01 PM
 * To change this template use File | Settings | File Templates.
 */
@Report("accountList")
@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
public class AccountListCsvDownload extends BaseReport {

    @Autowired
    @Qualifier("AccountDtoServiceV2")
    private AccountDtoService accountDtoService;


    @ReportBean("accountList")
    @SuppressWarnings("squid:S1172")
    public Collection<AccountDto> retrieveAccountList(Map<String, String> params) {

        ServiceErrors serviceError = new FailFastErrorsImpl();
        List<AccountDto> accountDtoList = accountDtoService.search(null,serviceError);
        return accountDtoList;

    }

}


