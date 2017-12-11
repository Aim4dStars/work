package com.bt.nextgen.api.morningstar.service;

import com.bt.nextgen.api.morningstar.model.MorningstarUrlDto;
import com.bt.nextgen.api.morningstar.model.MorningstarUrlKey;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.core.config.secure.CredentialLocator;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.resource.spi.security.PasswordCredential;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * This is a service to generate Url to Morningstar fund profile page.  In the future this class can be used to generate URL to other
 * Morningstar pages.
 */
@Service
class MorningstarUrlDtoServiceImpl implements MorningstarUrlDtoService {
	private static final Logger logger = LoggerFactory.getLogger(MorningstarUrlDtoServiceImpl.class);

	private static final String MORNINGSTAR_CREDENTIAL_LOCATOR = Properties.getString("morningstar.credentialLocator");
	private static final String MORNINGSTAR_KEY = "morningstar";

	private static final String ENCODING = "ASCII";
	private static final String ENCRYPTION_SCHEME = "DESede";
	private static final String CIPHER_ENCRYPTION_SCHEME = ENCRYPTION_SCHEME + "/CBC/PKCS5Padding";
	private static final String MORNINGSTAR_FUND_PROFILE_URL = Properties.getString("morningstar.fundProfile.url");

	private static final int TIME_TOLERANCE = 5;

	@Autowired
	@Qualifier("avaloqAssetIntegrationService")
	private AssetIntegrationService assetIntegrationService;

	@Autowired
	private UserProfileService userProfileService;

	@Override
	public MorningstarUrlDto find(MorningstarUrlKey key, ServiceErrors serviceErrors) {
		return new MorningstarUrlDto(generateFundProfileUrl(key.getAssetId(), serviceErrors));
	}

	/**
	 * Returns the full URL to Morningstar fund profile page.  Note that only APIR code will be sent to Morningstar.  There are currently no
	 * other types available for us.
	 * <p/>
	 * e.g http://ltqa.morningstar.com/l5k0kjmbi9/snapshot/snapshot.aspx?token=oe5EurclL%2FhdBHlWGBBgXWmZdg5EEv3vODQNCnKkyQY%3D&externalidtype=APIR&externalid=SGP
	 *
	 * @param assetId       the asset ID, which will be used to look up the asset's APIR code
	 * @param serviceErrors the service errors
	 * @return the Morningstar URL, consists of the following params: security token (token), asset type (externalidtype),
	 * APIR code (externalid)
	 */
	private String generateFundProfileUrl(String assetId, ServiceErrors serviceErrors) {
		// Time-Stamp in UTC format
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		final Date currentTime = new Date();
		final String utcTime = dateFormat.format(currentTime);

		// Retrieve asset
		final Asset asset = assetIntegrationService.loadAsset(assetId, serviceErrors);

		// Building up security token data
		final String stringToEncrypt = utcTime + "~~" + TIME_TOLERANCE + "~~" + userProfileService.getUserId();
		final String securityToken = encrypt(stringToEncrypt, serviceErrors);

		return MORNINGSTAR_FUND_PROFILE_URL + "?token=" + securityToken + "&externalidtype=APIR&externalid=" + asset.getAssetCode();
	}

	/**
	 * Encryption function as specified in Morningstar technical specification.
	 *
	 * @param data          the data to encrypt
	 * @param serviceErrors the service errors
	 * @return DES encrypted data in base 64 and converted to URL friendly string.
	 */
	private String encrypt(String data, ServiceErrors serviceErrors) {
		String encryptedData = null;

		// Use credential locator to retrieve the password/encryption key
		CredentialLocator credentialLocator = getCredentialLocator();
		PasswordCredential locator = credentialLocator.locateByName(MORNINGSTAR_KEY);

		String encryptionKey = String.valueOf(locator.getPassword());

		try {
			// Use commons implementation instead of Sun's defunct BASE64Encoder/BASE64Decoder
			final Base64 base64 = new Base64();

			// Key and IV is from the same string, separated by a space
			final String[] encryptionPair = encryptionKey.split(" ");

			final SecretKeySpec key = new SecretKeySpec(base64.decode(encryptionPair[0]), ENCRYPTION_SCHEME);
			final IvParameterSpec iv = new IvParameterSpec(base64.decode(encryptionPair[1]));

			final Cipher dataEncryptor = Cipher.getInstance(CIPHER_ENCRYPTION_SCHEME);
			dataEncryptor.init(Cipher.ENCRYPT_MODE, key, iv);
			final byte[] encryptedText = dataEncryptor.doFinal(data.getBytes(ENCODING));

			encryptedData = URLEncoder.encode(base64.encodeToString(encryptedText), ENCODING);
		} catch (IOException | NoSuchPaddingException | InvalidAlgorithmParameterException | NoSuchAlgorithmException |
				IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
			final String errorMsg = "Unable to encrypt session token \"" + data + "\": " + e;
			serviceErrors.addError(new ServiceErrorImpl(errorMsg));
			logger.error(errorMsg);
		}

		return encryptedData;
	}

	/**
	 * Retrieve the credential locator, which can be either properties-based or WAS-based
	 *
	 * @return credential locator class
	 */
	private CredentialLocator getCredentialLocator() {
		CredentialLocator locator;

		try {
			locator = (CredentialLocator) Class.forName(MORNINGSTAR_CREDENTIAL_LOCATOR).newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			logger.error("Could not find the Morningstar credential locator via morningstar.credentialLocator", e);
			throw new IllegalArgumentException(e);
		}

		return locator;
	}
}
