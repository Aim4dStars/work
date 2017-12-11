package com.bt.nextgen.api.rollover.v1.controller;

import com.bt.nextgen.api.rollover.v1.model.ReceivedContributionDto;
import com.bt.nextgen.api.rollover.v1.model.ReceivedRolloverFundDto;
import com.bt.nextgen.api.rollover.v1.model.RolloverHistoryDto;
import com.bt.nextgen.api.rollover.v1.model.RolloverInDto;
import com.bt.nextgen.api.rollover.v1.model.RolloverInDtoImpl;
import com.bt.nextgen.api.rollover.v1.model.RolloverKey;
import com.bt.nextgen.api.rollover.v1.service.ReceivedContributionDtoService;
import com.bt.nextgen.api.rollover.v1.service.ReceivedRolloverDtoService;
import com.bt.nextgen.api.rollover.v1.service.RolloverHistoryDtoService;
import com.bt.nextgen.api.rollover.v1.service.RolloverInDtoService;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.config.SecureJsonObjectMapper;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.json.JsonSanitizer;
import org.junit.Assert;
import org.junit.Before;
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
public class RolloverInApiControllerTest {

    @InjectMocks
    private RolloverInApiController rolloverInApiController;

    @Mock
    private UserProfileService profileService;

    @Mock
    private RolloverInDtoService rolloverInDtoService;

    @Mock
    private RolloverHistoryDtoService rolloverHistoryDtoService;

    @Mock
    private ReceivedRolloverDtoService receivedRolloverDtoService;

    @Mock
    private ReceivedContributionDtoService receivedContributionDtoService;

    @Mock
    private SecureJsonObjectMapper mockMapper;

    private static final String rolloverJsonObject = "{\"apiVersion\":\"v1_0\",\"status\":1,\"data\":{\"key\":{\"accountId\":\"54EE13907A72EB67AA7627EE17AB0DC5CEAFED4B79673023\",\"rolloverId\":null},\"rolloverDetails\":[{\"accountId\":\"54EE13907A72EB67AA7627EE17AB0DC5CEAFED4B79673023\",\"fundId\":\"\",\"fundName\":\"David Alexander Baxter Superannuation Fund\",\"fundAbn\":\"11099645885\",\"fundUsi\":\"11099645885001\",\"rolloverType\":\"Cash Rollover\",\"amount\":100,\"fundAmount\":100,\"panInitiated\":true,\"accountName\":\"M12341234\",\"rolloverOption\":\"Partial Rollover\",\"includeInsurance\":false,\"lastTransSeqId\":\"1\",\"key\":{\"accountId\":\"54EE13907A72EB67AA7627EE17AB0DC5CEAFED4B79673023\"},\"type\":\"com.bt.nextgen.api.rollover.v1.model.RolloverDetailsDto\"}],\"rolloverType\":\"Cash Rollover\",\"type\":\"RolloverInDtoImpl\"},\"error\":null,\"paging\":null,\"lastUpdatedTime\":\"2017-01-20T05:28:01.337Z\",\"id\":{\"accountId\":\"54EE13907A72EB67AA7627EE17AB0DC5CEAFED4B79673023\",\"rolloverId\":null}}";
    private static final SecureJsonObjectMapper mapper = new SecureJsonObjectMapper();

    @Before
    public void setup() {
    }

    @Test(expected = AccessDeniedException.class)
    public void test_whenEmulating_thenSubmitNotAllowed() throws IOException {
        Mockito.when(profileService.isEmulating()).thenReturn(true);
        rolloverInApiController.create("accountId", "fakeRolloverDetailsJson");
    }

    @Test(expected = AccessDeniedException.class)
    public void test_whenEmulating_thenSaveNotAllowed() throws IOException {
        Mockito.when(profileService.isEmulating()).thenReturn(true);
        rolloverInApiController.save("accountId", "fakeRolloverDetailsJson");
    }

    @Test(expected = AccessDeniedException.class)
    public void test_whenEmulating_thenDiscardNotAllowed() throws IOException {
        Mockito.when(profileService.isEmulating()).thenReturn(true);
        rolloverInApiController.discard("accountId", "rolloverId");
    }

    @Test
    public void test_whenNotEmulating_thenSubmitAllowed() throws IOException {
        Mockito.when(profileService.isEmulating()).thenReturn(false);

        RolloverInDto rollIn = mapper.readerWithView(JsonViews.Write.class).forType(RolloverInDtoImpl.class)
                .readValue(JsonSanitizer.sanitize(rolloverJsonObject));

        ObjectReader reader = Mockito.mock(ObjectReader.class);
        Mockito.when(reader.forType(Mockito.any(Class.class))).thenReturn(reader);
        Mockito.when(reader.readValue(Mockito.anyString())).thenReturn(rollIn);

        Mockito.when(mockMapper.readerWithView(Mockito.any(Class.class))).thenReturn(reader);
        Mockito.when(rolloverInDtoService.submit(Mockito.any(RolloverInDto.class), Mockito.any(ServiceErrors.class))).thenReturn(
                rollIn);

        KeyedApiResponse<RolloverKey> response = rolloverInApiController.create(
                "54EE13907A72EB67AA7627EE17AB0DC5CEAFED4B79673023", rolloverJsonObject);

        Assert.assertNotNull(response);
        Assert.assertEquals("54EE13907A72EB67AA7627EE17AB0DC5CEAFED4B79673023", response.getId().getAccountId());
    }

