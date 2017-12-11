package com.bt.nextgen.database;

import org.junit.*;
import org.junit.rules.ExpectedException;

import java.sql.*;
import java.util.Calendar;

import static junit.framework.Assert.assertEquals;

public class StoredProcedureTestSetOnboardingCommunication {

    private static final int SUCCESS = 1;
    protected static Connection connection;
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void setup() throws Exception {
        connection = DatabaseTestUtil.initTestDB();
        Statement statement = connection.createStatement();
        statement.execute("INSERT INTO ONBOARDING_APPLICATION (ID) VALUES ('123')");
        statement.execute("INSERT INTO ONBOARDING_PARTY (ONBOARDING_PARTY_SEQ,ONBOARDING_APPLICATION_ID, GCM_PAN) VALUES (1,'123','GCM_PAN_ID_1')");
        statement.close();
    }

    @AfterClass
    public static void tearDown() throws SQLException {
        DatabaseTestUtil.closeTestDB(connection);
    }

    @Test
    public void testCommunicationRecordIsInserted() throws SQLException {
        String communicationId = "commId23232";
        String applicationId = "123";
        String gcmPan = "GCM_PAN_ID_1";
        String communicationStatus = "emailSent";
        String emailAddress = "test@test.com";
        Timestamp initiationTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
        String trackingId = "TRACKING_ID";
        String failureMsg = "NO FAILURE";

        int status;

        status = DatabaseTestUtil.callSetCommunicationStoredProcedure(connection, communicationId, applicationId, gcmPan, communicationStatus, emailAddress, initiationTime, trackingId, failureMsg);

        ResultSet result = DatabaseTestUtil.getCommunicationDetails(connection,communicationId);

        Assert.assertEquals(status, SUCCESS);
        Assert.assertTrue(result.next());
        Assert.assertEquals(communicationStatus, result.getString("STATUS"));
        Assert.assertEquals(emailAddress, result.getString("EMAIL_ADDRESS"));
        Assert.assertEquals(trackingId, result.getString("TRACKING_ID"));
        Assert.assertEquals(failureMsg, result.getString("FAILURE_MESSAGE"));
        Assert.assertEquals(initiationTime, result.getTimestamp("COMMUNICATION_INITIATION_TIME"));
        Assert.assertNotNull(result.getTimestamp("LAST_MODIFIED_DATE"));
        assertEquals(Properties.getOracleUserName().toLowerCase(), result.getString("LAST_MODIFIED_ID").toLowerCase());

        result.getStatement().close();
        result.close();
    }

    @Test
    public void testExceptionIsThrownWhenCommunicationIdExists() throws SQLException {
        String communicationId = "commId23";
        String applicationId = "123";
        String gcmPan = "GCM_PAN_ID_1";
        String communicationStatus = "emailSent";
        String updatedCommunicationStatus = "emailNotificationFailed";
        String emailAddress = "test@test.com";
        Timestamp initiationTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
        String trackingId = "TRACKING_ID";
        String failureMsg = "NO FAILURE";
        int status;

        status = DatabaseTestUtil.callSetCommunicationStoredProcedure(connection, communicationId, applicationId, gcmPan, communicationStatus, emailAddress, initiationTime, trackingId, failureMsg);

        Assert.assertEquals(status, SUCCESS);

        DatabaseTestUtil.setExpectedSQLException(exception, 1, "unique constraint");
        DatabaseTestUtil.callSetCommunicationStoredProcedure(connection, communicationId, applicationId, gcmPan, updatedCommunicationStatus, emailAddress, initiationTime, trackingId, failureMsg);
    }

    @Test
    public void testExceptionIsThrownWhenApplicationIdIsNullOrEmpty() throws SQLException {

        DatabaseTestUtil.setExpectedSQLException(exception, 20100, "APPLICATION ID IS NULL OR EMPTY");
        DatabaseTestUtil.callSetCommunicationStoredProcedure(connection,
                "commId232", null, "GCM_PAN_ID_1", "emailSent", "test@test.com", new Timestamp(Calendar.getInstance().getTimeInMillis()), "TRACKING_ID", "NO FAILURE");
    }

    @Test
    public void testExceptionIsThrownWhenCommunicationIdIsNullOrEmpty() throws SQLException {

        DatabaseTestUtil.setExpectedSQLException(exception, 20101, "COMMUNICATION ID IS NULL OR EMPTY");
        DatabaseTestUtil.callSetCommunicationStoredProcedure(connection,
                "", "123", "GCM_PAN_ID_1", "emailSent", "test@test.com", new Timestamp(Calendar.getInstance().getTimeInMillis()), "TRACKING_ID", "NO FAILURE");
    }

    @Test
    public void testExceptionIsThrownWhenGCMPanIsNullOrEmpty() throws SQLException {

        DatabaseTestUtil.setExpectedSQLException(exception, 20102, "GCM PAN ID IS NULL OR EMPTY");
        DatabaseTestUtil.callSetCommunicationStoredProcedure(connection,
                "32432432", "123", null, "emailSent", "test@test.com", new Timestamp(Calendar.getInstance().getTimeInMillis()), "TRACKING_ID", "NO FAILURE");
    }

    @Test
    public void testExceptionIsThrownWhenEmailIdIsNull() throws SQLException {

        DatabaseTestUtil.setExpectedSQLException(exception, 20103, "EMAIL ADDRESS CANNOT BE NULL");
        DatabaseTestUtil.callSetCommunicationStoredProcedure(connection, "322432", "123", "GCM_PAN_ID_1", "emailSent", null, new Timestamp(Calendar.getInstance().getTimeInMillis()), "TRACKING_ID", "NO FAILURE");
    }

    @Test
    public void testExceptionIsThrownWhenCommunicationInitiationTimeIsNull() throws SQLException {

        DatabaseTestUtil.setExpectedSQLException(exception, 20104, "COMMUNICATION INITIATION TIMESTAMP CANNOT BE NULL");
        DatabaseTestUtil.callSetCommunicationStoredProcedure(connection, "322432", "123", "GCM_PAN_ID_1", "emailSent", "test@test.com", null, "TRACKING_ID", "NO FAILURE");
    }
}
