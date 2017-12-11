package com.bt.nextgen.core.repository;

import com.bt.nextgen.draftaccount.repository.ClientApplication;

import java.util.List;

/**
 * Created by F058391 on 19/11/2015.
 */
public interface CisKeyClientApplicationRepository {
    List<ClientApplication> findClientApplicationsForCisKey(String cisKey);

    void save(CisKeyClientApplication cisKeyClientApplication);
}
