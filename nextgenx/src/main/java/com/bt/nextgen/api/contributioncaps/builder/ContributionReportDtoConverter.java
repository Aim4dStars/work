package com.bt.nextgen.api.contributioncaps.builder;

import com.bt.nextgen.api.contributioncaps.model.ContributionReportDto;
import com.bt.nextgen.api.contributioncaps.model.ContributionSubtypeValuationDto;
import com.bt.nextgen.api.contributioncaps.model.MemberContributionCapValuationDto;
import com.bt.nextgen.core.reporting.ReportUtils;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationSubtype;
import com.bt.nextgen.service.integration.externalasset.builder.DateTimeConverter;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Converts  MemberContributionCap to ContributionReportDto model
 */
public final class ContributionReportDtoConverter {

    private ContributionReportDtoConverter()
    {
    }

    public static ContributionReportDto toContributionReportDto(MemberContributionCapValuationDto memberContributionCapValuationDto) {
        Map<String, BigDecimal> subCategories = new HashMap<>();
        ContributionReportDto contributionReportDto = new ContributionReportDto();
        contributionReportDto.setName(memberContributionCapValuationDto.getFirstName() +" "+ memberContributionCapValuationDto.getLastName());
        contributionReportDto.setBirthDate(setDateFormatToDisplayFormat(memberContributionCapValuationDto.getMemberContributionsCapDto().getDateOfBirth()));
        contributionReportDto.setConcAvailable((String) ReportUtils.toCurrencyString(memberContributionCapValuationDto.getConcessionalAvailableBalance()));
        contributionReportDto.setConcCap((String)ReportUtils.toCurrencyString(memberContributionCapValuationDto.getMemberContributionsCapDto().getConcessionalCap()));
        contributionReportDto.setConcTotal((String)ReportUtils.toCurrencyString(memberContributionCapValuationDto.getConcessionalTotal()));
        contributionReportDto.setNonConcAvailable((String)ReportUtils.toCurrencyString(memberContributionCapValuationDto.getNonConcessionalAvailableBalance()));
        contributionReportDto.setNonConcCap((String)ReportUtils.toCurrencyString(memberContributionCapValuationDto.getMemberContributionsCapDto().getNonConcessionalCap()));
        contributionReportDto.setNonConcTotal((String)ReportUtils.toCurrencyString(memberContributionCapValuationDto.getNonConcessionalTotal()));
        contributionReportDto.setOtherTotal((String)ReportUtils.toCurrencyString(memberContributionCapValuationDto.getOtherContributionsTotal()));
        contributionReportDto.setTotalContributions((String)ReportUtils.toCurrencyString(memberContributionCapValuationDto.getTotalContributions()));
        contributionReportDto.setAge(String.valueOf(memberContributionCapValuationDto.getMemberContributionsCapDto().getAge()));
        List<ContributionSubtypeValuationDto> contributionSubtypeValuationDtos = memberContributionCapValuationDto.getContributionSubtypeValuationDto();
        if (CollectionUtils.isNotEmpty(contributionSubtypeValuationDtos)){
            for (ContributionSubtypeValuationDto contributionSubTypeValue: contributionSubtypeValuationDtos) {
                subCategories.put(contributionSubTypeValue.getContributionSubtype(), contributionSubTypeValue.getAmount());
            }
            setAllSubCategories(subCategories,contributionReportDto);
            setAllSubCategoriesLabels(contributionReportDto);
        }
        return contributionReportDto;
    }

    /**
     * Convert date in string format "2015-07-15" to "15 Jul 2015"
     * @param inputDate input date to be converted
     */
    private static String setDateFormatToDisplayFormat(String inputDate) {
        String toDate = null;
        if (org.apache.commons.lang3.StringUtils.isNotBlank(inputDate) && inputDate.length()>9) {
            String dateToConvert  = inputDate.substring(0,10);
            DateTimeConverter dateTimeConverter = new DateTimeConverter();
            DateTime dateTime = dateTimeConverter.convert(dateToConvert);
            toDate = ApiFormatter.asShortDate(dateTime);
        }
        return toDate;
    }


