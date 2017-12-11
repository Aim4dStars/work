package com.bt.nextgen.core.web.controller;

import com.bt.nextgen.addressbook.PayeeValidator;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.service.MessageService;
import com.bt.nextgen.core.service.MessageServiceImpl;
import com.bt.nextgen.core.web.model.AjaxResponse;
import com.bt.nextgen.core.web.validator.FieldValidator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ValidationController
{
	private static final Logger logger = LoggerFactory.getLogger(ValidationController.class);
	private static final String FIELD_BSB = "bsb";
	private static final String FIELD_BILLER_CODE = "billerCode";
	@Autowired
	private FieldValidator fieldValidator;

	@Autowired
	private PayeeValidator payeeValidator;
	
	@Autowired
	private CmsService cmsService;

	private MessageService messageService = new MessageServiceImpl();

	@RequestMapping(value = "/secure/api/validateField", method = RequestMethod.GET)
	public @ResponseBody AjaxResponse validate(final @RequestParam("conversationId") String conversationId,
		final @RequestParam("fieldId") String fieldId, @RequestParam("data") String fieldValue) throws Exception
	{
		String errorCode = null;
		if (conversationId.equalsIgnoreCase(FIELD_BSB))
		{
			errorCode = payeeValidator.validateBsb(fieldValue, null);
		}
		else if (conversationId.equalsIgnoreCase(FIELD_BILLER_CODE))
		{
			errorCode = payeeValidator.validateBillerCode(fieldValue, null);
		}
		if (StringUtils.isNotBlank(errorCode))
		{
			//return new AjaxResponse(false, messageService.lookup(errorCode));
			return new AjaxResponse(false, cmsService.getContent(errorCode));
		}
		return new AjaxResponse();


		//		Class<?> clazz = Class.forName(conversationId);
		//		List<String> errors = fieldValidator.validateField(clazz, fieldId, fieldValue);
		//		if (errors.isEmpty())
		//		{
		//			return new AjaxResponse();
		//		}
		//		logger.info("errors.get(0)=" + errors.get(0));
		//		logger.info("messageService.lookup: " + messageService.lookup(errors.get(0)));
		//		return new AjaxResponse(false, messageService.lookup(errors.get(0)));
	}


}