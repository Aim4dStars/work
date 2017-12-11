package com.bt.nextgen.service.avaloq.pension;

/**
 * Interface for pension commencement status.
 */
public interface PensionCommencementStatus {
    /**
     * Document Id for pension commencement request.
     *
     * @return {@code non-null} if pension commencement is in progress, {@code null} otherwise.
     */
    Long getDocId();
}
