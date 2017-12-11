package com.bt.nextgen.core.reporting;

/**
 * Encapsulates what identifiers are required for a report.
 */
public interface ReportIdentity
{
	String getTemplateKey();

	/**
	 * Simple first implementation that supports ids as a String
	 */
	public static class ReportIdentityString implements ReportIdentity
	{
		private final String key;

		private ReportIdentityString(String key)
		{
			this.key = key;
		}

		public static ReportIdentityString asIdentity(String key)
		{
			return new ReportIdentityString(key);
		}

		@Override public String getTemplateKey()
		{
			return key;
		}

		@Override public String toString()
		{
			return "ReportIdentityString{" +
				"key='" + key + '\'' +
				'}';
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o)
			{
				return true;
			}
			if (o == null || getClass() != o.getClass())
			{
				return false;
			}

			ReportIdentityString that = (ReportIdentityString) o;

			if (key != null ? !key.equals(that.key) : that.key != null)
			{
				return false;
			}

			return true;
		}

		@Override
		public int hashCode()
		{
			return key != null ? key.hashCode() : 0;
		}
	}

}
