package com.bt.nextgen.core.web.model;

import java.io.Serializable;
import java.util.List;

import com.bt.nextgen.service.avaloq.Template;
/**
 * 
 * @author L055011
 *
 *DESCOPED FOR BETA5: SEARCH SPIKE
 */
public class SearchParametersImpl implements Serializable, SearchParameters
{
	
	
	private static final long serialVersionUID = 1L;
	
	
	private List<SearchCriteria> searchCriterias;
	private Template searchFor;
	
	
	/* (non-Javadoc)
	 * @see com.bt.nextgen.core.web.model.SearchParameters#getSearchFor()
	 */
	@Override
	public Template getSearchFor() {
		return searchFor;
	}
	public void setSearchFor(Template searchFor) {
		this.searchFor = searchFor;
	}
	/* (non-Javadoc)
	 * @see com.bt.nextgen.core.web.model.SearchParameters#getSearchCriterias()
	 */
	@Override
	public List<SearchCriteria> getSearchCriterias() {
		return searchCriterias;
	}
	public void setSearchCriterias(List<SearchCriteria> searchCriterias) {
		this.searchCriterias = searchCriterias;
	}
	
	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((searchCriterias == null) ? 0 : searchCriterias.hashCode());
		result = prime * result
				+ ((searchFor == null) ? 0 : searchFor.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SearchParametersImpl other = (SearchParametersImpl) obj;
		if (searchCriterias == null) {
			if (other.searchCriterias != null)
				return false;
		} else if (!searchCriterias.equals(other.searchCriterias))
			return false;
		if (searchFor != other.searchFor)
			return false;
		return true;
	}
	
	
}
