package com.bt.nextgen.core.toggle;

import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Base class to extend for service beans that need to swap in different underlying implementations based on a
 * feature toggle. Superclasses simply specify the name of the toggle to check, and the names of the two beans that
 * are to be used based on whether the toggle is on or off. This class does all the pre-work of checking the toggle,
 * and then loading the relevant bean instance from the application context. The subclass can then use this bean
 * sure in the knowledge that they've got the correct instance.
 */
public class ToggledBeanUser<B> {

    @Autowired
    private ApplicationContext application;

    @Autowired
    private FeatureTogglesService togglesService;

    private final Class<B> beanClass;

    private final String toggleName;

    private final String beanNameIfToggleOn;

    private final String beanNameIfToggleOff;

    /** The actual bean instance being used. */
    private B bean;

    /**
     * Constructor, to be invoked by subclasses.
     * @param beanClass class of the service interface being used.
     * @param toggleName name of the feature toggle to be checked.
     * @param beanNameIfToggleOn name of the implementation bean to fetch if the toggle is on.
     * @param beanNameIfToggleOff name of the implementation bean to fetch if the toggle is off.
     */
    @SuppressWarnings({"squid:UnusedProtectedMethod"})
    protected ToggledBeanUser(Class<B> beanClass, String toggleName, String beanNameIfToggleOn, String beanNameIfToggleOff) {
        this.beanClass = beanClass;
        this.toggleName = toggleName;
        this.beanNameIfToggleOn = beanNameIfToggleOn;
        this.beanNameIfToggleOff = beanNameIfToggleOff;
    }

    /**
     * Check the feature toggles service to see which way the wind is blowing, and load the appropriate bean instance.
     */
    @PostConstruct
    public void initBean() {
        final FeatureToggles toggles = togglesService.findOne(new FailFastErrorsImpl());
        final boolean toggle = toggles.getFeatureToggle(toggleName);
        final String beanName = toggle ? beanNameIfToggleOn : beanNameIfToggleOff;
        getLogger(getClass()).info("Toggle: {}={}; Loading bean: {}", toggleName, toggle, beanName);
        this.bean = application.getBean(beanName, beanClass);
    }

    /**
     * Subclasses use this method to access the correctly loaded bean.
     * @return the bean instance to be used.
     */
    @SuppressWarnings({"squid:UnusedProtectedMethod"})
    protected B getBean() {
        return bean;
    }
}
