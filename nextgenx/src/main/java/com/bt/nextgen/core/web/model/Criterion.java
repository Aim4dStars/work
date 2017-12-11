package com.bt.nextgen.core.web.model;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: L053474
 * Date: 2/10/13
 * Time: 2:10 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Criterion {
    private final String name;
    private final List<String> value;

    public abstract boolean isSingleValue();
    protected Criterion(String name, List<String> value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public List<String> getValue() {
        return value;
    }
}
