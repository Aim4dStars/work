package com.bt.nextgen.service.avaloq.account;

import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.avaloq.domain.SmsfImpl;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.service.integration.account.PersonRelation;
import com.btfin.panorama.core.security.integration.domain.InvestorDetail;
import com.bt.nextgen.service.integration.domain.InvestorRole;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

public class WrapAccountDetailBuilderTest {

    @Test
    public void shouldAddMemberToPersonRolesIfPersonIsAMember() throws Exception {
        WrapAccountDetailBuilder wrapAccountDetailBuilder = new WrapAccountDetailBuilder(Arrays.asList(getWrapAccountDetailWithClientsAndRoles()));

        List<? extends WrapAccountDetail> wrapAccountDetails = wrapAccountDetailBuilder.buildAccountDetail();

        assertThat(wrapAccountDetails, hasSize(1));
        Map<ClientKey, PersonRelation> associatedPersons = wrapAccountDetails.get(0).getAssociatedPersons();

        assertThat(associatedPersons.get(ClientKey.valueOf("CLIENT_1")).getPersonRoles(), hasItem(InvestorRole.Member));
        assertThat(associatedPersons.get(ClientKey.valueOf("CLIENT_2")).getPersonRoles(), not(hasItem(InvestorRole.Member)));
        assertThat(associatedPersons.get(ClientKey.valueOf("CLIENT_3")).getPersonRoles(), not(hasItem(InvestorRole.Member)));
        assertThat(associatedPersons.get(ClientKey.valueOf("CLIENT_4")).getPersonRoles(), hasItem(InvestorRole.Member));
    }

    @Test
    public void shouldAddBeneficiaryToPersonRolesIfPersonIsABeneficiary() throws Exception {
        WrapAccountDetailBuilder wrapAccountDetailBuilder = new WrapAccountDetailBuilder(Arrays.asList(getWrapAccountDetailWithClientsAndRoles()));

        List<? extends WrapAccountDetail> wrapAccountDetails = wrapAccountDetailBuilder.buildAccountDetail();

        assertThat(wrapAccountDetails, hasSize(1));
        Map<ClientKey, PersonRelation> associatedPersons = wrapAccountDetails.get(0).getAssociatedPersons();

        assertThat(associatedPersons.get(ClientKey.valueOf("CLIENT_1")).getPersonRoles(), not(hasItem(InvestorRole.Beneficiary)));
        assertThat(associatedPersons.get(ClientKey.valueOf("CLIENT_2")).getPersonRoles(), not(hasItem(InvestorRole.Beneficiary)));
        assertThat(associatedPersons.get(ClientKey.valueOf("CLIENT_3")).getPersonRoles(), hasItem(InvestorRole.Beneficiary));
        assertThat(associatedPersons.get(ClientKey.valueOf("CLIENT_4")).getPersonRoles(), hasItem(InvestorRole.Beneficiary));
    }

    @Test
    public void shouldAddShareholderToPersonRolesIfPersonIsAShareholder() throws Exception {
        WrapAccountDetailBuilder wrapAccountDetailBuilder = new WrapAccountDetailBuilder(Arrays.asList(getWrapAccountDetailWithClientsAndRoles()));

        List<? extends WrapAccountDetail> wrapAccountDetails = wrapAccountDetailBuilder.buildAccountDetail();

        assertThat(wrapAccountDetails, hasSize(1));
        Map<ClientKey, PersonRelation> associatedPersons = wrapAccountDetails.get(0).getAssociatedPersons();

        assertThat(associatedPersons.get(ClientKey.valueOf("CLIENT_1")).getPersonRoles(), not(hasItem(InvestorRole.Shareholder)));
        assertThat(associatedPersons.get(ClientKey.valueOf("CLIENT_2")).getPersonRoles(), hasItem(InvestorRole.Shareholder));
        assertThat(associatedPersons.get(ClientKey.valueOf("CLIENT_3")).getPersonRoles(), not(hasItem(InvestorRole.Shareholder)));
        assertThat(associatedPersons.get(ClientKey.valueOf("CLIENT_4")).getPersonRoles(), hasItem(InvestorRole.Shareholder));
    }

    @Test
    public void shouldAddShareholderToLinkedClientsRolesIfPersonIsAShareholder() throws Exception {
        WrapAccountDetailBuilder wrapAccountDetailBuilder = new WrapAccountDetailBuilder(Arrays.asList(getWrapAccountDetailWithClientsAndRoles()));

        List<? extends WrapAccountDetail> wrapAccountDetails = wrapAccountDetailBuilder.buildAccountDetail();

        assertThat(wrapAccountDetails, hasSize(1));
        List<InvestorDetail> linkedClients = ((SmsfImpl) ((WrapAccountDetailImpl)wrapAccountDetails.get(0)).getOwners().get(0)).getLinkedClients();

        assertThat(linkedClients.get(0).getPersonRoles(), not(hasItem(InvestorRole.Shareholder)));
        assertThat(linkedClients.get(1).getPersonRoles(), hasItem(InvestorRole.Shareholder));
        assertThat(linkedClients.get(2).getPersonRoles(), not(hasItem(InvestorRole.Shareholder)));
        assertThat(linkedClients.get(3).getPersonRoles(), hasItem(InvestorRole.Shareholder));
    }

