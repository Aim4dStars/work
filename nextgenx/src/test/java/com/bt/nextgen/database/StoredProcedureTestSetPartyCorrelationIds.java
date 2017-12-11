package com.bt.nextgen.database;

import org.junit.*;
import org.junit.rules.ExpectedException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class StoredProcedureTestSetPartyCorrelationIds {

    protected static Connection connection;

    private static String base_xml_1;
    private static String base_xml_2;
    private static String single_party;
    private static String multiple_parties;
    private static String no_party;
    private static String no_party_correlation_seq;
    private static String no_gcm_pan;
    private static String party_not_found;
    private static String party_seq_empty;
    private static String gcm_pan_empty;

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
    public void setup() throws SQLException, ClassNotFoundException {

        Statement statement = connection.createStatement();
        statement.execute("INSERT INTO ONBOARDING_APPLICATION (ID) VALUES ('123')");

        String addresses = "                       <addresses>" +
                "                                   <address>" +
                "                                       <address_correlation_seq>1</address_correlation_seq>" +
                "                                       <avaloq_address_id>AV_ADD_ID_123</avaloq_address_id> " +
                "                                   </address>" +
                "                               </addresses>";

        base_xml_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "        <application xmlns=\"" + DatabaseTestUtil.XML_INPUT_OB_CORRELATION_IDS_NAMESPACE + "\">" +
                "                      <application_correlation_id>123</application_correlation_id>" +
                "                      <avaloq_order_id>333</avaloq_order_id>" +
                "                          <accounts>" +
                "                           <account>" +
                "                               <account_correlation_seq>1</account_correlation_seq>" +
                "                               <account_number>234234324</account_number>" +
                "                           </account>" +
                "                       </accounts>";
        single_party = "    <parties>" +
                "                           <party>" +
                "                               <party_correlation_seq>1</party_correlation_seq>" +
                "                               <gcm_pan>GCM PAN ID 1</gcm_pan>" +
                addresses +
                "                           </party>" +
                "                       </parties>";
        base_xml_2 = "                     </application>";

        multiple_parties = "    <parties>" +
                "                           <party>" +
                "                               <party_correlation_seq>1</party_correlation_seq>" +
                "                               <gcm_pan>GCM PAN ID 1</gcm_pan>" +
                addresses +
                "                           </party>" +
                "                           <party>" +
                "                               <party_correlation_seq>2</party_correlation_seq>" +
                "                               <gcm_pan>GCM PAN ID 2</gcm_pan>" +
                addresses +
                "                           </party>" +
                "                       </parties>";

        no_party = "                    <parties>" +
                "                       </parties>";


        party_not_found = "    <parties>" +
                "                           <party>" +
                "                               <party_correlation_seq>900</party_correlation_seq>" +
                "                               <gcm_pan>GCM PAN ID 1</gcm_pan>" +
                addresses +
                "                           </party>" +
                "                       </parties>";

        no_party_correlation_seq = "    <parties>" +
                "                           <party>" +
                "                               <gcm_pan>GCM PAN ID 1</gcm_pan>" +
                addresses +
                "                           </party>" +
                "                       </parties>";

        no_gcm_pan = "    <parties>" +
                "                           <party>" +
                "                               <party_correlation_seq>900</party_correlation_seq>" +
                addresses +
                "                           </party>" +
                "                       </parties>";

        party_seq_empty = "    <parties>" +
                "                           <party>" +
                "                               <party_correlation_seq></party_correlation_seq>" +
                "                               <gcm_pan>GCM PAN ID 1</gcm_pan>" +
                addresses +
                "                           </party>" +
                "                       </parties>";

        gcm_pan_empty = "    <parties>" +
                "                           <party>" +
                "                               <party_correlation_seq>1</party_correlation_seq>" +
                "                               <gcm_pan></gcm_pan>" +
                addresses +
                "                           </party>" +
                "                       </parties>";
    }

    @Test
    public void testCorrelationIdIsUpdatedForSingleParty() throws SQLException {
        String applicationId = "123";
        String single_party_xml = base_xml_1 + single_party + base_xml_2;
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, single_party_xml);
        DatabaseTestUtil.verifyPartyCorrelationIdIsUpdated(connection, "123", "1", "GCM PAN ID 1");

    }

    @Test
    public void testLastModifiedIdAndLastModifiedDateAreSetForPartyWhenPartyDoesNotExist() throws SQLException {
        String applicationId = "123";
        String gcmPan = "GCM PAN ID 1";
        String single_party_xml = base_xml_1 + single_party + base_xml_2;

        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, single_party_xml);

        ResultSet result = DatabaseTestUtil.getPartyDetails(connection, applicationId, gcmPan);
        result.next();

        assertNotNull(result.getTimestamp("LAST_MODIFIED_DATE"));
        assertEquals(Properties.getOracleUserName().toLowerCase(), result.getString("LAST_MODIFIED_ID").toLowerCase());

        result.getStatement().close();
        result.close();
    }

    @Test
    public void testLastModifiedIdAndLastModifiedDateAreSetForPartyWhenPartyExists() throws SQLException {
        String applicationId = "123";
        String gcmPan = "GCM PAN ID 1";
        String single_party_xml = base_xml_1 + single_party + base_xml_2;

        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, single_party_xml);

        Statement statement = connection.createStatement();
        statement.executeUpdate("UPDATE ONBOARDING_PARTY SET LAST_MODIFIED_ID = 'I_SHOULD_BE_CHANGED', LAST_MODIFIED_DATE = null" +
                " WHERE ONBOARDING_APPLICATION_ID = " + applicationId + " AND GCM_PAN = '" + gcmPan + "'");
        statement.close();

        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, single_party_xml);

        ResultSet result = DatabaseTestUtil.getPartyDetails(connection, applicationId, gcmPan);
        DatabaseTestUtil.verifyLastModifiedIdAndLastModifiedDateAreUpdated(result);
    }

    @Test
    public void testCorrelationIdsUpdatedForMultipleParties() throws SQLException {
        String applicationId = "123";
        String multiple_parties_xml = base_xml_1 + multiple_parties + base_xml_2;
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, multiple_parties_xml);
        DatabaseTestUtil.verifyPartyCorrelationIdIsUpdated(connection, "123", "1", "GCM PAN ID 1");
        DatabaseTestUtil.verifyPartyCorrelationIdIsUpdated(connection, "123", "2", "GCM PAN ID 2");

    }

    @Test
    public void testInsertPartyRecordWhenPartyIdNotFound() throws SQLException {
        String applicationId = "123";
        String party_id_not_found_xml = base_xml_1 + party_not_found + base_xml_2;
        connection.createStatement().executeQuery("DELETE ONBOARDING_PARTY WHERE ONBOARDING_PARTY_SEQ = '900' AND ONBOARDING_APPLICATION_ID='123'");
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, party_id_not_found_xml);
        DatabaseTestUtil.verifyPartyCorrelationIdIsUpdated(connection, "123", "900", "GCM PAN ID 1");
    }

    @Test
    public void testExceptionIsThrownWhenPartySeqIsEmpty() throws SQLException {
        String applicationId = "123";
        String party_seq_empty_xml = base_xml_1 + party_seq_empty + base_xml_2;
        DatabaseTestUtil.setExpectedSQLException(exception, 20106, "PARTY CORRELATION SEQUENCE IS EMPTY");
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, party_seq_empty_xml);


    }

    @Test
    public void shouldNotThrowExceptionAndShouldUpdateApplicationAndAccountWhenPartyElementIsMissing() throws SQLException {
        String applicationId = "123";
        String party_element_missing_xml = base_xml_1 + no_party + base_xml_2;
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, party_element_missing_xml);
        DatabaseTestUtil.verifyApplicationCorrelationIdIsUpdated(connection, applicationId, "333");
        DatabaseTestUtil.verifyAccountCorrelationIdIsUpdated(connection, applicationId, "1" , "234234324");
    }

    @Test
    public void testExceptionIsThrownWhenPartyCorrelationSeqElementMissing() throws SQLException {
        String applicationId = "123";
        String no_party_correlation_seq_xml = base_xml_1 + no_party_correlation_seq + base_xml_2;
        DatabaseTestUtil.setXMLValidationExpectedException(exception);
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, no_party_correlation_seq_xml);
    }

    @Test
    public void testExceptionIsThrownWhenGcmPanIsMissing() throws SQLException {
        String applicationId = "123";
        String no_gcm_pan_xml = base_xml_1 + no_gcm_pan + base_xml_2;
        DatabaseTestUtil.setXMLValidationExpectedException(exception);
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, no_gcm_pan_xml);
    }

    @Test
    public void testExceptionIsThrownWhenPartiesElementMissing() throws SQLException {
        String applicationId = "123";
        String parties_missing_xml = base_xml_1 + base_xml_2;
        connection.createStatement().executeQuery("UPDATE ONBOARDING_APPLICATION SET AVALOQ_ORDER_ID=null WHERE ID ='123'");
        DatabaseTestUtil.setXMLValidationExpectedException(exception);
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, parties_missing_xml);
    }

    @Test
    public void testExceptionIsThrownWhenGcmPanIsEmpty() throws SQLException {
        String applicationId = "123";
        String avaloq_account_empty_xml = base_xml_1 + gcm_pan_empty + base_xml_2;
        DatabaseTestUtil.setExpectedSQLException(exception, 20105, "GCM PAN IS NULL");
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, avaloq_account_empty_xml);
        connection.createStatement().executeQuery("UPDATE ONBOARDING_APPLICATION SET AVALOQ_ORDER_ID=null WHERE ID ='123'");
    }

    @Test
    public void testRollbackIsDoneWhenAnExceptionIsThrown() throws SQLException {
        String applicationId = "123";
        int accountCorrelationSeq = 1;
        String avaloq_account_empty_xml = base_xml_1 + gcm_pan_empty + base_xml_2;
        DatabaseTestUtil.setExpectedSQLException(exception, 20105, "GCM PAN IS NULL");
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, avaloq_account_empty_xml);
        DatabaseTestUtil.verifyApplicationCorrelationIdIsNotUpdated(connection, applicationId);
        DatabaseTestUtil.verifyAccountCorrelationIdIsNotUpdated(connection, applicationId, accountCorrelationSeq);
    }

    @After
    public void tearDown() throws SQLException {
        if (connection != null) {
            Statement statement = connection.createStatement();
            statement.executeQuery("DELETE ONBOARDING_ACCOUNT WHERE ONBOARDING_ACCOUNT_SEQ = '2' AND ONBOARDING_APPLICATION_ID='123'");
            statement.executeQuery("DELETE ONBOARDING_PARTY WHERE ONBOARDING_PARTY_SEQ='2' AND ONBOARDING_APPLICATION_ID='123'");
            statement.executeQuery("DELETE ONBOARDING_PARTY WHERE ONBOARDING_PARTY_SEQ='1' AND ONBOARDING_APPLICATION_ID='123'");
            statement.executeQuery("DELETE ONBOARDING_PARTY WHERE ONBOARDING_PARTY_SEQ = '900' AND ONBOARDING_APPLICATION_ID='123'");
            statement.executeQuery("DELETE ONBOARDING_ACCOUNT WHERE ONBOARDING_ACCOUNT_SEQ = '1' AND ONBOARDING_APPLICATION_ID='123'");
            statement.executeQuery("DELETE ONBOARDING_APPLICATION WHERE ID = '123'");
        }
    }
}
