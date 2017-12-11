package com.bt.nextgen.service.avaloq.transactionhistory;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.payeedetails.ContainerTypeConverter;
import com.bt.nextgen.service.integration.Origin;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationType;
import com.bt.nextgen.service.integration.transactionhistory.BTOrderType;
import com.bt.nextgen.service.integration.transactionhistory.TransactionHistory;
import com.bt.nextgen.service.integration.transactionhistory.TransactionSubType;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

@ServiceBean(xpath = "evt")
public class TransactionHistoryImpl implements TransactionHistory {
    private static final String XML_HEADER = "./evt_head_list/evt_head/";
    private static final String XPATH_KEY_VALUE = "./evt_head_list/evt_head/detail_list/detail/key/val";

    private static final String CONT_HEADER = "../../../../cont_head_list/cont_head/";
    private static final String POS_HEADER = "../../pos_head_list/pos_head/";
    private static final String BP_HEAD = "../../../../../../bp_head_list/";

    private static final String CONT_TYPE_LIST = "contri_type_list/contri_type";
    private static final String SUB_TYPE_LIST = "sa_type_list/sa_type";

    /**
     * {@link CodeCategory}
     */
    private static final String CODE_CATEGORY_META_TYPE = "META_TYPE";

    /**
     * {@link CodeCategory}
     */
    private static final String CODE_CATEGORY_ORDER_TYPE = "TRANSACTION_ORDER_TYPE";

    // ==================== Fields mapped from response ====================

    @ServiceElement(xpath = XML_HEADER + "evt_id/val")
    private Integer evtId;

    @ServiceElement(xpath = XML_HEADER + "doc_id/val")
    private String docId;

    @ServiceElement(xpath = XML_HEADER + "copr_seq_nr/val", converter = BigDecimalConverter.class)
    private BigDecimal coprSeqNr;

    @ServiceElement(xpath = BP_HEAD + "bp_head/bp_id/val")
    private String accountId;

    // TODO : add a converter for metaType
    @ServiceElement(xpath = XML_HEADER + "meta_typ_id/val", staticCodeCategory = CODE_CATEGORY_META_TYPE)
    private String metaType;

    // TODO : add a converter for orderType
    @ServiceElement(xpath = XML_HEADER + "order_type_id/val", staticCodeCategory = CODE_CATEGORY_ORDER_TYPE)
    private String orderType;

    @ServiceElement(xpath = XML_HEADER + "ui_ot_id/val", staticCodeCategory = "UI_ORDER_TYPE")
    private BTOrderType btOrderType;

    @ServiceElement(xpath = XML_HEADER + "ui_ot_id/val")
    private String transactionType;

    @ServiceElement(xpath = POS_HEADER + "/bal/val", converter = BigDecimalConverter.class)
    private BigDecimal balance;

    @ServiceElement(xpath = POS_HEADER + "/bal/val", converter = BigDecimalConverter.class)
    private BigDecimal closingBalance;

    @ServiceElement(xpath = XML_HEADER + "qty/val", converter = BigDecimalConverter.class)
    private BigDecimal amount;

    @ServiceElement(xpath = XML_HEADER + "trx_date/val", converter = DateTimeTypeConverter.class)
    private DateTime effectiveDate;

    @ServiceElement(xpath = XML_HEADER + "val_date/val", converter = DateTimeTypeConverter.class)
    private DateTime valDate;

    @ServiceElement(xpath = XPATH_KEY_VALUE + "[contains(.,'clear_date')]/ancestor::detail[1]/val/val", converter = DateTimeTypeConverter.class)
    private DateTime clearDate;

    @ServiceElement(xpath = XPATH_KEY_VALUE + "[contains(.,'payer_name')]/ancestor::detail[1]/val/val")
    private String payerName;

    @ServiceElement(xpath = XPATH_KEY_VALUE
            + "[contains(.,'net_amount')]/ancestor::detail[1]/val/val", converter = BigDecimalConverter.class)
    private BigDecimal netAmount;

    @ServiceElement(xpath = XPATH_KEY_VALUE + "[contains(.,'payee_name')]/ancestor::detail[1]/val/val")
    private String payeeName;

    @ServiceElement(xpath = XPATH_KEY_VALUE + "[contains(.,'payer_bsb')]/ancestor::detail[1]/val/val")
    private String payerBsb;

