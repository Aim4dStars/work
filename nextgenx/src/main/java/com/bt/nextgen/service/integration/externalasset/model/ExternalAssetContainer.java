package com.bt.nextgen.service.integration.externalasset.model;

import com.bt.nextgen.integration.xml.annotation.LazyServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created to verify container type for smsf external holdings
 */
@LazyServiceBean(expression="cont_type_head_list/cont_type_head/cont_type_id/val", staticCodeCategory = "CONTAINER_TYPE", equalsTo = "fe_cc_ex")
public class ExternalAssetContainer implements AssetContainer{

    @ServiceElementList(xpath = "cont_list/cont/asset_grp_list/asset_grp/pos_list/pos/pos_head_list/pos_head", type = AbstractExternalAsset.class)
    private List<ExternalAsset> externalAssetList = new ArrayList<>();

    public List<ExternalAsset> getExternalAssetList() {
        return externalAssetList;
    }
}
