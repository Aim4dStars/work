package com.bt.nextgen.core.jms.cacheinvalidation;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.NestedCheckedException;
import org.springframework.core.NestedRuntimeException;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.mock;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

public final class ServiceLocatorFactoryBeanTest {

    private DefaultListableBeanFactory bf;

    @Before
    public void setUp() {
        bf = new DefaultListableBeanFactory();
    }

    @Test
    public void testNoArgGetter() {
        bf.registerBeanDefinition("testService", genericBeanDefinition(TestService.class).getBeanDefinition());
        bf.registerBeanDefinition("factory",
                genericBeanDefinition(ServiceLocatorFactoryBean.class)
                        .addPropertyValue("serviceLocatorInterface", TestServiceLocator.class)
                        .getBeanDefinition());

        TestServiceLocator factory = (TestServiceLocator) bf.getBean("factory");
        TestService testService = factory.getTestService();
        assertNotNull(testService);
    }

    @Test
    public void testErrorOnTooManyOrTooFew() throws Exception {
        bf.registerBeanDefinition("testService", genericBeanDefinition(TestService.class).getBeanDefinition());
        bf.registerBeanDefinition("testServiceInstance2", genericBeanDefinition(TestService.class).getBeanDefinition());
        bf.registerBeanDefinition("factory",
                genericBeanDefinition(ServiceLocatorFactoryBean.class)
                        .addPropertyValue("serviceLocatorInterface", TestServiceLocator.class)
                        .getBeanDefinition());
        bf.registerBeanDefinition("factory2",
                genericBeanDefinition(ServiceLocatorFactoryBean.class)
                        .addPropertyValue("serviceLocatorInterface", TestServiceLocator2.class)
                        .getBeanDefinition());
        bf.registerBeanDefinition("factory3",
                genericBeanDefinition(ServiceLocatorFactoryBean.class)
                        .addPropertyValue("serviceLocatorInterface", TestService2Locator.class)
                        .getBeanDefinition());

        try {
            TestServiceLocator factory = (TestServiceLocator) bf.getBean("factory");
            factory.getTestService();
            fail("Must fail on more than one matching type");
        } catch (NoSuchBeanDefinitionException ex) { /* expected */ }

        try {
            TestServiceLocator2 factory = (TestServiceLocator2) bf.getBean("factory2");
            factory.getTestService(null);
            fail("Must fail on more than one matching type");
        } catch (NoSuchBeanDefinitionException ex) { /* expected */ }

        try {
            TestService2Locator factory = (TestService2Locator) bf.getBean("factory3");
            factory.getTestService();
            fail("Must fail on no matching types");
        } catch (NoSuchBeanDefinitionException ex) { /* expected */ }
    }

    @Test
    public void testErrorOnTooManyOrTooFewWithCustomServiceLocatorException() {
        bf.registerBeanDefinition("testService", genericBeanDefinition(TestService.class).getBeanDefinition());
        bf.registerBeanDefinition("testServiceInstance2", genericBeanDefinition(TestService.class).getBeanDefinition());
        bf.registerBeanDefinition("factory",
                genericBeanDefinition(ServiceLocatorFactoryBean.class)
                        .addPropertyValue("serviceLocatorInterface", TestServiceLocator.class)
                        .addPropertyValue("serviceLocatorExceptionClass", CustomServiceLocatorException1.class)
                        .getBeanDefinition());
        bf.registerBeanDefinition("factory2",
                genericBeanDefinition(ServiceLocatorFactoryBean.class)
                        .addPropertyValue("serviceLocatorInterface", TestServiceLocator2.class)
                        .addPropertyValue("serviceLocatorExceptionClass", CustomServiceLocatorException2.class)
                        .getBeanDefinition());
        bf.registerBeanDefinition("factory3",
                genericBeanDefinition(ServiceLocatorFactoryBean.class)
                        .addPropertyValue("serviceLocatorInterface", TestService2Locator.class)
                        .addPropertyValue("serviceLocatorExceptionClass", CustomServiceLocatorException3.class)
                        .getBeanDefinition());

        try {
            TestServiceLocator factory = (TestServiceLocator) bf.getBean("factory");
            factory.getTestService();
            fail("Must fail on more than one matching type");
        }
        catch (CustomServiceLocatorException1 expected) {
            assertTrue(expected.getCause() instanceof NoSuchBeanDefinitionException);
        }

        try {
            TestServiceLocator2 factory2 = (TestServiceLocator2) bf.getBean("factory2");
            factory2.getTestService(null);
            fail("Must fail on more than one matching type");
        }
        catch (CustomServiceLocatorException2 expected) {
            assertTrue(expected.getCause() instanceof NoSuchBeanDefinitionException);
        }

        try {
            TestService2Locator factory3 = (TestService2Locator) bf.getBean("factory3");
            factory3.getTestService();
            fail("Must fail on no matching type");
        } catch (CustomServiceLocatorException3 ex) { /* expected */ }
    }

