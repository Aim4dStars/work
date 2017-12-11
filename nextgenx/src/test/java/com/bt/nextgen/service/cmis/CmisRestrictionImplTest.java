package com.bt.nextgen.service.cmis;

import com.bt.nextgen.service.cmis.annotation.ColumnAnnotationProcessor;
import com.bt.nextgen.service.integration.financialdocument.Restriction;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by L075208 on 1/09/2015.
 */
public class CmisRestrictionImplTest {

    private ColumnAnnotationProcessor processor = ColumnAnnotationProcessor.getInstance();

    @Test
    public void testEqualToRestriction() throws Exception {
        Restriction restriction = new CmisRestrictionImpl();
        restriction.equalTo(processor.getColumn("documentName"), "test");
        Assert.assertEquals(" "+processor.getColumn("documentName")+" = 'test'" ,restriction.toString());
    }
    @Test
    public void testLikeRestriction()
    {
        Restriction restriction=new CmisRestrictionImpl();
        restriction.like(processor.getColumn("documentName"), "test");
        Assert.assertEquals(" "+processor.getColumn("documentName") +" like '%test%'",restriction.toString());
    }
    @Test
    public void testNotEqualRestriction()
    {
        Restriction restriction=new CmisRestrictionImpl();
        restriction.notEqual(processor.getColumn("documentType"), "test");
        Assert.assertEquals(" "+processor.getColumn("documentType") + " <> 'test'",restriction.toString());
    }
    @Test
    public void testInRestriction()
    {
        Restriction restriction=new CmisRestrictionImpl();
        List<String> values=new ArrayList<>();
        values.add("test1");
        values.add("test2");
        restriction.in(processor.getColumn("status"), values);
        Assert.assertEquals(" "+processor.getColumn("status") + " IN ('test1','test2')",restriction.toString());
    }
    @Test
     public void testNotInRestriction()
    {
        Restriction restriction=new CmisRestrictionImpl();
        List<String> values=new ArrayList<>();
        values.add("test1");
        values.add("test2");
        restriction.notIn(processor.getColumn("status"), values);
        Assert.assertEquals(" "+processor.getColumn("status") + " NOT IN ('test1','test2')",restriction.toString());
    }
    @Test
    public void testLessThanRestriction()
    {
        Restriction restriction=new CmisRestrictionImpl();
        DateTime date = new DateTime();
        restriction.lessThan(processor.getColumn("uploadedDate"),date);
        Assert.assertEquals(" "+processor.getColumn("uploadedDate") + " < timestamp '" + date.toString() + "'",restriction.toString());
    }
    @Test
    public void testGreaterThanRestriction()
    {
        Restriction restriction=new CmisRestrictionImpl();
        DateTime date = new DateTime();
        restriction.greaterThan(processor.getColumn("uploadedDate"), date);
        Assert.assertEquals(" "+processor.getColumn("uploadedDate") + " > timestamp '" + date.toString() + "'",restriction.toString());
    }

    @Test
    public void testGreaterThanEqualRestriction()
    {
        Restriction restriction=new CmisRestrictionImpl();
        DateTime date = new DateTime();
        restriction.greaterThanEqual(processor.getColumn("uploadedDate"), date);
        Assert.assertEquals(" "+processor.getColumn("uploadedDate") +" >= timestamp '"+date.toString()+"'",restriction.toString());
    }
    @Test
    public void testLessThanEqualRestriction()
    {
        Restriction restriction=new CmisRestrictionImpl();
        DateTime date = new DateTime();
        restriction.lessThanEqual(processor.getColumn("uploadedDate"), date);
        Assert.assertEquals(" "+processor.getColumn("uploadedDate") +" <= timestamp '"+date.toString()+"'",restriction.toString());
    }
    @Test
    public void isNullRestriction()
    {
        Restriction restriction=new CmisRestrictionImpl();
        restriction.isNull(processor.getColumn("documentName"));
        Assert.assertEquals(" " + processor.getColumn("documentName") + " IS NULL",restriction.toString());
    }
    @Test
    public void isNotNullRestriction()
    {
        Restriction restriction=new CmisRestrictionImpl();
        restriction.isNotNull(processor.getColumn("documentName"));
        Assert.assertEquals(" " + processor.getColumn("documentName") + " IS NOT NULL",restriction.toString());
    }

}
