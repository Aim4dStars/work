package com.bt.nextgen.api.tracking.service;

import com.bt.nextgen.api.tracking.model.TrackingDto;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.service.ServiceErrors;

import java.util.Date;
import java.util.List;

public interface TrackingDtoService extends SearchByCriteriaDtoService<TrackingDto> {

    List<TrackingDto> searchForUnapprovedApplications(Date fromDate, Date toDate, final ServiceErrors serviceErrors);

    int countUnapprovedApplications(Date fromDate, Date toDate);

    List<TrackingDto> getTrackingDtos(List<ClientApplication> applications, boolean doMapAccounts, final boolean doFetchAdviserDetailInfo, final ServiceErrors serviceErrors);
}