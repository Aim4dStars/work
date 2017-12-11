package com.bt.nextgen.service.avaloq.rollover;

import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.client.AccountKeyConverter;
import com.bt.nextgen.service.avaloq.transaction.TransactionErrorDetailsImpl;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.rollover.RolloverDetails;
import com.bt.nextgen.service.integration.rollover.RolloverOption;
import com.bt.nextgen.service.integration.rollover.RolloverType;
import com.bt.nextgen.service.integration.transaction.TransactionResponse;
import com.bt.nextgen.service.integration.transaction.TransactionValidation;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@ServiceBean(xpath = "/", type = ServiceBeanType.CONCRETE)
public class RolloverDetailsImpl extends TransactionErrorDetailsImpl implements RolloverDetails, TransactionResponse {
    public static final String XML_HEADER = "//data/";

    @ServiceElement(xpath = XML_HEADER + "bp_id/val", converter = AccountKeyConverter.class)
    private AccountKey accountKey;

    @ServiceElement(xpath = "//data/doc/val")
    private String rolloverId;

    @ServiceElement(xpath = XML_HEADER + "fund_id/val")
    private String fundId;

    @ServiceElement(xpath = XML_HEADER + "fund_name/val")
    private String fundName;

    @ServiceElement(xpath = XML_HEADER + "fund_abn/val")
    private String fundAbn;

    @ServiceElement(xpath = XML_HEADER + "fund_usi/val")
    private String usi;

    @ServiceElement(xpath = XML_HEADER + "fund_estim_amt/val")
    private BigDecimal amount;

    @ServiceElement(xpath = XML_HEADER + "ss_rlov_in/val")
    private Boolean panInitiated;

    @ServiceElement(xpath = XML_HEADER + "ss_req_dt/val", converter = DateTimeTypeConverter.class)
    private DateTime requestDate;

    @ServiceElement(xpath = XML_HEADER + "mbr_acc_nr/val")
    private String accountNumber;

    @ServiceElement(xpath = XML_HEADER + "rlov_opt_id/val", staticCodeCategory = "ROLLOVER_OPTION")
    private RolloverOption rolloverOption;

    @ServiceElement(xpath = XML_HEADER + "rlov_type_id/val", staticCodeCategory = "ROLLOVER_TYPE")
    private RolloverType rolloverType;

    @ServiceElement(xpath = XML_HEADER + "incl_insur/val")
    private Boolean includeInsurance;

    @ServiceElement(xpath = "//rsp/get/last_trans_seq_nr/val")
    private String lastTransSeqId;

    @ServiceElement(xpath = "//rsp/exec/err_list/err", type = TransactionValidationImpl.class)
    private List<TransactionValidation> warnings;
    private List<ValidationError> validationErrors;

    public RolloverDetailsImpl() {
        super();
    }

    public static String getXmlHeader() {
        return XML_HEADER;
    }

    public AccountKey getAccountKey() {
        return accountKey;
    }

    public String getRolloverId() {
        return rolloverId;
    }

    public String getFundId() {
        return fundId;
    }

    public String getFundName() {
        return fundName;
    }

    public String getFundAbn() {
        return fundAbn;
    }

    public String getFundUsi() {
        return usi;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Boolean getPanInitiated() {
        return panInitiated;
    }

    public DateTime getRequestDate() {
        return requestDate;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public RolloverOption getRolloverOption() {
        return rolloverOption;
    }

    public void setAccountKey(AccountKey accountKey) {
        this.accountKey = accountKey;
    }

    public void setRolloverId(String rolloverId) {
        this.rolloverId = rolloverId;
    }

    public void setFundId(String fundId) {
        this.fundId = fundId;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public void setFundAbn(String fundAbn) {
        this.fundAbn = fundAbn;
    }

    public void setFundUsi(String usi) {
        this.usi = usi;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setPanInitiated(Boolean panInitiated) {
        this.panInitiated = panInitiated;
    }

    public void setRequestDate(DateTime requestDate) {
        this.requestDate = requestDate;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setRolloverOption(RolloverOption rolloverOption) {
        this.rolloverOption = rolloverOption;
    }

    public RolloverType getRolloverType() {
        return rolloverType;
    }

    public void setRolloverType(RolloverType rolloverType) {
        this.rolloverType = rolloverType;
    }

    public Boolean getIncludeInsurance() {
        return includeInsurance;
    }

    public void setIncludeInsurance(Boolean includeInsurance) {
        this.includeInsurance = includeInsurance;
    }

    public String getLastTransSeqId() {
        return lastTransSeqId;
    }

    public void setLastTransSeqId(String lastTransSeqId) {
        this.lastTransSeqId = lastTransSeqId;
    }

    public List<TransactionValidation> getWarnings() {
        return warnings;
    }

    @Override
    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }

    @Override
    public String getLocListItem(Integer index) {
        return null;
    }

    @Override
    public BigInteger getLocItemIndex(String itemId) {
        return null;
    }

}
