package com.bt.nextgen.serviceops.controller;

import com.bt.nextgen.api.account.v3.model.PersonRelationDto;
import com.bt.nextgen.api.account.v3.model.WrapAccountDetailDto;
import com.bt.nextgen.api.broker.model.BrokerDto;
import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.client.model.AddressTypeV2;
import com.bt.nextgen.api.client.v2.model.InvestorDto;
import com.bt.nextgen.api.client.v2.model.RegisteredEntityDto;
import com.bt.nextgen.core.web.model.Intermediary;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.AccountSubType;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.domain.InvestorRole;
import com.bt.nextgen.serviceops.model.LinkedClientModel;
import com.bt.nextgen.serviceops.model.ServiceOpsModel;
import com.bt.nextgen.serviceops.model.WrapAccountModel;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.junit.Test;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.web.model.AddressModel;
import com.bt.nextgen.serviceops.model.IntermediariesModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

public class ServiceOpsConverterTest
{
	@Test
	public void testToIntermediariesModel()
	{
		String exepctedState = "Victoria";
		
		Intermediary person = new Intermediary();
		person.setFirstName("Blue");
		person.setLastName("Petertay");
		person.setDealerGroupName("Dealer Group 3");
		person.setPrimaryEmailId("TM@gmail.com");
		person.setPrimaryMobileNumber("0425987456");
		person.setClientId(EncodedString.fromPlainText("11840"));
        person.setRole("Dealer Group Manager");
		AddressModel primaryDomiAddress = new AddressModel();
		primaryDomiAddress.setAddressLine2("addressLine2");
		primaryDomiAddress.setState("Victoria");
        person.setPrimaryDomiAddress(primaryDomiAddress);

		IntermediariesModel intermediariesModel = ServiceOpsConverter.toIntermediariesModel(person);
		
		assertThat(intermediariesModel,notNullValue());
		assertThat(intermediariesModel.getRole(),Is.is("Dealer Group Manager"));
		assertThat(intermediariesModel.getState(),Is.is(exepctedState));
	}

	@Test
	public void testToWrapAccountModel() {

		WrapAccountDetailImpl wrapAccount = new WrapAccountDetailImpl();
		wrapAccount.setAccountName("Test Account");
		wrapAccount.setAccountStatus(AccountStatus.ACTIVE);
		wrapAccount.setAccountStructureType(AccountStructureType.Individual);
		wrapAccount.setAccountNumber("120005251");
		wrapAccount.setAccountKey(AccountKey.valueOf("1234"));
		wrapAccount.setMigrationKey("M1111118");

		WrapAccountModel wrapAccountModel  = ServiceOpsConverter.convertToSimpleWrapAccountModel(wrapAccount);

		assertNotNull(wrapAccountModel);
		assertThat(EncodedString.toPlainText(wrapAccountModel.getAccountId()), Is.is(wrapAccount.getAccountKey().getId()));
		assertThat(wrapAccountModel.getAccountName(), Is.is("Test Account"));
		assertThat(wrapAccountModel.getAccountStatus(), Is.is(AccountStatus.ACTIVE.getStatus()));
		assertThat(wrapAccountModel.getAccountNumber(), Is.is("120005251"));
		assertThat(wrapAccountModel.getAccountType(), Is.is(AccountStructureType.Individual.name()));
		assertThat(wrapAccountModel.getmNumber() ,Is.is("M1111118"));
	}

