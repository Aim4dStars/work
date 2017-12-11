package com.bt.nextgen.api.beneficiary.controller;

/**
 * Created by L067218 on 7/07/2016.
 */

import com.bt.nextgen.api.beneficiary.model.SuperNominationTypeDto;
import com.bt.nextgen.api.beneficiary.service.NominationTypesDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.util.StringUtils.trimArrayElements;

/**
    * Test case for SuperNominationTypeApiController
     */
@RunWith(MockitoJUnitRunner.class)
public class SuperNominationTypeApiControllerTest {

    @InjectMocks
    SuperNominationTypeApiController superNominationTypeApiController;

    @Mock
    NominationTypesDtoService nominationTypesDtoService;

    private List<SuperNominationTypeDto> superNominationTypeDtoList = null;

    String accountId;
    String filter;

    @Before
    public void init() {

        accountId = EncodedString.fromPlainText("11861").toString();
        superNominationTypeDtoList = new ArrayList<>();
        SuperNominationTypeDto nominationTypeDto1 = setNominationDto("21", "NOMN_AUTO_REVSNRY", "nomn_auto_revsnry", "au_sa_death_benf", "Auto reversionary", true, true, "pension");
        SuperNominationTypeDto nominationTypeDto2 = setNominationDto("2", "NOMN_NBIND_SIS", "nomn_nbind_sis", "au_sa_death_benf", "Non-lapsing nomination", false, false, "super,pension");
        SuperNominationTypeDto nominationTypeDto3 = setNominationDto("4", "NOMN_BIND_NLAPS_TRUSTD", "nomn_bind_nlaps_trustd", "au_sa_death_benf", "Trustee discretion", false, false, "super,pension");
        superNominationTypeDtoList.add(nominationTypeDto1);
        superNominationTypeDtoList.add(nominationTypeDto2);
        superNominationTypeDtoList.add(nominationTypeDto3);

    }

    @Test
    public void getSuperNominationTypesWithFilter() {
        filter= "true";
        when(nominationTypesDtoService.search(any(ArrayList.class), any(ServiceErrors.class))).
                thenReturn(superNominationTypeDtoList);

        ApiResponse response = superNominationTypeApiController.getSuperNominationTypes(accountId, filter);
        verify(nominationTypesDtoService, times(1)).search(any(ArrayList.class), any(ServiceErrors.class));

        assertThat(response, is(notNullValue()));

        ResultListDto<SuperNominationTypeDto> resultListDto = (ResultListDto<SuperNominationTypeDto>) response.getData();
        assertThat("List size", resultListDto.getResultList().size(), is(3));


        SuperNominationTypeDto nominationTypeDto1 = resultListDto.getResultList().get(0);
        assertThat("nomination type 0 - id", nominationTypeDto1.getId(), equalTo("21"));
        assertThat("nomination type 0 - label", nominationTypeDto1.getLabel() , equalTo("Auto reversionary"));
        assertThat("nomination type 0 - intlId", nominationTypeDto1.getIntlId(), equalTo("nomn_auto_revsnry"));
        assertThat("nomination type 0 - listName", nominationTypeDto1.getListName(), equalTo("au_sa_death_benf"));
        assertThat("nomination type 0 - value", nominationTypeDto1.getValue(), equalTo("NOMN_AUTO_REVSNRY"));
        assertThat("nomination type 0 - isDependentOnly", nominationTypeDto1.isDependentOnly(), equalTo(true));
        assertThat("nomination type 0 - isSoleNominationOnly", nominationTypeDto1.isSoleNominationOnly(), equalTo(true));

        SuperNominationTypeDto nominationTypeDto2 = resultListDto.getResultList().get(1);
        assertThat("nomination type 1 - id", nominationTypeDto2.getId(), equalTo("2"));
        assertThat("nomination type 1 - label", nominationTypeDto2.getLabel() , equalTo("Non-lapsing nomination"));
        assertThat("nomination type 1 - intlId", nominationTypeDto2.getIntlId(), equalTo("nomn_nbind_sis"));
        assertThat("nomination type 1 - listName", nominationTypeDto2.getListName(), equalTo("au_sa_death_benf"));
        assertThat("nomination type 1 - value", nominationTypeDto2.getValue(), equalTo("NOMN_NBIND_SIS"));
        assertThat("nomination type 1 - isDependentOnly", nominationTypeDto2.isDependentOnly(), equalTo(false));
        assertThat("nomination type 1 - isSoleNominationOnly", nominationTypeDto2.isSoleNominationOnly(), equalTo(false));

        SuperNominationTypeDto nominationTypeDto3 = resultListDto.getResultList().get(2);
        assertThat("nomination type 2 - id", nominationTypeDto3.getId(), equalTo("4"));
        assertThat("nomination type 2 - label", nominationTypeDto3.getLabel() , equalTo("Trustee discretion"));
        assertThat("nomination type 2 - intlId", nominationTypeDto3.getIntlId(), equalTo("nomn_bind_nlaps_trustd"));
        assertThat("nomination type 2 - listName", nominationTypeDto3.getListName(), equalTo("au_sa_death_benf"));
        assertThat("nomination type 2 - value", nominationTypeDto3.getValue(), equalTo("NOMN_BIND_NLAPS_TRUSTD"));
        assertThat("nomination type 2 - isDependentOnly", nominationTypeDto3.isDependentOnly(), equalTo(false));
        assertThat("nomination type 2 - isSoleNominationOnly", nominationTypeDto3.isSoleNominationOnly(), equalTo(false));
    }

    @Test
    public void testForNullNominationTypeList() {
        when(nominationTypesDtoService.search(any(ArrayList.class),any(ServiceErrors.class))).
                thenReturn(null);
        ApiResponse response = superNominationTypeApiController.getSuperNominationTypes(accountId, filter);
        verify(nominationTypesDtoService, times(1)).search(any(ArrayList.class),  any(ServiceErrors.class));
        ResultListDto<SuperNominationTypeDto> resultListDto = (ResultListDto<SuperNominationTypeDto>) response.getData();
        //Verify that the result list is null
        assertThat("List size", resultListDto.getResultList(), is(nullValue()));
    }

    public SuperNominationTypeDto setNominationDto(String id, String value, String intlId, String listName, String label, boolean dependentOnly, boolean soleNominationOnly, String subTypes){
        SuperNominationTypeDto nominationDto = new SuperNominationTypeDto();
        nominationDto.setId(id);
        nominationDto.setValue(value);
        nominationDto.setLabel(label);
        nominationDto.setListName(listName);
        nominationDto.setIntlId(intlId);
        nominationDto.setDependentOnly(dependentOnly);
        nominationDto.setSoleNominationOnly(soleNominationOnly);
        String[] superAccountSubTypes = trimArrayElements(org.apache.commons.lang3.StringUtils.split(value, ","));
        nominationDto.setSupportedSuperAccountSubTypes(Arrays.asList(superAccountSubTypes));
        return nominationDto;
    }

}
