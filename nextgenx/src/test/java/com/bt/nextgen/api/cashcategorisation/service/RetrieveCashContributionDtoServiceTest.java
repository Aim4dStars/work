package com.bt.nextgen.api.cashcategorisation.service;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.staticdata.model.StaticCodeDto;
import com.bt.nextgen.api.transactioncategorisation.model.TransactionCategoryDto;
import com.bt.nextgen.api.transactioncategorisation.service.TransactionCategoryDtoServiceImpl;
import com.bt.nextgen.api.transactionhistory.model.SmsfMembersDto;
import com.bt.nextgen.api.transactionhistory.service.RetrieveSmsfMembersDtoServiceImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.integration.account.PersonKey;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationSubtype;
import com.bt.nextgen.service.integration.cashcategorisation.model.CategorisableCashTransactionDto;
import com.bt.nextgen.service.integration.cashcategorisation.model.Contribution;
import com.bt.nextgen.service.integration.cashcategorisation.model.DepositKey;
import com.bt.nextgen.service.integration.cashcategorisation.model.MemberContributionImpl;
import com.bt.nextgen.service.integration.cashcategorisation.service.CashCategorisationIntegrationService;
import com.bt.nextgen.service.integration.cashcategorisation.service.RetrieveCashContributionDtoServiceImpl;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;

@RunWith(MockitoJUnitRunner.class)
public class RetrieveCashContributionDtoServiceTest
{
    @InjectMocks
    private RetrieveCashContributionDtoServiceImpl cashContributionDtoService;

    @Mock
    private CashCategorisationIntegrationService cashCategorisationIntegrationService;

    @Mock
    private ClientIntegrationService clientIntegrationService;

    @Mock
    private RetrieveSmsfMembersDtoServiceImpl retrieveSmsfMembersDtoServiceImpl;

    @Mock
    private TransactionCategoryDtoServiceImpl transactionCategoryDtoServiceImpl;

    private List <Contribution> contributionList;
    private List <SmsfMembersDto> smsfList;
    private CategorisableCashTransactionDto cashDto;
    List <TransactionCategoryDto> tranCatDtoList;

