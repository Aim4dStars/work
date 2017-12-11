package com.bt.nextgen.core.web.model;

import com.bt.nextgen.core.util.Properties;

import java.util.ArrayList;
import java.util.List;
import static java.util.Arrays.asList;

/**
 * Created with IntelliJ IDEA.
 * User: L053474
 * Date: 2/10/13
 * Time: 2:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionCriteria implements Criteria {
    private static final String field1AvaloqName = "avaloqName1";
    private static final String field2AvaloqName = "avaloqName2";
    private static final String field3AvaloqName = "avaloqName3";
    private String field1;
    private String field2;
    private String field3;


    @Override
    public List<Criterion> getCriteria() {
        List<Criterion> criteria = new ArrayList<>();
        criteria.add(new SingleCriterion(Properties.get(field1AvaloqName), formatCriteria(field1)));
        criteria.add(new ListCriterion(Properties.get(field2AvaloqName), asList(new String[]{formatCriteria(field2)})));
        return criteria;
    }

    private static String formatCriteria(String field1) {
        return field1;
    }
}
