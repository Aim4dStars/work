package com.bt.nextgen.service.avaloq.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.search.PersonResponse;
import com.bt.nextgen.service.integration.search.PersonSearchIntegrationService;
import com.bt.nextgen.service.integration.search.PersonSearchRequest;
import com.bt.nextgen.service.integration.search.PersonSearchResult;

/**
 * This service is used for lookup of any person existing in avaloq.
 */

@Service
public class AvaloqPersonSearchIntegrationServiceImpl implements PersonSearchIntegrationService
{
	private static final Logger logger = LoggerFactory.getLogger(AvaloqPersonSearchIntegrationServiceImpl.class);
	
	@Autowired
	private AvaloqExecute avaloqExecute;
	
	@Autowired
	private StaticIntegrationService staticCodes;
	
	@Override
	public List<PersonResponse> searchUser(PersonSearchRequest request, String intlId ,ServiceErrors serviceErrors)
	{
		if (StringUtils.isNotEmpty(intlId))
		{
			logger.info("Loading static codes for person intlId {}", intlId);
			Collection <Code> codes = staticCodes.loadCodes(CodeCategory.PERSON_TYPE, serviceErrors);
			for (Code code : codes)
			{
				if (intlId.equalsIgnoreCase(code.getIntlId()))
				{
					request.setPersonTypeId(code.getCodeId());
					break;
				}
			}
		}

		return searchUser(request, serviceErrors);
	}
	
	/**
	 * This method is used for searching person inside avaloq. Inputs required for this service are the SearchToken, Role and PersonType.
	 * This service uses a specific avaloq operation - SRCH_REQ, for searching the matched persons 
	 * @param PersonSearchRequest
	 * @return List<PersonResponse>
	 */
	@Override
	public List<PersonResponse> searchUser(PersonSearchRequest request, ServiceErrors serviceErrors)
	{
		try
		{
			PersonSearchResult response = avaloqExecute.executeSearchOperationRequest(request,
				PersonSearchResultImpl.class,
				serviceErrors);
			return response.getPersonResponse();
		}
		catch (Exception e)
		{
			logger.error("Exception in Person Search ", e);
			return new ArrayList <>();
		}
	}

}
