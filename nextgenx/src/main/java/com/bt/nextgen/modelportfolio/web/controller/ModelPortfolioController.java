package com.bt.nextgen.modelportfolio.web.controller;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioUploadDto;
import com.bt.nextgen.api.modelportfolio.v2.service.ModelPortfolioUploadDtoService;
import com.bt.nextgen.api.modelportfolio.v2.validation.ModelPortfolioDtoErrorMapper;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.exception.ApiValidationException;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.operation.Submit;
import com.bt.nextgen.modelportfolio.util.ModelPortfolioUploadUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ModelPortfolioController
{
	private static final Logger logger = LoggerFactory.getLogger(ModelPortfolioController.class);

	@Autowired
	private ModelPortfolioUploadUtil modelPortfolioUploadUtil;

	@Autowired
	private ModelPortfolioUploadDtoService modelPortfolioUploadDtoService;

	@Autowired
	private ModelPortfolioDtoErrorMapper modelPortfolioUploadErrorMapper;

	@Autowired
	private JsonObjectMapper mapper;

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.POST, value = "/secure/page/models/{model-id}")
	@ResponseBody
	String uploadModel(@PathVariable(UriMappingConstants.MODEL_ID_URI_MAPPING) String modelId,
		@RequestPart("modelupload") MultipartFile file) throws Exception
	{
		try
		{
			ModelPortfolioUploadDto modelUpload = modelPortfolioUploadUtil.parseFile(modelId, file);

			new Submit <>(ApiVersion.CURRENT_VERSION,
				modelPortfolioUploadDtoService,
				modelPortfolioUploadErrorMapper,
				modelUpload).performOperation();
			return "<textarea data-type=\"application/json\" data-status=\"200\">{\"lastUpdatedTime\": \"" + new DateTime()
				+ "\"}</textarea>";
		}
		catch (BadRequestException e)
		{
			return "<textarea data-type=\"application/json\" data-status=\"500\">{\"message\": \"" + e.getMessage()
				+ "\"}</textarea>";
		}
		catch (ApiValidationException e)
		{
			String errorJson = mapper.writeValueAsString(e.getErrors());

			return "<textarea data-type=\"application/json\" data-status=\"500\">{\"message\": \"" + e.getMessage()
				+ "\", \"error\": {\"errors\": " + errorJson + "}}</textarea>";
		}
		catch (Exception e)
		{
			return "<textarea data-type=\"application/json\" data-status=\"500\">{\"message\": \"" + e.getMessage()
				+ "\"}</textarea>";
		}
	}
}
