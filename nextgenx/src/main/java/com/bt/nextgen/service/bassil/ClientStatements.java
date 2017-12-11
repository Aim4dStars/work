package com.bt.nextgen.service.bassil;

import java.util.List;
import java.util.Map;

/**
 * Represents a document detail from the basil service. 
 */
public interface ClientStatements
{

	/**
	 * 
	 * @return DocumentKey in the basil service
	 */
	public String getDocumentID();

	/**
	 * 
	 * @return 
	 */
	public String getDocumentEntryDate();

	/**
	 * 
	 * @return document properties
	 */
	public Map <String, List <String>> getDocumentProperties();

	/**
	 * 
	 * @return basil document type attribute
	 */
	public String getDocumentType();

	/**
	 * 
	 * @param attributeName
	 * @return attribute values
	 */
	public List <String> getDocumentProperties(String attributeName);
}
