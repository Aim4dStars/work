package com.bt.nextgen.api.draftaccount.service;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.api.draftaccount.model.DashboardClientApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.DashboardClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.PermittedClientApplicationRepository;
import com.bt.nextgen.service.ServiceErrors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(value = "springJpaTransactionManager")
public class DashboardClientApplicationDtoServiceImpl implements DashboardClientApplicationDtoService {

    @Autowired
    private PermittedClientApplicationRepository clientApplicationRepository;

    @Override
    public DashboardClientApplicationDto getLatestDraftAccounts(int count, ServiceErrors serviceErrors) {
        List<ClientApplication> clientApplications = clientApplicationRepository.findCertainNumberOfLatestDraftAccounts(count);
        Long numberOfDraftAccounts = clientApplicationRepository.getNumberOfDraftAccounts();
        List<DashboardClientApplicationDetailsDto> clientApplicationDetailsDtos = Lambda.convert(clientApplications, new Converter<ClientApplication, DashboardClientApplicationDetailsDto>() {
            @Override
            public DashboardClientApplicationDetailsDto convert(ClientApplication clientApplication) {
                return convertToDashboardClientApplicationDto(clientApplication);
            }
        });

        DashboardClientApplicationDto dashboardClientApplicationDto = new DashboardClientApplicationDto();
        dashboardClientApplicationDto.setTotalNumberOfDraftApplications(numberOfDraftAccounts);
        dashboardClientApplicationDto.setDraftClientApplications(clientApplicationDetailsDtos);
        return dashboardClientApplicationDto;
    }

    private DashboardClientApplicationDetailsDto convertToDashboardClientApplicationDto(ClientApplication clientApplication) {
        DashboardClientApplicationDetailsDto dashboardClientApplicationDetailsDto = new DashboardClientApplicationDetailsDto();
        IClientApplicationForm clientApplicationForm = clientApplication.getClientApplicationForm();

        dashboardClientApplicationDetailsDto.setKey(new ClientApplicationKey(clientApplication.getId()));
        dashboardClientApplicationDetailsDto.setAccountName(clientApplicationForm.getAccountName());
        dashboardClientApplicationDetailsDto.setAccountType(clientApplicationForm.getAccountType().value());
        dashboardClientApplicationDetailsDto.setLastModifiedDate(clientApplication.getLastModifiedAt());

        return dashboardClientApplicationDetailsDto;
    }
}
