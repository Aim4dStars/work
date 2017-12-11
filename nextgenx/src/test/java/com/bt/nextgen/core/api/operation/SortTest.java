package com.bt.nextgen.core.api.operation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;

public class SortTest {
    private static List<TestDto> testList;

    class TestOperation implements ControllerOperation {

        @Override
        public ApiResponse performOperation() {
            return new ApiResponse("Vtest", new ResultListDto<>(new ArrayList<TestDto>(testList)));
        }

    }

    @Before
    public void setup() {
        testList = new ArrayList<>();
        testList.add(new TestDto(new TestKey("k1", "k2"), "c", "1"));
        testList.add(new TestDto(new TestKey("k3", "k4"), "a", "2"));
        testList.add(new TestDto(new TestKey("k5", "k6"), "a", "3"));
        testList.add(new TestDto(new TestKey("k7", "k8"), "b", "3"));
        testList = Collections.unmodifiableList(testList);
    }

    @Test
    public void testPerformOperation_whenOperationIsInvokedForASingleAttribute_thenTheListIsSortedAccordingToThatAttribute() {
        ApiResponse response = new Sort<>(new TestOperation(), "attr1").performOperation();
        List<TestDto> result = ((ResultListDto<TestDto>) response.getData()).getResultList();
        Assert.assertEquals(result.size(), testList.size());
        Assert.assertEquals(result.get(0).getAttr1(), "a");
        Assert.assertEquals(result.get(1).getAttr1(), "a");
        Assert.assertEquals(result.get(2).getAttr1(), "b");
        Assert.assertEquals(result.get(3).getAttr1(), "c");
    }

    @Test
    public void testPerformOperation_whenOperationIsInvokedForASingleAttributeDescending_thenTheListIsSortedAccordingToThatAttributeDescending() {
        ApiResponse response = new Sort<>(new TestOperation(), "attr1,desc").performOperation();
        List<TestDto> result = ((ResultListDto<TestDto>) response.getData()).getResultList();
        Assert.assertEquals(result.size(), testList.size());
        Assert.assertEquals(result.get(0).getAttr1(), "c");
        Assert.assertEquals(result.get(1).getAttr1(), "b");
        Assert.assertEquals(result.get(2).getAttr1(), "a");
        Assert.assertEquals(result.get(3).getAttr1(), "a");
    }

    @Test
    public void testPerformOperation_whenOperationIsInvokedForAMoreThanOneAttribute_thenTheListIsSortedInTheOrderListed() {
        ApiResponse response = new Sort<>(new TestOperation(), "attr2;attr1,desc").performOperation();
        List<TestDto> result = ((ResultListDto<TestDto>) response.getData()).getResultList();
        Assert.assertEquals(result.size(), testList.size());
        Assert.assertEquals(result.get(0).getAttr1(), "c");
        Assert.assertEquals(result.get(1).getAttr1(), "a");
        Assert.assertEquals(result.get(2).getAttr1(), "b");
        Assert.assertEquals(result.get(3).getAttr1(), "a");
    }

    @Test
    public void testPerformOperation_whenComparingStrings_itIsCaseInsensitiveComparison() {
        testList = new ArrayList<>();
        testList.add(new TestDto(new TestKey("k1", "k2"), "BT Aus", "1"));
        testList.add(new TestDto(new TestKey("k3", "k4"), "Bennelong", "2"));
        testList.add(new TestDto(new TestKey("k3", "k4"), "B T Bennelong", "2"));
        ApiResponse response = new Sort<>(new TestOperation(), "attr1").performOperation();
        List<TestDto> result = ((ResultListDto<TestDto>) response.getData()).getResultList();
        Assert.assertEquals(result.size(), testList.size());
        Assert.assertEquals(result.get(0).getAttr1(), "B T Bennelong");
        Assert.assertEquals(result.get(1).getAttr1(), "Bennelong");
        Assert.assertEquals(result.get(2).getAttr1(), "BT Aus");
    }

}