    @Test
    public void test_whenNotEmulating_thenSaveAllowed() throws IOException {
        Mockito.when(profileService.isEmulating()).thenReturn(false);

        RolloverInDto rollIn = mapper.readerWithView(JsonViews.Write.class).forType(RolloverInDtoImpl.class)
                .readValue(JsonSanitizer.sanitize(rolloverJsonObject));

        ObjectReader reader = Mockito.mock(ObjectReader.class);
        Mockito.when(reader.forType(Mockito.any(Class.class))).thenReturn(reader);
        Mockito.when(reader.readValue(Mockito.anyString())).thenReturn(rollIn);

        Mockito.when(mockMapper.readerWithView(Mockito.any(Class.class))).thenReturn(reader);
        Mockito.when(rolloverInDtoService.save(Mockito.any(RolloverInDto.class), Mockito.any(ServiceErrors.class))).thenReturn(
                rollIn);

        KeyedApiResponse<RolloverKey> response = rolloverInApiController.save("54EE13907A72EB67AA7627EE17AB0DC5CEAFED4B79673023",
                rolloverJsonObject);

        Assert.assertNotNull(response);
        Assert.assertEquals("54EE13907A72EB67AA7627EE17AB0DC5CEAFED4B79673023", response.getId().getAccountId());
    }

    @Test
    public void test_whenNotEmulating_thenDiscardAllowed() throws IOException {
        Mockito.when(profileService.isEmulating()).thenReturn(false);

        RolloverInDto rolloverIn = Mockito.mock(RolloverInDto.class);
        Mockito.when(rolloverIn.getKey()).thenReturn(new RolloverKey("accountId", "rolloverId"));
        Mockito.when(rolloverInDtoService.discard(Mockito.any(RolloverKey.class), Mockito.any(ServiceErrors.class))).thenReturn(
                rolloverIn);

        KeyedApiResponse<RolloverKey> response = rolloverInApiController.discard("accountId", "rolloverId");

        Assert.assertNotNull(response);
        Assert.assertEquals("accountId", response.getId().getAccountId());
        Assert.assertEquals("rolloverId", response.getId().getRolloverId());
    }

    @Test
    public void test_whenLoadCalled_thenRolloverDetailsReturned() throws IOException {
        RolloverInDto rolloverIn = Mockito.mock(RolloverInDto.class);
        Mockito.when(rolloverIn.getKey()).thenReturn(new RolloverKey("accountId", "rolloverId"));
        Mockito.when(rolloverInDtoService.find(Mockito.any(RolloverKey.class), Mockito.any(ServiceErrors.class))).thenReturn(
                rolloverIn);

        KeyedApiResponse<RolloverKey> response = rolloverInApiController.load("accountId", "rolloverId");

        Assert.assertNotNull(response);
        Assert.assertEquals("accountId", response.getId().getAccountId());
        Assert.assertEquals("rolloverId", response.getId().getRolloverId());
    }

    @Test
    public void testGetRolloverHistory() throws IOException {
        Mockito.when(
                rolloverHistoryDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<List<RolloverHistoryDto>>() {

                    @Override
                    public List<RolloverHistoryDto> answer(InvocationOnMock invocation) throws Throwable {
                        List<ApiSearchCriteria> criteria = (List<ApiSearchCriteria>) invocation.getArguments()[0];
                        Assert.assertEquals(3, criteria.size());
                        Assert.assertEquals("54EE13907A72EB67AA7627EE17AB0DC5CEAFED4B79673023", criteria.get(0).getValue());
                        Assert.assertEquals("2017-01-01", criteria.get(1).getValue());
                        Assert.assertEquals("2017-03-03", criteria.get(2).getValue());

                        return Collections.emptyList();
                    }
                });

        rolloverInApiController
                .getRolloverHistory("54EE13907A72EB67AA7627EE17AB0DC5CEAFED4B79673023", "2017-01-01", "2017-03-03");
    }

    @Test
    public void testGetReceivedRollover() throws IOException {
        Mockito.when(
                receivedRolloverDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<List<ReceivedRolloverFundDto>>() {

                    @Override
                    public List<ReceivedRolloverFundDto> answer(InvocationOnMock invocation) throws Throwable {
                        List<ApiSearchCriteria> criteria = (List<ApiSearchCriteria>) invocation.getArguments()[0];
                        Assert.assertEquals(1, criteria.size());
                        Assert.assertEquals("54EE13907A72EB67AA7627EE17AB0DC5CEAFED4B79673023", criteria.get(0).getValue());

                        return Collections.emptyList();
                    }
                });

        rolloverInApiController.getReceivedRollover("54EE13907A72EB67AA7627EE17AB0DC5CEAFED4B79673023");
    }

    @Test
    public void testGetReceivedContribution() throws IOException {
        Mockito.when(
                receivedContributionDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class),
                        Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<List<ReceivedContributionDto>>() {

            @Override
            public List<ReceivedContributionDto> answer(InvocationOnMock invocation) throws Throwable {
                List<ApiSearchCriteria> criteria = (List<ApiSearchCriteria>) invocation.getArguments()[0];
                Assert.assertEquals(1, criteria.size());
                Assert.assertEquals("54EE13907A72EB67AA7627EE17AB0DC5CEAFED4B79673023", criteria.get(0).getValue());

                return Collections.emptyList();
            }
        });

        rolloverInApiController.getReceivedContribution("54EE13907A72EB67AA7627EE17AB0DC5CEAFED4B79673023");
    }
}
