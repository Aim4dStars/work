package com.bt.nextgen.service.avaloq.client;

import ch.lambdaj.Lambda;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.avaloq.domain.existingclient.AccountDataForIndividual;
import com.bt.nextgen.service.avaloq.domain.existingclient.IndividualWithAccountDataImpl;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.group.customer.BankingCustomerIdentifier;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.domain.Address;
import com.btfin.panorama.core.security.integration.domain.Individual;
import com.btfin.panorama.core.security.integration.domain.Investor;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.ClientType;
import com.bt.nextgen.service.integration.userinformation.GenericClient;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ch.lambdaj.Lambda.on;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;

public class AvaloqClientIntegrationServiceImplIntegrationTest extends BaseSecureIntegrationTest {

    @Autowired
    @Qualifier("avaloqClientIntegrationService")
    ClientIntegrationService avaloqClientIntegrationService;

    @Autowired
    UserProfileService userProfileService;

    @SecureTestContext
    @Test
    public void testLoadClientListForIndividualInvestor(){
        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        Collection<Client> clients = avaloqClientIntegrationService.loadClients(serviceErrors);
        Assert.assertThat(clients.size(), is(7));

        Map<ClientKey, Client> clientMap = Lambda.index(clients, on(Client.class).getClientKey());

        Individual individual = (Individual) clientMap.get(ClientKey.valueOf("60512"));

        Assert.assertNotNull(individual);
        assertThat(individual.getClientKey(), is(ClientKey.valueOf("60512")));
        assertThat(individual.getGcmId(), is("201617515"));
        assertThat(individual.getBankReferenceKey().getId(), is("201617515"));
        assertThat(individual.getCISKey().getId(), is("17787120022"));
        assertThat(individual.getClientType(), is(ClientType.N));
        assertThat(individual.getTitle(), is("Mr"));
        assertThat(individual.getFirstName(), is("Adrian"));
        assertThat(individual.getLastName(), is("Smith"));
        assertThat(individual.getDateOfBirth(), is(DateTime.parse("1987-09-10")));
        assertThat(individual.isRegistrationOnline(), is(false));
        //TODO: why mobile number
        assertThat(individual.getPrimaryContactNumber(), is("0414222333"));
        assertThat(individual.getPrimaryEmail(), is("abc@abc.com"));

        assertThat(individual.getAddresses().size(), is(1));        
        Address address = individual.getAddresses().get(0);
        assertThat(address.getCountryCode(), is("au"));
        assertThat(address.getCountry(), is("Australia"));
        assertThat(address.getSuburb(), is("Sydney"));
        assertThat(address.getStateOther(), is("New South Wales"));
        assertThat(address.getPostCode(), is("2000"));
        
        ///This needs to be a enum mapped value rather than just returning the avaloqId
        assertThat(address.getStateCode(), is("btfg$au_nsw"));
        assertThat(address.getState(), is("New South Wales"));

        Investor company = (Investor) clientMap.get(ClientKey.valueOf("80494"));
        assertNotNull(company);
        Assert.assertThat(company.getClientKey().getId(), is("80494"));
        Assert.assertThat(company.getGcmId(), is("201634002"));
        assertThat(company.getBankReferenceKey().getId(), is("201634002"));
        assertThat(company.getCISKey().getId(), is("36420920000"));
        Assert.assertThat(company.getFullName(), is("Demo Wilson Parking Pty Ltd"));
        Assert.assertThat(((Individual)company).isRegistrationOnline(), is(false));
        //Assert.assertThat(company.getAsicName(), is("Demo Wilson Parking Pty Ltd"));
        Assert.assertThat(company.getLegalForm(), is(InvestorType.COMPANY));

        Address companyAddress = company.getAddresses().get(0);
        Assert.assertThat(companyAddress.getCountryCode(),is("au"));
        Assert.assertThat(companyAddress.getSuburb(),is("Sydney"));
        assertThat(address.getStateCode(), is("btfg$au_nsw"));
        Assert.assertThat(companyAddress.getStateOther(),is("New South Wales"));
        Assert.assertThat(companyAddress.getPostCode(),is("2000"));
        Assert.assertThat(company.getAssociatedPersonKeys().size(),is(4));
    }

