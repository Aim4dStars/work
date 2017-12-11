/**
 * 
 */
package com.bt.nextgen.service.onboarding.btesb;

import com.bt.nextgen.service.onboarding.ResendRegistrationEmailResponse;
import ns.btfin_com.product.panorama.onboardingservice.onboardingresponse.v3_0.CreateOneTimePasswordSendEmailResponseMsgType;

import static ns.btfin_com.product.panorama.onboardingservice.onboardingresponse.v3_0.StatusTypeCode.SUCCESS;

/**
 * @author L055011
 * 
 */
public class ResendRegistrationEmailAdapter extends ResponseAdapter implements ResendRegistrationEmailResponse
{
	public ResendRegistrationEmailAdapter()
	{
	}

	public ResendRegistrationEmailAdapter(CreateOneTimePasswordSendEmailResponseMsgType jaxbResponse)
	{
		if (null != jaxbResponse && jaxbResponse.getStatus() != SUCCESS)
		{
			setServiceErrors(jaxbResponse.getResponseDetails().getErrorResponses().getErrorResponse());
		}
	}

	/**
	 * forming a jaxb request from pojo request
	 * @param request
	 * @return
	 */
	/*public CreateOneTimePasswordAndSendEmailRequestMsgType createResendRegistrationEmailRequest(
			ResendRegistrationEmailRequest request) 
	{
		ObjectFactory objectFactory = AvaloqObjectFactory
				.getOnboardingObjectFactory();
		OBOTPInvolvedPartyType partyType = objectFactory
				.createOBOTPInvolvedPartyType();
		AdviserDetailsType adviserDetailsType = new AdviserDetailsType();
		OBAdviserType adviserType = new OBAdviserType();

		InvolvedPartyDetailsType involvedPartyDetailsType = new InvolvedPartyDetailsType();

		EmailAddressesType emailAddressesType = new EmailAddressesType();
		emailAddressesType.getEmailAddress().add(
				getEmailAddresses(request.getAdviserPrimaryEmailAddress()));

		PartyDetailType partyDetailType = new PartyDetailType();
		IndividualType individualType = new IndividualType();
		individualType.setGivenName(request.getAdviserFirstName());
		individualType.setLastName(request.getAdviserLastName());
		partyDetailType.setIndividual(individualType);

		involvedPartyDetailsType.setPartyDetails(partyDetailType);
		involvedPartyDetailsType.setEmailAddresses(emailAddressesType);
		ContactsType contactsType = new ContactsType();

		contactsType.getContact().add(
				getContactDetail(request.getAdviserPrimaryContactNumber(),
						request.getAdviserPrimaryContactNumberType()));
		involvedPartyDetailsType.setContacts(contactsType);

		adviserDetailsType.setAdviserNumber(request.getAdviserOracleUserId());
		adviserDetailsType
				.setAdviserNumberIssuer(AdviserNumberIssuerType.WESTPAC);
		adviserDetailsType.setAdviserDetails(involvedPartyDetailsType);

		adviserType.setAdviserDetails(adviserDetailsType);

		// ------------------------------Investor------------------------------

		OBOTPInvestorType obotpInvestorType = new OBOTPInvestorType();
		OBOTPInvolvedPartyDetailsType obotpInvolvedPartyDetailsType = new OBOTPInvolvedPartyDetailsType();

		OBContactsType obContactsType = new OBContactsType();
		obContactsType.getContact().add(
				getContactDetail(request.getInvestorPrimaryContactNumber(),
						request.getInvestorPrimaryContactNumberType()));
		obotpInvolvedPartyDetailsType.setContacts(obContactsType);
		obotpInvolvedPartyDetailsType.setCustomerNumber(request
				.getInvestorOracleUserId());
		obotpInvolvedPartyDetailsType
				.setCustomerNumberIssuer(CustomerNoAllIssuerType.WESTPAC);
		OBEmailAddressesType obEmailAddressesType = new OBEmailAddressesType();

		obEmailAddressesType.getEmailAddress().add(
				getEmailAddresses(request.getInvestorPrimaryEmailAddress()));

		obotpInvolvedPartyDetailsType.setEmailAddresses(obEmailAddressesType);

		OBPartyDetailType obPartyDetailType = new OBPartyDetailType();
		IndividualType individualType1 = new IndividualType();
		individualType1.setLastName(request.getInvestorLastName());
		individualType1.setGivenName(request.getInvestorFirstName());
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		Date dateOfBirth = new Date();// Date of birth
		gregorianCalendar.setTime(dateOfBirth);
		XMLGregorianCalendar dateOfBirth2 = null;
		try {
			dateOfBirth2 = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(gregorianCalendar);

		} catch (DatatypeConfigurationException e) {
			logger.error(
					"Exception Occured while forming a request for ResendRegistrationEmail Operation of OnboradingService {} ",
					e.getMessage());

		}
		individualType1.setDateOfBirth(dateOfBirth2);
		if(StringUtils.isNotEmpty(request.getInvestorGender()))
		{
			individualType1.setGender(GenderTypeCode.fromValue(StringUtils
					.capitalize(request.getInvestorGender()))); // /Gender
		}
		individualType1.setTitlePrefix(request.getInvestorSalutation());
		obPartyDetailType.setIndividual(individualType1);
		obotpInvolvedPartyDetailsType.setPartyDetails(obPartyDetailType);
		obotpInvestorType.setInvestorDetails(obotpInvolvedPartyDetailsType);

		partyType.setInvestor(obotpInvestorType);
		partyType.setAdviser(adviserType);

		CreateOneTimePasswordAndSendEmailRequestMsgType type = new CreateOneTimePasswordAndSendEmailRequestMsgType();
		CreateOneTimePasswordAndSendEmailRequestMsgType message = objectFactory
				.createCreateOneTimePasswordAndSendEmailRequestMsg(type)
				.getValue();
		if(request.getPersonRole()!=null)
		{
			if (request.getPersonRole().equals(Roles.ROLE_INVESTOR)) {
				message.setAccountPartyRole(OBAccountPartyRole.ACCOUNT_OWNER_ROLE);
			} else {
				message.setAccountPartyRole(OBAccountPartyRole.INTERMEDIARY_ROLE);
			}
		}
		
		message.setInvolvedPartys(partyType);
		return message;
	}

	*/
}
