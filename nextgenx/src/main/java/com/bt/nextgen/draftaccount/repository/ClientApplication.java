package com.bt.nextgen.draftaccount.repository;


import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.bt.nextgen.api.draftaccount.LoggingConstants;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDtoMapImpl;
import com.bt.nextgen.api.draftaccount.model.form.ClientApplicationFormFactory;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.DirectClientApplicationFormData;
import com.bt.nextgen.api.draftaccount.schemas.v1.OnboardingApplicationFormData;
import com.bt.nextgen.config.ApplicationContextProvider;
import com.bt.nextgen.core.repository.OnBoardingApplication;

@Entity
@Table(name = "CLIENT_APPLICATION")
public class ClientApplication implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientApplication.class);
    private static final String PREFIX = LoggingConstants.ONBOARDING + LoggingConstants.ONBOARDING_DELIMITER;

    @Id()
    @Column(name = "CLIENT_APPLICATION_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CLIENT_APPLICATION_SEQ")
    @SequenceGenerator(name = "CLIENT_APPLICATION_SEQ", sequenceName = "CLIENT_APPLICATION_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "ADVISER_POSITION_ID")
    private String adviserPositionId;

    @Column(name = "LAST_MODIFIED_ID")
    private String lastModifiedId;

    @Column(name = "LAST_MODIFIED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedAt;

    @Column(name = "FORM_DATA")
    private String formData;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    // TODO: use @Convert(converter=ClientApplicationStatusConverter.class) when we are using >j2ee 6.0 and >JPA 2.0
    // ClientApplicationStatusConverter implements javax.persistence.AttributeConverter<ClientApplicationStatus, String> {
    // There are two methods:
    // public String convertToDatabaseColumn(ClientApplicationStatus attribute){...}
    // public YourEnum convertToEntityAttribute(String dbString) {...}
    private ClientApplicationStatus status = ClientApplicationStatus.draft;

    @Column(name = "PRODUCT_ID")
    private String productId;

    @ManyToOne(optional=true, cascade = CascadeType.ALL)
    @JoinColumn(name="ONBOARDING_APPLICATION_ID", referencedColumnName = "id")
    private OnBoardingApplication onboardingApplication;

    @Transient
    private transient ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();

    public DateTime getLastModifiedAt() {
        if (lastModifiedAt != null) {
          return new DateTime(lastModifiedAt);
        } else {
            return null;
        }
    }

    /**
     * Used only by unit test to set a Mocked ApplicationContext when necessary.
     * @param ctx
     */
    public void setApplicationContext(ApplicationContext ctx){
        this.applicationContext = ctx;
    }

    public void setLastModifiedAt(DateTime lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt.toDate();
    }

    public String getAdviserPositionId() {
        return adviserPositionId;
    }

    public void setAdviserPositionId(String adviserId) {
        this.adviserPositionId = adviserId;
    }

    public Long getId() {
        return id;
    }

    public String getFormData() {
        return formatPersonType(formData);
    }

    public void setFormData(Object formDataObject) {
        if (formDataObject instanceof String) {
            this.formData = formatPersonType((String) formDataObject);
        } else {
            ObjectMapper mapper = (ObjectMapper) applicationContext.getBean("jsonObjectMapper");
            try {
                this.formData = formatPersonType(mapper.writeValueAsString(formDataObject));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //TODO: this is only required for inflight apps where persontype is still a string, remove when enough time has passed
    private String formatPersonType(String formData) {
        String formattedFormData = formData;
        try {
            if (null != formData && formData.length() > 0) {
                JSONObject formDataObj = new JSONObject(formData);
                JSONObject shareholder = formDataObj.getJSONObject("shareholderandmembers");
                JSONArray additional = (JSONArray) shareholder.get("additionalShareHoldersAndMembers");
                for (int i = 0; i < additional.length(); i++) {
                    JSONObject additionalShareholder = (JSONObject) additional.get(i);
                    Object personType = additionalShareholder.get("persontype");

                    if (personType instanceof String) {
                        LOGGER.info(PREFIX + "Inflight app found: person type is being transformed from a string to array.");
                        additionalShareholder.put("persontype", Arrays.asList(personType));
                    }
                }
                formattedFormData = formDataObj.toString();
            }
        } catch (JSONException e) {
            LOGGER.debug(PREFIX + "No person types stored for additional shareholders and members in this application. {}", e.getMessage());
        }
        return formattedFormData;
    }

    public ClientApplicationStatus getStatus() {
        return status;
    }

    private void setStatus(ClientApplicationStatus status) {
        this.status = status;
    }

    @VisibleForTesting
    public void setClientApplicationsStatus(ClientApplicationStatus status) {
        this.status = status;
    }

    public String getLastModifiedId() {
        return lastModifiedId;
    }

    public void setLastModifiedId(String lastModifiedId) {
        this.lastModifiedId = lastModifiedId;
    }

    public OnBoardingApplication getOnboardingApplication() {
        return onboardingApplication;
    }

    public void setOnboardingApplication(OnBoardingApplication onboardingApplication) {
        this.onboardingApplication = onboardingApplication;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void markDeleted() {
        setStatus(ClientApplicationStatus.deleted);
    }

    public void markSubmitted() {
        setStatus(ClientApplicationStatus.processing);
    }

    public void updateDocUploadedStatus() {
        setStatus(ClientApplicationStatus.docuploaded);
    }

    public void markActive() {
        setStatus(ClientApplicationStatus.active);
    }

    public void assertCanBeModified() {
        if (!getStatus().equals(ClientApplicationStatus.draft)) {
            throw new IllegalStateException("Cannot update Client Application (id="+getId()+") in state '"+getStatus()+"'");
        }
    }

    public void assertCanBeModifiedOffline() {
        if (!getStatus().equals(ClientApplicationStatus.processing) || !getOnboardingApplication().isOffline()) {
            throw new IllegalStateException("Cannot update  Client Application (id="+getId()+") in state '"+getStatus()+"'");
        }
    }

    private Map<String, Object> getFormDataAsMap() throws IOException {
        ObjectMapper mapper = (ObjectMapper)applicationContext.getBean("jsonObjectMapper");
        return mapper.readValue(getFormData(), new TypeReference<Map<String, Object>>(){});
    }

    @SuppressWarnings("squid:S00112")
    public IClientApplicationForm getClientApplicationForm() {
        try {
            final Object formDataObject;
            final Map<String, Object> formDataMap = getFormDataAsMap();
            ClientApplicationDto dto = new ClientApplicationDtoMapImpl(formDataMap);//dto used to parse the Map only
            if (dto.isJsonSchemaSupported()) { //JSON schema supported -> use v1 forms impl
                ObjectMapper mapper = applicationContext.getBean("jsonObjectMapper", ObjectMapper.class);
                Class formDataClass = OnboardingApplicationFormData.class;
                if(dto.isDirectApplication()) {
                    formDataClass = DirectClientApplicationFormData.class;
                }
                formDataObject = mapper.readValue(getFormData(), formDataClass);
            } else {
                formDataObject = formDataMap;
            }
            return ClientApplicationFormFactory.getNewClientApplicationForm(formDataObject);
        } catch (IOException e) {
            //TODO: create a user exception to control this error case
            throw new RuntimeException(e);
        }
    }
}
