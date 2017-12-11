package com.bt.nextgen.serviceops.controller;

import com.bt.nextgen.service.avaloq.domain.EmailImpl;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.onboarding.ProvisionMFADeviceRequest;
import com.bt.nextgen.serviceops.model.ProvisionMFARequestData;
import com.bt.nextgen.serviceops.service.ProvisionMFADeviceService;
import org.hamcrest.Matchers;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import org.mockito.ArgumentCaptor;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.bt.nextgen.api.account.v3.model.PersonRelationDto;
import com.bt.nextgen.api.draftaccount.model.AccountSettingsDto;
import com.bt.nextgen.service.integration.account.AccountStructureType;

import org.apache.struts.mock.MockHttpSession;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bt.nextgen.api.account.v2.model.LinkedAccountDto;
import com.bt.nextgen.api.broker.model.BrokerDto;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.api.client.service.MaintainArrangementAndRelationshipService;
import com.bt.nextgen.api.client.service.MaintainIpToIpRelationshipDTOService;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.ServiceOpsClientApplicationDto;
import com.bt.nextgen.clients.web.model.ClientModel;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.core.repository.OnboardingApplicationStatus;
import com.btfin.panorama.core.security.Roles;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.core.security.api.service.PermissionBaseDtoService;
import com.btfin.panorama.core.security.profile.Profile;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.service.CredentialService;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.web.RequestQuery;
import com.bt.nextgen.core.web.model.AjaxResponse;
import com.bt.nextgen.core.web.util.View;
import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.avaloq.domain.PhoneImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.avaloq.userprofile.JobProfileImpl;
import com.btfin.panorama.core.security.integration.customer.ChannelType;
import com.bt.nextgen.service.group.customer.CredentialRequest;
import com.btfin.panorama.core.security.integration.customer.CredentialType;
import com.btfin.panorama.core.security.integration.customer.CustomerCredentialInformation;
import com.bt.nextgen.service.group.customer.CustomerLoginManagementIntegrationService;
import com.btfin.panorama.core.security.integration.customer.UserGroup;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.btfin.panorama.core.security.integration.userinformation.UserInformation;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.bt.nextgen.service.prm.pojo.PrmDto;
import com.bt.nextgen.service.prm.pojo.PrmEventType;
import com.bt.nextgen.service.prm.service.PrmService;
import com.bt.nextgen.service.prm.service.PrmServiceImpl;
import com.bt.nextgen.serviceops.model.IntermediariesModel;
import com.bt.nextgen.serviceops.model.ServiceOpsModel;
import com.bt.nextgen.serviceops.model.UserAccountStatusModel;
import com.bt.nextgen.serviceops.model.WrapAccountModel;
import com.bt.nextgen.serviceops.service.DeviceArrangementServiceImpl;
import com.bt.nextgen.serviceops.service.ModifyChannelAccessCredentialService;
import com.bt.nextgen.serviceops.service.ResendRegistrationEmailService;
import com.bt.nextgen.serviceops.service.ResetPasswordService;
import com.bt.nextgen.serviceops.service.ServiceOpsService;
import com.bt.nextgen.serviceops.service.UpdateDeviceArrangementService;
import com.bt.nextgen.serviceops.service.UserAccountStatusService;
import com.btfin.panorama.core.security.UserAccountStatus;
import com.bt.nextgen.userauthority.web.Action;
import com.bt.nextgen.util.SamlUtil;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

@RunWith(MockitoJUnitRunner.class)
public class ServiceOpsControllerTest {
    EncodedString userId = EncodedString.fromPlainText("MTE2NjQ=");
    EncodedString portfolioId = EncodedString.fromPlainText("MTE5NDk=");
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd");

    @InjectMocks
    private ServiceOpsController serviceOpsController;
    
    @InjectMocks
    private ServiceOpsGcmController serviceOpsGcmController;

    @Mock
    ServiceOpsService serviceOpsService;

    AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter;
    MockHttpServletRequest request;
    MockHttpServletResponse response;
    @Mock
    UserProfileService userProfileService;

    @Mock
    PrmService prmService;

    @Mock
    DeviceArrangementServiceImpl deviceArrangementService;

    @Mock
    CmsService cmsService;

    @Mock
    ResetPasswordService resetPasswordService;

    @Mock
    private CredentialService credentialService;

    @Mock
    ModifyChannelAccessCredentialService blockService;

    @Mock
    UpdateDeviceArrangementService updateDeviceArrangementService;

    @Mock
    UserAccountStatusService userAccountStatusService;

    @Mock
    RedirectAttributes redirectAttributes;

    @Mock
    ServiceOpsModel serviceOpsModel;
    @Mock
    WebDataBinder dataBinder;

    @Mock
    private RequestQuery requestQuery;

    @Mock
    private ServiceErrors serviceErrors;

    @Mock
    private ClientIntegrationService clientIntegrationService;

    @Mock
    private PermissionBaseDtoService permissionBaseService;

    private SamlToken samlToken;
    @Mock
    private CustomerLoginManagementIntegrationService customerLoginService;

    @Mock
    ResendRegistrationEmailService resendRegistrationEmailService;

    @Mock
    FeatureTogglesService featureTogglesService;

    @Mock
    private MaintainArrangementAndRelationshipService maintainArrangementAndRelationshipService;
    
    @Mock
    private MaintainIpToIpRelationshipDTOService   maintainIpToIpRelationshipDTOService;

    @Mock
    private ProvisionMFADeviceService provisionMFADeviceService;

    private static final String SEARCH_CRITERIA = "TAY";

    private static final String KEY = "ThatThingThatThing";

    @SuppressWarnings("serial")
    private static final Serializable VALUE = new Serializable() {
    };

    @Before
    public void setup() throws Exception {
        when(requestQuery.getOriginalHost()).thenReturn("");
        annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        Mockito.when(serviceOpsService.getSortedUsers(anyString())).thenAnswer(new Answer<ServiceOpsModel>() {
            @Override
            public ServiceOpsModel answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                if (arguments != null && arguments.length > 0 && arguments[0] != null) {
                    ServiceOpsModel serviceOpsModel = new ServiceOpsModel();
                    serviceOpsModel.setPrimaryMobileNumber("0124563987");
                    List<IntermediariesModel> intermediaries = new ArrayList<IntermediariesModel>();
                    List<ClientModel> clients = new ArrayList<ClientModel>();

                    IntermediariesModel intermediariesModel = new IntermediariesModel();
                    intermediariesModel.setClientId("11467");
                    intermediariesModel.setUserId("001002");
                    intermediariesModel.setCity("Chatswood");
                    intermediariesModel.setState("NSW");
                    intermediariesModel.setDealerGroup("bt");
                    intermediariesModel.setEmail("test@btfinancialgroup.com");
                    intermediariesModel.setFirstName("Martin");
                    intermediariesModel.setLastName("Taylor");
                    intermediariesModel.setPhone("0244556677");

                    IntermediariesModel intermediariesModel1 = new IntermediariesModel();
                    intermediariesModel1.setClientId("11468");
                    intermediariesModel1.setUserId("001003");
                    intermediariesModel1.setCity("Chatswood");
                    intermediariesModel1.setState("NSW");
                    intermediariesModel1.setDealerGroup("solar");
                    intermediariesModel1.setEmail("test1@btfinancialgroup.com");
                    intermediariesModel1.setFirstName("Martin1");
                    intermediariesModel1.setLastName("Taylor1");
                    intermediariesModel1.setPhone("0244556688");

                    intermediaries.add(intermediariesModel);
                    intermediaries.add(intermediariesModel1);

                    ClientModel clientModel = new ClientModel();
                    clientModel.setClientId("11469");
                    clientModel.setUserId("001004");
                    clientModel.setCity("Chatswood");
                    clientModel.setState("NSW");
                    clientModel.setEmail("test2@btfinancialgroup.com");
                    clientModel.setFirstName("Martin2");
                    clientModel.setLastName("Taylor2");
                    clientModel.setPhone("024455669");

                    ClientModel clientModel1 = new ClientModel();
                    clientModel1.setClientId("11470");
                    clientModel1.setUserId("001005");
                    clientModel1.setCity("Chatswood");
                    clientModel1.setState("NSW");
                    clientModel1.setEmail("test3@btfinancialgroup.com");
                    clientModel1.setFirstName("Martin3");
                    clientModel1.setLastName("Taylor3");
                    clientModel1.setPhone("0244557788");

                    clients.add(clientModel);
                    clients.add(clientModel1);

                    serviceOpsModel.setClients(clients);
                    serviceOpsModel.setIntermediaries(intermediaries);

                    return serviceOpsModel;
                }
                return null;
            }
        });

