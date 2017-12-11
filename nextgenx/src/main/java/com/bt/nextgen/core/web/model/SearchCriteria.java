package com.bt.nextgen.core.web.model;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew Barker m035652
 * Date: 9/08/13
 * Time: 10:16 AM
 *
 *DESCOPED FOR BETA5: SEARCH SPIKE 
 */

public interface SearchCriteria 
{
	SearchParams getSearchKey();

    String getSearchValue();

    Object getSearchDataType();

    Date getSearchDate();

    Integer getSearchInteger(); 
}
