package com.bt.nextgen.service.cmis;

import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.cmis.annotation.ColumnAnnotationProcessor;
import com.bt.nextgen.service.cmis.constants.DocumentConstants;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.financialdocument.Criteria;
import com.bt.nextgen.service.integration.financialdocument.QueryBuilder;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by L062329 on 16/07/2015.
 */
public class CMISQueryBuilderImplTest {

    private ColumnAnnotationProcessor processor = ColumnAnnotationProcessor.getInstance();

    //@Test
    public void testAddRestriction() throws Exception {
        QueryBuilder queryBuilder = new CmisQueryBuilderImpl();
        Criteria criteria = queryBuilder.createCriteria();
        criteria.add(criteria.equalTo(processor.getColumn("status"), "test"));
        Assert.assertEquals("WHERE " + processor.getColumn("status") + " = 'test'", criteria.applyCriteria());
    }

    @Test
    public void testAndRestriction() throws Exception {
        QueryBuilder queryBuilder = new CmisQueryBuilderImpl();
        Criteria criteria = queryBuilder.createCriteria();
        criteria.add(criteria.equalTo(processor.getColumn("status"), "test"));
        criteria.add(criteria.and(criteria.equalTo(processor.getColumn("documentName"), "test")));
        Assert.assertEquals("WHERE " + processor.getColumn("status") + " = 'test' AND ( " + processor.getColumn("documentName") + " = 'test' )", criteria.applyCriteria());

        queryBuilder = new CmisQueryBuilderImpl();
        criteria = queryBuilder.createCriteria();
        criteria.add(criteria.equalTo(processor.getColumn("status"), "test"));
        criteria.add(criteria.and(criteria.equalTo(processor.getColumn("documentName"), "test")));
        criteria.add(criteria.and(criteria.equalTo(processor.getColumn("uploadedBy"), "test")));
        Assert.assertEquals("WHERE " + processor.getColumn("status") + " = 'test' AND ( " +  processor.getColumn("documentName") + " = 'test' ) AND ( " +  processor.getColumn("uploadedBy") + " = 'test' )", criteria.applyCriteria());
    }

    @Test
    public void testOrRestriction() throws Exception {
        QueryBuilder queryBuilder = new CmisQueryBuilderImpl();
        Criteria criteria = queryBuilder.createCriteria();
        criteria.add(criteria.equalTo(processor.getColumn("status"), "test"));
        criteria.add(criteria.or(criteria.equalTo(processor.getColumn("documentName"), "test")));
        Assert.assertEquals("WHERE " + processor.getColumn("status") + " = 'test' OR ( "+ processor.getColumn("documentName") +" = 'test' )", criteria.applyCriteria());

        queryBuilder = new CmisQueryBuilderImpl();
        criteria = queryBuilder.createCriteria();
        criteria.add(criteria.equalTo(processor.getColumn("status"), "test"));
        criteria.add(criteria.or(criteria.equalTo(processor.getColumn("documentName"), "test")));
        criteria.add(criteria.or(criteria.equalTo(processor.getColumn("uploadedBy"), "test")));
        Assert.assertEquals("WHERE "+ processor.getColumn("status") +" = 'test' OR ( "+ processor.getColumn("documentName") +" = 'test' ) OR ( "+ processor.getColumn("uploadedBy") +" = 'test' )", criteria.applyCriteria());

    }

    @Test
    public void testLikeRestriction() throws Exception {
        QueryBuilder queryBuilder = new CmisQueryBuilderImpl();
        Criteria criteria = queryBuilder.createCriteria();
        criteria.add(criteria.like(processor.getColumn("documentName"), "test"));
        Assert.assertEquals("WHERE "+ processor.getColumn("documentName") +" like '%test%'", criteria.applyCriteria());
    }

    @Test
    public void testNotEqualRestriction() throws Exception {
        QueryBuilder queryBuilder = new CmisQueryBuilderImpl();
        Criteria criteria = queryBuilder.createCriteria();
        criteria.add(criteria.notEqual(processor.getColumn("documentType"), "test"));
        Assert.assertEquals("WHERE " + processor.getColumn("documentType") + " <> 'test'", criteria.applyCriteria());
    }

