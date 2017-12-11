package com.bt.nextgen.service.bassil;

import java.util.ArrayList;
import java.util.List;

import ns.btfin_com.sharedservices.bpm.image.imageservice.imagereply.v1_0.MatchingImageType;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagereply.v1_0.MatchingImagesType;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagereply.v1_0.SearchImagesErrorResponseType;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagereply.v1_0.SearchImagesResponseMsgType;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagerequest.v1_0.SearchImagesRequestMsgType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.portfolio.web.model.StatementTypeErrorModel;
import com.bt.nextgen.web.controller.cash.util.Attribute;

/**
 * Implements StatementService and BassilService
 * 
 *
 */
@Service
public class BassilServiceImpl implements BassilService, StatementService
{
	private static String key = "basil";

	private static final Logger logger = LoggerFactory.getLogger(BassilServiceImpl.class);

	@Autowired
	private WebServiceProvider provider;

	public SearchImagesResponseMsgType loadImages(String accountId)
	{
		logger.info("Start of Method : loadImages");
		SearchImagesRequestMsgType request = BasilUtil.makeSearchImageRequest(accountId);
		SearchImagesResponseMsgType response = (SearchImagesResponseMsgType)provider.sendWebService(key, request);
		logger.info("End of Method : loadImages, response.status : {} ", response.getStatus());
		return response;
	}

	@Override
	public List <ClientStatements> loadClientStatements(String accountId, StatementTypeErrorModel error) throws Exception
	{
		logger.info("Start of Method : loadImages");

		SearchImagesRequestMsgType request = BasilUtil.makeSearchImageRequest(accountId);
		SearchImagesResponseMsgType response = (SearchImagesResponseMsgType)provider.sendWebService(key, request);
		if (response != null && Attribute.CLIENT_STATEMENT_SUCCESS_STATUS.equalsIgnoreCase(response.getStatus().value()))
		{
			return processSuccessResponse(response);
		}
		else if (response != null && Attribute.CLIENT_STATEMENT_ERROR_STATUS.equalsIgnoreCase(response.getStatus().value()))
		{
			error = processErrorResponse(response, error);
		}
		logger.info("End of Method : loadImages, response.status : {} ", response.getStatus());
		return null;
	}

	private List <ClientStatements> processSuccessResponse(SearchImagesResponseMsgType searchImagesResponseMsgType)
		throws Exception
	{
		List <ClientStatements> clientStatements = new ArrayList <ClientStatements>();

		MatchingImagesType mathImagesType = searchImagesResponseMsgType.getResponseDetails()
			.getSuccessResponse()
			.getMatchingImages();
		if (mathImagesType != null)
		{
			List <MatchingImageType> matchingImageTypes = searchImagesResponseMsgType.getResponseDetails()
				.getSuccessResponse()
				.getMatchingImages()
				.getMatchingImage();
			if (matchingImageTypes != null)
			{
				for (MatchingImageType matchingImageType : matchingImageTypes)
				{
					ClientStatementsJaxb clientStatement = new ClientStatementsJaxb(matchingImageType);
					clientStatements.add(clientStatement);
				}
			}
		}
		return clientStatements;
	}

	private StatementTypeErrorModel processErrorResponse(SearchImagesResponseMsgType searchImagesResponseMsgType,
		StatementTypeErrorModel statementTypeErrorModel) throws Exception
	{

		SearchImagesErrorResponseType searchImagesErrorResponseType = searchImagesResponseMsgType.getResponseDetails()
			.getErrorResponses()
			.getErrorResponse()
			.get(0);
		if (searchImagesErrorResponseType != null)
		{
			statementTypeErrorModel.setSubCode(searchImagesErrorResponseType.getSubCode());
			statementTypeErrorModel.setDescription(searchImagesErrorResponseType.getDescription());
			StringBuilder reasonBuilder = new StringBuilder();
			for (String reason : searchImagesErrorResponseType.getReason())
				reasonBuilder.append(reason);
			statementTypeErrorModel.setReason(reasonBuilder.toString());
		}
		return statementTypeErrorModel;
	}
}
