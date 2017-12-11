package com.bt.nextgen.serviceops.service;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.service.CredentialService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceError;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.domain.EmailImpl;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.onboarding.CreateAccountRequest;
import com.bt.nextgen.service.onboarding.CreateAccountResponse;
import com.bt.nextgen.service.onboarding.OnboardingIntegrationService;
import com.bt.nextgen.service.onboarding.btesb.ProcessAdvisersAdapter;
import com.bt.nextgen.serviceops.model.ServiceOpsModel;
import com.bt.nextgen.serviceops.model.UserAccountStatusModel;
import com.btfin.panorama.core.security.UserAccountStatus;
import com.bt.nextgen.web.controller.cash.util.Attribute;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserAccountStatusServiceImplTest {
    @InjectMocks
    private UserAccountStatusService userAccountStatusService = new UserAccountStatusServiceImpl();
    @Mock
    private CredentialService credentialService;
    @Mock
    private OnboardingIntegrationService btEsbService;
    @Mock
    CmsService cmsService;
    @Mock
    private DeviceArrangementService deviceArrangementService;

    @Test
    public void testCreateAccountReturnTrueWhenStatusCodeIsSuccess() {
        ServiceOpsModel serviceOps = new ServiceOpsModel();
        serviceOps.setFirstName("Taylow");
        serviceOps.setLastName("Mark");
        serviceOps.setGcmId("12345");
        serviceOps.setPrimaryMobileNumber("043117707");
        EmailImpl email = new EmailImpl();
        email.setEmail("tm@gmail.com");
        serviceOps.setClientId(EncodedString.fromPlainText("MTE2NjQ="));
        email.setType(AddressMedium.EMAIL_PRIMARY);
        List<Email> emails = new ArrayList<>();
        emails.add(email);
        serviceOps.setEmail(emails);
        CreateAccountResponse response = new ProcessAdvisersAdapter();
        when(btEsbService.processAdvisers(any(CreateAccountRequest.class))).thenReturn(response);
        String isAccountCreated = userAccountStatusService.createAccount(serviceOps);
        Assert.assertThat(isAccountCreated, IsNull.notNullValue());
        Assert.assertThat(isAccountCreated, Is.is(Attribute.SUCCESS_MESSAGE));
    }

    @Test
    public void testCreateAccountReturnFalseWhenStatusCodeIsError() {
        String message = "Sorry, a technical error has occurred. Please call 1300 881 716 and quote error code : Unknown.";
        ServiceOpsModel serviceOps = new ServiceOpsModel();
        serviceOps.setFirstName("Taylow");
        serviceOps.setLastName("Mark");
        serviceOps.setGcmId("12345");
        serviceOps.setPrimaryMobileNumber("043117707");
        EmailImpl email = new EmailImpl();
        email.setEmail("tm@gmail.com");
        serviceOps.setClientId(EncodedString.fromPlainText("MTE2NjQ="));
        email.setType(AddressMedium.EMAIL_PRIMARY);
        List<Email> emails = new ArrayList<>();
        emails.add(email);
        serviceOps.setEmail(emails);
        serviceOps.setClientId(EncodedString.fromPlainText("MTE2NjQ="));
        CreateAccountResponse response = new ProcessAdvisersAdapter();
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        serviceErrors.addError(getServiceErrors());
        response.setServiceErrors(serviceErrors);
        when(btEsbService.processAdvisers(any(CreateAccountRequest.class))).thenReturn(response);
        when(cmsService.getDynamicContent(anyString(), any(String[].class))).thenReturn(message);
        String isAccountCreated = userAccountStatusService.createAccount(serviceOps);
        Assert.assertThat(isAccountCreated, IsNull.notNullValue());
        Assert.assertThat(isAccountCreated, Is.is(message));
    }

    private ServiceError getServiceErrors() {
        ServiceErrorImpl serviceError = new ServiceErrorImpl();
        serviceError.setId("123");
        return serviceError;
    }

    @Test
    public void testCreateAccountReturnFalseWhenStatusCodeIsUnknownError() {
        String message = "SUCCESS";
        ServiceOpsModel serviceOps = new ServiceOpsModel();
        serviceOps.setFirstName("Taylow");
        serviceOps.setLastName("Mark");
        serviceOps.setGcmId("12345");
        serviceOps.setRole(Attribute.ADVISER);
        serviceOps.setPrimaryMobileNumber("043117707");
        EmailImpl email = new EmailImpl();
        email.setEmail("tm@gmail.com");
        serviceOps.setClientId(EncodedString.fromPlainText("MTE2NjQ="));
        email.setType(AddressMedium.EMAIL_PRIMARY);
        List<Email> emails = new ArrayList<>();
        emails.add(email);
        serviceOps.setEmail(emails);
        serviceOps.setClientId(EncodedString.fromPlainText("MTE2NjQ="));
        CreateAccountResponse response = new ProcessAdvisersAdapter();
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        serviceErrors.addError(new ServiceErrorImpl());
        response.setServiceErrors(serviceErrors);
        when(btEsbService.processAdvisers(any(CreateAccountRequest.class))).thenReturn(response);
        when(cmsService.getDynamicContent(anyString(), any(String[].class))).thenReturn(message);
        String isAccountCreated = userAccountStatusService.createAccount(serviceOps);
        Assert.assertThat(isAccountCreated, IsNull.notNullValue());
        Assert.assertThat(isAccountCreated, Is.is(message));
    }

    @Test
    public void testLookupReturn_ActiveStatus_WhenDeviceExist_Non_Migrated_Customer() {
        UserAccountStatusModel userAccountStatusModel = new UserAccountStatusModel();
        userAccountStatusModel.setUserAccountStatus(UserAccountStatus.ACTIVE);

        when(deviceArrangementService.isDeviceDetailsFound(anyString())).thenReturn(Boolean.TRUE);
        when(
            credentialService.lookupStatus(
                anyString(), any(ServiceErrors.class))).thenReturn(
            userAccountStatusModel);
        UserAccountStatusModel status = userAccountStatusService.lookupStatus(
            "userId", "deviceId", false);
        Assert.assertThat(status, IsNull.notNullValue());
        Assert.assertThat(
            status.getUserAccountStatus(), CoreMatchers.equalTo(UserAccountStatus.ACTIVE));
    }

    @Test
    public void testLookupReturn_ActiveStatus_WhenDeviceExist_Migrated_Customer() {
        UserAccountStatusModel userAccountStatusModel = new UserAccountStatusModel();
        userAccountStatusModel.setUserAccountStatus(UserAccountStatus.ACTIVE);

        when(deviceArrangementService.isDeviceDetailsFound(anyString())).thenReturn(Boolean.TRUE);
        when(
                credentialService.lookupStatus(
                        anyString(), any(ServiceErrors.class))).thenReturn(
                userAccountStatusModel);
        UserAccountStatusModel status = userAccountStatusService.lookupStatus(
                "userId", "deviceId", true);
        Assert.assertThat(status, IsNull.notNullValue());
        Assert.assertThat(
                status.getUserAccountStatus(), CoreMatchers.equalTo(UserAccountStatus.ACTIVE));
    }

    @Test
    public void testLookupReturn_AccountCreationIncompleteStatus_WhenDeviceDoesNotExistinSAFI() {
        UserAccountStatusModel userAccountStatusModel = new UserAccountStatusModel();
        userAccountStatusModel.setUserAccountStatus(UserAccountStatus.ACTIVE);

        when(deviceArrangementService.isDeviceDetailsFound(anyString())).thenReturn(Boolean.FALSE);
        when(
            credentialService.lookupStatus(
                anyString(), any(ServiceErrors.class))).thenReturn(
            userAccountStatusModel);
        UserAccountStatusModel status = userAccountStatusService.lookupStatus(
            "userId", "deviceId", false);
        Assert.assertThat(status, IsNull.notNullValue());
        Assert.assertThat(
            status.getUserAccountStatus(), CoreMatchers.equalTo(UserAccountStatus.ACTIVE));
    }

    @Test
    public void testLookupReturn_AccountCreationIncompleteStatus_WhenDeviceDoesNotExistAtAll() {
        UserAccountStatusModel userAccountStatusModel = new UserAccountStatusModel();
        userAccountStatusModel.setUserAccountStatus(UserAccountStatus.ACTIVE);

        when(deviceArrangementService.isDeviceDetailsFound(anyString())).thenReturn(Boolean.FALSE);
        when(
                credentialService.lookupStatus(
                        anyString(), any(ServiceErrors.class))).thenReturn(
                userAccountStatusModel);
        UserAccountStatusModel status = userAccountStatusService.lookupStatus(
                "userId", null, false);
        Assert.assertThat(status, IsNull.notNullValue());
        Assert.assertThat(
                status.getUserAccountStatus(), CoreMatchers.equalTo(UserAccountStatus.ACCOUNT_CREATION_INCOMPLETE));
    }


    @Test
    public void testLookupReturn_ActiveStatus_WhenDeviceNotExistAbs_Migrated() {
        UserAccountStatusModel userAccountStatusModel = new UserAccountStatusModel();
        userAccountStatusModel.setUserAccountStatus(UserAccountStatus.ACTIVE);

        when(deviceArrangementService.isDeviceDetailsFound(anyString())).thenReturn(Boolean.TRUE);
        when(
                credentialService.lookupStatus(
                        anyString(), any(ServiceErrors.class))).thenReturn(
                userAccountStatusModel);
        UserAccountStatusModel status = userAccountStatusService.lookupStatus(
                "userId", null, true);
        Assert.assertThat(status, IsNull.notNullValue());
        Assert.assertThat(
                status.getUserAccountStatus(), CoreMatchers.equalTo(UserAccountStatus.ACTIVE));
    }

}
