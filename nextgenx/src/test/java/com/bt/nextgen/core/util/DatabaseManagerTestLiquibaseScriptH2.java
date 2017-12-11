package com.bt.nextgen.core.util;

import com.bt.nextgen.database.DatabaseTestUtil;
import liquibase.exception.LiquibaseException;
import org.junit.Test;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import java.sql.*;

import static junit.framework.Assert.assertTrue;

/**
 * The name of this class is given as DatabaseManagerTestLiquibaseScript to avoid getting
 * this junit run in other maven profiles.
 *
 * This junit will be run only for maven profile 'database'.
 */
public class DatabaseManagerTestLiquibaseScriptH2 extends DatabaseManagerTestLiquibaseScript {

    @Test
    public void testDatabaseSetupIsProper_WhenRunningLiquibaseScriptOnAnEmptyH2DatabaseInTestDataContext() throws Exception {
        Driver driver = new org.h2.Driver();

        String h2Url, h2Username, h2Password;

        h2Url = com.bt.nextgen.database.Properties.get("h2.db.url");
        h2Username = com.bt.nextgen.database.Properties.get("h2.db.username");
        h2Password = com.bt.nextgen.database.Properties.get("h2.db.password");

        //Get H2 connection
        Connection h2connection = DatabaseTestUtil.getConnection("org.h2.Driver", h2Url, h2Username, h2Password);
        //Clean H2 schema
        Statement statement = h2connection.createStatement();
        statement.execute("DROP ALL OBJECTS");

        SimpleDriverDataSource dataSource = new SimpleDriverDataSource(driver, h2Url, h2Username, h2Password);

        runLiquibaseAndAssertItIsRun(h2connection, dataSource);

        h2connection.close();
    }

}