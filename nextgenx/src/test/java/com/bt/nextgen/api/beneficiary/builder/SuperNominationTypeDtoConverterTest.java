package com.bt.nextgen.api.beneficiary.builder;

import com.bt.nextgen.api.beneficiary.model.SuperNominationTypeDto;
import com.bt.nextgen.service.avaloq.account.AccountSubType;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.integration.code.Code;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by L067218 on 7/07/2016.
 */

@RunWith(MockitoJUnitRunner.class)
public class SuperNominationTypeDtoConverterTest {

    Collection<Code> typeCodeList = new ArrayList<>();

    @Before
    public void setUp() throws Exception {

        Code code1 = new CodeImpl("4", "NOMN_BIND_NLAPS_TRUSTD", "Australian Superannuation Death Benefits Nomination - Binding - Non Lapsing (Trust Deed)", "nomn_bind_nlaps_trustd");
        ((CodeImpl) code1).addField("btfg$ui_name", "Trustee discretion");
        ((CodeImpl) code1).addField("btfg$ui_dep_only", "");
        ((CodeImpl) code1).addField("btfg$ui_pct", "");
        ((CodeImpl) code1).addField("btfg$ui_super_acc_type", "super,pension");
        Code code2 = new CodeImpl("2", "NOMN_NBIND_SIS", "Australian Superannuation Death Benefits Nomination - Non Binding (SIS)", "nomn_nbind_sis");
        ((CodeImpl) code2).addField("btfg$ui_name", "Non-lapsing nomination");
        ((CodeImpl) code2).addField("btfg$ui_dep_only", "");
        ((CodeImpl) code2).addField("btfg$ui_pct", "");
        ((CodeImpl) code2).addField("btfg$ui_super_acc_type", "super,pension");
        Code code3 = new CodeImpl("21", "NOMN_AUTO_REVSNRY", "Australian Superannuation Death Benefits Nomination - Automatic Reversionary", "nomn_auto_revsnry");
        ((CodeImpl) code3).addField("btfg$ui_name", "Auto reversionary");
        ((CodeImpl) code3).addField("btfg$ui_dep_only", "+");
        ((CodeImpl) code3).addField("btfg$ui_pct", "100");
        ((CodeImpl) code3).addField("btfg$ui_super_acc_type", "pension");
        typeCodeList.add(code1);
        typeCodeList.add(code2);
        typeCodeList.add(code3);

    }
    @Test
    public void getNominationTypesWhenAccountIsPensionAndFilterIsTrue()
    {
        final SuperNominationTypeDtoConverter dtoConverter = new SuperNominationTypeDtoConverter();
        final List<SuperNominationTypeDto> nominationList;
        nominationList = dtoConverter.createNominationTypeList(typeCodeList, AccountSubType.PENSION, "true");
        assertEquals(nominationList.size(),3);

        SuperNominationTypeDto nominationTypeDto1 = nominationList.get(0);
        assertThat("nomination type 0 - id", nominationTypeDto1.getId(), equalTo("21"));
        assertThat("nomination type 0 - label", nominationTypeDto1.getLabel() , equalTo("Auto reversionary"));
        assertThat("nomination type 0 - intlId", nominationTypeDto1.getIntlId(), equalTo("nomn_auto_revsnry"));
        assertThat("nomination type 0 - listName", nominationTypeDto1.getListName(), equalTo("au_sa_death_benf"));
        assertThat("nomination type 0 - value", nominationTypeDto1.getValue(), equalTo("NOMN_AUTO_REVSNRY"));
        assertThat("nomination type 0 - isDependentOnly", nominationTypeDto1.isDependentOnly(), equalTo(true));
        assertThat("nomination type 0 - isSoleNominationOnly", nominationTypeDto1.isSoleNominationOnly(), equalTo(true));

        SuperNominationTypeDto nominationTypeDto2 = nominationList.get(1);
        assertThat("nomination type 1 - id", nominationTypeDto2.getId(), equalTo("2"));
        assertThat("nomination type 1 - label", nominationTypeDto2.getLabel() , equalTo("Non-lapsing nomination"));
        assertThat("nomination type 1 - intlId", nominationTypeDto2.getIntlId(), equalTo("nomn_nbind_sis"));
        assertThat("nomination type 1 - listName", nominationTypeDto2.getListName(), equalTo("au_sa_death_benf"));
        assertThat("nomination type 1 - value", nominationTypeDto2.getValue(), equalTo("NOMN_NBIND_SIS"));
        assertThat("nomination type 1 - isDependentOnly", nominationTypeDto2.isDependentOnly(), equalTo(false));
        assertThat("nomination type 1 - isSoleNominationOnly", nominationTypeDto2.isSoleNominationOnly(), equalTo(false));

        SuperNominationTypeDto nominationTypeDto3 = nominationList.get(2);
        assertThat("nomination type 2 - id", nominationTypeDto3.getId(), equalTo("4"));
        assertThat("nomination type 2 - label", nominationTypeDto3.getLabel() , equalTo("Trustee discretion"));
        assertThat("nomination type 2 - intlId", nominationTypeDto3.getIntlId(), equalTo("nomn_bind_nlaps_trustd"));
        assertThat("nomination type 2 - listName", nominationTypeDto3.getListName(), equalTo("au_sa_death_benf"));
        assertThat("nomination type 2 - value", nominationTypeDto3.getValue(), equalTo("NOMN_BIND_NLAPS_TRUSTD"));
        assertThat("nomination type 2 - isDependentOnly", nominationTypeDto3.isDependentOnly(), equalTo(false));
        assertThat("nomination type 2 - isSoleNominationOnly", nominationTypeDto3.isSoleNominationOnly(), equalTo(false));


    }

