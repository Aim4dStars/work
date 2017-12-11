package com.bt.nextgen.api.inspecietransfer.v3.controller;

import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferDto;
import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferKey;
import com.bt.nextgen.api.inspecietransfer.v3.service.InspecieTransferDtoService;
import com.bt.nextgen.api.inspecietransfer.v3.service.TaxParcelIndependentUploadServiceImpl;
import com.bt.nextgen.api.inspecietransfer.v3.service.TaxParcelUploadService;
import com.bt.nextgen.api.inspecietransfer.v3.validation.InspecieTransferDtoErrorMapper;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.config.SecureJsonObjectMapper;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto.ErrorType;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

@RunWith(MockitoJUnitRunner.class)
public class InspecieTransferUploadApiControllerTest {

    @InjectMocks
    private InspecieTransferUploadApiController uploadApiController;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private InspecieTransferDtoService transferBundleService;

    @Mock
    private com.bt.nextgen.api.inspecietransfer.v2.service.InspecieTransferDtoService transferMassSettleService;

    @Mock
    private InspecieTransferDtoErrorMapper inspecieTransferDtoErrorMapper;

    @Mock
    private TaxParcelUploadService uploadService;

    @Mock
    private TaxParcelIndependentUploadServiceImpl independentUploadService;

    @Mock
    private SecureJsonObjectMapper mockMapper;

    private static final String inspecieTransferJsonValidateObject = "{\"key\":{\"accountId\":\"106EF72FEE1B2EC13B321AA399A3B74AC0C02914EBEAC09C\",\"transferId\":null},\"orderType\":\"In specie account transfer\",\"transferType\":\"Listed Securities Broker Sponsored\",\"isCBO\":false,\"transferDate\":null,\"sourceAccountKey\":{\"accountId\":\"106EF72FEE1B2EC13B321AA399A3B74AC0C02914EBEAC09C\"},\"targetAccountKey\":{\"accountId\":\"106EF72FEE1B2EC13B321AA399A3B74AC0C02914EBEAC09C\"},\"targetContainerId\":\"221175E508902AD07AA4064078FD148B3B2F80ABFDDF8DE8\",\"sourceContainerId\":null,\"targetAssetId\":null,\"transferAssets\":[{\"asset\":{\"type\":\"Asset\",\"assetId\":\"110521\",\"assetName\":\"Westpac Banking Corporation\",\"assetType\":\"SHARE\",\"assetCluster\":\"ls\",\"status\":\"Open\",\"isin\":\"AU000000WBC1\",\"assetCode\":\"WBC\",\"ipsId\":null,\"assetClass\":\"Australian shares\",\"groupClass\":\"Australian shares\",\"distributionMethods\":null,\"riskMeasure\":null,\"issuerId\":\"109674\",\"issuerName\":\"Westpac Banking Corporation\",\"prePensionRestricted\":false,\"key\":\"110521\"},\"quantity\":1,\"sponsorDetails\":{\"pid\":\"01193\",\"pidName\":null,\"custodian\":null,\"hin\":\"X1234512345\",\"srn\":null,\"accNumber\":null,\"sourceContainerId\":null,\"key\":\"PID:01193:HIN:X1234512345:\"},\"taxParcels\":[{\"assetId\":null,\"assetCode\":\"WBC\",\"taxRelevanceDate\":\"2014-12-31T16:00:00.000Z\",\"taxVisibilityDate\":null,\"quantity\":1,\"costBase\":null,\"reducedCostBase\":null,\"indexedCostBase\":null,\"originalCostBase\":0}],\"isCashTransfer\":null,\"amount\":null,\"vettWarnings\":null}],\"transferPreferences\":[],\"action\":\"validate\",\"errorType\":\"warning\",\"type\":\"DomainApiError\"}],\"isFullClose\":false,\"incomePreference\":null,\"type\":\"InspecieTransferDtoImpl\"}";
    private static final SecureJsonObjectMapper mapper = new SecureJsonObjectMapper();

