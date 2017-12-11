package com.bt.nextgen.api.contributioncaps.builder;

import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.integration.contributioncaps.model.MemberContributionsCap;
import com.bt.nextgen.api.contributioncaps.model.MemberContributionsCapDto;
import com.bt.nextgen.api.transactionhistory.model.SmsfMembersDto;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Converts member smsf contribution cap limit integration model to dto
 */
public final class MemberContributionCapConverter
{
	private MemberContributionCapConverter()
	{
	}

	public static List<MemberContributionsCapDto> toMemberContributionDtoList(List<MemberContributionsCap> memberCapList, List<SmsfMembersDto> smsfList)
	{
		List<MemberContributionsCapDto> dtoList = new ArrayList<>();

		DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");

		for (MemberContributionsCap memberCap : memberCapList)
		{
			// Tactical fix here -- avaloq service is incorrectly returning a blank top_head element
			if (memberCap.getPersonId() != null)
			{
				MemberContributionsCapDto memberCapDto = new MemberContributionsCapDto();
				memberCapDto.setPersonId(memberCap.getPersonId());

				memberCapDto.setAge(Double.valueOf(memberCap.getAge()).intValue());
				memberCapDto.setConcessionalCap(memberCap.getConcessionalCap());

				if (memberCap.getDateOfBirth() != null)
					memberCapDto.setDateOfBirth(memberCap.getDateOfBirth().toString(dtf));

				if (memberCap.getFinancialYear() != null)
					memberCapDto.setFinancialYear(memberCap.getFinancialYear().toString(dtf));

				memberCapDto.setNonConcessionalCap(memberCap.getNonConcessionalCap());

				dtoList.add(memberCapDto);
			}
		}
		sortListByFullName(dtoList, smsfList);
		

		return dtoList;
	}
	
	
	private static void sortListByFullName(List<MemberContributionsCapDto> contriCapList , final List<SmsfMembersDto> smsfList)
	{
		
		//Sorting by full name
		Collections.sort(contriCapList, new Comparator <MemberContributionsCapDto>()
		{
			public int compare(MemberContributionsCapDto member1, MemberContributionsCapDto member2) {
					String member1FullName = createNameForSorting(member1,smsfList);
					String member2FullName = createNameForSorting(member2,smsfList);

					if (member1FullName != null && member2FullName != null) {
						return member1FullName.toUpperCase().compareTo(member2FullName.toUpperCase());
					}
					else if (member1FullName == null) {
						return -1;
					}
					else if (member2FullName == null) {
						return 1;
					}
					else {
						return 0;
					}
			}
			
		});
	}
	
	private static String createNameForSorting(MemberContributionsCapDto member, List <SmsfMembersDto> smsfList)
	{
		StringBuilder memberFullName = new StringBuilder();
		if (smsfList != null)
		{
			for (SmsfMembersDto smsfDto : smsfList)
			{
				if (smsfDto.getPersonId().equals(member.getPersonId()))
				{
					memberFullName.append(smsfDto.getFirstName() != null ? smsfDto.getFirstName() : Constants.EMPTY_STRING);
					memberFullName.append(smsfDto.getLastName() != null ? smsfDto.getLastName() : Constants.EMPTY_STRING);
				}
			}
		}
		return memberFullName.toString();
	}
}
