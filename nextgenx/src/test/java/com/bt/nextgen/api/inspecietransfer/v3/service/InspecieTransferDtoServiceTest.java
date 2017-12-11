package com.bt.nextgen.api.inspecietransfer.v3.service;

import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferDto;
import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferKey;
import com.bt.nextgen.api.inspecietransfer.v3.model.TransferAssetDto;
import com.bt.nextgen.api.inspecietransfer.v3.util.TransferAssetConverter;
import com.bt.nextgen.api.inspecietransfer.v3.validation.InspecieTransferDtoErrorMapper;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.transfer.transfergroup.TransferGroupDetailsImpl;
import com.bt.nextgen.service.integration.account.IncomePreference;
import com.bt.nextgen.service.integration.order.OrderType;
import com.bt.nextgen.service.integration.transaction.TransactionResponse;
import com.bt.nextgen.service.integration.transfer.TransferType;
import com.bt.nextgen.service.integration.transfer.transfergroup.TransferAsset;
import com.bt.nextgen.service.integration.transfer.transfergroup.TransferGroupIntegrationService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

@RunWith(MockitoJUnitRunner.class)
public class InspecieTransferDtoServiceTest {

    @InjectMocks
    private InspecieTransferDtoServiceImpl transferDtoService;

    @Mock
    private TransferGroupIntegrationService transferIntegrationService;

    @Mock
    private InspecieTransferDtoErrorMapper inspecieErrorMapper;

    @Mock
    private TransferAssetConverter transferAssetConverter;

    private ServiceErrors serviceErrors;

