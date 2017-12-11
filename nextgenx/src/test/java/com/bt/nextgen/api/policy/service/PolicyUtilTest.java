package com.bt.nextgen.api.policy.service;

import com.bt.nextgen.api.policy.model.BeneficiaryDto;
import com.bt.nextgen.api.policy.model.OwnerDto;
import com.bt.nextgen.api.policy.model.Person;
import com.bt.nextgen.api.policy.util.PolicyUtil;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class PolicyUtilTest {

    @Test
    public void testGetSortedWithNames() {
        List<Person> persons = new ArrayList<>();

        OwnerDto ownerDto1 = new OwnerDto();
        OwnerDto ownerDto2 = new OwnerDto();
        OwnerDto ownerDto3 = new OwnerDto();
        OwnerDto ownerDto4 = new OwnerDto();
        OwnerDto ownerDto5 = new OwnerDto();

        ownerDto1.setGivenName("Mark");
        ownerDto1.setLastName("Parker");
        ownerDto2.setGivenName("Steve");
        ownerDto2.setLastName("Smith");
        ownerDto3.setGivenName("Oberian");
        ownerDto4.setGivenName("John");
        ownerDto4.setLastName("Smith");
        ownerDto5.setGivenName("Steve");
        ownerDto5.setLastName("Adams");

        persons.add(ownerDto1);
        persons.add(ownerDto2);
        persons.add(ownerDto3);
        persons.add(ownerDto4);
        persons.add(ownerDto5);

        List<Person> sortedList = PolicyUtil.getSortedWithNames(persons);
        Assert.assertNotNull(sortedList);

        Person person1 = sortedList.get(0);
        Assert.assertEquals("John", person1.getGivenName());
        Person person2 = sortedList.get(1);
        Assert.assertEquals("Mark", person2.getGivenName());
        Person person3 = sortedList.get(2);
        Assert.assertEquals("Oberian", person3.getGivenName());
        Person person4 = sortedList.get(3);
        Assert.assertEquals("Steve", person4.getGivenName());
        Assert.assertEquals("Adams", person4.getLastName());
        Person person5 = sortedList.get(4);
        Assert.assertEquals("Steve", person5.getGivenName());
        Assert.assertEquals("Smith", person5.getLastName());
    }


    @Test
    public void testGetSortedWithContribution() {
        List<BeneficiaryDto> beneficiaries = new ArrayList<>();

        BeneficiaryDto beneficiaryDto1 = new BeneficiaryDto();
        beneficiaryDto1.setBeneficiaryContribution(new BigDecimal(70));
        beneficiaryDto1.setGivenName("Minnie");
        beneficiaryDto1.setLastName("Steve");
        BeneficiaryDto beneficiaryDto2 = new BeneficiaryDto();
        beneficiaryDto2.setBeneficiaryContribution(new BigDecimal(10));
        beneficiaryDto2.setGivenName("Jamie");
        beneficiaryDto2.setLastName("Steve");
        BeneficiaryDto beneficiaryDto3 = new BeneficiaryDto();
        beneficiaryDto3.setBeneficiaryContribution(new BigDecimal(10));
        beneficiaryDto3.setGivenName("Jamie");
        beneficiaryDto3.setLastName("Miny");
        BeneficiaryDto beneficiaryDto4 = new BeneficiaryDto();
        beneficiaryDto4.setBeneficiaryContribution(new BigDecimal(10));
        beneficiaryDto4.setGivenName("Abhinav");
        beneficiaryDto4.setLastName("Gupta");

        beneficiaries.add(beneficiaryDto1);
        beneficiaries.add(beneficiaryDto2);
        beneficiaries.add(beneficiaryDto3);
        beneficiaries.add(beneficiaryDto4);
        List<BeneficiaryDto> sortedList = PolicyUtil.getSortedWithContribution(beneficiaries);
        Assert.assertNotNull(sortedList);

        BeneficiaryDto beneficiaryDto = sortedList.get(0);
        Assert.assertEquals("Minnie", beneficiaryDto.getGivenName());
        Assert.assertEquals("Steve", beneficiaryDto.getLastName());
        Assert.assertEquals(new BigDecimal(70), beneficiaryDto.getBeneficiaryContribution());
        beneficiaryDto = sortedList.get(1);
        Assert.assertEquals("Abhinav", beneficiaryDto.getGivenName());
        Assert.assertEquals("Gupta", beneficiaryDto.getLastName());
        Assert.assertEquals(new BigDecimal(10), beneficiaryDto.getBeneficiaryContribution());
        beneficiaryDto = sortedList.get(2);
        Assert.assertEquals("Jamie", beneficiaryDto.getGivenName());
        Assert.assertEquals("Miny", beneficiaryDto.getLastName());
        Assert.assertEquals(new BigDecimal(10), beneficiaryDto.getBeneficiaryContribution());
        beneficiaryDto = sortedList.get(3);
        Assert.assertEquals("Jamie", beneficiaryDto.getGivenName());
        Assert.assertEquals("Steve", beneficiaryDto.getLastName());
        Assert.assertEquals(new BigDecimal(10), beneficiaryDto.getBeneficiaryContribution());
    }

    @Test
    public void testNextRenewalDateForCommencementDateAsToday() {
        DateTime commencementDate = new DateTime();
        commencementDate = commencementDate.withYear(commencementDate.getYear()).
                withMonthOfYear(commencementDate.getMonthOfYear()).withDayOfMonth(commencementDate.getDayOfMonth());
        DateTime nextRenewalDate = new DateTime();
        nextRenewalDate = nextRenewalDate.withYear(nextRenewalDate.getYear()).
                withMonthOfYear(nextRenewalDate.getMonthOfYear()).withDayOfMonth(nextRenewalDate.getDayOfMonth())
                .withMinuteOfHour(0).withSecondOfMinute(0).withHourOfDay(0).withMillisOfSecond(0);
        Assert.assertEquals("Next Renewal Date year should be next year:", nextRenewalDate.withYear(nextRenewalDate.getYear() + 1).withMinuteOfHour(0).
                withSecondOfMinute(0).withHourOfDay(0).withMillisOfSecond(0).toString(), PolicyUtil.setNextRenewalDate(commencementDate, nextRenewalDate));
    }

    @Test
    public void testNextRenewalDateForCommencementDateInPast() {
        DateTime commencementDate = new DateTime();
        commencementDate = commencementDate.withYear(2015).withMonthOfYear(12).withDayOfMonth(9);
        DateTime nextRenewalDate = new DateTime();
        nextRenewalDate = nextRenewalDate.withYear(2016).withMonthOfYear(12).withDayOfMonth(9).withMinuteOfHour(0).
                withSecondOfMinute(0).withHourOfDay(0).withMillisOfSecond(0);
        Assert.assertEquals("Year should be same as nextRenewalDateYear", "2016-12-09T00:00:00.000+11:00",
                PolicyUtil.setNextRenewalDate(commencementDate, nextRenewalDate));
    }

}
