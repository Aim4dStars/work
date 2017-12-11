package com.bt.nextgen.service.avaloq.installation;

import java.util.List;

/**
 * @author l079764 (Andy Barker)
 *
 * Holder object which describes an avaloq release package which has been applied to an avaloq installation
 *
 */
public interface AvaloqReleasePackage extends Comparable<AvaloqReleasePackage>
{

	/**
	 * The names of the package of changes identifying a group of
	 *
	 * @return The Avaloq name for the release package
	 */
	String getAvaloqReleaseName();

	/**
	 *
	 * @return A list of changes which have been applies as part of this release package
	 */
	List<AvaloqChange> getAvaloqChanges();


	/**
	 *
	 * @return a unique ID for this package based on the installed changes and the name of the package
	 */
	String getAvaloqPackageUid();

}
