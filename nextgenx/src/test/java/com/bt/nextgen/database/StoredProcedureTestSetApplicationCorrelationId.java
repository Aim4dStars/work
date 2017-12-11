package com.bt.nextgen.database;

import org.junit.*;
import org.junit.rules.ExpectedException;

import java.sql.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

public class StoredProcedureTestSetApplicationCorrelationId {

    private static final int APPLICATION_ID_CANNOT_BE_FOUND_EXCEPTION = 20100;
    private static final int APPLICATION_ID_AND_AVALOQ_ORDER_ID_CANNOT_BE_NULL_EXCEPTION = 20101;
    private static final int XML_NAMESPACE_MISSING_EXCEPTION = 30937;
    private static final String AVALOQ_ORDER_ID_AND_APPLICATION_ID_CANNOT_BE_NULL_MSG = "AVALOQ ORDER ID AND APPLICATION ID CANNOT BE NULL OR EMPTY";


    protected static Connection connection;

    private static String avaloq_xml_element;
    private static String application_xml_element;
    private static String base_xml_1;
    private static String base_xml_1_without_namespace;
    private static String base_xml_2;
    private static String proper_application_id_avaloq_id;
    private static String application_id_not_found;
    private static String application_id_empty;
    private static String avaloq_id_empty;
    private static String application_id_avaloq_id_empty;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void init() throws Exception {
        connection = DatabaseTestUtil.initTestDB();
    }

    @AfterClass
    public static void closeTestDB() throws SQLException {
        DatabaseTestUtil.closeTestDB(connection);
    }

    @Before
    public void setup() throws SQLException, ClassNotFoundException  {

        Statement statement = connection.createStatement();
        statement.execute("INSERT INTO ONBOARDING_APPLICATION (ID) VALUES ('123')");
        statement.close();
        base_xml_1_without_namespace = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "                     <application>";
        base_xml_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "        <application xmlns=\"" + DatabaseTestUtil.XML_INPUT_OB_CORRELATION_IDS_NAMESPACE + "\">";
        base_xml_2 = "                     <accounts>" +
                "                           <account>" +
                "                               <account_correlation_seq>1</account_correlation_seq>" +
                "                               <account_number>234234324</account_number>" +
                "                           </account>" +
                "                       </accounts>" +
                "                       <parties>" +
                "                           <party>" +
                "                               <party_correlation_seq>1</party_correlation_seq>" +
                "                               <gcm_pan>GCM PAN ID 1</gcm_pan>" +
                "                               <addresses>" +
                "                                   <address>" +
                "                                       <address_correlation_seq>1</address_correlation_seq>" +
                "                                       <avaloq_address_id>AV_ADD_ID_123</avaloq_address_id> " +
                "                                   </address>" +
                "                               </addresses>" +
                "                           </party>" +
                "                       </parties>" +
                "                     </application>";

        application_xml_element = "<application_correlation_id>123</application_correlation_id>";
        avaloq_xml_element = "<avaloq_order_id>333</avaloq_order_id>";

        proper_application_id_avaloq_id = application_xml_element + avaloq_xml_element;

        avaloq_id_empty = application_xml_element + "<avaloq_order_id></avaloq_order_id>";
        application_id_empty = "<application_correlation_id></application_correlation_id>" + avaloq_xml_element;
        application_id_avaloq_id_empty = "<application_correlation_id></application_correlation_id>" + "<avaloq_order_id></avaloq_order_id>";

        application_id_not_found = "<application_correlation_id>9999999999999</application_correlation_id>" + avaloq_xml_element;
    }

