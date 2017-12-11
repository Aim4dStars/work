package com.bt.nextgen.service.avaloq.insurance;

import com.bt.nextgen.service.avaloq.insurance.model.*;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class BenefitTypeTest
{
    @Test
    public void getDefaultOptionsForBenefitTypeTPD()
    {
        BenefitType benefitType = BenefitType.TPD;
        Collection<BenefitOptionType> options = benefitType.getOptions();

        assertNotNull(options.size());
        assertThat(options, containsInAnyOrder(BenefitOptionType.WAIVER_LIFE_PREMIUM,
                                                BenefitOptionType.TPD_BUSINESS_COVER,
                                                BenefitOptionType.TPD_DOUBLE,
                                                BenefitOptionType.TPD_BUY_BACK));
    }

    @Test
    public void getOptionsForBenefitTypeTPD()
    {
        PolicyImpl policy = new PolicyImpl();

        //policy = STAND_ALONE_TPD
        //Account = SUPER
        policy.setPolicyType(PolicyType.STAND_ALONE_TPD);
        Collection<BenefitOptionType> options = BenefitType.TPD.getOptions(policy, AccountStructureType.SUPER);
        assertNotNull(options.size());
        assertTrue(options.isEmpty());

        //policy = KEY_PERSON_INCOME
        //Account = SUPER
        policy.setPolicyType(PolicyType.KEY_PERSON_INCOME);
        options = BenefitType.TPD.getOptions(policy, AccountStructureType.SUPER);
        assertNotNull(options.size());
        assertTrue(options.isEmpty());

        //policy = TERM_LIFE
        //Account = SUPER
        policy.setPolicyType(PolicyType.TERM_LIFE);
        options = BenefitType.TPD.getOptions(policy, AccountStructureType.SUPER);
        assertNotNull(options.size());
        assertThat(options, containsInAnyOrder(BenefitOptionType.TPD_BUSINESS_COVER,
                BenefitOptionType.TPD_DOUBLE,
                BenefitOptionType.TPD_BUY_BACK));

        //policy = TERM_LIFE
        //Account = INVESTMENT
        options = BenefitType.TPD.getOptions(policy, AccountStructureType.SMSF);
        assertNotNull(options.size());
        assertThat(options, containsInAnyOrder(BenefitOptionType.WAIVER_LIFE_PREMIUM,
                BenefitOptionType.TPD_BUSINESS_COVER,
                BenefitOptionType.TPD_DOUBLE,
                BenefitOptionType.TPD_BUY_BACK));

        //policy = FLEXIBLE_LINKING_PLUS
        //Account = SUPER
        policy.setPolicyType(PolicyType.FLEXIBLE_LINKING_PLUS);
        options = BenefitType.TPD.getOptions(policy, AccountStructureType.SUPER);
        assertNotNull(options.size());
        assertThat(options, containsInAnyOrder(BenefitOptionType.TPD_BUSINESS_COVER,
                BenefitOptionType.TPD_DOUBLE,
                BenefitOptionType.TPD_BUY_BACK));

        //policy = TERM_LIFE_AS_SUPER
        //Account = SUPER
        policy.setPolicyType(PolicyType.TERM_LIFE_AS_SUPER);
        options = BenefitType.TPD.getOptions(policy, AccountStructureType.SUPER);
        assertNotNull(options.size());
        assertThat(options, containsInAnyOrder(BenefitOptionType.TPD_BUSINESS_COVER,
                BenefitOptionType.TPD_DOUBLE,
                BenefitOptionType.TPD_BUY_BACK));

        //policy = FLEXIBLE_LINKING_PLUS
        //Account = SUPER
        policy.setPolicyType(PolicyType.FLEXIBLE_LINKING_PLUS);
        options = BenefitType.SUPER_PLUS_TPD.getOptions(policy, AccountStructureType.SUPER);
        assertNotNull(options.size());
        assertThat(options, containsInAnyOrder(BenefitOptionType.TPD_BUSINESS_COVER,
                BenefitOptionType.TPD_DOUBLE,
                BenefitOptionType.TPD_BUY_BACK));

        //policy = TERM_LIFE_AS_SUPER
        //Account = SUPER
        policy.setPolicyType(PolicyType.TERM_LIFE_AS_SUPER);
        options = BenefitType.SUPER_PLUS_TPD.getOptions(policy, AccountStructureType.SUPER);
        assertNotNull(options.size());
        assertThat(options, containsInAnyOrder(BenefitOptionType.TPD_BUSINESS_COVER,
                BenefitOptionType.TPD_DOUBLE,
                BenefitOptionType.TPD_BUY_BACK));

/*        //policy = ADVICE_SERVICE_FEE
        //Account = SUPER
        policy.setPolicyType(PolicyType.ADVICE_SERVICE_FEE);
        options = BenefitType.SUPER_PLUS_TPD.getOptions(policy, AccountStructureType.SUPER);
        assertNotNull(options.size());
        assertThat(options, containsInAnyOrder(BenefitOptionType.WAIVER_LIFE_PREMIUM,
                BenefitOptionType.TPD_BUSINESS_COVER,
                BenefitOptionType.TPD_DOUBLE,
                BenefitOptionType.TPD_BUY_BACK));*/
    }

    @Test
    public void getOptionsForBenefitTypeIncomeProtectionAndSuperPlusIncomeProtection()
    {
        PolicyImpl policy = new PolicyImpl();

        policy.setPolicyType(PolicyType.INCOME_LINKING_PLUS);
        Collection<BenefitOptionType> options = BenefitType.INCOME_PROTECTION.getOptions(policy, AccountStructureType.SUPER);
        assertNotNull(options.size());
        assertThat(options, containsInAnyOrder(BenefitOptionType.ACCIDENT,
                BenefitOptionType.SUPER_CONTRIBUTION));

        policy.setPolicyType(PolicyType.INCOME_PROTECTION);
        options = BenefitType.INCOME_PROTECTION.getOptions(policy, AccountStructureType.SUPER);
        assertNotNull(options.size());
        assertThat(options, containsInAnyOrder(BenefitOptionType.ACCIDENT,
                BenefitOptionType.SUPER_CONTRIBUTION));

        policy.setPolicyType(PolicyType.INCOME_PROTECTION_AS_SUPER);
        options = BenefitType.INCOME_PROTECTION.getOptions(policy, AccountStructureType.SUPER);
        assertNotNull(options.size());
        assertThat(options, containsInAnyOrder(BenefitOptionType.ACCIDENT,
                BenefitOptionType.SUPER_CONTRIBUTION));

        policy.setPolicyType(PolicyType.INCOME_PROTECTION);
        policy.setPolicySubType(PolicySubType.BUSINESS_OVERHEAD);
        options = BenefitType.INCOME_PROTECTION.getOptions(policy, AccountStructureType.SUPER);
        assertNotNull(options.size());
        assertTrue(options.isEmpty());
    }

    @Test
    public void getOptionsForBenefitTypeSuperPlusIncomeProtection()
    {
        PolicyImpl policy = new PolicyImpl();

        policy.setPolicyType(PolicyType.INCOME_LINKING_PLUS);
        Collection<BenefitOptionType> options = BenefitType.SUPER_PLUS_INCOME_PROTECTION.getOptions(policy, AccountStructureType.SUPER);
        assertNotNull(options.size());
        assertThat(options, containsInAnyOrder(BenefitOptionType.ACCIDENT,
                BenefitOptionType.SUPER_CONTRIBUTION));

        policy.setPolicyType(PolicyType.INCOME_PROTECTION);
        options = BenefitType.SUPER_PLUS_INCOME_PROTECTION.getOptions(policy, AccountStructureType.SUPER);
        assertNotNull(options.size());
        assertThat(options, containsInAnyOrder(BenefitOptionType.ACCIDENT,
                BenefitOptionType.SUPER_CONTRIBUTION));

        policy.setPolicyType(PolicyType.INCOME_PROTECTION_AS_SUPER);
        options = BenefitType.SUPER_PLUS_INCOME_PROTECTION.getOptions(policy, AccountStructureType.SUPER);
        assertNotNull(options.size());
        assertThat(options, containsInAnyOrder(BenefitOptionType.ACCIDENT,
                BenefitOptionType.SUPER_CONTRIBUTION));
    }

    @Test
    public void getOptionsForBenefitTypeLivingAndLivingPlus()
    {
        PolicyImpl policy = new PolicyImpl();

        policy.setPolicyType(PolicyType.STAND_ALONE_LIVING);
        Collection<BenefitOptionType> options = BenefitType.LIVING.getOptions(policy, AccountStructureType.SUPER);
        assertNotNull(options.size());
        assertThat(options, containsInAnyOrder(BenefitOptionType.LIVING_REINSTATEMENT));

        policy.setPolicyType(PolicyType.STAND_ALONE_LIVING);
        options = BenefitType.LIVING_PLUS.getOptions(policy, AccountStructureType.SUPER);
        assertNotNull(options.size());
        assertThat(options, containsInAnyOrder(BenefitOptionType.LIVING_REINSTATEMENT));

        policy.setPolicyType(PolicyType.TERM_LIFE);
        options = BenefitType.LIVING_PLUS.getOptions(policy, AccountStructureType.SUPER);
        assertNotNull(options.size());
        assertThat(options, containsInAnyOrder(BenefitOptionType.LIVING_REINSTATEMENT,
                                                BenefitOptionType.LIVING_DOUBLE,
                                                BenefitOptionType.LIVING_BUSINESS_COVER));
    }
}