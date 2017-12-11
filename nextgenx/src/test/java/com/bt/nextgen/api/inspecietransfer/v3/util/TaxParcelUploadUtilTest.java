package com.bt.nextgen.api.inspecietransfer.v3.util;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferDto;
import com.bt.nextgen.api.inspecietransfer.v3.model.TransferPreferenceDto;
import com.bt.nextgen.api.inspecietransfer.v3.util.vetting.TaxParcelRow;
import com.bt.nextgen.api.inspecietransfer.v3.util.vetting.TaxParcelUploadFile;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto.ErrorType;
import com.bt.nextgen.service.integration.account.IncomePreference;
import com.bt.nextgen.service.integration.order.OrderType;
import com.bt.nextgen.service.integration.transfer.TransferType;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

@RunWith(MockitoJUnitRunner.class)
public class TaxParcelUploadUtilTest {

    @InjectMocks
    private TaxParcelUploadUtil util;

    @Mock
    private CmsService cmsService;

    @Before
    public void setup() {
        Mockito.when(cmsService.getContent(Mockito.anyString())).thenReturn("error");
        Mockito.when(cmsService.getDynamicContent(Mockito.anyString(), Mockito.any(String[].class))).thenReturn("dynamicError");
    }

    @Test
    public void testGetError() {
        
        String[] params = { "params", };

        DomainApiErrorDto error = util.getError("code");        
        Assert.assertEquals("code", error.getErrorId());
        Assert.assertEquals("error", error.getMessage());
        Assert.assertEquals(ErrorType.ERROR.toString(), error.getErrorType());
        
        error = util.getError("code", params);
        Assert.assertEquals("code", error.getErrorId());
        Assert.assertEquals("dynamicError", error.getMessage());
        Assert.assertEquals(ErrorType.ERROR.toString(), error.getErrorType());

        error = util.getWarning("code");
        Assert.assertEquals("code", error.getErrorId());
        Assert.assertEquals("error", error.getMessage());
        Assert.assertEquals(ErrorType.WARNING.toString(), error.getErrorType());
        
        error = util.getWarning("code", params);
        Assert.assertEquals("code", error.getErrorId());
        Assert.assertEquals("dynamicError", error.getMessage());
        Assert.assertEquals(ErrorType.WARNING.toString(), error.getErrorType());
    }

    @Test
    public void testGetDtoForTransfer() {
        
        AccountKey targetAccountKey = Mockito.mock(AccountKey.class);
        Mockito.when(targetAccountKey.getAccountId()).thenReturn("targetId");

        AccountKey sourceAccountKey = Mockito.mock(AccountKey.class);
        Mockito.when(sourceAccountKey.getAccountId()).thenReturn("sourceId");

        InspecieTransferDto transferDto = Mockito.mock(InspecieTransferDto.class);
        Mockito.when(transferDto.getIsCBO()).thenReturn(Boolean.TRUE);
        Mockito.when(transferDto.getTargetAccountKey()).thenReturn(targetAccountKey);
        Mockito.when(transferDto.getSourceAccountKey()).thenReturn(sourceAccountKey);
        Mockito.when(transferDto.getTargetAssetId()).thenReturn("assetId");
        Mockito.when(transferDto.getTargetContainerId()).thenReturn("containerId");
        Mockito.when(transferDto.getTransferDate()).thenReturn(new DateTime("2016-01-01"));
        Mockito.when(transferDto.getIncomePreference()).thenReturn("transfer");
        Mockito.when(transferDto.getTransferPreferences()).thenReturn(new ArrayList<TransferPreferenceDto>());
        Mockito.when(transferDto.getWarnings()).thenReturn(new ArrayList<DomainApiErrorDto>());

        TaxParcelUploadFile parsedFile = Mockito.mock(TaxParcelUploadFile.class);
        Mockito.when(parsedFile.getRows()).thenReturn(new ArrayList<TaxParcelRow>());

        InspecieTransferDto result = util.getDtoForTransfer(transferDto, parsedFile, TransferType.LS_BROKER_SPONSORED, "pid");

        Assert.assertEquals("targetId", result.getKey().getAccountId());
        Assert.assertEquals(OrderType.IN_SPECIE_TRANSFER.getDisplayName(), result.getOrderType());
        Assert.assertEquals("sourceId", result.getSourceAccountKey().getAccountId());
        Assert.assertEquals("targetId", result.getTargetAccountKey().getAccountId());
        Assert.assertEquals(TransferType.LS_BROKER_SPONSORED.getDisplayName(), result.getTransferType());
        Assert.assertEquals(0, result.getTransferAssets().size());
        Assert.assertEquals(IncomePreference.TRANSFER.getIntlId(), result.getIncomePreference());
    }
}