    @SecureTestContext
    @Test
    public void testLoadClientListForExistingClientSearch(){
        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        Collection<com.btfin.panorama.service.avaloq.domain.existingclient.Client> clients = avaloqClientIntegrationService.loadClientsForExistingClientSearch(serviceErrors, "anyName");
        Assert.assertThat(clients.size(), is(7));

        com.btfin.panorama.service.avaloq.domain.existingclient.Individual individual = Lambda.selectFirst(clients, new LambdaMatcher<com.btfin.panorama.service.avaloq.domain.existingclient.Client>() {

            @Override
            protected boolean matchesSafely(com.btfin.panorama.service.avaloq.domain.existingclient.Client client) {
                return "32562".equals(client.getClientKey().getId());
            }
        });

        com.btfin.panorama.service.avaloq.domain.existingclient.Client legalClient = Lambda.selectFirst(clients, new LambdaMatcher<com.btfin.panorama.service.avaloq.domain.existingclient.Client>() {

            @Override
            protected boolean matchesSafely(com.btfin.panorama.service.avaloq.domain.existingclient.Client client) {
                return "32558".equals(client.getClientKey().getId());
            }
        });

        assertNotNull(individual);
        assertThat(individual.getClientKey(), is(ClientKey.valueOf("32562")));
        assertThat(individual.getGcmId(), is("201603351"));
        assertThat(individual.getBankReferenceKey().getId(), is("201603351"));
        assertThat(individual.getPrimaryEmail(), is("abc6@test.com"));
        assertThat(individual.getPrimaryMobile(), is("0466666666"));
        assertThat(individual.getCISKey().getId(), is("24509110081"));
        assertThat(individual.getFirstName(), is("Adrian"));
        assertThat(individual.getLastName(), is("Smith"));
        assertThat(individual.getDateOfBirth(), is(DateTime.parse("1968-10-03")));

        assertThat(individual.getAddresses().size(), is(1));
        Address address = individual.getAddresses().get(0);
        assertThat(address.getCountryCode(), is("au"));
        assertThat(address.getCountry(), is("Australia"));
        assertThat(address.getSuburb(), is("Sydney"));
        assertThat(address.getState(), is("Queensland"));
        assertThat(address.getPostCode(), is("2000"));

        assertNotNull(legalClient);
        assertThat(legalClient.getClientKey().getId(), is("32558"));
        assertThat(legalClient.getFullName(), is("Demo Wilson Parking Pty Ltd"));
        assertThat(legalClient.getLegalForm(), is(InvestorType.COMPANY));

        Address companyAddress = legalClient.getAddresses().get(0);
        assertThat(companyAddress.getCountryCode(), is("au"));
        assertThat(companyAddress.getSuburb(), is("Sydney"));
        assertThat(companyAddress.getState(), is("New South Wales"));
        assertThat(companyAddress.getPostCode(), is("2000"));
    }

    @SecureTestContext
    @Test
    public void testLoadClientWithDirectSuperAccount() {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        com.btfin.panorama.service.avaloq.domain.existingclient.Client client = avaloqClientIntegrationService.loadClientByCISKey("36420920011", serviceErrors);
        Assert.assertThat(client.getFullName(), is("DemoOne test"));
        IndividualWithAccountDataImpl individual = (IndividualWithAccountDataImpl)client;
        Assert.assertThat(individual.getHasTfn(), is(true));
        Assert.assertNotNull(individual.getAccountData());
        Assert.assertEquals(individual.getAccountData().size(), 3);
        Assert.assertTrue(individual.hasDirectSuperAccount());
        Assert.assertFalse(individual.hasDirectPensionAccount());
    }