    @Test(expected = BadRequestException.class)
    public void testInRestriction() throws Exception {
        QueryBuilder queryBuilder = new CmisQueryBuilderImpl();
        Criteria criteria = queryBuilder.createCriteria();
        List<String> values = new ArrayList<String>();

        values.add("test");
        values.add("test2");
        values.add("test3");
        criteria.add(criteria.in(processor.getColumn("status"), values));
        Assert.assertEquals("WHERE " + processor.getColumn("status") + " IN ('test','test2','test3')", criteria.applyCriteria());

        queryBuilder = new CmisQueryBuilderImpl();
        criteria = queryBuilder.createCriteria();
        values = new ArrayList<String>();
        criteria.add(criteria.in("test", values));
    }

    @Test
     public void testLessThanRestriction() throws Exception {
        QueryBuilder queryBuilder = new CmisQueryBuilderImpl();
        Criteria criteria = queryBuilder.createCriteria();
        DateTime date = new DateTime();
        criteria.add(criteria.lessThan(processor.getColumn("uploadedDate"), date));
        Assert.assertEquals("WHERE " + processor.getColumn("uploadedDate") + " < timestamp '" + date.toString() + "'", criteria.applyCriteria());
    }

    @Test
    public void testGreaterThanRestriction() throws Exception {
        QueryBuilder queryBuilder = new CmisQueryBuilderImpl();
        Criteria criteria = queryBuilder.createCriteria();
        DateTime date = new DateTime();
        criteria.add(criteria.greaterThan(processor.getColumn("uploadedDate"), date));
        Assert.assertEquals("WHERE " + processor.getColumn("uploadedDate") + " > timestamp '" + date.toString() + "'", criteria.applyCriteria());
    }

    @Test
    public void testGreaterThanEqualRestriction() throws Exception {
        QueryBuilder queryBuilder = new CmisQueryBuilderImpl();
        Criteria criteria = queryBuilder.createCriteria();
        DateTime date = new DateTime();
        criteria.add(criteria.greaterThanEqual(processor.getColumn("uploadedDate"), date));
        Assert.assertEquals("WHERE "+ processor.getColumn("uploadedDate") +" >= timestamp '"+date.toString()+"'", criteria.applyCriteria());
    }

    @Test
    public void testLessThanEqualRestriction() throws Exception {
        QueryBuilder queryBuilder = new CmisQueryBuilderImpl();
        Criteria criteria = queryBuilder.createCriteria();
        DateTime date = new DateTime();
        criteria.add(criteria.lessThanEqual(processor.getColumn("uploadedDate"), date));
        Assert.assertEquals("WHERE "+ processor.getColumn("uploadedDate") +" <= timestamp '"+date.toString()+"'", criteria.applyCriteria());
    }

    @Test
    public void testAndOrRestriction() throws Exception {
        QueryBuilder queryBuilder = new CmisQueryBuilderImpl();
        Criteria criteria = queryBuilder.createCriteria();
        criteria.add(criteria.equalTo(processor.getColumn("status"), "test"));
        DateTime date = new DateTime();
        criteria.add(criteria.and(criteria.lessThanEqual(processor.getColumn("uploadedDate"), date)));
        criteria.add(criteria.or(criteria.greaterThanEqual(processor.getColumn("uploadedDate"), date)));
        Assert.assertEquals("WHERE "+ processor.getColumn("status") +" = 'test' AND ( "+ processor.getColumn("uploadedDate") +" <= timestamp '"+date.toString()+"' ) OR ( "+ processor.getColumn("uploadedDate") +" >= timestamp '"+date.toString()+"' )", criteria.applyCriteria());
    }

    @Test
    public void testMultipleAndOr() throws Exception {
        QueryBuilder queryBuilder = new CmisQueryBuilderImpl();
        Criteria criteria = queryBuilder.createCriteria();
        criteria.add(criteria.equalTo("status", "test"));
        criteria.add(criteria.and(criteria.equalTo("financialYear", "test"), criteria.or(criteria.equalTo("startDate", "testDate"), criteria.and(criteria.equalTo("endDate", "testDate")))));
        Assert.assertEquals("WHERE status = 'test' AND ( financialYear = 'test' OR ( startDate = 'testDate' AND ( endDate = 'testDate' )))", criteria.applyCriteria());
    }

