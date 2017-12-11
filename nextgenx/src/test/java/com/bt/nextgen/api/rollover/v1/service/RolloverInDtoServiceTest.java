package com.bt.nextgen.api.rollover.v1.service;

import com.bt.nextgen.api.rollover.v1.model.RolloverDetailsDto;
import com.bt.nextgen.api.rollover.v1.model.RolloverInDto;
import com.bt.nextgen.api.rollover.v1.model.RolloverKey;
import com.bt.nextgen.api.rollover.v1.validation.RolloverDtoErrorMapperImpl;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto.ErrorType;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.rollover.RolloverDetailsImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.rollover.CashRolloverService;
import com.bt.nextgen.service.integration.rollover.RolloverDetails;
import com.bt.nextgen.service.integration.rollover.RolloverOption;
import com.bt.nextgen.service.integration.rollover.RolloverType;
import com.btfin.panorama.core.security.encryption.EncodedString;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

@RunWith(MockitoJUnitRunner.class)
public class RolloverInDtoServiceTest {

    @InjectMocks
    private RolloverInDtoServiceImpl rolloverDtoService;

    @Mock
    private CashRolloverService cashRolloverService;

    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private RolloverDtoErrorMapperImpl errorMapper;

    private RolloverInDto inputDto;

    @Before
    public void setup() {
        RolloverDetailsDto detailDto = Mockito.mock(RolloverDetailsDto.class);
        Mockito.when(detailDto.getAccountId()).thenReturn(EncodedString.fromPlainText("accountId").toString());
        Mockito.when(detailDto.getFundId()).thenReturn("fundId");
        Mockito.when(detailDto.getFundName()).thenReturn("fundName");
        Mockito.when(detailDto.getFundAbn()).thenReturn("fundAbn");
        Mockito.when(detailDto.getFundUsi()).thenReturn("fundUsi");
        Mockito.when(detailDto.getRolloverType()).thenReturn(RolloverType.CASH_ROLLOVER.getDisplayName());
        Mockito.when(detailDto.getFundAmount()).thenReturn(BigDecimal.TEN);
        Mockito.when(detailDto.getPanInitiated()).thenReturn(Boolean.TRUE);
        Mockito.when(detailDto.getAccountName()).thenReturn("accountNumber");
        Mockito.when(detailDto.getRolloverOption()).thenReturn(RolloverOption.FULL.getDisplayName());
        Mockito.when(detailDto.getIncludeInsurance()).thenReturn(Boolean.FALSE);
        Mockito.when(detailDto.getLastTransSeqId()).thenReturn("1");

        inputDto = Mockito.mock(RolloverInDto.class);
        Mockito.when(inputDto.getKey()).thenReturn(
                new RolloverKey(EncodedString.fromPlainText("accountId").toString(), "rolloverId"));
        Mockito.when(inputDto.getRolloverType()).thenReturn(RolloverType.CASH_ROLLOVER.getDisplayName());
        Mockito.when(inputDto.getRolloverDetails()).thenReturn(Arrays.asList(detailDto, detailDto));
    }

    @Test
    public void testConvertToModel() {
        RolloverDetails detail = rolloverDtoService.convertToRolloverDetails(inputDto.getKey().getAccountId(), inputDto.getKey()
                .getRolloverId(), RolloverType.CASH_ROLLOVER, "1", inputDto.getRolloverDetails().get(0));

        Assert.assertEquals("accountId", detail.getAccountKey().getId());
        Assert.assertEquals("rolloverId", detail.getRolloverId());
        Assert.assertEquals("fundId", detail.getFundId());
        Assert.assertEquals("fundName", detail.getFundName());
        Assert.assertEquals("fundAbn", detail.getFundAbn());
        Assert.assertEquals("fundUsi", detail.getFundUsi());
        Assert.assertEquals(BigDecimal.TEN, detail.getAmount());
        Assert.assertEquals(Boolean.TRUE, detail.getPanInitiated());
        Assert.assertEquals("accountNumber", detail.getAccountNumber());
        Assert.assertEquals(RolloverOption.FULL, detail.getRolloverOption());
        Assert.assertEquals(RolloverType.CASH_ROLLOVER, detail.getRolloverType());
        Assert.assertEquals(Boolean.FALSE, detail.getIncludeInsurance());
        Assert.assertEquals("1", detail.getLastTransSeqId());
    }

