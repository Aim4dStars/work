package com.bt.nextgen.service.avaloq.installation.impl;

import java.util.Collections;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.impl.auth.UnsupportedDigestAlgorithmException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.avaloq.installation.AvaloqInstallationInformation;
import com.bt.nextgen.service.avaloq.installation.AvaloqReleasePackage;

@ServiceBean(
	xpath = "//data",
	type = ServiceBeanType.CONCRETE
)
public class AvaloqInstallationInformationImpl extends AvaloqBaseResponseImpl implements AvaloqInstallationInformation
{

	private static final Logger logger = LoggerFactory.getLogger(AvaloqInstallationInformationImpl.class);

	@ServiceElementList(xpath="release_list/release",type=AvaloqReleasePackageImpl.class)
	private List<AvaloqReleasePackage> avaloqReleasePackages;

	@Override public List<AvaloqReleasePackage> getAvaloqReleasePackages()
	{
		return avaloqReleasePackages;
	}

	private String installationUid;

	@Override public DateTime getBaselineDate()
	{
		return null;
	}

	@Override public String getInstallationUid()
	{
		if(this.installationUid==null)
		{
			this.installationUid =createUniqueInstallationId(this.avaloqReleasePackages);
		}
		return this.installationUid;
	}


	@Override public String getAvaloqDatabaseName()
	{
		//TODO get the database name from the ABS server.
		return null;
	}


	/**
	 *
	 * Method that sorts the changes and releases which make up and avaloq installation then uses the information to
	 * create a unique has id that identifies this release.
	 *
	 * This method will be able to determine equivalence even if one avaloq has connectivity to the build server and the
	 * other does not.
	 *
	 * @param avaloqReleasePackages
	 * @return A unique Id that identifies this installation version
	 */
	private static String createUniqueInstallationId(List<AvaloqReleasePackage> avaloqReleasePackages)
	{
		Collections.sort(avaloqReleasePackages);
		StringBuffer allPackages = new StringBuffer();
		for(AvaloqReleasePackage releasePackage:avaloqReleasePackages)
			allPackages.append(releasePackage.getAvaloqReleaseName()).append(",").append(releasePackage.getAvaloqPackageUid()).append(",");

		String packageList = allPackages.toString();

		logger.debug("List of changes is {}", packageList );
		try
		{
			return DigestUtils.md5Hex(packageList);

		}catch(UnsupportedDigestAlgorithmException err)
		{
			logger.info("Failed to load MD5 sum algorith, setting unique release ID to be complete change string",err);
			return packageList;
		}

	}


}