    @Before
    public void setup() {

        // Mocks for errors
        DomainApiErrorDto apiError = Mockito.mock(DomainApiErrorDto.class);
        Mockito.when(inspecieErrorMapper.map(Mockito.anyListOf(ValidationError.class))).thenReturn(Arrays.asList(apiError));

        ValidationError error = Mockito.mock(ValidationError.class);
        TransactionResponse transactionResponse = Mockito.mock(TransactionResponse.class);
        Mockito.when(transactionResponse.getValidationErrors()).thenReturn(Arrays.asList(error));

        // Mocks for TransferAssetConverter
        TransferAssetDto transferAssetDto = Mockito.mock(TransferAssetDto.class);
        Mockito.when(
                transferAssetConverter.toDtoList(Mockito.anyListOf(TransferAsset.class), Mockito.any(TransferType.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(Arrays.asList(transferAssetDto));

        serviceErrors = new FailFastErrorsImpl();
    }

    @Test
    public void testValidate_whenFullInspecieTransferDtoProvided_thenReturnedDtoHasRelevantFields() {
        InspecieTransferDto mockTransferDto = getMockInspecieTransferDto();
        TransferGroupDetailsImpl transferGroupDetails = transferDtoService.toTransferGroupDetails(mockTransferDto);

        Mockito.when(
                transferIntegrationService.validateTransfer(Mockito.any(TransferGroupDetailsImpl.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(transferGroupDetails);

        InspecieTransferDto dto = transferDtoService.validate(mockTransferDto, serviceErrors);

        Assert.assertNotNull(dto);
        Assert.assertEquals("accountId", decode(dto.getKey().getAccountId()));
        Assert.assertEquals("transferId", dto.getKey().getTransferId());
        Assert.assertEquals(OrderType.IN_SPECIE_TRANSFER.getDisplayName(), dto.getOrderType());
        Assert.assertEquals(TransferType.LS_BROKER_SPONSORED.getDisplayName(), dto.getTransferType());
        Assert.assertEquals(Boolean.TRUE, dto.getIsCBO());
        Assert.assertEquals(DateTime.parse("2016-07-01"), dto.getTransferDate());
        Assert.assertEquals("targetAccountId", decode(dto.getTargetAccountKey().getAccountId()));
        Assert.assertEquals("destContainerId", decode(dto.getTargetContainerId()));
        Assert.assertEquals(IncomePreference.REINVEST.getIntlId(), dto.getIncomePreference());
        Assert.assertEquals(1, dto.getTransferAssets().size());
        Assert.assertEquals(0, dto.getTransferPreferences().size());
        Assert.assertEquals(1, dto.getWarnings().size());

        Assert.assertNull(dto.getSourceAccountKey().getAccountId());
        Assert.assertNull(dto.getSourceContainerId());
        Assert.assertNull(dto.getTargetAssetId());
        Assert.assertFalse(dto.getIsFullClose());
    }

    @Test
    public void testSubmit_whenFullInspecieTransferDtoProvided_thenReturnedDtoHasRelevantFields() {
        InspecieTransferDto mockTransferDto = getMockInspecieTransferDto();
        TransferGroupDetailsImpl transferGroupDetails = transferDtoService.toTransferGroupDetails(mockTransferDto);

        Mockito.when(
                transferIntegrationService.submitTransfer(Mockito.any(TransferGroupDetailsImpl.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(transferGroupDetails);

        InspecieTransferDto dto = transferDtoService.submit(mockTransferDto, serviceErrors);

        Assert.assertNotNull(dto);
        Assert.assertEquals("accountId", decode(dto.getKey().getAccountId()));
        Assert.assertEquals("transferId", dto.getKey().getTransferId());
        Assert.assertEquals(OrderType.IN_SPECIE_TRANSFER.getDisplayName(), dto.getOrderType());
        Assert.assertEquals(TransferType.LS_BROKER_SPONSORED.getDisplayName(), dto.getTransferType());
        Assert.assertEquals(Boolean.TRUE, dto.getIsCBO());
        Assert.assertEquals(DateTime.parse("2016-07-01"), dto.getTransferDate());
        Assert.assertEquals("targetAccountId", decode(dto.getTargetAccountKey().getAccountId()));
        Assert.assertEquals("destContainerId", decode(dto.getTargetContainerId()));
        Assert.assertEquals(IncomePreference.REINVEST.getIntlId(), dto.getIncomePreference());
        Assert.assertEquals(1, dto.getTransferAssets().size());
        Assert.assertEquals(0, dto.getTransferPreferences().size());
        Assert.assertEquals(1, dto.getWarnings().size());

        Assert.assertNull(dto.getSourceAccountKey().getAccountId());
        Assert.assertNull(dto.getSourceContainerId());
        Assert.assertNull(dto.getTargetAssetId());
        Assert.assertFalse(dto.getIsFullClose());
    }

    @Test
    public void testValidate_whenFullIntraAccountTransferDtoProvided_thenReturnedDtoHasRelevantFields() {
        InspecieTransferDto mockTransferDto = getMockIntraAccountTransferDto();
        TransferGroupDetailsImpl transferGroupDetails = transferDtoService.toTransferGroupDetails(mockTransferDto);

        Mockito.when(
                transferIntegrationService.validateTransfer(Mockito.any(TransferGroupDetailsImpl.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(transferGroupDetails);

        InspecieTransferDto dto = transferDtoService.validate(mockTransferDto, serviceErrors);

        Assert.assertNotNull(dto);
        Assert.assertEquals("accountId", decode(dto.getKey().getAccountId()));
        Assert.assertEquals("transferId", dto.getKey().getTransferId());
        Assert.assertEquals(OrderType.INTRA_ACCOUNT_TRANSFER.getDisplayName(), dto.getOrderType());
        Assert.assertEquals(TransferType.LS_BROKER_SPONSORED.getDisplayName(), dto.getTransferType());
        Assert.assertEquals(Boolean.TRUE, dto.getIsCBO());
        Assert.assertEquals(Boolean.TRUE, dto.getIsFullClose());
        Assert.assertEquals(DateTime.parse("2016-07-01"), dto.getTransferDate());
        Assert.assertEquals("sourceAccountId", decode(dto.getSourceAccountKey().getAccountId()));
        Assert.assertEquals("targetAccountId", decode(dto.getTargetAccountKey().getAccountId()));
        Assert.assertEquals("destAssetId", dto.getTargetAssetId());
        Assert.assertEquals("sourceContainerId", decode(dto.getSourceContainerId()));
        Assert.assertEquals(IncomePreference.TRANSFER.getIntlId(), dto.getIncomePreference());
        Assert.assertEquals(1, dto.getTransferAssets().size());
        Assert.assertEquals(0, dto.getTransferPreferences().size());
        Assert.assertEquals(1, dto.getWarnings().size());

        Assert.assertNull(dto.getTargetContainerId());
    }

    @Test
    public void testSubmit_whenFullIntraAccountTransferDtoProvided_thenReturnedDtoHasRelevantFields() {
        InspecieTransferDto mockTransferDto = getMockIntraAccountTransferDto();
        TransferGroupDetailsImpl transferGroupDetails = transferDtoService.toTransferGroupDetails(mockTransferDto);

        Mockito.when(
                transferIntegrationService.submitTransfer(Mockito.any(TransferGroupDetailsImpl.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(transferGroupDetails);

        InspecieTransferDto dto = transferDtoService.submit(mockTransferDto, serviceErrors);

        Assert.assertNotNull(dto);
        Assert.assertEquals("accountId", decode(dto.getKey().getAccountId()));
        Assert.assertEquals("transferId", dto.getKey().getTransferId());
        Assert.assertEquals(OrderType.INTRA_ACCOUNT_TRANSFER.getDisplayName(), dto.getOrderType());
        Assert.assertEquals(TransferType.LS_BROKER_SPONSORED.getDisplayName(), dto.getTransferType());
        Assert.assertEquals(Boolean.TRUE, dto.getIsCBO());
        Assert.assertEquals(Boolean.TRUE, dto.getIsFullClose());
        Assert.assertEquals(DateTime.parse("2016-07-01"), dto.getTransferDate());
        Assert.assertEquals("sourceAccountId", decode(dto.getSourceAccountKey().getAccountId()));
        Assert.assertEquals("targetAccountId", decode(dto.getTargetAccountKey().getAccountId()));
        Assert.assertEquals("destAssetId", dto.getTargetAssetId());
        Assert.assertEquals("sourceContainerId", decode(dto.getSourceContainerId()));
        Assert.assertEquals(IncomePreference.TRANSFER.getIntlId(), dto.getIncomePreference());
        Assert.assertEquals(1, dto.getTransferAssets().size());
        Assert.assertEquals(0, dto.getTransferPreferences().size());
        Assert.assertEquals(1, dto.getWarnings().size());

        Assert.assertNull(dto.getTargetContainerId());
    }

    @Test
    public void testValidate_whenMinimalDtoProvided_thenReturnedDtoHasMissingFields() {
        InspecieTransferDto mockTransferDto = getMinimalMockTransferDto();
        TransferGroupDetailsImpl transferGroupDetails = transferDtoService.toTransferGroupDetails(mockTransferDto);

        Mockito.when(
                transferIntegrationService.validateTransfer(Mockito.any(TransferGroupDetailsImpl.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(transferGroupDetails);

        InspecieTransferDto dto = transferDtoService.validate(mockTransferDto, serviceErrors);

        Assert.assertNotNull(dto);
        Assert.assertNull(dto.getKey().getAccountId());
        Assert.assertNull(dto.getKey().getTransferId());
        Assert.assertFalse(dto.getIsFullClose());
        Assert.assertNull(dto.getOrderType());
        Assert.assertNull(dto.getTransferType());
        Assert.assertFalse(dto.getIsCBO());
        Assert.assertNull(dto.getTransferDate());
        Assert.assertNull(dto.getSourceAccountKey().getAccountId());
        Assert.assertNull(dto.getTargetAccountKey().getAccountId());
        Assert.assertNull(dto.getTargetContainerId());
        Assert.assertNull(dto.getTargetAssetId());
        Assert.assertNull(dto.getIncomePreference());
        Assert.assertEquals(1, dto.getTransferAssets().size());
        Assert.assertEquals(0, dto.getTransferPreferences().size());
        Assert.assertEquals(1, dto.getWarnings().size());
    }

    @Test
    public void testSubmit_whenMinimalDtoProvided_thenReturnedDtoHasMissingFields() {
        InspecieTransferDto mockTransferDto = getMinimalMockTransferDto();
        TransferGroupDetailsImpl transferGroupDetails = transferDtoService.toTransferGroupDetails(mockTransferDto);

        Mockito.when(
                transferIntegrationService.submitTransfer(Mockito.any(TransferGroupDetailsImpl.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(transferGroupDetails);

        InspecieTransferDto dto = transferDtoService.submit(mockTransferDto, serviceErrors);

        Assert.assertNotNull(dto);
        Assert.assertNull(dto.getKey().getAccountId());
        Assert.assertNull(dto.getKey().getTransferId());
        Assert.assertFalse(dto.getIsFullClose());
        Assert.assertNull(dto.getOrderType());
        Assert.assertNull(dto.getTransferType());
        Assert.assertFalse(dto.getIsCBO());
        Assert.assertNull(dto.getTransferDate());
        Assert.assertNull(dto.getSourceAccountKey().getAccountId());
        Assert.assertNull(dto.getTargetAccountKey().getAccountId());
        Assert.assertNull(dto.getTargetContainerId());
        Assert.assertNull(dto.getTargetAssetId());
        Assert.assertNull(dto.getIncomePreference());
        Assert.assertEquals(1, dto.getTransferAssets().size());
        Assert.assertEquals(0, dto.getTransferPreferences().size());
        Assert.assertEquals(1, dto.getWarnings().size());
    }

    private InspecieTransferDto getMockInspecieTransferDto() {
        InspecieTransferKey key = Mockito.mock(InspecieTransferKey.class);
        Mockito.when(key.getAccountId()).thenReturn(encode("accountId"));
        Mockito.when(key.getTransferId()).thenReturn("transferId");

        InspecieTransferDto mockDto = Mockito.mock(InspecieTransferDto.class);
        Mockito.when(mockDto.getKey()).thenReturn(key);
        Mockito.when(mockDto.getOrderType()).thenReturn(OrderType.IN_SPECIE_TRANSFER.getDisplayName());
        Mockito.when(mockDto.getTransferType()).thenReturn(TransferType.LS_BROKER_SPONSORED.getDisplayName());
        Mockito.when(mockDto.getIsCBO()).thenReturn(Boolean.TRUE);
        Mockito.when(mockDto.getIsFullClose()).thenReturn(Boolean.TRUE);
        Mockito.when(mockDto.getTransferDate()).thenReturn(new DateTime("2016-07-01"));
        Mockito.when(mockDto.getSourceAccountKey()).thenReturn(
                new com.bt.nextgen.api.account.v3.model.AccountKey(encode("sourceAccountId")));
        Mockito.when(mockDto.getTargetAccountKey()).thenReturn(
                new com.bt.nextgen.api.account.v3.model.AccountKey(encode("targetAccountId")));

        Mockito.when(mockDto.getTargetContainerId()).thenReturn(encode("destContainerId"));
        Mockito.when(mockDto.getSourceContainerId()).thenReturn(encode("sourceContainerId"));
        Mockito.when(mockDto.getTargetAssetId()).thenReturn("destAssetId");
        Mockito.when(mockDto.getIncomePreference()).thenReturn("reinvest");

        return mockDto;
    }

    private InspecieTransferDto getMockIntraAccountTransferDto() {
        InspecieTransferKey key = Mockito.mock(InspecieTransferKey.class);
        Mockito.when(key.getAccountId()).thenReturn(encode("accountId"));
        Mockito.when(key.getTransferId()).thenReturn("transferId");

        InspecieTransferDto mockDto = Mockito.mock(InspecieTransferDto.class);
        Mockito.when(mockDto.getKey()).thenReturn(key);
        Mockito.when(mockDto.getOrderType()).thenReturn(OrderType.INTRA_ACCOUNT_TRANSFER.getDisplayName());
        Mockito.when(mockDto.getTransferType()).thenReturn(TransferType.LS_BROKER_SPONSORED.getDisplayName());
        Mockito.when(mockDto.getIsCBO()).thenReturn(Boolean.TRUE);
        Mockito.when(mockDto.getIsFullClose()).thenReturn(Boolean.TRUE);
        Mockito.when(mockDto.getTransferDate()).thenReturn(new DateTime("2016-07-01"));
        Mockito.when(mockDto.getSourceAccountKey()).thenReturn(
                new com.bt.nextgen.api.account.v3.model.AccountKey(encode("sourceAccountId")));
        Mockito.when(mockDto.getTargetAccountKey()).thenReturn(
                new com.bt.nextgen.api.account.v3.model.AccountKey(encode("targetAccountId")));

        Mockito.when(mockDto.getSourceContainerId()).thenReturn(encode("sourceContainerId"));
        Mockito.when(mockDto.getTargetAssetId()).thenReturn("destAssetId");
        Mockito.when(mockDto.getIncomePreference()).thenReturn("transfer");

        return mockDto;
    }

    private InspecieTransferDto getMinimalMockTransferDto() {
        InspecieTransferKey key = Mockito.mock(InspecieTransferKey.class);

        InspecieTransferDto mockDto = Mockito.mock(InspecieTransferDto.class);
        Mockito.when(mockDto.getKey()).thenReturn(key);

        return mockDto;
    }

    private String encode(String val) {
        return EncodedString.fromPlainText(val).toString();
    }

    private String decode(String val) {
        return EncodedString.toPlainText(val).toString();
    }
}