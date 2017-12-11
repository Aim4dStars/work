package com.bt.nextgen.database;

import com.bt.nextgen.core.util.DatabaseManager;
import liquibase.exception.LiquibaseException;
import oracle.jdbc.OracleDriver;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.rules.ExpectedException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

public class DatabaseTestUtil {
    public static final String APPLICATION_DATA_XML = "application_data_xml";
    public static final String GCM_PAN = "gcm_pan";
    public static final String STATUS = "status";
    public static final String FAILURE_MSG = "failure_msg";
    public static final String APPLICATION_ID = "application_id";
    public static final String APPLICATION_STATUS = "application_status";
    public static final String PARTY_STATUS = "party_status";
    public static final String AVALOQ_ORDER_ID = "avaloq_order_id";
    public static final String COMMUNICATION_ID = "communication_id";
    public static final String COMMUNICATION_STATUS = "communication_status";
    public static final String EMAIL_ADDRESS = "email_address";
    public static final String INITIATION_TIME = "initiation_time";
    public static final String TRACKING_ID = "tracking_id";

    public static final int INVALID_XML_DOCUMENT_EXCEPTION = 31154;
    public static final String XML_INPUT_OB_CORRELATION_IDS_NAMESPACE = "ns://private.btfin.com/Panorama/Onboarding/IDUpdate/V1_0";
    static final String INVALID_XML_DOCUMENT_MSG = "invalid XML document";
    private static final String CALL_SET_ONBOARDING_CORRELATION_IDS = "{call onboarding_id_updates.set_onboarding_correlation_ids(:" + APPLICATION_ID + ",:" + APPLICATION_DATA_XML + ",:" + STATUS + ")}";
    private static final String CALL_SET_ONBOARDING_COMMUNICATION = "{call communication.set_onboarding_communication(:" + COMMUNICATION_ID + ",:" + APPLICATION_ID + ",:" + GCM_PAN + ",:" + COMMUNICATION_STATUS + ",:" + EMAIL_ADDRESS + ",:" + INITIATION_TIME + ",:" + TRACKING_ID + ",:" + FAILURE_MSG + ",:" + STATUS + ")}";
    private static final String CALL_SET_ONBOARDING_APPLICATION_STATUS = "{call onboarding_status_updates.set_onboarding_app_status(:" + APPLICATION_ID + ",:" + APPLICATION_STATUS + ",:" + FAILURE_MSG + ",:" + STATUS + ")}";
    private static final String CALL_SET_ONBOARDING_PARTY_STATUS = "{call onboarding_status_updates.set_onboarding_party_status(:" + APPLICATION_ID + ",:" + GCM_PAN + ",:" + PARTY_STATUS + ",:" + FAILURE_MSG + ",:" + STATUS + ")}";
    private static final String CALL_SET_ONBOARDING_COMMUNICATION_STATUS = "{call onboarding_status_updates.set_onboarding_comm_status(:" + COMMUNICATION_ID + ",:" + COMMUNICATION_STATUS + ",:" + FAILURE_MSG + ",:" + STATUS + ")}";

    private static final String CLEANUP_SCHEMA = "BEGIN" +
            "  FOR r1 IN ( SELECT 'DROP ' || object_type || ' ' || object_name || DECODE ( object_type, 'TABLE', ' CASCADE CONSTRAINTS PURGE' ) AS v_sql" +
            "                FROM user_objects" +
 "               WHERE object_type IN ( 'TABLE', 'VIEW', 'PACKAGE', 'TYPE', 'PROCEDURE', 'FUNCTION', 'SEQUENCE' )"
            +
            "               ORDER BY object_type," +
            "                        object_name ) LOOP" +
            "    EXECUTE IMMEDIATE r1.v_sql;" +
            "  END LOOP;" +
            "END;";


    public static Connection initTestDB() throws Exception {
        cleanupSchema();
        //stored procedures do not need test data. Hence the context is changed.
        runLiquibase(getOracleDataSource(), "!test-data");
        return getOracleConnection();
    }

    public static void cleanupSchema() throws Exception {
        Connection connection = getOracleConnection();
        cleanupSchema(connection);
        connection.close();
    }

    public static void runLiquibase(DataSource dataSource, String context) throws LiquibaseException {
        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.setDataSource(dataSource);

        java.util.Properties properties = Properties.all();

        properties.put("database.liquibase.contexts", context);
        properties.put("database.liquibase.automation.enabled", "true");
        properties.put("database.liquibase.exception-is-fatal", "true");

        databaseManager.run();
    }

