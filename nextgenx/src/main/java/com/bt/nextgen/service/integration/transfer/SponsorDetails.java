package com.bt.nextgen.service.integration.transfer;

import com.bt.nextgen.service.integration.account.AccountKey;

/**
 * Each attribute will contain different details of the sponsor/custodian depending on the in-specie transfer type.
 * 
 * @author m028796
 * 
 */
public interface SponsorDetails {

    /**
     * Retrieve the Chess-sponsor Id / Asset Registry Id.
     * 
     * @return
     */
    public String getSponsorId();

    /**
     * When transferring in Managed Funds, this will return the Custodian or Platform id.
     * 
     * @return
     */
    public String getPlatformId();

    /**
     * Depending on the type of in-specie-transfer, this field will hold the HIN / Account number
     * 
     * @return
     */
    public String getInvestmentId();

    /**
     * In the case of Listed securities issuer sponsored transfer, this attribute will contain the SRN value.
     * 
     * @return
     */
    public String getRegistrationDetails();

    public AccountKey getAccountKey();

    /**
     * Container-id of the Managed-portfolio.
     * 
     * @return
     */
    public String getSourceContainerId();

    /**
     * CHESS Sponsor name.
     * 
     * @return
     */
    public String getSponsorName();
}
