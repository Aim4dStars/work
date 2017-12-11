package com.bt.nextgen.api.inspecietransfer.v2.controller;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto;
import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferKey;
import com.bt.nextgen.api.inspecietransfer.v2.model.SettlementRecordDto;
import com.bt.nextgen.api.inspecietransfer.v2.model.SponsorDetailsDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v2.service.InspecieTransferDtoService;
import com.bt.nextgen.api.inspecietransfer.v2.util.TaxParcelUploadUtil;
import com.bt.nextgen.api.order.validation.OrderGroupDtoErrorMapper;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.security.api.model.PermissionAccountKey;
import com.bt.nextgen.core.security.api.model.PermissionsDto;
import com.bt.nextgen.core.security.api.service.PermissionAccountDtoService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;

@RunWith(MockitoJUnitRunner.class)
@Ignore("dodgy mock uri method handler doesn't have full spring capabilities")
public class InspecieTransferApiControllerTest {

    @InjectMocks
    private InspecieTransferApiController inspecieTransferApiController = new InspecieTransferApiController();

    @Mock
    private InspecieTransferDtoService inspecieTransferDtoService;
    @Mock
    private TaxParcelUploadUtil taxParcelUploadUtil;
    @Mock
    private OrderGroupDtoErrorMapper orderGroupDtoErrorMapper;
    @Mock
    private PermissionAccountDtoService permissionAccountDtoService;
    @Mock
    private UserProfileService profileService;
    @Mock
    private PermissionsDto permissionsDto;

    @Mock
    private static AnnotationMethodHandlerAdapter annotationMethodHandler;

    private MockHttpServletRequest mockHttpServletRequest;
    private MockHttpServletResponse mockHttpServletResponse;

    private static final String TEST_FILE = "C:\\Anitha\\R2\\Portfolio Management\\taxparcel.csv";

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        mockHttpServletRequest = new MockHttpServletRequest(RequestMethod.GET.name(), "/");
        mockHttpServletResponse = new MockHttpServletResponse();
        annotationMethodHandler = new AnnotationMethodHandlerAdapter();
        HttpMessageConverter[] messageConverters = { new MappingJackson2HttpMessageConverter() };
        annotationMethodHandler.setMessageConverters(messageConverters);
    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.account.v2.controller.regularinvestment.RegularInvestmentApiController#create(java.lang.String, java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public final void testCreateValidate() throws Exception {

        SponsorDetailsDtoImpl sponsor = new SponsorDetailsDtoImpl();
        sponsor.setSrn("srn");
        InspecieTransferDto transferDto = new InspecieTransferDtoImpl("transferType", sponsor,
                new ArrayList<SettlementRecordDto>(), "destContainerId", new InspecieTransferKey(), new Boolean(false),
                new ArrayList<DomainApiErrorDto>());

        Mockito.when(profileService.isEmulating()).thenReturn(false);

