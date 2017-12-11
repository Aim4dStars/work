package com.bt.nextgen.reports.account;

import com.bt.nextgen.api.contributioncaps.model.ContributionReportDto;
import com.bt.nextgen.api.contributioncaps.model.ContributionSubtypeValuationDto;
import com.bt.nextgen.api.contributioncaps.model.MemberContributionCapValuationDto;
import com.bt.nextgen.api.contributioncaps.model.MemberContributionsCapDto;
import com.bt.nextgen.api.contributioncaps.service.AccountContributionSummaryDtoService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationSubtype;
import com.bt.nextgen.service.integration.cashcategorisation.model.ContributionClassification;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)

public class ContributionPdfReportTest{

    private static final String PARAM_DATE = "date";
    private static final String ACCOUNTID = "account-id";

    @InjectMocks
    private ContributionPdfReport report;

    @Mock
    private AccountContributionSummaryDtoService accountContributionSummaryDtoService;

    @Mock
    private ContentDtoService contentService;

    final Map<String, Object> params = new HashMap<String, Object>();

    @Before
    public void init() {
        DateTime dateTime = new DateTime();
        params.put(PARAM_DATE, dateTime.getYear() + "-07-01");
        params.put(ACCOUNTID, "E872817000501F2BA21043CB70EB82F2DBFADCD605F785A2");

        List<MemberContributionCapValuationDto> memberContributionCapValuationDtos = new ArrayList<>();

        List<ContributionSubtypeValuationDto> contributionSubtypeValuationDtos = new ArrayList<>();
        ContributionSubtypeValuationDto contributionSubtypeValuationDto = new ContributionSubtypeValuationDto();
        contributionSubtypeValuationDto.setContributionSubtype(CashCategorisationSubtype.EMPLOYER.getAvaloqInternalId());
        contributionSubtypeValuationDto.setContributionClassification(ContributionClassification.CONCESSIONAL.getAvaloqInternalId());
        contributionSubtypeValuationDto.setAmount(new BigDecimal(14000));
        contributionSubtypeValuationDtos.add(contributionSubtypeValuationDto);
        contributionSubtypeValuationDto = new ContributionSubtypeValuationDto();
        contributionSubtypeValuationDto.setContributionSubtype(CashCategorisationSubtype.PERSONAL_CONCESSIONAL.getAvaloqInternalId());
        contributionSubtypeValuationDto.setContributionClassification(ContributionClassification.CONCESSIONAL.getAvaloqInternalId());
        contributionSubtypeValuationDto.setAmount(BigDecimal.ZERO);
        contributionSubtypeValuationDtos.add(contributionSubtypeValuationDto);
        contributionSubtypeValuationDto = new ContributionSubtypeValuationDto();
        contributionSubtypeValuationDto.setContributionSubtype(CashCategorisationSubtype.OTHER_THIRD_PARTY.getAvaloqInternalId());
        contributionSubtypeValuationDto.setContributionClassification(ContributionClassification.CONCESSIONAL.getAvaloqInternalId());
        contributionSubtypeValuationDto.setAmount(BigDecimal.ZERO);
        contributionSubtypeValuationDtos.add(contributionSubtypeValuationDto);
        contributionSubtypeValuationDto = new ContributionSubtypeValuationDto();
        contributionSubtypeValuationDto.setContributionSubtype(CashCategorisationSubtype.FOREIGN_SUPER_ASSESSABLE.getAvaloqInternalId());
        contributionSubtypeValuationDto.setContributionClassification(ContributionClassification.CONCESSIONAL.getAvaloqInternalId());
        contributionSubtypeValuationDto.setAmount(BigDecimal.ZERO);
        contributionSubtypeValuationDtos.add(contributionSubtypeValuationDto);


        contributionSubtypeValuationDto = new ContributionSubtypeValuationDto();
        contributionSubtypeValuationDto.setContributionSubtype(CashCategorisationSubtype.PERSONAL_NON_CONCESSIONAL.getAvaloqInternalId());
        contributionSubtypeValuationDto.setContributionClassification(ContributionClassification.NON_CONCESSIONAL.getAvaloqInternalId());
        contributionSubtypeValuationDto.setAmount(BigDecimal.ZERO);
        contributionSubtypeValuationDtos.add(contributionSubtypeValuationDto);
        contributionSubtypeValuationDto = new ContributionSubtypeValuationDto();
        contributionSubtypeValuationDto.setContributionSubtype(CashCategorisationSubtype.SPOUSE_CHILD_CONTRIBUTION.getAvaloqInternalId());
        contributionSubtypeValuationDto.setContributionClassification(ContributionClassification.NON_CONCESSIONAL.getAvaloqInternalId());
        contributionSubtypeValuationDto.setAmount(BigDecimal.ZERO);
        contributionSubtypeValuationDtos.add(contributionSubtypeValuationDto);
        contributionSubtypeValuationDto = new ContributionSubtypeValuationDto();
        contributionSubtypeValuationDto.setContributionSubtype(CashCategorisationSubtype.FOREIGN_SUPER_NON_ASSESSABLE.getAvaloqInternalId());
        contributionSubtypeValuationDto.setContributionClassification(ContributionClassification.NON_CONCESSIONAL.getAvaloqInternalId());
        contributionSubtypeValuationDto.setAmount(BigDecimal.ZERO);
        contributionSubtypeValuationDtos.add(contributionSubtypeValuationDto);


        contributionSubtypeValuationDto = new ContributionSubtypeValuationDto();
        contributionSubtypeValuationDto.setContributionSubtype(CashCategorisationSubtype.GOVT_CO_CONTRIBUTION.getAvaloqInternalId());
        contributionSubtypeValuationDto.setContributionClassification(ContributionClassification.OTHER.getAvaloqInternalId());
        contributionSubtypeValuationDto.setAmount(new BigDecimal(13780));
        contributionSubtypeValuationDtos.add(contributionSubtypeValuationDto);
        contributionSubtypeValuationDto = new ContributionSubtypeValuationDto();
        contributionSubtypeValuationDto.setContributionSubtype(CashCategorisationSubtype.PERSONAL_INJURY_ELECTION.getAvaloqInternalId());
        contributionSubtypeValuationDto.setContributionClassification(ContributionClassification.OTHER.getAvaloqInternalId());
        contributionSubtypeValuationDto.setAmount(BigDecimal.ZERO);
        contributionSubtypeValuationDtos.add(contributionSubtypeValuationDto);
        contributionSubtypeValuationDto = new ContributionSubtypeValuationDto();
        contributionSubtypeValuationDto.setContributionSubtype(CashCategorisationSubtype.GCT_SMALL_BUS_15YEAR_EXEMPTION.getAvaloqInternalId());
        contributionSubtypeValuationDto.setContributionClassification(ContributionClassification.OTHER.getAvaloqInternalId());
        contributionSubtypeValuationDto.setAmount(new BigDecimal(22500));
        contributionSubtypeValuationDtos.add(contributionSubtypeValuationDto);
        contributionSubtypeValuationDto = new ContributionSubtypeValuationDto();
        contributionSubtypeValuationDto.setContributionSubtype(CashCategorisationSubtype.GCT_SMALL_BUS_RETIREMENT_EXEMPTION.getAvaloqInternalId());
        contributionSubtypeValuationDto.setContributionClassification(ContributionClassification.OTHER.getAvaloqInternalId());
        contributionSubtypeValuationDto.setAmount(BigDecimal.ZERO);
        contributionSubtypeValuationDtos.add(contributionSubtypeValuationDto);

        MemberContributionsCapDto memberContributionsCapDto = new MemberContributionsCapDto();
        memberContributionsCapDto.setPersonId("69768");
        memberContributionsCapDto.setAge(52);
        memberContributionsCapDto.setConcessionalCap(new BigDecimal(35000));
        memberContributionsCapDto.setDateOfBirth("1962-10-19");
        memberContributionsCapDto.setFinancialYear("2015-07-19");
        memberContributionsCapDto.setNonConcessionalCap(new BigDecimal(180000));


        MemberContributionCapValuationDto memberContributionCapValuationDto = new MemberContributionCapValuationDto();
        memberContributionCapValuationDto.setFirstName("Bill");
        memberContributionCapValuationDto.setContributionSubtypeValuationDto(contributionSubtypeValuationDtos);
        memberContributionCapValuationDto.setLastName("Dias");
        memberContributionCapValuationDto.setMemberContributionsCapDto(memberContributionsCapDto);
        memberContributionCapValuationDto.setPersonId("69768");


        memberContributionCapValuationDtos.add(memberContributionCapValuationDto);

        Mockito.when(accountContributionSummaryDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class),
                Mockito.any(ServiceErrors.class))).thenReturn(memberContributionCapValuationDtos);

        ContentDto infoContent = new ContentDto("Ins-IP-0287", "Ins-IP-0287-value");
        ContentKey infoContentKey = new ContentKey("Ins-IP-0287");
        Mockito.when(contentService.find(Mockito.eq(infoContentKey), Mockito.any(ServiceErrors.class))).thenReturn(infoContent);
        //
        ContentDto infoContent88 = new ContentDto("Ins-IP-0088", "Ins-IP-0088-value");
        ContentKey infoContentKey88 = new ContentKey("Ins-IP-0088");
        Mockito.when(contentService.find(Mockito.eq(infoContentKey88), Mockito.any(ServiceErrors.class))).thenReturn(infoContent88);

        ContentDto infoContent321 = new ContentDto("Ins-IP-0321", "Ins-IP-0321-value");
        ContentKey infoContentKey321 = new ContentKey("Ins-IP-0321");
        Mockito.when(contentService.find(Mockito.eq(infoContentKey321), Mockito.any(ServiceErrors.class))).thenReturn(infoContent321);

        //
        ContentDto disclaimerContent = new ContentDto("DS-IP-0071", "DS-IP-0071-value");
        ContentKey disclaimerContentKey = new ContentKey("DS-IP-0071");
        Mockito.when(contentService.find(Mockito.eq(disclaimerContentKey), Mockito.any(ServiceErrors.class))).thenReturn(disclaimerContent);
    }

    @Test
    public void getContributionReportData() {
        List <ContributionReportDto> contributionReportDtos = report.getContributionReport(params, new HashMap<String, Object>());

        assertEquals(contributionReportDtos.size(),1);
        ContributionReportDto contributionReportDto = contributionReportDtos.get(0);
        assertEquals(contributionReportDto.getName(), "Bill Dias");
        assertEquals(contributionReportDto.getAge(), "52");
        assertEquals(contributionReportDto.getBirthDate(), "19 Oct 1962");
        assertEquals(contributionReportDto.getContributionTypeLabel(), "Contribution type");
        assertEquals(contributionReportDto.getTotalContributions(), "50,280.00");
        assertEquals(contributionReportDto.getConcTotal(), "14,000.00");
        assertEquals(contributionReportDto.getNonConcTotal(), "0.00");
        assertEquals(contributionReportDto.getOtherTotal(), "36,280.00");
        assertEquals(contributionReportDto.getConcCap(), "35,000.00");
        assertEquals(contributionReportDto.getConcAvailable(), "21,000.00");
        assertEquals(contributionReportDto.getNonConcCap(), "180,000.00");
        assertEquals(contributionReportDto.getNonConcAvailable(), "180,000.00");

        //Order of the displayid
        assertEquals(contributionReportDto.getConcSubCategoriesLabel().size(),4);
        assertEquals(contributionReportDto.getConcSubCategoriesLabel().get(0),CashCategorisationSubtype.EMPLOYER.getName());
        assertEquals(contributionReportDto.getConcSubCategoriesLabel().get(1),CashCategorisationSubtype.PERSONAL_CONCESSIONAL.getName());
        assertEquals(contributionReportDto.getConcSubCategoriesLabel().get(2),CashCategorisationSubtype.OTHER_THIRD_PARTY.getName());
        assertEquals(contributionReportDto.getConcSubCategoriesLabel().get(3),CashCategorisationSubtype.FOREIGN_SUPER_ASSESSABLE.getName());

        assertEquals(contributionReportDto.getNonConcSubCategoriesLabel().size(),3);
        assertEquals(contributionReportDto.getNonConcSubCategoriesLabel().get(0),CashCategorisationSubtype.PERSONAL_NON_CONCESSIONAL.getName());
        assertEquals(contributionReportDto.getNonConcSubCategoriesLabel().get(1),CashCategorisationSubtype.SPOUSE_CHILD_CONTRIBUTION.getName());
        assertEquals(contributionReportDto.getNonConcSubCategoriesLabel().get(2),CashCategorisationSubtype.FOREIGN_SUPER_NON_ASSESSABLE.getName());

        assertEquals(contributionReportDto.getOtherSubCategoriesLabel().size(),4);
        assertEquals(contributionReportDto.getOtherSubCategoriesLabel().get(0),CashCategorisationSubtype.GOVT_CO_CONTRIBUTION.getName());
        assertEquals(contributionReportDto.getOtherSubCategoriesLabel().get(1),CashCategorisationSubtype.PERSONAL_INJURY_ELECTION.getName());
        assertEquals(contributionReportDto.getOtherSubCategoriesLabel().get(2),CashCategorisationSubtype.GCT_SMALL_BUS_15YEAR_EXEMPTION.getName());
        assertEquals(contributionReportDto.getOtherSubCategoriesLabel().get(3),CashCategorisationSubtype.GCT_SMALL_BUS_RETIREMENT_EXEMPTION.getName());

        assertEquals(contributionReportDto.getConcSubCategories().size(),4);
        assertEquals(contributionReportDto.getConcSubCategories().get(0),"14,000.00");
        assertEquals(contributionReportDto.getConcSubCategories().get(1),"0.00");
        assertEquals(contributionReportDto.getConcSubCategories().get(2),"0.00");
        assertEquals(contributionReportDto.getConcSubCategories().get(3),"0.00");

        assertEquals(contributionReportDto.getNonConcSubCategories().size(),3);
        assertEquals(contributionReportDto.getNonConcSubCategories().get(0),"0.00");
        assertEquals(contributionReportDto.getNonConcSubCategories().get(1),"0.00");
        assertEquals(contributionReportDto.getNonConcSubCategories().get(2),"0.00");

        assertEquals(contributionReportDto.getOtherSubCategories().size(),4);
        assertEquals(contributionReportDto.getOtherSubCategories().get(0),"13,780.00");
        assertEquals(contributionReportDto.getOtherSubCategories().get(1),"0.00");
        assertEquals(contributionReportDto.getOtherSubCategories().get(2),"22,500.00");
        assertEquals(contributionReportDto.getOtherSubCategories().get(3),"0.00");

    }

    @Test
    public void getfinancialYear() {
        DateTime dateTime = new DateTime();
        int year = dateTime.getYear();
        assertThat(report.getFinancialYear(params), equalTo("FY "+ year + " / " + (year + 1)));
    }

    @Test
    public void getInfoMessage() {
        // getContributionsDto
        assertThat(report.getInfoMessage(params, new HashMap<String, Object>()), equalTo("Ins-IP-0287-value"));
    }

    @Test
    public void getInfoMessageWithNoMembers() {
        // getContributionsDto
        List<MemberContributionCapValuationDto> memberContributionCapValuationDtos = new ArrayList<>();
        Mockito.when(accountContributionSummaryDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class),
                Mockito.any(ServiceErrors.class))).thenReturn(memberContributionCapValuationDtos);
        assertThat(report.getInfoMessage(params, new HashMap<String, Object>()), equalTo("Ins-IP-0321-value"));
    }

    @Test
    public void getInfoMessageWithMembersAndNoContributions() {
        // getContributionsDto
        List<MemberContributionCapValuationDto> memberContributionCapValuationDtosWithNoContributions  = getContributionsWithMembersAndNoContributions();
        Mockito.when(accountContributionSummaryDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class),
                Mockito.any(ServiceErrors.class))).thenReturn(memberContributionCapValuationDtosWithNoContributions);
        assertThat(report.getInfoMessage(params, new HashMap<String, Object>()), equalTo("Ins-IP-0088-value"));
    }


    @Test
    public void getDisclaimer() {
        assertThat(report.getDisclaimer(), equalTo("DS-IP-0071-value"));
    }

    @Test
    public void getReportType() {
        assertThat(report.getReportType(new HashMap<String, Object>(), new HashMap<String, Object>()), equalTo("Contribution report"));
    }

    @Test
    public void getFinancialYearLabel() {
        Map<String, Object> localParams = new HashMap<String, Object>();
        //
        localParams.put(PARAM_DATE, new DateTime(2017, 3, 9, 12, 0, 0, 0).getYear() + "-07-01");
        assertThat(report.getFinancialYearLabel(localParams), equalTo("FY 2017/2018"));
        //
        localParams.clear();
        localParams.put(PARAM_DATE, new DateTime(1999, 3, 9, 12, 0, 0, 0).getYear() + "-07-01");
        assertThat(report.getFinancialYearLabel(localParams), equalTo("FY 1999/2000"));
        //
        localParams.clear();
        localParams.put(PARAM_DATE, new DateTime(2050, 3, 9, 12, 0, 0, 0).getYear() + "-07-01");
        assertThat(report.getFinancialYearLabel(localParams), equalTo("FY 2050/2051"));
    }

    @Test
    public void hasAtLeastOneContribution() {
        // init
        List<ContributionReportDto> contributionReportDtos = new ArrayList<ContributionReportDto>();
        ContributionReportDto dto = new ContributionReportDto();
        contributionReportDtos.add(dto);
        // null case
        assertThat(report.hasAtLeastOneContribution(null), equalTo(false));
        // has members, has contributions
        dto.setTotalContributions("1.00");
        assertThat(report.hasAtLeastOneContribution(contributionReportDtos), equalTo(true));
        // has members, has no contributions
        dto.setTotalContributions("0.00");
        assertThat(report.hasAtLeastOneContribution(contributionReportDtos), equalTo(false));
    }

    private List<MemberContributionCapValuationDto> getContributionsWithMembersAndNoContributions(){
        List<MemberContributionCapValuationDto> memberContributionCapValuationDtosWithNoContributions = new ArrayList<>();
        List<ContributionSubtypeValuationDto> contributionSubtypeValuationDtos1 = new ArrayList<>();
        ContributionSubtypeValuationDto contributionSubtypeValuationDto = new ContributionSubtypeValuationDto();
        contributionSubtypeValuationDto.setContributionSubtype(CashCategorisationSubtype.EMPLOYER.getAvaloqInternalId());
        contributionSubtypeValuationDto.setContributionClassification(ContributionClassification.CONCESSIONAL.getAvaloqInternalId());
        contributionSubtypeValuationDto.setAmount(BigDecimal.ZERO);
        contributionSubtypeValuationDtos1.add(contributionSubtypeValuationDto);
        contributionSubtypeValuationDto = new ContributionSubtypeValuationDto();
        contributionSubtypeValuationDto.setContributionSubtype(CashCategorisationSubtype.PERSONAL_CONCESSIONAL.getAvaloqInternalId());
        contributionSubtypeValuationDto.setContributionClassification(ContributionClassification.CONCESSIONAL.getAvaloqInternalId());
        contributionSubtypeValuationDto.setAmount(BigDecimal.ZERO);
        contributionSubtypeValuationDtos1.add(contributionSubtypeValuationDto);
        contributionSubtypeValuationDto = new ContributionSubtypeValuationDto();
        contributionSubtypeValuationDto.setContributionSubtype(CashCategorisationSubtype.OTHER_THIRD_PARTY.getAvaloqInternalId());
        contributionSubtypeValuationDto.setContributionClassification(ContributionClassification.CONCESSIONAL.getAvaloqInternalId());
        contributionSubtypeValuationDto.setAmount(BigDecimal.ZERO);
        contributionSubtypeValuationDtos1.add(contributionSubtypeValuationDto);
        contributionSubtypeValuationDto = new ContributionSubtypeValuationDto();
        contributionSubtypeValuationDto.setContributionSubtype(CashCategorisationSubtype.FOREIGN_SUPER_ASSESSABLE.getAvaloqInternalId());
        contributionSubtypeValuationDto.setContributionClassification(ContributionClassification.CONCESSIONAL.getAvaloqInternalId());
        contributionSubtypeValuationDto.setAmount(BigDecimal.ZERO);
        contributionSubtypeValuationDtos1.add(contributionSubtypeValuationDto);


        contributionSubtypeValuationDto = new ContributionSubtypeValuationDto();
        contributionSubtypeValuationDto.setContributionSubtype(CashCategorisationSubtype.PERSONAL_NON_CONCESSIONAL.getAvaloqInternalId());
        contributionSubtypeValuationDto.setContributionClassification(ContributionClassification.NON_CONCESSIONAL.getAvaloqInternalId());
        contributionSubtypeValuationDto.setAmount(BigDecimal.ZERO);
        contributionSubtypeValuationDtos1.add(contributionSubtypeValuationDto);
        contributionSubtypeValuationDto = new ContributionSubtypeValuationDto();
        contributionSubtypeValuationDto.setContributionSubtype(CashCategorisationSubtype.SPOUSE_CHILD_CONTRIBUTION.getAvaloqInternalId());
        contributionSubtypeValuationDto.setContributionClassification(ContributionClassification.NON_CONCESSIONAL.getAvaloqInternalId());
        contributionSubtypeValuationDto.setAmount(BigDecimal.ZERO);
        contributionSubtypeValuationDtos1.add(contributionSubtypeValuationDto);
        contributionSubtypeValuationDto = new ContributionSubtypeValuationDto();
        contributionSubtypeValuationDto.setContributionSubtype(CashCategorisationSubtype.FOREIGN_SUPER_NON_ASSESSABLE.getAvaloqInternalId());
        contributionSubtypeValuationDto.setContributionClassification(ContributionClassification.NON_CONCESSIONAL.getAvaloqInternalId());
        contributionSubtypeValuationDto.setAmount(BigDecimal.ZERO);
        contributionSubtypeValuationDtos1.add(contributionSubtypeValuationDto);


        contributionSubtypeValuationDto = new ContributionSubtypeValuationDto();
        contributionSubtypeValuationDto.setContributionSubtype(CashCategorisationSubtype.GOVT_CO_CONTRIBUTION.getAvaloqInternalId());
        contributionSubtypeValuationDto.setContributionClassification(ContributionClassification.OTHER.getAvaloqInternalId());
        contributionSubtypeValuationDto.setAmount(BigDecimal.ZERO);
        contributionSubtypeValuationDtos1.add(contributionSubtypeValuationDto);
        contributionSubtypeValuationDto = new ContributionSubtypeValuationDto();
        contributionSubtypeValuationDto.setContributionSubtype(CashCategorisationSubtype.PERSONAL_INJURY_ELECTION.getAvaloqInternalId());
        contributionSubtypeValuationDto.setContributionClassification(ContributionClassification.OTHER.getAvaloqInternalId());
        contributionSubtypeValuationDto.setAmount(BigDecimal.ZERO);
        contributionSubtypeValuationDtos1.add(contributionSubtypeValuationDto);
        contributionSubtypeValuationDto = new ContributionSubtypeValuationDto();
        contributionSubtypeValuationDto.setContributionSubtype(CashCategorisationSubtype.GCT_SMALL_BUS_15YEAR_EXEMPTION.getAvaloqInternalId());
        contributionSubtypeValuationDto.setContributionClassification(ContributionClassification.OTHER.getAvaloqInternalId());
        contributionSubtypeValuationDto.setAmount(BigDecimal.ZERO);
        contributionSubtypeValuationDtos1.add(contributionSubtypeValuationDto);
        contributionSubtypeValuationDto = new ContributionSubtypeValuationDto();
        contributionSubtypeValuationDto.setContributionSubtype(CashCategorisationSubtype.GCT_SMALL_BUS_RETIREMENT_EXEMPTION.getAvaloqInternalId());
        contributionSubtypeValuationDto.setContributionClassification(ContributionClassification.OTHER.getAvaloqInternalId());
        contributionSubtypeValuationDto.setAmount(BigDecimal.ZERO);
        contributionSubtypeValuationDtos1.add(contributionSubtypeValuationDto);

        MemberContributionsCapDto memberContributionsCapDto = new MemberContributionsCapDto();
        memberContributionsCapDto.setPersonId("69768");
        memberContributionsCapDto.setAge(52);
        memberContributionsCapDto.setConcessionalCap(new BigDecimal(35000));
        memberContributionsCapDto.setDateOfBirth("1962-10-19");
        memberContributionsCapDto.setFinancialYear("2015-07-19");
        memberContributionsCapDto.setNonConcessionalCap(new BigDecimal(180000));


        MemberContributionCapValuationDto memberContributionCapValuationDto1 = new MemberContributionCapValuationDto();
        memberContributionCapValuationDto1.setFirstName("Bill");
        memberContributionCapValuationDto1.setContributionSubtypeValuationDto(contributionSubtypeValuationDtos1);
        memberContributionCapValuationDto1.setLastName("Dias");
        memberContributionCapValuationDto1.setMemberContributionsCapDto(memberContributionsCapDto);
        memberContributionCapValuationDto1.setPersonId("69768");

        memberContributionCapValuationDtosWithNoContributions.add(memberContributionCapValuationDto1);
        return memberContributionCapValuationDtosWithNoContributions;
    }
}