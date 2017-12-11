package com.bt.nextgen.core.security.api.service;

import com.bt.nextgen.api.subscriptions.model.SubscriptionDto;
import com.bt.nextgen.api.subscriptions.model.WorkFlowStatusDto;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.AccountSubType;
import com.bt.nextgen.service.avaloq.account.PensionAccountDetailImpl;
import com.bt.nextgen.service.avaloq.account.TransactionPermission;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.avaloq.userprofile.JobProfileImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.account.BlockCode;
import com.btfin.panorama.service.integration.account.PersonRelation;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerRole;
import com.btfin.panorama.service.integration.broker.BrokerType;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.broker.JobAuthorizationRole;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PermissionAccountDtoServiceBase {
    protected Broker getBroker(final BrokerType brokerType, final String brokerKey) {
        Broker broker = mock(Broker.class);
        when(broker.getKey()).thenReturn(BrokerKey.valueOf(brokerKey));
        when(broker.getBrokerType()).thenReturn(brokerType);
        return broker;
    }

    WrapAccountDetail getBlockedAccount(BlockCode blockCode, String customerId, BrokerKey brokerKey,
                                        TransactionPermission userPermission) {
        WrapAccountDetailImpl account = new WrapAccountDetailImpl();
        account.setAccountKey(AccountKey.valueOf("11918"));
        HashMap<ClientKey, List<BlockCode>> map = new HashMap<>();
        ArrayList<BlockCode> blockedCodeList = new ArrayList<>();
        blockedCodeList.add(blockCode);
        map.put(ClientKey.valueOf("123"), blockedCodeList);
        account.setBlockedReason(map);
        account.setAssociatedPersons(getAssociatedPersons(customerId, userPermission));
        account.setAdviserKey(brokerKey);
        account.setAccountStructureType(AccountStructureType.Trust);
        return account;
    }

    WrapAccountDetail getClosedAccount(String customerId, BrokerKey brokerKey, TransactionPermission userPermission) {
        WrapAccountDetailImpl account = new WrapAccountDetailImpl();
        account.setAccountKey(AccountKey.valueOf("11918"));
        account.setAssociatedPersons(getAssociatedPersons(customerId, userPermission));
        account.setAccountStatus(AccountStatus.CLOSE);
        account.setAdviserKey(brokerKey);
        account.setAccountStructureType(AccountStructureType.Trust);
        return account;
    }

    WrapAccountDetail getNonBlockedAccount(String customerId, BrokerKey adviserKey,
                                           AccountStructureType accountStructureType, TransactionPermission userPermission) {
        WrapAccountDetailImpl account = (WrapAccountDetailImpl) getNonBlockedAccount(customerId, adviserKey, userPermission);
        account.setAccountStructureType(accountStructureType);
        return account;
    }

    WrapAccountDetail getNonBlockedAccount(String customerId, BrokerKey adviserKey, TransactionPermission userPermission) {
        WrapAccountDetailImpl account = new WrapAccountDetailImpl();
        account.setAccountKey(AccountKey.valueOf("11918"));
        account.setAdviserKey(adviserKey);
        account.setAdviserPersonId(ClientKey.valueOf(customerId));
        account.setAdviserPermissions(Collections.singleton(TransactionPermission.Payments_Deposits));
        account.setAssociatedPersons(getAssociatedPersons(customerId, userPermission));
        account.setAccountStructureType(AccountStructureType.Trust);
        account.setOpen(true);
        return account;
    }

    WrapAccountDetail getNonBlockedAccountWithAccountMaintenance(String customerId, BrokerKey adviserKey,
                                                                 TransactionPermission userPermission) {
        WrapAccountDetailImpl account = new WrapAccountDetailImpl();
        account.setAccountKey(AccountKey.valueOf("11918"));
        account.setAdviserKey(adviserKey);
        account.setAdviserPersonId(ClientKey.valueOf(customerId));
        account.setAssociatedPersons(getAssociatedPersons(customerId, userPermission));
        account.setAccountStructureType(AccountStructureType.Trust);
        return account;
    }

    protected UserProfile getProfile(final JobRole role, final String jobId, final String customerId, UserExperience userExperience,
                                     FunctionalRole... roles) {
        UserInformationImpl user = new UserInformationImpl();
        user.setClientKey(ClientKey.valueOf(customerId));
        JobProfileImpl job = new JobProfileImpl();
        job.setJobRole(role);
        job.setJob(JobKey.valueOf(jobId));
        job.setUserExperience(userExperience);
        if (CollectionUtils.isNotEmpty(Arrays.asList(roles))) {
            user.setFunctionalRoles(Arrays.asList(roles));
        } else {
            user.setFunctionalRoles(Arrays.asList(FunctionalRole.Add_remove_update_adviser_role_on_user,
                    FunctionalRole.Make_a_BPAYPay_Anyone_Payment, FunctionalRole.Create_a_complaint_feedback,
                    FunctionalRole.View_Client_Orders, FunctionalRole.Trade_entry, FunctionalRole.View_account_reports,
                    FunctionalRole.Update_super_beneficiaries, FunctionalRole.Pension_Commencement,
                    FunctionalRole.Personal_Tax_Deduction, FunctionalRole.Make_a_payment_linked_accounts));
        }
        return new UserProfileAdapterImpl(user, job);
    }

    UserProfile getProfileWithNoInvestmentOrderPermissions(final JobRole role, final String jobId,
                                                           final String customerId) {
        UserInformationImpl user = new UserInformationImpl();
        user.setClientKey(ClientKey.valueOf(customerId));
        user.setFunctionalRoles(Arrays.asList(FunctionalRole.Add_remove_update_adviser_role_on_user,
                FunctionalRole.Make_a_BPAYPay_Anyone_Payment, FunctionalRole.Create_a_complaint_feedback));
        JobProfileImpl job = new JobProfileImpl();
        job.setJobRole(role);
        job.setJob(JobKey.valueOf(jobId));

        return new UserProfileAdapterImpl(user, job);
    }

    Map<ClientKey, PersonRelation> getAssociatedPersons(String customerId, TransactionPermission userPermission) {
        Map<ClientKey, PersonRelation> associatedPersons = new HashMap<>();
        associatedPersons.put(ClientKey.valueOf(customerId), getPersonRelation(customerId, userPermission));
        return associatedPersons;
    }

    private PersonRelation getPersonRelation(final String customerId, final TransactionPermission userPermission) {
        PersonRelation person = mock(PersonRelation.class);
        when(person.getClientKey()).thenReturn(ClientKey.valueOf(customerId));
        when(person.getPermissions()).thenReturn(getTransactionPermissions(userPermission));
        when(person.isAdviser()).thenReturn(Boolean.FALSE);
        when(person.isApprover()).thenReturn(Boolean.FALSE);
        when(person.isPrimaryContact()).thenReturn(Boolean.FALSE);
        return person;
    }

    private Set<TransactionPermission> getTransactionPermissions(TransactionPermission userPermission) {
        Set<TransactionPermission> transactionPermissions = new HashSet<>();
        transactionPermissions.add(userPermission);
        return transactionPermissions;
    }

    protected BrokerUser getBrokerUser(final JobRole jobRole, final BrokerKey brokerKey,
                                       final JobAuthorizationRole authorizationRole) {
        BrokerUser brokerUser = mock(BrokerUser.class);
        List<BrokerRole> roles = Arrays.asList(getBrokerRole(jobRole, brokerKey, authorizationRole));
        when(brokerUser.getRoles()).thenReturn(roles);
        when(brokerUser.isRegisteredOnline()).thenReturn(false);
        when(brokerUser.isRegistrationOnline()).thenReturn(false);
        when(brokerUser.getAge()).thenReturn(0);
        return brokerUser;
    }

    private BrokerRole getBrokerRole(final JobRole jobRole, final BrokerKey brokerKey,
                                     final JobAuthorizationRole authorizationRole) {
        final BrokerRole brokerRole = mock(BrokerRole.class);
        when(brokerRole.getRole()).thenReturn(jobRole);
        when(brokerRole.getAuthorizationRole()).thenReturn(authorizationRole);
        when(brokerRole.getKey()).thenReturn(brokerKey);
        return brokerRole;
    }

    List<FunctionalRole> getFunctionalRoleList(FunctionalRole... functionalRoles) {
        final List<FunctionalRole> roleList = new ArrayList<>();
        Collections.addAll(roleList, functionalRoles);
        return roleList;
    }

    List<SubscriptionDto> getDoneSubscriptionList(String status) {
        List<SubscriptionDto> subsList = new ArrayList<>();
        SubscriptionDto dto = new SubscriptionDto();
        dto.setStatus(status);
        dto.setServiceType("FA");
        dto.setAccountName("Person1");
        dto.setAdviserFirstName("Person1");
        dto.setServiceName("Fund Admin");
        List<WorkFlowStatusDto> states = new ArrayList<>();
        WorkFlowStatusDto statusDto = new WorkFlowStatusDto();
        statusDto.setState("Application requested");
        statusDto.setStatus("Complete");
        states.add(statusDto);
        dto.setStates(states);
        subsList.add(dto);
        return subsList;
    }

    public WrapAccountDetail getSuperAccount(String customerId, BrokerKey adviserKey, TransactionPermission userPermission, AccountSubType accSubType) {
        WrapAccountDetailImpl account = new WrapAccountDetailImpl();
        account.setAccountKey(AccountKey.valueOf("11918"));
        account.setAdviserKey(adviserKey);
        account.setAdviserPersonId(ClientKey.valueOf(customerId));
        account.setAdviserPermissions(Collections.singleton(TransactionPermission.Payments_Deposits));
        account.setAssociatedPersons(getAssociatedPersons(customerId, userPermission));
        account.setAccountStructureType(AccountStructureType.SUPER);
        account.setSuperAccountSubType(accSubType);
        return account;
    }

    public PensionAccountDetailImpl getSuperPensionAccount(String customerId, BrokerKey adviserKey, TransactionPermission userPermission, AccountSubType accSubType, DateTime date) {
        PensionAccountDetailImpl account = new PensionAccountDetailImpl();
        account.setAccountKey(AccountKey.valueOf("11918"));
        account.setAdviserKey(adviserKey);
        account.setAdviserPersonId(ClientKey.valueOf(customerId));
        account.setAdviserPermissions(Collections.singleton(TransactionPermission.Payments_Deposits));
        account.setAssociatedPersons(getAssociatedPersons(customerId, userPermission));
        account.setAccountStructureType(AccountStructureType.SUPER);
        account.setSuperAccountSubType(accSubType);
        account.setCommenceDate(date);
        return account;
    }

    public WrapAccountDetail getCompanyRegistration(String customerId, BrokerKey adviserKey, TransactionPermission userPermission) {

        Set<TransactionPermission> permissionSet = new HashSet<>();
        permissionSet.add(TransactionPermission.Payments_Deposits);
        permissionSet.add(TransactionPermission.Company_Registration);
        permissionSet.add(TransactionPermission.Account_Maintenance);
        permissionSet.add(TransactionPermission.No_Transaction);
        permissionSet.add(TransactionPermission.Payments_Deposits);
        permissionSet.add(TransactionPermission.Payments_Deposits_To_Linked_Accounts);

        WrapAccountDetailImpl account = new WrapAccountDetailImpl();
        account.setAccountKey(AccountKey.valueOf("11918"));
        account.setAdviserKey(adviserKey);
        account.setAdviserPersonId(ClientKey.valueOf(customerId));
        account.setAdviserPermissions(permissionSet);
        account.setAssociatedPersons(getAssociatedPersons(customerId, userPermission));
        account.setAccountStructureType(AccountStructureType.SUPER);
        return account;
    }

    UserProfile getProfileWithNoViewAccountReportPermissions(final JobRole role, final String jobId, final String customerId,
                                                             UserExperience userExperience,
                                                             FunctionalRole... roles) {
        UserInformationImpl user = new UserInformationImpl();
        user.setClientKey(ClientKey.valueOf(customerId));
        JobProfileImpl job = new JobProfileImpl();
        job.setJobRole(role);
        job.setJob(JobKey.valueOf(jobId));
        job.setUserExperience(userExperience);
        if (CollectionUtils.isNotEmpty(Arrays.asList(roles))) {
            user.setFunctionalRoles(Arrays.asList(roles));
        } else {
            user.setFunctionalRoles(Arrays.asList(FunctionalRole.Add_remove_update_adviser_role_on_user,
                    FunctionalRole.Make_a_BPAYPay_Anyone_Payment, FunctionalRole.Create_a_complaint_feedback,
                    FunctionalRole.View_Client_Orders, FunctionalRole.Trade_entry, FunctionalRole.Update_super_beneficiaries,
                    FunctionalRole.Pension_Commencement, FunctionalRole.Personal_Tax_Deduction));
        }
        return new UserProfileAdapterImpl(user, job);
    }

    UserProfile getProfileWithNoTaxDeductionUpdatePermissions(final JobRole role, final String jobId, final String customerId,
                                                              UserExperience userExperience,
                                                              FunctionalRole... roles) {
        UserInformationImpl user = new UserInformationImpl();
        user.setClientKey(ClientKey.valueOf(customerId));
        JobProfileImpl job = new JobProfileImpl();
        job.setJobRole(role);
        job.setJob(JobKey.valueOf(jobId));
        job.setUserExperience(userExperience);
        if (CollectionUtils.isNotEmpty(Arrays.asList(roles))) {
            user.setFunctionalRoles(Arrays.asList(roles));
        } else {
            user.setFunctionalRoles(Arrays.asList(FunctionalRole.Add_remove_update_adviser_role_on_user,
                    FunctionalRole.Make_a_BPAYPay_Anyone_Payment, FunctionalRole.Create_a_complaint_feedback,
                    FunctionalRole.View_Client_Orders, FunctionalRole.Trade_entry, FunctionalRole.Update_super_beneficiaries,
                    FunctionalRole.Pension_Commencement));
        }
        return new UserProfileAdapterImpl(user, job);
    }

    UserProfile getProfileWithNoCommencePensionUpdatePermissions(final JobRole role, final String jobId, final String customerId,
                                                                 UserExperience userExperience,
                                                                 FunctionalRole... roles) {
        UserInformationImpl user = new UserInformationImpl();
        user.setClientKey(ClientKey.valueOf(customerId));
        JobProfileImpl job = new JobProfileImpl();
        job.setJobRole(role);
        job.setJob(JobKey.valueOf(jobId));
        job.setUserExperience(userExperience);
        if (CollectionUtils.isNotEmpty(Arrays.asList(roles))) {
            user.setFunctionalRoles(Arrays.asList(roles));
        } else {
            user.setFunctionalRoles(Arrays.asList(FunctionalRole.Add_remove_update_adviser_role_on_user,
                    FunctionalRole.Make_a_BPAYPay_Anyone_Payment, FunctionalRole.Create_a_complaint_feedback,
                    FunctionalRole.View_Client_Orders, FunctionalRole.Trade_entry, FunctionalRole.Update_super_beneficiaries,
                    FunctionalRole.Personal_Tax_Deduction));
        }
        return new UserProfileAdapterImpl(user, job);
    }

    UserProfile getProfileWithNoPaymentCreatePermissions(final JobRole role, final String jobId, final String customerId,
                                                         UserExperience userExperience,
                                                         FunctionalRole... roles) {
        UserInformationImpl user = new UserInformationImpl();
        user.setClientKey(ClientKey.valueOf(customerId));
        JobProfileImpl job = new JobProfileImpl();
        job.setJobRole(role);
        job.setJob(JobKey.valueOf(jobId));
        job.setUserExperience(userExperience);
        if (CollectionUtils.isNotEmpty(Arrays.asList(roles))) {
            user.setFunctionalRoles(Arrays.asList(roles));
        } else {
            user.setFunctionalRoles(Arrays.asList(FunctionalRole.Add_remove_update_adviser_role_on_user,
                    FunctionalRole.Make_a_BPAYPay_Anyone_Payment, FunctionalRole.Create_a_complaint_feedback,
                    FunctionalRole.View_Client_Orders, FunctionalRole.Trade_entry, FunctionalRole.Update_super_beneficiaries,
                    FunctionalRole.Personal_Tax_Deduction, FunctionalRole.Pension_Commencement));
        }
        return new UserProfileAdapterImpl(user, job);
    }


}

