package com.bt.nextgen.util.type;

import com.btfin.panorama.core.security.encryption.EncodedString;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class EncodedStringTest {
    @Test
    public void testDecryptEncoded() {
        assertThat(EncodedString.toPlainText("4BFB6852C5D37DEAC93928DC134ACDBBC6F96332EEAE9FAB"), is("1450720"));
    }

    @Test
    public void testPlainText() throws Exception {
        assertThat(EncodedString.fromPlainText("james").plainText(), is("james"));
    }

    @Test
    public void testToPlainText() throws Exception {
        EncodedString james = EncodedString.fromPlainText("james");

        assertThat(james.plainText(), is("james"));
    }

    @Test
    public void testToPlainTextUsingTL() throws Exception {
        EncodedString james = EncodedString.fromPlainTextUsingTL("james");

        assertThat(james.plainText(), is("james"));
    }

    @Test
    public void testPlainTextUsingTL() throws Exception {
        EncodedString james = EncodedString.fromPlainTextUsingTL("james");

        String output1 = james.toString();

        EncodedString jame2 = EncodedString.fromPlainTextUsingTL("james");

        String output2 = jame2.toString();

        assertThat(output1.toString().equals(output2.toString()), is(true));
    }

    @Test
    public void testTransitiveEqualsUsingTL() throws Exception {
        final String name = "james";
        assertThat(EncodedString.fromPlainTextUsingTL(name).plainText(), is(name));
        assertThat(new EncodedString(EncodedString.fromPlainTextUsingTL(name).toString()).plainText(), is(name));
    }

    @Test
    public void testTransitiveEquals() throws Exception {
        final String name = "james";
        assertThat(EncodedString.fromPlainText(name).plainText(), is(name));
        assertThat(new EncodedString(EncodedString.fromPlainText(name).toString()).plainText(), is(name));
    }

    @Test
    public void testEquals() throws Exception {
        final EncodedString JAMES = EncodedString.fromPlainText("james");
        final EncodedString JAMESPrime = EncodedString.fromPlainText("james");
        assertThat(JAMESPrime, is(JAMES));
    }

    @Test
    public void testHashCode() throws Exception {
        final EncodedString JAMES = EncodedString.fromPlainText("james");
        final EncodedString JAMESPrime = EncodedString.fromPlainText("james");
        assertThat(JAMES.hashCode(), is(JAMESPrime.hashCode()));
    }

    @Test
    public void testFromPlainText() throws Exception {
        final EncodedString JAMES = EncodedString.fromPlainText("james");
        assertThat(JAMES.plainText(), is("james"));
    }

    @Test(expected = org.jasypt.exceptions.EncryptionOperationNotPossibleException.class)
    public void testToPlainTextForAnInvalidEncryptedValue() {
        final String encodedText = "eric";
        assertThat(EncodedString.toPlainText(encodedText), is("error"));
    }
}