    /**
     * Contribution report - sub categories display name set
     * @param contributionReportDto
     */
    private static void setAllSubCategoriesLabels(ContributionReportDto contributionReportDto) {
        List<String> concSubCategoriesLabel = new ArrayList<>();
        concSubCategoriesLabel.add(CashCategorisationSubtype.EMPLOYER.getName());
        concSubCategoriesLabel.add(CashCategorisationSubtype.PERSONAL_CONCESSIONAL.getName());
        concSubCategoriesLabel.add(CashCategorisationSubtype.OTHER_THIRD_PARTY.getName());
        concSubCategoriesLabel.add(CashCategorisationSubtype.FOREIGN_SUPER_ASSESSABLE.getName());
        List<String> nonConcSubCategoriesLabel = new ArrayList<>();
        nonConcSubCategoriesLabel.add(CashCategorisationSubtype.PERSONAL_NON_CONCESSIONAL.getName());
        nonConcSubCategoriesLabel.add(CashCategorisationSubtype.SPOUSE_CHILD_CONTRIBUTION.getName());
        nonConcSubCategoriesLabel.add(CashCategorisationSubtype.FOREIGN_SUPER_NON_ASSESSABLE.getName());
        List<String> otherSubCategoriesLabel = new ArrayList<>();
        otherSubCategoriesLabel.add(CashCategorisationSubtype.GOVT_CO_CONTRIBUTION.getName());
        otherSubCategoriesLabel.add(CashCategorisationSubtype.PERSONAL_INJURY_ELECTION.getName());
        otherSubCategoriesLabel.add(CashCategorisationSubtype.GCT_SMALL_BUS_15YEAR_EXEMPTION.getName());
        otherSubCategoriesLabel.add(CashCategorisationSubtype.GCT_SMALL_BUS_RETIREMENT_EXEMPTION.getName());
        contributionReportDto.setConcSubCategoriesLabel(concSubCategoriesLabel);
        contributionReportDto.setNonConcSubCategoriesLabel(nonConcSubCategoriesLabel);
        contributionReportDto.setOtherSubCategoriesLabel(otherSubCategoriesLabel);

    }

    /**
     * Contribution report - sub categories value set
     * @param subCategories
     * @param contributionReportDto
     */

    private static void setAllSubCategories(Map<String, BigDecimal> subCategories, ContributionReportDto contributionReportDto) {
        List<String> concSubCategories = new ArrayList<>();
        concSubCategories.add((String)ReportUtils.toCurrencyString(subCategories.get(CashCategorisationSubtype.EMPLOYER.getAvaloqInternalId())));
        concSubCategories.add((String)ReportUtils.toCurrencyString(subCategories.get(CashCategorisationSubtype.PERSONAL_CONCESSIONAL.getAvaloqInternalId())));
        concSubCategories.add((String)ReportUtils.toCurrencyString(subCategories.get(CashCategorisationSubtype.OTHER_THIRD_PARTY.getAvaloqInternalId())));
        concSubCategories.add((String)ReportUtils.toCurrencyString(subCategories.get(CashCategorisationSubtype.FOREIGN_SUPER_ASSESSABLE.getAvaloqInternalId())));
        List<String> nonConcSubCategories = new ArrayList<>();
        nonConcSubCategories.add((String)ReportUtils.toCurrencyString(subCategories.get(CashCategorisationSubtype.PERSONAL_NON_CONCESSIONAL.getAvaloqInternalId())));
        nonConcSubCategories.add((String)ReportUtils.toCurrencyString(subCategories.get(CashCategorisationSubtype.SPOUSE_CHILD_CONTRIBUTION.getAvaloqInternalId())));
        nonConcSubCategories.add((String)ReportUtils.toCurrencyString(subCategories.get(CashCategorisationSubtype.FOREIGN_SUPER_NON_ASSESSABLE.getAvaloqInternalId())));
        List<String> otherSubCategories = new ArrayList<>();
        otherSubCategories.add((String)ReportUtils.toCurrencyString(subCategories.get(CashCategorisationSubtype.GOVT_CO_CONTRIBUTION.getAvaloqInternalId())));
        otherSubCategories.add((String)ReportUtils.toCurrencyString(subCategories.get(CashCategorisationSubtype.PERSONAL_INJURY_ELECTION.getAvaloqInternalId())));
        otherSubCategories.add((String)ReportUtils.toCurrencyString(subCategories.get(CashCategorisationSubtype.GCT_SMALL_BUS_15YEAR_EXEMPTION.getAvaloqInternalId())));
        otherSubCategories.add((String)ReportUtils.toCurrencyString(subCategories.get(CashCategorisationSubtype.GCT_SMALL_BUS_RETIREMENT_EXEMPTION.getAvaloqInternalId())));
        contributionReportDto.setConcSubCategories(concSubCategories);
        contributionReportDto.setNonConcSubCategories(nonConcSubCategories);
        contributionReportDto.setOtherSubCategories(otherSubCategories);
    }

}
