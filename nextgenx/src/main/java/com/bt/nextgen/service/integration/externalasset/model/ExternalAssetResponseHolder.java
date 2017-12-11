package com.bt.nextgen.service.integration.externalasset.model;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;

import java.util.ArrayList;
import java.util.List;

@ServiceBean(xpath = "/", type = ServiceBeanType.CONCRETE)
public class ExternalAssetResponseHolder extends AvaloqBaseResponseImpl
{
    @ServiceElementList(xpath = "//bp_list/bp/cont_type_list/cont_type", type = AbstractAssetContainerImpl.class)
    private List<AssetContainer> assetContainer = new ArrayList<>();

    public List<AssetContainer> getAssetContainer() {
        return assetContainer;
    }
}