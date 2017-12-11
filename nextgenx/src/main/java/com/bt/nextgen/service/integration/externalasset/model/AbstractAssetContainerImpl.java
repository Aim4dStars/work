package com.bt.nextgen.service.integration.externalasset.model;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;

import java.util.ArrayList;
import java.util.List;

/**
 * Container abstract class for lazy bean
 */

@ServiceBean(xpath = "cont_type", type = ServiceBeanType.ABSTRACT,
        lazyBeanClasses = {OtherContainers.class, ExternalAssetContainer.class})
public abstract class AbstractAssetContainerImpl implements AssetContainer {

    @Override
    public List<ExternalAsset> getExternalAssetList() {
        return new ArrayList<>();
    }
}
