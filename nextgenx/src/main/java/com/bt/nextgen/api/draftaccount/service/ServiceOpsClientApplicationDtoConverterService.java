package com.bt.nextgen.api.draftaccount.service;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.draftaccount.model.ClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.ServiceOpsClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.tracking.model.PersonInfo;
import com.bt.nextgen.api.tracking.model.TrackingDto;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.serviceops.service.ServiceOpsClientApplicationStatus;

@Service
public class ServiceOpsClientApplicationDtoConverterService
{

	@Autowired
	private ClientApplicationDtoConverterService clientApplicationDtoConverterService;

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceOpsClientApplicationDtoConverterService.class);

	public ServiceOpsClientApplicationDto convertToDto(ClientApplication clientApplication, ServiceErrors serviceErrors)
	{

		ServiceOpsClientApplicationDto serviceOpsClientApplicationDto = new ServiceOpsClientApplicationDto();
		ClientApplicationDto clientApplicationDto = clientApplicationDtoConverterService.convertToDto(clientApplication,
			serviceErrors);

		LOGGER.info("ClientApplicationDto :  {}", clientApplicationDto);

		serviceOpsClientApplicationDto.setAdviserName(clientApplicationDto.getAdviserName());
		serviceOpsClientApplicationDto.setReferenceNumber(clientApplicationDto.getReferenceNumber());
		serviceOpsClientApplicationDto.setKey(clientApplicationDto.getKey());

		IClientApplicationForm form =clientApplication.getClientApplicationForm();
		String accountType = form.getAccountType().value();
		serviceOpsClientApplicationDto.setAccountType(accountType);
		serviceOpsClientApplicationDto.setAccountName(form.getAccountName());
		serviceOpsClientApplicationDto.setStatus(ServiceOpsClientApplicationStatus.FAILED);
		serviceOpsClientApplicationDto.setFailureMessage(getFailureMessage(clientApplication));
		serviceOpsClientApplicationDto.setLastModified(clientApplicationDto.getLastModified());
		serviceOpsClientApplicationDto.setLastModifiedByName(clientApplicationDto.getLastModifiedByName());
		serviceOpsClientApplicationDto.setProductName(clientApplicationDto.getProductName());

		return serviceOpsClientApplicationDto;
	}

	public ServiceOpsClientApplicationDto convertToDto(TrackingDto trackingDto, ServiceOpsClientApplicationStatus status) {
		ServiceOpsClientApplicationDto serviceOpsClientApplicationDto = new ServiceOpsClientApplicationDto();

		serviceOpsClientApplicationDto.setAdviserName(getFullName(trackingDto.getAdviser()));
		serviceOpsClientApplicationDto.setReferenceNumber(trackingDto.getReferenceNumber());
		serviceOpsClientApplicationDto.setKey(trackingDto.getClientApplicationId());

		serviceOpsClientApplicationDto.setAccountType(trackingDto.getAccountType());
		serviceOpsClientApplicationDto.setAccountNumber(trackingDto.getAccountId());
		serviceOpsClientApplicationDto.setAccountName(trackingDto.getDisplayName());
        serviceOpsClientApplicationDto.setStatus(status);

		serviceOpsClientApplicationDto.setLastModified(trackingDto.getLastModified());
		serviceOpsClientApplicationDto.setLastModifiedByName(getFullName(trackingDto.getLastModifiedBy()));
		serviceOpsClientApplicationDto.setProductName(trackingDto.getProductName());

		return serviceOpsClientApplicationDto;
	}

	private String getFullName(PersonInfo personInfo) {
		if(StringUtils.isNotEmpty(personInfo.getFirstName()) && StringUtils.isNotEmpty(personInfo.getLastName())) {

			return String.format("%s, %s",
					personInfo.getLastName(),
					personInfo.getFirstName());
		} else {
			return StringUtils.isNotEmpty(personInfo.getFirstName())? personInfo.getFirstName() : StringUtils.EMPTY;
		}

	}

	private String getFailureMessage(ClientApplication clientApplication) {
		StringBuilder failureMessage = new StringBuilder(clientApplication.getOnboardingApplication().getStatus().toString());
		if (clientApplication.getOnboardingApplication().getFailureMessage() != null) {
			failureMessage.append(" : ").append(clientApplication.getOnboardingApplication().getFailureMessage());
		}
		return failureMessage.toString();
	}

}
