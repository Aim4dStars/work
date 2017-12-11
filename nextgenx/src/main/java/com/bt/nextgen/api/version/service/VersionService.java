package com.bt.nextgen.api.version.service;

import com.bt.nextgen.service.avaloq.installation.AvaloqInstallationInformation;

public interface VersionService
{

	String getAvaloqVersion();

	void refreshAvaloqVersion();

	AvaloqInstallationInformation getAvaloqInstallationInformation();

	String getFullAvaloqVersion();


}
