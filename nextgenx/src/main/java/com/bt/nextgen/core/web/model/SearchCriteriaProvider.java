package com.bt.nextgen.core.web.model;

import java.util.Date;
/**
 * DESCOPED FOR BETA5: SEARCH SPIKE
 * @author L055011
 *
 */
public class SearchCriteriaProvider implements SearchCriteria
{

	private SearchParams searchKey;
	private String searchValue;
	private Object searchDataType;
	private Date searchDate;
	private Integer searchInteger;
	
	public SearchParams getSearchKey() {
		return searchKey;
	}
	public void setSearchKey(SearchParams searchKey) {
		this.searchKey = searchKey;
	}
	public String getSearchValue() {
		return searchValue;
	}
	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}
	
	public Date getSearchDate() {
		return searchDate;
	}
	public void setSearchDate(Date searchDate) {
		this.searchDate = searchDate;
	}
	public Integer getSearchInteger() {
		return searchInteger;
	}
	public void setSearchInteger(Integer searchInteger) {
		this.searchInteger = searchInteger;
	}
	public Object getSearchDataType() {
		return searchDataType;
	}
	public void setSearchDataType(Object searchDataType) {
		this.searchDataType = searchDataType;
	}
	

}

