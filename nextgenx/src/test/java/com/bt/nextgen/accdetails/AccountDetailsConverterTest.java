package com.bt.nextgen.accdetails;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.bt.nextgen.accdetails.domain.TrustType;
import com.bt.nextgen.accdetails.web.model.CompanyAccountType;
import com.bt.nextgen.accdetails.web.model.SMSFAccountType;
import com.bt.nextgen.accdetails.web.model.TrustAccountType;
import com.bt.nextgen.core.web.model.AddressModel;
import com.bt.nextgen.core.web.model.Investor;
import com.bt.nextgen.core.web.model.LegalPerson;
import com.bt.nextgen.core.web.model.Person;
import com.bt.nextgen.core.web.model.TaxInfo;
import com.bt.nextgen.web.controller.cash.util.Attribute;

public class AccountDetailsConverterTest
{

	private Person uiPerson;
	private LegalPerson legalPerson;
	private AddressModel resiAddress;
	private AddressModel postAddress;

	@Before
	public void setUp()
	{
		uiPerson = new Investor();
		uiPerson.setFullName("test_name");
		TaxInfo taxInfo = new TaxInfo();
		taxInfo.setTaxOption("taxOption");
		uiPerson.setTaxInfo(taxInfo);
		List <AddressModel> addresses = new ArrayList <>();
		resiAddress = new AddressModel();
		resiAddress.setType(Attribute.RESIDENTIAL);
		postAddress = new AddressModel();
		postAddress.setType(Attribute.POSTAL);
		addresses.add(resiAddress);
		addresses.add(postAddress);
		uiPerson.setAddresses(addresses);

		legalPerson = new LegalPerson();
		legalPerson.setAbn("123");
		legalPerson.setAcn("4454");
		legalPerson.setArsn("244");
		legalPerson.setAsic("324");
		legalPerson.setRegistrationState("NSW");
		legalPerson.setLegislationName("legislationName");
		legalPerson.setRegulatorName("regulatorName");
	}

	@Test
	public void testCompanyAccountType()
	{
		CompanyAccountType companyDetails = AccountDetailsConverter.toCompanyAccountTypeModel(uiPerson, legalPerson);
		assertThat(companyDetails.getAbn(), is(legalPerson.getAbn()));
		assertThat(companyDetails.getAccountName(), is(uiPerson.getFullName()));
		assertThat(companyDetails.getAcn(), is(legalPerson.getAcn()));
		assertThat(companyDetails.getAsic(), is(legalPerson.getAsic()));
		assertThat(companyDetails.getIdStatus(), is(legalPerson.getIdStatus()));
		assertThat(companyDetails.getPrincipalAddress(), is(resiAddress));
		assertThat(companyDetails.getRegisteredAddress(), is(postAddress));
		assertThat(companyDetails.getTfn(), is(uiPerson.getTaxInfo().getTaxOption()));

	}

	@Test
	public void testTrustAccountType_WithSuperFundTrustType()
	{
		legalPerson.setTrustType(TrustType.GOVT_SUPER_FUND.getTrustTypeValue());
		TrustAccountType trustDetails = AccountDetailsConverter.toTrustAccountTypeModel(uiPerson, legalPerson);
		assertThat(trustDetails.getAbn(), is(legalPerson.getAbn()));
		assertThat(trustDetails.getAccountName(), is(uiPerson.getFullName()));
		assertThat(trustDetails.getAddress(), is(resiAddress));
		assertThat(trustDetails.getArsn(), nullValue());
		assertThat(trustDetails.getIdStatus(), is(legalPerson.getIdStatus()));
		assertThat(trustDetails.getLegislationName(), is(legalPerson.getLegislationName()));
		assertThat(trustDetails.getLicensingNumber(), nullValue());
		assertThat(trustDetails.getRegistrationState(), is(legalPerson.getRegistrationState()));
		assertThat(trustDetails.getRegulatorName(), nullValue());
		assertThat(trustDetails.getTfn(), is(uiPerson.getTaxInfo().getTaxOption()));
		assertThat(trustDetails.getTrustType(), is(legalPerson.getTrustType()));

	}

