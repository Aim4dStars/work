package com.bt.nextgen.core.security.encryption;

public interface DecryptionService {
    @SuppressWarnings("squid:S00112")
    String aes256Decryption(String encryptedText, String encryptionKey);
    String aes256Decryption(String encryptedText);
}
