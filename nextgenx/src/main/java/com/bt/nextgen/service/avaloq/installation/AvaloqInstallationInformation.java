package com.bt.nextgen.service.avaloq.installation;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;

/**
 * @author l079764 (Andy Barker)
 *
 * Holder interface for details of the avaloq installation in a given environment
 */
public interface AvaloqInstallationInformation
{
	/**
	 * Query to get information about all release packages which are installed on the avaloq database
	 *
	 * @return A list of Kernel release components that make up this avaloq installation
	 */
	List<AvaloqReleasePackage> getAvaloqReleasePackages();

	/**
	 *
	 * @return The date of the original prod baseline (e.g. the date this installation was copied from production before being upgraded
	 */
	DateTime getBaselineDate();


	/**
	 * Method to load a unique description of this installation which can be used to identify equivalent avaloq releases
	 *
	 * @return A unique description of this database installation taking into account baseline date and all installed Release Packages and Changes
	 */
	String getInstallationUid();


	/**
	 * The avaloq database name of the running instance
	 *
	 * @return The avaloq database name
	 */
	String getAvaloqDatabaseName();


}