    @Test
    public void testConvertToModel_forEmptyRolloverAmount() {
        RolloverDetailsDto detailDto = Mockito.mock(RolloverDetailsDto.class);
        Mockito.when(detailDto.getAccountId()).thenReturn(EncodedString.fromPlainText("accountId").toString());
        Mockito.when(detailDto.getFundId()).thenReturn("fundId");
        Mockito.when(detailDto.getFundName()).thenReturn("fundName");
        Mockito.when(detailDto.getFundAbn()).thenReturn("fundAbn");
        Mockito.when(detailDto.getFundUsi()).thenReturn("fundUsi");
        Mockito.when(detailDto.getRolloverType()).thenReturn(RolloverType.CASH_ROLLOVER.getDisplayName());
        Mockito.when(detailDto.getFundAmount()).thenReturn(null);
        Mockito.when(detailDto.getPanInitiated()).thenReturn(Boolean.TRUE);
        Mockito.when(detailDto.getAccountName()).thenReturn("accountNumber");
        Mockito.when(detailDto.getRolloverOption()).thenReturn(RolloverOption.FULL.getDisplayName());
        Mockito.when(detailDto.getIncludeInsurance()).thenReturn(Boolean.FALSE);
        Mockito.when(detailDto.getLastTransSeqId()).thenReturn("1");

        RolloverDetails detail = rolloverDtoService.convertToRolloverDetails(inputDto.getKey().getAccountId(), inputDto.getKey()
                .getRolloverId(), RolloverType.CASH_ROLLOVER, "1", detailDto);

        Assert.assertEquals("accountId", detail.getAccountKey().getId());
        Assert.assertEquals("rolloverId", detail.getRolloverId());
        Assert.assertEquals("fundId", detail.getFundId());
        Assert.assertEquals("fundName", detail.getFundName());
        Assert.assertEquals("fundAbn", detail.getFundAbn());
        Assert.assertEquals("fundUsi", detail.getFundUsi());
        Assert.assertEquals(null, detail.getAmount());
        Assert.assertEquals(Boolean.TRUE, detail.getPanInitiated());
        Assert.assertEquals("accountNumber", detail.getAccountNumber());
        Assert.assertEquals(RolloverOption.FULL, detail.getRolloverOption());
        Assert.assertEquals(RolloverType.CASH_ROLLOVER, detail.getRolloverType());
        Assert.assertEquals(Boolean.FALSE, detail.getIncludeInsurance());
        Assert.assertEquals("1", detail.getLastTransSeqId());
    }

    @Test
    public void testConvertToModel_emptyFundId() {
        RolloverDetailsDto detailDto = Mockito.mock(RolloverDetailsDto.class);
        Mockito.when(detailDto.getAccountId()).thenReturn(EncodedString.fromPlainText("accountId").toString());
        Mockito.when(detailDto.getFundId()).thenReturn("");
        Mockito.when(detailDto.getFundName()).thenReturn("fundName");
        Mockito.when(detailDto.getFundAbn()).thenReturn("fundAbn");
        Mockito.when(detailDto.getFundUsi()).thenReturn("fundUsi");
        Mockito.when(detailDto.getRolloverType()).thenReturn(RolloverType.CASH_ROLLOVER.getDisplayName());
        Mockito.when(detailDto.getFundAmount()).thenReturn(null);
        Mockito.when(detailDto.getPanInitiated()).thenReturn(Boolean.TRUE);
        Mockito.when(detailDto.getAccountName()).thenReturn("accountNumber");
        Mockito.when(detailDto.getRolloverOption()).thenReturn(RolloverOption.FULL.getDisplayName());
        Mockito.when(detailDto.getIncludeInsurance()).thenReturn(Boolean.FALSE);
        Mockito.when(detailDto.getLastTransSeqId()).thenReturn("1");

        RolloverDetails detail = rolloverDtoService.convertToRolloverDetails(inputDto.getKey().getAccountId(), inputDto.getKey()
                .getRolloverId(), RolloverType.CASH_ROLLOVER, "1", detailDto);

        Assert.assertNull(detail.getFundId());
    }

