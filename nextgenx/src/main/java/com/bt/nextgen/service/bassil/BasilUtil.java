package com.bt.nextgen.service.bassil;

import java.util.List;

import ns.btfin_com.sharedservices.bpm.image.imageservice.imagerequest.v1_0.FilterDocImageIndexPropsType;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagerequest.v1_0.FilterIndexPropsType;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagerequest.v1_0.ImageRequestContextType;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagerequest.v1_0.ImageSearchFilterType;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagerequest.v1_0.InvertedIndexPropType;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagerequest.v1_0.KeyIndexPropType;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagerequest.v1_0.ObjectFactory;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagerequest.v1_0.SearchDocPropsType;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagerequest.v1_0.SearchImagesRequestMsgType;
import ns.btfin_com.sharedservices.bpm.image.v1_0.DocImageIndexPropType;
import ns.btfin_com.sharedservices.bpm.image.v1_0.DocImageIndexPropValuesType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bt.nextgen.core.util.Properties;

public class BasilUtil {

	private static final Logger logger = LoggerFactory
			.getLogger(BasilUtil.class);

	public static SearchImagesRequestMsgType makeSearchImageRequest(
			String accountId) {
		ObjectFactory of = new ObjectFactory();
		SearchImagesRequestMsgType request = of
				.createSearchImagesRequestMsgType();
		ImageRequestContextType imageRequestContextType = new ImageRequestContextType();
		imageRequestContextType.setVersion("1_0");
		imageRequestContextType.setResponseVersion("1_0");
		// TODO:Should system id, as of now we will have nextgenonline
		String submitter = Properties.get("basil.submitter");
		imageRequestContextType.setSubmitter(submitter);

		// TODO:Id value from EsbHeaderAdderInterceptor has to be supplied here.
		// This will be taken care during actual integration stage.
		imageRequestContextType.setTrackingID("550e8400-e29b-41d4-a716-446655440000");
		request.setContext(imageRequestContextType);
		// request.setMaximumResults("1");
		DocImageIndexPropValuesType docImageIndexPropValuesType = new DocImageIndexPropValuesType();
		List<String> investorNumberList = docImageIndexPropValuesType
				.getDocumentIndexStringPropertyValue();
		investorNumberList.add(accountId);

		InvertedIndexPropType invertedIndexPropType = of
				.createInvertedIndexPropType();
		invertedIndexPropType
				.setDocumentIndexPropertyName(BasilIndexKeyValueEnum.InvestorNumber.toString());
		invertedIndexPropType
				.setDocumentIndexPropertyValues(docImageIndexPropValuesType);

		KeyIndexPropType keyIndexPropType = of.createKeyIndexPropType();
		keyIndexPropType.setInvertedKeyIndexProperty(invertedIndexPropType);
		SearchDocPropsType searchDocPropsType = new SearchDocPropsType();
		searchDocPropsType.setKeyIndexProperty(keyIndexPropType);

		FilterDocImageIndexPropsType filterDocImageIndexPropsType = new FilterDocImageIndexPropsType();
		List<DocImageIndexPropType> documentIndexPropertyList = filterDocImageIndexPropsType
				.getDocumentIndexProperty();

		// Setting BusinessLine
		DocImageIndexPropType docImageIndexPropTypeBl = new DocImageIndexPropType();
		docImageIndexPropTypeBl
				.setDocumentIndexPropertyName(BasilIndexKeyValueEnum.BusinessLine.toString());
		DocImageIndexPropValuesType documentIndexPropertyValuesBl = new DocImageIndexPropValuesType();
		List<String> blList = documentIndexPropertyValuesBl
				.getDocumentIndexStringPropertyValue();
		blList.add(BasilIndexKeyValueEnum.BusinessLine.getIndexValue());
		docImageIndexPropTypeBl
				.setDocumentIndexPropertyValues(documentIndexPropertyValuesBl);


		//REMOVED TrustType as per the instruction given by BA and Image Server Team
		// Setting Trust Type
//		DocImageIndexPropType docImageIndexPropTypeTt = new DocImageIndexPropType();
//		docImageIndexPropTypeTt
//				.setDocumentIndexPropertyName(BasilIndexKeyValueEnum.TrustType.toString());
//		DocImageIndexPropValuesType documentIndexPropertyValuesTt = new DocImageIndexPropValuesType();
//		List<String> trustList = documentIndexPropertyValuesTt
//				.getDocumentIndexStringPropertyValue();
//		trustList.add(BasilIndexKeyValueEnum.TrustType.getIndexValue());
//		docImageIndexPropTypeTt
//				.setDocumentIndexPropertyValues(documentIndexPropertyValuesTt);

		documentIndexPropertyList.add(docImageIndexPropTypeBl);
//		documentIndexPropertyList.add(docImageIndexPropTypeTt);
		FilterIndexPropsType filterIndexPropsType = new FilterIndexPropsType();
		filterIndexPropsType
				.setDocumentIndexProperties(filterDocImageIndexPropsType);

		searchDocPropsType.setFilterDocumentProperties(filterIndexPropsType);

		ImageSearchFilterType imageSearchFilterType = of
				.createImageSearchFilterType();
		imageSearchFilterType.setDocumentProperties(searchDocPropsType);
		request.setSearchFilter(imageSearchFilterType);

		return request;
	}
}
