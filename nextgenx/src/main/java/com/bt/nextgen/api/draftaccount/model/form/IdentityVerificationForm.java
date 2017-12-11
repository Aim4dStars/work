package com.bt.nextgen.api.draftaccount.model.form;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.bt.nextgen.config.ApplicationContextProvider;

import static java.util.Collections.emptyMap;

/**
 * @deprecated Should be using the v1 version of this class instead.
 */
@Deprecated
class IdentityVerificationForm implements IIdentityVerificationForm {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdentityVerificationForm.class);

    private final Map<String, Object> individualInvestorDetails;

    @JsonIgnore
    private final ApplicationContext appContext = ApplicationContextProvider.getApplicationContext();

    public IdentityVerificationForm(Map<String, Object> individualInvestorDetails) {
        this.individualInvestorDetails = individualInvestorDetails;
    }

    public boolean hasInternationalDocuments() {
        return getIdentityDocument().get("internationaldocuments") != null;
    }

    public IIdentityVerificationDocuments getInternationalDocuments() {
        return new IdentityVerificationDocuments((Map<String, Object>) getIdentityDocument().get("internationaldocuments"));
    }

    public boolean hasNonPhotoDocuments() {
        return getIdentityDocument().get("nonphotodocuments") != null;
    }

    public IIdentityVerificationDocuments getNonPhotoDocuments() {
        return new IdentityVerificationDocuments((Map<String, Object>) getIdentityDocument().get("nonphotodocuments"));
    }

    public boolean hasPhotoDocuments() {
        return getIdentityDocument().get("photodocuments") != null;
    }

    public IIdentityVerificationDocuments getPhotoDocuments() {
        return new IdentityVerificationDocuments((Map<String, Object>) getIdentityDocument().get("photodocuments"));
    }

    private Map<String, Object> getIdentityDocument() {
        Map<String, Object> identityDocument = emptyMap();
        final Object documents = individualInvestorDetails.get("identitydocument");
        if (documents != null) {
            try {
                ObjectMapper mapper = (ObjectMapper)appContext.getBean("jsonObjectMapper");
                identityDocument = mapper.convertValue(documents, Map.class);
            } catch (IllegalArgumentException e) {
                LOGGER.error("Error converting identity documents: ", e);
            }
        }
        return identityDocument;
    }
}
