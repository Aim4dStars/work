package com.bt.nextgen.service.integration;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.bt.nextgen.core.web.ApiFormatter;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagereply.v1_0.MatchingImageType;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagereply.v1_0.SearchImagesErrorResponseType;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagereply.v1_0.SearchImagesResponseMsgType;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagerequest.v1_0.SearchImagesRequestMsgType;
import ns.btfin_com.sharedservices.bpm.image.v1_0.DocImageIndexPropType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.clients.util.ClientUtil;
import com.bt.nextgen.core.exception.ParseException;
import com.bt.nextgen.core.exception.ResourceNotFoundException;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.portfolio.web.model.ClientStatementsInterface;
import com.bt.nextgen.portfolio.web.model.ClientStatementsModel;
import com.bt.nextgen.portfolio.web.model.StatementDetailModel;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.bt.nextgen.portfolio.web.model.StatementTypeErrorModel;
import com.bt.nextgen.portfolio.web.model.StatementTypeModel;
import com.bt.nextgen.service.ServiceError;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;



@Service
public class ImageServerStatementsService implements StatementsIntegrationService
{
	private static String key = "basil";

	private static final Logger logger = LoggerFactory.getLogger(ImageServerStatementsService.class);

	@Autowired
	private WebServiceProvider provider;
	@Resource(name = "userDetailsService")
	private BankingAuthorityService userSamlService;

	@Override
	public ClientStatementsInterface getStatements(String portfolioId, ServiceErrors serviceErrors)
	{
		logger.info("Getting statments for PortfolioId [{}]", portfolioId);
		ClientStatementsInterface clientStatementsModel = new ClientStatementsModel();
		try
		{
			SearchImagesRequestMsgType request = StatementsUtil.makeSearchImageRequest(portfolioId);		
			SearchImagesResponseMsgType response = (SearchImagesResponseMsgType)provider.sendWebServiceWithSecurityHeader(userSamlService.getSamlToken(), key, request);
			if(Attribute.CLIENT_STATEMENT_SUCCESS_STATUS.equalsIgnoreCase(response.getStatus().value()))
			{
				List<StatementTypeModel> statementTypeModelList = new ArrayList<StatementTypeModel>();;
				try
				{
					statementTypeModelList = processSuccessResponse(response);
				}
				catch (Exception e)
				{
					ServiceError error = new ServiceErrorImpl();
					error.setException(new ParseException(e));
					error.setReason("Unable to parse response from" + key);
					serviceErrors.addError(error);
				}
				//sort based in statement type.
				Map<String,List<StatementTypeModel>> sortedStatementMap = groupByStatementType(statementTypeModelList);
				clientStatementsModel.setStatementTypeModelList(sortedStatementMap.get(Attribute.STATEMENT));
				clientStatementsModel.setAccountConfirmationModelList(sortedStatementMap.get(Attribute.ACCOUNT_CONFIRMATIONS));
				clientStatementsModel.setAnnualStatementTypeModelList(sortedStatementMap.get(Attribute.ANNUAL_STMTS));
			}
			else if(Attribute.CLIENT_STATEMENT_ERROR_STATUS.equalsIgnoreCase(response.getStatus().value()))
			{
				StatementTypeErrorModel statementTypeErrorModel = new StatementTypeErrorModel();;
				try
				{
					statementTypeErrorModel = processErrorResponse(response);
				}
				catch (Exception e)
				{
					ServiceError error = new ServiceErrorImpl();
					error.setException(new ParseException(e));
					error.setReason("Error in parsing error code from" + key);
					serviceErrors.addError(error);
				}
				clientStatementsModel.setStatementTypeErrorModel(statementTypeErrorModel);
			}
		}
		catch (Exception e)
		{
			ServiceError error = new ServiceErrorImpl();
			error.setException(new ResourceNotFoundException(e));
			error.setReason("Unable to find requested document from " + key);
			serviceErrors.addError(error);
		}
		
		return clientStatementsModel;
	}
	
	/*-----------------------------------------------------------------------------------------------------------------
	 * Utility methods -
	 *-----------------------------------------------------------------------------------------------------------------*/
	
	private List<StatementTypeModel> processSuccessResponse(SearchImagesResponseMsgType searchImagesResponseMsgType) throws Exception{
		Map<String,StatementTypeModel> statementTypeModelMap = new HashMap<>();
		List<StatementTypeModel> statementTypeModelList = new ArrayList<StatementTypeModel>();
		List<MatchingImageType> matchingImageTypes = searchImagesResponseMsgType.getResponseDetails().getSuccessResponse().getMatchingImages().getMatchingImage();
		if(matchingImageTypes != null){
			processMatchingTypes(matchingImageTypes,statementTypeModelMap);
		}
		statementTypeModelList.addAll(statementTypeModelMap.values());
		return statementTypeModelList;
	}

