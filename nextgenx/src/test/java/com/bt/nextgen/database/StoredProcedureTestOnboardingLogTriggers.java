package com.bt.nextgen.database;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class StoredProcedureTestOnboardingLogTriggers {

    private static final int SUCCESS = 1;
    protected static Connection connection;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void setup() throws Exception {
        connection = DatabaseTestUtil.initTestDB();
        Statement statement = connection.createStatement();
        statement.execute("INSERT INTO ONBOARDING_APPLICATION (ID) VALUES ('123')");
        statement
                .execute("INSERT INTO ONBOARDING_PARTY (ONBOARDING_PARTY_SEQ,ONBOARDING_APPLICATION_ID, GCM_PAN) VALUES (1,'123','GCM_PAN_ID_1')");
        statement.close();
    }

    @After
    public void cleanOnboardingLog() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("TRUNCATE TABLE ONBOARDING_LOG");
        statement.close();
    }

    @AfterClass
    public static void tearDown() throws SQLException {
        DatabaseTestUtil.closeTestDB(connection);
    }

    @Test
    public void testOnboardingApplicationTriggerFires_insertsIntoLog() throws SQLException {

        ResultSet result = getOnboardingLogRows();
        Assert.assertFalse(result.next());

        String applicationId = "123";
        String applicationStatus = "SomeFailureStatus";
        String failureMessage = "There was a failure";

        int status = DatabaseTestUtil.callSetOnboardingApplicationStatusStoredProcedure(connection, applicationId,
                applicationStatus, failureMessage);
        Assert.assertEquals(status, SUCCESS);

        result = getOnboardingLogRows();
        Assert.assertTrue(result.next());

        Assert.assertEquals(applicationId, result.getString("ONBOARDING_APPLICATION_ID"));
        Assert.assertEquals(applicationStatus, result.getString("STATUS"));
        Assert.assertEquals(failureMessage, result.getString("FAILURE_MESSAGE"));

        Assert.assertFalse(result.next());

        result.getStatement().close();
        result.close();
    }

    @Test
    public void testOnboardingPartyTriggerFires_insertsIntoLog() throws SQLException {

        ResultSet result = getOnboardingLogRows();
        Assert.assertFalse(result.next());

        String applicationId = "123";
        String gcmId = "GCM_PAN_ID_1";
        String applicationStatus = "SomeFailureStatus";
        String failureMessage = "There was a failure";

        int status = DatabaseTestUtil.callSetOnboardingPartyStatusStoredProcedure(connection, applicationId, gcmId,
                applicationStatus, failureMessage);
        Assert.assertEquals(status, SUCCESS);

        result = getOnboardingLogRows();
        Assert.assertTrue(result.next());

        Assert.assertEquals(applicationId, result.getString("ONBOARDING_APPLICATION_ID"));
        Assert.assertEquals(gcmId, result.getString("CLIENT_GCM_ID"));
        Assert.assertEquals(applicationStatus, result.getString("STATUS"));
        Assert.assertEquals(failureMessage, result.getString("FAILURE_MESSAGE"));

        Assert.assertFalse(result.next());

        result.getStatement().close();
        result.close();
    }

    private ResultSet getOnboardingLogRows() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT * FROM ONBOARDING_LOG");
        return result;
    }
}