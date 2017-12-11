package com.bt.nextgen.database;

import org.junit.*;
import org.junit.rules.ExpectedException;

import java.sql.*;

public class StoredProcedureTestSetApplicationStatus {

    public static final int SUCCESS = 1;
    private static final int APPLICATION_ID_CANNOT_BE_FOUND_EXCEPTION = 20100;
    private static final int APPLICATION_ID_IS_NULL_EXCEPTION = 20101;
    protected static Connection connection;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void setup() throws Exception {
        connection = DatabaseTestUtil.initTestDB();
        Statement statement = connection.createStatement();
        statement.execute("INSERT INTO ONBOARDING_APPLICATION (ID) VALUES ('123')");
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
    public void testApplicationStatusIsUpdated() throws SQLException {
        String applicationId = "123";
        String statusToBeSet = "UPDATED_STATUS";
        String failureMessageToBeSet = "Nothing to set";
        int run = DatabaseTestUtil.callSetOnboardingApplicationStatusStoredProcedure(connection, applicationId, statusToBeSet, failureMessageToBeSet);
        DatabaseTestUtil.verifyApplicationStatusIsUpdated(connection, applicationId, statusToBeSet, failureMessageToBeSet);
        Assert.assertEquals(1, run);
    }

    @Test
    public void testExceptionIsThrownWhenApplicationIdNotFound() throws SQLException {
        String applicationId = "9999999999999";
        String statusToBeSet = "HOLA HOLA";
        String failureMessageToBeSet = "Nothing to set";
        String expectedMsg = "ONBOARDING APPLICATION ID CANNOT BE FOUND. APPLICATION ID:" + applicationId;
        connection.createStatement().executeQuery("DELETE ONBOARDING_APPLICATION WHERE ID = '9999999999999'");
        DatabaseTestUtil.setExpectedSQLException(exception, APPLICATION_ID_CANNOT_BE_FOUND_EXCEPTION, expectedMsg);
        DatabaseTestUtil.callSetOnboardingApplicationStatusStoredProcedure(connection, applicationId, statusToBeSet, failureMessageToBeSet);
    }

    @Test
    public void testExceptionIsThrownWhenApplicationIdIsNull() throws SQLException {
        String statusToBeSet = "HOLA HOLA";
        String failureMessageToBeSet = "Nothing to set";
        String expectedMsg = "APPLICATION ID IS NULL.";
        DatabaseTestUtil.setExpectedSQLException(exception, APPLICATION_ID_IS_NULL_EXCEPTION, expectedMsg);
        DatabaseTestUtil.callSetOnboardingApplicationStatusStoredProcedure(connection, null, statusToBeSet, failureMessageToBeSet);
    }

    @Test
    public void testFailureMessageIsTrimmedIfMoreThan4K() throws SQLException {
        String applicationId = "123";
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

        int run = DatabaseTestUtil.callSetOnboardingApplicationStatusStoredProcedure(connection, applicationId, statusToBeSet, failureMessageToBeSet);
        DatabaseTestUtil.verifyApplicationStatusIsUpdated(connection, applicationId, statusToBeSet, expectedFailureMessage);
        Assert.assertEquals(1,run);
    }

    @Test
    public void testApplicationStatusIsUpdatedWhenFailureMessageIsNull() throws SQLException {
        String applicationId = "123";
        String statusToBeSet = "UPDATED_STATUS";
        int run = DatabaseTestUtil.callSetOnboardingApplicationStatusStoredProcedure(connection, applicationId, statusToBeSet, null);
        DatabaseTestUtil.verifyApplicationStatusIsUpdated(connection, applicationId, statusToBeSet, null);
        Assert.assertEquals(1,run);
    }

    @Test
    public void testLastModifiedIdAndLastModifiedDateFieldsAreUpdatedWhenApplicationStatusIsUpdated() throws SQLException {
        String applicationId = "123";
        String applicationStatus = "ApplicationCreated";
        String failureMsg = "NO FAILURE";

        DatabaseTestUtil.callSetOnboardingApplicationStatusStoredProcedure(connection, applicationId, applicationStatus, failureMsg);

        Statement statement = connection.createStatement();
        statement.executeUpdate("UPDATE ONBOARDING_APPLICATION SET LAST_MODIFIED_ID = 'I_SHOULD_BE_CHANGED', LAST_MODIFIED_DATE = null WHERE ID = "+ applicationId);
        statement.close();

        DatabaseTestUtil.callSetOnboardingApplicationStatusStoredProcedure(connection, applicationId, "NEW STATUS", "");

        ResultSet result = DatabaseTestUtil.getApplicationDetails(connection, applicationId);
        DatabaseTestUtil.verifyLastModifiedIdAndLastModifiedDateAreUpdated(result);
    }
}
