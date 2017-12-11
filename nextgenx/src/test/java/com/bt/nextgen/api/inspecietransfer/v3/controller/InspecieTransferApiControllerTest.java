package com.bt.nextgen.api.inspecietransfer.v3.controller;

import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferDto;
import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v3.model.TransferOrderDto;
import com.bt.nextgen.api.inspecietransfer.v3.service.InspecieTransferDtoService;
import com.bt.nextgen.api.inspecietransfer.v3.service.TransferOrderDtoService;
import com.bt.nextgen.api.inspecietransfer.v3.validation.InspecieTransferDtoErrorMapper;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.config.SecureJsonObjectMapper;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.service.ServiceErrors;
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
public class InspecieTransferApiControllerTest {

    @InjectMocks
    private InspecieTransferApiController inspecieTransferApiController;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private InspecieTransferDtoService transferService;

    @Mock
    private InspecieTransferDtoErrorMapper inspecieTransferDtoErrorMapper;

    @Mock
    private TransferOrderDtoService transferOrderService;

    @Mock
    private SecureJsonObjectMapper mockMapper;

    private static final String inspecieTransferJsonInvalidObject = "{\"key\":{\"accountId\":\"106EF72FEE1B2EC13B321AA399A3B74AC0C02914EBEAC09C\",\"transferId\":null},\"orderType\":\"In specie account transfer\",\"transferType\":\"Listed Securities Broker Sponsored\",\"isCBO\":false,\"transferDate\":null,\"sourceAccountKey\":{\"accountId\":\"106EF72FEE1B2EC13B321AA399A3B74AC0C02914EBEAC09C\"},\"targetAccountKey\":{\"accountId\":\"106EF72FEE1B2EC13B321AA399A3B74AC0C02914EBEAC09C\"},\"targetContainerId\":\"221175E508902AD07AA4064078FD148B3B2F80ABFDDF8DE8\",\"sourceContainerId\":null,\"targetAssetId\":null,\"transferAssets\":[{\"asset\":{\"type\":\"Asset\",\"assetId\":\"110521\",\"assetName\":\"Westpac Banking Corporation\",\"assetType\":\"SHARE\",\"assetCluster\":\"ls\",\"status\":\"Open\",\"isin\":\"AU000000WBC1\",\"assetCode\":\"WBC\",\"ipsId\":null,\"assetClass\":\"Australian shares\",\"groupClass\":\"Australian shares\",\"distributionMethods\":null,\"riskMeasure\":null,\"issuerId\":\"109674\",\"issuerName\":\"Westpac Banking Corporation\",\"prePensionRestricted\":false,\"key\":\"110521\"},\"quantity\":1,\"sponsorDetails\":{\"pid\":\"01193\",\"pidName\":null,\"custodian\":null,\"hin\":\"X1234512345\",\"srn\":null,\"accNumber\":null,\"sourceContainerId\":null,\"key\":\"PID:01193:HIN:X1234512345:\"},\"taxParcels\":[{\"assetId\":null,\"assetCode\":\"WBC\",\"taxRelevanceDate\":\"2014-12-31T16:00:00.000Z\",\"taxVisibilityDate\":null,\"quantity\":1,\"costBase\":null,\"reducedCostBase\":null,\"indexedCostBase\":null,\"originalCostBase\":0}],\"isCashTransfer\":null,\"amount\":null,\"vettWarnings\":null}],\"transferPreferences\":[],\"action\":\"wrooong\",\"errorType\":\"warning\",\"type\":\"DomainApiError\"}],\"isFullClose\":false,\"incomePreference\":null,\"type\":\"InspecieTransferDtoImpl\"}";
    private static final String inspecieTransferJsonValidateObject = "{\"key\":{\"accountId\":\"106EF72FEE1B2EC13B321AA399A3B74AC0C02914EBEAC09C\",\"transferId\":null},\"orderType\":\"In specie account transfer\",\"transferType\":\"Listed Securities Broker Sponsored\",\"isCBO\":false,\"transferDate\":null,\"sourceAccountKey\":{\"accountId\":\"106EF72FEE1B2EC13B321AA399A3B74AC0C02914EBEAC09C\"},\"targetAccountKey\":{\"accountId\":\"106EF72FEE1B2EC13B321AA399A3B74AC0C02914EBEAC09C\"},\"targetContainerId\":\"221175E508902AD07AA4064078FD148B3B2F80ABFDDF8DE8\",\"sourceContainerId\":null,\"targetAssetId\":null,\"transferAssets\":[{\"asset\":{\"type\":\"Asset\",\"assetId\":\"110521\",\"assetName\":\"Westpac Banking Corporation\",\"assetType\":\"SHARE\",\"assetCluster\":\"ls\",\"status\":\"Open\",\"isin\":\"AU000000WBC1\",\"assetCode\":\"WBC\",\"ipsId\":null,\"assetClass\":\"Australian shares\",\"groupClass\":\"Australian shares\",\"distributionMethods\":null,\"riskMeasure\":null,\"issuerId\":\"109674\",\"issuerName\":\"Westpac Banking Corporation\",\"prePensionRestricted\":false,\"key\":\"110521\"},\"quantity\":1,\"sponsorDetails\":{\"pid\":\"01193\",\"pidName\":null,\"custodian\":null,\"hin\":\"X1234512345\",\"srn\":null,\"accNumber\":null,\"sourceContainerId\":null,\"key\":\"PID:01193:HIN:X1234512345:\"},\"taxParcels\":[{\"assetId\":null,\"assetCode\":\"WBC\",\"taxRelevanceDate\":\"2014-12-31T16:00:00.000Z\",\"taxVisibilityDate\":null,\"quantity\":1,\"costBase\":null,\"reducedCostBase\":null,\"indexedCostBase\":null,\"originalCostBase\":0}],\"isCashTransfer\":null,\"amount\":null,\"vettWarnings\":null}],\"transferPreferences\":[],\"action\":\"validate\",\"errorType\":\"warning\",\"type\":\"DomainApiError\"}],\"isFullClose\":false,\"incomePreference\":null,\"type\":\"InspecieTransferDtoImpl\"}";
    private static final String inspecieTransferJsonSubmitObject = "{\"key\":{\"accountId\":\"106EF72FEE1B2EC13B321AA399A3B74AC0C02914EBEAC09C\",\"transferId\":null},\"orderType\":\"In specie account transfer\",\"transferType\":\"Listed Securities Broker Sponsored\",\"isCBO\":false,\"transferDate\":null,\"sourceAccountKey\":{\"accountId\":\"106EF72FEE1B2EC13B321AA399A3B74AC0C02914EBEAC09C\"},\"targetAccountKey\":{\"accountId\":\"106EF72FEE1B2EC13B321AA399A3B74AC0C02914EBEAC09C\"},\"targetContainerId\":\"221175E508902AD07AA4064078FD148B3B2F80ABFDDF8DE8\",\"sourceContainerId\":null,\"targetAssetId\":null,\"transferAssets\":[{\"asset\":{\"type\":\"Asset\",\"assetId\":\"110521\",\"assetName\":\"Westpac Banking Corporation\",\"assetType\":\"SHARE\",\"assetCluster\":\"ls\",\"status\":\"Open\",\"isin\":\"AU000000WBC1\",\"assetCode\":\"WBC\",\"ipsId\":null,\"assetClass\":\"Australian shares\",\"groupClass\":\"Australian shares\",\"distributionMethods\":null,\"riskMeasure\":null,\"issuerId\":\"109674\",\"issuerName\":\"Westpac Banking Corporation\",\"prePensionRestricted\":false,\"key\":\"110521\"},\"quantity\":1,\"sponsorDetails\":{\"pid\":\"01193\",\"pidName\":null,\"custodian\":null,\"hin\":\"X1234512345\",\"srn\":null,\"accNumber\":null,\"sourceContainerId\":null,\"key\":\"PID:01193:HIN:X1234512345:\"},\"taxParcels\":[{\"assetId\":null,\"assetCode\":\"WBC\",\"taxRelevanceDate\":\"2014-12-31T16:00:00.000Z\",\"taxVisibilityDate\":null,\"quantity\":1,\"costBase\":null,\"reducedCostBase\":null,\"indexedCostBase\":null,\"originalCostBase\":0}],\"isCashTransfer\":null,\"amount\":null,\"vettWarnings\":null}],\"transferPreferences\":[],\"action\":\"submit\",\"errorType\":\"warning\",\"type\":\"DomainApiError\"}],\"isFullClose\":false,\"incomePreference\":null,\"type\":\"InspecieTransferDtoImpl\"}";
    private static final SecureJsonObjectMapper mapper = new SecureJsonObjectMapper();

