package com.bt.nextgen.service.integration.financialdocument;

import org.oasis_open.docs.ns.cmis.messaging._200908.Query;

/**
 * Created by L062329 on 16/07/2015.
 */
public interface QueryBuilder {

    Criteria createCriteria();

    void setCriteria(Criteria criteria);

    String getQuery();

    Query getCmisQueryObject();
}
