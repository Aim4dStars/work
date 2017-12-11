package com.bt.nextgen.service.avaloq.basil;

import com.btfin.panorama.core.security.avaloq.AvaloqBankingAuthorityService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.UserCacheService;
import com.bt.nextgen.service.avaloq.insurance.service.InsuranceResponseBuilder;
import com.btfin.panorama.core.concurrent.AbstractConcurrentComplete;
import com.btfin.panorama.core.concurrent.Concurrent;
import com.btfin.panorama.core.concurrent.ConcurrentCallable;
import com.btfin.panorama.core.concurrent.ConcurrentComplete;
import com.btfin.panorama.core.concurrent.ConcurrentResult;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagereply.v1_0.SearchImagesResponseMsgType;
import ns.btfin_com.sharedservices.bpm.image.imageservice.imagerequest.v1_0.SearchImagesRequestMsgType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Integration service for getting Basil document details
 * Created by M035995 on 26/09/2016.
 */
@Service
public class BasilIntegrationServiceImpl implements BasilIntegrationService {

    private static final String BASIL_KEY = "basil";

    @Autowired
    private WebServiceProvider provider;

    @Autowired
    private UserProfileService userProfileService;

    @Resource(name = "userDetailsService")
    private AvaloqBankingAuthorityService userSamlService;

    @Autowired
    public UserCacheService userCacheService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(key = "{#root.target.getActiveProfileCacheKey(), #root.target.getSortedList(#policyNumberList), #root.target.getSortedList(#portfolioNumberList) }", value = "com.bt.nextgen.service.avaloq.basil.InsuranceDocument")
    public List<ImageDetails> getInsuranceDocuments(List<String> policyNumberList, Set<String> portfolioNumberList, ServiceErrors serviceErrors) {
        final List<ImageDetails> imageResponseList = new ArrayList<>();
        final List<ConcurrentCallable<?>> concurrentCallableList = new ArrayList<>();
        for (String policyNumber : policyNumberList) {
            concurrentCallableList.add(loadPolicyDocuments(DocumentProperty.SPOLICYID.getCode(), policyNumber,
                    DocumentType.DISHONOUR_LETTER.getCode(), DocumentType.DISHONOUR_LETTER_SECOND.getCode()));
            concurrentCallableList.add(loadPolicyDocuments(DocumentProperty.SPOLICYID.getCode(), policyNumber,
                    DocumentType.LAPSED_LETTER.getCode(), DocumentType.MANUAL_DISHONOUR_LETTER.getCode(),
                    DocumentType.REINSTATEMENT_LETTER.getCode(), DocumentType.CANCELLED_LETTER.getCode()));
        }
        for (String portfolioNumber : portfolioNumberList) {
            concurrentCallableList.add(loadPolicyDocuments(DocumentProperty.SPORTFOLIONUMBER.getCode(), portfolioNumber,
                    DocumentType.WELCOME_LETTER.getCode(), DocumentType.RENEWAL_LETTER.getCode()));
        }
        if (CollectionUtils.isNotEmpty(concurrentCallableList)) {
            final ConcurrentCallable[] concurrentCallableArray = new ConcurrentCallable[concurrentCallableList.size()];
            Concurrent.when(3, concurrentCallableList.toArray(concurrentCallableArray))
                    .done(processResults(imageResponseList, serviceErrors)).execute();
        }
        return imageResponseList;


    }

    private ConcurrentCallable<SearchImagesResponseMsgType> loadPolicyDocuments(final String requestKey, final String policyNumber, final String... documentTypes) {
        return new ConcurrentCallable<SearchImagesResponseMsgType>() {
            @Override
            public SearchImagesResponseMsgType call() {
                final SearchImagesRequestMsgType searchImagesRequestMsgType = new BasilRequestBuilder().getBasilRequest
                        (requestKey, policyNumber, userProfileService.getGcmId(), documentTypes);
                return (SearchImagesResponseMsgType) provider.sendWebServiceWithSecurityHeader(userSamlService.getSamlToken(),
                        BASIL_KEY, searchImagesRequestMsgType);
            }
        };
    }

    private ConcurrentComplete processResults(final List<ImageDetails> imageResponseList, final ServiceErrors serviceErrors) {
        return new AbstractConcurrentComplete() {
            @Override
            public void run() {
                final List<? extends ConcurrentResult<?>> resultList = this.getResults();
                for (ConcurrentResult concurrentResult : resultList) {
                    final SearchImagesResponseMsgType responseMsgType = (SearchImagesResponseMsgType) concurrentResult.getResult();
                    imageResponseList.addAll(new InsuranceResponseBuilder().getImageDetails(responseMsgType, serviceErrors));
                }
            }
        };
    }

    /**
     * Return sorted, comma separated list
     *
     * @param inputList
     *
     * @return String  - sorted comma
     */

    public String getSortedList(List<String> inputList) {
        Collections.sort(inputList);
        String outputList = StringUtils.join(inputList, ",");
        return outputList;
    }


    public String getActiveProfileCacheKey() {
        return userCacheService.getActiveProfileCacheKey();
    }

}
