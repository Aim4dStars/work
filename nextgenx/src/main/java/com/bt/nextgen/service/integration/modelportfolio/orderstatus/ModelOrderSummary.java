package com.bt.nextgen.service.integration.modelportfolio.orderstatus;

import java.util.List;

public interface ModelOrderSummary {

    public String getAssetCode();

    public String getAssetId();

    public List<ModelOrderDetails> getIpsList();
}
