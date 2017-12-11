package com.bt.nextgen.service.avaloq.contributionhistory;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.cashcategorisation.model.ContributionClassification;
import com.bt.nextgen.service.integration.externalasset.builder.IsoDateTimeConverter;
import org.joda.time.DateTime;

import javax.annotation.concurrent.Immutable;
import java.math.BigDecimal;

/**
 * Avaloq business object for contribution summary for a apecific type.
 */
@ServiceBean(xpath = "buc_head")
@Immutable
public class ContributionSummaryByTypeImpl implements ContributionSummaryByType {
    /**
     * Avaloq document id.
     * It is used to find the latest contribution as Avaloq guarantees the allocation of docId to
     * be in the sequence of contributions processing.
     */
    @ServiceElement(xpath = "doc_id/val")
    private Long docId;

    /**
     * Contribution classification.
     */
    @ServiceElement(xpath = "sa_cap_type/val", converter = ContributionClassificationConverter.class)
    private ContributionClassification contributionClassification;

    /**
     * Contribution type.
     */
    @ServiceElement(xpath = "sa_contri_type/val", converter = ContributionTypeConverter.class)
    private ContributionType contributionType;

    /**
     * Date of the last contribution for the contribution type represented by this summary.
     * <br/>
     * Note that Avaloq does not provide time component in this attribute.
     */
    @ServiceElement(xpath = "val_date/val", converter = IsoDateTimeConverter.class)
    private DateTime lastContributionDate;

    /**
     * Total amount of contribution for the contribution type represented by this summary.
     */
    @ServiceElement(xpath = "gross_tc/val", converter = BigDecimalConverter.class)
    private BigDecimal amount;


    @Override
    public Long getDocId() {
        return docId;
    }

    public ContributionClassification getContributionClassification() {
        return contributionClassification;
    }

    public ContributionType getContributionType() {
        return contributionType;
    }

    public DateTime getLastContributionDate() {
        return lastContributionDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setContributionClassification(ContributionClassification contributionClassification) {
        this.contributionClassification = contributionClassification;
    }

    public void setContributionType(ContributionType contributionType) {
        this.contributionType = contributionType;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
