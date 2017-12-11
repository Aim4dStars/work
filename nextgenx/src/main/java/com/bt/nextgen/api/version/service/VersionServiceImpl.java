package com.bt.nextgen.api.version.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.installation.AvaloqChange;
import com.bt.nextgen.service.avaloq.installation.AvaloqInstallationInformation;
import com.bt.nextgen.service.avaloq.installation.AvaloqReleasePackage;
import com.bt.nextgen.service.avaloq.installation.AvaloqVersionIntegrationService;

@Component
public class VersionServiceImpl implements VersionService
{
	private static final String COULD_NOT_LOAD_VERSION = "Could not load version";

	@Autowired
	AvaloqVersionIntegrationService avaloqVersionIntegrationService;

	@Override public String getAvaloqVersion()
	{
		AvaloqInstallationInformation info = getAvaloqInstallationInformation();
		if (null == info) {
			return COULD_NOT_LOAD_VERSION;
		}
		return info.getInstallationUid();
	}

	@Override public AvaloqInstallationInformation getAvaloqInstallationInformation()
	{
		ServiceErrors errors = new FailFastErrorsImpl();
		return avaloqVersionIntegrationService.getAvaloqInstallInformation(errors);
	}

	@Override public void refreshAvaloqVersion() {
		avaloqVersionIntegrationService.refreshAvaloqVersion();
	}

	@Override public String getFullAvaloqVersion()
	{
		ServiceErrors errors = new FailFastErrorsImpl();
		AvaloqInstallationInformation installInfo = avaloqVersionIntegrationService.getAvaloqInstallInformation(errors);
		return (createVersionString(installInfo));
	}

	private static final String RELEASE_SEPARATOR = "\n\r";
	private static final String RELEASE_NAME = "Release name :";
	private static final String RELEASE_VERSION = "Release version :";
	private static final String CHANGE_LIST = "Changes :";
	private static final String CHANGE_SEPARATOR = ",";

	protected String createVersionString(AvaloqInstallationInformation installationInformation)
	{
		if(null==installationInformation){
			return COULD_NOT_LOAD_VERSION;
		}
		StringBuffer versionString = new StringBuffer("Avaloq Version ID:");
		versionString.append(installationInformation.getInstallationUid()).append(RELEASE_SEPARATOR);
		for(AvaloqReleasePackage releasePackage: installationInformation.getAvaloqReleasePackages())
		{
			versionString.append(RELEASE_NAME)
					.append(releasePackage.getAvaloqReleaseName())
					.append(CHANGE_SEPARATOR).append(RELEASE_VERSION)
					.append(releasePackage.getAvaloqPackageUid()).append(RELEASE_SEPARATOR).append(CHANGE_LIST);

			for(AvaloqChange change :releasePackage.getAvaloqChanges()) {
				versionString.append(change.getId()).append(CHANGE_SEPARATOR);
			}
			versionString.append(RELEASE_SEPARATOR).append(RELEASE_SEPARATOR);
		}
		return versionString.toString();
	}

}
