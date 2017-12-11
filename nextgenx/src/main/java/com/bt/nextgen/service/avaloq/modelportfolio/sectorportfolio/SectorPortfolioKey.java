package com.bt.nextgen.service.avaloq.modelportfolio.sectorportfolio;

import com.bt.nextgen.core.domain.key.StringIdKey;

@SuppressWarnings("serial")
public final class SectorPortfolioKey extends StringIdKey {

    private SectorPortfolioKey() {
        super();
    }

    private SectorPortfolioKey(String sectorPortfolioId) {
        super(sectorPortfolioId);
    }

    public static SectorPortfolioKey valueOf(String sectorPortfolioId) {
        if (sectorPortfolioId == null) {
            return null;
        } else {
            return new SectorPortfolioKey(sectorPortfolioId);
        }
    }
}
