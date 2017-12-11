package com.bt.nextgen.service.integration.transfer.transfergroup;

import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.IncomePreference;
import com.bt.nextgen.service.integration.order.ModelPreferenceAction;
import com.bt.nextgen.service.integration.order.OrderType;
import com.bt.nextgen.service.integration.transaction.TransactionValidation;
import com.bt.nextgen.service.integration.transfer.BeneficialOwnerChangeStatus;
import com.bt.nextgen.service.integration.transfer.TransferType;
import org.joda.time.DateTime;

import java.util.List;

public interface TransferGroupDetails {

    /**
     * Unique identifier of a transfer-order
     * 
     * @return
     */
    public String getTransferId();

    /**
     * OrderType that identifies if it is an external or internal transfer.
     * 
     * @return
     */
    public OrderType getOrderType();

    /**
     * Destination container id for this transfer.
     * 
     * @return
     */
    public String getDestContainerId();

    /**
     * Destination asset id. This is when transferring to a new MP (i.e. not an existing holding).
     * 
     * @return
     */
    public String getDestAssetId();

    /**
     * External transfer type (e.g. CHESS-SPONSORED, BROKER-SPONSOREd etc).
     * 
     * @return
     */
    public TransferType getExternalTransferType();

    /**
     * Underlying assets to be transferred.
     * 
     * @return
     */
    public List<TransferAsset> getTransferAssets();

    /**
     * Indicator if there is a change in beneficial ownership. Only used for external transfer.
     * 
     * @return
     */
    public BeneficialOwnerChangeStatus getChangeOfBeneficialOwnership();

    /**
     * Warning-responses from Avaloq
     * 
     * @return
     */
    public List<TransactionValidation> getWarnings();

    /**
     * Source account key where the assets are transferred FROM.
     * 
     * @return
     */
    public AccountKey getSourceAccountKey();

    /**
     * Destination account key where the assets will be transferred TO.
     * 
     * @return
     */
    public AccountKey getTargetAccountKey();

    /**
     * Managed Portfolio preferences if any.
     * 
     * @return
     */
    public List<ModelPreferenceAction> getPreferenceList();

    /**
     * Date when the transfer was initiated.
     * 
     * @return
     */
    public DateTime getTransferDate();

    public String getSourceContainerId();

    public boolean getCloseAfterTransfer();

    /**
     * Number of days to delay the drawdown process.
     * 
     * @return
     */
    public Integer getDrawdownDelayDays();

    /**
     * Income preference assigned to container if transferring into MP or TMP (e.g. TRANSFER or REINVEST)
     * 
     * @return
     */
    public IncomePreference getIncomePreference();
}
