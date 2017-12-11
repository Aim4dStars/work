package com.bt.nextgen.service.avaloq.ips;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementInterface;
import com.bt.nextgen.service.integration.ips.IpsFee;
import com.bt.nextgen.service.integration.ips.IpsKey;

import javax.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

@ServiceBean(xpath = "ips")
public class InvestmentPolicyStatementImpl implements InvestmentPolicyStatementInterface {

    private static final String IPS_HEAD = "ips_head_list/ips_head/";
    @NotNull
    @ServiceElement(xpath = IPS_HEAD + "ips_id/val", converter = IpsKeyConverter.class)
    private IpsKey ipsKey;

    @NotNull
    @ServiceElement(xpath = IPS_HEAD + "ips_name/val")
    private String investmentName;

    @NotNull
    @ServiceElement(xpath = IPS_HEAD + "ips_sym/val")
    private String code;

    @ServiceElement(xpath = IPS_HEAD + "ips_apir/val")
    private String apirCode;

    @ServiceElement(xpath = IPS_HEAD + "ips_invst_style_id/val")
    private String investmentStyleId;

    @ServiceElement(xpath = IPS_HEAD + "ips_asset_class_id/val")
    private String assetClassId;

    @ServiceElement(xpath = IPS_HEAD + "min_init_invst/val")
    private BigDecimal minInitInvstAmt;

    @ServiceElement(xpath = IPS_HEAD + "invst_mgr_id/val")
    private String investmentManagerPersonId;

    @ServiceElement(xpath = IPS_HEAD + "im_fee_factor/val")
    private BigDecimal percentage;

    @ServiceElement(xpath = IPS_HEAD + "is_wtax_form_relv/val")
    private Boolean taxAssetDomicile;

    @ServiceElement(xpath = IPS_HEAD + "fee_list/fee", type = IpsFeeImpl.class)
    private List<IpsFee> feeList;
    
    /**
     * The weighted income cost ratio of constituent funds.
     */
    @ServiceElement(xpath = IPS_HEAD + "wgt_icr/val")
    private BigDecimal weightedIcr;

    @Override
    public IpsKey getIpsKey() {
        return ipsKey;
    }

    @Override
    public void setIpsKey(IpsKey ipsKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getInvestmentName() {
        return investmentName;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getApirCode() {
        return apirCode;
    }

    @Override
    public String getInvestmentStyleId() {
        return investmentStyleId;
    }

    @Override
    public String getAssetClassId() {
        return assetClassId;
    }

    @Override
    public BigDecimal getMinInitInvstAmt() {
        return minInitInvstAmt;
    }

    @Override
    public String getInvestmentManagerPersonId() {
        return investmentManagerPersonId;
    }

    @Override
    public Boolean getTaxAssetDomicile() {
        return taxAssetDomicile;
    }

    @Override
    public List<IpsFee> getFeeList() {
        return feeList;
    }

    public void setInvestmentName(String investmentName) {
        this.investmentName = investmentName;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setApirCode(String apirCode) {
        this.apirCode = apirCode;
    }

    public void setInvestmentStyleId(String investmentStyleId) {
        this.investmentStyleId = investmentStyleId;
    }

    public void setAssetClassId(String assetClassId) {
        this.assetClassId = assetClassId;
    }

    public void setMinInitInvstAmt(BigDecimal minInitInvstAmt) {
        this.minInitInvstAmt = minInitInvstAmt;
    }

    public void setInvestmentManagerPersonId(String investmentManagerPersonId) {
        this.investmentManagerPersonId = investmentManagerPersonId;
    }

    public void setTaxAssetDomicile(Boolean taxAssetDomicile) {
        this.taxAssetDomicile = taxAssetDomicile;
    }

    public void setFeeList(List<IpsFee> feeList) {
        this.feeList = feeList;
    }

    @Override
    public BigDecimal getPercentage() {
        return percentage;
    }

    @Override
    public BigDecimal getWeightedIcr() {
        return weightedIcr;
    }

}
