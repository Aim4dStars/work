package com.bt.nextgen.accdetails;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bt.nextgen.accdetails.domain.TrustType;
import com.bt.nextgen.accdetails.web.model.CompanyAccountType;
import com.bt.nextgen.accdetails.web.model.SMSFAccountType;
import com.bt.nextgen.accdetails.web.model.TrustAccountType;
import com.bt.nextgen.core.web.model.AddressModel;
import com.bt.nextgen.core.web.model.LegalPerson;
import com.bt.nextgen.core.web.model.Person;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.web.controller.cash.util.Attribute;

public class AccountDetailsConverter
{

	private static final Logger logger = LoggerFactory.getLogger(AccountDetailsConverter.class);
	/****
	 * This method will set the ComapnyDetails Model using the LegalPerson(avaloq UI_BP), uiPerson(avaloq UI_PERSON) details
	 */
	public static CompanyAccountType toCompanyAccountTypeModel(Person uiPerson, LegalPerson legalPerson)
	{
		logger.info("Setting the CompanyAccount type for person {}", uiPerson.getFullName());
		CompanyAccountType companyDetails = new CompanyAccountType();
		companyDetails.setAccountName(uiPerson.getFullName());
		companyDetails.setTfn(uiPerson.getTaxInfo().getTaxOption());

		companyDetails.setAbn(legalPerson.getAbn());
		companyDetails.setAcn(legalPerson.getAcn());
		companyDetails.setAsic(legalPerson.getAsic());
		if(Constants.IDV_STATUS_COMPLETED.equalsIgnoreCase(legalPerson.getIdStatus()))
		{
			companyDetails.setIdStatus(Constants.IDV_STATUS_VERIFIED);
		}
		else{
			companyDetails.setIdStatus(legalPerson.getIdStatus());
		}
		if (uiPerson.getAddresses() != null && uiPerson.getAddresses().size() > 0)
		{
			for (AddressModel address : uiPerson.getAddresses())
			{
				if (address.getType().equalsIgnoreCase(Attribute.RESIDENTIAL))
				{
					companyDetails.setPrincipalAddress(address);
				}
				else
				{
					companyDetails.setRegisteredAddress(address);
				}
			}
		}
		return companyDetails;
	}

	/****
	 * This method will set the TrustAccountType Model using the LegalPerson(avaloq UI_BP), uiPerson(avaloq UI_PERSON) details
	 */
	public static TrustAccountType toTrustAccountTypeModel(Person uiPerson, LegalPerson legalPerson)
	{
		logger.info("Setting the TrustAccount type for person {}", uiPerson.getFullName());
		TrustAccountType trustDetails = new TrustAccountType();
		trustDetails.setAccountName(uiPerson.getFullName());
		trustDetails.setTfn(uiPerson.getTaxInfo().getTaxOption());
		if (uiPerson.getAddresses() != null && uiPerson.getAddresses().size() > 0)
		{
			for (AddressModel address : uiPerson.getAddresses())
			{
				if (address.getType().equalsIgnoreCase(Attribute.RESIDENTIAL))
				{
					trustDetails.setAddress(address);
					break;
				}
			}
		}

		trustDetails.setTrustType(legalPerson.getTrustType());
		trustDetails.setAbn(legalPerson.getAbn());
		if(Constants.IDV_STATUS_COMPLETED.equalsIgnoreCase(legalPerson.getIdStatus()))
		{
			trustDetails.setIdStatus(Constants.IDV_STATUS_VERIFIED);
		}else{
			trustDetails.setIdStatus(legalPerson.getIdStatus());
		}
		
		trustDetails.setRegistrationState(legalPerson.getRegistrationState());
		if (TrustType.REGI_MIS.getTrustTypeValue().equals(trustDetails.getTrustType()))
		{
			trustDetails.setArsn(legalPerson.getArsn());
		}

		if (TrustType.REGU_TRUST.getTrustTypeValue().equals(trustDetails.getTrustType()))
		{
			trustDetails.setRegulatorName(legalPerson.getRegulatorName());
			trustDetails.setLicensingNumber(legalPerson.getArsn());
		}

		if (TrustType.GOVT_SUPER_FUND.getTrustTypeValue().equals(trustDetails.getTrustType()))
		{
			trustDetails.setLegislationName(legalPerson.getLegislationName());
		}
		return trustDetails;
	}

	/****
	 * This method will set the SMSFAccountType Model using the LegalPerson(avaloq UI_BP), uiPerson(avaloq UI_PERSON) details
	 */
	public static SMSFAccountType toSMSFAccountTypeModel(Person uiPerson, LegalPerson legalPerson)
	{
		logger.info("Setting the SMSFAccount type for person {}", uiPerson.getFullName());
		SMSFAccountType smsfDetails = new SMSFAccountType();
		smsfDetails.setAccountName(uiPerson.getFullName());
		smsfDetails.setTfn(uiPerson.getTaxInfo().getTaxOption());
		if (uiPerson.getAddresses() != null && uiPerson.getAddresses().size() > 0)
		{
			for (AddressModel address : uiPerson.getAddresses())
			{
				if (address.getType().equalsIgnoreCase(Attribute.RESIDENTIAL))
				{
					smsfDetails.setAddress(address);
					break;
				}
			}
		}

		smsfDetails.setAbn(legalPerson.getAbn());
		smsfDetails.setRegistrationState(legalPerson.getRegistrationState());
		if(Constants.IDV_STATUS_COMPLETED.equalsIgnoreCase(legalPerson.getIdStatus()))
		{
			smsfDetails.setIdStatus(Constants.IDV_STATUS_VERIFIED);
		}
		else{
			smsfDetails.setIdStatus(legalPerson.getIdStatus());
		}
		return smsfDetails;
	}

}
