package com.bt.nextgen.core.util;

import com.bt.nextgen.database.DatabaseTestUtil;
import org.junit.Test;

import java.sql.Connection;

/**
 * The name of this class is given as DatabaseManagerTestLiquibaseScript to avoid getting
 * this junit run in other maven profiles.
 *
 * This junit will be run only for maven profile 'database'.
 */
public class DatabaseManagerTestLiquibaseScriptOracle extends DatabaseManagerTestLiquibaseScript {

    @Test
    public void testDatabaseSetupIsProper_WhenRunningLiquibaseScriptOnAnEmptyOracleDatabaseInTestDataContext() throws Exception {

        Connection connection = DatabaseTestUtil.getOracleConnection();

        DatabaseTestUtil.cleanupSchema(connection);

        runLiquibaseAndAssertItIsRun(connection, DatabaseTestUtil.getOracleDataSource());

        DatabaseTestUtil.cleanupSchema(connection);
        connection.close();
    }

    @Test
    public void testDatabaseSetupIsProper_WhenRunningLiquibaseScriptOnAnOracleDatabaseBackedUpFromTestEnvironment() throws Exception {

        Connection connection = DatabaseTestUtil.getOracleConnection();

        DatabaseTestUtil.cleanupSchema(connection);
        DatabaseTestUtil.createDatabaseFromBackup(connection, "dev1");

        runLiquibaseAndAssertItIsRun(connection, DatabaseTestUtil.getOracleDataSource());

        DatabaseTestUtil.cleanupSchema(connection);
        connection.close();
    }

}