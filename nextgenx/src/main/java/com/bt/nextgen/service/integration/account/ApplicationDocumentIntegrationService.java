package com.bt.nextgen.service.integration.account;

import com.bt.nextgen.service.ServiceErrors;

import java.util.List;

public interface ApplicationDocumentIntegrationService
{
	List<ApplicationDocumentDetail> loadApplicationDocuments(List<String> accountNumbers, ServiceErrors serviceErrors);
	List<ApplicationDocumentDetail> loadApplicationDocumentsDirect(List<String> cisKeys, ServiceErrors serviceErrors);
}
