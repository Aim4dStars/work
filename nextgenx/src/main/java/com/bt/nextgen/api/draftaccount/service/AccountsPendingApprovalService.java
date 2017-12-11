package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.integration.account.WrapAccount;

import java.util.List;

public interface AccountsPendingApprovalService {
    List<WrapAccount> getUserAccountsPendingApprovals(ServiceErrors serviceErrors);
}
