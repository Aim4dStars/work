package com.bt.nextgen.database;

import org.junit.*;

import java.sql.*;

public class StoredProcedureTestSetOnboardingCorrelationIds {

    private static final int SUCCESS_STATUS = 1;
    private static final String CALL_SET_CORRELATION_IDS = "{call onboarding_id_updates.set_onboarding_correlation_ids(:application_id,:application_data_xml,:status)}";
    private static final String APPLICATION_ID = "application_id";
    private static final String APPLICATION_DATA_XML = "application_data_xml";
    private static final String STATUS = "status";
    protected static Connection connection;
    private static String proper_xml_data;

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
        statement.execute("INSERT INTO ONBOARDING_APPLICATION (ID, STATUS) VALUES ('123','ApplicationCreationInProgress')");
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
    }

    @Test
    public void testStatusOutput() throws SQLException {

        CallableStatement statement = callSetCorrelationIdStoredProcedureWithProperXMLInput();
        Assert.assertEquals(SUCCESS_STATUS, statement.getInt(STATUS));
        statement.close();
    }

    @Test
    public void testApplicationCorrelationIdIsUpdated() throws SQLException {
        CallableStatement statement = callSetCorrelationIdStoredProcedureWithProperXMLInput();
        verifyApplicationCorrelationIdIsUpdated("123", "333");
        statement.close();
    }

    @Test
    public void testApplicationStatusIsResetAsNullWhileUpdatingCorrelationIds() throws SQLException {
        CallableStatement statement = callSetCorrelationIdStoredProcedureWithProperXMLInput();
        verifyApplicationStatusIsReset("123");
        statement.close();
    }

    @Test
    public void testAccountCorrelationIdIsUpdated() throws SQLException {
        CallableStatement statement = callSetCorrelationIdStoredProcedureWithProperXMLInput();
        verifyAccountCorrelationIdIsUpdated("123", "1", "234234324");
        statement.close();
    }

    @Test
    public void testPartyCorrelationIdIsUpdated() throws SQLException {
        CallableStatement statement = callSetCorrelationIdStoredProcedureWithProperXMLInput();
        verifyPartyCorrelationIdIsUpdated("123", "1", "GCM PAN ID 1");
        verifyPartyCorrelationIdIsUpdated("123", "2", "GCM PAN ID 2");
        statement.close();
    }


    private void verifyApplicationCorrelationIdIsUpdated(String applicationCorrelationId, String avaloqOrderId) throws SQLException {

        ResultSet result = DatabaseTestUtil.getApplicationDetails(connection, applicationCorrelationId);
        Assert.assertTrue(result.next());
        Assert.assertEquals(avaloqOrderId, result.getString("AVALOQ_ORDER_ID"));
        result.close();
    }

    private void verifyApplicationStatusIsReset(String applicationCorrelationId) throws SQLException {

        ResultSet result = DatabaseTestUtil.getApplicationDetails(connection, applicationCorrelationId);
        Assert.assertTrue(result.next());
        Assert.assertNull(result.getString("STATUS"));
        result.close();
    }

    private void verifyAccountCorrelationIdIsUpdated(String applicationCorrelationId, String accountCorrelationSeq, String accountNumber) throws SQLException {

        ResultSet result = DatabaseTestUtil.getAccountDetails(connection, applicationCorrelationId, accountCorrelationSeq);
        Assert.assertTrue(result.next());
        Assert.assertEquals(accountNumber, result.getString("ACCOUNT_NUMBER"));
        result.close();
    }

    private void verifyPartyCorrelationIdIsUpdated(String applicationCorrelationId, String partySeq, String gcmPan) throws SQLException {

        ResultSet result = DatabaseTestUtil.getPartyDetails(connection,applicationCorrelationId, gcmPan);
        Assert.assertTrue(result.next());
        Assert.assertEquals(partySeq, result.getString("ONBOARDING_PARTY_SEQ"));
        result.close();
    }


    private CallableStatement callSetCorrelationIdStoredProcedureWithProperXMLInput() throws SQLException {
        String application_id = "123";

        CallableStatement statement = connection.prepareCall(CALL_SET_CORRELATION_IDS);
        //Note: Order of setting the values is important!
        statement.setString(APPLICATION_ID, application_id);
        //statement.setCharacterStream(APPLICATION_DATA_XML,new StringReader(applicationData),applicationData.length());
        statement.setString(APPLICATION_DATA_XML, proper_xml_data);
        statement.registerOutParameter(STATUS, Types.INTEGER);

        statement.execute();
        return statement;
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
