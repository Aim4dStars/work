package com.bt.nextgen.api.portfolio.v3.model.cashmovements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;

import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.bt.nextgen.service.integration.portfolio.cashmovements.CashMovement;

public class OutstandingMovementsDto extends AbstractOutstandingCashDto {
    private List<OutstandingCash> outstanding = new ArrayList<>();

    public OutstandingMovementsDto(Map<Pair<String, DateTime>, Asset> assets, Map<AssetKey, TermDepositPresentation> tds,
            List<CashMovement> movements) {
        super(movements.isEmpty() ? null : movements.get(0).getCategory());
        for (CashMovement movement : movements) {
            outstanding.add(new OutstandingCash(assets.get(new ImmutablePair<String, DateTime>(movement.getAssetKey().getId(),
                    movement.getSettlementDate())), tds.get(movement.getAssetKey()), movement));
        }
    }

    public List<OutstandingCash> getOutstanding() {
        return outstanding;
    }

}
