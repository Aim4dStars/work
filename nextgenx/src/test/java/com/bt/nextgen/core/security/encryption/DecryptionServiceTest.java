package com.bt.nextgen.core.security.encryption;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DecryptionServiceTest {

    private DecryptionService decryptionService;

    @Before
    public void setup() {
        decryptionService = new DecryptionServiceImpl();
    }

    @Test
    public void testAes256Decryption() {
        String decryptedTextPassingKey = decryptionService.aes256Decryption(
                "oAUpZpsj1EEDgNqRGFWDPcjrDgFqkemlNN2i2wCW0Q1cl9xjAlKpox5mUpx1h7l9EcRROBZRkNgix3mm/wDCfw==",
                "E75B92A3-3299-4407-A913-C5CA196B3CAB");

        Assert.assertThat(decryptedTextPassingKey, Is.is("032240032240123456"));

        String decryptedText = decryptionService
                .aes256Decryption("oAUpZpsj1EEDgNqRGFWDPcjrDgFqkemlNN2i2wCW0Q1cl9xjAlKpox5mUpx1h7l9EcRROBZRkNgix3mm/wDCfw==");
        Assert.assertThat(decryptedText, Is.is("032240032240123456"));

        decryptedText = decryptionService.aes256Decryption("/n+MNQaDnEawInd+CJ4yL6c/qvTowxykywtLF1Pnd18SJlWVy/isNL/4ULU2uAD6I9gpEXMKo+ZpKnPDGrvqkg==");
        Assert.assertThat(decryptedText, Is.is("032240032240123456"));

        decryptedText = decryptionService.aes256Decryption("iLMIB6y/N6WeD675NV6lqhUA8kY1pjVoeWfeSFS1x04kKAyh0s4cKGhvgYmWA/5g");
        Assert.assertThat(decryptedText, Is.is("262786120055835"));

        decryptedText = decryptionService
                .aes256Decryption("ltzhZPRGAOfcH9u/J8cJ+iB8x8JN3VyOllqqqJJ8NUIYF8q+MThcpAzkXvR3LI3C");

        Assert.assertThat(decryptedText, Is.is("262786120055835"));

        decryptedText = decryptionService.aes256Decryption("TeQjdGv3C/tBwkXvt0baqEfbq0FTq0YS07PNenCqHnnm6p2zAK1wtvR238bt760I");

        Assert.assertThat(decryptedText, Is.is("262786120055835"));

        decryptedText = decryptionService.aes256Decryption("IfkITLGIo2mSwNYCJoWrhf8La8phe3ZNwpm0Zt2KLDwoRdjvC1DTnK0vGTbQA/Jo");

        Assert.assertThat(decryptedText, Is.is("262786120055835"));
    }
}