	@Test
	public void testTrustAccountType_WithRMISTrustType()
	{
		legalPerson.setTrustType(TrustType.REGI_MIS.getTrustTypeValue());
		TrustAccountType trustDetails = AccountDetailsConverter.toTrustAccountTypeModel(uiPerson, legalPerson);
		assertThat(trustDetails.getAbn(), is(legalPerson.getAbn()));
		assertThat(trustDetails.getArsn(), is(legalPerson.getArsn()));
		assertThat(trustDetails.getRegistrationState(), is(legalPerson.getRegistrationState()));
		assertThat(trustDetails.getTfn(), is(uiPerson.getTaxInfo().getTaxOption()));
		assertThat(trustDetails.getAccountName(), is(uiPerson.getFullName()));
		assertThat(trustDetails.getAddress(), is(resiAddress));
		assertThat(trustDetails.getIdStatus(), is(legalPerson.getIdStatus()));
		assertThat(trustDetails.getLegislationName(), nullValue());
		assertThat(trustDetails.getLicensingNumber(), nullValue());
		assertThat(trustDetails.getRegulatorName(), nullValue());
		assertThat(trustDetails.getTrustType(), is(legalPerson.getTrustType()));

	}

	@Test
	public void testTrustAccountType_WithRegulatedTrustType()
	{
		legalPerson.setTrustType(TrustType.REGU_TRUST.getTrustTypeValue());
		TrustAccountType trustDetails = AccountDetailsConverter.toTrustAccountTypeModel(uiPerson, legalPerson);
		assertThat(trustDetails.getAbn(), is(legalPerson.getAbn()));
		assertThat(trustDetails.getRegulatorName(), is(legalPerson.getRegulatorName()));
		assertThat(trustDetails.getLicensingNumber(), is(legalPerson.getArsn()));
		assertThat(trustDetails.getRegistrationState(), is(legalPerson.getRegistrationState()));
		assertThat(trustDetails.getTfn(), is(uiPerson.getTaxInfo().getTaxOption()));
		assertThat(trustDetails.getAccountName(), is(uiPerson.getFullName()));
		assertThat(trustDetails.getAddress(), is(resiAddress));
		assertThat(trustDetails.getIdStatus(), is(legalPerson.getIdStatus()));
		assertThat(trustDetails.getTrustType(), is(legalPerson.getTrustType()));

		assertThat(trustDetails.getArsn(), nullValue());
		assertThat(trustDetails.getLegislationName(), nullValue());
	}

	@Test
	public void testTrustAccountType_WithOtherTrustType()
	{
		legalPerson.setTrustType(TrustType.OTHER.getTrustTypeValue());
		TrustAccountType trustDetails = AccountDetailsConverter.toTrustAccountTypeModel(uiPerson, legalPerson);
		assertThat(trustDetails.getAbn(), is(legalPerson.getAbn()));
		assertThat(trustDetails.getRegistrationState(), is(legalPerson.getRegistrationState()));
		assertThat(trustDetails.getTfn(), is(uiPerson.getTaxInfo().getTaxOption()));
		assertThat(trustDetails.getAccountName(), is(uiPerson.getFullName()));
		assertThat(trustDetails.getAddress(), is(resiAddress));
		assertThat(trustDetails.getIdStatus(), is(legalPerson.getIdStatus()));
		assertThat(trustDetails.getTrustType(), is(legalPerson.getTrustType()));

		assertThat(trustDetails.getArsn(), nullValue());
		assertThat(trustDetails.getLegislationName(), nullValue());
		assertThat(trustDetails.getLicensingNumber(), nullValue());
		assertThat(trustDetails.getRegulatorName(), nullValue());
	}

	@Test
	public void testSMSFAccountType()
	{
		SMSFAccountType smsfDetails = AccountDetailsConverter.toSMSFAccountTypeModel(uiPerson, legalPerson);
		assertThat(smsfDetails.getAbn(), is(legalPerson.getAbn()));
		assertThat(smsfDetails.getAccountName(), is(uiPerson.getFullName()));
		assertThat(smsfDetails.getAddress(), is(resiAddress));
		assertThat(smsfDetails.getRegistrationState(), is(legalPerson.getRegistrationState()));
		assertThat(smsfDetails.getIdStatus(), is(legalPerson.getIdStatus()));
		assertThat(smsfDetails.getTfn(), is(uiPerson.getTaxInfo().getTaxOption()));

	}
}
