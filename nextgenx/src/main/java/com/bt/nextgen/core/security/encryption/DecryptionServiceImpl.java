package com.bt.nextgen.core.security.encryption;

import com.bt.nextgen.core.util.Properties;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

@Service
public class DecryptionServiceImpl implements DecryptionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DecryptionServiceImpl.class);
    private static final String ENCRYPTION_KEY = Properties.get("eam.aes256.encryption.key");

    private static final int KEY_SIZE = 32;
    private static final int IV_SIZE = 16;

    private static final BouncyCastleProvider PROVIDER = new BouncyCastleProvider();

    static {
        Security.addProvider(PROVIDER);
    }

    /**
     * Method to decrypt the encrypted test using AES256 algorithm.
     *
     * @param encryptedText : The encrypted text which needs to be decrypted.
     * @param encryptionKey : The secret key used to encryt the text originally.
     * @return Decrypted result.
     * @throws Exception
     */
    @SuppressWarnings({"squid:S1943", "squid:S00112", "squid:MethodCyclomaticComplexity",
            "checkstyle:com.puppycrawl.tools.checkstyle.checks.whitespace.NoLineWrapCheck"})
    @Override
    public String aes256Decryption(String encryptedText, String encryptionKey) {
        try {
            LOGGER.debug("***** The encrypted string is :{}", encryptedText);
            byte[] encryptedTextBytes = Base64.decodeBase64(encryptedText.getBytes());
            byte[] saltBytes = Arrays.copyOfRange(encryptedTextBytes, 0, 32);
            LOGGER.debug("*****Encrypted Byte array: {}", encryptedTextBytes.length);
            byte[] finalencryptedTextBytes = Arrays.copyOfRange(encryptedTextBytes, 32, encryptedTextBytes.length);
            LOGGER.debug("*****final encrypted text: {}", finalencryptedTextBytes.length);

            // Derive the key
            PBEParametersGenerator generator = new PKCS5S2ParametersGenerator();
            generator.init(PBEParametersGenerator.PKCS5PasswordToUTF8Bytes((encryptionKey).toCharArray()), saltBytes, 1000);
            KeyParameter params = (KeyParameter) generator.generateDerivedParameters((KEY_SIZE + IV_SIZE) * 8);
            byte[] keyBytes = params.getKey();

            byte[] key = new byte[32];
            byte[] iv = new byte[16];
            System.arraycopy(keyBytes, 0, key, 0, 32);
            System.arraycopy(keyBytes, 32, iv, 0, 16);

            SecretKeySpec secret = new SecretKeySpec(key, "AES");
            AlgorithmParameterSpec ivSpec = new IvParameterSpec(iv);
            // Decrypt the message
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", BouncyCastleProvider.PROVIDER_NAME);
            cipher.init(Cipher.DECRYPT_MODE, secret, ivSpec);
            byte[] decryptedTextBytes = cipher.doFinal(finalencryptedTextBytes);
            LOGGER.debug("***** The decrypted text is :{}", decryptedTextBytes);
            return new String(decryptedTextBytes);
        } catch (Exception e1) {
            LOGGER.error("Problem in decrypting the text, debugging required. Please check the security jar of JRE as well.", e1);
            throw new RuntimeException(e1);
        }
    }

    @Override
    public String aes256Decryption(String encryptedText) {
        return aes256Decryption(encryptedText, ENCRYPTION_KEY);
    }
}
