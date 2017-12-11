package com.bt.nextgen.service.avaloq.insurance.service;

import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.basil.DocumentProperties;
import com.bt.nextgen.service.avaloq.basil.DocumentPropertiesImpl;
import com.bt.nextgen.service.avaloq.basil.DocumentProperty;
import com.bt.nextgen.service.avaloq.basil.ImageDetails;
import com.bt.nextgen.service.avaloq.basil.ImageDetailsImpl;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyStatusCode;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyTrackingImpl;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyType;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyUnderWritingNotesImpl;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyUnderwriting;
import com.bt.nextgen.service.avaloq.insurance.model.PolicyUnderwritingImpl;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyreply.v4_2.RetrieveUnderwritingByPolicyNumberResponseDetailType;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyreply.v4_2.RetrieveUnderwritingByPolicyNumberResponseMsgType;
import ns.btfin_com.product.insurance.lifeinsurance.policy.policyservice.policyreply.v4_2.RetrieveUnderwritingByPolicyNumberSuccessResponseType;
import ns.btfin_com.product.insurance.lifeinsuranceservice.policy.v4_2.RetrievePolicyBasicType;
import ns.btfin_com.product.insurance.lifeinsuranceservice.policy.v4_2.UnderwritingRequirementType;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagereply.v1_0.MatchingImageType;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagereply.v1_0.SearchImagesResponseDetailsType;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagereply.v1_0.SearchImagesResponseMsgType;
import ns.btfin_com.sharedservices.bpm.image.v1_0.DocImageIndexPropType;
import ns.btfin_com.sharedservices.bpm.image.v1_0.DocImageIndexPropsType;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public class InsuranceResponseBuilder {

    private static final String STATUS_ERROR = "Error";

    public PolicyUnderwriting getUnderwritingDetails(RetrieveUnderwritingByPolicyNumberResponseMsgType response,
                                                     ServiceErrors serviceErrors) {
        RetrieveUnderwritingByPolicyNumberResponseDetailType responseDetails = null;
        if (response != null && response.getResponseDetails() != null && isNotEmpty(response.getResponseDetails().getResponseDetail())) {
            responseDetails = response.getResponseDetails().getResponseDetail().get(0);
        }
        if (responseDetails == null || STATUS_ERROR.equals(response.getStatus().value())) {
            ServiceErrorImpl error = new ServiceErrorImpl();
            error.setReason(responseDetails != null ? responseDetails.getErrorResponse().getDescription() : "Error retrieving underwriting policy response");
            serviceErrors.addError(error);
            return new PolicyUnderwritingImpl();
        }
        else {
            PolicyUnderwritingImpl policyUnderwriting = new PolicyUnderwritingImpl();
            policyUnderwriting.setPolicyDetails(getPolicyDetails(responseDetails.getSuccessResponse()));
            policyUnderwriting.setUnderWritingNotes(getUnderWritingNotes(responseDetails.getSuccessResponse()));
            return policyUnderwriting;
        }
    }

    private List<PolicyTrackingImpl> getPolicyDetails
            (RetrieveUnderwritingByPolicyNumberSuccessResponseType successResponseType) {
        List<PolicyTrackingImpl> policyTrackings = new ArrayList<>();

        RetrievePolicyBasicType policyBasicType = successResponseType.getPolicyDetails();

        PolicyTrackingImpl policyTracking = new PolicyTrackingImpl();
        policyTracking.setPolicyNumber(policyBasicType.getPolicyNumber().getValue());
        policyTracking.setPolicyType(PolicyType.forValue(policyBasicType.getPolicyType().value()));
        policyTracking.setPolicyStatus(PolicyStatusCode.forStatus(policyBasicType.getPolicyStatus().value()));
        policyTrackings.add(policyTracking);
        return policyTrackings;
    }

    private List<PolicyUnderWritingNotesImpl> getUnderWritingNotes
            (RetrieveUnderwritingByPolicyNumberSuccessResponseType successResponseType) {
        List<PolicyUnderWritingNotesImpl> policyUnderWritingNotes = new ArrayList<>();
        List<UnderwritingRequirementType> underwritingNotes = successResponseType.getPolicyLifeDetail().get(0).
                getUnderwritingRequirement();
        if (CollectionUtils.isNotEmpty(underwritingNotes)) {
            for (UnderwritingRequirementType underwritingNote : underwritingNotes) {
                PolicyUnderWritingNotesImpl writingNote = new PolicyUnderWritingNotesImpl();
                writingNote.setDateRequested(convertToDateTime(underwritingNote.getUnderwritingRequestDate()));
                writingNote.setSignOffDate(convertToDateTime(underwritingNote.getUnderwritingSignoffDate()));
                writingNote.setCodeDescription(underwritingNote.getUnderwritingCodeDesc());
                if (underwritingNote.getUnderwritingDetails() != null) {
                    writingNote.setUnderwritingDetails(underwritingNote.getUnderwritingDetails().getValue());
                }
                policyUnderWritingNotes.add(writingNote);
            }
        }
        return policyUnderWritingNotes;
    }

    private DateTime convertToDateTime(XMLGregorianCalendar date) {
        if (date != null) {
            return new DateTime(date.toGregorianCalendar().getTime());
        }
        return null;
    }

    /**
     * Convert Basil response to domain model
     *
     * @param response
     * @param serviceErrors
     *
     * @return List<ImageDetails> - List of documents
     */
    public List<ImageDetails> getImageDetails(SearchImagesResponseMsgType response, ServiceErrors serviceErrors) {
        SearchImagesResponseDetailsType responseDetails = null;
        if (response != null) {
            responseDetails = response.getResponseDetails();
        }
        if (responseDetails == null || responseDetails.getErrorResponses() != null) {
            ServiceErrorImpl serviceError = new ServiceErrorImpl();
            serviceError.setReason("Error while fetching BASIL documents");
            serviceErrors.addError(serviceError);
            return new ArrayList<>();
        }
        else {
            List<ImageDetails> imageDetails = new ArrayList<>();
            if (responseDetails.getSuccessResponse().getMatchingImages() != null) {
                for (MatchingImageType imageType : responseDetails.getSuccessResponse().getMatchingImages().getMatchingImage()) {
                    ImageDetailsImpl imageDetail = new ImageDetailsImpl();
                    imageDetail.setDocumentURL(imageType.getDocumentURL());
                    imageDetail.setDocumentId(imageType.getDocumentID());
                    imageDetail.setMimeType(imageType.getDocumentMimeType());
                    imageDetail.setDocumentEntryDate(convertToDateTime(imageType.getDocumentEntryDate()));
                    imageDetail.setDocumentPropertiesList(getDocumentProperties(imageType.getDocumentIndexProperties()));
                    imageDetails.add(imageDetail);
                }
            }
            return imageDetails;
        }
    }

    private List<DocumentProperties> getDocumentProperties(DocImageIndexPropsType indexPropsType) {
        List<DocumentProperties> documentProperties = new ArrayList<>();
        for (DocImageIndexPropType indexPropType : indexPropsType.getDocumentIndexProperty()) {
            DocumentPropertiesImpl documentProperty = new DocumentPropertiesImpl();
            documentProperty.setDocumentPropertyName(DocumentProperty.findByCode(indexPropType.getDocumentIndexPropertyName()));
            if (CollectionUtils.isNotEmpty(indexPropType.getDocumentIndexPropertyValues().getDocumentIndexStringPropertyValue())) {
                documentProperty.setDocumentPropertyStringValue(indexPropType.getDocumentIndexPropertyValues().getDocumentIndexStringPropertyValue().get(0));
            }
            if (CollectionUtils.isNotEmpty(indexPropType.getDocumentIndexPropertyValues().getDocumentIndexDatePropertyValue())) {
                documentProperty.setDocumentPropertyDateValue(convertToDateTime(indexPropType.getDocumentIndexPropertyValues().
                        getDocumentIndexDatePropertyValue().get(0).getDocumentIndexDateValue()));
            }
            documentProperties.add(documentProperty);
        }
        Collections.sort(documentProperties, new BeanComparator("documentPropertyName"));
        return documentProperties;
    }
}
