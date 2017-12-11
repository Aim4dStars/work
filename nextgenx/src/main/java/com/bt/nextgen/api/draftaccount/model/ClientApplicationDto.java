package com.bt.nextgen.api.draftaccount.model;

import org.joda.time.DateTime;

import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.draftaccount.repository.ClientApplicationStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The ClientApplicationDto interface being serialized/deserialized
 * using custom  ClientApplicationDtoSerializer and ClientApplicationDtoDeserializer
 *
 * Created by m040398 on 18/07/2016.
 */
public interface ClientApplicationDto extends KeyedDto<ClientApplicationKey> {

    /**
     * This the default version of formData JSON before introducing the JSON schemas before any TODOs refactoring.
     */
    String FORM_DATA_DEFAULT_VERSION = "1.0.0";//TODO: start using this value when validating the JSON payload ?

    String getAdviserId();

    void setAdviserId(String adviserId);

    ClientApplicationKey getKey();

    void setKey(ClientApplicationKey key);

    Object getFormData();

    void setFormData(Object formData);

    /**
     * Specify if the JSON payload supports JSON Schema.
     * @return true if this dto supports JSON schema
     */
    public boolean isJsonSchemaSupported();

    public ClientApplicationStatus getStatus();

    public void setStatus(ClientApplicationStatus status);

    public String getLastModifiedByName();

    public void setLastModifiedByName(String lastModifiedByName);

    public String getReferenceNumber();

    public void setReferenceNumber(String referenceNumber);

    public String getAdviserName();

    public void setAdviserName(String adviserName);

    public DateTime getLastModified();

    public void setLastModified(DateTime lastModified);

    public String getProductName();

    public void setProductName(String productName);

    public String getProductId();

    public void setProductId(String productId);

    public void setOffline(boolean offline);

    public boolean isOffline();

    /**
     *
     * @return true if this dto is used by Direct
     */
    boolean isDirectApplication();

    @JsonIgnore
    public String getAccountType();

}