	private StatementTypeErrorModel processErrorResponse(SearchImagesResponseMsgType searchImagesResponseMsgType) throws Exception{
		StatementTypeErrorModel statementTypeErrorModel = new StatementTypeErrorModel();
		SearchImagesErrorResponseType searchImagesErrorResponseType = searchImagesResponseMsgType.getResponseDetails().getErrorResponses().getErrorResponse().get(0);
		if(searchImagesErrorResponseType != null){
			statementTypeErrorModel.setSubCode(searchImagesErrorResponseType.getSubCode());
			statementTypeErrorModel.setDescription(searchImagesErrorResponseType.getDescription());
			StringBuilder reasonBuilder = new StringBuilder();
			for(String reason:searchImagesErrorResponseType.getReason())
				reasonBuilder.append(reason);
			statementTypeErrorModel.setReason(reasonBuilder.toString());
		}
		return statementTypeErrorModel;
	}
	
	private void processMatchingTypes(List<MatchingImageType> matchingImageTypes,Map<String,StatementTypeModel> statementTypeModelMap) throws Exception{
		for(MatchingImageType matchingImageType:matchingImageTypes){
			String documentId = matchingImageType.getDocumentURL();
			List<DocImageIndexPropType> docImageIndexPropTypes = matchingImageType.getDocumentIndexProperties().getDocumentIndexProperty();
			if(docImageIndexPropTypes != null){
				String documentType = null;
				Date effectiveDate = null;
				for(DocImageIndexPropType docImageIndexPropType:docImageIndexPropTypes){
					String documentIndexPropertyName = docImageIndexPropType.getDocumentIndexPropertyName();
					if(Attribute.DOCUMENT_TYPE.equalsIgnoreCase(documentIndexPropertyName)){
						documentType = docImageIndexPropType.getDocumentIndexPropertyValues().getDocumentIndexStringPropertyValue().get(0);
					}
					if(Attribute.EFFECTIVE_DATE.equalsIgnoreCase(documentIndexPropertyName)){
						effectiveDate = IntegrationServiceUtil.toDate(docImageIndexPropType.getDocumentIndexPropertyValues().getDocumentIndexDatePropertyValue().get(0).getDocumentIndexDateValue());
					}
				}
				ClientUtil.convertClientStatementDomainToModel(documentType,documentId,effectiveDate,statementTypeModelMap);
				
			}
		}
	}
	
	private void sortByEffectiveDateStatementDetailModels(List<StatementDetailModel> statementDetailModels){
		Collections.sort(statementDetailModels,new Comparator<StatementDetailModel>() {
			@Override
			public int compare(StatementDetailModel statementDetailModel1,
					StatementDetailModel statementDetailModel2) {
				return ApiFormatter.parseDate(statementDetailModel1.getPeriodToDate()).compareTo(ApiFormatter.parseDate(statementDetailModel2.getPeriodToDate()));
			}
		});
	}
	
	private Map<String,List<StatementTypeModel>> groupByStatementType(List<StatementTypeModel> statementTypeModelList){
		String[] statementPopulateSequence =
		{
			Attribute.QRTLY_STMTS, Attribute.QRTLY_PAYG_STMTS, Attribute.ANNUAL_STMTS, Attribute.ACCOUNT_CONFIRMATIONS
		};
		for(StatementTypeModel statementTypeModel:statementTypeModelList)
		{
			if(Attribute.QRTLY_STMTS.equals(statementTypeModel.getStatementType()) || Attribute.QRTLY_PAYG_STMTS.equals(statementTypeModel.getStatementType())|| Attribute.ANNUAL_STMTS.equals(statementTypeModel.getStatementType())){
				sortByEffectiveDateStatementDetailModels(statementTypeModel.getStatementDetailList());
			}/*else if(Attribute.ACCOUNT_CONFIRMATIONS.equals(statementTypeModel.getStatementType())){
				sortAccountConfirmationStatements(statementTypeModel.getStatementDetailList());
			}*/
		}
		List<StatementTypeModel> sortedStatementTypeModelList = new ArrayList<>();
		List<StatementTypeModel> accountConfirmationStatementTypeModelList = new ArrayList<>();
		List <StatementTypeModel> annualStatementTypeModelList = new ArrayList <>();
		Map<String,List<StatementTypeModel>> sortedStatementModelMap = new HashMap<>();
		Multimap<String, StatementTypeModel> statementTypeModelMultiMap = ArrayListMultimap.create();
		for(StatementTypeModel statementTypeModel:statementTypeModelList)
			statementTypeModelMultiMap.put(statementTypeModel.getStatementType(),statementTypeModel);
		for(String status:statementPopulateSequence)
    	{
    		if(statementTypeModelMultiMap.get(status) != null){
    			if(Attribute.ACCOUNT_CONFIRMATIONS.equals(status)){
    				accountConfirmationStatementTypeModelList.addAll(statementTypeModelMultiMap.get(status));
				}
				else if (Attribute.ANNUAL_STMTS.equals(status))
				{
					annualStatementTypeModelList.addAll(statementTypeModelMultiMap.get(status));
    			}else{
    				sortedStatementTypeModelList.addAll(statementTypeModelMultiMap.get(status));
    			}
    		}
    	}
		sortedStatementModelMap.put(Attribute.ACCOUNT_CONFIRMATIONS, accountConfirmationStatementTypeModelList);
		sortedStatementModelMap.put(Attribute.STATEMENT, sortedStatementTypeModelList);
		sortedStatementModelMap.put(Attribute.ANNUAL_STMTS, annualStatementTypeModelList);
		return sortedStatementModelMap;
	}

}