    @Test
    public void testAvaloqOrderIdIsUpdatedWhenSetCorrelationIdsSPIsCalled() throws SQLException {
        String applicationId = "123";
        String avaloqOrderId = "333";
        String proper_xml_data = base_xml_1 +
                proper_application_id_avaloq_id +
                base_xml_2;
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, proper_xml_data);
        DatabaseTestUtil.verifyApplicationCorrelationIdIsUpdated(connection, applicationId, avaloqOrderId);
    }

    @Test
    public void testLastModifiedDateAndLastModifiedIdAreUpdatedForApplication() throws SQLException {
        String applicationId = "123";
        String proper_xml_data = base_xml_1 +
                proper_application_id_avaloq_id +
                base_xml_2;


        Statement statement = connection.createStatement();
        statement.executeUpdate("UPDATE ONBOARDING_APPLICATION SET LAST_MODIFIED_ID = 'I_SHOULD_BE_CHANGED', LAST_MODIFIED_DATE = null WHERE ID = "+ applicationId);
        statement.close();

        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, proper_xml_data);

        ResultSet result = DatabaseTestUtil.getApplicationDetails(connection, applicationId);
        result.next();

        assertNull(result.getString("STATUS"));
        assertNull(result.getString("FAILURE_MESSAGE"));

        result.getStatement().close();
        result.close();
    }

    @Test
    public void testStatusAndFailureMessageAreResetForApplication() throws SQLException {
        String applicationId = "123";
        String proper_xml_data = base_xml_1 +
                proper_application_id_avaloq_id +
                base_xml_2;


        Statement statement = connection.createStatement();
        statement.executeUpdate("UPDATE ONBOARDING_APPLICATION SET STATUS = 'I_SHOULD_BE_RESET_TO_NULL', FAILURE_MESSAGE = 'I_SHOULD_BE_RESET_AS_WELL' WHERE ID = "+ applicationId);
        statement.close();

        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, proper_xml_data);

        ResultSet result = DatabaseTestUtil.getApplicationDetails(connection, applicationId);
        DatabaseTestUtil.verifyLastModifiedIdAndLastModifiedDateAreUpdated(result);
    }


    @Test
    public void testExceptionIsThrownWhenApplicationIdNotFound() throws SQLException {
        String application_id_not_found_xml = base_xml_1 + application_id_not_found + base_xml_2;
        String applicationId = "9999999999999";
        String expectedMsg = "ONBOARDING APPLICATION ID CANNOT BE FOUND. APPLICATION ID: " + applicationId;
        Statement stmt = connection.createStatement();
        stmt.executeQuery("DELETE ONBOARDING_PARTY WHERE ONBOARDING_APPLICATION_ID = '9999999999999'");
        stmt.executeQuery("DELETE ONBOARDING_ACCOUNT WHERE ONBOARDING_APPLICATION_ID = '9999999999999'");
        stmt.executeQuery("DELETE ONBOARDING_APPLICATION WHERE ID = '9999999999999'");
        DatabaseTestUtil.setExpectedSQLException(exception, APPLICATION_ID_CANNOT_BE_FOUND_EXCEPTION, expectedMsg);
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, application_id_not_found_xml);
    }

    @Test
    public void testExceptionIsThrownWhenAvaloqOrderIdIsNull() throws SQLException {
        String applicationId = "123";
        String avaloq_id_null_xml = base_xml_1 + application_xml_element + base_xml_2;
        DatabaseTestUtil.setXMLValidationExpectedException(exception);
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, avaloq_id_null_xml);
    }

    @Test
    public void testExceptionIsThrownWhenApplicationIdIsNull() throws SQLException {
        String applicationId = "123";
        String application_id_null_xml = base_xml_1 + avaloq_xml_element + base_xml_2;
        DatabaseTestUtil.setXMLValidationExpectedException(exception);
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, application_id_null_xml);
    }

    @Test
    public void testExceptionIsThrownWhenApplicationIdAndAvaloqOrderIdAreNull() throws SQLException {
        String applicationId = "123";
        String application_and_avaloq_id_null_xml = base_xml_1 + base_xml_2;
        DatabaseTestUtil.setXMLValidationExpectedException(exception);
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, application_and_avaloq_id_null_xml);
    }

    @Test
    public void testExceptionIsThrownWhenAvaloqOrderIdIsEmpty() throws SQLException {
        String applicationId = "123";
        String avaloq_id_empty_xml = base_xml_1 + avaloq_id_empty + base_xml_2;
        DatabaseTestUtil.setExpectedSQLException(exception, APPLICATION_ID_AND_AVALOQ_ORDER_ID_CANNOT_BE_NULL_EXCEPTION, AVALOQ_ORDER_ID_AND_APPLICATION_ID_CANNOT_BE_NULL_MSG);
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, avaloq_id_empty_xml);
    }

    @Test
    public void testExceptionIsThrownWhenApplicationIdIsEmpty() throws SQLException {
        String applicationId = "123";
        String application_id_empty_xml = base_xml_1 + application_id_empty + base_xml_2;
        DatabaseTestUtil.setExpectedSQLException(exception, APPLICATION_ID_AND_AVALOQ_ORDER_ID_CANNOT_BE_NULL_EXCEPTION, AVALOQ_ORDER_ID_AND_APPLICATION_ID_CANNOT_BE_NULL_MSG);
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, application_id_empty_xml);
    }

    @Test
    public void testExceptionIsThrownWhenApplicationIdAndAvaloqOrderIdAreEmpty() throws SQLException {
        String applicationId = "123";
        String application_id_avaloq_id_empty_xml = base_xml_1 + application_id_avaloq_id_empty + base_xml_2;
        DatabaseTestUtil.setExpectedSQLException(exception, APPLICATION_ID_AND_AVALOQ_ORDER_ID_CANNOT_BE_NULL_EXCEPTION, AVALOQ_ORDER_ID_AND_APPLICATION_ID_CANNOT_BE_NULL_MSG);
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, application_id_avaloq_id_empty_xml);
    }

    @Test
    public void testExceptionIsThrownWhenNamespaceIsNotProvided() throws SQLException {
        String applicationId = "123";
        String xml_without_namespace = base_xml_1_without_namespace + proper_application_id_avaloq_id + base_xml_2;
        DatabaseTestUtil.setExpectedSQLException(exception, XML_NAMESPACE_MISSING_EXCEPTION, "No schema definition for 'application' (namespace '') in parent '/'");
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, xml_without_namespace);
    }

    @After
    public void tearDown() throws SQLException {
        if (connection != null) {
            Statement statement = connection.createStatement();
            statement.execute("DELETE ONBOARDING_PARTY WHERE ONBOARDING_PARTY_SEQ='1' AND ONBOARDING_APPLICATION_ID='123'");
            statement.execute("DELETE ONBOARDING_ACCOUNT WHERE ONBOARDING_ACCOUNT_SEQ = '1' AND ONBOARDING_APPLICATION_ID='123'");
            statement.execute("DELETE ONBOARDING_APPLICATION WHERE ID = '123'");
            statement.close();
        }
    }
}
