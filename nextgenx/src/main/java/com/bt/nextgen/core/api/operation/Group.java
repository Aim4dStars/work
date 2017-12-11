package com.bt.nextgen.core.api.operation;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.Dto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.core.api.model.ResultMapDto;

/**
 * Decorates the another operation by providing "group by" functionality which turns
 * a result list into a result map keyed by the group by attribute
 *
 * example
 * 	[ {a: 1, b: 1}, {a: 2, b: 2}, {a: 3, b: 2}, {a: 4, b: 1}, {a: 5, b: 3}] group by b
 *  will result in
 *  [ { b:1 [{a: 1, b: 1}, {a: 4, b: 1}]}, { b:2 [{a: 2, b: 2}, {a: 3, b: 2}]}, { b:3 [{a: 5, b: 1}]}]
 */
public class Group <K, T extends KeyedDto <K>> extends ChainedControllerOperation
{
	public static final String GROUP_PARAMETER = "group-by";
	private String groupByProperty;
	private String version;

	public Group(String version, ControllerOperation chained, String groupBy)
	{
		super(chained);
		this.version = version;
		this.groupByProperty = groupBy;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ApiResponse performChainedOperation(ApiResponse chainedResponse)
	{
		if (groupByProperty == null)
		{
			//short circuit for ungrouped request
			return chainedResponse;
		}

		try
		{
			Dto data = chainedResponse.getData();

			if (!(data instanceof ResultListDto))
			{
				throw new IllegalArgumentException("group only supports list operations");
			}

			ResultListDto <Dto> resultList = (ResultListDto <Dto>)data;

			return new ApiResponse(chainedResponse.getApiVersion(),
				chainedResponse.getStatus(),
				new ResultMapDto <Dto>(groupDto(((ResultListDto <Dto>)data).getResultList())),
				chainedResponse.getError(),
				chainedResponse.getPaging());
		}
		catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
		{
			throw new BadRequestException(version, "unable to group by " + groupByProperty, e);
		}
	}

	private Map <Object, List <Dto>> groupDto(List <Dto> resultList)
		throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		Map <Object, List <Dto>> grouped = new HashMap <>();

		for (Dto dto : resultList)
		{
			Object groupingValue = PropertyUtils.getProperty(dto, groupByProperty);
			List <Dto> groupedList = grouped.get(groupingValue);
			if (groupedList == null)
			{
				groupedList = new ArrayList <>();
				grouped.put(groupingValue, groupedList);
			}
			groupedList.add(dto);
		}

		return grouped;

	}

}
