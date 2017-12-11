package com.bt.nextgen.api.modelportfolio.v2.model.sector;

public class SectorPortfolioKey
{
    private String sectorPortfolioId;

    public SectorPortfolioKey(String sectorPortfolioId)
	{
		super();
        this.sectorPortfolioId = sectorPortfolioId;
	}

    public String getSectorPortfolioId()
	{
        return sectorPortfolioId;
	}

}
