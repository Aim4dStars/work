package com.bt.nextgen.core.toggle.beans;

public abstract class AbstractSampleService implements SampleService {

    private String beanName;

    @Override
    public final void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public final String getBeanName() {
        return beanName;
    }
}
