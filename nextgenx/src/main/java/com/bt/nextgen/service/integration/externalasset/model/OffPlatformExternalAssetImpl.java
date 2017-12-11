package com.bt.nextgen.service.integration.externalasset.model;


import com.bt.nextgen.integration.xml.annotation.LazyServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.externalasset.builder.PropertyTypeNameConverter;
import com.bt.nextgen.api.smsf.constants.PropertyType;

/**
 * Represents a specific asset that is not in the Panorama universe of actual assets.
 * <p>That is, it is not tradeable or in any global list of assets on the system</p>
 */
@LazyServiceBean(expression = "if (count(asset_id/val)=0) then true() else false()")
public class OffPlatformExternalAssetImpl extends AbstractExternalAsset implements OffPlatformExternalAsset
{
   //@ServiceElement(xpath = "ext_hld_property_type", staticCodeCategory = "POS_PROPERTY_TYPE")
   @ServiceElement(xpath = "ext_hld_prty_type/val", staticCodeCategory = "POS_PROPERTY_TYPE")
    private PropertyType propertyType;


    @Override
    public PropertyType getPropertyType() {
        return propertyType;
    }

    @Override
    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }


}
