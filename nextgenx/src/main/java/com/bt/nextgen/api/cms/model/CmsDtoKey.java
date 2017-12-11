package com.bt.nextgen.api.cms.model;

public class CmsDtoKey {

    //Key which maps to AEM path
    private String key;

    //Parameters which can be used to construct AEM path, comma delimetered string
    private String query;

    public CmsDtoKey() {
    }

    public CmsDtoKey(String key, String query) {
        this.key = key;
        this.query = query;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
