package com.bt.nextgen.api.marketdata.v1.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.marketdata.v1.model.CipherInitialisationException;
import com.bt.nextgen.api.marketdata.v1.model.EncryptionFailedException;
import com.bt.nextgen.api.marketdata.v1.model.SsoKeyRequest;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.Configuration;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class MarkitOnDemandKeyService implements SsoKeyService
{
	private static final Logger logger = LoggerFactory.getLogger(MarkitOnDemandKeyService.class);

	private static final String MARKIT_PROP_PREFIX = "markit.on.demand.";

	private static final String MARKIT_SHARED_IV = "markit.on.demand.shared.iv";

	private static final String DEFAULT_ENCODING = "UTF-8";

	private static final String MARKIT_TIMESTAMP_FORMAT= "yyyyMMddHHmmss";

	private static final String MARKIT_ENCRYPTION_TYPE = "AES/CBC/PKCS5Padding";

	private static final DateTimeFormatter markitDateFormatter = DateTimeFormat.forPattern(MARKIT_TIMESTAMP_FORMAT);


	public enum EncryptionStrength
	{
		AES_256_BIT("256bit.aes.key","256bit.key.prefix"),
		AES_128_BIT("aes.key","key.prefix");

		private final String propertyAesKey;
		private final String keyPrefix;

		EncryptionStrength(String properyAesKey,String keyPrefix)
		{
			this.propertyAesKey = properyAesKey;
			this.keyPrefix = keyPrefix;
		}

		public String getPropertyAesKey()
		{
			return this.propertyAesKey;
		}

		public String getKeyPrefix()
		{
			return this.keyPrefix;
		}

	}

	private static final EncryptionStrength DEFAULT_ENCRYPTION_STRENGTH = EncryptionStrength.AES_128_BIT;

	@Autowired
	private Configuration configuration;

	@Autowired
	private UserProfileService userProfileService;
	
	@Autowired
        private UserTierService userTierService;




	@Override public String getEncryptedKey() throws EncryptionFailedException
	{
		return getEncryptedKey(DEFAULT_ENCRYPTION_STRENGTH);
	}

	@Override public String getEncryptedKey(EncryptionStrength strength) throws EncryptionFailedException{
        return this.getEncryptedKey(strength, null);
    }

    @Override
    public String getEncryptedKey(EncryptionStrength strength, AccountKey accountKey) throws EncryptionFailedException {
        ConsistentEncodedString jobProfileId = ConsistentEncodedString
                .fromPlainText(userProfileService.getActiveProfile().getJob().getId());
        SsoKeyRequest request = new SsoKeyRequest(jobProfileId.toString(), userTierService.getUserTier(accountKey),
                getZuluTimeInMarkitFormat(DateTime.now()), String.valueOf(userTierService.isShareEnabled()));
        String encryptedResult = "";
        try {
            encryptedResult = getEncrytpedString(strength, request);
        } catch (CipherInitialisationException error) {
            logger.error("Failed to encrypt the parameters", error);
            throw new EncryptionFailedException(error);
        }

        return encryptedResult;
    }

	public String getEncrytpedString(EncryptionStrength strength, SsoKeyRequest keyRequest) throws CipherInitialisationException
	{

        logger.debug("--------- Data to be encrypted = {}", keyRequest.getFullRequestString());
		String sKey = configuration.getString(MARKIT_PROP_PREFIX + strength.getPropertyAesKey());
		String initialisationVector = configuration.getString(MARKIT_SHARED_IV);


		Cipher aesCipher = initialiseCipher(sKey,MARKIT_ENCRYPTION_TYPE, initialisationVector);

		byte[] cleartext1 = new byte[0];
		try
		{
			cleartext1 = aesCipher.doFinal(keyRequest.getFullRequestString().getBytes(DEFAULT_ENCODING));
		}
		catch (IllegalBlockSizeException|BadPaddingException|UnsupportedEncodingException e)
		{
			logger.error("Error encrypting url param request string", e);
		}
		// base64 encode so that URL is safe to pass via http

		final Base64 base64 = new Base64();

		String sBase64CBC = base64.encodeAsString(cleartext1);
		// using commons instead of the almost defunct Sun encoder.
		//new sun.misc.BASE64Encoder().encode(cleartext1);

        logger.info("Base64 = {}", sBase64CBC);
		String finalBase64Encodedresponse = addBtfgAlgorithmPrefix(strength,sBase64CBC);
		logger.info("WSOD ready encrypted link {}", finalBase64Encodedresponse);
		return finalBase64Encodedresponse;
	}

	private String addBtfgAlgorithmPrefix(EncryptionStrength strength,String base64EncodedString)
	{
		return configuration.getString(MARKIT_PROP_PREFIX + strength.getKeyPrefix()) + base64EncodedString;
	}

	public static String getZuluTimeInMarkitFormat(DateTime time)
	{
		DateTime unformattedDate = time.toDateTime(DateTimeZone.forID("UTC"));
		return markitDateFormatter.print(unformattedDate);
	}

	private Cipher initialiseCipher(String secretKey, String encyptAlgorithm, String initialisationVector) throws CipherInitialisationException
	{
		Cipher aesCipher=null;


		try
		{
			final Base64 base64 = new Base64();
			SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(DEFAULT_ENCODING), "AES");
			aesCipher = Cipher.getInstance(encyptAlgorithm); // defaults to ECB w/PKCS5 padding
			byte[] iv = base64.decode(initialisationVector);

			IvParameterSpec ivspec = new IvParameterSpec(iv);
			aesCipher.init(Cipher.ENCRYPT_MODE, keySpec, ivspec);
		}
		catch (InvalidKeyException|NoSuchAlgorithmException|NoSuchPaddingException|InvalidAlgorithmParameterException|UnsupportedEncodingException e)
		{
			logger.error("Error initialising encryption", e);
			throw new CipherInitialisationException(e);
		}

		return aesCipher;
	}

}
