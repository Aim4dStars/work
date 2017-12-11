package com.bt.nextgen.userdetails.web;

import com.bt.nextgen.core.web.model.Intermediary;
import com.bt.nextgen.core.web.model.PersonInterface;
import com.bt.nextgen.service.avaloq.StaticCodeInterface;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.integration.search.PersonResponse;
import com.bt.nextgen.service.integration.search.ProfileUserRole;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class UserDetailsConverterTest {
    /*private SrchRsp srchRsp = (SrchRsp)JaxbUtil.unmarshall("/webservices/response/UserSearchAvaloq_UT.xml", SrchRsp.class);*/

    private List<StaticCodeInterface> codes = new ArrayList<StaticCodeInterface>();

    private ProfileUserRole pmUserRole;
    private ProfileUserRole dealerUserRole;

	@Before
    public void setup() {

        pmUserRole = getUserRole(JobRole.PORTFOLIO_MANAGER);
        dealerUserRole = getUserRole(JobRole.DEALER_GROUP_MANAGER);

//		StaticCode code1 = new StaticCode("1126", "BP Account Maintenance", "BP_ACC_MAINT");
//		StaticCode code2 = new StaticCode("1", "No transaction", "NO_TRX");
//		StaticCode code3 = new StaticCode("1125", "Payments &amp;amp; Deposits to Anyone", "PAY_INPAY_ALL");
//		StaticCode code4 = new StaticCode("1124", "Payments &amp;amp; Deposits to Linked Accounts Only", "PAY_INPAY_LINK");
//		codes.add(code1);
//		codes.add(code2);
//		codes.add(code3);
//		codes.add(code4);
//		codes.add(new StaticCode("121", "Legal Person", "L"));
//		codes.add(new StaticCode("120", "Natural Person", "N"));
	}




	@Test
	@Ignore
	public void testToPersonInterface() throws Exception
	{
        // int expectedListSize = 4;
        // List <PersonInterface> interfaceList = null;//UserDetailsConverter.toPersonInterface(srchRsp.getData().getPerson());
        // assertThat(interfaceList, notNullValue());
        // assertThat(interfaceList.size(), Is.is(expectedListSize));
        // for (PersonInterface person : interfaceList)
        // {
        // assertThat(person.getFullName(), is(not(isEmptyOrNullString())));
        // assertThat(person.getOracleUser(), is(not(isEmptyOrNullString())));
        // assertThat(person.getClientId(), is(notNullValue()));
        // assertThat(person.getClientId().plainText(), is(not(isEmptyOrNullString())));
        // assertThat(person.getPrimaryDomiAddress(), is(notNullValue()));
        //
        // }
	}

    @Test
    public void test_toPersonWithEmptyCollections() {
        List<PersonInterface> results = UserDetailsConverter.toPersonInterfaceList(Collections.EMPTY_LIST);
        Assert.assertTrue(results.size() == 0);
	}

    @Test
    public void test_toPerson_withNullProfileUserRoles() {
        ProfileUserRole role = null;
        PersonResponse pr = getEmptyPersonResponse(Collections.singletonList(role));
        when(pr.getProfileUserRoles()).thenReturn(null);

        List<PersonInterface> results = UserDetailsConverter.toPersonInterfaceList(Collections.singletonList(pr));
        Assert.assertTrue(results.size() == 1);
    }

    @Test
    public void test_toPerson_withTerminatedProfileUserRoles() {
        ProfileUserRole role = getUserRole(JobRole.ASSISTANT);
        when(role.getCloseDate()).thenReturn("2000-01-01");
        PersonResponse pr = getEmptyPersonResponse(Collections.singletonList(role));
        List<PersonInterface> results = UserDetailsConverter.toPersonInterfaceList(Collections.singletonList(pr));
        Intermediary intermediary = (Intermediary) results.get(0);

        Assert.assertEquals(role.getCompanyName(), intermediary.getCompanyName());
        Assert.assertEquals(role.getDealerGroupName(), intermediary.getDealerGroupName());
        Assert.assertEquals("Admin Assistant - Terminated (01 Jan 2000)", intermediary.getRole());
    }

    @Test
    public void test_toPerson_withProfileUserRoles() {
        
        PersonResponse pr = getEmptyPersonResponse(Collections.singletonList(pmUserRole));
        List<PersonInterface> results = UserDetailsConverter.toPersonInterfaceList(Collections.singletonList(pr));
        Assert.assertTrue(results.size() == 1);
        Intermediary intermediary = (Intermediary) results.get(0);
        Assert.assertEquals("Portfolio Manager", intermediary.getRole());

        pr = getEmptyPersonResponse(Collections.singletonList(dealerUserRole));
        results = UserDetailsConverter.toPersonInterfaceList(Collections.singletonList(pr));
        Assert.assertTrue(results.size() == 1);
        intermediary = (Intermediary) results.get(0);
        Assert.assertEquals("Dealer Group Manager", intermediary.getRole());
    }

    @Test
    public void test_toPerson_withProfileUserRoles2() {

        PersonResponse pr = getPersonResponse(Collections.singletonList(pmUserRole));
        List<PersonInterface> results = UserDetailsConverter.toPersonInterfaceList(Collections.singletonList(pr));
        Assert.assertTrue(results.size() == 1);
        Intermediary intermediary = (Intermediary) results.get(0);
        Assert.assertEquals("Portfolio Manager", intermediary.getRole());
        Assert.assertEquals(pr.getLastName(), intermediary.getLastName());
        Assert.assertEquals(pr.getFirstName(), intermediary.getFirstName());
        Assert.assertEquals(pr.getMiddleName(), intermediary.getMiddleName());
        Assert.assertEquals(pr.getFullName(), intermediary.getFullName());
        Assert.assertEquals(pr.getPrimaryEmail(), intermediary.getPrimaryEmailId());
        Assert.assertEquals(pr.getGcmId(), intermediary.getGcmId());
        Assert.assertEquals(pr.getPrimaryMobile(), intermediary.getPrimaryMobileNumber());
        Assert.assertEquals(pr.getDomiSuburb(), intermediary.getPrimaryDomiAddress().getAddressLine2());
        Assert.assertEquals(pr.getDomiState(), intermediary.getPrimaryDomiAddress().getState());
        Assert.assertEquals(pr.getOpenDate(), intermediary.getOpenDate());
    }

    private ProfileUserRole getUserRole(JobRole role) {
        ProfileUserRole userRole = mock(ProfileUserRole.class);
        when(userRole.getUserRole()).thenReturn(role);
        when(userRole.getCompanyName()).thenReturn("companyName");
        when(userRole.getDealerGroup()).thenReturn("dealerGroup");
        when(userRole.getDealerGroupName()).thenReturn("dealerGroupName");

        return userRole;
    }

    private PersonResponse getEmptyPersonResponse(List<ProfileUserRole> roles) {
        final String emptyStr = "";
        PersonResponse res = mock(PersonResponse.class);
        when(res.getClientKey()).thenReturn(ClientKey.valueOf(emptyStr));
        when(res.getLastName()).thenReturn(emptyStr);
        when(res.getFirstName()).thenReturn(emptyStr);
        when(res.getMiddleName()).thenReturn(emptyStr);
        when(res.getFullName()).thenReturn(emptyStr);
        when(res.getPrimaryEmail()).thenReturn(emptyStr);
        when(res.getGcmId()).thenReturn(emptyStr);
        when(res.getPrimaryMobile()).thenReturn(emptyStr);
        when(res.getDomiSuburb()).thenReturn(emptyStr);
        when(res.getDomiState()).thenReturn(emptyStr);
        when(res.getOpenDate()).thenReturn(emptyStr);
        when(res.getProfileUserRoles()).thenReturn(roles);
        return res;
    }

    private PersonResponse getPersonResponse(List<ProfileUserRole> roles) {
        final String emptyStr = "";
        PersonResponse res = mock(PersonResponse.class);
        when(res.getClientKey()).thenReturn(ClientKey.valueOf("clientId"));
        when(res.getLastName()).thenReturn("lastName");
        when(res.getFirstName()).thenReturn("firstName");
        when(res.getMiddleName()).thenReturn("middleName");
        when(res.getFullName()).thenReturn("fullName");
        when(res.getPrimaryEmail()).thenReturn("email");
        when(res.getGcmId()).thenReturn("gcmId");
        when(res.getPrimaryMobile()).thenReturn("mobile");
        when(res.getDomiSuburb()).thenReturn("suburb");
        when(res.getDomiState()).thenReturn("state");
        when(res.getOpenDate()).thenReturn("dd-mm-yyyy");
        when(res.getProfileUserRoles()).thenReturn(roles);
        return res;
    }
}
