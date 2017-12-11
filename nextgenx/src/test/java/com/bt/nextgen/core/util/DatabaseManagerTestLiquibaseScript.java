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
public class DatabaseManagerTestLiquibaseScript {




    protected void runLiquibaseAndAssertItIsRun(Connection connection, SimpleDriverDataSource dataSource) throws SQLException, ClassNotFoundException, LiquibaseException {
        int initialCount = getLiquibaseChangeSetsCount(connection);

        DatabaseTestUtil.runLiquibase(dataSource);

        int countAfterLiquibaseIsRun = getLiquibaseChangeSetsCount(connection);

        assertTrue(countAfterLiquibaseIsRun > initialCount);
    }

    private int getLiquibaseChangeSetsCount(Connection connection) throws SQLException {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS CHANGESETS_COUNT FROM DATABASECHANGELOG");

            int changeSetsCount = -1;

            if (resultSet.next()) {
                changeSetsCount = resultSet.getInt("CHANGESETS_COUNT");
            }

            return changeSetsCount;
        } catch (SQLException e) {
            return 0;
        }
    }
}