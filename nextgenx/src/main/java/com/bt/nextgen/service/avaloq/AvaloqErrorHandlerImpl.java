package com.bt.nextgen.service.avaloq;

import com.bt.nextgen.service.ServiceError;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.abs.err.v1_0.Err;
import com.btfin.abs.err.v1_0.ErrList;

public class AvaloqErrorHandlerImpl implements com.bt.nextgen.service.integration.AvaloqErrorHandler
{
	@Override
	public ServiceErrors handleErrors(ErrList ErrorList, ServiceErrors serviceErrors)
	{
		for (Err error : ErrorList.getErr())
		{
			if (error.getType().value() == "ovr")
			{

				OverridableServiceErrorImpl ovrError = (OverridableServiceErrorImpl)addErrorToList(new OverridableServiceErrorImpl(),
					error);
				//serviceErrors.addError(ovrError);

			}
			/*else
			{
				ServiceError UIerror = (ServiceErrorImpl)addErrorToList(new ServiceErrorImpl(), error);
				serviceErrors.addError(UIerror);
			}
			*/
		}
		return serviceErrors;
	}

	public static ServiceError addErrorToList(ServiceError serviceError, Err error)
	{

		serviceError.setId(error.getId());
		serviceError.setMessage(error.getErrMsg());
		serviceError.setType(error.getType().value());

		return serviceError;

	}

}
