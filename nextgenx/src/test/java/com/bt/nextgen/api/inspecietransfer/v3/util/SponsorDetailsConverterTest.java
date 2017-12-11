package com.bt.nextgen.api.inspecietransfer.v3.util;

import com.bt.nextgen.api.inspecietransfer.v3.model.SponsorDetailsDto;
import com.bt.nextgen.service.avaloq.transfer.transfergroup.SponsorDetailsImpl;
import com.bt.nextgen.service.integration.transfer.SponsorDetails;
import com.bt.nextgen.service.integration.transfer.TransferType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SponsorDetailsConverterTest {

    @Test
    public void testFromDto() {

        SponsorDetailsDto mockDto = Mockito.mock(SponsorDetailsDto.class);
        Mockito.when(mockDto.getPid()).thenReturn("pid");
        Mockito.when(mockDto.getPidName()).thenReturn("pidName");
        Mockito.when(mockDto.getCustodian()).thenReturn("custodian");
        Mockito.when(mockDto.getHin()).thenReturn("hin");
        Mockito.when(mockDto.getSrn()).thenReturn("srn");
        Mockito.when(mockDto.getAccNumber()).thenReturn("accountId");
        Mockito.when(mockDto.getSourceContainerId()).thenReturn("sourceContainerId");

        SponsorDetails model = SponsorDetailsConverter.fromDto(mockDto, TransferType.LS_BROKER_SPONSORED.getDisplayName());
        Assert.assertEquals("pid", model.getSponsorId());
        Assert.assertEquals("pidName", model.getSponsorName());
        Assert.assertEquals("hin", model.getInvestmentId());

        model = SponsorDetailsConverter.fromDto(mockDto, TransferType.LS_OTHER.getDisplayName());
        Assert.assertEquals("pid", model.getSponsorId());
        Assert.assertEquals("pidName", model.getSponsorName());
        Assert.assertEquals("hin", model.getInvestmentId());
        Assert.assertEquals("custodian", model.getPlatformId());

        model = SponsorDetailsConverter.fromDto(mockDto, TransferType.MANAGED_FUND.getDisplayName());
        Assert.assertEquals("custodian", model.getPlatformId());
        Assert.assertEquals("accountId", model.getInvestmentId());

        model = SponsorDetailsConverter.fromDto(mockDto, TransferType.LS_ISSUER_SPONSORED.getDisplayName());
        Assert.assertEquals("srn", model.getInvestmentId());

        model = SponsorDetailsConverter.fromDto(mockDto, TransferType.OTHER_PLATFORM.getDisplayName());
        Assert.assertEquals("pid", model.getSponsorId());
        Assert.assertEquals("pidName", model.getSponsorName());
        Assert.assertEquals("hin", model.getInvestmentId());
        Assert.assertEquals("custodian", model.getPlatformId());

        Mockito.when(mockDto.getHin()).thenReturn(null);

        model = SponsorDetailsConverter.fromDto(mockDto, TransferType.OTHER_PLATFORM.getDisplayName());
        Assert.assertEquals("accountId", model.getInvestmentId());
        Assert.assertEquals("custodian", model.getPlatformId());
    }

    @Test
    public void testToDto() {

        SponsorDetails mockModel = Mockito.mock(SponsorDetailsImpl.class);
        Mockito.when(mockModel.getSponsorId()).thenReturn("pid");
        Mockito.when(mockModel.getSponsorName()).thenReturn("pidName");
        Mockito.when(mockModel.getPlatformId()).thenReturn("custodian");
        Mockito.when(mockModel.getInvestmentId()).thenReturn("hin");
        Mockito.when(mockModel.getRegistrationDetails()).thenReturn("srn");
        Mockito.when(mockModel.getSourceContainerId()).thenReturn("sourceContainerId");

        SponsorDetailsDto dto = SponsorDetailsConverter.toDto(mockModel, TransferType.LS_BROKER_SPONSORED);
        Assert.assertEquals("pid", dto.getPid());
        Assert.assertEquals("pidName", dto.getPidName());
        Assert.assertEquals("hin", dto.getHin());

        dto = SponsorDetailsConverter.toDto(mockModel, TransferType.LS_OTHER);
        Assert.assertEquals("pid", dto.getPid());
        Assert.assertEquals("pidName", dto.getPidName());
        Assert.assertEquals("hin", dto.getHin());
        Assert.assertEquals("custodian", dto.getCustodian());

        dto = SponsorDetailsConverter.toDto(mockModel, TransferType.LS_ISSUER_SPONSORED);
        Assert.assertEquals("hin", dto.getSrn());

        dto = SponsorDetailsConverter.toDto(mockModel, TransferType.MANAGED_FUND);
        Assert.assertEquals("hin", dto.getAccNumber());
        Assert.assertEquals("custodian", dto.getCustodian());

        dto = SponsorDetailsConverter.toDto(mockModel, TransferType.OTHER_PLATFORM);
        Assert.assertEquals("pid", dto.getPid());
        Assert.assertEquals("pidName", dto.getPidName());
        Assert.assertEquals("hin", dto.getHin());
        Assert.assertEquals("custodian", dto.getCustodian());

        Mockito.when(mockModel.getSponsorId()).thenReturn(null);

        dto = SponsorDetailsConverter.toDto(mockModel, TransferType.OTHER_PLATFORM);
        Assert.assertEquals("hin", dto.getAccNumber());
        Assert.assertEquals("custodian", dto.getCustodian());
    }
}
