package com.bt.nextgen.api.modelportfolio.v2.util.rebalance;

import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.RebalanceOrderDetailsDto;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.RebalanceOrderGroupDto;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
public class RebalanceOrdersSortingHelper {

    private static final String TP_CASH_NAME = "TP Cash";
    private static final Ordering<String> NULLSAFE_CASEINSENSITIVE = Ordering.from(String.CASE_INSENSITIVE_ORDER)
            .nullsLast();

    public void basicSort(List<RebalanceOrderGroupDto> orderGroupDtoList) {
        Collections.sort(orderGroupDtoList, new RebalanceOrderGroupComparator());
        for (RebalanceOrderGroupDto orderGroupDto : orderGroupDtoList) {
            Collections.sort(orderGroupDto.getOrderDetails(), new RebalanceOrderDetailsBasicComparator());
        }
    }

    public void detailedSort(List<RebalanceOrderGroupDto> orderGroupDtoList) {
        Collections.sort(orderGroupDtoList, new RebalanceOrderGroupComparator());
        for (RebalanceOrderGroupDto orderGroupDto : orderGroupDtoList) {
            Collections.sort(orderGroupDto.getOrderDetails(), new RebalanceOrderDetailsDetailedComparator());
        }
    }

    private class RebalanceOrderGroupComparator implements Comparator<RebalanceOrderGroupDto> {
        // Sort by ascending adviser name
        @Override
        public int compare(RebalanceOrderGroupDto o1, RebalanceOrderGroupDto o2) {
            return NULLSAFE_CASEINSENSITIVE.compare(o1.getAdviserName(), o2.getAdviserName());
        }
    }

    private class RebalanceOrderDetailsBasicComparator implements Comparator<RebalanceOrderDetailsDto> {
        // Sort by ascending asset name. TP Cash is always last.
        @Override
        public int compare(RebalanceOrderDetailsDto o1, RebalanceOrderDetailsDto o2) {
            boolean isO1Cash = NULLSAFE_CASEINSENSITIVE.compare(o1.getAssetName(), TP_CASH_NAME) == 0;
            boolean isO2Cash = NULLSAFE_CASEINSENSITIVE.compare(o2.getAssetName(), TP_CASH_NAME) == 0;

            if (isO1Cash && isO2Cash) {
                return 0;
            } else if (isO1Cash) {
                return 1;
            } else if (isO2Cash) {
                return -1;
            }
            return NULLSAFE_CASEINSENSITIVE.compare(o1.getAssetName(), o2.getAssetName());
        }
    }

    private class RebalanceOrderDetailsDetailedComparator implements Comparator<RebalanceOrderDetailsDto> {
        // Sort by ascending account name, then by ascending asset class, then by ascending asset name. TP Cash is always last.
        @Override
        public int compare(RebalanceOrderDetailsDto o1, RebalanceOrderDetailsDto o2) {
            boolean accountNamesMatch = NULLSAFE_CASEINSENSITIVE.compare(o1.getAccountName(), o2.getAccountName()) == 0;
            boolean isO1Cash = NULLSAFE_CASEINSENSITIVE.compare(o1.getAssetName(), TP_CASH_NAME) == 0;
            boolean isO2Cash = NULLSAFE_CASEINSENSITIVE.compare(o2.getAssetName(), TP_CASH_NAME) == 0;

            if (accountNamesMatch) {
                if (isO1Cash && isO2Cash) {
                    return 0;
                } else if (isO1Cash) {
                    return 1;
                } else if (isO2Cash) {
                    return -1;
                }
            }
            return ComparisonChain.start().compare(o1.getAccountName(), o2.getAccountName(), NULLSAFE_CASEINSENSITIVE)
                    .compare(o1.getAssetClass(), o2.getAssetClass(), NULLSAFE_CASEINSENSITIVE)
                    .compare(o1.getAssetName(), o2.getAssetName(), NULLSAFE_CASEINSENSITIVE).result();
        }
    }

}
