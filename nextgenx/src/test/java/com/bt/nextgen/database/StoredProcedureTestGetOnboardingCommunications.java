package com.bt.nextgen.database;

import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.sql.*;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

public class StoredProcedureTestGetOnboardingCommunications {

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

    @Test
    public void testCommunicationDetailsReturnedForTheDurationRequested() throws SQLException {
        String communicationId1 = "commId44455";
        String communicationId2 = "commId44444";
        String communicationId3 = "commId66666";
        String applicationId = "123";
        String gcmPan = "GCM_PAN_ID_1";
        String communicationStatus = "emailSent";
        String emailAddress = "test@test.com";
        Timestamp fromTimestamp = Timestamp.valueOf("2014-10-14 00:00:00.0");
        Timestamp initiationTime1 = Timestamp.valueOf("2014-10-18 12:00:00.0");
        Timestamp initiationTime2 = Timestamp.valueOf("2014-10-19 18:00:00.0");
        Timestamp initiationTime3 = Timestamp.valueOf("2014-10-09 18:00:00.0");
        Timestamp toTimestamp = Timestamp.valueOf("2014-10-20 00:00:00.0");
        String trackingId = "TRACKING_ID";
        String failureMsg = "NO FAILURE";

        //3 records are inserted in onboarding_communication table for 3 different timestamp values
        DatabaseTestUtil.callSetCommunicationStoredProcedure(connection, communicationId1, applicationId, gcmPan, communicationStatus, emailAddress, initiationTime1, trackingId, failureMsg);
        DatabaseTestUtil.callSetCommunicationStoredProcedure(connection, communicationId2, applicationId, gcmPan, communicationStatus, emailAddress, initiationTime2, trackingId, failureMsg);
        DatabaseTestUtil.callSetCommunicationStoredProcedure(connection, communicationId3, applicationId, gcmPan, communicationStatus, emailAddress, initiationTime3, trackingId, failureMsg);

        //Result set will contain only 2 records for which the timestamp fall between the fromTimeStamp and toTimestamp
        ResultSet resultSet = getCommunicationResultSet(fromTimestamp, toTimestamp);

        ArrayList<String> resultCommunicationIDs = new ArrayList<>();
        while (resultSet.next()) {
            resultCommunicationIDs.add(resultSet.getString("communication_id"));
        }

        Assert.assertEquals(2, resultCommunicationIDs.size());
        assertThat(resultCommunicationIDs, hasItems(communicationId1, communicationId2));
        resultSet.close();
    }

    private ResultSet getCommunicationResultSet(Timestamp fromTimestamp, Timestamp toTimestamp) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("BEGIN communication.get_onboarding_communications(?,?,?); END;");
        callableStatement.setTimestamp(1, fromTimestamp);
        callableStatement.setTimestamp(2, toTimestamp);
        callableStatement.registerOutParameter(3, OracleTypes.CURSOR);
        callableStatement.execute();
        return ((OracleCallableStatement) callableStatement).getCursor(3);
    }

    @Test
    public void testExceptionIsThrownWhenFromTimestampIsNull() throws SQLException {
        Timestamp toTimestamp = Timestamp.valueOf("2014-10-20 00:00:00.0");
        DatabaseTestUtil.setExpectedSQLException(exception, 20100, "FROM OR TO TIMESTAMP CANNOT BE NULL");
        getCommunicationResultSet(null,toTimestamp);
    }

    @Test
    public void testExceptionIsThrownWhenToTimestampIsNull() throws SQLException {
        Timestamp fromTimestamp = Timestamp.valueOf("2014-10-20 00:00:00.0");
        DatabaseTestUtil.setExpectedSQLException(exception, 20100, "FROM OR TO TIMESTAMP CANNOT BE NULL");
        getCommunicationResultSet(fromTimestamp,null);
    }

    @AfterClass
    public static void tearDown() throws SQLException {
        if (connection != null) {
            DatabaseTestUtil.closeTestDB(connection);
            connection.close();
        }
    }
}
