package com.bt.nextgen.service.btesb.supermatch;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.btesb.supermatch.model.FundCategory;
import com.bt.nextgen.service.btesb.supermatch.model.MemberImpl;
import com.bt.nextgen.service.btesb.supermatch.model.SuperFundAccountImpl;
import com.bt.nextgen.service.integration.supermatch.Member;
import com.bt.nextgen.service.integration.supermatch.StatusSummary;
import com.bt.nextgen.service.integration.supermatch.SuperFundAccount;
import com.bt.nextgen.service.integration.supermatch.SuperMatchDetails;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.bt.nextgen.core.type.DateUtil.convertToDateTime;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@Ignore
public class SuperMatchIntegrationServiceTest extends BaseSecureIntegrationTest {

    @Autowired
    private SuperMatchIntegrationServiceImpl superMatchIntegrationService;

    private String customerId;
    private SuperFundAccountImpl superFundAccount;
    private List<SuperMatchDetails> result;

    @Before
    public void setUp() throws Exception {
        customerId = "74061351";
        superFundAccount = new SuperFundAccountImpl();
        final MemberImpl member = new MemberImpl();
        member.setCustomerId(customerId);
        member.setIssuer("WestpacLegacy");
        member.setFirstName("Jon");
        member.setLastName("Smith");
        member.setDateOfBirth(new DateTime("1990-01-01"));
        superFundAccount.setMembers(Collections.<Member>singletonList(member));
    }

    @Test
    @SecureTestContext
    public void retrieveSuperDetails() {
        result = superMatchIntegrationService.retrieveSuperDetails(customerId, superFundAccount, new ServiceErrorsImpl());
        assertNotNull(result);
        assertEquals(result.size(), 1);

        SuperMatchDetails superMatchDetails = result.get(0);

        StatusSummary statusSummary = superMatchDetails.getStatusSummary();
        assertEquals(statusSummary.getConsentStatus(), "Submitted");
        assertEquals(statusSummary.isConsentStatusProvided(), true);
        assertEquals(statusSummary.isMatchResultAcknowledged(), true);
        assertEquals(statusSummary.getMatchResultAvailableStatus(), "Available");
        assertEquals(statusSummary.getAtoHeldFundCount(), Integer.valueOf(0));
        assertEquals(statusSummary.getExternalFundCount(), Integer.valueOf(4));
        assertEquals(statusSummary.getLastMatchResultDateTime(), convertToDateTime("2016-11-22T08:49:35+11:00", "yyyy-MM-dd'T'HH:mm:ssZ"));
        assertEquals(statusSummary.getCustomerType(), "Member");

        assertEquals(superMatchDetails.getAtoMonies().size(), 2);
        assertEquals(superMatchDetails.getAtoMonies().get(0).getBalance().setScale(2, BigDecimal.ROUND_HALF_EVEN), BigDecimal.valueOf(7154).setScale(2, BigDecimal.ROUND_HALF_EVEN));
        assertEquals(superMatchDetails.getAtoMonies().get(0).getCategory(), "SuperannuationGuarantee");

        assertEquals(superMatchDetails.getAtoMonies().get(1).getBalance().setScale(2, BigDecimal.ROUND_HALF_EVEN), BigDecimal.valueOf(9154).setScale(2, BigDecimal.ROUND_HALF_EVEN));
        assertEquals(superMatchDetails.getAtoMonies().get(1).getCategory(), "UnclaimedTemporaryResident");

        assertEquals(superMatchDetails.getSuperFundAccounts().size(), 5);
        SuperFundAccount updatedSuperFundAccount = superMatchDetails.getSuperFundAccounts().get(0);

        assertEquals(updatedSuperFundAccount.getAccountNumber(), "2402599");
        assertEquals(updatedSuperFundAccount.getInsuranceIndicator(), true);
        assertEquals(updatedSuperFundAccount.getDefinedBenefitIndicator(), true);
        assertEquals(updatedSuperFundAccount.getAccountBalance().setScale(2, BigDecimal.ROUND_HALF_EVEN), BigDecimal.valueOf(7154).setScale(2, BigDecimal.ROUND_HALF_EVEN));
        assertEquals(updatedSuperFundAccount.getFundCategory(), FundCategory.ROLLOVERABLE);
        assertNull(updatedSuperFundAccount.getRolloverStatusProvidedDateTime());
        assertEquals(updatedSuperFundAccount.getOrganisationName(), "A & B Sutherland Super Fund");
        assertEquals(updatedSuperFundAccount.getContactName(), "Services Team Client");
        assertEquals(updatedSuperFundAccount.getAddressLine(), "");
        assertEquals(updatedSuperFundAccount.getMembers().size(), 1);

        Member member = updatedSuperFundAccount.getMembers().get(0);
        assertEquals(member.getFirstName(), "DESHAUN SHUFFLER TEST");
        assertEquals(member.getLastName(), "WILLIE");
        assertEquals(member.getDateOfBirth(), convertToDateTime("1938-09-07", "yyyy-MM-dd"));
        assertEquals(member.getCustomerId(), "74061351");
        assertEquals(member.getIssuer(), "Westpac");
    }

