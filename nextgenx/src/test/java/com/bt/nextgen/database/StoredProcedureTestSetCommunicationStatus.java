package com.bt.nextgen.database;

import org.junit.*;
import org.junit.rules.ExpectedException;

import java.sql.*;
import java.util.Calendar;

public class StoredProcedureTestSetCommunicationStatus {

    public static final int SUCCESS = 1;
    private static final int COMMUNICATION_ID_CANNOT_BE_FOUND_EXCEPTION = 20108;
    private static final int COMMUNICATION_ID_CANNOT_BE_NULL_EXCEPTION = 20109;
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
        if (connection != null) {
            DatabaseTestUtil.closeTestDB(connection);
            connection.close();
        }
    }

    @Test
    public void testLastModifiedIdAndLastModifiedDateFieldsAreUpdatedWhenCommunicationStatusIsUpdated() throws SQLException {
        String communicationId = "commId23232";
        String applicationId = "123";
        String gcmPan = "GCM_PAN_ID_1";
        String communicationStatus = "emailSent";
        String emailAddress = "test@test.com";
        Timestamp initiationTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
        String trackingId = "TRACKING_ID";
        String failureMsg = "NO FAILURE";

        DatabaseTestUtil.callSetCommunicationStoredProcedure(connection, communicationId, applicationId, gcmPan, communicationStatus, emailAddress, initiationTime, trackingId, failureMsg);

        Statement statement = connection.createStatement();
        statement.executeUpdate("UPDATE ONBOARDING_COMMUNICATION SET LAST_MODIFIED_ID = 'I_SHOULD_BE_CHANGED', LAST_MODIFIED_DATE = null WHERE COMMUNICATION_ID = '"+ communicationId +"'");
        statement.close();

        DatabaseTestUtil.callSetOnboardingCommunicationStatusStoredProcedure(connection, communicationId, "NEW STATUS", "");

        ResultSet result = DatabaseTestUtil.getCommunicationDetails(connection, communicationId);
        DatabaseTestUtil.verifyLastModifiedIdAndLastModifiedDateAreUpdated(result);
    }

    @Test
    public void testCommunicationStatusIsUpdated() throws SQLException {
        String communicationId = "1";
        String statusToBeSet = "UPDATED_STATUS";
        String failureMessageToBeSet = "Nothing to set";

        DatabaseTestUtil.callSetCommunicationStoredProcedure(connection, communicationId, "123", "GCM_PAN_ID_1", "emailSent", "test@test.com", Timestamp.valueOf("2014-10-18 12:00:00.0"), "TRACKING_ID", "NO FAILURE");

        int run = DatabaseTestUtil.callSetOnboardingCommunicationStatusStoredProcedure(connection, communicationId, statusToBeSet, failureMessageToBeSet);
        DatabaseTestUtil.verifyCommunicationStatusIsUpdated(connection, communicationId, statusToBeSet, failureMessageToBeSet);
        Assert.assertEquals(1, run);
    }

    @Test
    public void testExceptionIsThrownWhenCommunicationIdNotFound() throws SQLException {
        String communicationId = "2";
        connection.createStatement().executeQuery("DELETE ONBOARDING_COMMUNICATION WHERE COMMUNICATION_ID = '" + communicationId + "'");
        DatabaseTestUtil.setExpectedSQLException(exception, COMMUNICATION_ID_CANNOT_BE_FOUND_EXCEPTION, "COMMUNICATION ID - " + communicationId + " IS NOT FOUND");
        DatabaseTestUtil.callSetOnboardingCommunicationStatusStoredProcedure(connection, communicationId, "UPDATED_STATUS", "Nothing to set");
    }

    @Test
    public void testExceptionIsThrownWhenCommunicationIdIsNull() throws SQLException {
        String failureMessageToBeSet = "Nothing to set";
        String expectedMsg = "COMMUNICATION ID IS NULL";
        DatabaseTestUtil.setExpectedSQLException(exception, COMMUNICATION_ID_CANNOT_BE_NULL_EXCEPTION, expectedMsg);
        DatabaseTestUtil.callSetOnboardingCommunicationStatusStoredProcedure(connection, null, "HOLA HOLA", failureMessageToBeSet);
    }

    @Test
    public void testFailureMessageIsTrimmedIfMoreThan4K() throws SQLException {
        String communicationId = "3";
        String statusToBeSet = "UPDATED_STATUS";
        String failureMessageToBeSet = "String containing more than 4000 characters. erfzvxmxsfxisnedfdfjqevgovteiynpbkz" +
                "kvnorfqqvzyowndfcflhnddhldcpoduauaejjpdcdcztxwiwzihufecijiqegurwogwrphdkpcuxwcljltoghzmvnsjqrrfoswxvhqa" +
                "hwgeomjrbuuolmfuvqfpgaxpmaiafnvybyowyowrodyocfcmxnvkrltbbyibmfejlcfhucuertnjfqizypyvbdlskyokfyodzljrgj" +
                "tqnmjchvcowvisjjzygoiqldsmnygxcczamqzkbripiqlhfyjodtnpwbrrrmmlmqfnfxournpljohtamospihcrtnbecmmpjhqvxhv" +
                "icokpmqemqqerwrrirtikyfjbjplmsnzfjlpotozqoptdjptgbgdhlamvwltkamhirdqetzittmqsdohgtztiznjwgaoqzkdryzymj" +
                "syfhbljjtjaciloeoysxxeztlzzyujwyjmzddggxbfwacrxhkxajuizcidnxjwqckcizaibpckzhbvstykjfyepbqedhghfatxeywi" +
                "rjmiyxlhvhqrpckwgfrxlzrkymmvpskqlmumjwbzycluxucrllxrdevmtqvuqoqjimtuaubxffvyfjtsxavgztiouxlyvztzscxhxl" +
                "lcekzjgpjazktrasfolnnspleoivbvafrxtvmqiumpvxxnfwfysocsrgwodsagqypznrwixyhzbvpruhwbyukitwfyqcnzxwiqtsri" +
                "cntgnjzcxrzrxnrxsxltavuvsjcmrpzfgtgqdhombxhvaxgyhtanelmzknuofnxlvmcwsxriarwxsamkplwtahcipkmtuyvuwjdtdu" +
                "rcgnavpqjpudhrigbmyghgmpeuqxxihzbjoqpubztcqzqtecoxfooxrhdznwjdrydnjtsitrswwsothlahmppfhgvpmdjjnmzdzevc" +
                "jejlkhmuznuppbfyytgrrvdlqrnxwrpzdxsnsilolzbmisnemrcxcguojhvpcfgsgwjkuelflwvchhbhsqjbjeshmskfprpfxbeqve" +
                "xrkuuyatjxhqywmygetowxriqjzayhfimeazwtsfxsjoutkkemamrpbjnyukgoszyijxptekjimanxxittvjahlbguibazrookkufm" +
                "cyluyltclsqwkatwyjclidhbkfkfogncpkimueawdwhrjlttxllirrhtrvhquzklggevbodqgdcknautlmkwnqjiocoufsmjtwudxr" +
                "iqgohpdomscfflhaxtiyaiflwiscpozscggpupymflvxmmpjbizltjwmhwpbfyurnmytgahtkktriqolanxpbntsddvmhdaigteszb" +
                "lrzrnmifqdfzftpyyifppwufsmstqwngchawcihzqtvjltyiravljedwfbvyfwymvifsvdlkhkmqcdnvpxwgnqgiqczzqqrrswwmql" +
                "dmlfbpiadcybqtentuohuqhdqefgxtsmhdhaxvlxtkhmafvxddsdaiatknopdlqunsswlrmswjxjrgsfwoauvuxilaefiiulamoqte" +
                "ivdirwdbxacuhuqvbitzagetaytihquiwaalyumbbdvkfrospbaxrxwlmiwjpyuwzsvpkwtfbtzmtpkirrqzalgxcqisdmihrvlgee" +
                "oikbqepbxyfiraeehcgdbfrzfpvfizmvnebhugqcdatlwloqaunilnicjepmzdlegzfdwrfhktckscnttkvxvwalnlszsptlnhawsn" +
                "btzguuivxlgybimjibffirjytponvxnopbtwqjctuzgtiwvrcaftcvdxeqgsgqtsfrkgtnycdigwlsldcshabbnqnswuhpefddhyks" +
                "ovisnweuajksjvpqlcfgvetxjjuwiqqtgzvxynkkrkjrfcwlivlyzechsfxcbjeztjrgaouhujhxpwqnjsehbutqfyqsraskcvyqwi" +
                "qnufhlszppgjtzokwurrfctysflxnzatlcmuqzxzoqtlvfzzdeefzjfwmpmqxhjqfgjmdrcuswqegykaweysmeymussipbgmllpzda" +
                "rckjvedannbytrevkcmdrxpqajinrkjpccwnfcfseylevpllzmnsoqkatwfcpttpeswdweucgmwictgbcgghsbctjunnrlvtzlzdw" +
                "xsvlsxwyhztdnyethrxtgcgrwkuflphbdnhouxsjdjmwnhwhviulpqplqonbpydsoudtsonhnzvpovwzanhhghdwfnkxmegwgmlox" +
                "wwthpolpkzqchrkflkxrqaqicoscwhaknfxpqqrfaknbpbwpbymlgircqnqtlplxfybvfocsdvexmkbtdxgbcgctjtjsonacecswk" +
                "siclxyjzhhnatwlcuelsnpaxorxnlrwwlvzmkujrfgiokxmolracrcbhbbznnryjhlzttrwnifwqhlgcktbfnlahgdzwebizmonzq" +
                "pqxtzkigilgjrllloaguzaskhfchffezynaoghpzczapsfbsyvgvnevuuzbphrodghfbizbuhtfqazuqezpgsdteqveavypuaodqz" +
                "qvfdnbgirsqrmhsicewbvgixjztycxuakgmvowggbalqlllvbowjdglkbaxgjfhgfkulbrdjosymbgpfqtrpqurtrkwmyjfznoabr" +
                "orhrgiggtdrdmtnmkjnaexiyvazjhamsnlgwwhqmuezhntggfhgcnvdhepjyanrxvfnnivvnusvmijsfmpqvupuzalcarhwutkoci" +
                "eufzllwowrsyvuzzzjsqqtcmxmixjngdsbgdvueccrpcwrimyofuikvuqoehqbaxbszfkiaenkfupxgfxzfibaowmwlhktmzokdas" +
                "fcsqzcqqyqcekqemzyirzwedocnbaqmddpwvezfflatyyrjszmjkovotbwusmqrvqewniakloawnidpxxfathxyjedmmsoxnuozvw" +
                "gqkbywhnirrkyflxpqgcgvvknhjizvovzinnpbifkwsqnaekeduypjerqwyotioufcjxnslhvnlxyogrdunougksvwgvdceiyqche" +
                "lgcpotkfckvhvszdbhxmubqbtlcyojytnsizlgxwzkjybzufkoobpugszpbvvexqyxgfwlsodnlrofdtwvyfhrzezghspakkocszl" +
                "yhtmhrbshxdgppnckuykiotsffmyqqowwhgawblixjdbbrzmqzsszvwxoyjxtglvlhgxehumybydzdyrtavrvcrhvomgydxsljxia" +
                "xkgcdjjcnmakjgebkpzzvhxrqcwlpojgtzbzjzqhigpkpcdzfklqbfrrlgsybfmlbnmmhxzbqnszfvcrrahcxqqdyixycnfrpnlmr" +
                "jvucyalmvbgangqzqnuktppgfrgubcgxaycwgbuwqndcflfvnoprkncigrzjvmhckaomtxmsnrofkmmfcpkewoowwqoxriqijkflr" +
                "pystxwnacfwpebaagdvakldyajyklvczmpzvcenbdxmnflqqbwzitqkibraheohxqcumquhknojzwankvkihmgsrmxinjcuhjquvr" +
                "xzsyjcztaepxlgjfhioknjbpbohrbxmiyhulmfjojdlyhgcjztroebpwuwqseugagnotyrmbuffhhaoqcwxctonwgtoaxgxocwuvn" +
                "kerthsljxqapzpffrxfigzciuysxpgqjexcpscfnqnmfryolclybuhnigwhwnlwxrrvaofonzmayanhclzztmzzvmkktsxdujemck" +
                "qpcjzspabahgplspoaoqzqwjhiwnyahfgbuebaylddazlnhyqnetbbvanputkwqdonwcypqnlisuudxcvekkomkdmiulquceslqdx" +
                "radegcusjtoylsryokeyxxyxykazckosoegmeaatxmrawntslgurobaqpzpquskwnhqrfihpubhzthofklhfvlhclugdwurojlrgz" +
                "pyfxwwwjxkpfupumhqovpbwxstxeefegotzdacbsknyrgsvuxadrchgshnknxdvmnfnoxwyhafvbvbgsigpkgnpqdbrtevuckivvj" +
                "rjwpabglhqdnlccwlyqhjrdqsvrzhxwclpikrwkaeqcjpcrrpkbuunmzvqtggbdnycxzvdtofvdeghnqjlqhkbjgzsrmaithfysaa" +
                "gtftvhmvtkuzztozunqvqaujlcdxcdvwjmixsqwytplwmmetjvfykiphwwogzgevkbylgulklmfudgobanotvozwcevfbjqqozbla" +
                "wakwudwkstvuxrohwzurkpwlgoaadrsntmpfwltnsjmebavwaoukmrmbemegvihhzlshaevhwlzznkxlrcovyudkfwqxxmnlkcxdb" +
                "odbylocmiuhrpmjvswlonlsfsivxmoibgsefwoexrrnyagtvmmqddytnmnzsjswwekdknhkzdbgmbkzajzyaovywudpsshqzcc ";

        String expectedFailureMessage = failureMessageToBeSet.substring(0,4000);

        DatabaseTestUtil.callSetCommunicationStoredProcedure(connection, communicationId, "123", "GCM_PAN_ID_1", "emailSent", "test@test.com", Timestamp.valueOf("2014-10-18 12:00:00.0"), "TRACKING_ID", "NO FAILURE");

        int run = DatabaseTestUtil.callSetOnboardingCommunicationStatusStoredProcedure(connection, communicationId, statusToBeSet, failureMessageToBeSet);
        DatabaseTestUtil.verifyCommunicationStatusIsUpdated(connection, communicationId, statusToBeSet, expectedFailureMessage);
        Assert.assertEquals(1,run);
    }

    @Test
    public void testCommunicationStatusIsUpdatedWhenFailureMessageIsNull() throws SQLException {
        String communicationId = "4";
        String statusToBeSet = "UPDATED_STATUS";
        String applicationId = "123";

        DatabaseTestUtil.callSetCommunicationStoredProcedure(connection, communicationId, applicationId, "GCM_PAN_ID_1", "emailSent", "test@test.com", Timestamp.valueOf("2014-10-18 12:00:00.0"), "TRACKING_ID", "NO FAILURE");

        int run = DatabaseTestUtil.callSetOnboardingCommunicationStatusStoredProcedure(connection, communicationId, statusToBeSet, null);
        DatabaseTestUtil.verifyCommunicationStatusIsUpdated(connection, communicationId, statusToBeSet, null);
        Assert.assertEquals(1,run);
    }
}
