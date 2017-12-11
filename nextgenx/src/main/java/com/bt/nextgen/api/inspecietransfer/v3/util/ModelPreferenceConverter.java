package com.bt.nextgen.api.inspecietransfer.v3.util;

import com.bt.nextgen.api.inspecietransfer.v3.model.TransferPreferenceDto;
import com.bt.nextgen.service.avaloq.transfer.transfergroup.ModelPreferenceImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.modelpreferences.Preference;
import com.bt.nextgen.service.integration.order.ModelPreferenceAction;
import com.bt.nextgen.service.integration.order.PreferenceAction;

import java.util.ArrayList;
import java.util.List;

public final class ModelPreferenceConverter {

    private ModelPreferenceConverter() {
        // hide public constructor
    }

    public static List<ModelPreferenceAction> fromDtoList(List<TransferPreferenceDto> preferenceDtos) {
        List<ModelPreferenceAction> preferences = new ArrayList<>();

        if (preferenceDtos != null) {
            for (TransferPreferenceDto preferenceDto : preferenceDtos) {
                ModelPreferenceAction preference = new ModelPreferenceImpl(AccountKey.valueOf(preferenceDto.getIssuerKey()
                        .getAccountId()), Preference.valueOf(preferenceDto.getPreference()),
                        PreferenceAction.valueOf(preferenceDto.getAction()));
                preferences.add(preference);
            }
        }

        return preferences;
    }

    public static List<TransferPreferenceDto> toDtoList(List<ModelPreferenceAction> preferences) {
        List<TransferPreferenceDto> preferenceDtos = new ArrayList<>();

        if (preferences != null) {
            for (ModelPreferenceAction preference : preferences) {
                String issuerId = preference.getIssuerKey() == null ? null : preference.getIssuerKey().getId();
                String name = preference.getPreference() == null ? null : preference.getPreference().name();
                String action = preference.getAction() == null ? null : preference.getAction().name();
                TransferPreferenceDto preferenceDto = new TransferPreferenceDto(issuerId, name, action);
                preferenceDtos.add(preferenceDto);
            }
        }

        return preferenceDtos;
    }
}
