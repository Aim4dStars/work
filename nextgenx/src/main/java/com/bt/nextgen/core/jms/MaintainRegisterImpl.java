package com.bt.nextgen.core.jms;

import com.bt.nextgen.core.repository.MaintainRegister;
import com.bt.nextgen.core.repository.PartialInvalidationRequestRegisterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bt.nextgen.core.repository.RequestRegisterRepository;

@Component("maintainRegister")
public class MaintainRegisterImpl implements MaintainRegister 
{

	@Autowired
	private RequestRegisterRepository requestRegister;

	@Autowired
	private PartialInvalidationRequestRegisterRepository partialInvalidationRequestRegisterRepository;

	@Override
	public void manage(String requestType, String eventType)
	{
		requestRegister.updateRequestEntry(requestType, eventType);
	}

	@Override
	public void managePartialInvalidation(String requestId) {
		partialInvalidationRequestRegisterRepository.removeEntry(requestId);

	}

}
