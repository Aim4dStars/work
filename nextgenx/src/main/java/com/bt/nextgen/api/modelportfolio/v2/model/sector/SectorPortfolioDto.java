package com.bt.nextgen.api.modelportfolio.v2.model.sector;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.avaloq.modelportfolio.sectorportfolio.SectorPortfolio;
import org.joda.time.DateTime;

import java.math.BigInteger;

public class SectorPortfolioDto extends BaseDto implements KeyedDto<SectorPortfolioKey> {

    private SectorPortfolioKey sectorPortfolioKey;
    private String name;
    private String code;
    private String assetClass;
    private String category;
    private String productType;
    private String status;
    private BigInteger ipsCount;
    private DateTime lastModifiedDate;
    private String lastModifiedBy;

    public SectorPortfolioDto(SectorPortfolio sectorPortfolio) {
        super();
        this.sectorPortfolioKey = new SectorPortfolioKey(sectorPortfolio.getId());
        this.name = sectorPortfolio.getName();
        this.code = sectorPortfolio.getCode();
        this.assetClass = sectorPortfolio.getAssetClass();
        this.category = sectorPortfolio.getCategory();
        this.productType = sectorPortfolio.getProductType();
        this.status = sectorPortfolio.getStatus();
        this.ipsCount = sectorPortfolio.getIpsCount();
        this.lastModifiedDate = sectorPortfolio.getLastModifiedDate();
        this.lastModifiedBy = sectorPortfolio.getLastModifiedBy();
    }

    @Override
    public SectorPortfolioKey getKey() {
        return sectorPortfolioKey;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getAssetClass() {
        return assetClass;
    }

    public String getCategory() {
        return category;
    }

    public String getProductType() {
        return productType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public BigInteger getIpsCount() {
        return ipsCount;
    }

    public DateTime getLastModifiedDate() {
        return lastModifiedDate;
    }
    
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }
}
