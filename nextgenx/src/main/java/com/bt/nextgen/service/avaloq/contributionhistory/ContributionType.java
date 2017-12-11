package com.bt.nextgen.service.avaloq.contributionhistory;

import javax.annotation.concurrent.Immutable;

/**
 * Type of contribution.
 */
@Immutable
public class ContributionType {
    /**
     * Contribution tyoe id.
     */
    private String id;

    /**
     * Label for contribution type.
     */
    private String label;


    /**
     * Ctor.
     *
     * @param id    Contribution tyoe id.
     * @param label Label for contribution type.
     */
    public ContributionType(String id, String label) {
        this.id = id;
        this.label = label;
    }


    /**
     * Get the contribution type id.
     *
     * @return
     */
    public String getId() {
        return id;
    }


    /**
     * Get the label for the contribution type.
     *
     * @return
     */
    public String getLabel() {
        return label;
    }
}
