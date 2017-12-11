package com.bt.nextgen.core.reporting;

import java.io.IOException;
import java.io.InputStream;

/**
 * Abstraction of a report template
 */
public interface ReportTemplate
{
	ReportIdentity getId();

	boolean isAvailable();

	/**
	 * A code representing the type of the report
	 * @return
	 */
	String getType();

	/**
	 * Stream the report contents
	 * @return
	 * @throws IOException
	 */
	InputStream getAsStream() throws IOException;

	/**
	 * Implementations can optionally store a cached 'compiled' version of themselves
	 * @param compiledVersion
	 */
	void setCompiledVersion(Object compiledVersion);

	/**
	 * Return any previously stored compiled version.
	 * @return Can return null if not supported, or #setCompiledVersion was called previously
	 */
	Object getCompiledVersion();
}