    @Test
    @SecureTestContext
    public void retrieveSuperDetailsFailure() {
        result = superMatchIntegrationService.retrieveSuperDetails("74061352", superFundAccount, new ServiceErrorsImpl());
        assertEquals(result.size(), 0);
    }

    @Test
    @SecureTestContext
    public void updateConsentStatus() {
        result = superMatchIntegrationService.updateConsentStatus(customerId, superFundAccount, true, new ServiceErrorsImpl());
        assertNotNull(result);
        assertEquals(result.size(), 1);

        SuperMatchDetails fund = result.get(0);
        assertEquals(fund.getSuperFundAccounts().size(), 9);

        SuperFundAccount updatedSuperFundAccount = fund.getSuperFundAccounts().get(0);
        assertEquals(updatedSuperFundAccount.getAccountNumber(), "27014940");
        assertEquals(updatedSuperFundAccount.getInsuranceIndicator(), false);
        assertEquals(updatedSuperFundAccount.getDefinedBenefitIndicator(), false);
        assertEquals(updatedSuperFundAccount.getAccountBalance().setScale(2, BigDecimal.ROUND_HALF_EVEN), BigDecimal.valueOf(2500).setScale(2, BigDecimal.ROUND_HALF_EVEN));
        assertEquals(updatedSuperFundAccount.getFundCategory(), FundCategory.ROLLOVERABLE);
        assertNull(updatedSuperFundAccount.getRolloverStatusProvidedDateTime());
        assertEquals(updatedSuperFundAccount.getOrganisationName(), "Linfox Staff Superannuation Fund");
        assertEquals(updatedSuperFundAccount.getContactName(), "Services Team Client Super");
        assertEquals(updatedSuperFundAccount.getAddressLine(), "");
        assertEquals(updatedSuperFundAccount.getMembers().size(), 1);

        Member member = updatedSuperFundAccount.getMembers().get(0);
        assertEquals(member.getFirstName(), "MADHURI");
        assertEquals(member.getLastName(), "KROPIK");
        assertEquals(member.getDateOfBirth(), convertToDateTime("1985-09-30", "yyyy-MM-dd"));
        assertEquals(member.getCustomerId(), "74061351");
        assertEquals(member.getIssuer(), "Westpac");
    }

    @Test
    @SecureTestContext
    public void updateAcknowledgementStatus() {
        result = superMatchIntegrationService.updateAcknowledgementStatus(customerId, superFundAccount, new ServiceErrorsImpl());
        assertNotNull(result);
        assertEquals(result.size(), 1);

        SuperMatchDetails fund = result.get(0);
        assertEquals(fund.getSuperFundAccounts().size(), 9);

        SuperFundAccount updatedSuperFundAccount = fund.getSuperFundAccounts().get(0);
        assertEquals(updatedSuperFundAccount.getAccountNumber(), "27014940");
        assertEquals(updatedSuperFundAccount.getInsuranceIndicator(), false);
        assertEquals(updatedSuperFundAccount.getDefinedBenefitIndicator(), false);
        assertEquals(updatedSuperFundAccount.getAccountBalance().setScale(2, BigDecimal.ROUND_HALF_EVEN), BigDecimal.valueOf(2500).setScale(2, BigDecimal.ROUND_HALF_EVEN));
        assertEquals(updatedSuperFundAccount.getFundCategory(), FundCategory.ROLLOVERABLE);
        assertNull(updatedSuperFundAccount.getRolloverStatusProvidedDateTime());
        assertEquals(updatedSuperFundAccount.getOrganisationName(), "Linfox Staff Superannuation Fund");
        assertEquals(updatedSuperFundAccount.getContactName(), "Services Team Client Super");
        assertEquals(updatedSuperFundAccount.getAddressLine(), "");
        assertEquals(updatedSuperFundAccount.getMembers().size(), 1);

        Member member = updatedSuperFundAccount.getMembers().get(0);
        assertEquals(member.getFirstName(), "MADHURI");
        assertEquals(member.getLastName(), "KROPIK");
        assertEquals(member.getDateOfBirth(), convertToDateTime("1985-09-30", "yyyy-MM-dd"));
        assertEquals(member.getCustomerId(), "74061351");
        assertEquals(member.getIssuer(), "Westpac");

    }

