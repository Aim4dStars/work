package com.bt.nextgen.database;

import org.junit.*;
import org.junit.rules.ExpectedException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StoredProcedureTestSetAccountCorrelationIds {

    protected static Connection connection;
    private static String base_xml_1;
    private static String base_xml_2;
    private static String single_account;
    private static String multiple_accounts;
    private static String no_account;
    private static String no_account_correlation_seq;
    private static String no_account_number;
    private static String account_not_found;
    private static String account_seq_empty;
    private static String avaloq_account_empty;

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
        statement.close();
        base_xml_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "        <application xmlns=\"" + DatabaseTestUtil.XML_INPUT_OB_CORRELATION_IDS_NAMESPACE + "\">" +
                "                      <application_correlation_id>123</application_correlation_id>" +
                "                      <avaloq_order_id>333</avaloq_order_id>";
        base_xml_2 = "                    <parties>" +
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

        single_account = "       <accounts>" +
                "                           <account>" +
                "                               <account_correlation_seq>1</account_correlation_seq>" +
                "                               <account_number>234234324</account_number>" +
                "                           </account>" +
                "                       </accounts>";

        multiple_accounts = "  <accounts>" +
                "                           <account>" +
                "                               <account_correlation_seq>1</account_correlation_seq>" +
                "                               <account_number>234234324</account_number>" +
                "                           </account>" +
                "                           <account>" +
                "                               <account_correlation_seq>2</account_correlation_seq>" +
                "                               <account_number>234234325</account_number>" +
                "                           </account>" +
                "                       </accounts>";


        no_account = "                    <accounts>" +
                "                       </accounts>";

        no_account_correlation_seq = "   <accounts>" +
                "                           <account>" +
                "                           <account_number>34434</account_number>" +
                "                           </account>" +
                "                       </accounts>";

        no_account_number = "       <accounts>" +
            "                           <account>" +
            "                           <account_correlation_seq>1</account_correlation_seq>" +
                "                       </account>" +
            "                       </accounts>";

        account_not_found = "       <accounts>" +
                "                           <account>" +
                "                               <account_correlation_seq>990</account_correlation_seq>" +
                "                               <account_number>234234324</account_number>" +
                "                           </account>" +
                "                       </accounts>";

        account_seq_empty = "       <accounts>" +
                "                           <account>" +
                "                               <account_correlation_seq></account_correlation_seq>" +
                "                               <account_number>234234324</account_number>" +
                "                           </account>" +
                "                       </accounts>";

        avaloq_account_empty = "       <accounts>" +
                "                           <account>" +
                "                               <account_correlation_seq>1</account_correlation_seq>" +
                "                               <account_number></account_number>" +
                "                           </account>" +
                "                       </accounts>";

    }

    @Test
    public void testCorrelationIdIsUpdatedForSingleAccount() throws SQLException {
        String applicationId = "123";
        String single_account_xml = base_xml_1 + single_account + base_xml_2;
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, single_account_xml);
        DatabaseTestUtil.verifyAccountCorrelationIdIsUpdated(connection, "123", "1", "234234324");

    }

    @Test
    public void testLastModifiedDateAndLastModifiedIdAreSetForAccountWhenAccountDoesNotExist() throws SQLException {
        String applicationId = "123";
        String single_account_xml = base_xml_1 + single_account + base_xml_2;

        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, single_account_xml);

        ResultSet result = DatabaseTestUtil.getAccountDetails(connection, applicationId, "1");
        DatabaseTestUtil.verifyLastModifiedIdAndLastModifiedDateAreUpdated(result);
    }

    @Test
    public void testLastModifiedDateAndLastModifiedIdAreUpdatedForAccountWhenAccountExists() throws SQLException {
        String applicationId = "123";
        String single_account_xml = base_xml_1 + single_account + base_xml_2;

        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, single_account_xml);

        Statement statement = connection.createStatement();
        statement.executeUpdate("UPDATE ONBOARDING_ACCOUNT SET LAST_MODIFIED_ID = 'I_SHOULD_BE_CHANGED', LAST_MODIFIED_DATE = null WHERE ONBOARDING_APPLICATION_ID = " + applicationId);
        statement.close();

        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, single_account_xml);

        ResultSet result = DatabaseTestUtil.getAccountDetails(connection, applicationId, "1");
        DatabaseTestUtil.verifyLastModifiedIdAndLastModifiedDateAreUpdated(result);
    }

    @Test
    public void testCorrelationIdsUpdatedForMultipleAccounts() throws SQLException {
        String applicationId = "123";
        String multiple_accounts_xml = base_xml_1 + multiple_accounts + base_xml_2;
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, multiple_accounts_xml);
        DatabaseTestUtil.verifyAccountCorrelationIdIsUpdated(connection, "123", "1", "234234324");
        DatabaseTestUtil.verifyAccountCorrelationIdIsUpdated(connection, "123", "2", "234234325");
    }

    @Test
    public void testAccountRecordIsInsertedWhenAccountIdNotFound() throws SQLException {
        String applicationId = "123";
        String account_id_not_found_xml = base_xml_1 + account_not_found + base_xml_2;
        connection.createStatement().executeQuery("DELETE ONBOARDING_ACCOUNT WHERE ONBOARDING_ACCOUNT_SEQ = '990' AND ONBOARDING_APPLICATION_ID='123'");
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, account_id_not_found_xml);
        String account_seq_empty_xml = base_xml_1 + account_seq_empty + base_xml_2;
        DatabaseTestUtil.setExpectedSQLException(exception, 20103, "ACCOUNT CORRELATION SEQUENCE IS EMPTY");
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, account_seq_empty_xml);
    }

    @Test
    public void testExceptionIsThrownWhenAccountElementWithinAccountsMissing() throws SQLException {
        String applicationId = "123";
        String no_account_within_accounts_xml = base_xml_1 + no_account + base_xml_2;
        DatabaseTestUtil.setXMLValidationExpectedException(exception);
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, no_account_within_accounts_xml);
    }

    @Test
    public void testExceptionIsThrownWhenAccountElementMissing() throws SQLException {
        String applicationId = "123";
        String no_account_element_xml = base_xml_1 + no_account+ base_xml_2;
        DatabaseTestUtil.setXMLValidationExpectedException(exception);
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, no_account_element_xml);
    }

    @Test
    public void testExceptionIsThrownWhenAccountCorrelationSeqElementMissing() throws SQLException {
        String applicationId = "123";
        String no_account_element_xml = base_xml_1 + no_account_correlation_seq + base_xml_2;
        DatabaseTestUtil.setXMLValidationExpectedException(exception);
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, no_account_element_xml);
    }

    @Test
    public void testExceptionIsThrownWhenAccountNumberElementMissing() throws SQLException {
        String applicationId = "123";
        String no_account_element_xml = base_xml_1 + no_account_number + base_xml_2;
        DatabaseTestUtil.setXMLValidationExpectedException(exception);
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, no_account_element_xml);
    }

    @Test
    public void testExceptionIsNotThrownWhenAccountsElementMissing() throws SQLException {
        String applicationId = "123";
        String avaloqOrderId = "333";
        String account_id_not_found_xml = base_xml_1 + base_xml_2;
        ResultSet resultSet = DatabaseTestUtil.getApplicationDetails(connection, applicationId);
        resultSet.next();
        String avaloqOrderIdBefore = resultSet.getString("AVALOQ_ORDER_ID");
        Assert.assertNull(avaloqOrderIdBefore);
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, account_id_not_found_xml);
        DatabaseTestUtil.verifyApplicationCorrelationIdIsUpdated(connection, applicationId, avaloqOrderId);
    }

    @Test
    public void testExceptionIsNotThrownWhenAvaloqAccountNumberIsEmpty() throws SQLException {
        String applicationId = "123";
        String avaloqOrderId = "333";
        String avaloq_account_empty_xml = base_xml_1 + avaloq_account_empty + base_xml_2;
        ResultSet resultSet = DatabaseTestUtil.getApplicationDetails(connection, applicationId);
        resultSet.next();
        String avaloqOrderIdBefore = resultSet.getString("AVALOQ_ORDER_ID");
        Assert.assertNull(avaloqOrderIdBefore);
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, avaloq_account_empty_xml);
        DatabaseTestUtil.verifyAccountCorrelationIdIsUpdated(connection, "123", "1", null);
        DatabaseTestUtil.verifyApplicationCorrelationIdIsUpdated(connection, applicationId, avaloqOrderId);
    }

    @Test
    public void testRollbackIsDoneWhenAnExceptionIsThrown() throws SQLException {
        String applicationId = "123";
        String account_seq_empty_xml = base_xml_1 + account_seq_empty + base_xml_2;
        DatabaseTestUtil.setExpectedSQLException(exception, 20103, "ACCOUNT CORRELATION SEQUENCE IS EMPTY");
        DatabaseTestUtil.callSetCorrelationIdsStoredProcedure(connection, applicationId, account_seq_empty_xml);
        DatabaseTestUtil.verifyApplicationCorrelationIdIsNotUpdated(connection, applicationId);
    }

    @After
    public void tearDown() throws SQLException {
        if (connection != null) {
            Statement statement = connection.createStatement();
            statement.execute("DELETE ONBOARDING_ACCOUNT WHERE ONBOARDING_ACCOUNT_SEQ = '990' AND ONBOARDING_APPLICATION_ID='123'");
            statement.execute("DELETE ONBOARDING_ACCOUNT WHERE ONBOARDING_ACCOUNT_SEQ = '2' AND ONBOARDING_APPLICATION_ID='123'");
            statement.execute("DELETE ONBOARDING_PARTY WHERE ONBOARDING_PARTY_SEQ='1' AND ONBOARDING_APPLICATION_ID='123'");
            statement.execute("DELETE ONBOARDING_ACCOUNT WHERE ONBOARDING_ACCOUNT_SEQ = '1' AND ONBOARDING_APPLICATION_ID='123'");
            statement.execute("DELETE ONBOARDING_APPLICATION WHERE ID = '123'");
            statement.close();
        }
    }
}
