package com.bt.nextgen.service.bassil;

import java.util.List;
import java.util.Map;

public class ClientStatementsJson implements ClientStatements
{

	private String json;

	public ClientStatementsJson()
	{}

	/**
	 * 
	 * @param json
	 */
	public ClientStatementsJson(String json)
	{
		this.json = json;
	}

	@Override
	public String getDocumentID()
	{
		//JSONObject criteria = criteriaArray.getJSONObject(i);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDocumentEntryDate()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map <String, List <String>> getDocumentProperties()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDocumentType()
	{
		return null;
	}

	@Override
	public List <String> getDocumentProperties(String attributeName)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
