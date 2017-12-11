package com.bt.nextgen.api.regularinvestment.v2.controller;

import com.bt.nextgen.api.order.model.OrderGroupKey;
import com.bt.nextgen.api.order.validation.OrderGroupDtoErrorMapper;
import com.bt.nextgen.api.regularinvestment.v2.model.RegularInvestmentDto;
import com.bt.nextgen.api.regularinvestment.v2.service.RegularInvestmentDtoService;
import com.bt.nextgen.api.regularinvestment.v2.service.RegularInvestmentTransactionDtoService;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.config.SecureJsonObjectMapper;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.json.JsonSanitizer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class RegularInvestmentApiControllerTest {

    @InjectMocks
    private RegularInvestmentApiController regularInvestmentApiController;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private RegularInvestmentDtoService ripDtoService;

    @Mock
    private RegularInvestmentTransactionDtoService ripTransactionDtoService;

    @Mock
    private OrderGroupDtoErrorMapper orderGroupDtoErrorMapper;

    @Mock
    private SecureJsonObjectMapper mockMapper;

    private static final String validateRipJson = "{\"orders\":[{\"orderType\":\"buy\",\"amount\":10,\"asset\":{\"assetId\":\"111279\",\"type\":\"ManagedFundAsset\"},\"sellAll\":false,\"assetType\":\"Managedfund\",\"distributionMethod\":null,\"units\":null,\"fundsAllocation\":[{\"accountId\":\"57D3992203C90AC70073BFDE9B7E7E34ABDDF7181C0C633D\",\"allocation\":1}]}],\"depositDetails\":null,\"investmentStartDate\":\"2017-08-30T16:00:00.000Z\",\"investmentEndDate\":\"2017-09-29T18:00:00.000Z\",\"frequency\":\"Monthly\",\"status\":\"null\"}";
    private static final String submitRipJson = "{\"orders\":[{\"orderType\":\"buy\",\"amount\":10,\"asset\":{\"assetId\":\"111279\",\"type\":\"ManagedFundAsset\"},\"sellAll\":false,\"assetType\":\"Managedfund\",\"distributionMethod\":null,\"units\":null,\"fundsAllocation\":[{\"accountId\":\"57D3992203C90AC70073BFDE9B7E7E34ABDDF7181C0C633D\",\"allocation\":1}]}],\"depositDetails\":null,\"investmentStartDate\":\"2017-08-30T16:00:00.000Z\",\"investmentEndDate\":\"2017-09-29T18:00:00.000Z\",\"frequency\":\"Monthly\",\"warnings\":[],\"status\":\"submit\"}";
    private static final String saveRipJson = "{\"orders\":[{\"orderType\":\"buy\",\"amount\":10,\"asset\":{\"assetId\":\"111279\",\"type\":\"ManagedFundAsset\"},\"sellAll\":false,\"assetType\":\"Managedfund\",\"distributionMethod\":null,\"units\":null,\"fundsAllocation\":[{\"accountId\":\"57D3992203C90AC70073BFDE9B7E7E34ABDDF7181C0C633D\",\"allocation\":1}]}],\"depositDetails\":null,\"investmentStartDate\":\"2017-08-30T16:00:00.000Z\",\"investmentEndDate\":\"2017-09-29T18:00:00.000Z\",\"frequency\":\"Monthly\",\"warnings\":[],\"status\":\"save\"}";
    private static final SecureJsonObjectMapper mapper = new SecureJsonObjectMapper();

    @Test(expected = AccessDeniedException.class)
    public void testCreateRip_whenEmulating_thenSubmitNotAllowed() throws IOException {
        Mockito.when(userProfileService.isEmulating()).thenReturn(true);
        regularInvestmentApiController.create("accountId", "modelData", "false");
    }

    @Test(expected = AccessDeniedException.class)
    public void testUpdateRip_whenEmulating_thenSubmitNotAllowed() throws IOException {
        Mockito.when(userProfileService.isEmulating()).thenReturn(true);
        regularInvestmentApiController.updateRegularInvestmentPlan("accountId", "ripId", "edit");
    }

    @Test
    public void testGetRip_whenIdsProvided_thenServiceCalledWithCorrectParameter() {
        Mockito.when(ripDtoService.find(Mockito.any(OrderGroupKey.class), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<RegularInvestmentDto>() {
                    @Override
                    public RegularInvestmentDto answer(InvocationOnMock invocation) {
                        OrderGroupKey key = (OrderGroupKey) invocation.getArguments()[0];
                        Assert.assertEquals("accountId", key.getAccountId());
                        Assert.assertEquals("ripId", key.getOrderGroupId());
                        return Mockito.mock(RegularInvestmentDto.class);
                    }
                });

        regularInvestmentApiController.getRegularInvestmentPlan("accountId", "ripId");
    }

    @Test
    public void testCreateRip_whenValidateDetailsProvided_thenValidateServiceCalledWithCorrectParameter() throws IOException {
        Mockito.when(userProfileService.isEmulating()).thenReturn(false);

        RegularInvestmentDto dto = mapper.readerWithView(JsonViews.Write.class).forType(RegularInvestmentDto.class)
                .readValue(JsonSanitizer.sanitize(validateRipJson));

        ObjectReader reader = Mockito.mock(ObjectReader.class);
        Mockito.when(reader.forType(Mockito.any(Class.class))).thenReturn(reader);
        Mockito.when(reader.readValue(Mockito.anyString())).thenReturn(dto);

        Mockito.when(mockMapper.readerWithView(Mockito.any(Class.class))).thenReturn(reader);
        Mockito.when(ripDtoService.validate(Mockito.any(RegularInvestmentDto.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<RegularInvestmentDto>() {
                    @Override
                    public RegularInvestmentDto answer(InvocationOnMock invocation) {
                        RegularInvestmentDto dto = (RegularInvestmentDto) invocation.getArguments()[0];
                        Assert.assertEquals("accountId", dto.getAccountKey().getAccountId());
                        return Mockito.mock(RegularInvestmentDto.class);
                    }
                });

        regularInvestmentApiController.create("accountId", "JSON", "true");
    }

    @Test
    public void testCreateRip_whenSubmitDetailsProvided_thenSubmitServiceCalledWithCorrectParameter() throws IOException {
        Mockito.when(userProfileService.isEmulating()).thenReturn(false);

        RegularInvestmentDto dto = mapper.readerWithView(JsonViews.Write.class).forType(RegularInvestmentDto.class)
                .readValue(JsonSanitizer.sanitize(submitRipJson));

        ObjectReader reader = Mockito.mock(ObjectReader.class);
        Mockito.when(reader.forType(Mockito.any(Class.class))).thenReturn(reader);
        Mockito.when(reader.readValue(Mockito.anyString())).thenReturn(dto);

        Mockito.when(mockMapper.readerWithView(Mockito.any(Class.class))).thenReturn(reader);
        Mockito.when(ripDtoService.submit(Mockito.any(RegularInvestmentDto.class), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<RegularInvestmentDto>() {
                    @Override
                    public RegularInvestmentDto answer(InvocationOnMock invocation) {
                        RegularInvestmentDto dto = (RegularInvestmentDto) invocation.getArguments()[0];
                        Assert.assertEquals("accountId", dto.getAccountKey().getAccountId());
                        Assert.assertEquals("submit", dto.getStatus());
                        return Mockito.mock(RegularInvestmentDto.class);
                    }
                });

        regularInvestmentApiController.create("accountId", "JSON", "false");
    }

    @Test
    public void testCreateRip_whenSaveDetailsProvided_thenSaveServiceCalledWithCorrectParameter() throws IOException {
        Mockito.when(userProfileService.isEmulating()).thenReturn(false);

        RegularInvestmentDto dto = mapper.readerWithView(JsonViews.Write.class).forType(RegularInvestmentDto.class)
                .readValue(JsonSanitizer.sanitize(saveRipJson));

        ObjectReader reader = Mockito.mock(ObjectReader.class);
        Mockito.when(reader.forType(Mockito.any(Class.class))).thenReturn(reader);
        Mockito.when(reader.readValue(Mockito.anyString())).thenReturn(dto);

        Mockito.when(mockMapper.readerWithView(Mockito.any(Class.class))).thenReturn(reader);
        Mockito.when(ripDtoService.create(Mockito.any(RegularInvestmentDto.class), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<RegularInvestmentDto>() {
                    @Override
                    public RegularInvestmentDto answer(InvocationOnMock invocation) {
                        RegularInvestmentDto dto = (RegularInvestmentDto) invocation.getArguments()[0];
                        Assert.assertEquals("accountId", dto.getAccountKey().getAccountId());
                        Assert.assertEquals("save", dto.getStatus());

                        RegularInvestmentDto result = Mockito.mock(RegularInvestmentDto.class);
                        Mockito.when(result.getKey()).thenReturn(new OrderGroupKey("accountId", "ripId"));
                        return result;
                    }
                });

        regularInvestmentApiController.create("accountId", "JSON", "false");
    }

    @Test
    public void testUpdateRip_whenDetailsProvided_thenServiceCalledWithCorrectParameter() throws IOException {
        Mockito.when(userProfileService.isEmulating()).thenReturn(false);
        Mockito.when(ripDtoService.update(Mockito.any(RegularInvestmentDto.class), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<RegularInvestmentDto>() {
                    @Override
                    public RegularInvestmentDto answer(InvocationOnMock invocation) {
                        RegularInvestmentDto dto = (RegularInvestmentDto) invocation.getArguments()[0];
                        Assert.assertEquals("accountId", dto.getKey().getAccountId());
                        Assert.assertEquals("ripId", dto.getKey().getOrderGroupId());
                        Assert.assertEquals("edit", dto.getRipStatus());
                        return Mockito.mock(RegularInvestmentDto.class);
                    }
                });

        regularInvestmentApiController.updateRegularInvestmentPlan("accountId", "ripId", "edit");
    }

    @Test
    public void testSearch_whenCacheOnlySearch_thenServiceCalledWithCorrectCriteria() throws IOException {
        Mockito.when(
                ripTransactionDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<List<RegularInvestmentDto>>() {
                    @Override
                    public List<RegularInvestmentDto> answer(InvocationOnMock invocation) {
                        List<ApiSearchCriteria> criteria = (List<ApiSearchCriteria>) invocation.getArguments()[0];

                        Assert.assertEquals(2, criteria.size());
                        Assert.assertEquals(Attribute.ACCOUNT_ID, criteria.get(0).getProperty());
                        Assert.assertEquals("accountId", criteria.get(0).getValue());

                        Assert.assertEquals("serviceType", criteria.get(1).getProperty());
                        Assert.assertEquals("cache", criteria.get(1).getValue());

                        return Collections.emptyList();
                    }
                });

        regularInvestmentApiController.search("accountId", "true");
    }

    @Test
    public void testSearch_whenNotCacheOnlySearch_thenServiceCalledWithCorrectCriteria() throws IOException {
        Mockito.when(
                ripTransactionDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<List<RegularInvestmentDto>>() {
                    @Override
                    public List<RegularInvestmentDto> answer(InvocationOnMock invocation) {
                        List<ApiSearchCriteria> criteria = (List<ApiSearchCriteria>) invocation.getArguments()[0];

                        Assert.assertEquals(1, criteria.size());
                        Assert.assertEquals(Attribute.ACCOUNT_ID, criteria.get(0).getProperty());
                        Assert.assertEquals("accountId", criteria.get(0).getValue());

                        return Collections.emptyList();
                    }
                });

        regularInvestmentApiController.search("accountId", "false");
    }

    @Test
    public void testSearch_whenMinimalSearchCriteriaProvided_thenServiceCalledWithCorrectCriteria() throws IOException {
        Mockito.when(
                ripTransactionDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<List<RegularInvestmentDto>>() {
                    @Override
                    public List<RegularInvestmentDto> answer(InvocationOnMock invocation) {
                        List<ApiSearchCriteria> criteria = (List<ApiSearchCriteria>) invocation.getArguments()[0];

                        Assert.assertEquals(1, criteria.size());
                        Assert.assertEquals(Attribute.ACCOUNT_ID, criteria.get(0).getProperty());
                        Assert.assertEquals("accountId", criteria.get(0).getValue());

                        return Collections.emptyList();
                    }
                });

        regularInvestmentApiController.search("accountId", null);
    }
}
