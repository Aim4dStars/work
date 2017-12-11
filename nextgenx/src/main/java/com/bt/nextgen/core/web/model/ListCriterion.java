package com.bt.nextgen.core.web.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: L053474
 * Date: 2/10/13
 * Time: 4:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListCriterion extends Criterion {

    public ListCriterion(String name, List<String> values) {
        super(name,values);
    }

    @Override
    public boolean isSingleValue() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}