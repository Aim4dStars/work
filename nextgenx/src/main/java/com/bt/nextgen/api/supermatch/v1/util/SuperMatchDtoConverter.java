package com.bt.nextgen.api.supermatch.v1.util;

import ch.lambdaj.group.Group;
import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.supermatch.v1.model.MemberDto;
import com.bt.nextgen.api.supermatch.v1.model.MoneyItemDto;
import com.bt.nextgen.api.supermatch.v1.model.RolloverDetailsDto;
import com.bt.nextgen.api.supermatch.v1.model.SuperMatchDto;
import com.bt.nextgen.api.supermatch.v1.model.SuperMatchDtoKey;
import com.bt.nextgen.api.supermatch.v1.model.SuperMatchFundDto;
import com.bt.nextgen.service.btesb.supermatch.model.AtoMoney;
import com.bt.nextgen.service.btesb.supermatch.model.FundCategory;
import com.bt.nextgen.service.integration.supermatch.Member;
import com.bt.nextgen.service.integration.supermatch.StatusSummary;
import com.bt.nextgen.service.integration.supermatch.SuperFundAccount;
import com.bt.nextgen.service.integration.supermatch.SuperMatchDetails;
import org.apache.commons.collections.CollectionUtils;
import org.hamcrest.Matchers;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static ch.lambdaj.group.Groups.by;
import static ch.lambdaj.group.Groups.group;
import static com.bt.nextgen.service.btesb.supermatch.model.FundCategory.PARTIALLY_ROLLOVERED;
import static com.bt.nextgen.service.btesb.supermatch.model.FundCategory.ROLLOVERED;

/**
 * Utility class to convert the super match details to DTO
 */
public class SuperMatchDtoConverter {

    private SuperMatchDtoConverter() {
        // hidhing the default constructor
    }

    /**
     * Converts Super match details to DTO
     *
     * @param superMatchDtoKey  - Super match dto key
     * @param superMatchDetails - Super match fund details
     */
    public static SuperMatchDto convertToDto(SuperMatchDtoKey superMatchDtoKey, List<SuperMatchDetails> superMatchDetails) {
        SuperMatchDto superMatchDto = new SuperMatchDto(superMatchDtoKey);
        if (CollectionUtils.isNotEmpty(superMatchDetails)) {
            final SuperMatchDetails superMatchDetail = superMatchDetails.get(0);
            final StatusSummary statusSummary = superMatchDetail.getStatusSummary();
            if (statusSummary != null) {
                superMatchDto.setConsentProvided(statusSummary.isConsentStatusProvided());
                superMatchDto.setMatchResultAvailable(statusSummary.isMatchResultAvailable());
                superMatchDto.setHasSeenResults(statusSummary.isMatchResultAcknowledged());
            }

            superMatchDto.setAtoHeldMonies(createMoneyItems(superMatchDetail.getAtoMonies()));
            superMatchDto.setSuperMatchFundList(createSuperFundDetails(superMatchDetail.getSuperFundAccounts()));
        }
        return superMatchDto;
    }

    private static List<MoneyItemDto> createMoneyItems(List<AtoMoney> atoMonies) {
        final List<MoneyItemDto> moneyItems = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(atoMonies)) {
            for (AtoMoney money : atoMonies) {
                moneyItems.add(new MoneyItemDto(money.getBalance(), money.getCategory()));
            }
        }
        return moneyItems;
    }

    private static List<SuperMatchFundDto> createSuperFundDetails(List<SuperFundAccount> superFundAccountAccounts) {
        final List<SuperMatchFundDto> funds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(superFundAccountAccounts)) {
            final Group<SuperFundAccount> superFundAccountGroup = group(superFundAccountAccounts, by(on(SuperFundAccount.class).getFundIdentifier()));

            for (Group<SuperFundAccount> group : superFundAccountGroup.subgroups()) {
                SuperFundAccount account = selectFirst(group.findAll(), Matchers.notNullValue());

                final SuperMatchFundDto superfundDto = new SuperMatchFundDto();
                superfundDto.setAccountNumber(account.getAccountNumber());
                superfundDto.setAbn(account.getAbn());
                superfundDto.setBalance(account.getAccountBalance().setScale(2, RoundingMode.HALF_EVEN));
                superfundDto.setOrgName(account.getOrganisationName());
                superfundDto.setUsi(account.getUsi());
                superfundDto.setContactName(account.getContactName());
                superfundDto.setInsuranceCovered(account.getInsuranceIndicator());
                superfundDto.setAddress(createAddressDto(account));
                superfundDto.setActivityStatus(account.getActivityStatus() != null ? account.getActivityStatus().getValue() : null);
                superfundDto.setRolloverable(FundCategory.ROLLOVERABLE.equals(account.getFundCategory()) ||
                        PARTIALLY_ROLLOVERED.equals(account.getFundCategory()));
                superfundDto.setRolloverDetails(createRolloverDetails(group.findAll()));
                superfundDto.setMembers(createMemberDetails(account.getMembers()));
                funds.add(superfundDto);
            }
        }
        return funds;
    }

    private static List<RolloverDetailsDto> createRolloverDetails(List<SuperFundAccount> superFundAccounts) {
        final List<RolloverDetailsDto> rollOverDetails = new ArrayList<>();
        RolloverDetailsDto rollOverDetail;
        for (SuperFundAccount superFundAccount : superFundAccounts) {
            if (ROLLOVERED.equals(superFundAccount.getFundCategory()) || PARTIALLY_ROLLOVERED.equals(superFundAccount.getFundCategory())) {
                rollOverDetail = new RolloverDetailsDto();
                rollOverDetail.setRolloverId(superFundAccount.getRolloverId());
                if (superFundAccount.getRolloverAmount() != null) {
                    rollOverDetail.setRolloverAmount(superFundAccount.getRolloverAmount().setScale(2, RoundingMode.HALF_EVEN));
                }
                rollOverDetail.setRolloverStatus(superFundAccount.getRolloverStatus());
                rollOverDetail.setRolloverFundCategory(superFundAccount.getRolloverId() != null ? PARTIALLY_ROLLOVERED.name() : ROLLOVERED.name());
                rollOverDetail.setRolloverProvidedTime(superFundAccount.getRolloverStatusProvidedDateTime());
                rollOverDetails.add(rollOverDetail);
            }
        }
        return rollOverDetails;
    }

    private static List<MemberDto> createMemberDetails(List<Member> members) {
        final List<MemberDto> memberDtos = new ArrayList<>();
        for (Member fundMember : members) {
            memberDtos.add(new MemberDto(fundMember));
        }
        return memberDtos;
    }

    private static AddressDto createAddressDto(SuperFundAccount account) {
        AddressDto addressDto = new AddressDto();
        addressDto.setAddressLine1(account.getAddressLine());
        addressDto.setAddressLine2(account.getLocality());
        addressDto.setState(account.getState());
        addressDto.setPostcode(account.getPostcode());
        addressDto.setCountryCode(account.getCountryCode());
        return addressDto;
    }
}