    @Test
    @SecureTestContext
    public void updateRollOverStatus() {
        List<SuperFundAccount> rollOverFunds = new ArrayList<>();
        SuperFundAccountImpl rollOverFund = new SuperFundAccountImpl();
        rollOverFund.setAccountNumber("2705940");
        rollOverFund.setUsi("73698319227");
        rollOverFund.setRolloverStatus(true);
        rollOverFund.setRolloverAmount(BigDecimal.TEN);
        rollOverFunds.add(rollOverFund);

        List<SuperMatchDetails> result = superMatchIntegrationService.updateRollOverStatus(customerId, superFundAccount, rollOverFunds, new ServiceErrorsImpl());
        assertNotNull(result);
        assertEquals(result.size(), 1);

        SuperMatchDetails fund = result.get(0);
        assertEquals(fund.getSuperFundAccounts().size(), 8);

        SuperFundAccount updatedSuperFundAccount = fund.getSuperFundAccounts().get(0);
        assertEquals(updatedSuperFundAccount.getAccountNumber(), "27014940");
        assertEquals(updatedSuperFundAccount.getInsuranceIndicator(), false);
        assertEquals(updatedSuperFundAccount.getDefinedBenefitIndicator(), false);
        assertEquals(updatedSuperFundAccount.getAccountBalance().setScale(2, BigDecimal.ROUND_HALF_EVEN), BigDecimal.valueOf(2500).setScale(2, BigDecimal.ROUND_HALF_EVEN));
        assertEquals(updatedSuperFundAccount.getFundCategory(), FundCategory.ROLLOVERABLE);
        assertNull(updatedSuperFundAccount.getRolloverStatusProvidedDateTime());
        assertEquals(updatedSuperFundAccount.getOrganisationName(), "Linfox Staff Superannuation Fund");
        assertEquals(updatedSuperFundAccount.getContactName(), "Services Team Client Super");
        assertEquals(updatedSuperFundAccount.getAddressLine(), "");
        assertEquals(updatedSuperFundAccount.getMembers().size(), 1);

        Member member = updatedSuperFundAccount.getMembers().get(0);
        assertEquals(member.getFirstName(), "JESSADA");
        assertEquals(member.getLastName(), "ESNOUF");
        assertEquals(member.getDateOfBirth(), convertToDateTime("1977-08-14", "yyyy-MM-dd"));
        assertEquals(member.getCustomerId(), "74061351");
        assertEquals(member.getIssuer(), "Westpac");

        assertEquals(fund.getAtoMonies().size(), 2);
        assertEquals(fund.getAtoMonies().get(0).getBalance().setScale(2, BigDecimal.ROUND_HALF_EVEN), BigDecimal.valueOf(7154).setScale(2, BigDecimal.ROUND_HALF_EVEN));
        assertEquals(fund.getAtoMonies().get(0).getCategory(), "SuperannuationGuarantee");

        assertEquals(fund.getAtoMonies().get(1).getBalance().setScale(2, BigDecimal.ROUND_HALF_EVEN), BigDecimal.valueOf(9154).setScale(2, BigDecimal.ROUND_HALF_EVEN));
        assertEquals(fund.getAtoMonies().get(1).getCategory(), "UnclaimedTemporaryResident");
    }

    @Test
    @SecureTestContext
    public void createMember() {
        assertTrue(superMatchIntegrationService.createMember(customerId, superFundAccount, new ServiceErrorsImpl()));
    }
}