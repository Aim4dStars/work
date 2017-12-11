package com.bt.nextgen.config;

import com.bt.nextgen.core.util.Properties;
import com.btfin.panorama.core.security.aes.AESEncryptService;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class JpaConfig {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AESEncryptService aesEncryptService;

    @Bean
    @Autowired
    @Primary
    public TransactionTemplate createTransactionTemplate(
            @Qualifier("springJpaTransactionManager") PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() throws NamingException {
        final LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource());
        factoryBean.setPackagesToScan(new String[] { "com.bt.nextgen.core.repository", "com.bt.nextgen.payments.repository",
                "com.bt.nextgen.transactions.repository", "com.bt.nextgen.draftaccount.repository",
                "com.bt.nextgen.service.integration.registration.model",
                "com.bt.nextgen.service.integration.registration.repository", "com.bt.nextgen.service.integration.options.model",
                "com.bt.nextgen.service.integration.termsandconditions.model",
                "com.bt.nextgen.service.integration.termsandconditions.repository",
                "com.bt.nextgen.service.integration.onboardinglog.model",
                "com.bt.nextgen.service.integration.onboardinglog.repository",
                "com.bt.nextgen.service.integration.user.notices.model",
                "com.bt.nextgen.service.integration.user.notices.repository",
                "com.btfin.panorama.service.integration.investmentfinder.model",
                "com.btfin.panorama.service.integration.watchlist.model",
                "com.bt.nextgen.serviceops.repository"});

        final JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter() {
            {
                setDatabasePlatform(Properties.get("jpa.dialect"));
                setShowSql(Properties.getBoolean("jpa.showSql"));
            }
        };

        factoryBean.setJpaVendorAdapter(vendorAdapter);
        factoryBean.setJpaProperties(additionalProperties());

        return factoryBean;
    }

    @Bean
    @Primary
    public DataSource dataSource() throws NamingException {
        final String jndiName = Properties.get("database.jndiName");
        logger.info("JNDI Name is {} ", jndiName);
        if (jndiName != null && jndiName.length() > 0) {
            logger.info("Database embedded value false, lookup datasource via jndi {}...", jndiName);
            Context ctx = new InitialContext();
            return (DataSource) ctx.lookup(jndiName);
        } else {

            logger.info("Creating Dataasource object based on property configuration.");
            final BasicDataSource dataSource = new BasicDataSource();

            String encryptedPassword = Properties.get("jdbc.password");
            logger.info("Getting Database configuration details  {} ", encryptedPassword.isEmpty());

            if (encryptedPassword.length() > 0) {
                String password = null;
                try {
                    password = aesEncryptService.decrypt(Properties.get("jdbc.password"));
                } catch (Exception e) {
                    logger.error("error decrypting password", e);
                    throw new IllegalStateException("error decrypting password");
                }
                dataSource.setPassword(password);
            }
            dataSource.setDriverClassName(Properties.get("jdbc.driver"));
            dataSource.setUrl(Properties.get("jdbc.url"));
            dataSource.setUsername(Properties.get("jdbc.username"));
            dataSource.setValidationQuery(Properties.get("jdbc.validationQuery"));
            dataSource.setTestWhileIdle(Properties.getBoolean("jdbc.testWhileIdle"));
            dataSource.setInitialSize(Properties.getInteger("jdbc.initialSize"));
            dataSource.setMaxActive(Properties.getInteger("jdbc.maxActive"));
            dataSource.setMaxIdle(Properties.getInteger("jdbc.maxIdle"));
            dataSource.setMinIdle(Properties.getInteger("jdbc.minIdle"));
            dataSource.setTimeBetweenEvictionRunsMillis(Properties.getInteger("jdbc.timeBetweenEvictionRunsMillis"));

            return dataSource;

        }

    }

    @Bean
    @Autowired
    @Primary
    public JpaTransactionManager springJpaTransactionManager(@Qualifier("entityManagerFactoryBean") AbstractEntityManagerFactoryBean entityBean) {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityBean.getObject());
        return transactionManager;
    }

    @SuppressWarnings("serial")
    final java.util.Properties additionalProperties() {
        return new java.util.Properties() {
            {
                setProperty("hibernate.cache.region.factory_class", "com.btfin.panorama.core.cache.EhCacheRegionFactory");
                setProperty("hibernate.cache.use_query_cache", Properties.get("jpa.cache"));
                setProperty("hibernate.cache.use_second_level_cache", Properties.get("jpa.cache"));
                setProperty("hibernate.cache.provider_configuration_file_resource_path", "/cache/ehcache.xml");
                setProperty("hibernate.jdbc.fetch_size", Properties.get("jpa.fetch_size"));
                setProperty("hibernate.jdbc.batch_size", Properties.get("jpa.batch_size"));
            }
        };
    }
}