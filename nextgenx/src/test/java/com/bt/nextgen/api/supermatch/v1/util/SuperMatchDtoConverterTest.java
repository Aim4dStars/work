package com.bt.nextgen.api.supermatch.v1.util;

import com.bt.nextgen.api.supermatch.v1.model.MemberDto;
import com.bt.nextgen.api.supermatch.v1.model.MoneyItemDto;
import com.bt.nextgen.api.supermatch.v1.model.SuperMatchDto;
import com.bt.nextgen.api.supermatch.v1.model.SuperMatchDtoKey;
import com.bt.nextgen.api.supermatch.v1.model.SuperMatchFundDto;
import com.bt.nextgen.service.btesb.supermatch.model.ActivityStatus;
import com.bt.nextgen.service.btesb.supermatch.model.AtoMoney;
import com.bt.nextgen.service.btesb.supermatch.model.FundCategory;
import com.bt.nextgen.service.integration.supermatch.Member;
import com.bt.nextgen.service.integration.supermatch.StatusSummary;
import com.bt.nextgen.service.integration.supermatch.SuperFundAccount;
import com.bt.nextgen.service.integration.supermatch.SuperMatchDetails;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class SuperMatchDtoConverterTest {

    private List<SuperMatchDetails> superMatchDetailList;
    private List<SuperFundAccount> superFundAccounts;
    private List<Member> members;
    private List<AtoMoney> atoMonies;

    @Before
    public void setUp() throws Exception {
        members = getMembers();
        superFundAccounts = getSuperFundAccounts();
        atoMonies = getAtoMonies();
        superMatchDetailList = getSuperMatchDetailList();
    }

    @Test
    public void convertToDto() {
        SuperMatchDtoKey key = new SuperMatchDtoKey("123456");
        SuperMatchDto superMatchDetails = SuperMatchDtoConverter.convertToDto(key, superMatchDetailList);

        assertEquals(superMatchDetails.isConsentProvided(), true);
        assertEquals(superMatchDetails.getSuperMatchFundList().size(), 1);
        assertEquals(superMatchDetails.getAtoHeldMonies().size(), 1);

        MoneyItemDto moneyItemDto = superMatchDetails.getAtoHeldMonies().get(0);
        assertEquals(moneyItemDto.getBalance(), BigDecimal.valueOf(1000).setScale(2, RoundingMode.HALF_EVEN));
        assertEquals(moneyItemDto.getCategory(), "Category");

        SuperMatchFundDto superMatchFundDto = superMatchDetails.getSuperMatchFundList().get(0);
        assertEquals(superMatchFundDto.getAccountNumber(), "123456");
        assertEquals(superMatchFundDto.getBalance(), BigDecimal.valueOf(1000).setScale(2, RoundingMode.HALF_EVEN));
        assertEquals(superMatchFundDto.getInsuranceCovered(), true);
        assertEquals(superMatchFundDto.getRolloverDetails().get(0).getRolloverStatus(), true);
        assertEquals(superMatchFundDto.getRolloverDetails().get(0).getRolloverId(), "1");
        assertEquals(superMatchFundDto.getRolloverDetails().get(0).getRolloverAmount(), BigDecimal.valueOf(100).setScale(2, BigDecimal.ROUND_HALF_EVEN));
        assertEquals(superMatchFundDto.getMembers().size(), 1);
        assertEquals(superMatchFundDto.getActivityStatus(), "Active");

        MemberDto member = superMatchFundDto.getMembers().get(0);
        assertEquals(member.getCustomerId(), "123456");
        assertEquals(member.getFirstName(), "John");
        assertEquals(member.getLastName(), "Smith");
    }

    @Test
    public void convertToDtoForNoSuperMatchResults() {
        SuperMatchDto superMatchDetails = SuperMatchDtoConverter.convertToDto(new SuperMatchDtoKey("123456"), null);

        assertNull(superMatchDetails.isConsentProvided());
        assertNull(superMatchDetails.getHasSeenResults());
        assertNull(superMatchDetails.getMatchResultAvailable());
        assertNull(superMatchDetails.getSuperMatchFundList());
        assertNull(superMatchDetails.getAtoHeldMonies());
    }

    @Test
    public void convertToDto_NoAtoHeldMonies() {
        atoMonies = null;
        superMatchDetailList = getSuperMatchDetailList();

        SuperMatchDtoKey key = new SuperMatchDtoKey("123456");
        SuperMatchDto superMatchDetails = SuperMatchDtoConverter.convertToDto(key, superMatchDetailList);
        assertEquals(superMatchDetails.getAtoHeldMonies().size(), 0);
    }

    private List<SuperMatchDetails> getSuperMatchDetailList() {
        superMatchDetailList = new ArrayList<>();
        SuperMatchDetails superMatchDetails = mock(SuperMatchDetails.class);
        StatusSummary statusSummary = mock(StatusSummary.class);
        Mockito.when(statusSummary.isConsentStatusProvided()).thenReturn(true);
        Mockito.when(superMatchDetails.getStatusSummary()).thenReturn(statusSummary);
        Mockito.when(superMatchDetails.getSuperFundAccounts()).thenReturn(superFundAccounts);
        Mockito.when(superMatchDetails.getAtoMonies()).thenReturn(atoMonies);

        superMatchDetailList.add(superMatchDetails);
        return superMatchDetailList;
    }

    private List<SuperFundAccount> getSuperFundAccounts() {
        superFundAccounts = new ArrayList<>();
        SuperFundAccount superFundAccount = mock(SuperFundAccount.class);
        Mockito.when(superFundAccount.getAbn()).thenReturn("123456");
        Mockito.when(superFundAccount.getAccountNumber()).thenReturn("123456");
        Mockito.when(superFundAccount.getFundCategory()).thenReturn(FundCategory.PARTIALLY_ROLLOVERED);
        Mockito.when(superFundAccount.getAccountBalance()).thenReturn(BigDecimal.valueOf(1000));
        Mockito.when(superFundAccount.getInsuranceIndicator()).thenReturn(true);
        Mockito.when(superFundAccount.getRolloverStatus()).thenReturn(true);
        Mockito.when(superFundAccount.getRolloverId()).thenReturn("1");
        Mockito.when(superFundAccount.getRolloverAmount()).thenReturn(BigDecimal.valueOf(100));
        Mockito.when(superFundAccount.getMembers()).thenReturn(members);
        Mockito.when(superFundAccount.getActivityStatus()).thenReturn(ActivityStatus.ACTIVE);

        SuperFundAccount rolloveredFundAccount = mock(SuperFundAccount.class);
        Mockito.when(rolloveredFundAccount.getAbn()).thenReturn("123456");
        Mockito.when(rolloveredFundAccount.getAccountNumber()).thenReturn("67890");
        Mockito.when(rolloveredFundAccount.getFundCategory()).thenReturn(FundCategory.ROLLOVERED);
        Mockito.when(rolloveredFundAccount.getAccountBalance()).thenReturn(BigDecimal.valueOf(1000));
        Mockito.when(rolloveredFundAccount.getInsuranceIndicator()).thenReturn(true);
        Mockito.when(rolloveredFundAccount.getRolloverStatus()).thenReturn(true);
        Mockito.when(rolloveredFundAccount.getMembers()).thenReturn(members);

        superFundAccounts.add(superFundAccount);
        superFundAccounts.add(rolloveredFundAccount);
        return superFundAccounts;
    }

    public List<Member> getMembers() {
        members = new ArrayList<>();
        Member member = mock(Member.class);
        Mockito.when(member.getCustomerId()).thenReturn("123456");
        Mockito.when(member.getFirstName()).thenReturn("John");
        Mockito.when(member.getLastName()).thenReturn("Smith");
        Mockito.when(member.getIssuer()).thenReturn("Westpac");
        members.add(member);
        return members;
    }

    public List<AtoMoney> getAtoMonies() {
        atoMonies = new ArrayList<>();
        AtoMoney money = mock(AtoMoney.class);
        Mockito.when(money.getBalance()).thenReturn(BigDecimal.valueOf(1000));
        Mockito.when(money.getCategory()).thenReturn("Category");
        atoMonies.add(money);
        return atoMonies;
    }
}