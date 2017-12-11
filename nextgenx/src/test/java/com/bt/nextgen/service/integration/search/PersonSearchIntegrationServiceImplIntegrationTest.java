package com.bt.nextgen.service.integration.search;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.btfin.panorama.core.security.Roles;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.search.AvaloqPersonSearchIntegrationServiceImpl;
import com.bt.nextgen.service.avaloq.search.PersonSearchRequestImpl;
import com.bt.nextgen.service.avaloq.search.PersonType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class PersonSearchIntegrationServiceImplIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired
	AvaloqPersonSearchIntegrationServiceImpl personSearch;

	@Test
    @SecureTestContext
	public void testPersonSearchResponse() throws Exception
	{
		PersonSearchRequest request = new PersonSearchRequestImpl();
		request.setSearchToken("tay%");
		request.setRoleType("");
		request.setPersonTypeId(PersonType.NATURAL_PERSON.getName());
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		
		List<PersonResponse> result = personSearch.searchUser(request, serviceErrors);
		assertNotNull(result);
		
		assertEquals("61685", result.get(0).getClientKey().getId());
		assertEquals("201616432", result.get(0).getGcmId());
		assertEquals("Deepshikha", result.get(0).getFirstName());
		assertEquals("Singh", result.get(0).getLastName());
		assertEquals("", result.get(0).getMiddleName());
		assertEquals("Deepshikha Singh", result.get(0).getFullName());
		assertEquals("abc@gmail.com", result.get(0).getPrimaryEmail());
		assertEquals("", result.get(0).getPrimaryMobile());
		assertEquals("INVESTOR", result.get(0).getProfileUserRoles().get(0).getUserRole().name());
		assertEquals(null, result.get(0).getProfileUserRoles().get(0).getDealerGroup());
		assertEquals("New South Wales", result.get(0).getDomiState());
		assertEquals("Kirribilli", result.get(0).getDomiSuburb());
		assertEquals("2015-01-20", result.get(0).getOpenDate());
		assertEquals(false, result.get(0).isBenef());

	}

	@Test
    @SecureTestContext
	public void testPersonSearchResponseAdminAssistant() throws Exception
	{
		PersonSearchRequest request = new PersonSearchRequestImpl();
		request.setSearchToken("tay%");
		request.setRoleType(Roles.ROLE_INVESTOR.name());
		request.setPersonTypeId(PersonType.NATURAL_PERSON.getName());
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		List<PersonResponse> result = personSearch.searchUser(request, serviceErrors);
		assertNotNull(result);
		
		assertEquals("55829", result.get(1).getClientKey().getId());
		assertEquals("201616274", result.get(1).getGcmId());
		assertEquals("Rod", result.get(1).getFirstName());
		assertEquals("Bailey", result.get(1).getLastName());
		assertEquals("DEMO ASSISTANT", result.get(1).getMiddleName());
		assertEquals("Rod DEMO ASSISTANT Bailey", result.get(1).getFullName());
		assertEquals("", result.get(1).getPrimaryEmail());
		assertEquals("0414222333", result.get(1).getPrimaryMobile());
		assertEquals("ASSISTANT", result.get(1).getProfileUserRoles().get(0).getUserRole().name());
		assertEquals("51769", result.get(1).getProfileUserRoles().get(0).getDealerGroup());
		assertEquals("New South Wales", result.get(1).getDomiState());
		assertEquals("Sydney", result.get(1).getDomiSuburb());
		assertEquals("2015-01-19", result.get(1).getOpenDate());
		assertEquals(false, result.get(0).isBenef());
	}
	
	@Test
    @SecureTestContext
	public void testPersonSearchResponseParaPlanner() throws Exception
	{
		PersonSearchRequest request = new PersonSearchRequestImpl();
		request.setSearchToken("tay%");
		request.setRoleType(Roles.ROLE_INVESTOR.name());
		request.setPersonTypeId(PersonType.NATURAL_PERSON.getName());
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		List<PersonResponse> result = personSearch.searchUser(request, serviceErrors);
		assertNotNull(result);
		
		assertEquals("55830", result.get(2).getClientKey().getId());
		assertEquals("201616275", result.get(2).getGcmId());
		assertEquals("Scott", result.get(2).getFirstName());
		assertEquals("Miller", result.get(2).getLastName());
		assertEquals("DEMO PARA-PLANNER", result.get(2).getMiddleName());
		assertEquals("Scott DEMO PARA-PLANNER Miller", result.get(2).getFullName());
		assertEquals("", result.get(2).getPrimaryEmail());
		assertEquals("0414888999", result.get(2).getPrimaryMobile());
		assertEquals("PARAPLANNER", result.get(2).getProfileUserRoles().get(0).getUserRole().name());
		assertEquals("51769", result.get(2).getProfileUserRoles().get(0).getDealerGroup());
		assertEquals("New South Wales", result.get(2).getDomiState());
		assertEquals("Sydney", result.get(2).getDomiSuburb());
		assertEquals("2015-01-19", result.get(2).getOpenDate());
		assertEquals(false, result.get(0).isBenef());
	}
	
	@Test
    @SecureTestContext
	public void testPersonSearchResponseInvstMgr() throws Exception
	{
		PersonSearchRequest request = new PersonSearchRequestImpl();
		request.setSearchToken("tay%");
		request.setRoleType(Roles.ROLE_INVESTOR.name());
		request.setPersonTypeId(PersonType.NATURAL_PERSON.getName());
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		List<PersonResponse> result = personSearch.searchUser(request, serviceErrors);
		assertNotNull(result);
		
		assertEquals("68489", result.get(3).getClientKey().getId());
		assertEquals("201601509", result.get(3).getGcmId());
		assertEquals("Anne", result.get(3).getFirstName());
		assertEquals("Ramos.1", result.get(3).getLastName());
		assertEquals("C.", result.get(3).getMiddleName());
		assertEquals("Anne C. Ramos.1", result.get(3).getFullName());
		assertEquals("", result.get(3).getPrimaryEmail());
		assertEquals("09169843995", result.get(3).getPrimaryMobile());
		assertEquals("INVESTMENT_MANAGER", result.get(3).getProfileUserRoles().get(0).getUserRole().name());
		assertEquals("51769", result.get(3).getProfileUserRoles().get(0).getDealerGroup());
		assertEquals("Australia Capital Territory", result.get(3).getDomiState());
		assertEquals("Windia", result.get(3).getDomiSuburb());
		assertEquals("2015-01-20", result.get(3).getOpenDate());
		assertEquals(false, result.get(3).isBenef());
	}


    @Test
    @SecureTestContext
    public void testPersonSearchResponseAccountant() throws Exception
    {
        PersonSearchRequest request = new PersonSearchRequestImpl();
        request.setSearchToken("tay%");
        request.setRoleType(Roles.ROLE_INVESTOR.name());
        request.setPersonTypeId(PersonType.NATURAL_PERSON.getName());
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<PersonResponse> result = personSearch.searchUser(request, serviceErrors);
        assertNotNull(result);

        assertEquals("152354", result.get(6).getClientKey().getId());
        assertEquals("227251502", result.get(6).getGcmId());
        assertEquals("acc", result.get(6).getFirstName());
        assertEquals("basic1", result.get(6).getLastName());
        assertEquals(null, result.get(6).getMiddleName());
        assertEquals("acc  basic1", result.get(6).getFullName());
        assertEquals("test123456@tt.com", result.get(6).getPrimaryEmail());
        assertEquals("04123123", result.get(6).getPrimaryMobile());
        assertEquals("ACCOUNTANT", result.get(6).getProfileUserRoles().get(0).getUserRole().name());
        assertEquals("100227", result.get(6).getProfileUserRoles().get(0).getDealerGroup());
        assertEquals("L person-121_2239", result.get(6).getProfileUserRoles().get(0).getCompanyName());
        assertEquals("New South Wales", result.get(6).getDomiState());
        assertEquals("Sydney", result.get(6).getDomiSuburb());
        assertEquals("2015-07-15", result.get(6).getOpenDate());
        assertEquals(false, result.get(6).isBenef());
    }

	
    /*
     * @Test
     * 
     * @SecureTestContext(username = "explode", customerId = "201101101") public
     * void testPersonSearchResponseAdviserError() throws Exception {
     * PersonSearchRequest request = new PersonSearchRequestImpl();
     * request.setSearchToken("tay%");
     * request.setRoleType(Roles.ROLE_INVESTOR.name());
     * request.setPersonTypeId(PersonType.NATURAL_PERSON.getName());
     * ServiceErrors serviceErrors = new ServiceErrorsImpl();
     * List<PersonResponse> result = personSearch.searchUser(request,
     * serviceErrors); assertThat(serviceErrors.hasErrors(), Is.is(true));
     * 
     * }
     */

	@Test
	@SecureTestContext
	public void testPersonSearchResponseApprovedAuthoriser() throws Exception {
		PersonSearchRequest request = new PersonSearchRequestImpl();
		request.setSearchToken("test%");
		request.setRoleType(Roles.ROLE_ADVISER.name());
		request.setPersonTypeId(PersonType.NATURAL_PERSON.getName());
		ServiceErrors serviceErrors = new ServiceErrorsImpl();

		List<PersonResponse> result = personSearch.searchUser(request, serviceErrors);
		assertNotNull(result);

		assertEquals("61685", result.get(7).getClientKey().getId());
		assertEquals("201616432", result.get(7).getGcmId());
		assertEquals("Approved", result.get(7).getFirstName());
		assertEquals("Test", result.get(7).getLastName());
		assertEquals("Authoriser", result.get(7).getMiddleName());
		assertEquals("Approved Authoriser Test", result.get(7).getFullName());
		assertEquals("abc@gmail.com", result.get(7).getPrimaryEmail());
		assertEquals("", result.get(7).getPrimaryMobile());
		assertEquals("APPROVED_AUTHORISER", result.get(7).getProfileUserRoles().get(0).getUserRole().name());
		assertEquals(null, result.get(7).getProfileUserRoles().get(0).getDealerGroup());
		assertEquals("New South Wales", result.get(7).getDomiState());
		assertEquals("Sydney", result.get(7).getDomiSuburb());
		assertEquals("2015-01-20", result.get(7).getOpenDate());
		assertEquals(false, result.get(7).isBenef());
	}
}