    @Test
    public void testStringArgGetter() throws Exception {
        bf.registerBeanDefinition("testService", genericBeanDefinition(TestService.class).getBeanDefinition());
        bf.registerBeanDefinition("factory",
                genericBeanDefinition(ServiceLocatorFactoryBean.class)
                        .addPropertyValue("serviceLocatorInterface", TestServiceLocator2.class)
                        .getBeanDefinition());

        // test string-arg getter with null id
        TestServiceLocator2 factory = (TestServiceLocator2) bf.getBean("factory");

        @SuppressWarnings("unused")
        TestService testBean = factory.getTestService(null);
        // now test with explicit id
        testBean = factory.getTestService("testService");
        // now verify failure on bad id
        try {
            factory.getTestService("bogusTestService");
            fail("Illegal operation allowed");
        } catch (NoSuchBeanDefinitionException ex) { /* expected */ }
    }


    @Test(expected=IllegalArgumentException.class)
    public void testNoServiceLocatorInterfaceSupplied() throws Exception {
        new ServiceLocatorFactoryBean().afterPropertiesSet();
    }

    @Test(expected=IllegalArgumentException.class)
    public void testWhenServiceLocatorInterfaceIsNotAnInterfaceType() throws Exception {
        ServiceLocatorFactoryBean factory = new ServiceLocatorFactoryBean();
        factory.setServiceLocatorInterface(getClass());
        factory.afterPropertiesSet();
        // should throw, bad (non-interface-type) serviceLocator interface supplied
    }

    @Test(expected=IllegalArgumentException.class)
    public void testWhenServiceLocatorExceptionClassToExceptionTypeWithOnlyNoArgCtor() throws Exception {
        ServiceLocatorFactoryBean factory = new ServiceLocatorFactoryBean();
        factory.setServiceLocatorExceptionClass(ExceptionClassWithOnlyZeroArgCtor.class);
        // should throw, bad (invalid-Exception-type) serviceLocatorException class supplied
    }

    @Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("unchecked")
    public void testWhenServiceLocatorExceptionClassIsNotAnExceptionSubclass() throws Exception {
        ServiceLocatorFactoryBean factory = new ServiceLocatorFactoryBean();
        factory.setServiceLocatorExceptionClass((Class) getClass());
        // should throw, bad (non-Exception-type) serviceLocatorException class supplied
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testWhenServiceLocatorMethodCalledWithTooManyParameters() throws Exception {
        ServiceLocatorFactoryBean factory = new ServiceLocatorFactoryBean();
        factory.setServiceLocatorInterface(ServiceLocatorInterfaceWithExtraNonCompliantMethod.class);
        factory.afterPropertiesSet();
        ServiceLocatorInterfaceWithExtraNonCompliantMethod locator = (ServiceLocatorInterfaceWithExtraNonCompliantMethod) factory.getObject();
        locator.getTestService("not", "allowed"); //bad method (too many args, doesn't obey class contract)
    }

    @Test
    public void testRequiresListableBeanFactoryAndChokesOnAnythingElse() throws Exception {
        BeanFactory beanFactory = mock(BeanFactory.class);
        try {
            ServiceLocatorFactoryBean factory = new ServiceLocatorFactoryBean();
            factory.setBeanFactory(beanFactory);
        } catch (FatalBeanException ex) {
            // expected
        }
    }


    public static class TestService {

    }


    public static class ExtendedTestService extends TestService {

    }


    public static class TestService2 {

    }


    public static interface TestServiceLocator {

        TestService getTestService();
    }


    public static interface TestServiceLocator2 {

        TestService getTestService(String id) throws CustomServiceLocatorException2;
    }


    public static interface TestServiceLocator3 {

        TestService getTestService();

        TestService getTestService(String id);

        TestService getTestService(int id);

        TestService someFactoryMethod();
    }


    public static interface TestService2Locator {

        TestService2 getTestService() throws CustomServiceLocatorException3;
    }


    public static interface ServiceLocatorInterfaceWithExtraNonCompliantMethod {

        TestService2 getTestService();

        TestService2 getTestService(String serviceName, String defaultNotAllowedParameter);
    }


    @SuppressWarnings("serial")
    public static class CustomServiceLocatorException1 extends NestedRuntimeException {

        public CustomServiceLocatorException1(String message, Throwable cause) {
            super(message, cause);
        }
    }


    @SuppressWarnings("serial")
    public static class CustomServiceLocatorException2 extends NestedCheckedException {

        public CustomServiceLocatorException2(Throwable cause) {
            super("", cause);
        }
    }


    @SuppressWarnings("serial")
    public static class CustomServiceLocatorException3 extends NestedCheckedException {

        public CustomServiceLocatorException3(String message) {
            super(message);
        }
    }


    @SuppressWarnings("serial")
    public static class ExceptionClassWithOnlyZeroArgCtor extends Exception {

    }

}