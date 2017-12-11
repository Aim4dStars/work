package com.bt.nextgen.service.avaloq.corporateaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionHelperImpl;
import com.bt.nextgen.api.corporateaction.v1.service.EffectiveCorporateActionType;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionConverterConstants;
import com.bt.nextgen.core.security.UserRole;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.PensionAccountDetailImpl;
import com.bt.nextgen.service.avaloq.account.PensionType;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOfferType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOptionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionSecurityExchangeType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionType;
import com.btfin.panorama.core.security.integration.userinformation.JobPermission;
import com.btfin.panorama.core.security.integration.userinformation.UserInformationIntegrationService;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionHelperImplTest {
    @InjectMocks
    private CorporateActionHelperImpl helper;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private UserInformationIntegrationService userInformationIntegrationService;

    @Before
    public void setup() {
    }

    @Test
    public void testEffectiveCorporateActionTypeByIndividualParams() {
        EffectiveCorporateActionType effectiveCorporateActionType = helper.getEffectiveCorporateActionType(CorporateActionType.MULTI_BLOCK,
                CorporateActionOfferType.PUBLIC_OFFER, null, false);

        assertNotNull(effectiveCorporateActionType);
        assertEquals(CorporateActionType.MULTI_BLOCK, effectiveCorporateActionType.getType());
        assertEquals(CorporateActionOfferType.PUBLIC_OFFER.name(), effectiveCorporateActionType.getCode());
        assertEquals(CorporateActionOfferType.PUBLIC_OFFER.getDescription(), effectiveCorporateActionType.getDescription());

        assertNull(helper.getEffectiveCorporateActionType(CorporateActionType.MULTI_BLOCK, null, null, false));
        assertEquals(CorporateActionType.EXERCISE_RIGHTS,
                helper.getEffectiveCorporateActionType(CorporateActionType.EXERCISE_RIGHTS, null, null, false).getType());
        assertEquals(CorporateActionType.SHARE_PURCHASE_PLAN,
                helper.getEffectiveCorporateActionType(CorporateActionType.SHARE_PURCHASE_PLAN, null, null, false).getType());
        assertEquals(CorporateActionType.SHARE_PURCHASE_PLAN,
                helper.getEffectiveCorporateActionType(CorporateActionType.SHARE_PURCHASE_PLAN, null, null, false).getType());
        assertEquals(CorporateActionType.NON_PRO_RATA_PRIORITY_OFFER,
                helper.getEffectiveCorporateActionType(CorporateActionType.SHARE_PURCHASE_PLAN, null, null, true).getType());
        assertEquals(CorporateActionType.SECURITY_EXCHANGE_EXCHANGE,
                helper.getEffectiveCorporateActionType(CorporateActionType.SECURITY_EXCHANGE_FRACTION, null,
                        CorporateActionSecurityExchangeType.SECURITY_EXCHANGE, false).getType());
        assertEquals(CorporateActionType.SECURITY_EXCHANGE_FRACTION,
                helper.getEffectiveCorporateActionType(CorporateActionType.SECURITY_EXCHANGE_FRACTION, null, null, false).getType());
        assertNull(helper.getEffectiveCorporateActionType(null, null, null, false));
    }

    @Test
    public void testGenerateCorporateActionStatus() {
        assertEquals(CorporateActionStatus.CLOSED,
                helper.generateCorporateActionStatus(CorporateActionGroup.VOLUNTARY, CorporateActionStatus.CLOSED, null,
                        null, null));

        assertEquals(CorporateActionStatus.CLOSED,
                helper.generateCorporateActionStatus(CorporateActionGroup.VOLUNTARY, CorporateActionStatus.CLOSED,
                        (new DateTime()).minusDays(1), new DateTime(), new DateTime()));

        assertEquals(CorporateActionStatus.CLOSED,
                helper.generateCorporateActionStatus(CorporateActionGroup.VOLUNTARY, CorporateActionStatus.OPEN,
                        (new DateTime()).minusDays(1), new DateTime(), new DateTime()));

        assertEquals(CorporateActionStatus.OPEN,
                helper.generateCorporateActionStatus(CorporateActionGroup.VOLUNTARY, CorporateActionStatus.OPEN,
                        (new DateTime()).plusDays(1), new DateTime(), new DateTime()));

        assertEquals(CorporateActionStatus.CLOSED,
                helper.generateCorporateActionStatus(CorporateActionGroup.VOLUNTARY, CorporateActionStatus.PENDING,
                        (new DateTime()).plusDays(1), new DateTime(), new DateTime()));

        assertEquals(CorporateActionStatus.CLOSED,
                helper.generateCorporateActionStatus(CorporateActionGroup.MANDATORY, CorporateActionStatus.UNDER_REVIEW,
                        new DateTime(), (new DateTime()).minusDays(1), new DateTime()));

        DateTime now = new DateTime();

        assertEquals(CorporateActionStatus.CLOSED,
                helper.generateCorporateActionStatus(CorporateActionGroup.VOLUNTARY, CorporateActionStatus.PENDING, now, now, now));

        assertEquals(CorporateActionStatus.OPEN,
                helper.generateCorporateActionStatus(CorporateActionGroup.VOLUNTARY, CorporateActionStatus.OPEN, now, now, now));

        assertEquals(CorporateActionStatus.OPEN,
                helper.generateCorporateActionStatus(CorporateActionGroup.VOLUNTARY, CorporateActionStatus.OPEN, now.plusDays(1), now,
                        now));

        assertEquals(CorporateActionStatus.CLOSED,
                helper.generateCorporateActionStatus(CorporateActionGroup.VOLUNTARY, CorporateActionStatus.OPEN, now.minusDays(1), now,
                        now));

        assertEquals(CorporateActionStatus.OPEN,
                helper.generateCorporateActionStatus(CorporateActionGroup.MANDATORY, CorporateActionStatus.OPEN, now, null, now));

        assertEquals(CorporateActionStatus.CLOSED,
                helper.generateCorporateActionStatus(CorporateActionGroup.MANDATORY, CorporateActionStatus.OPEN, now, now, now));

        assertEquals(CorporateActionStatus.CLOSED,
                helper.generateCorporateActionStatus(CorporateActionGroup.MANDATORY, CorporateActionStatus.OPEN, now, now.minusDays(1),
                        now));

        assertEquals(CorporateActionStatus.OPEN,
                helper.generateCorporateActionStatus(CorporateActionGroup.MANDATORY, CorporateActionStatus.OPEN, now, now.plusDays(1),
                        now));
    }

    @Test
    public void testFilterByManagedPortfolioAccounts() {
        CorporateActionAccount mpAccount = mock(CorporateActionAccount.class);
        when(mpAccount.getAccountId()).thenReturn("mpAccount");
        when(mpAccount.getIpsId()).thenReturn("ipsId");
        when(mpAccount.getContainerType()).thenReturn(ContainerType.MANAGED_PORTFOLIO);

        CorporateActionAccount shadowAccount = mock(CorporateActionAccount.class);
        when(shadowAccount.getAccountId()).thenReturn("shadowAccount");
        when(shadowAccount.getIpsId()).thenReturn("ipsId");
        when(shadowAccount.getContainerType()).thenReturn(ContainerType.SHADOW_MANAGED_PORTFOLIO);

        List<CorporateActionAccount> filteredAccounts = helper.filterByManagedPortfolioAccounts(
                Arrays.asList(mpAccount, shadowAccount), "ipsId");

        assertNotNull(filteredAccounts);
        assertEquals(1, filteredAccounts.size());
        assertEquals("mpAccount", filteredAccounts.get(0).getAccountId());
    }

    @Test
    public void testAllowPartialElection() {
        assertTrue(helper.allowPartialElection(CorporateActionType.MULTI_BLOCK, CorporateActionOfferType.REINVEST));
        assertTrue(helper.allowPartialElection(CorporateActionType.MULTI_BLOCK, CorporateActionOfferType.CONVERSION));
        assertTrue(helper.allowPartialElection(CorporateActionType.MULTI_BLOCK, CorporateActionOfferType.EXCHANGE));

        assertFalse(helper.allowPartialElection(CorporateActionType.MULTI_BLOCK, CorporateActionOfferType.CAPITAL_CALL));
        assertFalse(helper.allowPartialElection(CorporateActionType.EXERCISE_RIGHTS, null));
    }

    @Test
    public void testGetEffectiveCorporateActionTypeByCorporateActionDetails() {
        CorporateActionDetailsImpl corporateActionDetails = new CorporateActionDetailsImpl();
        corporateActionDetails.setCorporateActionType(CorporateActionType.SHARE_PURCHASE_PLAN);
        CorporateActionOptionImpl option = new CorporateActionOptionImpl();
        List<CorporateActionOption> options = new ArrayList<CorporateActionOption>();
        corporateActionDetails.setOptions(options);

        EffectiveCorporateActionType effectiveCorporateActionType = helper.getEffectiveCorporateActionType(corporateActionDetails);
        assertNotNull(effectiveCorporateActionType);
        assertEquals(CorporateActionType.SHARE_PURCHASE_PLAN, effectiveCorporateActionType.getType());

        option.setKey(CorporateActionOptionKey.IS_NON_PRO_RATA.getCode());
        option.setValue(CorporateActionConverterConstants.OPTION_VALUE_YES);
        options.add(option);

        effectiveCorporateActionType = helper.getEffectiveCorporateActionType(corporateActionDetails);
        assertNotNull(effectiveCorporateActionType);
        assertEquals(CorporateActionType.NON_PRO_RATA_PRIORITY_OFFER, effectiveCorporateActionType.getType());

        options.clear();
        option.setKey(CorporateActionOptionKey.IS_NON_PRO_RATA.getCode());
        option.setValue(CorporateActionConverterConstants.OPTION_VALUE_NO);
        options.add(option);

        effectiveCorporateActionType = helper.getEffectiveCorporateActionType(corporateActionDetails);
        assertNotNull(effectiveCorporateActionType);
        assertEquals(CorporateActionType.SHARE_PURCHASE_PLAN, effectiveCorporateActionType.getType());

        corporateActionDetails.setCorporateActionType(CorporateActionType.MULTI_BLOCK);
        corporateActionDetails.setCorporateActionOfferType(CorporateActionOfferType.PUBLIC_OFFER);
        effectiveCorporateActionType = helper.getEffectiveCorporateActionType(corporateActionDetails);
        assertNotNull(effectiveCorporateActionType);
        assertNotEquals(CorporateActionType.NON_PRO_RATA_PRIORITY_OFFER, effectiveCorporateActionType.getType());

        Assert.assertEquals(true, option.hasValue());
        option.setValue("123.0");
        Assert.assertNotNull(option.getBigDecimalValue());
        option.setValue(null);
        Assert.assertNull(option.getBigDecimalValue());
    }

    @Test
    public void testFilterByShadowPortfolioAccounts() {
        CorporateActionAccount mpAccount = mock(CorporateActionAccount.class);
        when(mpAccount.getAccountId()).thenReturn("mpAccount");
        when(mpAccount.getIpsId()).thenReturn("ipsId");
        when(mpAccount.getContainerType()).thenReturn(ContainerType.MANAGED_PORTFOLIO);

        CorporateActionAccount shadowAccount = mock(CorporateActionAccount.class);
        when(shadowAccount.getAccountId()).thenReturn("shadowAccount");
        when(shadowAccount.getIpsId()).thenReturn("ipsId");
        when(shadowAccount.getContainerType()).thenReturn(ContainerType.SHADOW_MANAGED_PORTFOLIO);

        List<CorporateActionAccount> filteredAccounts = helper.filterByShadowPortfolioAccounts(
                Arrays.asList(mpAccount, shadowAccount), "ipsId");

        assertNotNull(filteredAccounts);
        assertEquals(1, filteredAccounts.size());
        assertEquals(shadowAccount.getAccountId(), filteredAccounts.get(0).getAccountId());
    }

    @Test
    public void testGetAccountTypeDescription() {
        WrapAccount account = mock(WrapAccount.class);

        when(account.getAccountStructureType()).thenReturn(AccountStructureType.SUPER);

        assertEquals("Super", helper.getAccountTypeDescription(account));

        when(account.getAccountStructureType()).thenReturn(AccountStructureType.Individual);

        assertEquals("Individual", helper.getAccountTypeDescription(account));

        PensionAccountDetailImpl pension = mock(PensionAccountDetailImpl.class);
        when(pension.getAccountStructureType()).thenReturn(AccountStructureType.SUPER);
        when(pension.getPensionType()).thenReturn(PensionType.STANDARD);

        assertEquals("Pension", helper.getAccountTypeDescription(pension));

        when(pension.getPensionType()).thenReturn(PensionType.TTR);
        assertEquals("Pension (TTR)", helper.getAccountTypeDescription(pension));
    }


    @Test
    public void testHasUserRole_whenUserHasSpecifiedRoles_thenReturnTrue() {
        JobProfile jobProfile = mock(JobProfile.class);
        JobPermission jobPermission = mock(JobPermission.class);

        List<String> userRoles = new ArrayList<>();
        userRoles.add(UserRole.IRG_BASIC.getRole());
        userRoles.add(UserRole.TRUSTEE_BASIC.getRole());

        when(jobPermission.getUserRoles()).thenReturn(userRoles);
        when(userProfileService.getAvailableProfiles()).thenReturn(Arrays.asList(jobProfile));
        when(userInformationIntegrationService.getAvailableRoles(any(JobProfile.class), any(ServiceErrors.class)))
                .thenReturn(jobPermission);

        assertTrue(helper.hasUserRole(null, UserRole.IRG_BASIC));
        assertFalse(helper.hasUserRole(null, UserRole.USER_ROLE_INVESTOR));
    }

    @Test
    public void testHasUserRole_whenThereIsNoJobProfile_thenReturnFalse() {
        when(userProfileService.getAvailableProfiles()).thenReturn(null);

        assertFalse(helper.hasUserRole(null, UserRole.IRG_BASIC));
    }

    @Test
    public void testHasUserRole_whenThereIsNoUserRoles_thenReturnFalse() {
        JobProfile jobProfile = mock(JobProfile.class);
        JobPermission jobPermission = mock(JobPermission.class);

        when(jobPermission.getUserRoles()).thenReturn(null);
        when(userProfileService.getAvailableProfiles()).thenReturn(Arrays.asList(jobProfile));
        when(userInformationIntegrationService.getAvailableRoles(any(JobProfile.class), any(ServiceErrors.class)))
                .thenReturn(jobPermission);

        assertFalse(helper.hasUserRole(null, UserRole.IRG_BASIC));
    }
}