	@Test
	public void testConvertWrapAccountDtoToLinkedClientModel_Individual() {
		ServiceOpsModel serviceOpsModel = new ServiceOpsModel();

		setUpWrapAccountDetailDto(serviceOpsModel, AccountStructureType.Individual, false);

		serviceOpsModel = ServiceOpsConverter.convertWrapAccountDtoToLinkedClientModel(serviceOpsModel);

		List<LinkedClientModel> linkedClients = serviceOpsModel.getLinkedClients();
		//Adviser Test
		assertNotNull(linkedClients.get(0));
		assertThat(linkedClients.get(0).getFirstName(), Is.is("Test"));
		assertThat(linkedClients.get(0).getLastName(), Is.is("Adviser"));
		assertThat(linkedClients.get(0).getPaymentSetting(), Is.is("Linked accounts only"));
		assertThat(linkedClients.get(0).getPostalAddress(), Is.is("Sydney NSW"));

		//Client Test
		assertNotNull(linkedClients.get(1));
		assertThat(linkedClients.get(1).getFirstName(), Is.is("Test"));
		assertThat(linkedClients.get(1).getLastName(), Is.is("Owner"));
		assertThat(linkedClients.get(1).getPaymentSetting(), Is.is("Linked accounts only"));
		assertThat(linkedClients.get(1).getPostalAddress(), Is.is("33 Pitt Street, Sydney, NSW 2000"));
	}

	@Test
	public void testConvertWrapAccountDtoToLinkedClientModel_Trust() {
		ServiceOpsModel serviceOpsModel = new ServiceOpsModel();

		setUpWrapAccountDetailDto(serviceOpsModel, AccountStructureType.Trust, false);

		serviceOpsModel = ServiceOpsConverter.convertWrapAccountDtoToLinkedClientModel(serviceOpsModel);

		List<LinkedClientModel> linkedClients = serviceOpsModel.getLinkedClients();
		//Adviser Test
		assertNotNull(linkedClients.get(0));
		assertThat(linkedClients.get(0).getFirstName(), Is.is("Test"));
		assertThat(linkedClients.get(0).getLastName(), Is.is("Adviser"));
		assertThat(linkedClients.get(0).getPaymentSetting(), Is.is("Linked accounts only"));

		//Client Test
		assertNotNull(linkedClients.get(1));
		assertThat(linkedClients.get(1).getFirstName(), Is.is("Trust"));
		assertThat(linkedClients.get(1).getLastName(), Is.is("Owner1"));
		assertThat(linkedClients.get(1).getPaymentSetting(), Is.is("Linked accounts only"));

		assertNotNull(linkedClients.get(2));
		assertThat(linkedClients.get(2).getFirstName(), Is.is("Trust"));
		assertThat(linkedClients.get(2).getLastName(), Is.is("Owner2"));
		assertThat(linkedClients.get(2).getPaymentSetting(), Is.is("Linked accounts only"));
	}

	@Test
	public void testConvertWrapAccountDtoToLinkedClientModel_BTInvest() {
		ServiceOpsModel serviceOpsModel = new ServiceOpsModel();

		setUpWrapAccountDetailDto(serviceOpsModel, AccountStructureType.Individual, true);

		serviceOpsModel = ServiceOpsConverter.convertWrapAccountDtoToLinkedClientModel(serviceOpsModel);

		List<LinkedClientModel> linkedClients = serviceOpsModel.getLinkedClients();

		//Adviser Details will not be available
		//Client Test
		assertNotNull(linkedClients.get(0));
		assertThat(linkedClients.get(0).getFirstName(), Is.is("Test"));
		assertThat(linkedClients.get(0).getLastName(), Is.is("Owner"));
		assertThat(linkedClients.get(0).getPaymentSetting(), Is.is("Linked accounts only"));
	}

