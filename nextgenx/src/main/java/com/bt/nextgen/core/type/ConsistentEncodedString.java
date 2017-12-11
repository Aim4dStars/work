package com.bt.nextgen.core.type;

import java.io.Serializable;

import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.salt.SaltGenerator;
import org.jasypt.salt.ZeroSaltGenerator;

/**
 * This class generates the encoded string which is consistent for the same message input everytime.
 * 
 */
public class ConsistentEncodedString implements Serializable
{
    private static final long serialVersionUID = -2767730848351039273L;

    /**
     * Algorithm that produces the constant result as there is not salt added.
     */
    private static EncodingAlgorithm constantCodeAlgorithm = new EncodingAlgorithm()
    {
        private final PBEStringEncryptor bte;
        {
        	SaltGenerator saltGenerator = new ZeroSaltGenerator();
            StandardPBEStringEncryptor spse = new StandardPBEStringEncryptor();
            spse.setPassword("password");
            spse.setStringOutputType("HEX");
            spse.setSaltGenerator(saltGenerator);
            bte = spse;
        }

        @Override
        public String encode(String me)
        {
            return bte.encrypt(me);
        }

        @Override
        public String decode(String me)
        {
            return bte.decrypt(me);
        }
    };

    private final String encodedData;
    private final String plainText;

    /**
     * The encodedData is assumed to have been encoded via this class
     *
     * @param encodedData
     */
    public ConsistentEncodedString(String encodedData)
    {
        this(encodedData, constantCodeAlgorithm.decode(encodedData));
    }

    /**
     * The encodedData is assumed to have been encoded via this class
     *
     * @param encodedData
     * @param plainText
     */
    private ConsistentEncodedString(String encodedData, String plainText)
    {
        this.encodedData = encodedData;
        this.plainText = plainText;
    }

    /**
     * Return the contents, decoded.
     *
     * @return The contents of this encoded string in plain text
     */
    public String plainText()
    {
        return plainText;
    }

    @Override
    public String toString()
    {
        return encodedData;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        ConsistentEncodedString that = (ConsistentEncodedString)o;

        if (plainText != null ? !plainText.equals(that.plainText) : that.plainText != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return plainText != null ? plainText.hashCode() : 0;
    }

    /**
     * Create a EncodedString from a given string
     * @param plainValue
     * @return
     */
    public static ConsistentEncodedString fromPlainText(String plainValue)
    {
        return new ConsistentEncodedString(constantCodeAlgorithm.encode(plainValue), plainValue);
    }


    /**
     * Given an encoded String, return a decoded version
     * @param encodedValue
     * @return
     */
    public static String toPlainText(String encodedValue)
    {
        return new ConsistentEncodedString(encodedValue).plainText();
    }

    public static interface EncodingAlgorithm
    {
        String encode(String me);

        String decode(String me);
    }
}