    @Test
    public void testIsNotNull() throws Exception {
        QueryBuilder queryBuilder = new CmisQueryBuilderImpl();
        Criteria criteria = queryBuilder.createCriteria();
        criteria.add(criteria.equalTo("status", "test"));
        criteria.add(criteria.and(criteria.isNotNull("financialYear"), criteria.or(criteria.equalTo("financialYear", "testDate"))));
        Assert.assertEquals("WHERE status = 'test' AND ( financialYear IS NOT NULL OR ( financialYear = 'testDate' ))", criteria.applyCriteria());

    }

    @Test
    public void testIsNull() throws Exception {
        QueryBuilder queryBuilder = new CmisQueryBuilderImpl();
        Criteria criteria = queryBuilder.createCriteria();
        criteria.add(criteria.equalTo("status", "test"));
        criteria.add(criteria.and(criteria.isNull("test")));
        Assert.assertEquals("WHERE status = 'test' AND ( test IS NULL )", criteria.applyCriteria());

    }

    @Test
    public void featureToggle() throws Exception {
        CmisQueryBuilderImpl queryBuilder = new CmisQueryBuilderImpl();
        Collection<String> list = queryBuilder.featureToggle();
        if(Properties.getBoolean("feature.doclibv2")) {
            Assert.assertTrue(list.contains(DocumentConstants.COLUMN_DELETEDBY_ID));
            Assert.assertTrue(list.contains(DocumentConstants.COLUMN_RESTOREDBY_ID));
            Assert.assertTrue(list.contains(DocumentConstants.COLUMN_DELETED_ON));
            Assert.assertTrue(list.contains(DocumentConstants.COLUMN_RESTORED_ON));
            Assert.assertTrue(list.contains(DocumentConstants.COLUMN_DELETEDBY_ROLE));
            Assert.assertTrue(list.contains(DocumentConstants.COLUMN_RESTOREDBY_ROLE));
            Assert.assertTrue(list.contains(DocumentConstants.COLUMN_DELETEDBY_NAME));
            Assert.assertTrue(list.contains(DocumentConstants.COLUMN_RESTOREDBY_NAME));
            Assert.assertTrue(list.contains(DocumentConstants.COLUMN_DELETED));
            Assert.assertTrue(list.contains(DocumentConstants.COLUMN_PERMANENT));
            Assert.assertTrue(list.contains(DocumentConstants.COLUMN_UPDATEDBY_ID));
            Assert.assertTrue(list.contains(DocumentConstants.COLUMN_UPDATEDBY_ROLE));
            Assert.assertTrue(list.contains(DocumentConstants.COLUMN_UPDATEDBY_NAME));
        }
        else {
            Assert.assertFalse(list.contains(DocumentConstants.COLUMN_DELETEDBY_ID));
            Assert.assertFalse(list.contains(DocumentConstants.COLUMN_RESTOREDBY_ID));
            Assert.assertFalse(list.contains(DocumentConstants.COLUMN_DELETED_ON));
            Assert.assertFalse(list.contains(DocumentConstants.COLUMN_RESTORED_ON));
            Assert.assertFalse(list.contains(DocumentConstants.COLUMN_DELETEDBY_ROLE));
            Assert.assertFalse(list.contains(DocumentConstants.COLUMN_RESTOREDBY_ROLE));
            Assert.assertFalse(list.contains(DocumentConstants.COLUMN_DELETEDBY_NAME));
            Assert.assertFalse(list.contains(DocumentConstants.COLUMN_RESTOREDBY_NAME));
            Assert.assertFalse(list.contains(DocumentConstants.COLUMN_DELETED));
            Assert.assertFalse(list.contains(DocumentConstants.COLUMN_PERMANENT));
            Assert.assertFalse(list.contains(DocumentConstants.COLUMN_UPDATEDBY_ID));
            Assert.assertFalse(list.contains(DocumentConstants.COLUMN_UPDATEDBY_ROLE));
            Assert.assertFalse(list.contains(DocumentConstants.COLUMN_UPDATEDBY_NAME));
        }

    }

}