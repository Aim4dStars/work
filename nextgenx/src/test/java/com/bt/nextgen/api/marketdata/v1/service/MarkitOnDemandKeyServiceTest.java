package com.bt.nextgen.api.marketdata.v1.service;

import com.bt.nextgen.api.marketdata.v1.model.SsoKeyRequest;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import org.apache.commons.configuration.Configuration;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/*
 * Some Test Cases as will not run without an encryption platform that supports 256 bit AES (not in standard Java)
 */
@RunWith(MockitoJUnitRunner.class)
public class MarkitOnDemandKeyServiceTest
{

	private final Logger logger = LoggerFactory.getLogger(MarkitOnDemandKeyServiceTest.class);

	@InjectMocks
	public MarkitOnDemandKeyService modService;

	@Mock
	public Configuration configuration;

	@Mock
	public UserProfileService ups;
	
	@Mock
	public UserTierService userTierService;

	private static final String aes128BitKey = "very1very2secret";
	private static final String aes256BitKey = "ltMoaRnUWuxrZRuSA3nXMHceEGGFX1RI";

	@Before
	public void setupConfiguration()
	{

		Mockito.when(configuration.getString(eq("markit.on.demand.aes.key"))).thenReturn(aes128BitKey);
		Mockito.when(configuration.getString(eq("markit.on.demand.256bit.aes.key"))).thenReturn(aes256BitKey);

		Mockito.when(configuration.getString(eq("markit.on.demand.key.prefix"))).thenReturn("YYY2490_");
		Mockito.when(configuration.getString(eq("markit.on.demand.256bit.key.prefix"))).thenReturn("YYY2491_");

		Mockito.when(configuration.getString(eq("markit.on.demand.default.tier"))).thenReturn("Test%20Tier");
		Mockito.when(configuration.getString(eq("markit.on.demand.shared.iv"))).thenReturn("AAAAAAAAAAAAAAAAAAAAAA==");
        Mockito.when(configuration.getString(eq("markit.on.demand.adviser.tier"))).thenReturn("realtime");
        Mockito.when(configuration.getString(eq("markit.on.demand.investor.tier"))).thenReturn("delayed");

        UserProfile bob = Mockito.mock(UserProfile.class);
        when(bob.getBankReferenceId()).thenReturn("gcm1");
        when(bob.getJob()).thenReturn(JobKey.valueOf("123123123"));
        when(bob.getProfileId()).thenReturn("333333333");

		Mockito.when(ups.getActiveProfile()).thenReturn(bob);
        Mockito.when(userTierService.getUserTier(null)).thenReturn("Test%20Tier");

	}

	@Test
	public void testGetEncrytpedString() throws Exception
	{
		String value = modService.getEncrytpedString(MarkitOnDemandKeyService.EncryptionStrength.AES_128_BIT,new SsoKeyRequest("TestBT","Test%20Tier","20150603223234", "true"));
		assertThat(value, is(notNullValue()));
		assertThat(value, is("YYY2490_M6+nFIzJrUfG1AvAv6O/BQGmh9WJpzB/BMHvsKxw8SBqEVT9aBcb0JcYy7k4xjFEjbdJkkFIqpfRmPkD978v5FH3aOUJ7aOOoyywnvNhYRyfNPdVing+3CAqEoJgpw/e"));
		assertThat(decryptTest(value.substring(8),aes128BitKey),
                is("User_ID=TestBT&User_Tier=Test%20Tier&User_TimeStamp=20150603223234&enableShare=true"));
	}

	@Test
	@Ignore //this doesn't work in standard JAVA - you need the weapons grade encryption
	public void testGet256BitEncrytpedString() throws Exception
	{
		Mockito.when(configuration.getString(eq("markit.on.demand.aes.key"))).thenReturn(aes256BitKey);
		String value = modService.getEncrytpedString(MarkitOnDemandKeyService.EncryptionStrength.AES_256_BIT,new SsoKeyRequest("TestBT","Test%20Tier","20150603223234", "true"));
		assertThat(value, is(notNullValue()));
		//assertThat(value, is("YYY2491_oVU3LUkQuQhX/Dshh4oZQWkKVCcglSEsSll4E4xEk4mi5e8aSBhYOLfrZKDVGxZas+KNNMIFS3Y/\r\nXiKVGCokz/Mtq84Zgxtbz9OCK2cv1Jw="));
		assertThat(decryptTest(value.substring(8), aes256BitKey),
			is("User_ID=TestBT&User_Tier=Test%20Tier&User_TimeStamp=20150603223234"));
	}


	/*
	 * Simple utility test case to decrypt the API response
	 * TODO - remove once this has been accepted.
	 */
	@Ignore
	@Test
	public void simpleDecryptTest() throws Exception
	{
		String cypherText = "CnjXAqI1n5WEqZ5whUC5NsmVntupxhyKMsjJ/7sXxImSA0zY3O49eptQks4JD6rjJkUFCE+j4tTLK4C6Pg2HggmaOd4OZBJIXahe7PPc1uo=";
		String uncypherText = decryptTest(cypherText,"ltMoaRnUWuxrZRuS");
		assertThat(uncypherText, is("Sausages"));

	}


	private String decryptTest(String encryptedString, String key) throws Exception
	{
		Cipher aesCipher=null;

		byte[] decoded = new sun.misc.BASE64Decoder().decodeBuffer(encryptedString);

		SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
		// Create the cipher

		aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		byte[] iv = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		IvParameterSpec ivspec = new IvParameterSpec(iv);
		// Initialize the same cipher for decryption
		aesCipher.init(Cipher.DECRYPT_MODE, keySpec, ivspec);
		// decrypt
		String decrypted = new String(aesCipher.doFinal(decoded));
		logger.info("Cleartext: " + decrypted);
		return decrypted;
	}


	@Test
	public void testDateFormatter()
	{
		DateTime testTime = new DateTime(2009, 12, 25, 12, 0, 0, 0, DateTimeZone.forID("Australia/Sydney"));
		String result = MarkitOnDemandKeyService.getZuluTimeInMarkitFormat(testTime);
		assertThat(result, is(notNullValue()));
		assertThat(result,is("20091225010000"));


	}

	@Test
	public void testEncryptionString() throws Exception
	{
		DateTime now = new DateTime();
		String test = modService.getEncryptedKey();
		assertThat(test,is(notNullValue()));
		String decrypted = decryptTest(test.substring(8),aes128BitKey);
		logger.info("Full decrypted param string {}", decrypted);
		assertThat(decrypted, containsString("User_ID="));
		String encodedUserId = decrypted.substring((decrypted.indexOf("User_ID=") + 8), (decrypted.indexOf("User_Tier=")-1));
		logger.info("Encoded Id = {}", encodedUserId);
		ConsistentEncodedString decodedUserId = new ConsistentEncodedString(encodedUserId);
		assertThat(decodedUserId.plainText(), is ("123123123"));
		assertThat(decrypted, containsString("User_Tier=Test%20Tier"));

		String timeStart =  MarkitOnDemandKeyService.getZuluTimeInMarkitFormat(now);

		assertThat(decrypted, containsString("User_TimeStamp="+timeStart.substring(0,12)));
	}


}
