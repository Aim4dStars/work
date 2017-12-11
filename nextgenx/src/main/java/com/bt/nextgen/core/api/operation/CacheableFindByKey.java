package com.bt.nextgen.core.api.operation;

import com.bt.nextgen.core.api.dto.CacheableFindByKeyDtoService;
import com.bt.nextgen.core.api.exception.ApiException;
import com.bt.nextgen.core.api.exception.ServiceException;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.core.api.validation.ApiValidation;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;

/**
 * Delegates to the supplied dto service to execute the find by key using a
 * cache key. Will return appropriate 404 error the service returns null Will
 * return appropriate 304 error when the necessary inputs have not been supplied
 */
public class CacheableFindByKey<K, T extends KeyedDto<K>> implements ControllerOperation {
    private final K key;
    private final boolean clearCache;
    private final String version;
    private final CacheableFindByKeyDtoService<K, T> service;

    public CacheableFindByKey(String version, CacheableFindByKeyDtoService<K, T> service, K key, boolean clearCache) {
        this.key = key;
        this.clearCache = clearCache;
        this.version = version;
        this.service = service;
    }

    @SuppressWarnings({ "squid:S1166", "checkstyle:com.puppycrawl.tools.checkstyle.checks.coding.IllegalCatchCheck" })
    @Override
    public KeyedApiResponse<K> performOperation() {
        try {
            ApiValidation.preConditionCompleteKey(version, key);
            ServiceErrors serviceErrors = new FailFastErrorsImpl();
            KeyedApiResponse<K> result = new KeyedApiResponse<K>(version, key,
                    service.findFromCache(key, clearCache, serviceErrors));
            ApiValidation.postConditionDataNotNull(version, result);
            ApiValidation.postConditionNoServiceErrors(version, serviceErrors);
            return result;
        } catch (com.bt.nextgen.core.exception.ServiceException e) {
            throw new ServiceException(version, e.getServiceErrors(), e);
        } catch (ApiException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ApiException(version, e.getMessage(), e);
        }
    }
}
