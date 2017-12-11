package com.bt.nextgen.config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes =
{
	JpaConfig.class, com.btfin.panorama.core.security.aes.AESEncryptService.class
})
public class JpaConfigIntegrationTest implements BeanFactoryAware
{
	private BeanFactory beanFactory;

	@Test
	public void testEntityManagerFactoryBean()
	{
		LocalContainerEntityManagerFactoryBean bean = beanFactory.getBean(LocalContainerEntityManagerFactoryBean.class);
		assertNotNull(bean.getDataSource());
		assertNotNull(bean.getJpaVendorAdapter());
		assertNotNull(bean.getJpaPropertyMap());

	}

	@Test
	public void testDataSource() throws SQLException
	{
		DataSource dataSource = beanFactory.getBean(DataSource.class);
		assertNotNull(dataSource);
		assertNotNull(dataSource.getConnection());
	}

	@Test
	public void testTransactionManager()
	{
		JpaTransactionManager bean = beanFactory.getBean(JpaTransactionManager.class);
	    assertNotNull(bean);
	    assertNotNull(bean.getEntityManagerFactory());
	    assertNotNull(bean.getDataSource());
	}

	@Test
	public void testAdditionalProperties()
	{
		java.util.Properties bean = beanFactory.getBean(java.util.Properties.class);
		assertNotNull(bean);
		assertFalse(bean.isEmpty());
		
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException
	{
		this.beanFactory = beanFactory;

	}

}
