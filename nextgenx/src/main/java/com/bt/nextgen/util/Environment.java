package com.bt.nextgen.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bt.nextgen.core.util.SETTINGS;

public class Environment
{
	private static final Logger logger = LoggerFactory.getLogger(Environment.class);

	private final static String HOSTNAME_HASH;
	static {
		String hostname = "UNKNOWN";
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			logger.warn("Cannot identify my hostname, world broken error using UNKNOWN", e);
		}
		HOSTNAME_HASH = DigestUtils.sha256Hex(hostname);
		logger.info("Hostname hash created {} -> {}", hostname, HOSTNAME_HASH);
	}

	public enum ENVIRONMENT
	{
		CI, DEV, TEST, DEMO, INTEGRATION, TRAIN, AUTOMATED_TESTING, SIT, UAT, SVP, PROD
	}

	;

	public static ENVIRONMENT environment()
	{
		return ENVIRONMENT.valueOf(SETTINGS.ENVIRONMENT.value());
	}

	public static boolean isDevelopment()
	{
		return ENVIRONMENT.DEV.equals(environment());
	}

	public static boolean isIntegration()
	{
		return ENVIRONMENT.INTEGRATION.equals(environment());
	}

	public static boolean isSit()
	{
		return ENVIRONMENT.SIT.equals(environment());
	}

	public static boolean isUat()
	{
		return ENVIRONMENT.UAT.equals(environment());
	}

	public static boolean isTest()
	{
		return ENVIRONMENT.TEST.equals(environment());
	}

	public static boolean isDemo()
	{
		return ENVIRONMENT.DEMO.equals(environment());
	}

	public static boolean isProduction()
	{
		return ENVIRONMENT.PROD.equals(environment());
	}

    public static boolean isTraining()
    {
        return ENVIRONMENT.TRAIN.equals(environment());
    }
	/**
	 * make things read easier
	 *
	 * @return
	 */
	public static boolean notProduction()
	{
		return !isProduction();
	}

	public static String getHostnameHash() {
		return HOSTNAME_HASH;
	}
}
