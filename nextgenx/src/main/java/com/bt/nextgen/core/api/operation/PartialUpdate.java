package com.bt.nextgen.core.api.operation;

import com.bt.nextgen.core.api.dto.PartialUpdateDtoService;
import com.bt.nextgen.core.api.exception.ApiException;
import com.bt.nextgen.core.api.exception.ApiValidationException;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.core.api.validation.ApiValidation;
import com.bt.nextgen.core.api.validation.ErrorMapper;
import com.bt.nextgen.core.exception.ValidationException;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;

import java.util.Map;

/**
 * <pre>
 * Delegates to the supplied dto service to execute a partial update of an object. The partial updates will be encapsulated in a set of name/value pairs
 * responsibility of the extending dto service. 
 * Will return appropriate 404 error if the service returns null or an incomplete key or the partial updates cannot be applied to the target object, e.g. the field name provided does not exist.
 * Will return appropriate 304 error when the necessary inputs have not been supplied.
 * </pre>
 */
public class PartialUpdate<K, T extends KeyedDto<K>> implements ControllerOperation {
    private String version;
    private PartialUpdateDtoService<K, T> service;
    private K key;
    /**
     * The target class to perform the updates on.
     */
    private Class<T> targetClass;
    /**
     * The partial updates to apply to the T target object. The entries should be the target class field name and the value to
     * update it with.
     */
    private Map<String, ? extends Object> partialUpdates;
    private ErrorMapper mapper;

    public PartialUpdate(String version, PartialUpdateDtoService<K, T> service, K key,
            Map<String, ? extends Object> partialUpdates, Class<T> targetClass, ErrorMapper mapper) {
        this.version = version;
        this.service = service;
        this.key = key;
        this.partialUpdates = partialUpdates;
        this.targetClass = targetClass;
        this.mapper = mapper;
    }

    @Override
    public KeyedApiResponse<K> performOperation() {
        try {
            ApiValidation.preConditionCompleteKey(version, key);
            ApiValidation.preConditionFieldsExistOnTarget(version, partialUpdates.keySet(), targetClass);
            ServiceErrors serviceErrors = new FailFastErrorsImpl();
            KeyedApiResponse<K> result = new KeyedApiResponse<K>(version, key,
                    service.partialUpdate(key, partialUpdates, serviceErrors));
            ApiValidation.postConditionDataNotNull(version, result);
            ApiValidation.postConditionNoServiceErrors(version, serviceErrors);
            return result;
        } catch (ValidationException ex) {
            throw new ApiValidationException(version, mapper.map(ex.getErrors()), ex);
        } catch (RuntimeException e) {
            if (!(e instanceof ApiException)) {
                throw new ApiException(version, e.getMessage(), e);
            }
            throw e;
        }
    }
}