    @Before
    public void setup()
    {
        contributionList = new ArrayList <Contribution>();
        MemberContributionImpl impl1 = new MemberContributionImpl();
        impl1.setPersonKey(PersonKey.valueOf("65691"));
        impl1.setAmount(BigDecimal.valueOf(5000));
        impl1.setCashCategorisationSubtype(CashCategorisationSubtype.EMPLOYER);

        MemberContributionImpl impl2 = new MemberContributionImpl();
        impl2.setPersonKey(PersonKey.valueOf("65692"));
        impl2.setAmount(BigDecimal.valueOf(7000));
        impl2.setCashCategorisationSubtype(CashCategorisationSubtype.SPOUSE_CHILD_CONTRIBUTION);

        MemberContributionImpl impl3 = new MemberContributionImpl();
        impl3.setPersonKey(PersonKey.valueOf("65693"));
        impl3.setAmount(BigDecimal.valueOf(2000));
        impl3.setCashCategorisationSubtype(CashCategorisationSubtype.LOAN_REPAYMENT);

        MemberContributionImpl impl5 = new MemberContributionImpl();
        impl5.setPersonKey(PersonKey.valueOf("65696"));
        impl5.setAmount(BigDecimal.valueOf(6900));
        impl5.setCashCategorisationSubtype(CashCategorisationSubtype.ACTUARY_FEE);

        MemberContributionImpl impl4 = new MemberContributionImpl();
        impl4.setPersonKey(PersonKey.valueOf("65695"));
        impl4.setAmount(BigDecimal.valueOf(8500));

        MemberContributionImpl impl6 = new MemberContributionImpl();
        impl6.setPersonKey(PersonKey.valueOf("65695"));
        impl6.setAmount(BigDecimal.valueOf(5555));
        impl6.setCashCategorisationSubtype(CashCategorisationSubtype.INCOME_TAX);

        contributionList.add(impl1);
        contributionList.add(impl2);
        contributionList.add(impl3);
        contributionList.add(impl4);
        contributionList.add(impl5);
        contributionList.add(impl6);

        smsfList = new ArrayList <SmsfMembersDto>();
        SmsfMembersDto smsfDto1 = new SmsfMembersDto();
        smsfDto1.setFirstName("John");
        smsfDto1.setLastName("Cusack");
        smsfDto1.setPersonId("65691");
        SmsfMembersDto smsfDto2 = new SmsfMembersDto();
        smsfDto2.setFirstName("Martin");
        smsfDto2.setLastName("Taylor");
        smsfDto2.setPersonId("65692");
        SmsfMembersDto smsfDto3 = new SmsfMembersDto();
        smsfDto3.setFirstName("Tim");
        smsfDto3.setLastName("Martin");
        smsfDto3.setPersonId("65694");
        SmsfMembersDto smsfDto4 = new SmsfMembersDto();
        smsfDto4.setFirstName("Wong");
        smsfDto4.setLastName("Jennifer");
        smsfDto4.setPersonId("65693");
        SmsfMembersDto smsfDto5 = new SmsfMembersDto();
        smsfDto5.setFirstName("Adam");
        smsfDto5.setLastName("Christ");
        smsfDto5.setPersonId("65695");
        SmsfMembersDto smsfDto6 = new SmsfMembersDto();
        smsfDto6.setFirstName("Ian");
        smsfDto6.setLastName("Gibbs");
        smsfDto6.setPersonId("65696");
        smsfList.add(smsfDto1);
        smsfList.add(smsfDto2);
        smsfList.add(smsfDto3);
        smsfList.add(smsfDto4);
        smsfList.add(smsfDto5);
        smsfList.add(smsfDto6);

        tranCatDtoList = new ArrayList <TransactionCategoryDto>();
        TransactionCategoryDto dto1 = new TransactionCategoryDto();
        dto1.setIntlId("contri");
        List <StaticCodeDto> subCategories1 = new ArrayList <StaticCodeDto>();
        StaticCodeDto staticDto1 = new StaticCodeDto();
        staticDto1.setId("1");
        staticDto1.setIntlId("empl");

        StaticCodeDto staticDto2 = new StaticCodeDto();
        staticDto2.setId("4");
        staticDto2.setIntlId("spouse_chld_contri");
        subCategories1.add(staticDto1);
        subCategories1.add(staticDto2);
        dto1.setSubCategories(subCategories1);

        TransactionCategoryDto dto2 = new TransactionCategoryDto();
        dto2.setIntlId("purch");
        List <StaticCodeDto> subCategories2 = new ArrayList <StaticCodeDto>();
        StaticCodeDto staticDto3 = new StaticCodeDto();
        staticDto3.setId("27");
        staticDto3.setIntlId("td");
        subCategories2.add(staticDto3);
        dto2.setSubCategories(subCategories2);

        TransactionCategoryDto dto3 = new TransactionCategoryDto();
        dto3.setIntlId("prty");
        List <StaticCodeDto> subCategories3 = new ArrayList <StaticCodeDto>();
        StaticCodeDto staticDto4 = new StaticCodeDto();
        staticDto4.setId("41");
        staticDto4.setIntlId("loan_pay");
        subCategories3.add(staticDto4);
        dto3.setSubCategories(subCategories3);

        TransactionCategoryDto dto4 = new TransactionCategoryDto();
        dto4.setIntlId("regltry");
        List <StaticCodeDto> subCategories4 = new ArrayList <StaticCodeDto>();
        StaticCodeDto staticDto5 = new StaticCodeDto();
        staticDto5.setId("49");
        staticDto5.setIntlId("income_tax");
        subCategories4.add(staticDto5);
        dto4.setSubCategories(subCategories4);

        TransactionCategoryDto dto5 = new TransactionCategoryDto();
        dto5.setIntlId("lump_sum");
        List <StaticCodeDto> subCategories6 = new ArrayList <StaticCodeDto>();
        dto5.setSubCategories(subCategories6);

        TransactionCategoryDto dto6 = new TransactionCategoryDto();
        dto6.setIntlId("admin");
        List <StaticCodeDto> subCategories5 = new ArrayList <StaticCodeDto>();
        StaticCodeDto staticDto6 = new StaticCodeDto();
        staticDto6.setId("32");
        staticDto6.setIntlId("actuary");
        subCategories5.add(staticDto6);
        dto6.setSubCategories(subCategories5);

        tranCatDtoList.add(dto1);
        tranCatDtoList.add(dto2);
        tranCatDtoList.add(dto3);
        tranCatDtoList.add(dto4);
        tranCatDtoList.add(dto5);
        tranCatDtoList.add(dto6);

    }