    @ServiceElement(xpath = XPATH_KEY_VALUE + "[contains(.,'payee_bsb')]/ancestor::detail[1]/val/val")
    private String payeeBsb;

    @ServiceElement(xpath = XPATH_KEY_VALUE + "[contains(.,'payer_bank_acc_nr')]/ancestor::detail[1]/val/val")
    private String payerAccount;

    @ServiceElement(xpath = XPATH_KEY_VALUE + "[contains(.,'payee_acc_nr')]/ancestor::detail[1]/val/val")
    private String payeeAccount;

    @ServiceElement(xpath = XPATH_KEY_VALUE + "[contains(.,'payee_biller_code')]/ancestor::detail[1]/val/val")
    private String payeeBillerCode;

    @ServiceElement(xpath = XPATH_KEY_VALUE + "[contains(.,'payee_custr_ref')]/ancestor::detail[1]/val/val")
    private String payeeCustrRef;

    @ServiceElement(xpath = XML_HEADER + "extl_book_text/val")
    private String bookingText;

    @ServiceElement(xpath = XPATH_KEY_VALUE + "[contains(.,'trx_descn')]/ancestor::detail[1]/val/val")
    private String transactionDescription;

    @ServiceElement(xpath = XPATH_KEY_VALUE + "[contains(.,'doc_descn')]/ancestor::detail[1]/val/val")
    private String docDescription;

    @ServiceElement(xpath = XML_HEADER + "evt_status_id/val")
    private String status;

    @ServiceElement(xpath = CONT_HEADER + "cont_type_id/val", converter = ContainerTypeConverter.class)
    private ContainerType contType;

    @ServiceElement(xpath = CONT_HEADER + "cont_id/val")
    private String contId;

    @ServiceElement(xpath = POS_HEADER + "pos_name/annot/ctx/id")
    private String posId;

    @ServiceElement(xpath = POS_HEADER + "pos_name/val")
    private String posName;

    @ServiceElement(xpath = CONT_HEADER + "asset_id/val")
    private String contAssetId;

    @ServiceElement(xpath = POS_HEADER + "asset_id/val")
    private String posAssetId;

    @ServiceElement(xpath = XML_HEADER + "ref_asset_id/val")
    private String refAssetId;

    @ServiceElement(xpath = XML_HEADER + "cash_cat_type_id/val", staticCodeCategory = "CASH_CATEGORY_TYPE")
    private CashCategorisationType cashCategorisationType;

    @ServiceElement(xpath = XML_HEADER + "medium_id/val", staticCodeCategory = "MEDIUM")
    private Origin origin;

    @ServiceElement(xpath = XML_HEADER + CONT_TYPE_LIST + " | " + XML_HEADER + SUB_TYPE_LIST, type = TransactionSubTypeImpl.class)
    private List<TransactionSubType> transactionSubTypes;

    // ==================== Fields set in TransactionHistoryConverter
    // ====================

    private Asset asset;
    private Asset contAsset;
    private Asset refAsset;
    private boolean cleared;
    private boolean systemTransaction;
    private String thirdPartySystem;

    // ====================

    @Override
    public Integer getEvtId() {
        return evtId;
    }

    public void setEvtId(Integer evtId) {
        this.evtId = evtId;
    }

    @Override
    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    @Override
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Override
    public String getMetaType() {
        return metaType;
    }

    public void setMetaType(String metaType) {
        this.metaType = metaType;
    }

