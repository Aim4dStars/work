package com.bt.nextgen.service.integration.externalasset.model;

import com.bt.nextgen.integration.xml.annotation.LazyServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to map all other containers other than
 */
@LazyServiceBean(expression="cont_type_head_list/cont_type_head/cont_type_id/val", staticCodeCategory = "CONTAINER_TYPE", equalsTo = "portf_dir")
public class OtherContainers implements AssetContainer {

    @Override
    public List<ExternalAsset> getExternalAssetList() {
        return new ArrayList<>();

    }
}
