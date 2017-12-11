package com.bt.nextgen.api.account.v2.model.performance;

import com.bt.nextgen.api.account.v2.model.DateRangeAccountKey;
import org.joda.time.DateTime;

@Deprecated
public class AccountBenchmarkPerformanceKey extends DateRangeAccountKey {
    private String benchmarkId;

    public AccountBenchmarkPerformanceKey(String accountId, DateTime startDate, DateTime endDate, String benchmarkId) {
        super(accountId, startDate, endDate);
        this.benchmarkId = benchmarkId;
    }

    public String getBenchmarkId() {
        return benchmarkId;
    }

    public void setBenchmarkId(String benchmarkId) {
        this.benchmarkId = benchmarkId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((benchmarkId == null) ? 0 : benchmarkId.hashCode());
        return result;
    }

    @SuppressWarnings({ "squid:MethodCyclomaticComplexity", "squid:S1142" })
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        AccountBenchmarkPerformanceKey other = (AccountBenchmarkPerformanceKey) obj;
        if (benchmarkId == null) {
            if (other.benchmarkId != null)
                return false;
        } else if (!benchmarkId.equals(other.benchmarkId))
            return false;
        return true;
    }

}