        Mockito.when(
                inspecieTransferDtoService.validate(Mockito.any(InspecieTransferDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(transferDto);

        Mockito.when(permissionsDto.hasPermission(Mockito.anyString())).thenReturn(true);

        Mockito.when(permissionAccountDtoService.find(Mockito.any(PermissionAccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(permissionsDto);

        mockHttpServletRequest.setRequestURI("/secure/api/v1_0/accounts/sample-account-id/inspecie-transfer");
        mockHttpServletRequest.setParameter("transferDetails", "{\"key\":{\"accountId\":\"28100\",\"transferId\":\"32323\"}}");
        mockHttpServletRequest.setParameter("x-ro-validate-only", "true");
        mockHttpServletRequest.setMethod("POST");
        mockHttpServletRequest.setParameter("order-id", "sample-order-id");
        mockHttpServletRequest.setParameter("account-id", "sample-account-id");

        annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, inspecieTransferApiController);
    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.account.v1.controller.regularinvestment.RegularInvestmentApiController#create(java.lang.String, java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public final void testCreateSubmit() throws Exception {

        SponsorDetailsDtoImpl sponsor = new SponsorDetailsDtoImpl();
        sponsor.setSrn("srn");
        InspecieTransferDto transferDto = new InspecieTransferDtoImpl("transferType", sponsor,
                new ArrayList<SettlementRecordDto>(), "destContainerId", new InspecieTransferKey(), new Boolean(false),
                new ArrayList<DomainApiErrorDto>());

        Mockito.when(profileService.isEmulating()).thenReturn(false);

        Mockito.when(inspecieTransferDtoService.submit(Mockito.any(InspecieTransferDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(transferDto);

        Mockito.when(permissionsDto.hasPermission(Mockito.anyString())).thenReturn(true);

        Mockito.when(permissionAccountDtoService.find(Mockito.any(PermissionAccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(permissionsDto);

        mockHttpServletRequest.setRequestURI("/secure/api/v1_0/accounts/sample-account-id/inspecie-transfer");
        mockHttpServletRequest.setParameter("transferDetails",
                "{\"action\":\"submit\", \"key\":{\"accountId\":\"28100\",\"transferId\":\"32323\"}}");
        mockHttpServletRequest.setParameter("x-ro-validate-only", "false");
        mockHttpServletRequest.setMethod("POST");
        mockHttpServletRequest.setParameter("order-id", "sample-order-id");
        mockHttpServletRequest.setParameter("account-id", "sample-account-id");

        annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, inspecieTransferApiController);

    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.account.v1.controller.regularinvestment.RegularInvestmentApiController#getInspecieTransfer(java.lang.String, java.lang.String, org.springframework.web.multipart.MultipartFile)}
     * .
     */
    @Test
    public final void testGetInspecieTransfer() throws Exception {
        SponsorDetailsDtoImpl sponsor = new SponsorDetailsDtoImpl();
        sponsor.setSrn("srn");
        InspecieTransferDto transferDto = new InspecieTransferDtoImpl("transferType", sponsor,
                new ArrayList<SettlementRecordDto>(), "destContainerId", new InspecieTransferKey(), new Boolean(false),
                new ArrayList<DomainApiErrorDto>());

        Mockito.when(profileService.isEmulating()).thenReturn(false);

        Mockito.when(inspecieTransferDtoService.find(Mockito.any(InspecieTransferKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(transferDto);

        Mockito.when(permissionsDto.hasPermission(Mockito.anyString())).thenReturn(true);

        Mockito.when(permissionAccountDtoService.find(Mockito.any(PermissionAccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(permissionsDto);

        mockHttpServletRequest.setRequestURI("/secure/api/v1_0/accounts/sample-account-id/inspecie-transfer/1251425");

        mockHttpServletRequest.setMethod("GET");
        mockHttpServletRequest.setParameter("order-id", "sample-order-id");
        mockHttpServletRequest.setParameter("account-id", "sample-account-id");

        annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, inspecieTransferApiController);

    }
    //
    // /**
    // * Test method for
    // * {@link
    // com.bt.nextgen.api.account.v1.controller.regularinvestment.RegularInvestmentApiController#getInspecieTransfer(java.lang.String,
    // java.lang.String, org.springframework.web.multipart.MultipartFile)}
    // * .
    // */
    // @Test
    // public final void testUploadTaxParcel() throws Exception {
    // byte[] content = new byte[5];
    // MockMultipartFile mockMultipartFile = new MockMultipartFile("fileData",
    // "taxparcel.csv", "text/plain", content);
    //
    // MockMultipartHttpServletRequest request = new
    // MockMultipartHttpServletRequest();
    // MockMultipartHttpServletRequest mockMultipartHttpServletRequest =
    // (MockMultipartHttpServletRequest) request;
    // // MultipartFile multipartFile = (MultipartFile)
    // mockMultipartHttpServletRequest.getFile(TEST_FILE);
    //
    // SponsorDetailsDtoImpl sponsor = new SponsorDetailsDtoImpl();
    // sponsor.setSrn("srn");
    // InspecieTransferDtoImpl transferDto = new
    // InspecieTransferDtoImpl("transferType", sponsor,
    // new ArrayList<SettlementRecordDto>(), "destContainerId", new
    // InspecieTransferKey(), new Boolean(false),
    // new ArrayList<DomainApiErrorDto>());
    //
    // Mockito.when(profileService.isEmulating()).thenReturn(false);
    //
    // Mockito.when(taxParcelUploadUtil.parseFile(mockMultipartFile,
    // transferDto)).thenReturn(transferDto);
    //
    // Mockito.when(permissionsDto.hasPermission(Mockito.anyString())).thenReturn(true);
    //
    // Mockito.when(permissionAccountDtoService.find(Mockito.any(PermissionAccountKey.class),
    // Mockito.any(ServiceErrors.class)))
    // .thenReturn(permissionsDto);
    //
    // mockMultipartHttpServletRequest
    // .setRequestURI("/secure/api/v1_0/accounts/sample-account-id/inspecie-transfer/1251425/uploadTaxParcels");
    //
    // mockMultipartHttpServletRequest.setMethod("POST");
    // mockMultipartHttpServletRequest.setParameter("order-id",
    // "sample-order-id");
    // mockMultipartHttpServletRequest.setParameter("account-id",
    // "sample-account-id");
    //
    // annotationMethodHandler.handle(mockMultipartHttpServletRequest,
    // mockHttpServletResponse, inspecieTransferApiController);
    //
    // }
}