    @Test
    public void testSearch() throws IOException {

        Mockito.when(transferOrderService.search(Mockito.anyListOf(ApiSearchCriteria.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<List<TransferOrderDto>>() {
                    @Override
                    public List<TransferOrderDto> answer(InvocationOnMock invocation) throws Throwable {
                        List<ApiSearchCriteria> criteriaList = (List<ApiSearchCriteria>) invocation.getArguments()[0];

                        Assert.assertEquals(1, criteriaList.size());
                        Assert.assertEquals("accountId", criteriaList.get(0).getProperty());
                        Assert.assertEquals("accountIdValue", criteriaList.get(0).getValue());

                        return Collections.emptyList();
                    }
                });

        inspecieTransferApiController.search("accountIdValue");
    }

    @Test(expected = AccessDeniedException.class)
    public void testCreate_whenEmulating_thenAccessDenied() throws IOException {
        Mockito.when(userProfileService.isEmulating()).thenReturn(Boolean.TRUE);
        inspecieTransferApiController.create("accountIdValue", inspecieTransferJsonSubmitObject);
    }

    @Test(expected = AccessDeniedException.class)
    public void testCreate_whenInvalidAction_thenAccessDenied() throws IOException {
        Mockito.when(userProfileService.isEmulating()).thenReturn(Boolean.FALSE);

        InspecieTransferDto dto = mapper.readerWithView(JsonViews.Write.class).forType(InspecieTransferDtoImpl.class)
                .readValue(JsonSanitizer.sanitize(inspecieTransferJsonInvalidObject));

        ObjectReader reader = Mockito.mock(ObjectReader.class);
        Mockito.when(reader.forType(Mockito.any(Class.class))).thenReturn(reader);
        Mockito.when(reader.readValue(Mockito.anyString())).thenReturn(dto);

        Mockito.when(mockMapper.readerWithView(Mockito.any(Class.class))).thenReturn(reader);

        inspecieTransferApiController.create("accountIdValue", inspecieTransferJsonInvalidObject);
    }

    @Test
    public void testCreate_whenValidateAction_thenCorrectParameters() throws IOException {
        Mockito.when(userProfileService.isEmulating()).thenReturn(Boolean.FALSE);

        InspecieTransferDto dto = mapper.readerWithView(JsonViews.Write.class).forType(InspecieTransferDtoImpl.class)
                .readValue(JsonSanitizer.sanitize(inspecieTransferJsonValidateObject));

        ObjectReader reader = Mockito.mock(ObjectReader.class);
        Mockito.when(reader.forType(Mockito.any(Class.class))).thenReturn(reader);
        Mockito.when(reader.readValue(Mockito.anyString())).thenReturn(dto);

        Mockito.when(mockMapper.readerWithView(Mockito.any(Class.class))).thenReturn(reader);
        Mockito.when(transferService.validate(Mockito.any(InspecieTransferDto.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<InspecieTransferDto>() {
                    @Override
                    public InspecieTransferDto answer(InvocationOnMock invocation) throws Throwable {
                        InspecieTransferDto paramDto = (InspecieTransferDto) invocation.getArguments()[0];

                        Assert.assertNotNull(paramDto);
                        Assert.assertEquals("accountIdValue", paramDto.getKey().getAccountId());
                        Assert.assertEquals("validate", paramDto.getAction());

                        return Mockito.mock(InspecieTransferDto.class);
                    }
                });

        inspecieTransferApiController.create("accountIdValue", inspecieTransferJsonValidateObject);
    }

    @Test
    public void testCreate_whenSubmitAction_thenCorrectParameters() throws IOException {
        Mockito.when(userProfileService.isEmulating()).thenReturn(Boolean.FALSE);

        InspecieTransferDto dto = mapper.readerWithView(JsonViews.Write.class).forType(InspecieTransferDtoImpl.class)
                .readValue(JsonSanitizer.sanitize(inspecieTransferJsonSubmitObject));

        ObjectReader reader = Mockito.mock(ObjectReader.class);
        Mockito.when(reader.forType(Mockito.any(Class.class))).thenReturn(reader);
        Mockito.when(reader.readValue(Mockito.anyString())).thenReturn(dto);

        Mockito.when(mockMapper.readerWithView(Mockito.any(Class.class))).thenReturn(reader);
        Mockito.when(transferService.submit(Mockito.any(InspecieTransferDto.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<InspecieTransferDto>() {
                    @Override
                    public InspecieTransferDto answer(InvocationOnMock invocation) throws Throwable {
                        InspecieTransferDto paramDto = (InspecieTransferDto) invocation.getArguments()[0];

                        Assert.assertNotNull(paramDto);
                        Assert.assertEquals("accountIdValue", paramDto.getKey().getAccountId());
                        Assert.assertEquals("submit", paramDto.getAction());

                        return Mockito.mock(InspecieTransferDto.class);
                    }
                });

        inspecieTransferApiController.create("accountIdValue", inspecieTransferJsonSubmitObject);
    }
}
