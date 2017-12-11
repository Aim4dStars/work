package com.bt.nextgen.service.integration.externalasset.model;

import com.bt.nextgen.api.smsf.constants.PropertyType;

@SuppressWarnings({"squid:S1068", "findbugs:URF_UNREAD_FIELD"})
public interface OffPlatformExternalAsset
{

    PropertyType getPropertyType();

    void setPropertyType(PropertyType propertyType);

    String getPositionCode();

    void setPositionCode(String positionCode);
}
