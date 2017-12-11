package com.bt.nextgen.core.util;

import java.security.MessageDigest;

public class PasswordUtil
{
	public static final String HALGM_CHARSET = "CP1252";
	
	public static String createPassword(String password) throws Exception
	{
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(password.getBytes());

		byte byteData[] = md.digest();

		//toDomain the byte to hex format method 1
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++)
		{
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}
	
	
	/**
	 * Generates a byte[] representing encryption key that is used when delivering passwords to groupESB.
	 * Primarily used during the registration/change password process where passwords are supplied.
	 *  
	 * @param halgm encrpytion key as provided by the presentation layer
	 * @return byte[] representing the encrpytion key to be used to decrypt passwords provided by UI
	 * @throws Exception
	 */
	public static byte[] generateEncryptionKeyFromHalgm(String halgm) throws Exception
	{
		return org.apache.commons.ssl.Base64.decodeBase64(halgm.getBytes(HALGM_CHARSET));		
	}

}