    @SecureTestContext
    @Test
    @Ignore
    public void testLoadClientDetails_byGcmId(){
        GenericClient client = avaloqClientIntegrationService.loadClientDetailsByGcmId(getBankingCustomerIdentifier("201617515"), new ServiceErrorsImpl());
        Assert.assertNotNull(client);
        assertThat(client.getClientKey(), is(ClientKey.valueOf("69772")));
    }

    private BankingCustomerIdentifier getBankingCustomerIdentifier(final String gcmId) {
        return new BankingCustomerIdentifier() {
            @Override
            public String getBankReferenceId() {
                return gcmId;
            }

            @Override
            public UserKey getBankReferenceKey() {
                return null;
            }

            @Override
            public CISKey getCISKey() {
                return null;
            }
        };
    }
    
	@SecureTestContext(username = "explode", customerId = "201101101", jobRole="adviser",profileId="971",jobId="1234" )
	@Test
	public void testLoadClientListForIndividualInvestor_explodesAppropriately(){
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		avaloqClientIntegrationService.loadClients(serviceErrors);

		assertThat(serviceErrors.hasErrors(), is(true));
	}

	@SecureTestContext(username = "bigUser", customerId = "201635682", jobRole="adviser",profileId="5393",jobId="102540" )
	@Test
	public void testLoadClientListForBigAdviser(){
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		Map<ClientKey, Client> clientMap = new HashMap<>();

		Collection<Client> clients = avaloqClientIntegrationService.loadClients(serviceErrors);

		assertThat(serviceErrors.hasErrors(), is(false));
	}

    @SecureTestContext(username = "bigUser", customerId = "201635682", jobRole="adviser",profileId="5393",jobId="102540" )
    @Test
    public void testLoadGenericClientForAdvisor(){
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        GenericClient client = avaloqClientIntegrationService.loadGenericClientDetails(ClientKey.valueOf("32815"), serviceErrors);
        assertThat(client, is(notNullValue()));
        assertThat(client, instanceOf(GenericClientIntermediaryImpl.class));

        GenericClientIntermediaryImpl adviser =(GenericClientIntermediaryImpl)client;

        assertThat(adviser.isAdviser(), is(true));
        assertThat(adviser.getClientKey(), is(ClientKey.valueOf("32815")));
        assertThat(adviser.getFullName(), is("Mr Bradley Jackson"));
    }

    @SecureTestContext(username = "onlineRegAdviser", customerId = "201635682", jobRole="adviser",profileId="5393",jobId="102540" )
    @Test
    public void testUpdateRegisterOnlineGeneric_Adviser(){
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        GenericClient client = avaloqClientIntegrationService.updateRegisterOnline(ClientKey.valueOf("32815"), userProfileService.getActiveProfile().getJobRole(),serviceErrors);
        assertThat(client, is(notNullValue()));
        assertThat(client, instanceOf(GenericClientIntermediaryImpl.class));

        GenericClientIntermediaryImpl adviser =(GenericClientIntermediaryImpl)client;

        assertThat(adviser.isAdviser(), is(true));
        assertThat(adviser.getClientKey(), is(ClientKey.valueOf("32815")));
      }

    @SecureTestContext(username = "onlineRegInvestor", customerId = "201635682", jobRole="adviser",profileId="5393",jobId="102540" )
    @Test
    public void testUpdateRegisterOnlineGeneric_Investor(){
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        GenericClient client = avaloqClientIntegrationService.updateRegisterOnline(ClientKey.valueOf("157365"),userProfileService.getActiveProfile().getJobRole(),serviceErrors);
        assertThat(client, is(notNullValue()));
        assertThat(client, instanceOf(GenericClientInvestorImpl.class));
        assertThat(client.getModificationSeq(), is("5"));
        GenericClientInvestorImpl investor =(GenericClientInvestorImpl)client;

        assertThat(investor.getClientKey(), is(ClientKey.valueOf("157365")));
      }
}