    @Test(expected = AccessDeniedException.class)
    public void testUploadTaxParcel_whenEmulating_thenAccessDenied() throws IOException {
        Mockito.when(userProfileService.isEmulating()).thenReturn(Boolean.TRUE);
        uploadApiController.uploadTaxParcel("accountId", "json", "sponsorId", "sponsorName", null);
    }

    @Test(expected = AccessDeniedException.class)
    public void testUploadIndependentTaxParcel_whenEmulating_thenAccessDenied() throws IOException {
        Mockito.when(userProfileService.isEmulating()).thenReturn(Boolean.TRUE);
        uploadApiController.uploadIndependentTaxParcel("accountId", "transferId", null);
    }

    @Test
    public void testUploadTaxParcel_whenNoErrorsInFile_thenDtoReturned() throws JsonProcessingException, IOException {
        Mockito.when(userProfileService.isEmulating()).thenReturn(Boolean.FALSE);

        InspecieTransferDto dto = mapper.readerWithView(JsonViews.Write.class).forType(InspecieTransferDtoImpl.class)
                .readValue(JsonSanitizer.sanitize(inspecieTransferJsonValidateObject));
        ((InspecieTransferDtoImpl) dto).setWarnings(Collections.<DomainApiErrorDto> emptyList());

        ObjectReader reader = Mockito.mock(ObjectReader.class);
        Mockito.when(reader.forType(Mockito.any(Class.class))).thenReturn(reader);
        Mockito.when(reader.readValue(Mockito.anyString())).thenReturn(dto);

        Mockito.when(mockMapper.readerWithView(Mockito.any(Class.class))).thenReturn(reader);
        Mockito.when(
                uploadService.validateFile(Mockito.any(InspecieTransferDto.class), Mockito.anyString(), Mockito.anyString(),
                        Mockito.any(MultipartFile.class))).thenAnswer(new Answer<InspecieTransferDto>() {

            @Override
            public InspecieTransferDto answer(InvocationOnMock invocation) throws Throwable {
                InspecieTransferDto dto = (InspecieTransferDto) invocation.getArguments()[0];
                Assert.assertNotNull(dto);

                String sponsorId = (String) invocation.getArguments()[1];
                Assert.assertEquals("sponsorId", sponsorId);

                String sponsorName = (String) invocation.getArguments()[2];
                Assert.assertEquals("sponsorName", sponsorName);

                MultipartFile file = (MultipartFile) invocation.getArguments()[3];
                Assert.assertNotNull(file);

                return dto;
            }
        });

        KeyedApiResponse<InspecieTransferKey> apiResponse = Mockito.mock(KeyedApiResponse.class);
        Mockito.when(apiResponse.getData()).thenReturn(Mockito.mock(InspecieTransferDto.class));

        Mockito.when(transferBundleService.validate(Mockito.any(InspecieTransferDto.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<InspecieTransferDto>() {

                    @Override
                    public InspecieTransferDto answer(InvocationOnMock invocation) throws Throwable {
                        InspecieTransferDto dto = (InspecieTransferDto) invocation.getArguments()[0];
                        Assert.assertNotNull(dto);
                        return dto;
                    }
                });

        uploadApiController.uploadTaxParcel("accountId", "json", "sponsorId", "sponsorName", Mockito.mock(MultipartFile.class));
    }

    @Test
    public void testUploadTaxParcel_whenErrorsInFile_thenDtoReturnedWithoutAvaloqCalled() throws JsonProcessingException,
            IOException {
        Mockito.when(userProfileService.isEmulating()).thenReturn(Boolean.FALSE);

        InspecieTransferDto dto = mapper.readerWithView(JsonViews.Write.class).forType(InspecieTransferDtoImpl.class)
                .readValue(JsonSanitizer.sanitize(inspecieTransferJsonValidateObject));

        DomainApiErrorDto warning = Mockito.mock(DomainApiErrorDto.class);
        Mockito.when(warning.getErrorType()).thenReturn(ErrorType.WARNING.toString());
        DomainApiErrorDto error = Mockito.mock(DomainApiErrorDto.class);
        Mockito.when(error.getErrorType()).thenReturn(ErrorType.ERROR.toString());
        ((InspecieTransferDtoImpl) dto).setWarnings(Arrays.asList(warning, error));

        ObjectReader reader = Mockito.mock(ObjectReader.class);
        Mockito.when(reader.forType(Mockito.any(Class.class))).thenReturn(reader);
        Mockito.when(reader.readValue(Mockito.anyString())).thenReturn(dto);

        Mockito.when(mockMapper.readerWithView(Mockito.any(Class.class))).thenReturn(reader);
        Mockito.when(
                uploadService.validateFile(Mockito.any(InspecieTransferDto.class), Mockito.anyString(), Mockito.anyString(),
                        Mockito.any(MultipartFile.class))).thenReturn(dto);

        uploadApiController.uploadTaxParcel("accountId", "json", "sponsorId", "sponsorName", null);
    }

    @Test
    public void testUploadIndependentTaxParcel_whenNoErrorsInFile_thenDtoReturned() throws JsonProcessingException, IOException {
        Mockito.when(userProfileService.isEmulating()).thenReturn(Boolean.FALSE);

        Mockito.when(
                independentUploadService.validateFile(
                        Mockito.any(com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferKey.class),
                        Mockito.any(MultipartFile.class))).thenAnswer(
                new Answer<com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto>() {

                    @Override
                    public com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto answer(InvocationOnMock invocation)
                            throws Throwable {
                        com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferKey key = (com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferKey) invocation
                                .getArguments()[0];
                        Assert.assertEquals("accountId", key.getAccountId());
                        Assert.assertEquals("transferId", key.getTransferId());

                        MultipartFile file = (MultipartFile) invocation.getArguments()[1];
                        Assert.assertNotNull(file);

                        com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto dto = Mockito
                                .mock(com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto.class);
                        Mockito.when(dto.containsValidationWarningOnly()).thenReturn(true);
                        return dto;
                    }
                });

        KeyedApiResponse<com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferKey> apiResponse = Mockito
                .mock(KeyedApiResponse.class);
        Mockito.when(apiResponse.getData()).thenReturn(
                Mockito.mock(com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto.class));

        Mockito.when(
                transferMassSettleService.validate(
                        Mockito.any(com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto.class),
                        Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto>() {

            @Override
                    public com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto answer(InvocationOnMock invocation)
                            throws Throwable {
                        com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto dto = (com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto) invocation
                                .getArguments()[0];
                Assert.assertNotNull(dto);
                return dto;
            }
        });

        uploadApiController.uploadIndependentTaxParcel("accountId", "transferId", Mockito.mock(MultipartFile.class));
    }

    @Test
    public void testUploadIndependentTaxParcel_whenErrorsInFile_thenDtoReturnedWithoutAvaloqCalled()
            throws JsonProcessingException, IOException {
        Mockito.when(userProfileService.isEmulating()).thenReturn(Boolean.FALSE);

        Mockito.when(
                independentUploadService.validateFile(
                        Mockito.any(com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferKey.class),
                        Mockito.any(MultipartFile.class))).thenAnswer(
                new Answer<com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto>() {

                    @Override
                    public com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto answer(InvocationOnMock invocation)
                            throws Throwable {
                        com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferKey key = (com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferKey) invocation
                                .getArguments()[0];
                        Assert.assertEquals("accountId", key.getAccountId());
                        Assert.assertEquals("transferId", key.getTransferId());

                        MultipartFile file = (MultipartFile) invocation.getArguments()[1];
                        Assert.assertNotNull(file);

                        com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto dto = Mockito
                                .mock(com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto.class);
                        Mockito.when(dto.containsValidationWarningOnly()).thenReturn(false);
                        return dto;
                    }
                });

        uploadApiController.uploadIndependentTaxParcel("accountId", "transferId", Mockito.mock(MultipartFile.class));
    }
}
