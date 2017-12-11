package com.bt.nextgen.api.inspecietransfer.v3.util;

import com.bt.nextgen.api.inspecietransfer.v3.model.TransferPreferenceDto;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.modelpreferences.Preference;
import com.bt.nextgen.service.integration.order.ModelPreferenceAction;
import com.bt.nextgen.service.integration.order.PreferenceAction;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ModelPreferenceConverterTest {

    @Test
    public void testFromDtoList() {
        TransferPreferenceDto mockDto = Mockito.mock(TransferPreferenceDto.class);
        Mockito.when(mockDto.getIssuerKey()).thenReturn(new com.bt.nextgen.api.account.v3.model.AccountKey("issuerId"));
        Mockito.when(mockDto.getPreference()).thenReturn("CASH");
        Mockito.when(mockDto.getAction()).thenReturn("SET");

        TransferPreferenceDto mockDto2 = Mockito.mock(TransferPreferenceDto.class);
        Mockito.when(mockDto2.getIssuerKey()).thenReturn(new com.bt.nextgen.api.account.v3.model.AccountKey("issuerId"));
        Mockito.when(mockDto2.getPreference()).thenReturn("PRORATA");
        Mockito.when(mockDto2.getAction()).thenReturn("SET");

        List<ModelPreferenceAction> models = ModelPreferenceConverter.fromDtoList(Arrays.asList(mockDto, mockDto2));

        Assert.assertEquals(2, models.size());

        ModelPreferenceAction model = models.get(0);
        Assert.assertEquals("issuerId", model.getIssuerKey().getId());
        Assert.assertEquals(Preference.CASH, model.getPreference());
        Assert.assertEquals(PreferenceAction.SET, model.getAction());

        ModelPreferenceAction model2 = models.get(1);
        Assert.assertEquals("issuerId", model2.getIssuerKey().getId());
        Assert.assertEquals(Preference.PRORATA, model2.getPreference());
    }

    @Test
    public void testToDtoList() {
        ModelPreferenceAction mockModel = Mockito.mock(ModelPreferenceAction.class);
        Mockito.when(mockModel.getIssuerKey()).thenReturn(AccountKey.valueOf("issuerId"));
        Mockito.when(mockModel.getPreference()).thenReturn(Preference.CASH);
        Mockito.when(mockModel.getAction()).thenReturn(PreferenceAction.SET);

        ModelPreferenceAction mockModel2 = Mockito.mock(ModelPreferenceAction.class);
        Mockito.when(mockModel2.getIssuerKey()).thenReturn(AccountKey.valueOf("issuerId"));
        Mockito.when(mockModel2.getPreference()).thenReturn(Preference.PRORATA);
        Mockito.when(mockModel2.getAction()).thenReturn(PreferenceAction.SET);

        List<TransferPreferenceDto> dtos = ModelPreferenceConverter.toDtoList(Arrays.asList(mockModel, mockModel2));

        Assert.assertEquals(2, dtos.size());

        TransferPreferenceDto dto = dtos.get(0);
        Assert.assertEquals("issuerId", dto.getIssuerKey().getAccountId());
        Assert.assertEquals(Preference.CASH.name(), dto.getPreference());
        Assert.assertEquals(PreferenceAction.SET.name(), dto.getAction());

        TransferPreferenceDto dto2 = dtos.get(1);
        Assert.assertEquals("issuerId", dto2.getIssuerKey().getAccountId());
        Assert.assertEquals(Preference.PRORATA.name(), dto2.getPreference());
    }

    @Test
    public void conversionIsConsistent() {
        ModelPreferenceAction mockModel = Mockito.mock(ModelPreferenceAction.class);
        Mockito.when(mockModel.getIssuerKey()).thenReturn(AccountKey.valueOf("issuerId"));
        Mockito.when(mockModel.getPreference()).thenReturn(Preference.CASH);
        Mockito.when(mockModel.getAction()).thenReturn(PreferenceAction.SET);

        List<TransferPreferenceDto> dtos = ModelPreferenceConverter.toDtoList(Arrays.asList(mockModel));
        List<ModelPreferenceAction> models = ModelPreferenceConverter.fromDtoList(dtos);

        Assert.assertEquals(1, models.size());

        ModelPreferenceAction model = models.get(0);
        Assert.assertEquals(mockModel.getIssuerKey().getId(), model.getIssuerKey().getId());
        Assert.assertEquals(mockModel.getPreference(), model.getPreference());
    }
}
