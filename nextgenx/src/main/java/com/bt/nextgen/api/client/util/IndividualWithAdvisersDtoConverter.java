package com.bt.nextgen.api.client.util;

import com.bt.nextgen.api.client.model.IndividualWithAdvisersDto;
import com.bt.nextgen.service.avaloq.domain.existingclient.IndividualWithAccountDataImpl;

import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndividualWithAdvisersDtoConverter extends ExistingClientSearchDtoConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndividualWithAdvisersDtoConverter.class);

    @Override
    public Object toDTO(Object client) {
        IndividualWithAccountDataImpl individual = (IndividualWithAccountDataImpl) client;
        IndividualWithAdvisersDto individualDto =  new IndividualWithAdvisersDto();

        setCommonClientAttributes(individualDto, individual);

        if (individual.getDateOfBirth() != null) {
            individualDto.setDateOfBirth(individual.getDateOfBirth().toString());
            individualDto.setDateOfBirthForDisplay(DateTimeFormat.forPattern("dd MMM yyyy").print(individual.getDateOfBirth()));
        }

        individualDto.setFirstName(individual.getFirstName());
        individualDto.setLastName(individual.getLastName());
        individualDto.setDisplayName(individualDto.getLastName() + ", " + individualDto.getFirstName());

        individualDto.setInvestorType("Individual");

        Set<String> adviserPositionIds = new HashSet<>();
        adviserPositionIds.addAll(((IndividualWithAccountDataImpl) client).getAdviserPositionIds());
        individualDto.setAdviserPositionIds(adviserPositionIds);
        individualDto.setIndividualInvestor(isIndividualInvestor(individual));

        LOGGER.info("Retrieved individual full name ::" + individualDto.getFullName());
        return individualDto;
    }

    /**
     * This method will check whether the individual is investor based on primary email and mobile.
     * Primary email and mobiles are not captured for shareholder / BO
     * @param individual
     * @return true if the individual is investor and not shareholder / beneficial owner
     */
    private boolean isIndividualInvestor(IndividualWithAccountDataImpl individual) {
        return isNotEmpty(individual.getPrimaryEmail()) && isNotEmpty(individual.getPrimaryMobile());
    }
}
