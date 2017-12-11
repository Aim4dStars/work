package com.bt.nextgen.api.corporateaction.v1.permission;

import java.util.List;

public interface CorporateActionPermissionService {
    boolean checkPermissionForUser();

    boolean checkSubmitPermission(List<String> accountIds);

    boolean checkInvestorPermission(String accountId);
}
