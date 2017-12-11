package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.draftaccount.model.DashboardClientApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.DashboardClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.PermittedClientApplicationRepository;
import com.bt.nextgen.service.ServiceErrors;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DashboardClientApplicationDtoServiceImplTest {

    @Mock
    private PermittedClientApplicationRepository permittedClientApplicationRepository;

    @Mock
    private ServiceErrors serviceErrors;

    @InjectMocks
    DashboardClientApplicationDtoServiceImpl dashboardClientApplicationDtoService;

    @Test
    public void shouldReturnConvertedDashboardClientApplicationDto() throws Exception {
        int count = 3;
        long totalApplicationsCount = 4;
        DateTime modifiedDate = DateTime.now();

        ArrayList<ClientApplication> clientApplications = new ArrayList<>();
        ClientApplication clientApplication1 = createClientApplication(0l, "ACCOUNT_NAME_0", IClientApplicationForm.AccountType.INDIVIDUAL, modifiedDate);
        clientApplications.add(clientApplication1);

        ClientApplication clientApplication2 = createClientApplication(1l, "ACCOUNT_NAME_1", IClientApplicationForm.AccountType.COMPANY, modifiedDate);
        clientApplications.add(clientApplication2);

        ClientApplication clientApplication3 = createClientApplication(2l, "ACCOUNT_NAME_2", IClientApplicationForm.AccountType.CORPORATE_TRUST, modifiedDate);
        clientApplications.add(clientApplication3);

        when(permittedClientApplicationRepository.findCertainNumberOfLatestDraftAccounts(count)).thenReturn(clientApplications);
        when(permittedClientApplicationRepository.getNumberOfDraftAccounts()).thenReturn(totalApplicationsCount);

        DashboardClientApplicationDto result = dashboardClientApplicationDtoService.getLatestDraftAccounts(3, serviceErrors);

        List<DashboardClientApplicationDetailsDto> dashboardClientApplicationDetailsDtos = result.getDraftClientApplications();

        assertThat(result.getTotalNumberOfDraftApplications(), is(totalApplicationsCount));
        assertThat(dashboardClientApplicationDetailsDtos, hasSize(3));
        checkDashboardClientApplicationDto(dashboardClientApplicationDetailsDtos.get(0), clientApplication1);
        checkDashboardClientApplicationDto(dashboardClientApplicationDetailsDtos.get(1), clientApplication2);
        checkDashboardClientApplicationDto(dashboardClientApplicationDetailsDtos.get(2), clientApplication3);
    }

    private void checkDashboardClientApplicationDto(DashboardClientApplicationDetailsDto dashboardClientApplicationDetailsDto, ClientApplication clientApplication) {
        assertThat(dashboardClientApplicationDetailsDto.getAccountName(), is(clientApplication.getClientApplicationForm().getAccountName()));
        assertThat(dashboardClientApplicationDetailsDto.getAccountType(), is(clientApplication.getClientApplicationForm().getAccountType().value()));
        assertThat(dashboardClientApplicationDetailsDto.getLastModifiedDate(), is(clientApplication.getLastModifiedAt()));
        assertThat(dashboardClientApplicationDetailsDto.getKey().getClientApplicationKey(), is(clientApplication.getId()));
    }

    private ClientApplication createClientApplication(Long id, String accountName, IClientApplicationForm.AccountType accountType, DateTime modifiedDate) {
        IClientApplicationForm form = mock(IClientApplicationForm.class);
        when(form.getAccountName()).thenReturn(accountName);
        when(form.getAccountType()).thenReturn(accountType);

        ClientApplication clientApplication = mock(ClientApplication.class);
        when(clientApplication.getLastModifiedAt()).thenReturn(modifiedDate);
        when(clientApplication.getId()).thenReturn(id);
        when(clientApplication.getClientApplicationForm()).thenReturn(form);

        return clientApplication;
    }
}