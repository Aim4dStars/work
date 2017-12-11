package com.bt.nextgen.service.avaloq.installation;

import java.util.List;

import org.joda.time.DateTime;

/**
 * Interface which describes an avaloq change (commit)
 *
 * This has details about when it was installed, who worked on it and the avaloq task id
 *
 * Some of these fields are null when the ABS cannot connect to a build server (e.g. EPS)
 *
 * @author l079764 (Andy Barker)
 */
public interface AvaloqChange extends Comparable<AvaloqChange>
{


	/**
	 * This is always present
	 *
	 * @return The unique change id
	 */
	String getId();

	/**
	 * Gets the name of the change, this requires build server connectivity
	 *
	 * @return A semantic name of the change
	 */
	String getName();

	/**
	 *
	 * @return the time was installed on the given server
	 */
	DateTime getInstallationTime();

	/**
	 * Get all of the authors who were able to work on this change
	 *
	 * This requires build server connectivity
	 * @return A list of change authors
	 */
	List<String> getAuthors();

	/**
	 *
	 * This requires build server connectivity
	 * @return The task id of the change
	 */
	String getTaskId();

	/**
	 *
	 * @return The semantic name of the task
	 */
	String getTaskName();

}
