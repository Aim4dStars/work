package com.bt.nextgen.api.beneficiary.service;

import com.bt.nextgen.api.beneficiary.builder.BeneficiariesDetailsConverter;
import com.bt.nextgen.api.beneficiary.model.Beneficiary;
import com.bt.nextgen.service.avaloq.beneficiary.*;
import com.bt.nextgen.service.integration.domain.Gender;
import com.btfin.abs.trxservice.bp.v1_0.BpReq;
import com.btfin.abs.trxservice.bp.v1_0.SABenefDet;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Created by L067218 on 28/07/2016.
 */
public class BeneficiariesDetailsConverterTest {

    private BeneficiariesDetailsConverter beneficiariesDetailsConverter = new BeneficiariesDetailsConverter();

    SaveBeneficiariesDetails request;

    @Before
    public void init() {
        request = new SaveBeneficiariesDetailsImpl();

        List<BeneficiaryDetails> benefList = new ArrayList<>();

        BeneficiaryDetails benef1 = new BeneficiaryDetailsImpl();
        benef1.setGender(Gender.MALE);
        benef1.setLastName("Richard");
        benef1.setFirstName("Dennis");
        benef1.setNominationTypeinAvaloqFormat("nomn_bind_nlaps_trustd");
        benef1.setDateOfBirth(new DateTime("2006-05-11"));
        benef1.setAllocationPercent(new BigDecimal(100));
        benef1.setEmail("abcd@gmail.com");
        benef1.setPhoneNumber("123456789");
        benef1.setRelationshipType(RelationshipType.FINANCIAL_DEPENDENT);
        benefList.add(benef1);

        request.setAccountKey(new com.bt.nextgen.api.account.v2.model.AccountKey("15E67EC04FBD44111C4EC74731CF06E74D5C8FD5E765FAB3"));
        request.setModificationSeq("1");
        request.setBeneficiaries(benefList);
    }

    @Test
    public void testMakeBeneficiariesRequest(){

        BpReq bpReq = beneficiariesDetailsConverter.makeBeneficiariesRequest(request);
        assertThat("BpRequest is not null", bpReq, is(notNullValue()));
        assertThat("Number of beneficiaries", bpReq.getData().getAction().getUpdSaBenefDet().size(), equalTo(1));

        SABenefDet benefDetail1= bpReq.getData().getAction().getUpdSaBenefDet().get(0).getSaBenefDet().get(0);

        assertThat("Total allocation percent of Beneficiary 1:", benefDetail1.getPct().getVal(), is(new BigDecimal(100)));
        assertThat("Nominationtype of Beneficiary 1:", benefDetail1.getNomnType().getExtlVal().getVal(), is("nomn_bind_nlaps_trustd"));
        assertThat("RelationshipType of Beneficiary 1:", benefDetail1.getRelType().getExtlVal().getVal(), is("fin_dep"));
        assertThat("Phone no of Beneficiary 1:", benefDetail1.getCtactNr().getVal(), is("123456789"));
        assertThat("Email of Beneficiary 1:", benefDetail1.getEmail().getVal(), is("abcd@gmail.com"));
        assertThat("FirstName of Beneficiary 1:", benefDetail1.getFirstName().getVal(), is("Dennis"));
        assertThat("LastName of Beneficiary 1:", benefDetail1.getLastName().getVal(), is("Richard"));
        assertThat("Gender of Beneficiary 1:", benefDetail1.getGender().getExtlVal().getVal(), is("male"));
        assertThat("DateOfBirth of Beneficiary 1:", benefDetail1.getDob().getVal().toString(), is("2006-05-11"));

    }
}
