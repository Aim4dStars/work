package com.bt.nextgen.service.cmis;


import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.cmis.annotation.Column;
import com.bt.nextgen.service.cmis.annotation.ColumnAnnotationProcessor;
import com.bt.nextgen.service.cmis.constants.DocumentConstants;
import com.bt.nextgen.service.integration.financialdocument.Criteria;
import com.bt.nextgen.service.integration.financialdocument.QueryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matcher;
import org.oasis_open.docs.ns.cmis.messaging._200908.ObjectFactory;
import org.oasis_open.docs.ns.cmis.messaging._200908.Query;
import org.springframework.util.CollectionUtils;

import javax.xml.bind.JAXBElement;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Concrete implementation of QueryBuilder interface (Does not supports Select and From clause).
 */
public class CmisQueryBuilderImpl implements QueryBuilder {

    /**
     *
     */
    private Criteria criteria = new CmisCriteriaImpl();

    /**
     *
     */
    private ColumnAnnotationProcessor processor = ColumnAnnotationProcessor.getInstance();




    public CmisQueryBuilderImpl() {
    }

    public String getQuery() {
        String finalQuery = "";
        StringBuilder query = new StringBuilder("SELECT ");
        for (String column : featureToggle()) {
                query.append(column + ",");
        }
        finalQuery = StringUtils.removeEnd(query.toString(), ",");
        finalQuery = fromClause(finalQuery);
        return applyCriteria(finalQuery);
    }

    public Criteria createCriteria() {
        return this.criteria;
    }

    private String fromClause(String query) {
        return query + " FROM " + DocumentConstants.DOCUMENT_CLASS;
    }

    private String applyCriteria(String query) {
        StringBuilder queryWithCriteria = new StringBuilder(query);
        if (!CollectionUtils.isEmpty(criteria.getRestrictionList())) {
            queryWithCriteria.append(" ");
            queryWithCriteria.append(criteria.applyCriteria());
        }
        return queryWithCriteria.toString();
    }

    @Override
    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Query getCmisQueryObject() {
        ObjectFactory of = new ObjectFactory();
        Query query = of.createQuery();
        JAXBElement<BigInteger> propertiesTypeJaxbElement = of.createGetTypeChildrenMaxItems(new BigInteger(DocumentConstants.MAX_RESULT));
        query.setMaxItems(propertiesTypeJaxbElement);
        query.setRepositoryId(DocumentConstants.REPOSITORY);
        query.setStatement(getQuery());
        return query;
    }

    public Collection<String> featureToggle() {
        List<String> configuredColumns = Lambda.convert(processor.getColumns(), new Converter<Column, String>() {
            @Override
            public String convert(Column from) {
                return from.name();
            }
        });
        return configuredColumns;
    }
}
