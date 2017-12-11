package com.bt.nextgen.serviceops.service;

import com.bt.nextgen.api.draftaccount.model.ClientApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.ServiceOpsClientApplicationDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.serviceops.model.LeftNavPermissionModel;
import com.bt.nextgen.serviceops.model.ServiceOpsModel;
import com.bt.nextgen.serviceops.model.WrapAccountModel;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface ServiceOpsService {
    ServiceOpsModel getUsers(String searchCriteria, String roleType);

    ServiceOpsModel getUserDetail(String clientId, boolean isClientDetailPage, ServiceErrors serviceErrors) throws Exception;

    ServiceOpsModel getAccountDetail(String accountId, ServiceErrors serviceErrors);

    ServiceOpsModel getSortedUsers(String searchCriteria);

    ServiceOpsModel getSortedAccounts(String searchCriteria, ServiceErrors serviceErrors);

    ServiceOpsClientApplicationDto getFailedApplicationDetails(String applicationId);

    List<ServiceOpsClientApplicationDto> getFailedDirectApplications(String cisKey);

    void moveFailedApplicationToDraft(String applicationId);

    String downloadCsvOfAllUnapprovedApplications(Date fromDate, Date toDate, ServiceErrors serviceErrors) throws IOException;

    ClientApplicationDetailsDto getClientApplicationDetails(String clientApplicationId);

    ClientApplicationDetailsDto getClientApplicationDetailsByAccountNumber(String accountNumber);

    List<ServiceOpsClientApplicationDto> getApprovedClientApplicationsByCISKey(String cisKey);

    int countOfApplicationIdsForUnapprovedApplications(Date fromDate, Date toDate);

    List<WrapAccountModel> findWrapAccountDetail(String bpNumber);

    List<WrapAccountModel> findWrapAccountDetailsByGcm(final String gcmId);

    boolean updatePPID(String PPID, String clientID, ServiceErrors serviceErrors);

    boolean updatePreference(String preference ,String clientID ,ServiceErrors serviceErrors);

    boolean isServiceOpsSuperRole();

    boolean isServiceOpsRestricted();

    boolean isServiceOpsITSupportRole();

    LeftNavPermissionModel getLeftNavPermissions();
}
