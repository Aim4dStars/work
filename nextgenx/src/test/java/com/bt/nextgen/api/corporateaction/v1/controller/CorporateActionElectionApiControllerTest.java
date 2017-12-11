package com.bt.nextgen.api.corporateaction.v1.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.server.request.MockMvcRequestBuilders;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionDetailsBaseDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionPersistenceDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionsDto;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionPortfolioModelDto;
import com.bt.nextgen.api.corporateaction.v1.permission.CorporateActionPermissionService;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionCommonService;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionElectionDtoService;
import com.bt.nextgen.api.corporateaction.v1.validation.CorporateActionAccountDetailsDtoErrorMapper;
import com.bt.nextgen.api.draftaccount.FormDataValidator;
import com.bt.nextgen.api.draftaccount.controller.ClientApplicationDtoDeserializer;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.config.SecureJsonObjectMapper;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.service.ServiceErrors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionElectionApiControllerTest {
    @Mock
    private static CorporateActionElectionDtoService corporateActionElectionDtoService;

    @Mock
    @Qualifier("imCorporateActionElectionDtoService")
    private static CorporateActionElectionDtoService imCorporateActionElectionDtoService;

    @Mock
    private static CorporateActionAccountDetailsDtoErrorMapper corporateActionAccountDetailsDtoErrorMapper;

    @Mock
    private static CorporateActionCommonService corporateActionCommonService;

    @Mock
    private static CorporateActionPermissionService permissionService;

    @Mock
    private static UserProfileService userProfileService;

    @InjectMocks
    private CorporateActionElectionApiController corporateActionElectionApiController;

    @Mock
    private JsonObjectMapper objectMapper;

    @Mock
    private ObjectReader objectReader;

    @Captor
    private ArgumentCaptor<CorporateActionElectionDetailsBaseDto> argumentCaptor;

    @Before
    public void setup() {
        when(objectMapper.readerWithView(any(Class.class))).thenReturn(objectReader);
        when(objectReader.forType(any(TypeReference.class))).thenReturn(objectReader);

        when(corporateActionElectionDtoService.submit(any(CorporateActionElectionDetailsBaseDto.class), any(ServiceErrors.class)))
                .thenReturn(new CorporateActionElectionDetailsBaseDto());
    }

    @Test(expected = BadRequestException.class)
    public void testSubmitCorporateActionElections_whenCorporateActionIdIsNotANumber_thenThrowBadRequestException() {
        corporateActionElectionApiController
                .submitCorporateActionElections(EncodedString.fromPlainText("fakeCAId").toString(), "", "", null, null);
    }

    @Test(expected = BadRequestException.class)
    public void testSubmitCorporateActionElectionsByPost_whenCorporateActionIdIsNotANumber_thenThrowBadRequestException() throws
            IOException {
        corporateActionElectionApiController.submitCorporateActionElectionsByPost(EncodedString.fromPlainText("fakeCAId").toString(), "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSubmitCorporateActionElections_whenThereIsAMapperError_thenThrowIllegalArgumentException() throws IOException {
        when(objectReader.readValue(anyString())).thenThrow(new IOException());

        corporateActionElectionApiController.submitCorporateActionElections(
                "08E24C73A07BF3B6323E907360B880E544B662EBA8EE1BF7", ";dfd", null, null, null);
    }

    @Test(expected = AccessDeniedException.class)
    public void testSubmitCorporateActionElections_whenInvestorWithoutPermission_thenThrowAccessDeniedException() throws IOException {
        CorporateActionElectionDetailsDto caeDto = new CorporateActionElectionDetailsDto();

        when(userProfileService.isDealerGroup()).thenReturn(false);
        when(userProfileService.isInvestmentManager()).thenReturn(false);
        when(userProfileService.isPortfolioManager()).thenReturn(false);
        when(userProfileService.isInvestor()).thenReturn(true);
        when(corporateActionCommonService.getUserProfileService()).thenReturn(userProfileService);

        when(permissionService.checkSubmitPermission(anyList())).thenReturn(false);

        when(corporateActionElectionDtoService.submit(any(CorporateActionElectionDetailsDto.class),
                any(ServiceErrors.class))).thenReturn(caeDto);

        List<CorporateActionOptionDto> mockOptions = new ArrayList<>();
        List<CorporateActionAccountDetailsDto> mockAccounts = new ArrayList<>();
        when(objectReader.readValue(anyString())).thenReturn(mockOptions, mockAccounts);

        corporateActionElectionApiController
                .submitCorporateActionElections("08E24C73A07BF3B6323E907360B880E544B662EBA8EE1BF7", "", "", null, null);
    }

    @Test(expected = AccessDeniedException.class)
    public void testSubmitCorporateActionElectionsByPost_whenInvestorWithoutPermission_thenThrowAccessDeniedException() throws IOException {
        CorporateActionElectionDetailsDto caeDto = new CorporateActionElectionDetailsDto();

        when(userProfileService.isDealerGroup()).thenReturn(false);
        when(userProfileService.isInvestmentManager()).thenReturn(false);
        when(userProfileService.isPortfolioManager()).thenReturn(false);
        when(userProfileService.isInvestor()).thenReturn(true);
        when(corporateActionCommonService.getUserProfileService()).thenReturn(userProfileService);

        when(permissionService.checkSubmitPermission(anyList())).thenReturn(false);

        when(corporateActionElectionDtoService.submit(any(CorporateActionElectionDetailsDto.class),
                any(ServiceErrors.class))).thenReturn(caeDto);

        List<CorporateActionOptionDto> mockOptions = new ArrayList<>();
        List<CorporateActionAccountDetailsDto> mockAccounts = new ArrayList<>();

        // Request body method
        CorporateActionPersistenceDto corporateActionPersistenceDto =
                new CorporateActionPersistenceDto("", null, mockOptions, mockAccounts, null, null, false);

        when(objectReader.readValue(anyString())).thenReturn(corporateActionPersistenceDto);

        corporateActionElectionApiController
                .submitCorporateActionElectionsByPost("08E24C73A07BF3B6323E907360B880E544B662EBA8EE1BF7", "whatever");
    }

    @Test
    public void testSubmitCorporateActionElections_whenIsAdviser_thenReturnResponseData() throws IOException {
        CorporateActionElectionDetailsDto caeDto = new CorporateActionElectionDetailsDto();

        when(userProfileService.isDealerGroup()).thenReturn(false);
        when(userProfileService.isInvestmentManager()).thenReturn(false);
        when(userProfileService.isPortfolioManager()).thenReturn(false);
        when(userProfileService.isInvestor()).thenReturn(false);
        when(corporateActionCommonService.getUserProfileService()).thenReturn(userProfileService);

        when(permissionService.checkSubmitPermission(anyList())).thenReturn(true);

        when(corporateActionElectionDtoService.submit(any(CorporateActionElectionDetailsDto.class),
                any(ServiceErrors.class))).thenReturn(caeDto);

        List<CorporateActionOptionDto> mockOptions = new ArrayList<>();
        List<CorporateActionAccountDetailsDto> mockAccounts = new ArrayList<>();
        when(objectReader.readValue(anyString())).thenReturn(mockOptions, mockAccounts);

        ApiResponse response = corporateActionElectionApiController.submitCorporateActionElections(
                "08E24C73A07BF3B6323E907360B880E544B662EBA8EE1BF7", "", "", null, null);
        assertNotNull(response.getData());

        // Request body method
        CorporateActionPersistenceDto corporateActionPersistenceDto =
                new CorporateActionPersistenceDto("", null, mockOptions, mockAccounts, null, null, false);

        when(objectReader.readValue(anyString())).thenReturn(corporateActionPersistenceDto);

        response = corporateActionElectionApiController
                .submitCorporateActionElectionsByPost("08E24C73A07BF3B6323E907360B880E544B662EBA8EE1BF7", "whatever");
        assertNotNull(response.getData());
    }

    @Test
    public void testSubmitCorporateActionElections_whenIsInvestor_thenReturnResponseData() throws IOException {
        CorporateActionElectionDetailsDto caeDto = new CorporateActionElectionDetailsDto();

        when(userProfileService.isDealerGroup()).thenReturn(false);
        when(userProfileService.isInvestmentManager()).thenReturn(false);
        when(userProfileService.isPortfolioManager()).thenReturn(false);
        when(userProfileService.isInvestor()).thenReturn(true);
        when(corporateActionCommonService.getUserProfileService()).thenReturn(userProfileService);

        when(permissionService.checkSubmitPermission(anyList())).thenReturn(true);

        when(corporateActionElectionDtoService.submit(any(CorporateActionElectionDetailsDto.class),
                any(ServiceErrors.class))).thenReturn(caeDto);

        CorporateActionAccountDetailsDto accountDetailsDto = mock(CorporateActionAccountDetailsDto.class);
        when(accountDetailsDto.getAccountKey()).thenReturn(EncodedString.fromPlainText("1234567").toString());

        List<CorporateActionOptionDto> mockOptions = new ArrayList<>();
        List<CorporateActionAccountDetailsDto> mockAccounts = new ArrayList<>(1);
        mockAccounts.add(accountDetailsDto);

        when(objectReader.readValue(anyString())).thenReturn(mockOptions, mockAccounts);

        ApiResponse response = corporateActionElectionApiController.submitCorporateActionElections(
                "08E24C73A07BF3B6323E907360B880E544B662EBA8EE1BF7", "", "", null, null);
        assertNotNull(response.getData());

        // Request body method
        CorporateActionPersistenceDto corporateActionPersistenceDto =
                new CorporateActionPersistenceDto("", null, mockOptions, mockAccounts, null, null, false);

        when(objectReader.readValue(anyString())).thenReturn(corporateActionPersistenceDto);

        response = corporateActionElectionApiController
                .submitCorporateActionElectionsByPost("08E24C73A07BF3B6323E907360B880E544B662EBA8EE1BF7", "whatever");
        assertNotNull(response.getData());
    }

    @Test
    public void testSubmitCorporateActionElections_whenIsIm_thenReturnResponseData() throws IOException {
        ImCorporateActionElectionDetailsDto caeDto = new ImCorporateActionElectionDetailsDto();

        when(userProfileService.isDealerGroup()).thenReturn(false);
        when(userProfileService.isInvestmentManager()).thenReturn(true);
        when(userProfileService.isPortfolioManager()).thenReturn(false);
        when(corporateActionCommonService.getUserProfileService()).thenReturn(userProfileService);

        when(imCorporateActionElectionDtoService.submit(any(ImCorporateActionElectionDetailsDto.class),
                any(ServiceErrors.class))).thenReturn(caeDto);

        List<CorporateActionOptionDto> mockOptions = new ArrayList<>();
        List<ImCorporateActionPortfolioModelDto> mockPortfolios = new ArrayList<>();
        when(objectReader.readValue(anyString())).thenReturn(mockOptions, mockPortfolios);

        ApiResponse response = corporateActionElectionApiController.submitCorporateActionElections(
                "08E24C73A07BF3B6323E907360B880E544B662EBA8EE1BF7", "", null, "", "12345");
        assertNotNull(response.getData());

        // Request body method
        CorporateActionPersistenceDto corporateActionPersistenceDto =
                new CorporateActionPersistenceDto("", null, mockOptions, null, mockPortfolios, "12345", false);

        when(objectReader.readValue(anyString())).thenReturn(corporateActionPersistenceDto);

        response = corporateActionElectionApiController
                .submitCorporateActionElectionsByPost("08E24C73A07BF3B6323E907360B880E544B662EBA8EE1BF7", "whatever");
        assertNotNull(response.getData());
    }

    @Test
    public void testSubmitCorporateActionElections_ForDg_AccountsOnly() throws IOException {
        ImCorporateActionElectionDetailsDto caeDto = new ImCorporateActionElectionDetailsDto();

        when(userProfileService.isDealerGroup()).thenReturn(true);
        when(userProfileService.isInvestmentManager()).thenReturn(false);
        when(userProfileService.isPortfolioManager()).thenReturn(false);
        when(corporateActionCommonService.getUserProfileService()).thenReturn(userProfileService);

        when(corporateActionElectionDtoService.submit(any(CorporateActionElectionDetailsDto.class), any(ServiceErrors.class)))
                .thenReturn(caeDto);

        List<CorporateActionOptionDto> mockOptions = new ArrayList<>();
        List<ImCorporateActionPortfolioModelDto> mockAccounts = new ArrayList<>();
        List<ImCorporateActionPortfolioModelDto> mockPortfolios = new ArrayList<>();
        when(objectReader.readValue(anyString())).thenReturn(mockOptions, mockPortfolios, mockAccounts);

        ApiResponse response = corporateActionElectionApiController.submitCorporateActionElections(
                "08E24C73A07BF3B6323E907360B880E544B662EBA8EE1BF7", "", "", null, "12345");
        assertNotNull(response.getData());

        // Request body method
        CorporateActionPersistenceDto corporateActionPersistenceDto =
                new CorporateActionPersistenceDto("", null, mockOptions, null, mockPortfolios, "12345", false);

        when(objectReader.readValue(anyString())).thenReturn(corporateActionPersistenceDto);

        response = corporateActionElectionApiController
                .submitCorporateActionElectionsByPost("08E24C73A07BF3B6323E907360B880E544B662EBA8EE1BF7", "whatever");
        assertNotNull(response.getData());
    }

    @Test
    public void testSubmitCorporateActionElections_ForDg_AccountsAndModels() throws IOException {
        ImCorporateActionElectionDetailsDto caeDto = new ImCorporateActionElectionDetailsDto();

        when(userProfileService.isDealerGroup()).thenReturn(true);
        when(userProfileService.isInvestmentManager()).thenReturn(false);
        when(userProfileService.isPortfolioManager()).thenReturn(false);
        when(corporateActionCommonService.getUserProfileService()).thenReturn(userProfileService);

        when(imCorporateActionElectionDtoService.submit(any(ImCorporateActionElectionDetailsDto.class), any(ServiceErrors.class)))
                .thenReturn(caeDto);

        List<CorporateActionOptionDto> mockOptions = new ArrayList<>();
        List<ImCorporateActionPortfolioModelDto> mockAccounts = new ArrayList<>();

        ImCorporateActionPortfolioModelDto mockPortfolio = mock(ImCorporateActionPortfolioModelDto.class);
        when(mockPortfolio.getSelectedElections()).thenReturn(new CorporateActionSelectedOptionsDto());
        List<ImCorporateActionPortfolioModelDto> mockPortfolios = new ArrayList<>();
        mockPortfolios.add(mockPortfolio);

        when(objectReader.readValue(anyString())).thenReturn(mockOptions, mockPortfolios, mockAccounts);

        ApiResponse response = corporateActionElectionApiController.submitCorporateActionElections(
                "08E24C73A07BF3B6323E907360B880E544B662EBA8EE1BF7", "", "", "", "12345");
        assertNotNull(response.getData());

        // Request body method
        CorporateActionPersistenceDto corporateActionPersistenceDto =
                new CorporateActionPersistenceDto("", null, mockOptions, null, mockPortfolios, "12345", false);

        when(objectReader.readValue(anyString())).thenReturn(corporateActionPersistenceDto);

        response = corporateActionElectionApiController
                .submitCorporateActionElectionsByPost("08E24C73A07BF3B6323E907360B880E544B662EBA8EE1BF7", "whatever");
        assertNotNull(response.getData());
    }

    @Test
    public void testSubmitCorporateActionElections_ForDg_NothingProvided() throws IOException {
        ImCorporateActionElectionDetailsDto caeDto = new ImCorporateActionElectionDetailsDto();

        when(userProfileService.isDealerGroup()).thenReturn(true);
        when(userProfileService.isInvestmentManager()).thenReturn(false);
        when(corporateActionCommonService.getUserProfileService()).thenReturn(userProfileService);

        when(corporateActionElectionDtoService.submit(any(CorporateActionElectionDetailsDto.class), any(ServiceErrors.class)))
                .thenReturn(caeDto);

        List<CorporateActionOptionDto> mockOptions = new ArrayList<>();
        List<ImCorporateActionPortfolioModelDto> mockAccounts = null;
        List<ImCorporateActionPortfolioModelDto> mockPortfolios = null;
        when(objectReader.readValue(anyString())).thenReturn(mockOptions, mockPortfolios, mockAccounts);

        ApiResponse response = corporateActionElectionApiController.submitCorporateActionElections(
                "08E24C73A07BF3B6323E907360B880E544B662EBA8EE1BF7", "", null, null, "12345");
        assertNotNull(response.getData());

        // Request body method
        CorporateActionPersistenceDto corporateActionPersistenceDto =
                new CorporateActionPersistenceDto("", null, mockOptions, null, mockPortfolios, "12345", false);

        when(objectReader.readValue(anyString())).thenReturn(corporateActionPersistenceDto);
        response = corporateActionElectionApiController
                .submitCorporateActionElectionsByPost("08E24C73A07BF3B6323E907360B880E544B662EBA8EE1BF7", "whatever");
        assertNotNull(response.getData());
    }

    @Test
    public void testSubmitCorporateActionElections_ForPM_AccountsOnly() throws IOException {
        ImCorporateActionElectionDetailsDto caeDto = new ImCorporateActionElectionDetailsDto();

        when(userProfileService.isDealerGroup()).thenReturn(false);
        when(userProfileService.isInvestmentManager()).thenReturn(false);
        when(userProfileService.isPortfolioManager()).thenReturn(true);
        when(corporateActionCommonService.getUserProfileService()).thenReturn(userProfileService);

        when(corporateActionElectionDtoService.submit(any(CorporateActionElectionDetailsDto.class), any(ServiceErrors.class)))
                .thenReturn(caeDto);

        List<CorporateActionOptionDto> mockOptions = new ArrayList<>();
        List<ImCorporateActionPortfolioModelDto> mockAccounts = new ArrayList<>();
        List<ImCorporateActionPortfolioModelDto> mockPortfolios = new ArrayList<>();
        when(objectReader.readValue(anyString())).thenReturn(mockOptions, mockPortfolios, mockAccounts);

        ApiResponse response = corporateActionElectionApiController.submitCorporateActionElections(
                "08E24C73A07BF3B6323E907360B880E544B662EBA8EE1BF7", "", "", null, "12345");
        assertNotNull(response.getData());

        // Request body method
        CorporateActionPersistenceDto corporateActionPersistenceDto = new CorporateActionPersistenceDto("", null, mockOptions,
                null, mockPortfolios, "12345", false);

        when(objectReader.readValue(anyString())).thenReturn(corporateActionPersistenceDto);

        response = corporateActionElectionApiController.submitCorporateActionElectionsByPost(
                "08E24C73A07BF3B6323E907360B880E544B662EBA8EE1BF7", "whatever");
        assertNotNull(response.getData());
    }

    @Test
    public void testSubmitCorporateActionElections_ForPM_AccountsAndModels() throws IOException {
        ImCorporateActionElectionDetailsDto caeDto = new ImCorporateActionElectionDetailsDto();

        when(userProfileService.isDealerGroup()).thenReturn(false);
        when(userProfileService.isInvestmentManager()).thenReturn(false);
        when(userProfileService.isPortfolioManager()).thenReturn(true);
        when(corporateActionCommonService.getUserProfileService()).thenReturn(userProfileService);

        when(imCorporateActionElectionDtoService.submit(any(ImCorporateActionElectionDetailsDto.class), any(ServiceErrors.class)))
                .thenReturn(caeDto);

        List<CorporateActionOptionDto> mockOptions = new ArrayList<>();
        List<ImCorporateActionPortfolioModelDto> mockAccounts = new ArrayList<>();

        ImCorporateActionPortfolioModelDto mockPortfolio = mock(ImCorporateActionPortfolioModelDto.class);
        when(mockPortfolio.getSelectedElections()).thenReturn(new CorporateActionSelectedOptionsDto());
        List<ImCorporateActionPortfolioModelDto> mockPortfolios = new ArrayList<>();
        mockPortfolios.add(mockPortfolio);

        when(objectReader.readValue(anyString())).thenReturn(mockOptions, mockPortfolios, mockAccounts);

        ApiResponse response = corporateActionElectionApiController.submitCorporateActionElections(
                "08E24C73A07BF3B6323E907360B880E544B662EBA8EE1BF7", "", "", "", "12345");
        assertNotNull(response.getData());

        // Request body method
        CorporateActionPersistenceDto corporateActionPersistenceDto = new CorporateActionPersistenceDto("", null, mockOptions,
                null, mockPortfolios, "12345", false);

        when(objectReader.readValue(anyString())).thenReturn(corporateActionPersistenceDto);
        response = corporateActionElectionApiController
                .submitCorporateActionElectionsByPost("08E24C73A07BF3B6323E907360B880E544B662EBA8EE1BF7", "whatever");
        assertNotNull(response.getData());
    }

    @Test
    public void testSubmitCorporateActionElections_ForPM_NothingProvided() throws IOException {
        ImCorporateActionElectionDetailsDto caeDto = new ImCorporateActionElectionDetailsDto();

        when(userProfileService.isDealerGroup()).thenReturn(false);
        when(userProfileService.isInvestmentManager()).thenReturn(false);
        when(userProfileService.isPortfolioManager()).thenReturn(true);

        when(corporateActionCommonService.getUserProfileService()).thenReturn(userProfileService);

        when(corporateActionElectionDtoService.submit(any(CorporateActionElectionDetailsDto.class), any(ServiceErrors.class)))
                .thenReturn(caeDto);

        List<CorporateActionOptionDto> mockOptions = new ArrayList<>();
        List<ImCorporateActionPortfolioModelDto> mockAccounts = null;
        List<ImCorporateActionPortfolioModelDto> mockPortfolios = null;
        when(objectReader.readValue(anyString())).thenReturn(mockOptions, mockPortfolios, mockAccounts);

        ApiResponse response = corporateActionElectionApiController.submitCorporateActionElections(
                "08E24C73A07BF3B6323E907360B880E544B662EBA8EE1BF7", "", null, null, "12345");
        assertNotNull(response.getData());

        // Request body method
        CorporateActionPersistenceDto corporateActionPersistenceDto = new CorporateActionPersistenceDto("", null, mockOptions,
                null, mockPortfolios, "12345", false);

        when(objectReader.readValue(anyString())).thenReturn(corporateActionPersistenceDto);

        response = corporateActionElectionApiController.submitCorporateActionElectionsByPost(
                "08E24C73A07BF3B6323E907360B880E544B662EBA8EE1BF7", "whatever");

        assertNotNull(response.getData());
    }

    @Test
    public void testSubmitCorporateActionElectionsByPost_whenParamsPassed_thenDtoMappedCorrectly() throws Exception {
        when(userProfileService.isDealerGroup()).thenReturn(false);
        when(userProfileService.isInvestmentManager()).thenReturn(false);
        when(userProfileService.isPortfolioManager()).thenReturn(false);
        when(userProfileService.isInvestor()).thenReturn(false);
        when(corporateActionCommonService.getUserProfileService()).thenReturn(userProfileService);
        when(permissionService.checkSubmitPermission(anyList())).thenReturn(true);

        MockMvc mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();
        MockHttpServletRequestBuilder post =
                MockMvcRequestBuilders.post("/secure/api/corporateactions/v1_0/EDCC12075B29E898A8237F3500C3B1424A2DEB6DCB807AB2/submitElections");
        post.body(("{\"options\":[{\"id\":0,\"summary\":\"Take up\",\"isDefault\":false},{\"id\":1,\"summary\":\"Lapse\"," +
                "\"isDefault\":true}],\"accounts\":[{\"clientId\":\"1625347\",\"accountId\":\"120153580\"," +
                "\"accountKey\":\"18CF0B2B4D1E45BB4FD1F58C75BD50490CAFDB475FAB7C1D\",\"positionId\":\"1628844\"," +
                "\"selectedElections\":{\"options\":[{\"optionId\":0,\"units\":10}]}},{\"clientId\":\"1625240\"," +
                "\"accountId\":\"120153481\",\"accountKey\":\"409860480329925ADBEE100DDB631CBA4F36908D46E32790\"," +
                "\"positionId\":\"1628816\",\"selectedElections\":{\"options\":[{\"optionId\":0,\"units\":20}]}}]," +
                "\"closeDate\":\"2017-11-15T01:00:00.000Z\"}").getBytes());

        int status = mockMvc.perform(post).andReturn().getResponse().getStatus();

        verify(corporateActionElectionDtoService, atLeastOnce()).submit(argumentCaptor.capture(), any(ServiceErrors.class));

        CorporateActionElectionDetailsDto corporateActionElectionDetailsDto = (CorporateActionElectionDetailsDto) argumentCaptor.getValue();

        assertNotNull(corporateActionElectionDetailsDto.getOptions());
        assertNotNull(corporateActionElectionDetailsDto.getAccounts());

        CorporateActionOptionDto optionDto = corporateActionElectionDetailsDto.getOptions().iterator().next();

        assertEquals(0, optionDto.getId().intValue());
        assertEquals("Take up", optionDto.getSummary());
        assertNull(optionDto.getIsDefault());
        assertNull(optionDto.getTitle());
        assertNull(optionDto.getIsNoAction());

        CorporateActionAccountDetailsDto accountDetailsDto = corporateActionElectionDetailsDto.getAccounts().iterator().next();

        assertEquals("1628844", accountDetailsDto.getPositionId());
        assertEquals("120153580", accountDetailsDto.getAccountId());
        assertEquals("18CF0B2B4D1E45BB4FD1F58C75BD50490CAFDB475FAB7C1D", accountDetailsDto.getAccountKey());
        assertNotNull(accountDetailsDto.getSelectedElections());
        assertEquals(0, accountDetailsDto.getSelectedElections().getPrimarySelectedOption().getOptionId().intValue());
        assertEquals(BigDecimal.TEN, accountDetailsDto.getSelectedElections().getPrimarySelectedOption().getUnits());

        assertNull(accountDetailsDto.getSavedElections());
        assertNull(accountDetailsDto.getSubmittedElections());
        assertNull(accountDetailsDto.getHolding());
        assertNull(accountDetailsDto.getElectionStatus());
        assertNull(accountDetailsDto.getSavedElections());
        assertNull(accountDetailsDto.getCash());
        assertNull(accountDetailsDto.getTransactionDescription());
        assertNull(accountDetailsDto.getTransactionNumber());
        assertNull(accountDetailsDto.getTransactionStatus());
        assertNull(accountDetailsDto.getAccountType());
        assertNull(accountDetailsDto.getAccountName());
        assertNull(accountDetailsDto.getClientId());
        assertNull(accountDetailsDto.getClientName());
        assertNull(accountDetailsDto.getClientEmail());
        assertNull(accountDetailsDto.getClientPhone());
        assertNull(accountDetailsDto.getOriginalHolding());
        assertNull(accountDetailsDto.getPortfolio());
        assertNull(accountDetailsDto.getPortfolioValue());
        assertNull(accountDetailsDto.getNotification());

        assertEquals(200, status);
    }

    @Test
    public void testSubmitCorporateActionElections_whenParamsPassed_thenDtoMappedCorrectly() throws Exception {
        when(userProfileService.isDealerGroup()).thenReturn(false);
        when(userProfileService.isInvestmentManager()).thenReturn(false);
        when(userProfileService.isPortfolioManager()).thenReturn(false);
        when(userProfileService.isInvestor()).thenReturn(false);
        when(corporateActionCommonService.getUserProfileService()).thenReturn(userProfileService);
        when(permissionService.checkSubmitPermission(anyList())).thenReturn(true);

        MockMvc mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();
        MockHttpServletRequestBuilder post =
                MockMvcRequestBuilders.post("/secure/api/corporateactions/v1_0/EDCC12075B29E898A8237F3500C3B1424A2DEB6DCB807AB2/submit");
        post.param("options", "[{\"id\":1,\"summary\":\"$22 per share & 1:2 BHP shares\",\"isDefault\":false},{\"id\":2,\"summary\":\"Do " +
                "not participate\",\"isDefault\":true}]");
        post.param("accounts", "[{\"clientId\":\"519667\",\"accountId\":\"120070537\"," +
                "\"accountKey\":\"2713CB02D37D5339E2C6CCF3FBC0A620B74D9F8417BE817B\",\"positionId\":\"528298\"," +
                "\"selectedElections\":{\"options\":[{\"optionId\":1,\"units\":10,\"oversubscribe\":10,\"percent\":10}]," +
                "\"minimumPriceId\": 2}}]");

        int status = mockMvc.perform(post).andReturn().getResponse().getStatus();

        verify(corporateActionElectionDtoService, atLeastOnce()).submit(argumentCaptor.capture(), any(ServiceErrors.class));

        CorporateActionElectionDetailsDto corporateActionElectionDetailsDto = (CorporateActionElectionDetailsDto) argumentCaptor.getValue();

        assertNotNull(corporateActionElectionDetailsDto.getOptions());
        assertNotNull(corporateActionElectionDetailsDto.getAccounts());

        CorporateActionOptionDto optionDto = corporateActionElectionDetailsDto.getOptions().iterator().next();

        assertEquals(1, optionDto.getId().intValue());
        assertEquals("$22 per share & 1:2 BHP shares", optionDto.getSummary());
        assertNull(optionDto.getIsDefault());
        assertNull(optionDto.getTitle());
        assertNull(optionDto.getIsNoAction());

        CorporateActionAccountDetailsDto accountDetailsDto = corporateActionElectionDetailsDto.getAccounts().iterator().next();

        assertEquals("528298", accountDetailsDto.getPositionId());
        assertEquals("120070537", accountDetailsDto.getAccountId());
        assertEquals("2713CB02D37D5339E2C6CCF3FBC0A620B74D9F8417BE817B", accountDetailsDto.getAccountKey());
        assertNotNull(accountDetailsDto.getSelectedElections());
        assertEquals(2, accountDetailsDto.getSelectedElections().getMinimumPriceId().intValue());
        assertEquals(1, accountDetailsDto.getSelectedElections().getPrimarySelectedOption().getOptionId().intValue());
        assertEquals(BigDecimal.TEN, accountDetailsDto.getSelectedElections().getPrimarySelectedOption().getUnits());
        assertEquals(BigDecimal.TEN, accountDetailsDto.getSelectedElections().getPrimarySelectedOption().getOversubscribe());
        assertEquals(BigDecimal.TEN, accountDetailsDto.getSelectedElections().getPrimarySelectedOption().getPercent());
        assertEquals(1, accountDetailsDto.getSelectedElections().getPrimarySelectedOption().getOptionId().intValue());

        assertNull(accountDetailsDto.getSavedElections());
        assertNull(accountDetailsDto.getSubmittedElections());
        assertNull(accountDetailsDto.getHolding());
        assertNull(accountDetailsDto.getElectionStatus());
        assertNull(accountDetailsDto.getSavedElections());
        assertNull(accountDetailsDto.getCash());
        assertNull(accountDetailsDto.getTransactionDescription());
        assertNull(accountDetailsDto.getTransactionNumber());
        assertNull(accountDetailsDto.getTransactionStatus());
        assertNull(accountDetailsDto.getAccountType());
        assertNull(accountDetailsDto.getAccountName());
        assertNull(accountDetailsDto.getClientId());
        assertNull(accountDetailsDto.getClientName());
        assertNull(accountDetailsDto.getClientEmail());
        assertNull(accountDetailsDto.getClientPhone());
        assertNull(accountDetailsDto.getOriginalHolding());
        assertNull(accountDetailsDto.getPortfolio());
        assertNull(accountDetailsDto.getPortfolioValue());
        assertNull(accountDetailsDto.getNotification());

        assertEquals(200, status);
    }

    @EnableWebMvc
    @ComponentScan(basePackages = "com.bt.nextgen.api.corporateaction.v1.controller",
            excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {CorporateActionApiController.class,
                                                                                                CorporateActionApprovalApiController.class,
                                                                                                CorporateActionPersistenceApiController
                                                                                                        .class})})
    private static class TestConfiguration {
        @Bean(name = "PropertyPlaceholderConfigurer")
        private PropertyPlaceholderConfigurer getPropertyPlaceholderConfigurerBean() {
            PropertyPlaceholderConfigurer pc = new PropertyPlaceholderConfigurer();
            pc.setLocation(new ClassPathResource("/com/bt/nextgen/api/corporateaction/v1/UriConfig.properties"));
            return pc;
        }

        @Bean(name = "ClientApplicationDtoDeserializer")
        private ClientApplicationDtoDeserializer getClientApplicationDtoDeserializer() {
            return mock(ClientApplicationDtoDeserializer.class);
        }

        @Bean(name = "jsonObjectMapper")
        private ObjectMapper getObjectMapper() {
            return new JsonObjectMapper();
        }

        @Bean(name = "SecureJsonObjectMapper")
        private ObjectMapper getSecureObjectMapper() {
            return new SecureJsonObjectMapper();
        }

        @Bean(name = "FormDataValidator")
        private FormDataValidator getFormDataValidator() {
            return mock(FormDataValidator.class);
        }

        @Bean(name = "corporateActionElectionDtoService")
        private CorporateActionElectionDtoService getCorporateActionElectionDtoService() {
            return corporateActionElectionDtoService;
        }

        @Bean(name = "imCorporateActionElectionDtoService")
        private CorporateActionElectionDtoService getImCorporateActionElectionDtoService() {
            return corporateActionElectionDtoService;
        }

        @Bean
        private CorporateActionAccountDetailsDtoErrorMapper getCorporateActionAccountDetailsDtoErrorMapper() {
            return corporateActionAccountDetailsDtoErrorMapper;
        }

        @Bean
        private CorporateActionCommonService getCorporateActionCommonService() {
            return corporateActionCommonService;
        }

        @Bean
        private CorporateActionPermissionService permissionService() {
            return permissionService;
        }

        @Bean
        private UserProfileService getUserProfileService() {
            return userProfileService;
        }
    }
}
