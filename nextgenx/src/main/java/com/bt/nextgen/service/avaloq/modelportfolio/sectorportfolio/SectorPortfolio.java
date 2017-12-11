package com.bt.nextgen.service.avaloq.modelportfolio.sectorportfolio;

import org.joda.time.DateTime;

import java.math.BigInteger;

public interface SectorPortfolio {

    public String getId();

    public String getName();

    public String getCode();

    public String getInvestmentManagerId();

    public String getAssetClass();

    public String getCategory();

    public String getProductType();

    public String getStatus();

    public BigInteger getIpsCount();

    public DateTime getLastModifiedDate();

    public String getLastModifiedBy();

}