    @Test
    public void getNominationTypesWhenAccountIsAccumulationAndFilterIsTrue()
    {
        final SuperNominationTypeDtoConverter dtoConverter = new SuperNominationTypeDtoConverter();
        final List<SuperNominationTypeDto> nominationList;
        nominationList = dtoConverter.createNominationTypeList(typeCodeList, AccountSubType.ACCUMULATION, "true");
        assertEquals(nominationList.size(),2);

        SuperNominationTypeDto nominationTypeDto1 = nominationList.get(0);
        assertThat("nomination type 0 - id", nominationTypeDto1.getId(), equalTo("2"));
        assertThat("nomination type 0 - label", nominationTypeDto1.getLabel() , equalTo("Non-lapsing nomination"));
        assertThat("nomination type 0 - intlId", nominationTypeDto1.getIntlId(), equalTo("nomn_nbind_sis"));
        assertThat("nomination type 0 - listName", nominationTypeDto1.getListName(), equalTo("au_sa_death_benf"));
        assertThat("nomination type 0 - value", nominationTypeDto1.getValue(), equalTo("NOMN_NBIND_SIS"));
        assertThat("nomination type 0 - isDependentOnly", nominationTypeDto1.isDependentOnly(), equalTo(false));
        assertThat("nomination type 0 - isSoleNominationOnly", nominationTypeDto1.isSoleNominationOnly(), equalTo(false));

        SuperNominationTypeDto nominationTypeDto2 = nominationList.get(1);
        assertThat("nomination type 1 - id", nominationTypeDto2.getId(), equalTo("4"));
        assertThat("nomination type 1 - label", nominationTypeDto2.getLabel() , equalTo("Trustee discretion"));
        assertThat("nomination type 1 - intlId", nominationTypeDto2.getIntlId(), equalTo("nomn_bind_nlaps_trustd"));
        assertThat("nomination type 1 - listName", nominationTypeDto2.getListName(), equalTo("au_sa_death_benf"));
        assertThat("nomination type 1 - value", nominationTypeDto2.getValue(), equalTo("NOMN_BIND_NLAPS_TRUSTD"));
        assertThat("nomination type 1 - isDependentOnly", nominationTypeDto2.isDependentOnly(), equalTo(false));
        assertThat("nomination type 1 - isSoleNominationOnly", nominationTypeDto2.isSoleNominationOnly(), equalTo(false));


    }

    @Test
    public void getNominationTypesWhenAccountIsAccumulationAndFilterIsFalse()
    {
        final SuperNominationTypeDtoConverter dtoConverter = new SuperNominationTypeDtoConverter();
        final List<SuperNominationTypeDto> nominationList;
        nominationList = dtoConverter.createNominationTypeList(typeCodeList, AccountSubType.ACCUMULATION, "false");
        assertEquals(nominationList.size(),3);

        SuperNominationTypeDto nominationTypeDto1 = nominationList.get(0);
        assertThat("nomination type 0 - id", nominationTypeDto1.getId(), equalTo("21"));
        assertThat("nomination type 0 - label", nominationTypeDto1.getLabel() , equalTo("Auto reversionary"));
        assertThat("nomination type 0 - intlId", nominationTypeDto1.getIntlId(), equalTo("nomn_auto_revsnry"));
        assertThat("nomination type 0 - listName", nominationTypeDto1.getListName(), equalTo("au_sa_death_benf"));
        assertThat("nomination type 0 - value", nominationTypeDto1.getValue(), equalTo("NOMN_AUTO_REVSNRY"));
        assertThat("nomination type 0 - isDependentOnly", nominationTypeDto1.isDependentOnly(), equalTo(true));
        assertThat("nomination type 0 - isSoleNominationOnly", nominationTypeDto1.isSoleNominationOnly(), equalTo(true));

        SuperNominationTypeDto nominationTypeDto2 = nominationList.get(1);
        assertThat("nomination type 1 - id", nominationTypeDto2.getId(), equalTo("2"));
        assertThat("nomination type 1 - label", nominationTypeDto2.getLabel() , equalTo("Non-lapsing nomination"));
        assertThat("nomination type 1 - intlId", nominationTypeDto2.getIntlId(), equalTo("nomn_nbind_sis"));
        assertThat("nomination type 1 - listName", nominationTypeDto2.getListName(), equalTo("au_sa_death_benf"));
        assertThat("nomination type 1 - value", nominationTypeDto2.getValue(), equalTo("NOMN_NBIND_SIS"));
        assertThat("nomination type 1 - isDependentOnly", nominationTypeDto2.isDependentOnly(), equalTo(false));
        assertThat("nomination type 1 - isSoleNominationOnly", nominationTypeDto2.isSoleNominationOnly(), equalTo(false));

        SuperNominationTypeDto nominationTypeDto3 = nominationList.get(2);
        assertThat("nomination type 2 - id", nominationTypeDto3.getId(), equalTo("4"));
        assertThat("nomination type 2 - label", nominationTypeDto3.getLabel() , equalTo("Trustee discretion"));
        assertThat("nomination type 2 - intlId", nominationTypeDto3.getIntlId(), equalTo("nomn_bind_nlaps_trustd"));
        assertThat("nomination type 2 - listName", nominationTypeDto3.getListName(), equalTo("au_sa_death_benf"));
        assertThat("nomination type 2 - value", nominationTypeDto3.getValue(), equalTo("NOMN_BIND_NLAPS_TRUSTD"));
        assertThat("nomination type 2 - isDependentOnly", nominationTypeDto3.isDependentOnly(), equalTo(false));
        assertThat("nomination type 2 - isSoleNominationOnly", nominationTypeDto3.isSoleNominationOnly(), equalTo(false));


    }

}
