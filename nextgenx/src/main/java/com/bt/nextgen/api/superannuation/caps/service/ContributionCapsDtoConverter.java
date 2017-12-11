package com.bt.nextgen.api.superannuation.caps.service;


import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.superannuation.caps.model.ContributionCapDto;
import com.bt.nextgen.api.superannuation.caps.model.SuperAccountContributionCapsDto;
import com.bt.nextgen.service.avaloq.superannuation.caps.model.ContributionCaps;
import com.bt.nextgen.service.integration.cashcategorisation.model.ContributionClassification;
import org.joda.time.DateTime;

import java.util.LinkedList;
import java.util.List;


public final class ContributionCapsDtoConverter {
    /**
     * Ctor.
     */
    private ContributionCapsDtoConverter() {
    }

    /**
     * Convert integration cap object into a dto object
     *
     * @param accountKey account key
     * @param caps       integration cap bean
     *
     * @return
     */
    public static final SuperAccountContributionCapsDto toContributionCapsDto(AccountKey accountKey, DateTime financialYearStartDate, ContributionCaps caps) {
        return new SuperAccountContributionCapsDto(accountKey, financialYearStartDate.toLocalDate(), toContributionCapListDto(caps));
    }

    private static final List<ContributionCapDto> toContributionCapListDto(ContributionCaps caps) {
        ContributionCapDto concCapDto = new ContributionCapDto();
        concCapDto.setAmount(caps.getConcessionalCap());
        concCapDto.setContributionClassification(ContributionClassification.CONCESSIONAL.getAvaloqInternalId());
        concCapDto.setContributionClassificationLabel(ContributionClassification.CONCESSIONAL.getName());

        ContributionCapDto nconcCapDto = new ContributionCapDto();
        nconcCapDto.setAmount(caps.getNonConcessionalCap());
        nconcCapDto.setContributionClassification(ContributionClassification.NON_CONCESSIONAL.getAvaloqInternalId());
        nconcCapDto.setContributionClassificationLabel(ContributionClassification.NON_CONCESSIONAL.getName());

        List<ContributionCapDto> capsDtoList = new LinkedList<>();
        capsDtoList.add(concCapDto);
        capsDtoList.add(nconcCapDto);

        return capsDtoList;
    }
}