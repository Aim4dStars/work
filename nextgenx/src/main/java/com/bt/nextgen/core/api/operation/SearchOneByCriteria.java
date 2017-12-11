package com.bt.nextgen.core.api.operation;

import com.bt.nextgen.core.api.dto.SearchOneByCriteriaDtoService;
import com.bt.nextgen.core.api.exception.ApiException;
import com.bt.nextgen.core.api.exception.ServiceException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.Dto;
import com.bt.nextgen.core.api.validation.ApiValidation;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Search by criteria that returns a single result.
 * <p>
 * <p/>
 * Suppressed Sonar checks:
 * <ul>
 * <li><a href="http://sonar.cloud.btfin-dev.com/coding_rules#q=catching">Catching 'Exception' is not allowed</a> - key
 * phrase from description is "almost never acceptable"</li>
 * <li><a href="http://sonar.cloud.btfin-dev.com/coding_rules#languages=java|q=Style - Constrained method converts checked exception to unchecked">Style - Constrained method converts checked exception to unchecked</a> - needed to convert to {@link ApiException}"</li>
 * </ul>
 */
@SuppressWarnings({"checkstyle:com.puppycrawl.tools.checkstyle.checks.coding.IllegalCatchCheck", "fb-contrib:EXS_EXCEPTION_SOFTENING_NO_CHECKED"})
public class SearchOneByCriteria<T extends Dto> implements ControllerOperation {
    /**
     * Version of the API.
     */
    private final String version;

    /**
     * Service to call to perform the search.
     */
    private final SearchOneByCriteriaDtoService<T> service;

    /**
     * List of search criteria.
     */
    private final List<ApiSearchCriteria> criteria;


    /**
     * Ctor.
     *
     * @param version     API version.
     * @param service     Service to call to perform the search.
     * @param queryString Criteria for the search.
     */
    public SearchOneByCriteria(String version, SearchOneByCriteriaDtoService<T> service, String queryString) {
        this(version, service, ApiSearchCriteria.parseQueryString(version, queryString));
    }

    /**
     * Ctor.
     *
     * @param version  API version.
     * @param service  Service to call to perform the search.
     * @param criteria List of criteria for the search.
     */
    public SearchOneByCriteria(String version, SearchOneByCriteriaDtoService<T> service, List<ApiSearchCriteria> criteria) {
        this.version = version;
        this.service = service;
        this.criteria = criteria;
    }

    /**
     * Ctor.
     *
     * @param version  API version.
     * @param service  Service to call to perform the search.
     * @param criteria Array of criteria for the search.
     */
    public SearchOneByCriteria(String version, SearchOneByCriteriaDtoService<T> service, ApiSearchCriteria... criteria) {
        this(version, service, asList(criteria));
    }

    /**
     * Search action.
     *
     * @return API response containing a single object.
     */
    public ApiResponse performOperation() {
        try {
            final ServiceErrors serviceErrors = new FailFastErrorsImpl();
            final T oneResult = service.search(criteria, serviceErrors);
            final ApiResponse result = new ApiResponse(version, oneResult);

            ApiValidation.postConditionDataNotNull(version, result);
            ApiValidation.postConditionNoServiceErrors(version, serviceErrors);

            return result;
        }
        catch (com.btfin.panorama.service.exception.ServiceException e) {
            throw new ServiceException(version, e.getServiceErrors(), e);
        }
        catch (ApiException e) {
            throw e;
        }
        catch (RuntimeException e) {
            throw new ApiException(version, e.getMessage(), e);
        }
    }
}
