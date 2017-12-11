package com.bt.nextgen.api.beneficiary.service;

import com.bt.nextgen.api.beneficiary.model.SuperNominationTypeDto;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

/**
 * Integration test class for  {@link NominationTypesDtoService}
 */
public class NominationTypeDtoServiceIntegrationTest extends BaseSecureIntegrationTest {

    @Autowired
    NominationTypesDtoService nominationTypesDtoService;

    @Test
    public void findAllNominationTypes() {

        ApiSearchCriteria accountIdCriteria = new ApiSearchCriteria("accountId", ApiSearchCriteria.SearchOperation.EQUALS, "15E67EC04FBD44111C4EC74731CF06E74D5C8FD5E765FAB3", ApiSearchCriteria.OperationType.STRING);
        ApiSearchCriteria filterCriteria = new ApiSearchCriteria("filterForAccount", ApiSearchCriteria.SearchOperation.EQUALS, "true", ApiSearchCriteria.OperationType.STRING);

        List<ApiSearchCriteria> searchCriteriaList = new ArrayList<>();
        searchCriteriaList.add(accountIdCriteria);
        searchCriteriaList.add(filterCriteria);


        final List<SuperNominationTypeDto> dtoList = nominationTypesDtoService.search(searchCriteriaList, new ServiceErrorsImpl());
        SuperNominationTypeDto dto;
        String infoStr;
        int i;

        assertThat("Total Nomination types", dtoList.size(), equalTo(3));

        i = 0;
        dto = dtoList.get(i);
        infoStr = "nomination type " + i + " - ";
        assertThat(infoStr + "id", dto.getId(), equalTo("21"));
        assertThat(infoStr + "name value", dto.getValue(), equalTo("NOMN_AUTO_REVSNRY"));
        assertThat(infoStr + "IntlId", dto.getIntlId(), equalTo("nomn_auto_revsnry"));
        assertThat(infoStr + "listName", dto.getListName(), equalTo("au_sa_death_benf"));
        assertThat(infoStr + "name label", dto.getLabel(), equalTo("Auto reversionary"));
        assertThat(infoStr + "supportedSuperAccountSubTypes", dto.getSupportedSuperAccountSubTypes(), containsInAnyOrder("pension"));
        assertThat(infoStr + "dependentOnly", dto.isDependentOnly(), equalTo(true));
        assertThat(infoStr + "soleNominationOnly", dto.isSoleNominationOnly(), equalTo(true));

        i = 1;
        dto = dtoList.get(i);
        infoStr = "nomination type " + i + " - ";
        assertThat(infoStr + "id", dto.getId(), equalTo("4"));
        assertThat(infoStr + "name value", dto.getValue(), equalTo("NOMN_BIND_NLAPS_TRUSTD"));
        assertThat(infoStr + "IntlId", dto.getIntlId(), equalTo("nomn_bind_nlaps_trustd"));
        assertThat(infoStr + "listName", dto.getListName(), equalTo("au_sa_death_benf"));
        assertThat(infoStr + "name label", dto.getLabel(), equalTo("Non-lapsing nomination"));
        assertThat(infoStr + "supportedSuperAccountSubTypes", dto.getSupportedSuperAccountSubTypes(), containsInAnyOrder("super", "pension"));
        assertThat(infoStr + "dependentOnly", dto.isDependentOnly(), equalTo(false));
        assertThat(infoStr + "soleNominationOnly", dto.isSoleNominationOnly(), equalTo(false));

        i = 2;
        dto = dtoList.get(i);
        infoStr = "nomination type " + i + " - ";
        assertThat(infoStr + "id", dto.getId(), equalTo("2"));
        assertThat(infoStr + "name value", dto.getValue(), equalTo("NOMN_NBIND_SIS"));
        assertThat(infoStr + "IntlId", dto.getIntlId(), equalTo("nomn_nbind_sis"));
        assertThat(infoStr + "listName", dto.getListName(), equalTo("au_sa_death_benf"));
        assertThat(infoStr + "name label", dto.getLabel(), equalTo("Trustee discretion"));
        assertThat(infoStr + "supportedSuperAccountSubTypes", dto.getSupportedSuperAccountSubTypes(), containsInAnyOrder("pension", "super"));
        assertThat(infoStr + "dependentOnly", dto.isDependentOnly(), equalTo(false));
        assertThat(infoStr + "soleNominationOnly", dto.isSoleNominationOnly(), equalTo(false));
    }
}
