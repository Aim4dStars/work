package com.bt.nextgen.service.bassil;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import com.bt.nextgen.portfolio.web.model.StatementTypeErrorModel;

@Service
public class StatementServiceJsonFileImpl implements StatementService
{
	private String filename = "/json/basilResponse.json";

	@Override
	public List <ClientStatements> loadClientStatements(String accountId, StatementTypeErrorModel error) throws Exception
	{

		ObjectMapper mapper = new ObjectMapper();
		BasilResponse response = mapper.readValue(filename.getBytes(), BasilResponse.class);
		return response.getStatements();
	}

	private void loadResource()
	{
		//SETTINGS.WEB_SERVICE_STUB_BASE_DIR+"/json/basilResponse.json"
	}

	/*
	 * Json wrapper class 
	 */
	class BasilResponse
	{
		List <ClientStatements> statements;

		public List <ClientStatements> getStatements()
		{
			return statements;
		}

		public void setStatements(List <ClientStatements> statements)
		{
			this.statements = statements;
		}
	}
}
