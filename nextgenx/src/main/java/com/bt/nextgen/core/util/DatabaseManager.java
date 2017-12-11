package com.bt.nextgen.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Component;

import liquibase.Liquibase;
import liquibase.configuration.GlobalConfiguration;
import liquibase.configuration.LiquibaseConfiguration;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.logging.LogFactory;
import liquibase.logging.LogLevel;
import liquibase.resource.ResourceAccessor;

import static com.bt.nextgen.core.util.SETTINGS.*;

@Component
public class DatabaseManager
{
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private DataSource dataSource;

	private ResourceAccessor resourceAccessor = new ResourceAccessor()
	{
		private DefaultResourceLoader loader = new DefaultResourceLoader();

		@Override public Set<InputStream> getResourcesAsStream(String path) throws IOException
		{
			return Collections.singleton(loader.getResource(path).getInputStream());
		}

		@Override
		public Set<String> list(String relativeTo, String path, boolean includeFiles, boolean includeDirectories,
			boolean recursive) throws IOException
		{
			throw new IOException("This is not supported");
		}

		@Override public ClassLoader toClassLoader()
		{
			return loader.getClassLoader();
		}
	};

	public DatabaseManager()
	{
	}

	public void setResourceAccessor(ResourceAccessor resourceAccessor)
	{
		this.resourceAccessor = resourceAccessor;
	}

	public void setDataSource(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}

	public void run()
	{
		Database database = null;
        Liquibase liquibase = getLiquibase();
        try
        {
            String contexts = DATABASE_LIQUIBASE_CONTEXTS.value("");
            logger.info("Estimating database work running contexts '{}' ...", contexts);

            liquibase.reportStatus(true, contexts, getOutputWriter());

            logger.info("Calculated the following changes to database (Sql written to stdout)...");
            liquibase.update(contexts, getOutputWriter());

            // TODO this could be interesting in the future for 'non prod' testing - liquibase.updateTestingRollback()

            if(DATABASE_LIQUIBASE_AUTOMATION_ENABLED.isTrue()){
                logger.info("Automated installs enabled, running update....");
                getLiquibase().update(contexts);
            }
        }
        catch (LiquibaseException e)
        {
            logger.error("Problem running liquibase engine", e);
            if (DATABASE_LIQUIBASE_EXCEPTION_IS_FATAL.isTrue()) {
                throw new RuntimeException(e);
            }
        }
	}

	/**
	 * This object is stateful, be careful!
	 * @return
	 */
	private Liquibase getLiquibase()
	{
		Liquibase liquibase = null;
		try
		{
			liquibase = new Liquibase(DATABASE_LIQUIBASE_SOURCE.value(), resourceAccessor, getDatabase());
		}
		catch (LiquibaseException e)
		{
			logger.error("Failed to setup liquibase engine", e);
			throw new RuntimeException(e);
		}

		String liquibaseLogLevel = DATABASE_LIQUIBASE_LOGLEVEL.value("INFO").toUpperCase();
		logger.info("Setting liquibase log level to {}", liquibaseLogLevel);
		LogFactory.getInstance().setDefaultLoggingLevel(LogLevel.valueOf(liquibaseLogLevel));
		return liquibase;
	}

	private Database getDatabase()
	{
		try
		{
			return DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(dataSource.getConnection()));
		}
		catch (DatabaseException|SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * copied from liquibase.integration.commandline.Main.getOutputWriter()
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private Writer getOutputWriter() {
		String charsetName = LiquibaseConfiguration.getInstance().getConfiguration(GlobalConfiguration.class).getOutputEncoding();

		try
		{

			return new OutputStreamWriter(System.out, charsetName);
		}
		catch (UnsupportedEncodingException e)
		{
			logger.error("Unable to create output writer", e);
			throw new RuntimeException(e);
		}
	}

}
