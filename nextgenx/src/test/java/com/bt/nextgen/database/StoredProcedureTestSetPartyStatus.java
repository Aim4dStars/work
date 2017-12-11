package com.bt.nextgen.database;

import org.junit.*;
import org.junit.rules.ExpectedException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StoredProcedureTestSetPartyStatus {

    public static final int SUCCESS = 1;
    public static final String GCM_PAN_OR_APPLICATION_ID_IS_NULL_MSG = "GCM PAN OR APPLICATION ID IS NULL";
    private static final int PARTY_ID_CANNOT_BE_FOUND_EXCEPTION = 20106;
    private static final int PARTY_ID_OR_APPLICATION_ID_IS_NULL_EXCEPTION = 20107;
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
    public void testPartyStatusIsUpdated() throws SQLException {
        String applicationId = "123";
        String gcmPan = "GCM_PAN_ID_1";
        String statusToBeSet = "UPDATED_STATUS";
        String failureMessageToBeSet = "Nothing to set";
        int run = DatabaseTestUtil.callSetOnboardingPartyStatusStoredProcedure(connection, applicationId, gcmPan, statusToBeSet, failureMessageToBeSet);
        DatabaseTestUtil.verifyPartyStatusIsUpdated(connection, applicationId, gcmPan, statusToBeSet, failureMessageToBeSet);
        Assert.assertEquals(1, run);
    }

    @Test
    public void testExceptionIsThrownWhenPartyIdNotFound() throws SQLException {
        String applicationId = "123";
        String gcmPan = "GCM_PAN_ID_99889898";
        String statusToBeSet = "UPDATED_STATUS";
        String failureMessageToBeSet = "Nothing to set";
        String expectedMsg = "PARTY CANNOT BE FOUND";
        connection.createStatement().executeQuery("DELETE ONBOARDING_PARTY WHERE ONBOARDING_APPLICATION_ID = '123' AND GCM_PAN = '9999999999999'");
        DatabaseTestUtil.setExpectedSQLException(exception, PARTY_ID_CANNOT_BE_FOUND_EXCEPTION, expectedMsg);
        DatabaseTestUtil.callSetOnboardingPartyStatusStoredProcedure(connection, applicationId, gcmPan, statusToBeSet, failureMessageToBeSet);
    }

    @Test
    public void testExceptionIsThrownWhenApplicationIdIsNull() throws SQLException {
        String gcmPan = "SOMETHING";
        String statusToBeSet = "HOLA HOLA";
        String failureMessageToBeSet = "Nothing to set";
        DatabaseTestUtil.setExpectedSQLException(exception, PARTY_ID_OR_APPLICATION_ID_IS_NULL_EXCEPTION, GCM_PAN_OR_APPLICATION_ID_IS_NULL_MSG);
        DatabaseTestUtil.callSetOnboardingPartyStatusStoredProcedure(connection, null, gcmPan, statusToBeSet, failureMessageToBeSet);
    }

    @Test
    public void testExceptionIsThrownWhenGcmPanIsNull() throws SQLException {
        String applicationId = "123";
        String statusToBeSet = "HOLA HOLA";
        String failureMessageToBeSet = "Nothing to set";
        DatabaseTestUtil.setExpectedSQLException(exception, PARTY_ID_OR_APPLICATION_ID_IS_NULL_EXCEPTION, GCM_PAN_OR_APPLICATION_ID_IS_NULL_MSG);
        DatabaseTestUtil.callSetOnboardingPartyStatusStoredProcedure(connection, applicationId, null, statusToBeSet, failureMessageToBeSet);
    }

    @Test
    public void testFailureMessageIsTrimmedIfMoreThan4K() throws SQLException {
        String applicationId = "123";
        String gcmPan = "GCM_PAN_ID_1";
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
        int run = DatabaseTestUtil.callSetOnboardingPartyStatusStoredProcedure(connection, applicationId, gcmPan, statusToBeSet, failureMessageToBeSet);
        DatabaseTestUtil.verifyPartyStatusIsUpdated(connection, applicationId, gcmPan, statusToBeSet, expectedFailureMessage);
        Assert.assertEquals(1,run);
    }

    @Test
    public void testPartyStatusIsUpdatedWhenFailureMessageIsNull() throws SQLException {
        String applicationId = "123";
        String gcmPan = "GCM_PAN_ID_1";
        String statusToBeSet = "UPDATED_STATUS";
        int run = DatabaseTestUtil.callSetOnboardingPartyStatusStoredProcedure(connection, applicationId, gcmPan, statusToBeSet, null);
        DatabaseTestUtil.verifyPartyStatusIsUpdated(connection, applicationId, gcmPan, statusToBeSet, null);
        Assert.assertEquals(1,run);
    }

    @Test
    public void testLastModifiedIdAndLastModifiedDateFieldsAreUpdatedWhenPartyStatusIsUpdated() throws SQLException {
        String applicationId = "123";
        String gcmPan = "GCM_PAN_ID_1";
        String partyStatus = "NotificationSent";
        String failureMsg = "NO FAILURE";

        DatabaseTestUtil.callSetOnboardingPartyStatusStoredProcedure(connection, applicationId, gcmPan, partyStatus, failureMsg);

        Statement statement = connection.createStatement();
        statement.executeUpdate("UPDATE ONBOARDING_PARTY SET LAST_MODIFIED_ID = 'I_SHOULD_BE_CHANGED', LAST_MODIFIED_DATE = null" +
                " WHERE ONBOARDING_APPLICATION_ID = " + applicationId + " AND GCM_PAN = '" + gcmPan + "'");
        statement.close();

        DatabaseTestUtil.callSetOnboardingPartyStatusStoredProcedure(connection, applicationId, gcmPan, "NEW STATUS", "");

        ResultSet result = DatabaseTestUtil.getPartyDetails(connection, applicationId, gcmPan);
        DatabaseTestUtil.verifyLastModifiedIdAndLastModifiedDateAreUpdated(result);
    }
}
