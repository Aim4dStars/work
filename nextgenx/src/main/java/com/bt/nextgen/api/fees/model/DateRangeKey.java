package com.bt.nextgen.api.fees.model;

import org.joda.time.DateTime;

// IDE generated equals method
@SuppressWarnings({ "squid:MethodCyclomaticComplexity", "squid:S1142",
           "checkstyle:com.puppycrawl.tools.checkstyle.checks.metrics.NPathComplexityCheck", })
public class DateRangeKey {

	private DateTime startDate;
	private DateTime endDate;
	
	public DateRangeKey(DateTime startDate, DateTime endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
    public DateTime getStartDate() {
        return startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

	@Override
	public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
        result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
        return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DateRangeKey other = (DateRangeKey) obj;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}
}
