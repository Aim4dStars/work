package com.bt.nextgen.service.integration.search;

import java.util.List;

import com.bt.nextgen.service.ServiceErrors;

public interface PersonSearchIntegrationService
{
	List<PersonResponse> searchUser(PersonSearchRequest request, ServiceErrors serviceErrors);
	List<PersonResponse> searchUser(PersonSearchRequest request,String intlId ,ServiceErrors serviceErrors);
}
