package com.bt.nextgen.api.client.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import au.com.westpac.gn.common.xsd.identifiers.v1.RegistrationNumberType;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.AddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.DocumentAttribute;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.DocumentAttributeName;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.IdentityVerificationDocument;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.Individual;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.IndividualIDVAssessment;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.IndividualName;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.InvolvedPartyRegistrationArrangement;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.RetrieveIDVDetailsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.CreateIndividualIPResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainarrangementandiparrangementrelationships.v1.svc0256.ActionCode;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.EmailAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.InvolvedPartyArrangementRole;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.PhoneAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.StandardPostalAddress;

import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.RoleType;
import com.bt.nextgen.serviceops.model.CreateIndividualIPEmailPhoneContactMethodsReqModel;
import com.bt.nextgen.serviceops.model.CreateIndividualIPReqModel;
import com.bt.nextgen.serviceops.model.MaintainArrangementAndRelationshipReqModel;
import com.bt.nextgen.serviceops.model.MaintainIdvDetailReqModel;
import com.bt.nextgen.serviceops.model.RetrieveIDVDetailsReqModel;

/**
 * Created by L091297 on 08/06/2017.
 */
@SuppressWarnings("squid:S1200")
// Single Responsibility Principle
@Component
public class SiloMovementRequestBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerRawData.class);

	private final static String DATE_FORMAT = "dd-MMM-yyyy";

	private final static String ACCOUNT_PREFIX = "NG-002-";

	private final static String USE_CASE_1346 = "35d1b65704184ae3b87799400f7ab93c";

	private final static String USE_CASE_25 = "457eba2b65ca4c2f937d0deae9866312";

	private final static String USE_CASE_1 = "ipar";

	private final static String USE_CASE_2 = "ipsar";

	private final static String USE_CASE_4 = "enddateiparsol";

	private final static String USE_CASE_5 = "enddateipsar";

	private final static String OPT_TYPE = "create";

	public CreateIndividualIPReqModel get336ReqModel(RetrieveDetailsAndArrangementRelationshipsForIPsResponse input, RoleType roleType,
			String targetSilo) {
		LOGGER.info("Calling SiloMovementRequestBuilder.getCreateIndividualIPRequestModel");
		CreateIndividualIPReqModel createIndividualIPRequest = new CreateIndividualIPReqModel();
		if (null != input.getIndividual()) {
			setCreateIndividualPersonalInfo(createIndividualIPRequest, input);
			setCreateIndividualPersonalInfomation(createIndividualIPRequest, input);

			if (null != input.getIndividual().getPurposeOfBusinessRelationship()
					&& !input.getIndividual().getPurposeOfBusinessRelationship().isEmpty()) {
				createIndividualIPRequest
						.setPurposeOfBusinessRelationship(input.getIndividual().getPurposeOfBusinessRelationship().get(0).getValue());
			}

			if ((null != input.getIndividual().getHasForeignRegistration() && !input.getIndividual().getHasForeignRegistration().isEmpty())
					&& (null != input.getIndividual().getHasForeignRegistration().get(0).getRegistrationIdentifier() && !input.getIndividual()
							.getHasForeignRegistration().get(0).getRegistrationIdentifier().isEmpty())) {
				createIndividualIPRequest.setRegistrationIdentifierNumber(input.getIndividual().getHasForeignRegistration().get(0)
						.getRegistrationIdentifier().get(0).getRegistrationNumber());
			}

			createIndividualIPRequest.setRoleType("Customer");
			createIndividualIPRequest.setSilo(targetSilo);
			if (null != input.getIndividual().getSourceOfFunds() && !input.getIndividual().getSourceOfFunds().isEmpty()) {
				createIndividualIPRequest.setSourceOfFunds(input.getIndividual().getSourceOfFunds().get(0).getValue());
			}

			if (null != input.getIndividual().getSourceOfWealth() && !input.getIndividual().getSourceOfWealth().isEmpty()) {
				createIndividualIPRequest.setSourceOfWealth(input.getIndividual().getSourceOfWealth().get(0).getValue());
			}
			setStandardPostalAddress(input, createIndividualIPRequest);
			setAddressForService336(input, createIndividualIPRequest);
			// employmentDetails
			if (null != input.getIndividual().getEmploymentDetails()) {
				createIndividualIPRequest.setEmploymentType(input.getIndividual().getEmploymentDetails().getEmploymentType());
			}

			if (null != input.getIndividual().getHasPostalAddressContactMethod()
					&& !input.getIndividual().getHasPostalAddressContactMethod().isEmpty()) {
				createIndividualIPRequest.setUsage(input.getIndividual().getHasPostalAddressContactMethod().get(0).getUsage());
				LOGGER.info("Request created for SiloMovementRequestBuilder.getCreateIndividualIPRequestModel: "
						+ createIndividualIPRequest.toString());
			}
		}
		return createIndividualIPRequest;
	}

	public CreateIndividualIPEmailPhoneContactMethodsReqModel get336EmailPhoneReqModel(
			RetrieveDetailsAndArrangementRelationshipsForIPsResponse input, RoleType roleType, String targetSilo) {
		LOGGER.info("Calling SiloMovementRequestBuilder.getCreateIndividualIPRequestModel");
		CreateIndividualIPEmailPhoneContactMethodsReqModel emailPhoneContactMethodsReqModel = null;
		if (null != input.getIndividual()) {
			emailPhoneContactMethodsReqModel = setEmailContactMethod(input);
		}
		return emailPhoneContactMethodsReqModel;
	}

	@SuppressWarnings("squid:S1200")
	private CreateIndividualIPEmailPhoneContactMethodsReqModel setEmailContactMethod(RetrieveDetailsAndArrangementRelationshipsForIPsResponse input) {

		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual individual = input
				.getIndividual();
		EmailAddressContactMethod emailAddressContactMethod = !individual.getHasEmailAddressContactMethod().isEmpty() ? individual
				.getHasEmailAddressContactMethod().get(0) : null;

		PhoneAddressContactMethod phoneAddressContactMethod = !individual.getHasPhoneAddressContactMethod().isEmpty() ? individual
				.getHasPhoneAddressContactMethod().get(0) : null;

		CreateIndividualIPEmailPhoneContactMethodsReqModel emailPhoneContactMethodsReqModel = null;
		if (null != emailAddressContactMethod) {
			emailPhoneContactMethodsReqModel = new CreateIndividualIPEmailPhoneContactMethodsReqModel();
			emailPhoneContactMethodsReqModel
					.setEmailAddressContactAddressee(null != emailAddressContactMethod.getAddressee() ? emailAddressContactMethod.getAddressee()
							.getFullName() : null);

			emailPhoneContactMethodsReqModel
					.setEmailAddressContactEmailAddress(null != emailAddressContactMethod.getHasAddress() ? emailAddressContactMethod.getHasAddress()
							.getEmailAddress() : null);
			emailPhoneContactMethodsReqModel.setHasEmailContactMethodUsage(emailAddressContactMethod.getUsage());
			emailPhoneContactMethodsReqModel.setEmailAddressContactPreferredContactTime(emailAddressContactMethod.getPreferredContactTime());
			emailPhoneContactMethodsReqModel
					.setEmailAddressContactPriorityLevel(null != emailAddressContactMethod.getPriorityLevel() ? emailAddressContactMethod
							.getPriorityLevel().name() : null);
			emailPhoneContactMethodsReqModel.setEmailAddressContactValidityStatus(emailAddressContactMethod.getValidityStatus());
			setEmailContactMethodIdentifier(emailPhoneContactMethodsReqModel, emailAddressContactMethod);
		}

		if (null != phoneAddressContactMethod) {
			if (emailPhoneContactMethodsReqModel == null) {
				emailPhoneContactMethodsReqModel = new CreateIndividualIPEmailPhoneContactMethodsReqModel();
			}
			emailPhoneContactMethodsReqModel.setPhoneAddressContactContactInstructions(phoneAddressContactMethod.getContactInstructions());
			emailPhoneContactMethodsReqModel.setPhoneAddressContactContactMedium(phoneAddressContactMethod.getContactMedium());

			emailPhoneContactMethodsReqModel.setPhoneAddressContactPreferredContactTime(phoneAddressContactMethod.getPreferredContactTime());
			emailPhoneContactMethodsReqModel
					.setPhoneAddressContactPriorityLevel(null != phoneAddressContactMethod.getPriorityLevel() ? phoneAddressContactMethod
							.getPriorityLevel().name() : null);

			// createIndividualIPRequest.setPhoneAddressContactStartDate(phoneAddressContactMethod.getStartDate());
			emailPhoneContactMethodsReqModel.setHasPhoneContactMethodUsage(phoneAddressContactMethod.getUsage());
			emailPhoneContactMethodsReqModel.setPhoneAddressContactValidityStatus(phoneAddressContactMethod.getValidityStatus());
			setPhoneContactMethodContactIdentifier(emailPhoneContactMethodsReqModel, phoneAddressContactMethod);
			setPhoneContactMethodHasAddress(emailPhoneContactMethodsReqModel, phoneAddressContactMethod);
		}

		return emailPhoneContactMethodsReqModel;
	}

	private CreateIndividualIPEmailPhoneContactMethodsReqModel setEmailContactMethodIdentifier(
			CreateIndividualIPEmailPhoneContactMethodsReqModel emailPhoneContactMethodsReqModel, EmailAddressContactMethod emailAddressContactMethod) {

		if (null != emailAddressContactMethod) {
			emailPhoneContactMethodsReqModel
					.setEmailAddressContactContactMethodId(null != emailAddressContactMethod.getContactMethodIdentifier() ? emailAddressContactMethod
							.getContactMethodIdentifier().getContactMethodId() : null);

			emailPhoneContactMethodsReqModel
					.setEmailAddressContactIdentificationScheme((null != emailAddressContactMethod.getContactMethodIdentifier() && null != emailAddressContactMethod
							.getContactMethodIdentifier().getIdentificationScheme()) ? emailAddressContactMethod.getContactMethodIdentifier()
							.getIdentificationScheme().name() : null);

			emailPhoneContactMethodsReqModel
					.setEmailAddressContactSourceSystem(null != emailAddressContactMethod.getContactMethodIdentifier() ? emailAddressContactMethod
							.getContactMethodIdentifier().getSourceSystem() : null);
		}
		return emailPhoneContactMethodsReqModel;
	}

	private CreateIndividualIPEmailPhoneContactMethodsReqModel setPhoneContactMethodHasAddress(
			CreateIndividualIPEmailPhoneContactMethodsReqModel emailPhoneContactMethodsReqModel, PhoneAddressContactMethod phoneAddressContactMethod) {

		if (null != phoneAddressContactMethod) {
			emailPhoneContactMethodsReqModel
					.setPhoneAddressContactAreaCode(null != phoneAddressContactMethod.getHasAddress() ? phoneAddressContactMethod.getHasAddress()
							.getAreaCode() : null);

			emailPhoneContactMethodsReqModel
					.setPhoneAddressContactCountryCode(null != phoneAddressContactMethod.getHasAddress() ? phoneAddressContactMethod.getHasAddress()
							.getCountryCode() : null);
			emailPhoneContactMethodsReqModel
					.setPhoneAddressContactLocalNumber(null != phoneAddressContactMethod.getHasAddress() ? phoneAddressContactMethod.getHasAddress()
							.getLocalNumber() : null);
		}
		return emailPhoneContactMethodsReqModel;
	}

	private void setPhoneContactMethodContactIdentifier(CreateIndividualIPEmailPhoneContactMethodsReqModel emailPhoneContactMethodsReqModel,
			PhoneAddressContactMethod phoneAddressContactMethod) {

		emailPhoneContactMethodsReqModel
				.setPhoneAddressContactSourceSystem(null != phoneAddressContactMethod.getContactMethodIdentifier() ? phoneAddressContactMethod
						.getContactMethodIdentifier().getSourceSystem() : null);

		emailPhoneContactMethodsReqModel
				.setPhoneAddressContactFullTelephoneNumber(null != phoneAddressContactMethod.getContactMethodIdentifier() ? phoneAddressContactMethod
						.getContactMethodIdentifier().getContactMethodId() : null);

		emailPhoneContactMethodsReqModel
				.setPhoneAddressContactIdentificationScheme((null != phoneAddressContactMethod.getContactMethodIdentifier() && null != phoneAddressContactMethod
						.getContactMethodIdentifier().getIdentificationScheme()) ? phoneAddressContactMethod.getContactMethodIdentifier()
						.getIdentificationScheme().name() : null);

		emailPhoneContactMethodsReqModel
				.setPhoneAddressContactContactMethodId(null != phoneAddressContactMethod.getContactMethodIdentifier() ? phoneAddressContactMethod
						.getContactMethodIdentifier().getContactMethodId() : null);
	}

	private void setCreateIndividualPersonalInfo(CreateIndividualIPReqModel createIndividualIPRequest,
			RetrieveDetailsAndArrangementRelationshipsForIPsResponse input) {
		SimpleDateFormat dateFormater = new SimpleDateFormat(DATE_FORMAT);
		if (null != input.getIndividual().getHasPostalAddressContactMethod() && !input.getIndividual().getHasPostalAddressContactMethod().isEmpty()
				&& null != input.getIndividual().getHasPostalAddressContactMethod().get(0).getAddressee()) {
			createIndividualIPRequest.setAddresseeNameText(input.getIndividual().getHasPostalAddressContactMethod().get(0).getAddressee()
					.getNameText().get(0));
		}
		createIndividualIPRequest.setAddressType("StandardPostalAddress");
		if (null != input.getIndividual().getHasForName() && null != input.getIndividual().getHasForName().getHasAlternateName()
				&& !input.getIndividual().getHasForName().getHasAlternateName().isEmpty()) {
			createIndividualIPRequest.setAltName(input.getIndividual().getHasForName().getHasAlternateName().get(0).getName());
			createIndividualIPRequest.setAlternateName(input.getIndividual().getHasForName().getHasAlternateName().get(0).getName());
		}
		createIndividualIPRequest.setIsPreferred("");
		if (null != input.getIndividual().getBirthDate()) {
			createIndividualIPRequest.setBirthDate(dateFormater.format(input.getIndividual().getBirthDate().getValue().toGregorianCalendar()
					.getTime()));
		}
	}

	private void setCreateIndividualPersonalInfomation(CreateIndividualIPReqModel createIndividualIPRequest,
			RetrieveDetailsAndArrangementRelationshipsForIPsResponse input) {

		if (null != input.getIndividual().getHasForName()) {
			createIndividualIPRequest.setFirstName(input.getIndividual().getHasForName().getFirstName());
			createIndividualIPRequest.setPrefix(input.getIndividual().getHasForName().getPrefixTitle());
			if (null != input.getIndividual().getHasForName().getMiddleNames() && !input.getIndividual().getHasForName().getMiddleNames().isEmpty()) {
				createIndividualIPRequest.setMiddleNames(input.getIndividual().getHasForName().getMiddleNames().get(0));
			}
			createIndividualIPRequest.setPreferredName(input.getIndividual().getHasForName().getPreferredName());
			createIndividualIPRequest.setForeignRegistered(input.getIndividual().getIsForeignRegistered());
			createIndividualIPRequest.setGender(input.getIndividual().getGender());
			createIndividualIPRequest.setLastName(input.getIndividual().getHasForName().getLastName());
		}
	}

	private void setAddressForService336(RetrieveDetailsAndArrangementRelationshipsForIPsResponse input,
			CreateIndividualIPReqModel createIndividualIPRequest) {
		StandardPostalAddress spadd = null;
		if (null != input.getIndividual().getHasPostalAddressContactMethod() && !input.getIndividual().getHasPostalAddressContactMethod().isEmpty()) {
			spadd = (au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.StandardPostalAddress) input
					.getIndividual().getHasPostalAddressContactMethod().get(0).getHasAddress();
			if (spadd != null) {
				createIndividualIPRequest.setAddressLine1(spadd.getInternalIdentifier().getAddressKeyLine1());
				createIndividualIPRequest.setAddressLine2(spadd.getInternalIdentifier().getAddressKeyLine2());
				createIndividualIPRequest.setAddressLine3(spadd.getInternalIdentifier().getAddressKeyLine2());
				createIndividualIPRequest.setFloorNumber(spadd.getFloorNumber());
				createIndividualIPRequest.setUnitNumber(spadd.getUnitNumber());
				createIndividualIPRequest.setBuildingName(spadd.getBuildingName());
			}
		}
	}

	private void setStandardPostalAddress(RetrieveDetailsAndArrangementRelationshipsForIPsResponse input,
			CreateIndividualIPReqModel createIndividualIPRequest) {
		StandardPostalAddress spadd = null;
		if (null != input.getIndividual().getHasPostalAddressContactMethod() && !input.getIndividual().getHasPostalAddressContactMethod().isEmpty()) {
			spadd = (au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.StandardPostalAddress) input
					.getIndividual().getHasPostalAddressContactMethod().get(0).getHasAddress();
			if (spadd != null) {
				createIndividualIPRequest.setState(spadd.getState());
				createIndividualIPRequest.setStreetName(spadd.getStreetName());
				createIndividualIPRequest.setStreetNumber(spadd.getStreetNumber());
				createIndividualIPRequest.setStreetType(spadd.getStreetType());
				createIndividualIPRequest.setPostCode(spadd.getPostCode());
				createIndividualIPRequest.setCity(spadd.getCity());
				createIndividualIPRequest.setCountry(spadd.getCountry());
			}
		}
	}

	// 256
	public MaintainArrangementAndRelationshipReqModel get256ReqModel(String cisKey, String personType, InvolvedPartyArrangementRole arrangements,
			String opType) {
		SimpleDateFormat dateFormater = new SimpleDateFormat(DATE_FORMAT);
		LOGGER.info("Calling SiloMovementRequestBuilder.getMaintainArrangementAndRelationshipReqModel");
		MaintainArrangementAndRelationshipReqModel reqModel = new MaintainArrangementAndRelationshipReqModel();
		reqModel.setCisKey(cisKey);
		reqModel.setPersonType(personType);
		reqModel.setRequestedAction(ActionCode.MAINTAIN_ARRANGEMENT_AND_RELATIONSHIPS);

		if (null != arrangements && null != arrangements.getHasForContext()) {
			if (null != arrangements.getHasForContext().getAccountArrangementIdentifier()) {
				reqModel.setAccountNumber(arrangements.getHasForContext().getAccountArrangementIdentifier().getAccountNumber());
				reqModel.setBsbNumber(arrangements.getHasForContext().getAccountArrangementIdentifier().getAccountNumber());
			}

			reqModel.setLifecycleStatus(arrangements.getHasForContext().getLifecycleStatus());
			reqModel.setLifecycleStatusReason(arrangements.getHasForContext().getLifecycleStatusReason());
			if (null != arrangements.getHasForContext().getProductArrangementIdentifier()) {
				reqModel.setPanNumber(arrangements.getHasForContext().getProductArrangementIdentifier().getArrangementId()
						.replace(ACCOUNT_PREFIX, ""));
			}
			if (null != arrangements.getHasForContext().getAccountArrangementIdentifier()) {
				reqModel.setBsbNumber(arrangements.getHasForContext().getAccountArrangementIdentifier().getBsbNumber());
			}
			reqModel.setStartDate(dateFormater.format(new Date()));
			LOGGER.info("Request created for SiloMovementRequestBuilder.getMaintainArrangementAndRelationshipReqModel: " + reqModel.toString());
			setUsecase(arrangements, reqModel, opType);
		}
		if (null != arrangements && null != arrangements.getAuditContext() && null != arrangements.getAuditContext().getVersionNumber()) {
			reqModel.setVersionNumberAr(arrangements.getAuditContext().getVersionNumber());
			reqModel.setVersionNumberIpAr(arrangements.getAuditContext().getVersionNumber());
		} else {
			reqModel.setVersionNumberAr("0");
			reqModel.setVersionNumberIpAr("0");
		}
		return reqModel;
	}

	private void setUsecase(InvolvedPartyArrangementRole arrangements, MaintainArrangementAndRelationshipReqModel reqModel, String opType) {
		String useCaseCode = null;
		if (null != arrangements.getHasForContext() && null != arrangements.getHasForContext().getAccountArrangementIdentifier()
				&& null != arrangements.getHasForContext().getAccountArrangementIdentifier().getCanonicalProductCode()) {
			useCaseCode = arrangements.getHasForContext().getAccountArrangementIdentifier().getCanonicalProductCode();
			useCaseCode = useCaseCode.trim();
			reqModel.setProductCpc(useCaseCode);

		} else if (null != arrangements.getHasForContext() && null != arrangements.getHasForContext().getProductArrangementIdentifier()
				&& null != arrangements.getHasForContext().getProductArrangementIdentifier().getCanonicalProductCode()) {
			useCaseCode = arrangements.getHasForContext().getProductArrangementIdentifier().getCanonicalProductCode();
			useCaseCode = useCaseCode.trim();
			reqModel.setProductCpc(useCaseCode);
		}

		if (useCaseCode != null && OPT_TYPE.equalsIgnoreCase(opType)) {
			LOGGER.info("Request created for SiloMovementRequestBuilder.getMaintainArrangementAndRelationshipReqModel for IP AR Create ");
			switch (useCaseCode) {
			case USE_CASE_1346:
				reqModel.setUseCase(USE_CASE_1);
				break;
			case USE_CASE_25:
				reqModel.setUseCase(USE_CASE_2);
				break;
			default:
				break;
			}
		} else {
			LOGGER.info("Request created for SiloMovementRequestBuilder.getMaintainArrangementAndRelationshipReqModel for IP AR END Date ");
			switch (useCaseCode) {
			case USE_CASE_1346:
				reqModel.setUseCase(USE_CASE_4);
				break;
			case USE_CASE_25:
				reqModel.setUseCase(USE_CASE_5);
				break;
			default:
				break;
			}
		}
	}

	public RetrieveIDVDetailsReqModel get324ReqModel(String cisKey, String personType, String silo) {
		LOGGER.info("Calling SiloMovementRequestBuilder.get324ReqModel");
		RetrieveIDVDetailsReqModel reqModel = new RetrieveIDVDetailsReqModel();
		reqModel.setCisKey(cisKey);
		reqModel.setPersonType(personType);
		reqModel.setSilo(silo);
		LOGGER.info("Request created for SiloMovementRequestBuilder.get324ReqModel: " + reqModel.toString());
		return reqModel;
	}

	public MaintainIdvDetailReqModel get325ReqModel(RetrieveIDVDetailsResponse response324, CreateIndividualIPResponse response336) {
		LOGGER.info("Calling SiloMovementRequestBuilder.get325ReqModel");
		MaintainIdvDetailReqModel reqModel = new MaintainIdvDetailReqModel();
		reqModel.setRequestedAction("update");
		if (response336 != null && null != response336.getIndividual() && null != response336.getIndividual().getInvolvedPartyIdentifier()
				&& !response336.getIndividual().getInvolvedPartyIdentifier().isEmpty()) {
			reqModel.setCisKey(response336.getIndividual().getInvolvedPartyIdentifier().get(0).getInvolvedPartyId());
		}
		if (null != response324.getIdentityVerificationAssessment() && !response324.getIdentityVerificationAssessment().isEmpty()) {

			IndividualIDVAssessment individualIDVAssessment = (IndividualIDVAssessment) response324.getIdentityVerificationAssessment().get(0);

			if (null != individualIDVAssessment.getPerformedExternallyBy()
					&& null != individualIDVAssessment.getPerformedExternallyBy().getRoleIsPlayedBy()
					&& null != individualIDVAssessment.getPerformedExternallyBy().getRoleIsPlayedBy().getHasForName()
					&& !individualIDVAssessment.getPerformedExternallyBy().getRoleIsPlayedBy().getHasForName().isEmpty()) {

				IndividualName individualName = individualIDVAssessment.getPerformedExternallyBy().getRoleIsPlayedBy().getHasForName().get(0);

				reqModel.setAgentCisKey(individualIDVAssessment.getPerformedExternallyBy().getExternalIdentifier().getEmployeeNumber());
				reqModel.setAgentName(individualName.getFullName());
				reqModel.setEmployerName(individualIDVAssessment.getPerformedExternallyBy().getRoleIsPlayedBy().getEmploymentDetails()
						.getCurrentEmployer());
			}

			setDocuments(reqModel, individualIDVAssessment);
			setAddress(reqModel, individualIDVAssessment);
		}
		if (null != response324.getInvolvedParty()) {
			Individual individual = (Individual) response324.getInvolvedParty();
			if (null != individual.getIsPlayingRole() && null != individual.getIsPlayingRole().getCustomerIdentifier()) {
				reqModel.setCustomerNumber(individual.getIsPlayingRole().getCustomerIdentifier().getCustomerNumber());
			}
			setIndividual(reqModel, individual);
		}

		LOGGER.info("Request created for SiloMovementRequestBuilder.get325ReqModel: " + reqModel.toString());
		return reqModel;
	}

	private void setIndividual(MaintainIdvDetailReqModel reqModel, Individual individual) {
		SimpleDateFormat dateFormater = new SimpleDateFormat(DATE_FORMAT);
		if (null != individual.getHasForName() && !individual.getHasForName().isEmpty()) {
			if (null != individual.getBirthDate()) {
				reqModel.setDateOfBirth(dateFormater.format(individual.getBirthDate().getValue().toGregorianCalendar().getTime()));
			}
			reqModel.setFirstName(individual.getHasForName().get(0).getFirstName());
			String middleName = "";
			if ((null != individual.getHasForName() && !individual.getHasForName().isEmpty())
					&& (null != individual.getHasForName().get(0).getMiddleNames() && !individual.getHasForName().get(0).getMiddleNames().isEmpty())) {
				middleName = individual.getHasForName().get(0).getMiddleNames().get(0);
			}
			reqModel.setMiddleName(middleName);
			reqModel.setLastName(individual.getHasForName().get(0).getLastName());
			reqModel.setFullName(individual.getHasForName().get(0).getFirstName() + " " + middleName + " "
					+ individual.getHasForName().get(0).getLastName());
			reqModel.setIsSoleTrader(null != individual.getIsSoleTrader().value() ? individual.getIsSoleTrader().value() : "select");
		}
	}

	private void setAddress(MaintainIdvDetailReqModel reqModel, IndividualIDVAssessment individualIDVAssessment) {
		if (null != individualIDVAssessment.getHasForSubject()) {
			if (null != individualIDVAssessment.getHasForSubject().getHasContactMethod()
					&& !individualIDVAssessment.getHasForSubject().getHasContactMethod().isEmpty()) {
				StringBuilder usage = new StringBuilder();
				StringBuilder addressl1 = new StringBuilder();
				StringBuilder addressl2 = new StringBuilder();
				StringBuilder city = new StringBuilder();
				for (AddressContactMethod addressContactMethod : individualIDVAssessment.getHasForSubject().getHasContactMethod()) {

					usage = !StringUtils.isBlank(usage.toString()) ? usage.append(",").append(addressContactMethod.getUsage().value()) : usage
							.append(addressContactMethod.getUsage().value());

					addressl1 = !StringUtils.isBlank(addressl1.toString()) ? addressl1.append(",").append(
							addressContactMethod.getHasAddress().getAddressLine1()) : addressl1.append(addressContactMethod.getHasAddress()
							.getAddressLine1());

					addressl2 = !StringUtils.isBlank(addressl2.toString()) ? addressl2.append(",").append(
							addressContactMethod.getHasAddress().getAddressLine2()) : addressl2.append(addressContactMethod.getHasAddress()
							.getAddressLine2());

					city = !StringUtils.isBlank(city.toString()) ? city.append(",").append(addressContactMethod.getHasAddress().getCity()) : city
							.append(addressContactMethod.getHasAddress().getCity());
				}
				setAddressValuesForMaintainIdvDetailReq(reqModel, individualIDVAssessment, usage, addressl1, addressl2, city);
			}
			setAddressFor325(reqModel, individualIDVAssessment);
		}
	}

	private void setAddressValuesForMaintainIdvDetailReq(MaintainIdvDetailReqModel reqModel, IndividualIDVAssessment individualIDVAssessment,
			StringBuilder usage, StringBuilder addressl1, StringBuilder addressl2, StringBuilder city) {
		StringBuilder state = new StringBuilder();
		StringBuilder postalCode = new StringBuilder();
		StringBuilder country = new StringBuilder();
		StringBuilder addressType = new StringBuilder();
		for (AddressContactMethod addressContactMethod : individualIDVAssessment.getHasForSubject().getHasContactMethod()) {
			state = !StringUtils.isBlank(state.toString()) ? state.append(",").append(addressContactMethod.getHasAddress().getState()) : state
					.append(addressContactMethod.getHasAddress().getState());

			postalCode = !StringUtils.isBlank(postalCode.toString()) ? postalCode.append(",").append(
					addressContactMethod.getHasAddress().getPostCode()) : postalCode.append(addressContactMethod.getHasAddress().getPostCode());

			country = !StringUtils.isBlank(country.toString()) ? country.append(",").append(addressContactMethod.getHasAddress().getCountry())
					: country.append(addressContactMethod.getHasAddress().getCountry());
			addressType = !StringUtils.isBlank(addressType.toString()) ? addressType.append(",").append(
					addressContactMethod.getHasAddress().getPostalAddressType()) : addressType.append(addressContactMethod.getHasAddress()
					.getPostalAddressType());
		}

		reqModel.setUsage(usage.toString());
		reqModel.setAddressline1(addressl1.toString());
		reqModel.setAddressline2(addressl2.toString());
		reqModel.setCity(city.toString());
		reqModel.setState(state.toString());
		reqModel.setPincode(postalCode.toString());
		reqModel.setCountry(country.toString());
		reqModel.setPostalAddressType(addressType.toString());

	}

	private void setAddressFor325(MaintainIdvDetailReqModel reqModel, IndividualIDVAssessment individualIDVAssessment) {
		SimpleDateFormat dateFormater = new SimpleDateFormat(DATE_FORMAT);
		if (null != individualIDVAssessment.getHasForSubject().getHasRegistration()) {
			InvolvedPartyRegistrationArrangement involvedPartyRegistrationArrangement = individualIDVAssessment.getHasForSubject()
					.getHasRegistration();

			if (null != involvedPartyRegistrationArrangement.getRegistrationIdentifier()
					&& !involvedPartyRegistrationArrangement.getRegistrationIdentifier().isEmpty()) {

				reqModel.setRegistrationNumber(individualIDVAssessment.getHasForSubject().getHasRegistration().getRegistrationIdentifier().get(0)
						.getRegistrationNumber());
				reqModel.setRegistrationNumberType(null != individualIDVAssessment.getHasForSubject().getHasRegistration()
						.getRegistrationIdentifier().get(0).getRegistrationNumberType() ? individualIDVAssessment.getHasForSubject()
						.getHasRegistration().getRegistrationIdentifier().get(0).getRegistrationNumberType().value() : RegistrationNumberType.ABN
						.value());
			}
			Individual individual = individualIDVAssessment.getHasForSubject();
			if (null != individual && null != individual.getHasForName() && !individual.getHasForName().isEmpty()
					&& null != individual.getHasForName().get(1)) {

				reqModel.setInvolvedPartyNameType(individual.getHasForName().get(1).getInvolvedPartyNameType().value());
			} else {
				reqModel.setInvolvedPartyNameType("select");
			}
		}
		reqModel.setEvBusinessEntityName(individualIDVAssessment.getEvBusinessEntityName());
		reqModel.setEvRecepientEmail1(individualIDVAssessment.getEvRecepientEmail1());
		reqModel.setEvRecepientEmail2(individualIDVAssessment.getEvRecepientEmail2());
		if (null != individualIDVAssessment.getExternalIDVDate()) {
			reqModel.setExtIdvDate(dateFormater.format(individualIDVAssessment.getExternalIDVDate().getValue().toGregorianCalendar().getTime()));
		}
	}

	private void setDocuments(MaintainIdvDetailReqModel reqModel, IndividualIDVAssessment individualIDVAssessment) {
		if (null != individualIDVAssessment.getIdentityVerificationDocument() && !individualIDVAssessment.getIdentityVerificationDocument().isEmpty()) {
			StringBuilder docAttributeName = new StringBuilder();
			StringBuilder docAttributeValue = new StringBuilder();
			for (IdentityVerificationDocument identityVerificationDocument : individualIDVAssessment.getIdentityVerificationDocument()) {
				reqModel.setDocumentType(identityVerificationDocument.getDocumentType());
				for (DocumentAttribute documentAttribute : identityVerificationDocument.getDocumentAttribute()) {

					docAttributeName = !StringUtils.isBlank(docAttributeName.toString()) ? docAttributeName.append(",").append(
							documentAttribute.getAttributeName().value()) : docAttributeName.append(documentAttribute.getAttributeName().value());

					docAttributeValue = !StringUtils.isBlank(docAttributeValue.toString()) ? docAttributeValue.append(",").append(
							documentAttribute.getAttributeValue()) : docAttributeValue.append(documentAttribute.getAttributeValue());

				}
			}
			reqModel.setOptAttrName(StringUtils.isBlank(docAttributeName.toString()) ? DocumentAttributeName.IND_IDV_DOC_REF_NUM.value()
					: docAttributeName.toString());
			reqModel.setOptAttrVal(docAttributeValue.toString());
			reqModel.setIdvType("Individual");
		}
	}
}
