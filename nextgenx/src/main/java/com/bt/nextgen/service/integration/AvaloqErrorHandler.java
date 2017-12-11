package com.bt.nextgen.service.integration;

import com.bt.nextgen.service.ServiceErrors;
import com.btfin.abs.err.v1_0.ErrList;

public interface AvaloqErrorHandler
{
	ServiceErrors handleErrors(ErrList ErrorList, ServiceErrors serviceErrors);
}
