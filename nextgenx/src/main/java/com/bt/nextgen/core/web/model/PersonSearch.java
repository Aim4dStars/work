package com.bt.nextgen.core.web.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew Barker (m035652)
 * Date: 9/08/13
 * Time: 11:35 AM
 */
public class PersonSearch {

    public enum PersonSearchType
    {
        INVESTOR,ADVISER,PARA_PLANNER, ALL_USERS, INTERMEDIARIES
    };

    private int maxResultSize = -1; //provide an unlimited result set

    private PersonSearchType searchType;

    private List<SearchCriteria> searchCriteria;


    public PersonSearch(PersonSearchType type)
    {
        this.searchType = type;
    }

    public List<SearchCriteria> getSearchCriteria() {
        if(searchCriteria==null)
            this.searchCriteria=new ArrayList<>();
        return this.searchCriteria;
    }

    public void setSearchCriteria(List<SearchCriteria> searchCriteria) {
        this.searchCriteria = searchCriteria;
    }


    public PersonSearchType getSearchType(){
         return this.searchType;
    }

    public void setSearchType(PersonSearchType type){
        this.searchType = type;
    }

    public int getMaxResultSize() {
        return maxResultSize;
    }

    public void setMaxResultSize(int maxResultSize) {
        this.maxResultSize = maxResultSize;
    }
}
