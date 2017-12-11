package com.bt.nextgen.core.web.model;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created with IntelliJ IDEA.
 * User: L053474
 * Date: 2/10/13
 * Time: 4:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class SingleCriterion extends Criterion {
    public SingleCriterion(String name, String value) {
        super(name, asList(new String[]{value}));
    }

    @Override
    public boolean isSingleValue() {
        return true;
    }
}
