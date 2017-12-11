package com.bt.nextgen.core.api.operation;

import com.bt.nextgen.core.api.exception.BadRequestException;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Parsed API tier search parameter. Differs from  @see com.bt.nextgen.core.util.SearchParameter in that it supports more operations
 * than just equals. See @TODO (define a class for translating) for how to interface this class with the core search parameters.
 */
public class ApiSearchCriteria
{
	private SearchOperation operation;
	private OperationType operationType;
	private String value;
	private String property;

	public ApiSearchCriteria(String property, SearchOperation operation, String value, OperationType operationType)
	{
		super();
		this.property = property;
		this.operation = operation;
		this.value = value;
		this.operationType = operationType;
	}

    /**
     * Default operation type constructor - assumes a STRING type.
     * @param property
     * @param operation
     * @param value
     * @see com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType#STRING
     */
    public ApiSearchCriteria(String property, SearchOperation operation, String value) {
        this(property, operation, value, OperationType.STRING);
    }

	/**
	 * Default operation constructor - assumes an EQUALS operator.
	 * @param property
	 * @param value
	 * @see com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation#EQUALS
	 */
	public ApiSearchCriteria(String property, String value) {
		this(property, SearchOperation.EQUALS, value);
	}

	public SearchOperation getOperation()
	{
		return operation;
	}

	public OperationType getOperationType()
	{
		return operationType;
	}

	public String getValue()
	{
		return value;
	}

	public String getProperty()
	{
		return property;
	}

	public enum OperationType
	{
		DATE(DateTime.class, "date"), NUMBER(Number.class, "number"), STRING(String.class, "string"), BOOLEAN(Boolean.class, "boolean");

		private Class <? > instanceType;
		private String code;

		private OperationType(Class <? > instanceType, String code)
		{
			this.instanceType = instanceType;
			this.code = code;
		}

		public static OperationType forCode(String code)
		{
			for (OperationType op : values())
			{
				if (op.code.equals(code))
				{
					return op;
				}
			}
			throw new IllegalArgumentException("Unknown operation type " + code);
		}

		public Class<?> getInstance(){
			return instanceType;
		}
	}

	public enum SearchOperation
	{
		EQUALS("="),
		NEG_EQUALS("~="),
		GREATER_THAN(">"),
		NEG_GREATER_THAN("~>"),
		LESS_THAN("<"),
		NEG_LESS_THAN("~<"),
		STARTS_WITH("sw"),
		NEG_STARTS_WITH("~sw"),
		CONTAINS("c"),
		NEG_CONTAINS("~c"),
		LIST_CONTAINS("lc"),
		BETWEEN("bw");

		private String operator;

		private SearchOperation(String operator)
		{
			this.operator = operator;
		}

		public static SearchOperation forCode(String operator)
		{
			for (SearchOperation op : values())
			{
				if (op.operator.equals(operator))
				{
					return op;
				}
			}
			throw new IllegalArgumentException("Unknown operator " + operator);
		}
	}

	public static List <ApiSearchCriteria> parseQueryString(String apiVersion, final String queryString)
	{
		final List <ApiSearchCriteria> parsedCriteria = new ArrayList <>();

		if (queryString != null)
		{
			try
			{
				JSONArray criteriaArray = new JSONArray(queryString);

				for (int i = 0; i < criteriaArray.length(); i++)
				{
					JSONObject criteria = criteriaArray.getJSONObject(i);
					parsedCriteria.add(new ApiSearchCriteria(criteria.getString("prop"),
						SearchOperation.forCode(criteria.getString("op")),
						criteria.getString("val"),
						OperationType.forCode(criteria.getString("type"))));
				}
			}
			catch (JSONException | IllegalArgumentException e)
			{
				throw new BadRequestException(apiVersion, "Unable to parse query string " + queryString, e);
			}
		}
		return parsedCriteria;
	}

}
