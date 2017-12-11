package com.bt.nextgen.database;

import org.junit.*;
import org.junit.rules.ExpectedException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class StoredProcedureTestRegisterXMLSchemaForCorrelationIdsInput {

    private static final int SUCCESS_STATUS = 1;
    protected static Connection connection;
    private static String proper_xml_data;
    private static String not_proper_xml_data;
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void initialise() throws Exception {
        connection = DatabaseTestUtil.initTestDB();
    }

    @AfterClass
    public static void closeTest() throws SQLException {
        DatabaseTestUtil.closeTestDB(connection);
    }

    /**
     * Make sure 'onboarding-correlation-ids.xsd' is registered in the Oracle DB.
     * @throws SQLException, ClassNotFoundException
     */
    @Before
    public void setup() throws SQLException, ClassNotFoundException, RuntimeException {

        Statement statement = connection.createStatement();
        statement.execute("INSERT INTO ONBOARDING_APPLICATION (ID) VALUES ('123')");
        statement.execute("INSERT INTO ONBOARDING_ACCOUNT (ONBOARDING_ACCOUNT_SEQ,ONBOARDING_APPLICATION_ID) VALUES ('1','123')");
        statement.execute("INSERT INTO ONBOARDING_PARTY (ONBOARDING_PARTY_SEQ,ONBOARDING_APPLICATION_ID, GCM_PAN) VALUES ('1','123', 'PAN1')");
        statement.execute("INSERT INTO ONBOARDING_PARTY (ONBOARDING_PARTY_SEQ,ONBOARDING_APPLICATION_ID, GCM_PAN) VALUES ('2','123', 'PAN2')");
        statement.close();

        proper_xml_data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "                     <application xmlns=\"" + DatabaseTestUtil.XML_INPUT_OB_CORRELATION_IDS_NAMESPACE + "\">" +
                "                       <application_correlation_id>123</application_correlation_id>" +
                "                       <avaloq_order_id>333</avaloq_order_id>" +
                "                       <accounts>" +
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
                "                                       <address_correlation_seq>2</address_correlation_seq>" +
                "                                       <avaloq_address_id>AV_ADD_ID_124</avaloq_address_id> " +
                "                                   </address>"+
                "                                   <address>" +
                "                                       <address_correlation_seq>1</address_correlation_seq>" +
                "                                       <avaloq_address_id>AV_ADD_ID_123</avaloq_address_id> " +
                "                                   </address>"+
                "                               </addresses>" +
                "                           </party>" +
                "                           <party>" +
                "                               <party_correlation_seq>2</party_correlation_seq>" +
                "                               <gcm_pan>GCM PAN ID 2</gcm_pan>" +
                "                               <addresses>" +
                "                                   <address>" +
                "                                       <address_correlation_seq>1</address_correlation_seq>" +
                "                                       <avaloq_address_id>AV_ADD_ID_125</avaloq_address_id> " +
                "                                   </address>"+
                "                                   <address>" +
                "                                       <address_correlation_seq>2</address_correlation_seq>" +
                "                                       <avaloq_address_id>AV_ADD_ID_126</avaloq_address_id> " +
                "                                   </address>"+
                "                               </addresses>" +
                "                           </party>" +
                "                       </parties>" +
                "                     </application>";

        not_proper_xml_data = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "                     <application xmlns=\"" + DatabaseTestUtil.XML_INPUT_OB_CORRELATION_IDS_NAMESPACE + "\">" +
                "                       <application_correlation_id>123</application_correlation_id>" +
                "                       <avaloq_order_id>333</avaloq_order_id>" +
                "                       <accounts>" +
                "                           <account>" +
                "                               <account_correlation_seq>1</account_correlation_seq>" +
                "                           </account>" +
                "                       </accounts>" +
                "                       <parties>" +
                "                           <party>" +
                "                               <party_correlation_seq>2</party_correlation_seq>" +
                "                               <gcm_pan>GCM PAN ID 2</gcm_pan>" +
                "                                <address>" +
                "                                    <address_correlation_seq>1</address_correlation_seq>" +
                "                                    <avaloq_address_id>AV_ADD_ID_125</avaloq_address_id> " +
                "                                </address>"+
                "                                <address>" +
                "                                    <address_correlation_seq>2</address_correlation_seq>" +
                "                                    <avaloq_address_id>AV_ADD_ID_126</avaloq_address_id> " +
                "                                </address>"+
                "                           </party>" +
                "                       </parties>" +
                "                     </application>";
    }

    @Test
    public void testXMLInputIsAcceptedIfItAdheresTheSchema() throws SQLException {

        int status = DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, "123", proper_xml_data);
        Assert.assertEquals(SUCCESS_STATUS, status);
    }

    @Test
    public void testXMLInputIsNotAcceptedIfItDoesNotAdhereTheSchema() throws SQLException {
        DatabaseTestUtil.setExpectedSQLException(exception, 30937, "No schema definition");
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, "123", not_proper_xml_data);
    }

    @After
    public void tearDown() throws SQLException {
        if (connection != null) {
            Statement statement = connection.createStatement();
            statement.execute("DELETE ONBOARDING_PARTY WHERE ONBOARDING_PARTY_SEQ='1' AND ONBOARDING_APPLICATION_ID='123'");
            statement.execute("DELETE ONBOARDING_PARTY WHERE ONBOARDING_PARTY_SEQ='2' AND ONBOARDING_APPLICATION_ID='123'");
            statement.execute("DELETE ONBOARDING_ACCOUNT WHERE ONBOARDING_ACCOUNT_SEQ = '1' AND ONBOARDING_APPLICATION_ID='123'");
            statement.execute("DELETE ONBOARDING_APPLICATION WHERE ID = '123'");
        }
    }

}
