package com.bt.nextgen.service.avaloq.installation;


import com.bt.nextgen.service.ServiceErrors;

/**
 * @author l079764 (Andy Barker)
 */
public interface AvaloqVersionIntegrationService
{

	/**
	 * Get information about the instance of avaloq that the running UI is connected to
	 *
	 * @return Installation information about the running avaloq
	 */
	AvaloqInstallationInformation getAvaloqInstallInformation(ServiceErrors errors);


	/**
	 * Convenience method to be run after installation of new changes
	 * This will trigger a full reload version information and a recalc of the avaloq version
	 *
	 */
	void refreshAvaloqVersion();

}