    @Test
    public void getContributions()
    {
        Mockito.when(cashCategorisationIntegrationService.loadCashContributionsForTransaction(Mockito.any(String.class),
                Mockito.any(ServiceErrors.class))).thenReturn(contributionList);

        Mockito.when(retrieveSmsfMembersDtoServiceImpl.search(Mockito.any(ArrayList.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(smsfList);

        Mockito.when(transactionCategoryDtoServiceImpl.search(Mockito.any(ArrayList.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(tranCatDtoList);

        cashDto = new CategorisableCashTransactionDto();
        DepositKey key = new DepositKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0", "848089");
        ServiceErrors serviceErrors = null;
        cashDto = cashContributionDtoService.find(key, serviceErrors);

        assertNotNull(cashDto);
        assertNotNull(cashDto.getMemberContributionDtoList());
        Assert.assertEquals(cashDto.getMemberContributionDtoList().size(), 6);

        Assert.assertEquals(cashDto.getMemberContributionDtoList().get(0).getPersonId(), "65695");
        Assert.assertEquals(cashDto.getMemberContributionDtoList().get(0).getFullName(), "Adam Christ");
        Assert.assertEquals(cashDto.getMemberContributionDtoList().get(0).getAmount(), BigDecimal.valueOf(8500));
        Assert.assertEquals(cashDto.getMemberContributionDtoList().get(0).getTransactionCategory(), "lump_sum");
        Assert.assertEquals(cashDto.getMemberContributionDtoList().get(0).getContributionSubType(), null);

		Assert.assertEquals(cashDto.getMemberContributionDtoList().get(1).getPersonId(), "65695");
		Assert.assertEquals(cashDto.getMemberContributionDtoList().get(1).getFullName(), "Adam Christ");
		Assert.assertEquals(cashDto.getMemberContributionDtoList().get(1).getAmount(), BigDecimal.valueOf(5555));
		Assert.assertEquals(cashDto.getMemberContributionDtoList().get(1).getTransactionCategory(), "regltry");
		Assert.assertEquals(cashDto.getMemberContributionDtoList().get(1).getContributionSubType(), "income_tax");

        Assert.assertEquals(cashDto.getMemberContributionDtoList().get(2).getPersonId(), "65696");
        Assert.assertEquals(cashDto.getMemberContributionDtoList().get(2).getFullName(), "Ian Gibbs");
        Assert.assertEquals(cashDto.getMemberContributionDtoList().get(2).getAmount(), BigDecimal.valueOf(6900));
        Assert.assertEquals(cashDto.getMemberContributionDtoList().get(2).getTransactionCategory(), "admin");
        Assert.assertEquals(cashDto.getMemberContributionDtoList().get(2).getContributionSubType(), "actuary");

        Assert.assertEquals(cashDto.getMemberContributionDtoList().get(3).getPersonId(), "65691");
        Assert.assertEquals(cashDto.getMemberContributionDtoList().get(3).getFullName(), "John Cusack");
        Assert.assertEquals(cashDto.getMemberContributionDtoList().get(3).getAmount(), BigDecimal.valueOf(5000));
        Assert.assertEquals(cashDto.getMemberContributionDtoList().get(3).getTransactionCategory(), "contri");
        Assert.assertEquals(cashDto.getMemberContributionDtoList().get(3).getContributionSubType(), "empl");

		Assert.assertEquals(cashDto.getMemberContributionDtoList().get(4).getPersonId(), "65692");
		Assert.assertEquals(cashDto.getMemberContributionDtoList().get(4).getFullName(), "Martin Taylor");
		Assert.assertEquals(cashDto.getMemberContributionDtoList().get(4).getAmount(), BigDecimal.valueOf(7000));
		Assert.assertEquals(cashDto.getMemberContributionDtoList().get(4).getTransactionCategory(), "contri");
		Assert.assertEquals(cashDto.getMemberContributionDtoList().get(4).getContributionSubType(), "spouse_chld_contri");

        Assert.assertEquals(cashDto.getMemberContributionDtoList().get(5).getPersonId(), "65693");
        Assert.assertEquals(cashDto.getMemberContributionDtoList().get(5).getFullName(), "Wong Jennifer");
        Assert.assertEquals(cashDto.getMemberContributionDtoList().get(5).getAmount(), BigDecimal.valueOf(2000));
        Assert.assertEquals(cashDto.getMemberContributionDtoList().get(5).getTransactionCategory(), "prty");
        Assert.assertEquals(cashDto.getMemberContributionDtoList().get(5).getContributionSubType(), "loan_pay");



    }
}