	private void setUpWrapAccountDetailDto(ServiceOpsModel serviceOpsModel, AccountStructureType accountStructureType, boolean isDirectInvestor) {
		WrapAccountDetailDto wrapAccountDetail = new WrapAccountDetailDto();
		wrapAccountDetail.setRegisteredSinceDate(new DateTime(2016, 1, 15, 10, 0));
		wrapAccountDetail.setBsb("062032");
		wrapAccountDetail.setAccountType(accountStructureType.name());
		wrapAccountDetail.setAccountName("Test Account");

		BrokerDto adviser = new BrokerDto();
		if (isDirectInvestor) {
			adviser.setFirstName("BT");
			adviser.setLastName("Invest");
		} else {
			adviser.setFirstName("Test");
			adviser.setLastName("Adviser");
		}

		List<AddressDto> addressList = new ArrayList<>();

		AddressDto address = new AddressDto();
		address.setAddressType(AddressTypeV2.POSTAL.name());
		address.setStreetNumber("33");
		address.setStreetName("Pitt");
		address.setStreetType("Street");
		address.setCity("Sydney");
		address.setStateAbbr("NSW");
		address.setPostcode("2000");
		address.setMailingAddress(true);
		addressList.add(address);

		adviser.setAddresses(addressList);

		wrapAccountDetail.setAdviser(adviser);

		List<PersonRelationDto> settings = new ArrayList<>();
		PersonRelationDto client1Info = new PersonRelationDto();
		client1Info.setPermissions("Linked accounts only");
		client1Info.setPrimaryContactPerson(true);
		com.bt.nextgen.api.client.model.ClientKey clientKey1 = new com.bt.nextgen.api.client.model.ClientKey("11");
		client1Info.setClientKey(clientKey1);

		Set<InvestorRole> investorRoles = new HashSet<>();
		investorRoles.add(InvestorRole.Director);
		investorRoles.add(InvestorRole.Signatory);
		client1Info.setPersonRoles(investorRoles);
		settings.add(client1Info);

		PersonRelationDto client2Info = new PersonRelationDto();
		client2Info.setPermissions("Linked accounts only");
		client2Info.setPrimaryContactPerson(false);
		com.bt.nextgen.api.client.model.ClientKey clientKey11 = new com.bt.nextgen.api.client.model.ClientKey("11");
		client2Info.setClientKey(clientKey11);

		client2Info.setPersonRoles(investorRoles);
		settings.add(client2Info);

		PersonRelationDto adviserInfo = new PersonRelationDto();
		adviserInfo.setPermissions("Linked accounts only");
		adviserInfo.setPrimaryContactPerson(false);
		adviserInfo.setAdviser(true);
		com.bt.nextgen.api.client.model.ClientKey clientKey2 = new com.bt.nextgen.api.client.model.ClientKey("1");
		adviserInfo.setClientKey(clientKey2);
		adviserInfo.setPersonRoles(investorRoles);
		settings.add(adviserInfo);

		if (accountStructureType.equals(AccountStructureType.Individual) || accountStructureType.equals(AccountStructureType.Joint) || accountStructureType.equals(AccountStructureType.SUPER)) {
			List<InvestorDto> owners = new ArrayList<>();
			InvestorDto owner1 = new InvestorDto();
			owner1.setFirstName("Test");
			owner1.setLastName("Owner");
			owner1.setFullName("Test Owner");
			owner1.setGcmId("201612345");
			com.bt.nextgen.api.client.model.ClientKey clientKey3 = new com.bt.nextgen.api.client.model.ClientKey("11");
			owner1.setKey(clientKey3);
			owner1.setAddresses(addressList);
			owners.add(owner1);
			wrapAccountDetail.setOwners(owners);
		} else {
			List<InvestorDto> owners = new ArrayList<>();
			RegisteredEntityDto owner = new RegisteredEntityDto();
			owner.setFirstName("Legal");
			owner.setLastName("Owner");
			owner.setFullName("Legal Owner");
			owner.setGcmId("201654321");
			com.bt.nextgen.api.client.model.ClientKey clientKey3 = new com.bt.nextgen.api.client.model.ClientKey("12");
			owner.setKey(clientKey3);
			owner.setAddresses(addressList);


			List<InvestorDto> linkedClients = new ArrayList<>();

			RegisteredEntityDto linkedClient1 = new RegisteredEntityDto();
			linkedClient1.setFirstName("Trust");
			linkedClient1.setLastName("Owner1");
			linkedClient1.setFullName("Trust Owner1");
			linkedClient1.setGcmId("201612345");
			com.bt.nextgen.api.client.model.ClientKey clientKey4 = new com.bt.nextgen.api.client.model.ClientKey("1");
			linkedClient1.setKey(clientKey4);
			linkedClient1.setAddresses(addressList);

			RegisteredEntityDto linkedClient2 = new RegisteredEntityDto();
			linkedClient2.setFirstName("Trust");
			linkedClient2.setLastName("Owner2");
			linkedClient2.setFullName("Trust Owner2");
			linkedClient2.setGcmId("2016121345");
			com.bt.nextgen.api.client.model.ClientKey clientKey5 = new com.bt.nextgen.api.client.model.ClientKey("1");
			linkedClient2.setKey(clientKey5);
			linkedClient2.setAddresses(addressList);

			linkedClients.add(linkedClient1);
			linkedClients.add(linkedClient2);
			owner.setLinkedClients(linkedClients);
			owners.add(owner);
			wrapAccountDetail.setOwners(owners);
		}
		wrapAccountDetail.setSettings(settings);
		serviceOpsModel.setWrapAccountDetail(wrapAccountDetail);
	}

