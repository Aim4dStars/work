/**
 * 
 */
package com.bt.nextgen.service.onboarding.btesb;

import ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.ProvisionOnlineAccessResponseMsgType;

/**
 * @author L055011
 *
 */
public class ProcessAdvisersAdapter extends ProvisionOnlineAccessResponseAdapter
{
	public ProcessAdvisersAdapter() {}

	/**
	 * Constructor.
	 * @param jaxbResponse response object.
	 */
	public ProcessAdvisersAdapter(ProvisionOnlineAccessResponseMsgType jaxbResponse)
	{
		super(jaxbResponse);
	}

	/**
	 * converting the pojo request to Jaxb request
	 * @param request
	 * @return
	 *//*
	public ProcessAdvisersRequestMsgType createProcessAdvisersRequest(CreateAccountRequest request)
	{
		ObjectFactory objectFactory = new ObjectFactory();

		ProcessAdvisersRequestMsgType jaxbRequest = new ProcessAdvisersRequestMsgType();
		OBInvolvedPartysAdviserType obInvolvedPartysAdviserType = new OBInvolvedPartysAdviserType();
		jaxbRequest.setInvolvedPartys(obInvolvedPartysAdviserType);
		AccountAdviserType accountAdviserType = new AccountAdviserType();
		OBAdvisersType obAdvisersType = new OBAdvisersType();
		obInvolvedPartysAdviserType.setAdvisers(obAdvisersType);
		obAdvisersType.getAdviser().add(accountAdviserType);

		AdviserDetailsType adviserDetailsType = new AdviserDetailsType();
		accountAdviserType.setAdviserDetails(adviserDetailsType);
		adviserDetailsType.setAdviserNumber(request.getOracleUserId());
		adviserDetailsType.setAdviserNumberIssuer(AdviserNumberIssuerType.WESTPAC);

		InvolvedPartyDetailsType obPartyDetailType = new InvolvedPartyDetailsType();
		PartyDetailType pdt = new PartyDetailType();
		IndividualType individualType = new IndividualType();
		individualType.setLastName(request.getLastName());
		individualType.setGivenName(request.getFirstName());

		OBContactNumberType contactNo = new OBContactNumberType();
		contactNo.setNonStandardContactNumber(request.getPrimaryMobileNumber());

		OBContactDetailType contactDetailType = new OBContactDetailType();
		contactDetailType.setContactNumber(contactNo);
		contactDetailType.setContactNumberType(ContactNumberTypeCode.MOBILE);

		OBContactsType contact = new OBContactsType();
		ContactType contactType = new ContactType();
		contactType.setContactDetail(contactDetailType);

		contact.getContact().add(contactType);
		obPartyDetailType.setContacts(contact);

		OBEmailAddress emailAddress = new OBEmailAddress();
		emailAddress.setEmailAddress(request.getPrimaryEmailAddress());

		JAXBElement<EmailAddressDetailType> jaxbElement = objectFactory.createEmailAddressTypeEmailAddressDetail(emailAddress);
		EmailAddressType eaType = new EmailAddressType();
		eaType.setEmailAddressDetail(jaxbElement);

		OBEmailAddressesType addresses = new OBEmailAddressesType();
		addresses.getEmailAddress().add(eaType);

		obPartyDetailType.setEmailAddresses(addresses);
		pdt.setIndividual(individualType);
		obPartyDetailType.setPartyDetails(pdt);

		adviserDetailsType.setAdviserDetails(obPartyDetailType);
		accountAdviserType.setAdviserDetails(adviserDetailsType);
		
		return jaxbRequest;
	}
*/
}