        Mockito.when(serviceOpsService.getSortedAccounts(anyString(), any(ServiceErrors.class))).thenAnswer(new Answer<ServiceOpsModel>() {
            @Override
            public ServiceOpsModel answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                if (arguments != null && arguments.length > 0 && arguments[0] != null) {
                    ServiceOpsModel serviceOpsModel = new ServiceOpsModel();
                    serviceOpsModel.setPrimaryMobileNumber("0124563987");
                    List<WrapAccountModel> wrapAccountModels = new ArrayList<>();
                    ArrayList<String> ownerList = new ArrayList<>();
                    WrapAccountModel wrapAccountModel = new WrapAccountModel();

                    wrapAccountModel.setAccountId("1234");
                    wrapAccountModel.setAccountStatus(AccountStatus.ACTIVE.getStatus());
                    wrapAccountModel.setAccountName("Test Account");
                    wrapAccountModel.setAccountType(AccountStructureType.Individual.name());
                    wrapAccountModel.setAccountNumber("120005251");
                    wrapAccountModel.setProduct("BT Panorama Investments");
                    wrapAccountModel.setAdviserName("Test Advisor");
                    ownerList.add("Client 1");
                    wrapAccountModel.setOwners(ownerList);
                    wrapAccountModels.add(wrapAccountModel);

                    wrapAccountModel = new WrapAccountModel();
                    wrapAccountModel.setAccountId("5678");
                    wrapAccountModel.setAccountStatus(AccountStatus.ACTIVE.getStatus());
                    wrapAccountModel.setAccountName("Test Account 2");
                    wrapAccountModel.setAccountType(AccountStructureType.Joint.name());
                    wrapAccountModel.setAccountNumber("120001212");
                    wrapAccountModel.setProduct("BT Panorama Investments");
                    wrapAccountModel.setAdviserName("Test Advisor");
                    ownerList = new ArrayList<>();
                    ownerList.add("Client 1");
                    ownerList.add("Client 2");
                    wrapAccountModel.setOwners(ownerList);
                    wrapAccountModels.add(wrapAccountModel);
                    serviceOpsModel.setAccounts(wrapAccountModels);
                    return serviceOpsModel;
                }
                return null;
            }
        });
    }

    @Test
    public void testDetail() throws Exception {
        ServiceOpsModel serviceOpsModel = mock(ServiceOpsModel.class);
        Mockito.when(serviceOpsService.getUserDetail(eq(userId.plainText()), anyBoolean(), any(ServiceErrors.class))).thenReturn(serviceOpsModel);

        String view = serviceOpsController.detail(userId, new ModelMap());
        assertThat(view, equalTo(View.CLIENT_DETAIL));

        request.setRequestURI("/secure/page/serviceOps/" + userId + "/detail");
        request.setMethod("GET");

        annotationMethodHandlerAdapter.handle(request, response, serviceOpsController);
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));
    }

    @Test
    public void testAccountDetail() throws Exception {
        ServiceOpsModel serviceOpsModel = mock(ServiceOpsModel.class);
        Mockito.when(serviceOpsService.getAccountDetail(eq(userId.plainText()), any(ServiceErrors.class))).thenReturn(serviceOpsModel);

        String view = serviceOpsController.accountDetail(userId.plainText(), new ModelMap());
        assertThat(view, equalTo(View.ACCOUNT_DETAIL));

        request.setRequestURI("/secure/page/serviceOps/" + userId + "/accountDetail");
        request.setMethod("GET");

        annotationMethodHandlerAdapter.handle(request, response, serviceOpsController);
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));
    }

    @Test
    public void testSearchPersons() throws Exception {
        ServiceOpsModel serviceOpsModel = new ServiceOpsModel();
        Mockito.when(serviceOpsService.getUsers(SEARCH_CRITERIA,"btfg$invstr")).thenReturn(serviceOpsModel);

        AjaxResponse response = serviceOpsController.searchPersons(SEARCH_CRITERIA);

        //Check AjaxResponse
        assertThat(response.isSuccess(), is(true));
        assertThat(response.getData(), notNullValue());
    }

    @Test
    public void testSearchPersons_NoPersonDetails() throws Exception {
        Mockito.when(serviceOpsService.getUsers(SEARCH_CRITERIA,"")).thenReturn(null);

        AjaxResponse response = serviceOpsController.searchPersons(SEARCH_CRITERIA);

        //Check AjaxResponse
        assertThat(response.isSuccess(), is(false));
    }

    @Test(expected = MissingServletRequestParameterException.class)
    public void testExceptionForWrongURL() throws Exception {

        request.setRequestURI("/secure/api/searchClients");
        request.setMethod("GET");

        annotationMethodHandlerAdapter.handle(request, response, serviceOpsController);
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));
    }

    @Test(expected = NoSuchRequestHandlingMethodException.class)
    public void testExceptionForWrongSearchURL() throws Exception {

        request.setRequestURI("/secure/page/serviceOps//detail");
        request.setMethod("GET");

        annotationMethodHandlerAdapter.handle(request, response, serviceOpsController);
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));
    }

    @Test
    public void testSearch() throws Exception {
        ModelAndView modelAndView = serviceOpsController.search(SEARCH_CRITERIA, "clientsSearch");
        assertThat(modelAndView.getViewName(), equalTo(View.SERVICE_OP_HOME));
        ServiceOpsModel serviceOpsModel = (ServiceOpsModel) modelAndView.getModel().get("serviceOpsModel");
        assertThat(serviceOpsModel.getClients().size(), Is.is(2));
        List<ClientModel> clientModel = serviceOpsModel.getClients();
        List<IntermediariesModel> intermediariesModel = serviceOpsModel.getIntermediaries();

        assertThat(intermediariesModel.get(0).getClientId(), Is.is("11467"));
        assertThat(intermediariesModel.get(0).getUserId(), Is.is("001002"));
        assertThat(intermediariesModel.get(0).getCity(), Is.is("Chatswood"));
        assertThat(intermediariesModel.get(0).getState(), Is.is("NSW"));
        assertThat(intermediariesModel.get(0).getDealerGroup(), Is.is("bt"));
        assertThat(intermediariesModel.get(0).getEmail(), Is.is("test@btfinancialgroup.com"));
        assertThat(intermediariesModel.get(0).getFirstName(), Is.is("Martin"));
        assertThat(intermediariesModel.get(0).getLastName(), Is.is("Taylor"));
        assertThat(intermediariesModel.get(0).getPhone(), Is.is("0244556677"));

        assertThat(intermediariesModel.get(1).getClientId(), Is.is("11468"));
        assertThat(intermediariesModel.get(1).getUserId(), Is.is("001003"));
        assertThat(intermediariesModel.get(1).getCity(), Is.is("Chatswood"));
        assertThat(intermediariesModel.get(1).getState(), Is.is("NSW"));
        assertThat(intermediariesModel.get(1).getEmail(), Is.is("test1@btfinancialgroup.com"));
        assertThat(intermediariesModel.get(1).getFirstName(), Is.is("Martin1"));
        assertThat(intermediariesModel.get(1).getLastName(), Is.is("Taylor1"));
        assertThat(intermediariesModel.get(1).getPhone(), Is.is("0244556688"));

        assertThat(clientModel.get(0).getClientId(), Is.is("11469"));
        assertThat(clientModel.get(0).getUserId(), Is.is("001004"));
        assertThat(clientModel.get(0).getCity(), Is.is("Chatswood"));
        assertThat(clientModel.get(0).getState(), Is.is("NSW"));
        assertThat(clientModel.get(0).getEmail(), Is.is("test2@btfinancialgroup.com"));
        assertThat(clientModel.get(0).getFirstName(), Is.is("Martin2"));
        assertThat(clientModel.get(0).getLastName(), Is.is("Taylor2"));
        assertThat(clientModel.get(0).getPhone(), Is.is("024455669"));

        assertThat(clientModel.get(1).getClientId(), Is.is("11470"));
        assertThat(clientModel.get(1).getUserId(), Is.is("001005"));
        assertThat(clientModel.get(1).getCity(), Is.is("Chatswood"));
        assertThat(clientModel.get(1).getState(), Is.is("NSW"));
        assertThat(clientModel.get(1).getEmail(), Is.is("test3@btfinancialgroup.com"));
        assertThat(clientModel.get(1).getFirstName(), Is.is("Martin3"));
        assertThat(clientModel.get(1).getLastName(), Is.is("Taylor3"));
        assertThat(clientModel.get(1).getPhone(), Is.is("0244557788"));

        request.setRequestURI("/secure/page/serviceOps/home");
        request.setMethod("GET");

        annotationMethodHandlerAdapter.handle(request, response, serviceOpsController);
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));
    }

    @Test
    public void testSearchAccounts() throws Exception {
        ModelAndView modelAndView = serviceOpsController.search(SEARCH_CRITERIA, "accountsSearch");
        assertThat(modelAndView.getViewName(), equalTo(View.SERVICE_OP_HOME));
        ServiceOpsModel serviceOpsModel = (ServiceOpsModel) modelAndView.getModel().get("serviceOpsModel");
        assertThat(serviceOpsModel.getAccounts().size(), Is.is(2));
        List<WrapAccountModel> wrapAccountModels = serviceOpsModel.getAccounts();

        assertThat(wrapAccountModels.get(0).getAccountName(), Is.is("Test Account"));
        assertThat(wrapAccountModels.get(0).getAccountNumber(), Is.is("120005251"));
        assertThat(wrapAccountModels.get(0).getAccountId(), Is.is("1234"));
        assertThat(wrapAccountModels.get(0).getAdviserName(), Is.is("Test Advisor"));
        assertThat(wrapAccountModels.get(0).getAccountStatus(), Is.is(AccountStatus.ACTIVE.getStatus()));
        assertThat(wrapAccountModels.get(0).getAccountType(), Is.is(AccountStructureType.Individual.name()));
        assertThat(wrapAccountModels.get(0).getOwners().get(0), Is.is("Client 1"));

        assertThat(wrapAccountModels.get(1).getAccountName(), Is.is("Test Account 2"));
        assertThat(wrapAccountModels.get(1).getAccountNumber(), Is.is("120001212"));
        assertThat(wrapAccountModels.get(1).getAccountId(), Is.is("5678"));
        assertThat(wrapAccountModels.get(1).getAdviserName(), Is.is("Test Advisor"));
        assertThat(wrapAccountModels.get(1).getAccountStatus(), Is.is(AccountStatus.ACTIVE.getStatus()));
        assertThat(wrapAccountModels.get(1).getAccountType(), Is.is(AccountStructureType.Joint.name()));
        assertThat(wrapAccountModels.get(1).getOwners().get(0), Is.is("Client 1"));
        assertThat(wrapAccountModels.get(1).getOwners().get(1), Is.is("Client 2"));

        request.setRequestURI("/secure/page/serviceOps/home");
        request.setMethod("GET");

        annotationMethodHandlerAdapter.handle(request, response, serviceOpsController);
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));
    }
    
   
    
    @Test
    public void testRequireMobileConfirmation_URLMapping() throws Exception {
        request.setMethod(RequestMethod.GET.name());
        request.setParameter("mobile", "0400457123");
        request.setRequestURI("/secure/page/serviceOps/" + userId + "/requireMobileConfirmation");
        ServiceOpsModel serviceOpsModel = mock(ServiceOpsModel.class);
        Mockito.when(serviceOpsService.getUserDetail(eq(userId.plainText()), anyBoolean(), any(ServiceErrors.class))).thenReturn(serviceOpsModel);

        annotationMethodHandlerAdapter.handle(request, response, serviceOpsController);
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));

    }

    @Test(expected = HttpRequestMethodNotSupportedException.class)
    public void testRequireMobileConfirmation_RequestMethod() throws Exception {
        request.setMethod(RequestMethod.POST.name());
        request.setParameter("mobile", "123");
        request.setRequestURI("/secure/page/serviceOps/" + userId + "/requireMobileConfirmation");
        ServiceOpsModel serviceOpsModel = mock(ServiceOpsModel.class);
        Mockito.when(serviceOpsService.getUserDetail(eq(userId.plainText()), anyBoolean(), any(ServiceErrors.class))).thenReturn(serviceOpsModel);

        annotationMethodHandlerAdapter.handle(request, response, serviceOpsController);

    }

    @Test(expected = MissingServletRequestParameterException.class)
    public void testRequireMobileConfirmation_ReqParamMissing() throws Exception {
        request.setMethod(RequestMethod.GET.name());
        request.setRequestURI("/secure/page/serviceOps/" + userId + "/requireMobileConfirmation");
        ServiceOpsModel serviceOpsModel = mock(ServiceOpsModel.class);
        Mockito.when(serviceOpsService.getUserDetail(eq(userId.plainText()), anyBoolean(), any(ServiceErrors.class))).thenReturn(serviceOpsModel);

        annotationMethodHandlerAdapter.handle(request, response, serviceOpsController);

    }

    @Test
    public void testRequireMobileConfirmation_ModelAndView() throws Exception {
        ServiceOpsModel serviceOpsModel = mock(ServiceOpsModel.class);
        Mockito.when(serviceOpsService.getUserDetail(eq(userId.plainText()), anyBoolean(), any(ServiceErrors.class))).thenReturn(serviceOpsModel);

        ModelAndView mav = serviceOpsController.requireMobileConfirmation(userId, "0414654888");

        assertThat(mav.getViewName(), is(View.CLIENT_DETAIL));
        assertThat(mav.getModel(), notNullValue());
        Mockito.verify(serviceOpsModel, Mockito.times(1)).setMobileNumber(anyString());
    }

    @Test(expected = HttpRequestMethodNotSupportedException.class)
    public void testConfirmMobile_RequestMethod() throws Exception {
        request.setMethod(RequestMethod.GET.name());
        request.setParameter("mobile", "123");
        request.setParameter("secret", "secret");
        request.setParameter("userId", "userId");
        request.setParameter("safiDeviceId", "safiDeviceId");
        request.setRequestURI("/secure/page/serviceOps/userId/submitConfirmMobile");
        Mockito.when(deviceArrangementService.updateUserMobileNumber(anyString(), anyString(), anyString(), anyString(),anyString(), any(UserAccountStatusModel.class), any(ServiceErrors.class))).thenReturn(true);
        annotationMethodHandlerAdapter.handle(request, response, serviceOpsController);

    }

    @Test(expected = MissingServletRequestParameterException.class)
    public void testConfirmMobile_ReqParamMissing() throws Exception {
        request.setMethod(RequestMethod.POST.name());
        request.setRequestURI("/secure/page/serviceOps/" + userId + "/submitConfirmMobile");
        Mockito.when(deviceArrangementService.updateUserMobileNumber(anyString(), anyString(), anyString(), anyString(),anyString(),any(UserAccountStatusModel.class), any(ServiceErrors.class))).thenReturn(true);
        annotationMethodHandlerAdapter.handle(request, response, serviceOpsController);

    }



    // TODO: Test context for active profile needs to be defined
    @Test
    public void testConfirmMobile_ModelAndView() throws Exception {
        Mockito.when(deviceArrangementService.updateUserMobileNumber(anyString(), anyString(), anyString(), anyString(),anyString(), any(UserAccountStatusModel.class), any(ServiceErrors.class))).thenReturn(true);
        ServiceOpsModel serviceOpsModel = Mockito.mock(ServiceOpsModel.class);
        IndividualDetailImpl individualDetailImpl = Mockito.mock(IndividualDetailImpl.class);
        when(individualDetailImpl.getCISKey()).thenReturn(CISKey.valueOf("83971220010"));
        RequestContextHolder requestContextHolder = Mockito.mock(RequestContextHolder.class);
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setScheme("http");
        mockRequest.setServerName("geomajas.org");
        mockRequest.setServerPort(80);
        mockRequest.setContextPath("/test");
        ServletRequestAttributes attributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(attributes);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        //Assert.assertEquals("http://geomajas.org/test/d/", adus.getDispatcherUrl());
        MockHttpSession session = mock(MockHttpSession.class);
        samlToken = new SamlToken(SamlUtil.loadWplSamlNewPanoramaCustomer());
        when(userProfileService.getSamlToken()).thenReturn(samlToken);
        UserProfile activeProfile = getProfile(JobRole.ADVISER, "job id 1", "client1");
        // when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        when(userProfileService.getActiveProfile()).thenReturn(mock(UserProfile.class));
        when(userProfileService.getUserId()).thenReturn("201603880");
        when(userProfileService.isEmulating()).thenReturn(true);
        when(userProfileService.getEffectiveProfile()).thenReturn(mock(Profile.class));
        when(userProfileService.getEffectiveProfile().getUserId()).thenReturn("201603880");
        when(userProfileService.getActiveProfile().getUsername()).thenReturn("201603880");
        Mockito.when(credentialService.getCredentialId(anyString(), any(ServiceErrorsImpl.class))).thenReturn("credentialId");
        when(userProfileService.getUsername()).thenReturn("Darryl");
        Mockito.when(deviceArrangementService.updateUserMobileNumber(anyString(), anyString(), anyString(), anyString(),anyString(), any(UserAccountStatusModel.class), any(ServiceErrors.class))).thenReturn(true);
        Mockito.when(cmsService.getContent(anyString())).thenReturn("cmsContent");
        samlToken = new SamlToken(SamlUtil.loadWplSamlNewPanoramaCustomer());
        Mockito.when(userProfileService.getSamlToken()).thenReturn(samlToken);
        UserInformationImpl userInformation = new UserInformationImpl();
        JobProfileImpl jobProfile = new JobProfileImpl();
        UserProfileAdapterImpl userProfileAdapter = getUserProfileAdapter(userInformation,jobProfile);
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(userProfileAdapter);
        UserAccountStatusModel userAcctStatusModel = new UserAccountStatusModel();
        userAcctStatusModel.setUserAccountStatus(UserAccountStatus.ACTIVE);
        Mockito.when(credentialService.lookupStatus(anyString(), any(ServiceErrorsImpl.class))).thenReturn(userAcctStatusModel);
        serviceOpsModel.setFirstName("Martin");
        serviceOpsModel.setLastName("Taylor");
        serviceOpsModel.setSafiDeviceId("safiDeviceId");
        List<Phone> mobilePhones = new ArrayList<>();
        PhoneImpl phoneModel = new PhoneImpl();
        phoneModel.setNumber("61414565482");
        mobilePhones.add(phoneModel);
        serviceOpsModel.setMobilePhones(mobilePhones);
        Mockito.when(serviceOpsService.getUserDetail(anyString(), anyBoolean(), any(ServiceErrors.class))).thenReturn(serviceOpsModel);
       
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        when(permissionBaseService.hasBasicPermission("feature.global.prmNonValueEvents")).thenReturn(true);
        String view = serviceOpsController.confirmMobile(userId, "61414565482", redirectAttributes);
        assertThat(view, is("redirect:/secure/page/serviceOps/" + userId + "/detail"));
        // Mockito.verify(redirectAttributes, Mockito.times(2)).addFlashAttribute(anyString(), anyString());
    }
    
    @Test
    public void testConfirmMobile_MobileISNotNumber() throws Exception {
        Mockito.when(deviceArrangementService.updateUserMobileNumber(anyString(), anyString(), anyString(), anyString(),anyString(), any(UserAccountStatusModel.class), any(ServiceErrors.class))).thenReturn(true);
        ServiceOpsModel serviceOpsModel = Mockito.mock(ServiceOpsModel.class);
        IndividualDetailImpl individualDetailImpl = Mockito.mock(IndividualDetailImpl.class);
        when(individualDetailImpl.getCISKey()).thenReturn(CISKey.valueOf("83971220010"));
        RequestContextHolder requestContextHolder = Mockito.mock(RequestContextHolder.class);
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setScheme("http");
        mockRequest.setServerName("geomajas.org");
        mockRequest.setServerPort(80);
        mockRequest.setContextPath("/test");
        ServletRequestAttributes attributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(attributes);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        //Assert.assertEquals("http://geomajas.org/test/d/", adus.getDispatcherUrl());
        MockHttpSession session = mock(MockHttpSession.class);
        samlToken = new SamlToken(SamlUtil.loadWplSamlNewPanoramaCustomer());
        when(userProfileService.getSamlToken()).thenReturn(samlToken);
        UserProfile activeProfile = getProfile(JobRole.ADVISER, "job id 1", "client1");
        // when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        when(userProfileService.getActiveProfile()).thenReturn(mock(UserProfile.class));
        when(userProfileService.getUserId()).thenReturn("201603880");
        when(userProfileService.isEmulating()).thenReturn(true);
        when(userProfileService.getEffectiveProfile()).thenReturn(mock(Profile.class));
        when(userProfileService.getEffectiveProfile().getUserId()).thenReturn("201603880");
        when(userProfileService.getActiveProfile().getUsername()).thenReturn("201603880");
        Mockito.when(credentialService.getCredentialId(anyString(), any(ServiceErrorsImpl.class))).thenReturn("credentialId");
        when(userProfileService.getUsername()).thenReturn("Darryl");
        Mockito.when(deviceArrangementService.updateUserMobileNumber(anyString(), anyString(), anyString(), anyString(),anyString(), any(UserAccountStatusModel.class), any(ServiceErrors.class))).thenReturn(true);
        Mockito.when(cmsService.getContent(anyString())).thenReturn("cmsContent");
        samlToken = new SamlToken(SamlUtil.loadWplSamlNewPanoramaCustomer());
        Mockito.when(userProfileService.getSamlToken()).thenReturn(samlToken);
        UserInformationImpl userInformation = new UserInformationImpl();
        JobProfileImpl jobProfile = new JobProfileImpl();
        UserProfileAdapterImpl userProfileAdapter = getUserProfileAdapter(userInformation,jobProfile);
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(userProfileAdapter);
        UserAccountStatusModel userAcctStatusModel = new UserAccountStatusModel();
        userAcctStatusModel.setUserAccountStatus(UserAccountStatus.ACTIVE);
        Mockito.when(credentialService.lookupStatus(anyString(), any(ServiceErrorsImpl.class))).thenReturn(userAcctStatusModel);
        serviceOpsModel.setFirstName("Martin");
        serviceOpsModel.setLastName("Taylor");
        serviceOpsModel.setSafiDeviceId("safiDeviceId");
        List<Phone> mobilePhones = new ArrayList<>();
        PhoneImpl phoneModel = new PhoneImpl();
        phoneModel.setNumber("61414565482");
        mobilePhones.add(phoneModel);
        serviceOpsModel.setMobilePhones(mobilePhones);
        Mockito.when(serviceOpsService.getUserDetail(anyString(), anyBoolean(), any(ServiceErrors.class))).thenReturn(serviceOpsModel);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        when(permissionBaseService.hasBasicPermission("feature.global.prmNonValueEvents")).thenReturn(true);
        String view = serviceOpsController.confirmMobile(userId, "ABCDEFGHI", redirectAttributes);
        assertThat(view, is("redirect:/secure/page/serviceOps/" + userId + "/detail"));
        // Mockito.verify(redirectAttributes, Mockito.times(2)).addFlashAttribute(anyString(), anyString());
    }

    private UserProfileAdapterImpl getUserProfileAdapter(UserInformationImpl userInformation,JobProfileImpl jobProfile )
    {
        UserProfileAdapterImpl userProfileAdapter= new UserProfileAdapterImpl(userInformation,jobProfile);
        CustomerCredentialInformation customerCredentialInformation = new CustomerCredentialInformation() {
            @Override
            public UserAccountStatus getPrimaryStatus() {
                return null;
            }

            @Override
            public CredentialType getCredentialType() {
                return null;
            }

            @Override
            public List<Roles> getCredentialGroups() {
                return null;
            }

            @Override
            public ChannelType getChannelType() {
                return null;
            }

            @Override
            public String getLastUsed() {
                return null;
            }

            @Override
            public String getStartTimeStamp() {
                return null;
            }

            @Override
            public List<UserAccountStatus> getAllAccountStatusList() {
                return null;
            }

            @Override
            public String getServiceLevel() {
                return null;
            }

            @Override
            public String getServiceStatusErrorCode() {
                return null;
            }

            @Override
            public String getServiceStatusErrorDesc() {
                return null;
            }

            @Override
            public String getStatusInfo() {
                return null;
            }

            @Override
            public UserAccountStatus getUserAccountStatus() {
                return null;
            }

            @Override
            public DateTime getDate() {
                return null;
            }

            @Override
            public List<UserGroup> getUserGroup() {
                return null;
            }

            @Override
            public String getUserReferenceId() {
                return null;
            }

            @Override
            public String getNameId() {
                return null;
            }

            @Override
            public String getPpId() {
                return null;
            }

            @Override
            public String getUsername() {
                return null;
            }

            @Override
            public String getBankReferenceId() {
                return "BankReferenceID";
            }

            @Override
            public UserKey getBankReferenceKey() {
                return null;
            }

            @Override
            public CISKey getCISKey() {
                return null;
            }

            @Override
            public String getCredentialId() {
                return null;
            }
        };
        userProfileAdapter.setCustomerCredentialInformation(customerCredentialInformation);
        return  userProfileAdapter;

    }

    @Test(expected = HttpRequestMethodNotSupportedException.class)
    public void testSubmitAction_RequestMethod() throws Exception {
        request.setMethod(RequestMethod.GET.name());
        request.setRequestURI("/secure/page/serviceOps/" + userId + "/submitAction");
        annotationMethodHandlerAdapter.handle(request, response, serviceOpsController);

    }

    @Test(expected = NullPointerException.class)
    public void testSubmitAction_SignIn() throws Exception {
        Mockito.when(serviceOpsService.getUserDetail(eq("83351"), eq(true), any(ServiceErrors.class))).thenReturn(serviceOpsModel);
        String view = serviceOpsController.submitAction(userId, Action.SIGN_IN_AS_USER, serviceOpsModel, redirectAttributes);
        assertThat(view, is("redirect:/secure/page/serviceOps/{clientId}/detail"));
    }

    @Test
    public void testSubmitAction_SignIn_Investor() throws Exception {
        Mockito.when(serviceOpsModel.getUserName()).thenReturn("Investor");
        JobProfileImpl jobProfile = new JobProfileImpl();
        jobProfile.setJobRole(JobRole.INVESTOR);
        jobProfile.setProfileId("1234");
        List<JobProfile> jobProfileList = new ArrayList<>();
        jobProfileList.add(jobProfile);
        Mockito.when(serviceOpsModel.getJobProfiles()).thenReturn(jobProfileList);
        Mockito.when(serviceOpsModel.getGcmId()).thenReturn("10000667");
        EncodedString tempUserId = EncodedString.fromPlainText("83351");

        Mockito.when(serviceOpsService.getUserDetail(eq("83351"), eq(true), any(ServiceErrors.class))).thenReturn(serviceOpsModel);

        String view = serviceOpsController.submitAction(tempUserId, Action.SIGN_IN_AS_USER, serviceOpsModel, redirectAttributes);
        assertThat(view, is("redirect:/secure/page/serviceOps/startEmulation"));
    }


    @Test(expected = NullPointerException.class)
    public void testSubmitAction_SignIn_Investor_Role() throws Exception {
        Mockito.when(serviceOpsModel.getUserName()).thenReturn("Investor");
        Mockito.when(serviceOpsModel.getRole()).thenReturn("Investor");
        Mockito.when(serviceOpsModel.getUserId()).thenReturn("userId");
        Mockito.when(serviceOpsService.getUserDetail(eq("83351"), eq(true), any(ServiceErrors.class))).thenReturn(serviceOpsModel);

        String view = serviceOpsController.submitAction(userId, Action.SIGN_IN_AS_USER, serviceOpsModel, redirectAttributes);
        assertThat(view, is("redirect:/secure/page/serviceOps/{clientId}/detail"));
    }

    @Test(expected = NullPointerException.class)
    public void testSubmitAction_SignIn_DgUser() throws Exception {
        Mockito.when(serviceOpsModel.getUserName()).thenReturn("dealergroup");
        Mockito.when(serviceOpsModel.getRole()).thenReturn("Dealer Group Manager");
        Mockito.when(serviceOpsModel.getUserId()).thenReturn("userId");

        Mockito.when(serviceOpsModel.getGcmId()).thenReturn("10000667");
        EncodedString tempUserId = EncodedString.fromPlainText("83351");

        ServiceOpsModel serviceOpsClientModel = new ServiceOpsModel();
        serviceOpsClientModel.setGcmId("10000667");

        Mockito.when(serviceOpsService.getUserDetail(eq("83351"), eq(true), any(ServiceErrors.class))).thenReturn(serviceOpsClientModel);

        String view = serviceOpsController.submitAction(userId, Action.SIGN_IN_AS_USER, serviceOpsModel, redirectAttributes);
        assertThat(view, is("redirect:/secure/page/serviceOps/{clientId}/detail"));
    }

    @Test
    public void testSubmitAction_ResetPassword() throws Exception {

        ServiceOpsModel serviceOpsModel = Mockito.mock(ServiceOpsModel.class);
        IndividualDetailImpl individualDetailImpl = Mockito.mock(IndividualDetailImpl.class);
        when(individualDetailImpl.getCISKey()).thenReturn(CISKey.valueOf("83971220010"));
        RequestContextHolder requestContextHolder = Mockito.mock(RequestContextHolder.class);
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setScheme("http");
        mockRequest.setServerName("geomajas.org");
        mockRequest.setServerPort(80);
        mockRequest.setContextPath("/test");
        ServletRequestAttributes attributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(attributes);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        //Assert.assertEquals("http://geomajas.org/test/d/", adus.getDispatcherUrl());
        MockHttpSession session = mock(MockHttpSession.class);
        //HttpSession session = Mockito.mock(HttpSession.class);
        Mockito.when(blockService.blockUserAccess(userId.plainText(), new ServiceErrorsImpl())).thenReturn(true);
        samlToken = new SamlToken(SamlUtil.loadWplSamlNewPanoramaCustomer());
        when(userProfileService.getSamlToken()).thenReturn(samlToken);
        UserProfile activeProfile = getProfile(JobRole.ADVISER, "job id 1", "client1");
        // when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        when(userProfileService.getActiveProfile()).thenReturn(mock(UserProfile.class));
        when(userProfileService.getUserId()).thenReturn("201603880");
        when(userProfileService.isEmulating()).thenReturn(true);
        when(userProfileService.getEffectiveProfile()).thenReturn(mock(Profile.class));
        when(userProfileService.getEffectiveProfile().getUserId()).thenReturn("201603880");
        when(userProfileService.getActiveProfile().getUsername()).thenReturn("201603880");
        when(userProfileService.getUsername()).thenReturn("Darryl");
        FeatureToggles featureToggles =new FeatureToggles();
        featureToggles.setFeatureToggle("prmNonValueEvents",true);
        when(featureTogglesService.findOne(any(ServiceErrorsImpl.class))).thenReturn(featureToggles);
        when(permissionBaseService.hasBasicPermission("feature.global.prmNonValueEvents")).thenReturn(true);
        CustomerCredentialInformation customerCredential = mock(CustomerCredentialInformation.class);
        when(customerCredential.getUsername()).thenReturn("Martin Taylor");
        when(customerLoginService.getCustomerInformation(any(CredentialRequest.class), any(ServiceErrors.class))).thenReturn(
                customerCredential);
        if (/*Properties.getSafeBoolean("feature.prmNonValueEvents"*/ permissionBaseService.hasBasicPermission("feature.global.prmNonValueEvents")) {
            //2FA Active event trigger for PRM
            serviceOpsModel.setUserId("201603880");
            serviceOpsModel.setMobileNumber("61414565482");
            serviceOpsModel.setCisId("83971220010");
            PrmDto prmDto = Mockito.mock(PrmDto.class);
            when(prmDto.getEventType()).thenReturn(PrmEventType.ACCESSBLOCK);


        }
        Mockito.when(resetPasswordService.resetPassword(anyString(), anyString(), any(ServiceErrorsImpl.class))).thenReturn("tmpCreatedPassword");

        Mockito.when(cmsService.getContent(anyString())).thenReturn("cmsContent");
        ServiceOpsModel serviceops = new ServiceOpsModel();
        serviceops.setGcmId("12345");
        Mockito.when(serviceOpsService.getUserDetail(anyString(), anyBoolean(), any(ServiceErrors.class))).thenReturn(serviceops);
        String view = serviceOpsController.submitAction(userId, Action.RESET_PASSWORD, serviceOpsModel, redirectAttributes);
        assertThat(view, is("redirect:/secure/page/serviceOps/{clientId}/detail"));
        Mockito.verify(redirectAttributes, Mockito.times(2)).addFlashAttribute(anyString(), anyString());
    }

    @Test
    public void testUpdatePPID() throws Exception {
        //test some error input
        Mockito.when(serviceOpsService.updatePPID(anyString(), anyString(), any(ServiceErrors.class))).thenReturn(true);
        serviceOpsController.updateppid(userId, "123abc#", redirectAttributes);
        verify(redirectAttributes).addFlashAttribute(Attribute.ERR_MESSAGE, "Only numeric values are allowed for PPID");

       //test when all characters are special characters
        Mockito.when(serviceOpsService.updatePPID(anyString(),anyString(), any(ServiceErrors.class))).thenReturn(true);
        serviceOpsController.updateppid(userId, "$%^&&#", redirectAttributes);
        verify(redirectAttributes,Mockito.times(2)).addFlashAttribute(Attribute.ERR_MESSAGE, "Only numeric values are allowed for PPID");

        //test some valid input
        Mockito.when(serviceOpsService.updatePPID(anyString(),anyString(), any(ServiceErrors.class))).thenReturn(true);
        serviceOpsController.updateppid(userId, "1234", redirectAttributes);
        verify(redirectAttributes).addFlashAttribute(Attribute.MESSAGE, "The PPID has been successfully updated");

       //test when there is some error in the backend (Eam or ABS)
        Mockito.when(serviceOpsService.updatePPID(anyString(), anyString(), any(ServiceErrors.class))).thenReturn(false);
        serviceOpsController.updateppid(userId, "1234", redirectAttributes);
        verify(redirectAttributes).addFlashAttribute(Attribute.ERR_MESSAGE, "There was some problem in updating the PPID");
    }

    @Test
    public void testSubmitAction_BlockUserAccess() throws Exception {
        //serviceOpsModel = new ServiceOpsModel();
        ServiceOpsModel serviceOpsModel = Mockito.mock(ServiceOpsModel.class);
        IndividualDetailImpl individualDetailImpl = Mockito.mock(IndividualDetailImpl.class);
        when(individualDetailImpl.getCISKey()).thenReturn(CISKey.valueOf("83971220010"));
        RequestContextHolder requestContextHolder = Mockito.mock(RequestContextHolder.class);
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setScheme("http");
        mockRequest.setServerName("geomajas.org");
        mockRequest.setServerPort(80);
        mockRequest.setContextPath("/test");
        ServletRequestAttributes attributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(attributes);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        //Assert.assertEquals("http://geomajas.org/test/d/", adus.getDispatcherUrl());
        MockHttpSession session = mock(MockHttpSession.class);
        //HttpSession session = Mockito.mock(HttpSession.class);
        Mockito.when(blockService.blockUserAccess(userId.plainText(), new ServiceErrorsImpl())).thenReturn(true);
        samlToken = new SamlToken(SamlUtil.loadWplSamlNewPanoramaCustomer());
        when(userProfileService.getSamlToken()).thenReturn(samlToken);
        UserProfile activeProfile = getProfile(JobRole.ADVISER, "job id 1", "client1");
       // when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        when(userProfileService.getActiveProfile()).thenReturn(mock(UserProfile.class));
        when(userProfileService.getUserId()).thenReturn("201603880");
        when(userProfileService.isEmulating()).thenReturn(true);
        when(userProfileService.getEffectiveProfile()).thenReturn(mock(Profile.class));
        when(userProfileService.getEffectiveProfile().getUserId()).thenReturn("201603880");
        when(userProfileService.getActiveProfile().getUsername()).thenReturn("201603880");
        when(userProfileService.getUsername()).thenReturn("Darryl");
        FeatureToggles featureToggles =new FeatureToggles();
        featureToggles.setFeatureToggle("prmNonValueEvents",true);
        when(featureTogglesService.findOne(any(ServiceErrorsImpl.class))).thenReturn(featureToggles);
        when(permissionBaseService.hasBasicPermission("feature.global.prmNonValueEvents")).thenReturn(true);
        CustomerCredentialInformation customerCredential = mock(CustomerCredentialInformation.class);
        when(customerCredential.getUsername()).thenReturn("Martin Taylor");
        when(customerLoginService.getCustomerInformation(any(CredentialRequest.class), any(ServiceErrors.class))).thenReturn(
                customerCredential);
        if (/*Properties.getSafeBoolean("feature.prmNonValueEvents"*/ permissionBaseService.hasBasicPermission("feature.global.prmNonValueEvents")) {
            //2FA Active event trigger for PRM
               serviceOpsModel.setUserId("201603880");
               serviceOpsModel.setMobileNumber("61414565482");
               serviceOpsModel.setCisId("83971220010");
            PrmServiceImpl prmService = Mockito.mock(PrmServiceImpl.class);
            PrmDto prmDto = Mockito.mock(PrmDto.class);
               when(prmDto.getEventType()).thenReturn(PrmEventType.ACCESSBLOCK);


        }
           Mockito.when(credentialService.getCredentialId(anyString(), any(ServiceErrorsImpl.class))).thenReturn("credentialId");

        String view = serviceOpsController.submitAction(userId, Action.BLOCK_ACCESS, serviceOpsModel, redirectAttributes);
        assertThat(view, is("redirect:/secure/page/serviceOps/{clientId}/detail"));
    }

    @Test
    public void testSubmitAction_UnBlockUserAccess() throws Exception {
        serviceOpsModel = new ServiceOpsModel();
        serviceOpsModel.setAction("Active");
       // ServiceOpsModel serviceOpsModel = Mockito.mock(ServiceOpsModel.class);
        IndividualDetailImpl individualDetailImpl = Mockito.mock(IndividualDetailImpl.class);
        when(individualDetailImpl.getCISKey()).thenReturn(CISKey.valueOf("83971220010"));
        RequestContextHolder requestContextHolder = Mockito.mock(RequestContextHolder.class);
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setScheme("http");
        mockRequest.setServerName("geomajas.org");
        mockRequest.setServerPort(80);
        mockRequest.setContextPath("/test");
        ServletRequestAttributes attributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(attributes);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        //Assert.assertEquals("http://geomajas.org/test/d/", adus.getDispatcherUrl());
        MockHttpSession session = mock(MockHttpSession.class);
        Mockito.when(credentialService.getCredentialId(anyString(), any(ServiceErrorsImpl.class))).thenReturn("credentialId");
       // Mockito.when(credentialService.getCredentialId(anyString(), any(ServiceErrorsImpl.class))).thenReturn("credentialId");
        Mockito.when(blockService.unblockUserAccess(userId.plainText(), new ServiceErrorsImpl())).thenReturn(true);
        when(userProfileService.getActiveProfile()).thenReturn(mock(UserProfile.class));
        samlToken = new SamlToken(SamlUtil.loadWplSamlNewPanoramaCustomer());
        when(userProfileService.getSamlToken()).thenReturn(samlToken);
        UserProfile activeProfile = getProfile(JobRole.ADVISER, "job id 1", "client1");
       // when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        when(userProfileService.getActiveProfile().getUsername()).thenReturn("201603880");
        when(userProfileService.getUserId()).thenReturn("201603880");
        when(userProfileService.isEmulating()).thenReturn(true);
        when(userProfileService.getEffectiveProfile()).thenReturn(mock(Profile.class));
        when(userProfileService.getEffectiveProfile().getUserId()).thenReturn("201603880");
        when(userProfileService.getUsername()).thenReturn("Darryl");
        FeatureToggles featureToggles =new FeatureToggles();
        featureToggles.setFeatureToggle("prmNonValueEvents",true);
        when(featureTogglesService.findOne(any(ServiceErrorsImpl.class))).thenReturn(featureToggles);
        when(permissionBaseService.hasBasicPermission("feature.global.prmNonValueEvents")).thenReturn(true);
        CustomerCredentialInformation customerCredential = mock(CustomerCredentialInformation.class);
        when(customerCredential.getUsername()).thenReturn("Martin Taylor");
        when(customerLoginService.getCustomerInformation(any(CredentialRequest.class), any(ServiceErrors.class))).thenReturn(
                customerCredential);
            if (/*Properties.getSafeBoolean("feature.prmNonValueEvents"*/ permissionBaseService.hasBasicPermission("feature.global.prmNonValueEvents")) {
            //2FA Active event trigger for PRM
            PrmServiceImpl prmService = Mockito.mock(PrmServiceImpl.class);
            PrmDto prmDto = Mockito.mock(PrmDto.class);
                when(prmDto.getEventType()).thenReturn(PrmEventType.ACCESSBLOCK);
                serviceOpsModel.setUserId("201603880");
                serviceOpsModel.setMobileNumber("61414565482");
                serviceOpsModel.setCisId("83971220010");
        }
        String view = serviceOpsController.submitAction(userId, Action.UNBLOCK_ACCESS, serviceOpsModel, redirectAttributes);
        verify(prmService, times(1)).triggerAccessUnblockPrmEvent(Mockito.any(ServiceOpsModel.class));
        assertThat(view, is("redirect:/secure/page/serviceOps/{clientId}/detail"));
    }

    @Test(expected = NullPointerException.class)
    public void testSubmitAction_confirmSecurityMobileNo() throws Exception {

        Mockito.when(serviceOpsModel.getPrimaryMobileNumber()).thenReturn("61412355641");
        String view = serviceOpsController.submitAction(userId, Action.CONFIRM_SECURITY_MOBILE_NUMBER, serviceOpsModel, redirectAttributes);
        assertThat(view, is("redirect:/secure/page/serviceOps/{clientId}/requireMobileConfirmation?mobile=61412355641#showModal"));
    }

    @Test
    public void testSubmitAction_unlockSecurityMobileNo() throws Exception {
        ServiceOpsModel serviceops = new ServiceOpsModel();
        serviceops.setPrimaryMobileNumber("12345");
        Mockito.when(serviceOpsService.getUserDetail(anyString(), anyBoolean(), any(ServiceErrors.class))).thenReturn(serviceops);
        Mockito.when(userProfileService.getAvaloqId()).thenReturn("6000021");
        Mockito.when(updateDeviceArrangementService.unBlockMobile(anyString(), any(ServiceOpsModel.class), anyString())).thenReturn("123");
        Mockito.when(cmsService.getContent(anyString())).thenReturn("cmsContent");
        String view = serviceOpsController.submitAction(userId, Action.UNLOCK_SECURITY_MOBILE_NUMBER, serviceops, redirectAttributes);
        assertThat(view, is("redirect:/secure/page/serviceOps/{clientId}/detail"));
        Mockito.verify(redirectAttributes, Mockito.times(2)).addFlashAttribute(anyString(), anyString());
    }

    @Test
    public void testSubmitAccountDetailAction_ActiveUser_SignIn() throws Exception {
        Mockito.when(serviceOpsModel.getUserName()).thenReturn("Investor01");
        JobProfileImpl jobProfile = new JobProfileImpl();
        jobProfile.setJobRole(JobRole.INVESTOR);
        jobProfile.setProfileId("1234");
        List<JobProfile> jobProfileList = new ArrayList<>();
        jobProfileList.add(jobProfile);
        Mockito.when(serviceOpsModel.getJobProfiles()).thenReturn(jobProfileList);
        Mockito.when(serviceOpsModel.getGcmId()).thenReturn("10000667");
        Mockito.when(serviceOpsModel.getLoginStatus()).thenReturn(UserAccountStatus.ACTIVE);
        EncodedString bpId = EncodedString.fromPlainText("83351");
        EncodedString clientId = EncodedString.fromPlainText("83351");

        Mockito.when(serviceOpsService.getUserDetail(eq("83351"), eq(false), any(ServiceErrors.class))).thenReturn(serviceOpsModel);

        String view = serviceOpsController.submitAccountDetailAction(clientId.toString(), serviceOpsModel, redirectAttributes);
        assertThat(view, is("redirect:/secure/page/serviceOps/startEmulation"));
    }

    @Test
    public void testSubmitAccountDetailAction_BlockedUser_SignIn() throws Exception {
        Mockito.when(serviceOpsModel.getUserName()).thenReturn("Investor01");
        JobProfileImpl jobProfile = new JobProfileImpl();
        jobProfile.setJobRole(JobRole.INVESTOR);
        jobProfile.setProfileId("1234");
        List<JobProfile> jobProfileList = new ArrayList<>();
        jobProfileList.add(jobProfile);
        Mockito.when(serviceOpsModel.getJobProfiles()).thenReturn(jobProfileList);
        Mockito.when(serviceOpsModel.getGcmId()).thenReturn("10000667");
        Mockito.when(serviceOpsModel.getLoginStatus()).thenReturn(UserAccountStatus.BLOCKED);
        EncodedString bpId = EncodedString.fromPlainText("83351");
        EncodedString clientId = EncodedString.fromPlainText("83351");

        Mockito.when(serviceOpsService.getUserDetail(eq("83351"), anyBoolean(), any(ServiceErrors.class))).thenReturn(serviceOpsModel);

        String view = serviceOpsController.submitAccountDetailAction(clientId.toString(), serviceOpsModel, redirectAttributes);
        assertThat(view, is("redirect:/secure/page/serviceOps/{clientId}/accountDetail"));
    }

    @Test
    public void testSubmitAccountDetailAction_Exception() throws Exception {
        EncodedString clientId = EncodedString.fromPlainText("83351");
        Mockito.when(serviceOpsService.getUserDetail(eq("83351"), eq(false), any(ServiceErrors.class))).thenThrow(new Exception(""));
        String view = serviceOpsController.submitAccountDetailAction(clientId.toString(), serviceOpsModel, redirectAttributes);
        assertThat(view, is("redirect:/secure/page/serviceOps/{clientId}/accountDetail"));
    }

    @Test
    public void testGetAccounts() throws Exception {
        WrapAccountModel model = new WrapAccountModel();
        model.setAccountId("1213");
        model.setAccountName("XYZ");
        List<WrapAccountModel> list = new ArrayList<>();
        list.add(model);
        Mockito.when(serviceOpsService.findWrapAccountDetail(anyString())).thenReturn(list);

        Mockito.when(serviceOpsService.findWrapAccountDetailsByGcm(anyString())).thenReturn(list);

        ServiceOpsModel serviceOpsModel = new ServiceOpsModel();
        serviceOpsModel.setPpId("PPID");
        Mockito.when(serviceOpsService.getSortedUsers(anyString())).thenReturn(serviceOpsModel);

        ModelAndView modelAndView = serviceOpsController.getAccounts("123", "gcmId", "Account");
        Mockito.verify(serviceOpsService, Mockito.times(1)).findWrapAccountDetailsByGcm(anyString());

        modelAndView = serviceOpsController.getAccounts("123", null, "Account");
        Mockito.verify(serviceOpsService, Mockito.times(1)).getSortedUsers(anyString());

        modelAndView = serviceOpsController.getAccounts("123", "bpNumber", "Account");
        Mockito.verify(serviceOpsService, Mockito.times(1)).findWrapAccountDetail(anyString());


        Mockito.reset(serviceOpsService);

        // when validation fails no additional calls to method happens
        modelAndView = serviceOpsController.getAccounts("1", "bpNumber", "Account");
        Mockito.verify(serviceOpsService, Mockito.never()).findWrapAccountDetail(anyString());
        Mockito.verify(serviceOpsService, Mockito.never()).getSortedUsers(anyString());
        Mockito.verify(serviceOpsService, Mockito.never()).findWrapAccountDetail(anyString());


        Mockito.reset(serviceOpsService);
        // when the validation sucseeds addtional call's will be made to either one of the methods

        modelAndView = serviceOpsController.getAccounts("addd", "name", "Person");

        Mockito.verify(serviceOpsService, Mockito.never()).findWrapAccountDetail(anyString());
        Mockito.verify(serviceOpsService, Mockito.times(1)).getSortedUsers(anyString());
        Mockito.verify(serviceOpsService, Mockito.never()).findWrapAccountDetail(anyString());

    }



    @Test
    public void testSubmitAction_createAccount() throws Exception {
        Mockito.when(userAccountStatusService.createAccount(any(ServiceOpsModel.class))).thenReturn(Attribute.SUCCESS_MESSAGE);
        String view = serviceOpsController.submitAction(userId, Action.CREATE_ACCOUNT, serviceOpsModel, redirectAttributes);
        assertThat(view, is("redirect:/secure/page/serviceOps/{clientId}/detail"));
        Mockito.verify(redirectAttributes, Mockito.times(2)).addFlashAttribute(anyString(), anyString());
    }

    @Test
    public void testSearchApplication() {
        String view = serviceOpsController.searchApplication();
        assertThat(view, Is.is(View.APPLICATIONSEARCH));
    }

    @Test
    public void testGetClientApplicationDetails() {
        ModelMap modelMap = new ModelMap();
        String clientApplicationId = "2227";
        List<PersonRelationDto> personRelationDtos = new ArrayList<PersonRelationDto>();
        PersonRelationDto personRelationDto = new PersonRelationDto();
        personRelationDto.setAdviser(false);
        personRelationDto.setName("John Doe");
        personRelationDtos.add(personRelationDto);
        AccountSettingsDto accountSettingsDto = new AccountSettingsDto();
        accountSettingsDto.setPersonRelations(personRelationDtos);


        List<LinkedAccountDto> linkedAccounts = new ArrayList<LinkedAccountDto>();
        LinkedAccountDto linkedAccountDto = new LinkedAccountDto();
        linkedAccountDto.setAccountNumber("50001");
        linkedAccountDto.setBsb("1001");
        linkedAccountDto.setCurrencyId("100");
        linkedAccountDto.setName("Test linked account");
        linkedAccounts.add(linkedAccountDto);

        Map<String, Object> fees = new HashMap<String, Object>();
        BrokerDto adviser = new BrokerDto();
        adviser.setFirstName("Dennis");
        adviser.setLastName("Beecham");
        List<PhoneDto> phone = new ArrayList<>();
        PhoneDto phoneDto = new PhoneDto();
        phoneDto.setNumber("0420359664");
        phoneDto.setPhoneType("Landline");
        phoneDto.setPreferred(true);
        phone.add(phoneDto);
        adviser.setPhone(phone);

        ClientApplicationDetailsDto clientApplicationDetailsDto = new ClientApplicationDetailsDto().withOnboardingApplicationKey("11127").withAccountAvaloqStatus("ApplicationFailed").withAccountKey("50001").withAccountName("test acount").withAccountSettings(accountSettingsDto).withAccountType("individual").withAdviser(adviser).withFees(fees).withLinkedAccounts(linkedAccounts).withPdsUrl("/test/url").withProductName("Test Product").withReferenceNumber("R00011127");
        Mockito.when(serviceOpsService.getClientApplicationDetails(anyString())).thenReturn(clientApplicationDetailsDto);
        String view = serviceOpsController.getClientApplicationDetails(clientApplicationId, modelMap);
        assertThat(view, Is.is(View.APPLICATION_DETAIL));
        assertThat(modelMap.get(Attribute.CLIENTAPPLICATION), notNullValue());

        ClientApplicationDetailsDto clientApplicationDetailModel = (ClientApplicationDetailsDto) modelMap.get(Attribute.CLIENTAPPLICATION);
        assertThat(clientApplicationDetailModel.getAccountAvaloqStatus(), Is.is("ApplicationFailed"));
        assertThat(clientApplicationDetailModel.getAccountKey(), Is.is("50001"));
        assertThat(clientApplicationDetailModel.getAccountName(), Is.is("test acount"));
        assertThat(clientApplicationDetailModel.getAccountSettings(), notNullValue());
        assertThat(clientApplicationDetailModel.getAccountSettings().getPersonRelations().get(0).getName(), Is.is("John Doe"));
        assertThat(clientApplicationDetailModel.getAccountSettings().getPersonRelations().get(0).isAdviser(), Is.is(false));
        assertThat(clientApplicationDetailModel.getAdviser().getFirstName(), Is.is("Dennis"));
        assertThat(clientApplicationDetailModel.getAdviser().getLastName(), Is.is("Beecham"));
        assertThat(clientApplicationDetailModel.getAdviser().getPhone().get(0).getNumber(), Is.is("0420359664"));
        assertThat(clientApplicationDetailModel.getAdviser().getPhone().get(0).getPhoneType(), Is.is("Landline"));
        assertThat(clientApplicationDetailModel.getAdviser().getPhone().get(0).isPreferred(), Is.is(true));
        assertThat(clientApplicationDetailModel.getProductName(), Is.is("Test Product"));
        assertThat(clientApplicationDetailModel.getPdsUrl(), Is.is("/test/url"));
        assertThat(clientApplicationDetailModel.getReferenceNumber(), Is.is("R00011127"));
    }

    @Test
    public void testGetClientApplicationsByCISKey() throws Exception {
        String cisKey = "00099998887";

        Mockito.when(serviceOpsService.getApprovedClientApplicationsByCISKey(eq(cisKey))).thenReturn(Arrays.asList(new ServiceOpsClientApplicationDto()));
        ApiResponse clientApplicationsByCISKey = serviceOpsController.getClientApplicationsByCISKey(cisKey);
        assertThat(((ResultListDto<ServiceOpsClientApplicationDto>) clientApplicationsByCISKey.getData()).getResultList().get(0).getType(), is("ServiceOpsClientApplication"));
    }

    @Test
    public void testSearchFailedApplication() throws Exception {
        ServiceOpsClientApplicationDto serviceOpsClientApplicationDto = new ServiceOpsClientApplicationDto();
        serviceOpsClientApplicationDto.setAccountName("Test account");
        serviceOpsClientApplicationDto.setAccountType("individual");
        serviceOpsClientApplicationDto.setAdviserName("John Doe");
        serviceOpsClientApplicationDto.setFailureMessage("Server failure");
        serviceOpsClientApplicationDto.setProductName("Test product");
        Mockito.when(serviceOpsService.getFailedApplicationDetails(anyString())).thenReturn(serviceOpsClientApplicationDto);
        ApiResponse response = serviceOpsController.searchFailedApplication("2227");
        assertThat(response.getData(), notNullValue());
        assertThat(((ResultListDto<ServiceOpsClientApplicationDto>) response.getData()).getResultList().get(0), Is.is(serviceOpsClientApplicationDto));

        assertThat(serviceOpsClientApplicationDto.getAccountName(), Is.is("Test account"));
        assertThat(serviceOpsClientApplicationDto.getAccountType(), Is.is("individual"));
        assertThat(serviceOpsClientApplicationDto.getAdviserName(), Is.is("John Doe"));
        assertThat(serviceOpsClientApplicationDto.getFailureMessage(), Is.is("Server failure"));
        assertThat(serviceOpsClientApplicationDto.getProductName(), Is.is("Test product"));
    }

    @Test
    public void testDownloadCsvOfAllUnapprovedApplications() throws IOException {
        String fromDate = "20150201";
        String toDate = "20150301";
        HttpServletResponse resp = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);
        String csvContent = "Something";
        when(serviceOpsService.downloadCsvOfAllUnapprovedApplications(eq(dateTimeFormatter.parseDateTime(fromDate).toDate()), eq(dateTimeFormatter.parseDateTime(toDate).plusDays(1).minusSeconds(1).toDate()), any(ServiceErrors.class))).thenReturn(csvContent);
        when(resp.getWriter()).thenReturn(writer);
        serviceOpsController.downloadCsvOfAllUnapprovedApplications(resp, fromDate, toDate);
        verify(serviceOpsService).downloadCsvOfAllUnapprovedApplications(eq(dateTimeFormatter.parseDateTime(fromDate).toDate()), eq(dateTimeFormatter.parseDateTime(toDate).plusDays(1).minusSeconds(1).toDate()), any(ServiceErrors.class));
        verify(resp, times(1)).setContentType("text/csv; charset=UTF-8");
        verify(resp, times(1)).setHeader("Content-Disposition", "attachment; filename=unapproved_applications_report.csv");
        verify(resp, times(1)).getWriter();
    }

    @Test
    public void testMoveFailedApplicationToDraft() throws Exception {
        ApiResponse response = serviceOpsController.moveFailedApplicationToDraft("R000003744");
        Mockito.verify(serviceOpsService, Mockito.times(1)).moveFailedApplicationToDraft(Mockito.anyString());
        assertThat(response.getStatus(), CoreMatchers.is(1));
        assertThat(response.getError(), CoreMatchers.nullValue());
    }

    @Test
    public void testMoveFailedApplicationToDraftFailing() throws Exception {
        final String ERROR_TEXT = "Error during creating a new draft account based on an existing failed one";
        doThrow(new IllegalStateException(ERROR_TEXT)).when(serviceOpsService).moveFailedApplicationToDraft(Mockito.anyString());
        ApiResponse response = serviceOpsController.moveFailedApplicationToDraft("R000003744");
        Mockito.verify(serviceOpsService, Mockito.times(1)).moveFailedApplicationToDraft(Mockito.anyString());
        assertThat(response.getStatus(), CoreMatchers.is(0));
        assertThat(response.getError().getMessage(), CoreMatchers.is(ERROR_TEXT));
    }

    @Test
    public void testSubmitAction_resendRegistrationMailForInvestorSuccess() throws Exception {
        ServiceOpsModel serviceops = new ServiceOpsModel();
        serviceops.setRole(Attribute.INVESTOR);
        serviceops.setPrimaryMobileNumber("12345");
        serviceops.setGcmId("gcmId");
        Mockito.when(serviceOpsService.getUserDetail(anyString(), anyBoolean(), any(ServiceErrors.class))).thenReturn(serviceops);
        Mockito.when(resendRegistrationEmailService.resendRegistrationEmailForInvestor(anyString(), anyString(), anyString(), any(ServiceErrors.class))).thenReturn(Attribute.SUCCESS_MESSAGE);
        Mockito.when(cmsService.getContent(anyString())).thenReturn("cmsContent");
        String view = serviceOpsController.submitAction(userId, Action.RESEND_REGISTRATION_EMAIL, serviceOpsModel, redirectAttributes);
        assertThat(view, is("redirect:/secure/page/serviceOps/{clientId}/detail"));
        Mockito.verify(redirectAttributes, Mockito.times(2)).addFlashAttribute(anyString(), anyString());
    }

    @Test
    public void testResendExistingRegoCodeMailForInvestorSuccess() throws Exception {
        ServiceOpsModel serviceops = new ServiceOpsModel();
        serviceops.setRole(Attribute.INVESTOR);
        serviceops.setPrimaryMobileNumber("12345");
        serviceops.setGcmId("gcmId");
        Mockito.when(serviceOpsService.getUserDetail(anyString(), anyBoolean(), any(ServiceErrors.class))).thenReturn(serviceops);
        Mockito.when(resendRegistrationEmailService.resendRegistrationEmailWithExistingCodeForInvestor(anyString(), anyString(), anyString(), any(ServiceErrors.class))).thenReturn(Attribute.SUCCESS_MESSAGE);
        Mockito.when(cmsService.getContent(anyString())).thenReturn("cmsContent");
        String view = serviceOpsController.submitAction(userId, Action.RESEND_EXISTING_REGISTRATION_CODE, serviceOpsModel, redirectAttributes);
        assertThat(view, is("redirect:/secure/page/serviceOps/{clientId}/detail"));
        Mockito.verify(redirectAttributes, Mockito.times(2)).addFlashAttribute(anyString(), anyString());
    }

    @Test
    public void testResendExistingRegoCodeMailForAdviserSuccess() throws Exception {
        ServiceOpsModel serviceops = new ServiceOpsModel();
        serviceops.setRole(Attribute.ADVISER);
        serviceops.setPrimaryMobileNumber("12345");
        serviceops.setGcmId("gcmId");
        Mockito.when(serviceOpsService.getUserDetail(anyString(), anyBoolean(), any(ServiceErrors.class))).thenReturn(serviceops);
        Mockito.when(resendRegistrationEmailService.resendRegistrationEmailWithExistingCodeForAdviser(anyString(), anyString(), anyString(), any(ServiceErrors.class))).thenReturn(Attribute.SUCCESS_MESSAGE);
        Mockito.when(cmsService.getContent(anyString())).thenReturn("cmsContent");
        String view = serviceOpsController.submitAction(userId, Action.RESEND_EXISTING_REGISTRATION_CODE, serviceOpsModel, redirectAttributes);
        assertThat(view, is("redirect:/secure/page/serviceOps/{clientId}/detail"));
        Mockito.verify(redirectAttributes, Mockito.times(2)).addFlashAttribute(anyString(), anyString());
    }

    @Test
    public void testSubmitAction_resendRegistrationMailForInvestorErrorMsg() throws Exception {
        Mockito.when(serviceErrors.hasErrors()).thenReturn(true);
        ServiceError serviceError = new ServiceErrorImpl();
        List<ServiceError> errorList = new ArrayList();
        errorList.add(serviceError);
        Mockito.when(serviceErrors.getErrorList()).thenReturn(errorList);
        Mockito.when(cmsService.getContent(anyString())).thenReturn("cmsContent");
        ServiceOpsModel serviceops = new ServiceOpsModel();
        serviceops.setRole(Attribute.INVESTOR);
        serviceops.setPrimaryMobileNumber("12345");
        serviceops.setGcmId("gcmId");
        Mockito.when(serviceOpsService.getUserDetail(anyString(), anyBoolean(), any(ServiceErrors.class))).thenReturn(serviceops);
        Mockito.when(resendRegistrationEmailService.resendRegistrationEmailForInvestor(anyString(), anyString(), anyString(), any(ServiceErrors.class))).thenReturn(Attribute.ERROR_MESSAGE);
        Mockito.when(cmsService.getContent(anyString())).thenReturn("cmsContent");
        String view = serviceOpsController.submitAction(userId, Action.RESEND_REGISTRATION_EMAIL, serviceOpsModel, redirectAttributes);
        assertThat(view, is("redirect:/secure/page/serviceOps/{clientId}/detail"));
        Mockito.verify(redirectAttributes, Mockito.times(2)).addFlashAttribute(anyString(), anyString());
    }

    @Test
    public void testGetClientApplicationDetailsByAccountNumber_withActiveStatus_andApplicationAwaitingApproval() throws Exception {
        ModelMap modelMap = new ModelMap();
        String accountNumber = "120046644";
        String status = "active";

        ClientApplicationDetailsDto clientApplicationDetailsDto = new ClientApplicationDetailsDto();
        clientApplicationDetailsDto.withAccountAvaloqStatus(AccountStatus.PEND_OPN.getStatus());
        Mockito.when(serviceOpsService.getClientApplicationDetailsByAccountNumber(anyString())).thenReturn(clientApplicationDetailsDto);
        String view = serviceOpsController.getClientApplicationDetailsByAccountNumber(accountNumber, modelMap, status);
        assertThat(view, Is.is(View.APPLICATION_DETAIL));
        assertEquals(modelMap.get(Attribute.CLIENTAPPLICATION), null);
    }

    @Test
    public void testGetClientApplicationDetailsByAccountNumber_withActiveStatus_andApplicationActive() throws Exception {
        ModelMap modelMap = new ModelMap();
        String accountNumber = "120046644";
        String status = "active";

        ClientApplicationDetailsDto clientApplicationDetailsDto = new ClientApplicationDetailsDto();
        clientApplicationDetailsDto.withAccountAvaloqStatus(AccountStatus.ACTIVE.getStatus());
        Mockito.when(serviceOpsService.getClientApplicationDetailsByAccountNumber(anyString())).thenReturn(clientApplicationDetailsDto);
        String view = serviceOpsController.getClientApplicationDetailsByAccountNumber(accountNumber, modelMap, status);
        assertThat(view, Is.is(View.APPLICATION_DETAIL));
        assertThat(modelMap.get(Attribute.CLIENTAPPLICATION), notNullValue());
    }

    @Test
    public void testGetClientApplicationDetailsByAccountNumber_withActiveStatus_andApplicationFeInProgress() throws Exception {
        ModelMap modelMap = new ModelMap();
        String accountNumber = "120046644";
        String status = "active";

        ClientApplicationDetailsDto clientApplicationDetailsDto = new ClientApplicationDetailsDto();
        clientApplicationDetailsDto.withAccountAvaloqStatus(AccountStatus.FUND_ESTABLISHMENT_IN_PROGRESS.getStatus());
        Mockito.when(serviceOpsService.getClientApplicationDetailsByAccountNumber(anyString())).thenReturn(clientApplicationDetailsDto);
        String view = serviceOpsController.getClientApplicationDetailsByAccountNumber(accountNumber, modelMap, status);
        assertThat(view, Is.is(View.APPLICATION_DETAIL));
        assertThat(modelMap.get(Attribute.CLIENTAPPLICATION), notNullValue());
    }

    @Test
    public void testGetClientApplicationDetailsByAccountNumber_withActiveStatus_andApplicationFePending() throws Exception {
        ModelMap modelMap = new ModelMap();
        String accountNumber = "120046644";
        String status = "active";

        ClientApplicationDetailsDto clientApplicationDetailsDto = new ClientApplicationDetailsDto();
        clientApplicationDetailsDto.withAccountAvaloqStatus(AccountStatus.FUND_ESTABLISHMENT_PENDING.getStatus());
        Mockito.when(serviceOpsService.getClientApplicationDetailsByAccountNumber(anyString())).thenReturn(clientApplicationDetailsDto);
        String view = serviceOpsController.getClientApplicationDetailsByAccountNumber(accountNumber, modelMap, status);
        assertThat(view, Is.is(View.APPLICATION_DETAIL));
        assertEquals(modelMap.get(Attribute.CLIENTAPPLICATION), null);
    }


    @Test
    public void testGetClientApplicationDetailsByAccountNumber_withNoStatus() throws Exception {
        ModelMap modelMap = new ModelMap();
        String accountNumber = "120046644";

        ClientApplicationDetailsDto clientApplicationDetailsDto = new ClientApplicationDetailsDto();
        clientApplicationDetailsDto.withAccountAvaloqStatus(OnboardingApplicationStatus.smsfinProgress.toString());
        Mockito.when(serviceOpsService.getClientApplicationDetailsByAccountNumber(anyString())).thenReturn(clientApplicationDetailsDto);
        String view = serviceOpsController.getClientApplicationDetailsByAccountNumber(accountNumber, modelMap, null);
        assertThat(view, Is.is(View.APPLICATION_DETAIL));
        assertThat(modelMap.get(Attribute.CLIENTAPPLICATION), notNullValue());
    }

    @Test
    public void testSubmitAction_Resend_Rego_Mail_MissingDetails() throws Exception {
        Mockito.when(serviceOpsModel.getUserName()).thenReturn("Investor");
        JobProfileImpl jobProfile = new JobProfileImpl();
        jobProfile.setJobRole(JobRole.INVESTOR);
        jobProfile.setProfileId("1234");
        List<JobProfile> jobProfileList = new ArrayList<>();
        jobProfileList.add(jobProfile);
        Mockito.when(serviceOpsModel.getJobProfiles()).thenReturn(jobProfileList);
        Mockito.when(serviceOpsModel.getGcmId()).thenReturn("10000667");
        EncodedString tempUserId = EncodedString.fromPlainText("83351");
        Mockito.when(serviceOpsModel.getEmail()).thenReturn(null);
        Mockito.when(serviceOpsService.getUserDetail(eq("83351"), eq(true), any(ServiceErrors.class))).thenReturn(serviceOpsModel);
        String view = serviceOpsController.submitAction(tempUserId, Action.RESEND_REGISTRATION_EMAIL, serviceOpsModel, redirectAttributes);
        verify(redirectAttributes).addFlashAttribute(Attribute.ERR_MESSAGE, "Unable to resend registration email.Email is missing.");
        assertThat(view, is("redirect:/secure/page/serviceOps/{clientId}/detail"));
    }

    @Test
    public void testSubmitAction_Regenerate_Rego_Code_MissingDetails() throws Exception {
        Mockito.when(serviceOpsModel.getUserName()).thenReturn("Investor");
        JobProfileImpl jobProfile = new JobProfileImpl();
        jobProfile.setJobRole(JobRole.INVESTOR);
        jobProfile.setProfileId("1234");
        List<JobProfile> jobProfileList = new ArrayList<>();
        jobProfileList.add(jobProfile);
        Mockito.when(serviceOpsModel.getJobProfiles()).thenReturn(jobProfileList);
        Mockito.when(serviceOpsModel.getGcmId()).thenReturn("10000667");
        EncodedString tempUserId = EncodedString.fromPlainText("83351");
        Mockito.when(serviceOpsModel.getEmail()).thenReturn(null);
        Mockito.when(serviceOpsService.getUserDetail(eq("83351"), eq(true), any(ServiceErrors.class))).thenReturn(serviceOpsModel);
        String view = serviceOpsController.submitAction(tempUserId, Action.RESEND_EXISTING_REGISTRATION_CODE, serviceOpsModel, redirectAttributes);
        verify(redirectAttributes).addFlashAttribute(Attribute.STATUS, Attribute.ERROR_MESSAGE);
        assertThat(view, is("redirect:/secure/page/serviceOps/{clientId}/detail"));
    }

    @Test
    public void testSubmitAction_Regenerate_Rego_Code() throws Exception {
        List<Email> emails = new ArrayList<>();
        Mockito.when(serviceOpsModel.getUserName()).thenReturn("Investor");
        JobProfileImpl jobProfile = new JobProfileImpl();
        jobProfile.setJobRole(JobRole.INVESTOR);
        jobProfile.setProfileId("1234");
        List<JobProfile> jobProfileList = new ArrayList<>();
        jobProfileList.add(jobProfile);
        Mockito.when(serviceOpsModel.getJobProfiles()).thenReturn(jobProfileList);
        Mockito.when(serviceOpsModel.getGcmId()).thenReturn("10000667");
        EmailImpl email = new EmailImpl();
        email.setEmail("abc@mail.com");
        emails.add(email);
        Mockito.when(serviceOpsModel.getEmail()).thenReturn(emails);
        Mockito.when(serviceOpsService.getUserDetail(eq("83351"), eq(true), any(ServiceErrors.class))).thenReturn(serviceOpsModel);
        EncodedString tempUserId = EncodedString.fromPlainText("83351");
        Mockito.when(serviceOpsService.getUserDetail(eq("83351"), eq(true), any(ServiceErrors.class))).thenReturn(serviceOpsModel);
        String viewResendEmail = serviceOpsController.submitAction(tempUserId, Action.RESEND_REGISTRATION_EMAIL, serviceOpsModel, redirectAttributes);
        verify(redirectAttributes,times(0)).addFlashAttribute(Attribute.ERR_MESSAGE, "Unable to resend registration email.Email is missing.");
        assertThat(viewResendEmail, is("redirect:/secure/page/serviceOps/{clientId}/detail"));
        String viewResendRegoCode = serviceOpsController.submitAction(tempUserId, Action.RESEND_EXISTING_REGISTRATION_CODE, serviceOpsModel, redirectAttributes);
        verify(redirectAttributes,times(0)).addFlashAttribute(Attribute.ERR_MESSAGE, "Unable to resend existing registration code.Email is missing.");
        assertThat(viewResendRegoCode, is("redirect:/secure/page/serviceOps/{clientId}/detail"));

    }

    @Test
    public void testProvisionMFADevice_Success() throws Exception {
        ArgumentCaptor<ProvisionMFARequestData> argumentCaptor01 = ArgumentCaptor.forClass(ProvisionMFARequestData.class);
        ServiceOpsModel serviceops = new ServiceOpsModel();
        serviceops.setRole(Attribute.INVESTOR);
        serviceops.setPrimaryMobileNumber("12345");
        serviceops.setGcmId("gcmId");
        serviceops.setCisId("cisKey");
        Mockito.when(serviceOpsService.getUserDetail(anyString(), anyBoolean(), any(ServiceErrors.class))).thenReturn(serviceops);
        Mockito.when(provisionMFADeviceService.provisionMFADevice(any(ProvisionMFARequestData.class), any(ServiceErrors.class))).thenReturn(true);
        Mockito.when(cmsService.getContent(anyString())).thenReturn("cmsContent");
        String view = serviceOpsController.submitAction(userId, Action.PROVISION_MFA_DEVICE, serviceOpsModel, redirectAttributes);
        verify(provisionMFADeviceService,times(1)).provisionMFADevice(argumentCaptor01.capture(),any(ServiceErrors.class));
        ProvisionMFARequestData provisionMFADeviceRequest = (ProvisionMFARequestData)argumentCaptor01.getValue();
        assertNotNull(provisionMFADeviceRequest);
        assertThat(view, is("redirect:/secure/page/serviceOps/{clientId}/detail"));
        Mockito.verify(redirectAttributes, Mockito.times(2)).addFlashAttribute(anyString(), anyString());
        verify(redirectAttributes,times(1)).addFlashAttribute(Attribute.ACTION_PERFORMED, Action.PROVISION_MFA_DEVICE.name());
    }

    @Test
    public void testProvisionMFADevice_Failure() throws Exception {
        ArgumentCaptor<ProvisionMFARequestData> argumentCaptor01 = ArgumentCaptor.forClass(ProvisionMFARequestData.class);
        ServiceOpsModel serviceops = new ServiceOpsModel();
        serviceops.setRole(Attribute.INVESTOR);
        Mockito.when(serviceOpsService.getUserDetail(anyString(), anyBoolean(), any(ServiceErrors.class))).thenReturn(serviceops);
        Mockito.when(provisionMFADeviceService.provisionMFADevice(any(ProvisionMFARequestData.class), any(ServiceErrors.class))).thenReturn(false);
        Mockito.when(cmsService.getContent(anyString())).thenReturn("cmsContent");
        String view = serviceOpsController.submitAction(userId, Action.PROVISION_MFA_DEVICE, serviceOpsModel, redirectAttributes);
        verify(provisionMFADeviceService,times(1)).provisionMFADevice(argumentCaptor01.capture(),any(ServiceErrors.class));
        ProvisionMFARequestData provisionMFADeviceRequest = (ProvisionMFARequestData)argumentCaptor01.getValue();
        assertNotNull(provisionMFADeviceRequest);
        assertThat(view, is("redirect:/secure/page/serviceOps/{clientId}/detail"));
        Mockito.verify(redirectAttributes, Mockito.times(2)).addFlashAttribute(anyString(), anyString());
        verify(redirectAttributes,times(1)).addFlashAttribute(Attribute.ERR_MESSAGE, "Update MFA Device Failed");
    }


    public UserProfile getProfile(final JobRole role, final String jobId, final String customerId)
    {
        UserInformation user = new UserInformationImpl();
        user.setClientKey(ClientKey.valueOf(customerId));
        JobProfile job = getJobProfile(role, jobId);

        UserProfile profile = new UserProfileAdapterImpl(user, job);
        return profile;
    }

    private JobProfile getJobProfile(final JobRole role, final String jobId)
    {
        JobProfile job = Mockito.mock(JobProfile.class);
        when(job.getJobRole()).thenReturn(role);
        when(job.getJob()).thenReturn(JobKey.valueOf(jobId));
        return job;
    }
}