    @Test
    public void testSubmitRollover() {

        RolloverDetails detail = setRolloverDetails();
        Mockito.when(cashRolloverService.loadRolloverDetails(Mockito.anyString(), Mockito.any(ServiceErrors.class))).thenReturn(
                detail);
        Mockito.when(
                cashRolloverService.submitRolloverInDetails(Mockito.any(RolloverDetails.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(detail);

        Mockito.when(errorMapper.map(Mockito.anyList())).thenReturn(null);
        RolloverInDto dto = rolloverDtoService.submit(inputDto, new FailFastErrorsImpl());

        Assert.assertNotNull(dto);
        Assert.assertEquals("accountId", EncodedString.toPlainText(dto.getKey().getAccountId()));
        Assert.assertEquals("rolloverId", dto.getKey().getRolloverId());
        Assert.assertEquals(RolloverType.CASH_ROLLOVER.getDisplayName(), dto.getRolloverType());

        Assert.assertEquals(2, dto.getRolloverDetails().size());
        RolloverDetailsDto responseDetail = dto.getRolloverDetails().get(0);

        Assert.assertEquals("accountId", EncodedString.toPlainText(responseDetail.getAccountId()));
        Assert.assertEquals("fundId", responseDetail.getFundId());
        Assert.assertEquals("fundName", responseDetail.getFundName());
        Assert.assertEquals("fundAbn", responseDetail.getFundAbn());
        Assert.assertEquals("fundUsi", responseDetail.getFundUsi());
        Assert.assertEquals(BigDecimal.TEN, responseDetail.getFundAmount());
        Assert.assertEquals(BigDecimal.TEN, responseDetail.getAmount());
        Assert.assertEquals(Boolean.TRUE, responseDetail.getPanInitiated());
        Assert.assertEquals("accountNumber", responseDetail.getAccountName());
        Assert.assertEquals(RolloverOption.FULL.getDisplayName(), responseDetail.getRolloverOption());
        Assert.assertEquals(RolloverType.CASH_ROLLOVER.getDisplayName(), responseDetail.getRolloverType());
        Assert.assertEquals(Boolean.FALSE, responseDetail.getIncludeInsurance());
        Assert.assertEquals("1", responseDetail.getLastTransSeqId());
    }

    @Test
    public void testSubmitRollover_withErrors() {

        RolloverDetails detail = setRolloverDetails();
        Mockito.when(cashRolloverService.loadRolloverDetails(Mockito.anyString(), Mockito.any(ServiceErrors.class))).thenReturn(
                detail);
        Mockito.when(
                cashRolloverService.submitRolloverInDetails(Mockito.any(RolloverDetails.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(detail);

        DomainApiErrorDto errorDto = new DomainApiErrorDto(null, "errorReason", "errorMessage", ErrorType.ERROR);
        Mockito.when(errorMapper.map(Mockito.anyList())).thenReturn(Collections.singletonList(errorDto));

        Mockito.when(inputDto.getRolloverDetails().get(0).getWarnings()).thenReturn(Collections.singletonList(errorDto));
        Mockito.when(inputDto.getRolloverDetails().get(1).getWarnings()).thenReturn(Collections.singletonList(errorDto));
        RolloverInDto dto = rolloverDtoService.submit(inputDto, new FailFastErrorsImpl());

        // Validate that the domain in errorDto has been populated with the fund's name, ABN and USI details.
        Assert.assertNotNull(dto);
        Assert.assertEquals(2, dto.getWarnings().size());
        Assert.assertEquals("fundName ABN: fundAbn USI: fundUsi", dto.getWarnings().get(0).getDomain());
        Assert.assertEquals("fundName ABN: fundAbn USI: fundUsi", dto.getWarnings().get(1).getDomain());
    }

    @Test
    public void testSubmitRollover_withNullOrderDetailsInstance() {
        RolloverDetailsDto detailDto = Mockito.mock(RolloverDetailsDto.class);
        Mockito.when(inputDto.getRolloverDetails()).thenReturn(Arrays.asList(detailDto));
        
        RolloverDetails detail = setRolloverDetails();
        Mockito.when(cashRolloverService.loadRolloverDetails(Mockito.anyString(), Mockito.any(ServiceErrors.class))).thenReturn(
                detail);        
        Mockito.when(
                cashRolloverService.submitRolloverInDetails(Mockito.any(RolloverDetails.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(detail);
        // Null RolloverOption -> Null DTO.
        RolloverInDto dto = rolloverDtoService.submit(inputDto, new FailFastErrorsImpl());
        Assert.assertNotNull(dto);
        Assert.assertTrue(dto.getRolloverDetails().size() == 0);

        // Null fundAmount. Dto should NOT be null
        Mockito.when(detailDto.getFundAmount()).thenReturn(null);
        Mockito.when(detailDto.getRolloverOption()).thenReturn("rolloverOption");
        detail = setRolloverDetails();
        dto = rolloverDtoService.submit(inputDto, new FailFastErrorsImpl());
        Assert.assertNotNull(dto);
        Assert.assertTrue(dto.getRolloverDetails().size() == 1);

        // Null rolloverOption. Null DTO.
        Mockito.when(detailDto.getFundAmount()).thenReturn(BigDecimal.ONE);
        Mockito.when(detailDto.getRolloverOption()).thenReturn(null);
        detail = setRolloverDetails();
        dto = rolloverDtoService.submit(inputDto, new FailFastErrorsImpl());
        Assert.assertNotNull(dto);
        Assert.assertTrue(dto.getRolloverDetails().size() == 0);
    }

    @Test
    public void testSaveRollover() {

        RolloverDetails detail = setRolloverDetails();
        Mockito.when(cashRolloverService.loadRolloverDetails(Mockito.anyString(), Mockito.any(ServiceErrors.class))).thenReturn(
                detail);
        Mockito.when(
                cashRolloverService.saveRolloverDetails(Mockito.any(RolloverDetails.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(detail);

        RolloverInDto dto = rolloverDtoService.save(inputDto, new FailFastErrorsImpl());

        Assert.assertNotNull(dto);
        Assert.assertEquals("accountId", EncodedString.toPlainText(dto.getKey().getAccountId()));
        Assert.assertEquals("rolloverId", dto.getKey().getRolloverId());
        Assert.assertEquals(RolloverType.CASH_ROLLOVER.getDisplayName(), dto.getRolloverType());

        Assert.assertEquals(2, dto.getRolloverDetails().size());
        RolloverDetailsDto responseDetail = dto.getRolloverDetails().get(0);

        Assert.assertEquals("accountId", EncodedString.toPlainText(responseDetail.getAccountId()));
        Assert.assertEquals("fundId", responseDetail.getFundId());
        Assert.assertEquals("fundName", responseDetail.getFundName());
        Assert.assertEquals("fundAbn", responseDetail.getFundAbn());
        Assert.assertEquals("fundUsi", responseDetail.getFundUsi());
        Assert.assertEquals(BigDecimal.TEN, responseDetail.getFundAmount());
        Assert.assertEquals(BigDecimal.TEN, responseDetail.getAmount());
        Assert.assertEquals(Boolean.TRUE, responseDetail.getPanInitiated());
        Assert.assertEquals("accountNumber", responseDetail.getAccountName());
        Assert.assertEquals(RolloverOption.FULL.getDisplayName(), responseDetail.getRolloverOption());
        Assert.assertEquals(RolloverType.CASH_ROLLOVER.getDisplayName(), responseDetail.getRolloverType());
        Assert.assertEquals(Boolean.FALSE, responseDetail.getIncludeInsurance());
        Assert.assertEquals("1", responseDetail.getLastTransSeqId());
    }

    @Test
    public void testLoadRollover() {
        RolloverDetails detail = setRolloverDetails();
        Mockito.when(cashRolloverService.loadRolloverDetails(Mockito.anyString(), Mockito.any(ServiceErrors.class))).thenReturn(
                detail);

        RolloverKey key = new RolloverKey(EncodedString.fromPlainText("accountId").toString(), "rolloverId");
        RolloverInDto dto = rolloverDtoService.find(key, new FailFastErrorsImpl());

        Assert.assertNotNull(dto);
        Assert.assertEquals("accountId", EncodedString.toPlainText(dto.getKey().getAccountId()));
        Assert.assertEquals("rolloverId", dto.getKey().getRolloverId());
        Assert.assertEquals(RolloverType.CASH_ROLLOVER.getDisplayName(), dto.getRolloverType());

        Assert.assertEquals(1, dto.getRolloverDetails().size());
        RolloverDetailsDto responseDetail = dto.getRolloverDetails().get(0);

        Assert.assertEquals("accountId", EncodedString.toPlainText(responseDetail.getAccountId()));
        Assert.assertEquals("fundId", responseDetail.getFundId());
        Assert.assertEquals("fundName", responseDetail.getFundName());
        Assert.assertEquals("fundAbn", responseDetail.getFundAbn());
        Assert.assertEquals("fundUsi", responseDetail.getFundUsi());
        Assert.assertEquals(BigDecimal.TEN, responseDetail.getFundAmount());
        Assert.assertEquals(BigDecimal.TEN, responseDetail.getAmount());
        Assert.assertEquals(Boolean.TRUE, responseDetail.getPanInitiated());
        Assert.assertEquals("accountNumber", responseDetail.getAccountName());
        Assert.assertEquals(RolloverOption.FULL.getDisplayName(), responseDetail.getRolloverOption());
        Assert.assertEquals(RolloverType.CASH_ROLLOVER.getDisplayName(), responseDetail.getRolloverType());
        Assert.assertEquals(Boolean.FALSE, responseDetail.getIncludeInsurance());
    }

    @Test
    public void testDiscardRollover() {
        RolloverDetails detail = setRolloverDetails();
        Mockito.when(cashRolloverService.discardRolloverDetails(Mockito.anyString(), Mockito.any(ServiceErrors.class)))
                .thenReturn(detail);

        RolloverKey key = new RolloverKey(EncodedString.fromPlainText("accountId").toString(), "rolloverId");
        RolloverInDto dto = rolloverDtoService.discard(key, new FailFastErrorsImpl());

        Assert.assertNotNull(dto);
        Assert.assertEquals("accountId", EncodedString.toPlainText(dto.getKey().getAccountId()));
        Assert.assertEquals("rolloverId", dto.getKey().getRolloverId());
        Assert.assertEquals("Rollover Discarded", dto.getRolloverType());

        Assert.assertEquals(1, dto.getRolloverDetails().size());
        RolloverDetailsDto responseDetail = dto.getRolloverDetails().get(0);

        Assert.assertEquals("accountId", EncodedString.toPlainText(responseDetail.getAccountId()));
        Assert.assertEquals("fundId", responseDetail.getFundId());
        Assert.assertEquals("fundName", responseDetail.getFundName());
        Assert.assertEquals("fundAbn", responseDetail.getFundAbn());
        Assert.assertEquals("fundUsi", responseDetail.getFundUsi());
        Assert.assertEquals(BigDecimal.TEN, responseDetail.getFundAmount());
        Assert.assertEquals(BigDecimal.TEN, responseDetail.getAmount());
        Assert.assertEquals(Boolean.TRUE, responseDetail.getPanInitiated());
        Assert.assertEquals("accountNumber", responseDetail.getAccountName());
        Assert.assertEquals(RolloverOption.FULL.getDisplayName(), responseDetail.getRolloverOption());
        Assert.assertEquals(RolloverType.CASH_ROLLOVER.getDisplayName(), responseDetail.getRolloverType());
        Assert.assertEquals(Boolean.FALSE, responseDetail.getIncludeInsurance());
    }

    private RolloverDetails setRolloverDetails() {
        RolloverDetailsImpl detail = new RolloverDetailsImpl();
        detail.setAccountKey(AccountKey.valueOf("accountId"));
        detail.setRolloverId("rolloverId");
        detail.setFundId("fundId");
        detail.setFundName("fundName");
        detail.setFundAbn("fundAbn");
        detail.setFundUsi("fundUsi");
        detail.setAmount(BigDecimal.TEN);
        detail.setPanInitiated(Boolean.TRUE);
        detail.setRequestDate(new DateTime("2016-01-01"));
        detail.setAccountNumber("accountNumber");
        detail.setRolloverOption(RolloverOption.FULL);
        detail.setRolloverType(RolloverType.CASH_ROLLOVER);
        detail.setIncludeInsurance(Boolean.FALSE);
        detail.setLastTransSeqId("1");
        return detail;
    }
}