    @Override
    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    @Override
    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    @Override
    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public BigDecimal getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(BigDecimal closingBalance) {
        this.closingBalance = closingBalance;
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public DateTime getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(DateTime effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @Override
    public DateTime getValDate() {
        return valDate;
    }

    public void setValDate(DateTime valDate) {
        this.valDate = valDate;
    }

    @Override
    public DateTime getClearDate() {
        return clearDate;
    }

    public void setClearDate(DateTime clearDate) {
        this.clearDate = clearDate;
    }

    @Override
    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    @Override
    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    @Override
    public String getPayerBsb() {
        return payerBsb;
    }

    public void setPayerBsb(String payerBsb) {
        this.payerBsb = payerBsb;
    }

    @Override
    public String getPayeeBsb() {
        return payeeBsb;
    }

    public void setPayeeBsb(String payeeBsb) {
        this.payeeBsb = payeeBsb;
    }

    @Override
    public String getPayerAccount() {
        return payerAccount;
    }

    public void setPayerAccount(String payerAccount) {
        this.payerAccount = payerAccount;
    }

    @Override
    public String getPayeeAccount() {
        return payeeAccount;
    }

    public void setPayeeAccount(String payeeAccount) {
        this.payeeAccount = payeeAccount;
    }

    @Override
    public String getPayeeBillerCode() {
        return payeeBillerCode;
    }

    public void setPayeeBillerCode(String payeeBillerCode) {
        this.payeeBillerCode = payeeBillerCode;
    }

    @Override
    public String getPayeeCustrRef() {
        return payeeCustrRef;
    }

    public void setPayeeCustrRef(String payeeCustrRef) {
        this.payeeCustrRef = payeeCustrRef;
    }

    @Override
    public String getBookingText() {
        return bookingText;
    }

    public void setBookingText(String bookingText) {
        this.bookingText = bookingText;
    }

    @Override
    public String getTransactionDescription() {
        return transactionDescription;
    }

    public void setTransactionDescription(String transactionDescription) {
        this.transactionDescription = transactionDescription;
    }

    @Override
    public String getDocDescription() {
        return docDescription;
    }

    public void setDocDescription(String docDescription) {
        this.docDescription = docDescription;
    }

    @Override
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public ContainerType getContType() {
        return contType;
    }

    public void setContType(ContainerType contType) {
        this.contType = contType;
    }

    @Override
    public String getContId() {
        return contId;
    }

    public void setContId(String contId) {
        this.contId = contId;
    }

    @Override
    public String getPosId() {
        return posId;
    }

    public void setPosId(String posId) {
        this.posId = posId;
    }

    @Override
    public String getPosName() {
        return posName;
    }

    public void setPosName(String posName) {
        this.posName = posName;
    }

    @Override
    public String getContAssetId() {
        return contAssetId;
    }

    public void setContAssetId(String contAssetId) {
        this.contAssetId = contAssetId;
    }

    @Override
    public String getPosAssetId() {
        return posAssetId;
    }

    public void setPosAssetId(String posAssetId) {
        this.posAssetId = posAssetId;
    }

    @Override
    public Asset getContAsset() {
        return contAsset;
    }

    public void setContAsset(Asset contAsset) {
        this.contAsset = contAsset;
    }

    @Override
    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    @Override
    public String getRefAssetId() {
        return refAssetId;
    }

    public void setRefAssetId(String refAssetId) {
        this.refAssetId = refAssetId;
    }

    @Override
    public Asset getRefAsset() {
        return refAsset;
    }

    @Override
    public void setRefAsset(Asset refAsset) {
        this.refAsset = refAsset;
    }

    @Override
    public boolean isCleared() {
        return cleared;
    }

    public void setCleared(boolean isCleared) {
        this.cleared = isCleared;
    }

    @Override
    public boolean isSystemTransaction() {
        return systemTransaction;
    }

    public void setSystemTransaction(boolean isSystemTransaction) {
        this.systemTransaction = isSystemTransaction;
    }

    @Override
    public CashCategorisationType getCashCategorisationType() {
        return cashCategorisationType;
    }

    public void setCashCategorisationType(CashCategorisationType cashCatType) {
        this.cashCategorisationType = cashCatType;
    }

    @Override
    public BTOrderType getBTOrderType() {
        return btOrderType;
    }

    public void setBTOrderType(BTOrderType btOrderType) {
        this.btOrderType = btOrderType;
    }

    @Override
    public BigDecimal getCoprSeqNr() {
        return coprSeqNr;
    }

    public void setCoprSeqNr(BigDecimal coprSeqNr) {
        this.coprSeqNr = coprSeqNr;
    }

    @Override
    public Origin getOrigin() {
        return origin;
    }

    public void setOrigin(Origin origin) {
        this.origin = origin;
    }

    @Override
    public List<TransactionSubType> getTransactionSubTypes() {
        return transactionSubTypes;
    }

    public void setTransactionSubTypes(List<TransactionSubType> transactionSubTypes) {
        this.transactionSubTypes = transactionSubTypes;
    }

    @Override
    public BigDecimal getNetAmount() {
        if (netAmount == null) {
            return amount;
        }
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    @Override
    public boolean isReversal() {
        return status != null && status.trim().startsWith("-");
    }

    public String getThirdPartySystem() {
        return thirdPartySystem;
    }

    public void setThirdPartySystem(String thirdPartySystem) {
        this.thirdPartySystem = thirdPartySystem;
    }
}