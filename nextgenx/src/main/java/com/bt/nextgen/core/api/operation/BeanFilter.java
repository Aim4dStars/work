package com.bt.nextgen.core.api.operation;

import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.Dto;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import org.apache.commons.beanutils.BeanUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static ch.lambdaj.Lambda.convert;

/**
 * Api operation for filtering a result by a supplied query string. The filter operation is O(n) and should only be used for small lists for filtering
 * of larger lists, use a SearchByServiceOperation to provide more sophisticated functionality at the service layer.
 */
public class BeanFilter extends ChainedControllerOperation
{
	/** Enumeration of the desired Strictness of the BeanFilter when filtering Objects. */
	public enum Strictness {
		/** Beans will be matched if any of the criteria are satisfied. */
		ANY(false),

		/** Beans won't be matched unless all of the criteria are satisfied. */
		ALL(true);

		/** The defunct result to return if a matching iteration loop concludes without an early exit. */
		private final boolean defunct;

		Strictness(boolean defunct) {
			this.defunct = defunct;
		}

		boolean getDefunct() {
			return defunct;
		}
	}

	public static final String QUERY_PARAMETER = "query";

	private final String apiVersion;
	private final List<BeanPropertyMatcher> matchers;
	private final Strictness strictness;

	public BeanFilter(String apiVersion, ControllerOperation chained, Strictness strictness, List<ApiSearchCriteria> searchCriteria)
	{
		super(chained);
		Assert.notNull(strictness);
		this.apiVersion = apiVersion;
		this.strictness = strictness;
		this.matchers = convert(searchCriteria, new Converter<ApiSearchCriteria, BeanPropertyMatcher>() {
			@Override
			public BeanPropertyMatcher convert(ApiSearchCriteria from) {
				return new BeanPropertyMatcher(from);
			}
		});
	}

	/**
	 * Default strictness is {@link Strictness#ANY any}.
	 * @param apiVersion API version.
	 * @param chained chained operation.
	 * @param searchCriteria search criteria.
     */
	public BeanFilter(String apiVersion, ControllerOperation chained, List<ApiSearchCriteria> searchCriteria)
	{
		this(apiVersion, chained, Strictness.ANY, searchCriteria);
	}

	public BeanFilter(String apiVersion, ControllerOperation chained, Strictness strictness, String queryString)
	{
		this(apiVersion, chained, strictness, ApiSearchCriteria.parseQueryString(apiVersion, queryString));
	}

	public BeanFilter(String apiVersion, ControllerOperation chained, String queryString)
	{
		this(apiVersion, chained, Strictness.ANY, queryString);
	}

	public BeanFilter(String apiVersion, ControllerOperation chained, Strictness strictness, ApiSearchCriteria... searchCriteria) {
		this(apiVersion, chained, strictness, asList(searchCriteria));
	}

	public BeanFilter(String apiVersion, ControllerOperation chained, ApiSearchCriteria... searchCriteria) {
		this(apiVersion, chained, Strictness.ANY, searchCriteria);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ApiResponse performChainedOperation(ApiResponse chainedResponse)
	{
		if (matchers.isEmpty())
		{
			//short circuit where no criteria has been supplied
			return chainedResponse;
		}

		final Dto data = chainedResponse.getData();
		if (!(data instanceof ResultListDto))
		{
			throw new IllegalArgumentException("Sort only supports list operations");
		}

		return new ApiResponse(chainedResponse.getApiVersion(),
			chainedResponse.getStatus(),
			beanFilter((ResultListDto<Dto>) data),
			chainedResponse.getError(),
			chainedResponse.getPaging());
	}

	/**
	 * Filter the provided list of items. Items must match <i>all</i> of the criteria in this filter in order
	 * to qualify.
	 * @param data list of items to be filtered.
	 * @return the filtered list.
     */
	private ResultListDto<Dto> beanFilter(ResultListDto<Dto> data)
	{
		final List<Dto> results = data.getResultList();
		final List<Dto> matching = new ArrayList<>(results.size());

		for (Dto result : results) {
			if (matches(result)) {
				matching.add(result);
			}
		}
		return new ResultListDto<>(matching);
	}

	private boolean matches(Dto result) {
		for (BeanPropertyMatcher matcher : matchers) {
			if (matcher.matches(result)) {
				if (strictness == Strictness.ANY) {
					return true;
				}
			} else {
				if (strictness == Strictness.ALL) {
					return false;
				}
			}
		}
		return strictness.getDefunct();
	}

	private final class BeanPropertyMatcher extends BaseMatcher<String>
	{
		private String property;
		private String expected;
		private SearchOperation operation;

		public BeanPropertyMatcher(ApiSearchCriteria criteria)
		{
			this.property = criteria.getProperty();
			this.operation = criteria.getOperation();
			this.expected = criteria.getValue();
		}

		@Override
		public boolean matches(Object item)
		{
			try
			{
				final String beanPropertyValue = BeanUtils.getProperty(item, property);

				//TODO - numeric and date values are being compared as strings, need some special handling of types
				switch (operation)
				{
					case CONTAINS:
						return Matchers.containsString(expected).matches(beanPropertyValue);
					case EQUALS:
						return Matchers.equalTo(expected).matches(beanPropertyValue);
					case GREATER_THAN:
						return Matchers.greaterThan(expected).matches(beanPropertyValue);
					case LESS_THAN:
						return Matchers.lessThan(expected).matches(beanPropertyValue);
					case STARTS_WITH:
						return Matchers.startsWith(expected).matches(beanPropertyValue);
					case NEG_CONTAINS:
						return !Matchers.contains(expected).matches(beanPropertyValue);
					case NEG_EQUALS:
						return !Matchers.equalTo(expected).matches(beanPropertyValue);
					case NEG_GREATER_THAN:
						return !Matchers.greaterThan(expected).matches(beanPropertyValue);
					case NEG_LESS_THAN:
						return !Matchers.lessThan(expected).matches(beanPropertyValue);
					case NEG_STARTS_WITH:
						return !Matchers.startsWith(expected).matches(beanPropertyValue);
                    case LIST_CONTAINS:
                        final String[] beanListPropertyValue = BeanUtils.getArrayProperty(item, property);
                        return Matchers.hasItemInArray(expected).matches(beanListPropertyValue);
					default:
						throw new IllegalArgumentException("Unsupported filter " + operation);
				}
			}
			catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
			{
				throw new BadRequestException(apiVersion, "unable to filter bean", e);
			}

		}

		@Override
		public void describeTo(Description description)
		{
			description.appendText("beanPropertyMatches(").appendValue(property).appendText(", ")
				.appendValue(operation).appendText(", ").appendValue(expected).appendText(")");
		}
	}
}
