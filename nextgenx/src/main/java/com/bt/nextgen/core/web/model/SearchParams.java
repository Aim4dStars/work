package com.bt.nextgen.core.web.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Hierarchical Enum :
 * Created custom field "parent" that is initialised by constructor.
 * This structure allows implementation of method "is" that works like operator instanceof for classes and interfaces.
 * @author L055167
 *
 */
public enum SearchParams
{
	SEARCH_TYPE(null),

	AVALOQ_SEARCH_PARAMS(SEARCH_TYPE),
	TYPE(AVALOQ_SEARCH_PARAMS),
	FROM_DATE(AVALOQ_SEARCH_PARAMS),
	TO_DATE(AVALOQ_SEARCH_PARAMS),
	HIGHEST_AMOUNT(AVALOQ_SEARCH_PARAMS),
	LOWEST_AMOUNT(AVALOQ_SEARCH_PARAMS),
	PORTFOLIO_ID(AVALOQ_SEARCH_PARAMS),
	AVAILABLE_CASH(AVALOQ_SEARCH_PARAMS),
	ADVISER_NAME(AVALOQ_SEARCH_PARAMS),
	TOKEN(AVALOQ_SEARCH_PARAMS),
	STATE(AVALOQ_SEARCH_PARAMS),
	PORTFOLIO_VALUE(AVALOQ_SEARCH_PARAMS),
	ACCOUNT_TYPE(AVALOQ_SEARCH_PARAMS),
	TRX_TYPE_DESC(AVALOQ_SEARCH_PARAMS),
	fromTermDate(AVALOQ_SEARCH_PARAMS),
	toTermDate(AVALOQ_SEARCH_PARAMS),
	token(AVALOQ_SEARCH_PARAMS),
    startDate(AVALOQ_SEARCH_PARAMS),
    endDate(AVALOQ_SEARCH_PARAMS),
	ORDER_ID(AVALOQ_SEARCH_PARAMS),
	ADVISER_ID(AVALOQ_SEARCH_PARAMS),

	UI_SEARCH_PARAMS(SEARCH_TYPE),
	SORT_BY(UI_SEARCH_PARAMS),
	SORT_ORDER(UI_SEARCH_PARAMS),
	ROW_NUM(UI_SEARCH_PARAMS),
	index(UI_SEARCH_PARAMS),
	ts(UI_SEARCH_PARAMS);

	private SearchParams parent = null;
	private List <SearchParams> children = new ArrayList <SearchParams>();

	private SearchParams(SearchParams parent)
	{
		this.parent = parent;
		if (this.parent != null)
		{
			this.parent.addChild(this);
		}
	}

	private void addChild(SearchParams child)
	{
		this.children.add(child);
	}

	/**
	 * checks whether a SearchParam is a child of a Parent
	 * @param searchParam
	 * @return
	 */
	public boolean is(SearchParams searchParam)
	{
		if (searchParam == null)
		{
			return false;
		}

		for (SearchParams temp = this; temp != null; temp = temp.parent)
		{
			if (searchParam == temp)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * return immediate children of that parent.
	 * @return
	 */
	public SearchParams[] children()
	{
		return children.toArray(new SearchParams[children.size()]);
	}

	/**
	 * return all the child nodes of that parent
	 * @return
	 */
	public SearchParams[] allChildren()
	{
		List <SearchParams> list = new ArrayList <SearchParams>();
		addChildren(this, list);
		return list.toArray(new SearchParams[list.size()]);
	}

	private static void addChildren(SearchParams root, List <SearchParams> list)
	{
		list.addAll(root.children);
		for (SearchParams child : root.children)
		{
			addChildren(child, list);
		}
	}

}
