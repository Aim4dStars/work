package com.bt.nextgen.api.watchlist.v1.model;

import com.bt.nextgen.core.domain.key.StringIdKey;

public class InvestmentWatchlistKey extends StringIdKey {

    private static final long serialVersionUID = 1L;

    private InvestmentWatchlistKey(final String watchlistId) {
        super(watchlistId);
    }

    public static InvestmentWatchlistKey valueOf(String watchlistId) {
        return new InvestmentWatchlistKey(watchlistId);
    }
}
