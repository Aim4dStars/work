package com.bt.nextgen.core.api.operation;

import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.dto.ServiceFilterByKeyDtoService;
import com.bt.nextgen.core.api.exception.ApiException;
import com.bt.nextgen.core.api.exception.ServiceException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.Dto;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.core.api.validation.ApiValidation;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */

@SuppressWarnings({"squid:S1166", "squid:S1193", "checkstyle:com.puppycrawl.tools.checkstyle.checks.coding.IllegalCatchCheck"})
public class ServiceFilterByKey<K,T extends Dto> implements ControllerOperation {

   private  String version;
   private  ServiceFilterByKeyDtoService<K ,T>service;
   private  String queryString;
   private  K key;
   private  List<ApiSearchCriteria> filterCriteria=new ArrayList<>();

    public ServiceFilterByKey(String version,ServiceFilterByKeyDtoService<K,T>service,String queryString, K key ,String filter )
    {
        this.version=version;
        this.service=service;
        this.queryString=queryString;
        this.key=key;
        this.filterCriteria=ApiSearchCriteria.parseQueryString(ApiVersion.CURRENT_VERSION,filter);

    }

    @Override
    public ApiResponse performOperation() {
        try
        {
            ServiceErrors serviceErrors = new FailFastErrorsImpl();
            List<T> resultList = service.getFilteredValue(key, filterCriteria, queryString, serviceErrors);
            ApiResponse result = new ApiResponse(version, new ResultListDto<T>(resultList));
            ApiValidation.postConditionDataNotNull(version, result);
            ApiValidation.postConditionNoServiceErrors(version, serviceErrors);
            return result;
        }
        catch (com.bt.nextgen.core.exception.ServiceException e)
        {
            throw new ServiceException(version, e.getServiceErrors(), e);
        }
        catch (RuntimeException e)
        {
            if (!(e instanceof ApiException))
            {
                throw new ApiException(version, e.getMessage(), e);
            }
            throw e;
        }
    }
}