    @Test
    public void shouldAddMemberToLinkedClientsRolesIfPersonIsAMember() throws Exception {
        WrapAccountDetailBuilder wrapAccountDetailBuilder = new WrapAccountDetailBuilder(Arrays.asList(getWrapAccountDetailWithClientsAndRoles()));

        List<? extends WrapAccountDetail> wrapAccountDetails = wrapAccountDetailBuilder.buildAccountDetail();

        assertThat(wrapAccountDetails, hasSize(1));
        List<InvestorDetail> linkedClients = ((SmsfImpl) ((WrapAccountDetailImpl)wrapAccountDetails.get(0)).getOwners().get(0)).getLinkedClients();

        assertThat(linkedClients.get(0).getPersonRoles(), hasItem(InvestorRole.Member));
        assertThat(linkedClients.get(1).getPersonRoles(), not(hasItem(InvestorRole.Member)));
        assertThat(linkedClients.get(2).getPersonRoles(), not(hasItem(InvestorRole.Member)));
        assertThat(linkedClients.get(3).getPersonRoles(), hasItem(InvestorRole.Member));
    }

    @Test
    public void shouldAddBeneficiaryToLinkedClientsRolesIfPersonIsABeneficiary() throws Exception {
        WrapAccountDetailBuilder wrapAccountDetailBuilder = new WrapAccountDetailBuilder(Arrays.asList(getWrapAccountDetailWithClientsAndRoles()));

        List<? extends WrapAccountDetail> wrapAccountDetails = wrapAccountDetailBuilder.buildAccountDetail();

        assertThat(wrapAccountDetails, hasSize(1));
        List<InvestorDetail> linkedClients = ((SmsfImpl) ((WrapAccountDetailImpl)wrapAccountDetails.get(0)).getOwners().get(0)).getLinkedClients();

        assertThat(linkedClients.get(0).getPersonRoles(), not(hasItem(InvestorRole.Beneficiary)));
        assertThat(linkedClients.get(1).getPersonRoles(), not(hasItem(InvestorRole.Beneficiary)));
        assertThat(linkedClients.get(2).getPersonRoles(), hasItem(InvestorRole.Beneficiary));
        assertThat(linkedClients.get(3).getPersonRoles(), hasItem(InvestorRole.Beneficiary));
    }

    @Test
    public void shouldAddBenOwnerToLinkedClientRolesIfPersonIsABenOwner() throws Exception {
        WrapAccountDetailBuilder wrapAccountDetailBuilder = new WrapAccountDetailBuilder(Arrays.asList(getWrapAccountDetailWithClientsAndRoles()));

        List<? extends WrapAccountDetail> wrapAccountDetails = wrapAccountDetailBuilder.buildAccountDetail();

        assertThat(wrapAccountDetails, hasSize(1));
        List<InvestorDetail> linkedClients = ((SmsfImpl) ((WrapAccountDetailImpl)wrapAccountDetails.get(0)).getOwners().get(0)).getLinkedClients();

        assertThat(linkedClients.get(0).getPersonRoles(), not(hasItem(InvestorRole.Beneficiary)));
        assertThat(linkedClients.get(1).getPersonRoles(), not(hasItem(InvestorRole.Beneficiary)));
        assertThat(linkedClients.get(2).getPersonRoles(), hasItem(InvestorRole.BeneficialOwner));
        assertThat(linkedClients.get(3).getPersonRoles(), hasItem(InvestorRole.BeneficialOwner));
    }

    @Test
    public void shouldAddDirectorToLinkedClientsRolesIfPersonIsADirector() throws Exception {
        WrapAccountDetailBuilder wrapAccountDetailBuilder = new WrapAccountDetailBuilder(Arrays.asList(getWrapAccountDetailWithClientsAndRoles()));

        List<? extends WrapAccountDetail> wrapAccountDetails = wrapAccountDetailBuilder.buildAccountDetail();

        assertThat(wrapAccountDetails, hasSize(1));
        List<InvestorDetail> linkedClients = ((SmsfImpl) ((WrapAccountDetailImpl)wrapAccountDetails.get(0)).getOwners().get(0)).getLinkedClients();

        assertThat(linkedClients.get(0).getPersonRoles(), hasItem(InvestorRole.Director));
        assertThat(linkedClients.get(1).getPersonRoles(), hasItem(InvestorRole.Director));
        assertThat(linkedClients.get(2).getPersonRoles(), hasItem(InvestorRole.Director));
        assertThat(linkedClients.get(3).getPersonRoles(), hasItem(InvestorRole.Director));
    }

    private WrapAccountDetailImpl getWrapAccountDetailWithClientsAndRoles(){
        WrapAccountDetailImpl wrapAccountDetail = new WrapAccountDetailImpl();

        IndividualDetailImpl client1 = new IndividualDetailImpl();
        client1.setClientKey(ClientKey.valueOf("CLIENT_1"));
        client1.setMember(true);
        client1.setAssocRoleId(InvestorRole.Director);

        IndividualDetailImpl client2 = new IndividualDetailImpl();
        client2.setClientKey(ClientKey.valueOf("CLIENT_2"));
        client2.setShareholder(true);
        client2.setAssocRoleId(InvestorRole.Director);

        IndividualDetailImpl client3 = new IndividualDetailImpl();
        client3.setClientKey(ClientKey.valueOf("CLIENT_3"));
        client3.setBeneficiary(true);
        client3.setBeneficialOwner(true);
        client3.setAssocRoleId(InvestorRole.Director);

        IndividualDetailImpl client4 = new IndividualDetailImpl();
        client4.setClientKey(ClientKey.valueOf("CLIENT_4"));
        client4.setBeneficiary(true);
        client4.setShareholder(true);
        client4.setBeneficialOwner(true);
        client4.setMember(true);
        client4.setAssocRoleId(InvestorRole.Director);

        wrapAccountDetail.setAllAssociatedPersons(Arrays.<Client>asList(client1, client2, client3, client4));
        SmsfImpl owner = new SmsfImpl();
        wrapAccountDetail.setOwners(Arrays.<Client>asList(owner));
        wrapAccountDetail.setAdviserPersonId(ClientKey.valueOf("Adviser"));
        return wrapAccountDetail;
    }
}