    public static void runLiquibase(DataSource dataSource) throws LiquibaseException {
        runLiquibase(dataSource, "test-data");
    }

    public static void closeTestDB(Connection connection) throws SQLException {

        if (connection != null) {
            cleanupSchema(connection);
            connection.close();
        }
    }

    public static void cleanupSchema(Connection connection) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall(CLEANUP_SCHEMA);
        callableStatement.execute();
        callableStatement.close();
    }

    public static int callSetOnboardingApplicationStatusStoredProcedure(Connection connection, String applicationId, String applicationStatus, String failureMsg) throws SQLException {

        CallableStatement statement = connection.prepareCall(CALL_SET_ONBOARDING_APPLICATION_STATUS);
        //Note: Order of setting the values is important!
        statement.setString(APPLICATION_ID, applicationId);
        statement.setString(APPLICATION_STATUS, applicationStatus);
        statement.setString(FAILURE_MSG, failureMsg);
        statement.registerOutParameter(STATUS, Types.INTEGER);
        statement.execute();
        int status = statement.getInt(STATUS);
        statement.close();
        return status;
    }

    public static int callSetOnboardingPartyStatusStoredProcedure(Connection connection, String applicationId, String gcmPan, String partyStatus, String failureMsg) throws SQLException {

        CallableStatement statement = connection.prepareCall(CALL_SET_ONBOARDING_PARTY_STATUS);
        //Note: Order of setting the values is important!
        statement.setString(APPLICATION_ID, applicationId);
        statement.setString(GCM_PAN, gcmPan);
        statement.setString(PARTY_STATUS, partyStatus);
        statement.setString(FAILURE_MSG, failureMsg);
        statement.registerOutParameter(STATUS, Types.INTEGER);
        statement.execute();
        int status = statement.getInt(STATUS);
        statement.close();
        return status;
    }

    public static int callSetOnboardingCommunicationStatusStoredProcedure(Connection connection, String communicationId, String communicationStatus, String failureMsg) throws SQLException {

        CallableStatement statement = connection.prepareCall(CALL_SET_ONBOARDING_COMMUNICATION_STATUS);
        //Note: Order of setting the values is important!
        statement.setString(COMMUNICATION_ID, communicationId);
        statement.setString(COMMUNICATION_STATUS, communicationStatus);
        statement.setString(FAILURE_MSG, failureMsg);
        statement.registerOutParameter(STATUS, Types.INTEGER);
        statement.execute();
        int status = statement.getInt(STATUS);
        statement.close();
        return status;
    }

    public static int callSetCorrelationIdsStoredProcedure(Connection connection, String applicationId, String xml_data) throws SQLException {

        CallableStatement statement = connection.prepareCall(CALL_SET_ONBOARDING_CORRELATION_IDS);
        //Note: Order of setting the values is important!
        statement.setString(APPLICATION_ID, applicationId);
        statement.setString(APPLICATION_DATA_XML, xml_data);
        statement.registerOutParameter(STATUS, Types.INTEGER);
        statement.execute();
        int status = statement.getInt(STATUS);
        statement.close();
        return status;
    }

    public static int callSetCommunicationStoredProcedure(Connection connection, String communicationId, String applicationId, String gcmPan, String communicationStatus, String emailAddress, Timestamp initiationTime, String trackingId, String failureMsg) throws SQLException {

        CallableStatement statement = connection.prepareCall(CALL_SET_ONBOARDING_COMMUNICATION);
        //Note: Order of setting the values is important!
        statement.setString(COMMUNICATION_ID, communicationId);
        statement.setString(APPLICATION_ID, applicationId);
        statement.setString(GCM_PAN, gcmPan);
        statement.setString(COMMUNICATION_STATUS, communicationStatus);
        statement.setString(EMAIL_ADDRESS, emailAddress);
        statement.setTimestamp(INITIATION_TIME, initiationTime);
        statement.setString(TRACKING_ID, trackingId);
        statement.setString(FAILURE_MSG, failureMsg);
        statement.registerOutParameter(STATUS, Types.INTEGER);
        statement.execute();
        int status = statement.getInt(STATUS);
        statement.close();
        return status;
    }

    public static void verifyApplicationCorrelationIdIsUpdated(Connection connection, String applicationCorrelationId, String avaloqOrderId) throws SQLException {

        Statement statement = connection.createStatement();
        ResultSet result = getApplicationDetails(connection, applicationCorrelationId);
        Assert.assertTrue(result.next());
        Assert.assertEquals(avaloqOrderId, result.getString(AVALOQ_ORDER_ID));
        statement.close();
    }

    public static void verifyApplicationStatusIsUpdated(Connection connection, String applicationCorrelationId, String expectedStatus, String expectedFailureMessage) throws SQLException {

        ResultSet result = getApplicationDetails(connection, applicationCorrelationId);
        Assert.assertTrue(result.next());
        Assert.assertEquals(expectedStatus, result.getString("STATUS"));
        Assert.assertEquals(expectedFailureMessage, result.getString("FAILURE_MESSAGE"));
        result.close();
    }

    public static void verifyApplicationCorrelationIdIsNotUpdated(Connection connection, String applicationCorrelationId) throws SQLException {
        ResultSet result = getApplicationDetails(connection, applicationCorrelationId);
        Assert.assertTrue(result.next());
        Assert.assertNull(result.getString(AVALOQ_ORDER_ID));
        result.getStatement().close();
    }

    public static void verifyAccountCorrelationIdIsNotUpdated(Connection connection, String applicationCorrelationId, int accountCorrelationSeq) throws SQLException {

        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT ACCOUNT_NUMBER FROM ONBOARDING_ACCOUNT WHERE ONBOARDING_APPLICATION_ID = '" + applicationCorrelationId +
                "' AND ONBOARDING_ACCOUNT_SEQ = " + accountCorrelationSeq);
        Assert.assertTrue(result.next());
        Assert.assertNull(result.getString("ACCOUNT_NUMBER"));
        statement.close();
    }

    public static void verifyPartyCorrelationIdIsNotUpdated(Connection connection, String applicationCorrelationId, int partyCorrelationSeq) throws SQLException {

        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT GCM_PAN FROM ONBOARDING_PARTY WHERE ONBOARDING_APPLICATION_ID = '" + applicationCorrelationId +
                "' AND ONBOARDING_PARTY_SEQ = " + partyCorrelationSeq);
        Assert.assertTrue(result.next());
        Assert.assertNull(result.getString("GCM_PAN"));
        statement.close();
    }

    public static Connection getOracleConnection() throws Exception {
        if (Properties.getOracleUserName() == null) {
            throw new Exception("DB PROPERTIES ARE NOT SET");
        }
        return getConnection("oracle.jdbc.driver.OracleDriver",Properties.getOracleUrl(), Properties.getOracleUserName(), Properties.getOraclePassword());
    }

    public static void setXMLValidationExpectedException(ExpectedException exception) {
        setExpectedSQLException(exception, INVALID_XML_DOCUMENT_EXCEPTION, INVALID_XML_DOCUMENT_MSG);
    }

    public static void setExpectedSQLException(ExpectedException exception, int expectedErrorCode, String expectedMessage) {
        exception.expect(SQLException.class);
        exception.expect(hasProperty("errorCode", is(expectedErrorCode)));
        if (expectedMessage != null) {
            exception.expectMessage(CoreMatchers.containsString(expectedMessage));
        }
    }

    public static void verifyPartyCorrelationIdIsUpdated(Connection connection, String applicationCorrelationId, String partyCorrelationSeq, String gcmPan) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT GCM_PAN FROM ONBOARDING_PARTY WHERE ONBOARDING_APPLICATION_ID = '" + applicationCorrelationId +
                "' AND ONBOARDING_PARTY_SEQ = '" + partyCorrelationSeq + "'");
        Assert.assertTrue(result.next());
        Assert.assertEquals(gcmPan, result.getString("GCM_PAN"));
    }

    public static void verifyPartyStatusIsUpdated(Connection connection, String applicationId, String gcmPan, String expectedStatus, String expectedFailureMessage) throws SQLException {
        ResultSet result = getPartyDetails(connection, applicationId, gcmPan);
        Assert.assertTrue(result.next());
        Assert.assertEquals(expectedStatus, result.getString("STATUS"));
        Assert.assertEquals(expectedFailureMessage, result.getString("FAILURE_MESSAGE"));
        result.getStatement().close();
        result.close();
    }

    public static void verifyCommunicationStatusIsUpdated(Connection connection, String communicationId, String expectedStatus, String expectedFailureMessage) throws SQLException {
        ResultSet result = getCommunicationDetails(connection, communicationId);
        Assert.assertTrue(result.next());
        Assert.assertEquals(expectedStatus, result.getString("STATUS"));
        Assert.assertEquals(expectedFailureMessage, result.getString("FAILURE_MESSAGE"));
        result.getStatement().close();
        result.close();
    }

    public static ResultSet getApplicationDetails(Connection connection, String applicationCorrelationId) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery("SELECT AVALOQ_ORDER_ID, STATUS, FAILURE_MESSAGE, LAST_MODIFIED_DATE, LAST_MODIFIED_ID FROM ONBOARDING_APPLICATION WHERE ID = '" + applicationCorrelationId + "'");
    }

    public static ResultSet getPartyDetails(Connection connection, String applicationId, String gcmPan) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(
                "SELECT ONBOARDING_PARTY_SEQ, STATUS, FAILURE_MESSAGE, LAST_MODIFIED_ID, LAST_MODIFIED_DATE " +
                    "FROM ONBOARDING_PARTY " +
                    "WHERE GCM_PAN = '" + gcmPan + "'" +
                        " AND ONBOARDING_APPLICATION_ID = '" + applicationId + "'");
        return result;
    }

    public static ResultSet getAccountDetails(Connection connection, String applicationId, String accountCorrelationSeq) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery("SELECT ONBOARDING_ACCOUNT_SEQ, ONBOARDING_APPLICATION_ID, ACCOUNT_NUMBER, LAST_MODIFIED_DATE, LAST_MODIFIED_ID " +
                "FROM ONBOARDING_ACCOUNT WHERE ONBOARDING_APPLICATION_ID = '" + applicationId + "'");
    }

    public static ResultSet getCommunicationDetails(Connection connection, String communicationId) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT COMMUNICATION_ID," +
                "ONBOARDING_APPLICATION_ID," +
                "GCM_PAN," +
                "STATUS," +
                "EMAIL_ADDRESS," +
                "COMMUNICATION_INITIATION_TIME, " +
                "TRACKING_ID, " +
                "FAILURE_MESSAGE, " +
                "LAST_MODIFIED_ID, " +
                "LAST_MODIFIED_DATE " +
                "FROM " +
                "     ONBOARDING_COMMUNICATION " +
                "WHERE " +
                "     COMMUNICATION_ID = '" + communicationId + "'");
        return result;
    }


    public static void createDatabaseFromBackup(Connection connection, String environment) throws SQLException, IOException {

        cleanupSchema(connection);
        Resource resource = new ClassPathResource("/scripts/" + environment + "_export.sql");
        SQLFileReaderUtil sqlFileReaderUtil = new SQLFileReaderUtil();
        ArrayList<String> queries = sqlFileReaderUtil.createQueries(resource.getFile());
        Statement statement = connection.createStatement();
        for (String query : queries) {
            try {
                statement.execute(query);
                System.out.println("Query executed: " + query);
            } catch (SQLException e) {
                System.out.println("Exception in query :" + query);
                e.printStackTrace();
                throw e;

            }
        }
        statement.close();
        connection.commit();
        System.out.println("############## Database created using the backup from " + environment + "############## ");
    }

    public static Connection getConnection(String driverClassName, String url, String username, String password) throws ClassNotFoundException, SQLException {
        Class.forName(driverClassName);
        return DriverManager.getConnection(url, username, password);
    }

    public static SimpleDriverDataSource getOracleDataSource() throws Exception {
        //SET ORACLE DB PROPERTIES FIRST.
        if (Properties.getOracleUserName() == null) {
            throw new Exception("DB properties not set");
        }
        Driver driver = new OracleDriver();
        return new SimpleDriverDataSource(driver, Properties.getOracleUrl(), Properties.getOracleUserName(), Properties.getOraclePassword());
    }

    public static void verifyLastModifiedIdAndLastModifiedDateAreUpdated(ResultSet result) throws SQLException {
        result.next();

        assertNotNull(result.getTimestamp("LAST_MODIFIED_DATE"));
        assertEquals(Properties.getOracleUserName().toLowerCase(), result.getString("LAST_MODIFIED_ID").toLowerCase());

        result.getStatement().close();
        result.close();
    }

    static void verifyAccountCorrelationIdIsUpdated(Connection connection, String applicationCorrelationId, String accountCorrelationSeq, String accountNumber) throws SQLException {

        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT ACCOUNT_NUMBER FROM ONBOARDING_ACCOUNT WHERE ONBOARDING_APPLICATION_ID = '" + applicationCorrelationId +
                "' AND ONBOARDING_ACCOUNT_SEQ = '" + accountCorrelationSeq + "'");
        Assert.assertTrue(result.next());
        Assert.assertEquals(accountNumber, result.getString("ACCOUNT_NUMBER"));
        statement.close();
    }
}