	@Test
	public void testToWrapAccountModel_Super() {

		WrapAccountDetailImpl wrapAccount = new WrapAccountDetailImpl();
		wrapAccount.setAccountName("Test Account");
		wrapAccount.setAccountStatus(AccountStatus.ACTIVE);
		wrapAccount.setAccountStructureType(AccountStructureType.SUPER);
		wrapAccount.setAccountNumber("120005251");
		wrapAccount.setAccountKey(AccountKey.valueOf("1234"));
		wrapAccount.setMigrationKey("M11111118");

		WrapAccountModel wrapAccountModel  = ServiceOpsConverter.convertToSimpleWrapAccountModel(wrapAccount);

		assertNotNull(wrapAccountModel);
		assertThat(EncodedString.toPlainText(wrapAccountModel.getAccountId()), Is.is(wrapAccount.getAccountKey().getId()));
		assertThat(wrapAccountModel.getAccountName(), Is.is("Test Account"));
		assertThat(wrapAccountModel.getAccountStatus(), Is.is(AccountStatus.ACTIVE.getStatus()));
		assertThat(wrapAccountModel.getAccountNumber(), Is.is("120005251"));
		assertThat(wrapAccountModel.getAccountType(), Is.is(StringUtils.capitalize(AccountStructureType.SUPER.name().toLowerCase())));
		assertThat(wrapAccountModel.getmNumber() ,Is.is("M11111118"));
	}

	@Test
	public void testToWrapAccountModel_Pension() {

		WrapAccountDetailImpl wrapAccount = new WrapAccountDetailImpl();
		wrapAccount.setAccountName("Test Account");
		wrapAccount.setAccountStatus(AccountStatus.ACTIVE);
		wrapAccount.setAccountStructureType(AccountStructureType.SUPER);
		wrapAccount.setSuperAccountSubType(AccountSubType.PENSION);
		wrapAccount.setAccountNumber("120005251");
		wrapAccount.setAccountKey(AccountKey.valueOf("1234"));

		WrapAccountModel wrapAccountModel  = ServiceOpsConverter.convertToSimpleWrapAccountModel(wrapAccount);

		assertNotNull(wrapAccountModel);
		assertThat(EncodedString.toPlainText(wrapAccountModel.getAccountId()), Is.is(wrapAccount.getAccountKey().getId()));
		assertThat(wrapAccountModel.getAccountName(), Is.is("Test Account"));
		assertThat(wrapAccountModel.getAccountStatus(), Is.is(AccountStatus.ACTIVE.getStatus()));
		assertThat(wrapAccountModel.getAccountNumber(), Is.is("120005251"));
		assertThat(wrapAccountModel.getAccountType(), Is.is(StringUtils.capitalize(AccountSubType.PENSION.name().toLowerCase())));
	}
}
