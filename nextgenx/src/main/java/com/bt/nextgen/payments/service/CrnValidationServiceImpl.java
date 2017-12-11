package com.bt.nextgen.payments.service;

import au.com.bpay.payments.biller.CustomerReferenceNumberValidLengths;
import au.com.bpay.payments.biller.CustomerReferenceNumberValidationParameters;
import au.com.bpay.payments.crnvalidator.services.CrnValidationException;
import au.com.bpay.payments.crnvalidator.services.CrnValidator;
import au.com.bpay.payments.crnvalidator.services.CrnValidatorFactory;
import au.com.cardlink.common.services.checkdigit.CheckDigitCalculatorFactory;
import com.bt.nextgen.clients.util.JaxbUtil;
import com.bt.nextgen.payments.domain.CRNType;
import com.bt.nextgen.payments.domain.IcrnJaxb;
import com.bt.nextgen.payments.domain.IcrnResponse;
import com.bt.nextgen.payments.repository.BpayBiller;
import com.bt.nextgen.payments.repository.BpayBillerCodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CrnValidationServiceImpl implements CrnValidationService
{
	private static final Logger logger = LoggerFactory.getLogger(CrnValidationServiceImpl.class);
	private static final String ICRN_RESPONSE_FILE_NAME = "/webservices/response/IcrnResponse.xml";

	@Autowired
	private BpayBillerCodeRepository bpayBillerCodeRepository;
	char identifier = 'Y';
	
	private static final List<Boolean> VALID_LENGTHS = Arrays.asList(false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false, false, false, false, false, false);

	@Override
	public boolean hasValidBpayCrn(CrnValidationServiceCompatible toValidate)
	{
		BpayBiller biller = bpayBillerCodeRepository.load(toValidate.getBillerCode());
		if (biller == null)
		{
			return false;
		}
		else if(CRNType.ICRN.equals(biller.getCrnType()))
		{
			return validateIcrn(toValidate);
		}
		else
		{
			return validate(toValidate.getCustomerReference(), biller);
		}
	}

	private boolean validateIcrn(CrnValidationServiceCompatible toValidate)
	{
		//TODO: just reading true or false from ICRNResponse.xml file for testing purpose.
		// In future it will be webservice call to Avaloq
		logger.info("Validating ICRN for customerReference {} and billerCode {}", toValidate.getCustomerReference(), toValidate.getBillerCode());
		IcrnResponse icrnResponse = null;
		try
		{
			icrnResponse = JaxbUtil.unmarshall(ICRN_RESPONSE_FILE_NAME, IcrnResponse.class);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Error unmarshalling data from response");
		}

		if(icrnResponse != null)
		{
		    for(IcrnJaxb icrn:	icrnResponse.getIcrnList())
			{
				if(toValidate.getCustomerReference().equals(icrn.getNumber()))
				{
					return true;
				}
			}
		}

		return false;
	}

	private boolean validate(String customerReference, BpayBiller biller)
	{

		logger.info("Validating CRN for customerReference {} ", customerReference);
		CheckDigitCalculatorFactory cdFactory = new CheckDigitCalculatorFactory();
		CrnValidatorFactory crnFactory = new CrnValidatorFactory(cdFactory.getCheckDigitCalculator(),
			cdFactory.getCheckDigitRuleFactory());

		CustomerReferenceNumberValidationParameters params = new CustomerReferenceNumberValidationParameters();

		params.setCheckDigitRuleName(biller.getCrnCheckDigitRoutine());
		params.setFixedDigitsMask(biller.getCrnFixedDigitsMask().replaceAll("'", ""));

		Boolean[] convertedLengths = new ArrayList <Boolean>(VALID_LENGTHS).toArray(new Boolean[] {});
		String crnLength = biller.getCrnValidLengths();

		for (int i = 0; i < crnLength.length(); i++)
		{
			if (crnLength.charAt(i) == identifier)
			{
				convertedLengths[i] = true;
			}
		}
		params.setValidLengths(new CustomerReferenceNumberValidLengths(Arrays.asList(convertedLengths)));

		try
		{
			CrnValidator validator = crnFactory.getCrnValidator();
			validator.validate(customerReference, params);
			return true;
		}
		catch (CrnValidationException e)
		{
			logger.debug("Validation failed {} ", (e.getDetailedMessage() != null ? e.getDetailedMessage() : e.getMessage()));
			return false;
		}
		
		//Added a new catch as we need to catch all the exceptions while validating CRN number.
		catch (Exception e)
		{
			logger.debug("Validation failed {} ", (e.getMessage()));
			return false;
		}
	}
}
