package com.bt.nextgen.web.conversion;

import com.bt.nextgen.core.domain.Money;
import com.bt.nextgen.payments.domain.Payment;
import com.bt.nextgen.payments.domain.PaymentRepeatsEnd;
import com.bt.nextgen.payments.web.model.ConfirmDepositConversation;
import com.bt.nextgen.service.cash.request.AccountInstruction;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DepositConversationToPaymentRequest implements Converter<ConfirmDepositConversation, Payment>
{
	private static final Logger logger = LoggerFactory.getLogger(DepositConversationToPaymentRequest.class);

	@Override
	public Payment convert(ConfirmDepositConversation source)
	{
		Payment depositRequest = new Payment();
		//Creditor Account Details
		AccountInstruction toAccountInstruction = new AccountInstruction();
		toAccountInstruction.setReference(source.getToAccount());
		toAccountInstruction.setCode(source.getToBsb());
		toAccountInstruction.setName(source.getToName());
		toAccountInstruction.setNarrative("Making a deposit"); //Putting hard code message
		
		//Debitor Account Details
		AccountInstruction fromAccountInstruction = new AccountInstruction();
		fromAccountInstruction.setReference(source.getPayReference());
		fromAccountInstruction.setCode(source.getPayCode());
		fromAccountInstruction.setName(source.getPayName());
		fromAccountInstruction.setNarrative(source.getDescription());
		
		depositRequest.setToAccount(toAccountInstruction);
		depositRequest.setFromAccount(fromAccountInstruction);
		//Payment Schedule details
		SimpleDateFormat parseDate = new SimpleDateFormat("dd MMM yyyy");
		/*Date schDate = new Date();
		try
		{
			schDate = parseDate.parse(source.getConversation().getDate());
		}
		catch (ParseException e)
		{
			logger.error(e.getLocalizedMessage());
		}*/

		//		PaymentDate depositSchedule = new PaymentDate();
		//		PaymentRecurringDate depositRecurringDate = new PaymentRecurringDate();
		//
		//		depositSchedule.setPaymentEffectiveDate(schDate);
		//		depositRecurringDate.setPaymentStartDate(schDate);
		//		depositRecurringDate.setPaymentFrequency(source.getConversation().getFrequency());
		//		depositSchedule.setPaymentRecurringDate(depositRecurringDate);
		//		depositInstruction.setPaymentDate(depositSchedule);

		//Other Payment Instruction Details
		//depositRequest.setTodayDate(new DateTime());
		depositRequest.setAmount(new Money(source.getAmount().toString().replaceAll("\\$|,", "")));
		depositRequest.setDescription(source.getDescription());
		if(source.isRecurring()){
			depositRequest.setRecurring(true);
			//depositRequest.setFrequency(source.getFrequency());
			depositRequest.setFrequency(source.getPaymentFrequency());
			depositRequest.setPaymentEndDate(source.getPaymentEndDate());
				if(source.getRepeatEnds().equals(PaymentRepeatsEnd.REPEAT_NUMBER.name())){
					depositRequest.setPaymentMaxCount(source.getPaymentMaxCount());
				}
		}
		
			try{
				Date date = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).parse(source.getConversation().getDate());
				depositRequest.setTodayDate(new DateTime(date));
			}catch (ParseException e) {
				logger.info("Failed to parse payment date");	
			}
		
		return depositRequest;
	}
}
