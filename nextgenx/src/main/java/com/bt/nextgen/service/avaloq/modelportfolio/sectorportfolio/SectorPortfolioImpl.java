package com.bt.nextgen.service.avaloq.modelportfolio.sectorportfolio;

import com.bt.nextgen.core.conversion.BigIntegerConverter;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

import java.math.BigInteger;

@ServiceBean(xpath = "/mpf")
public class SectorPortfolioImpl implements SectorPortfolio {

    public static final String MPF_HEAD_PATH = "mpf_head_list/mpf_head/";

    private static final String CODE_CATEGORY_ASSET_CLASS = "MPF_ASSET_CLASS";
    private static final String CODE_CATEGORY_PRODUCT_TYPE = "MPF_PROD_TYPE";
    private static final String CODE_CATEGORY_STATUS = "MPF_STATUS";

    @NotNull
    @ServiceElement(xpath = MPF_HEAD_PATH + "mpf_id/val")
    private String id;

    @NotNull
    @ServiceElement(xpath = MPF_HEAD_PATH + "mpf_name/val")
    private String name;

    @ServiceElement(xpath = MPF_HEAD_PATH + "mpf_sym/val")
    private String code;

    @NotNull
    @ServiceElement(xpath = MPF_HEAD_PATH + "invst_mgr_id/val")
    private String investmentManagerId;

    @NotNull
    @ServiceElement(xpath = MPF_HEAD_PATH + "mpf_asset_class_id/val", staticCodeCategory = CODE_CATEGORY_ASSET_CLASS)
    private String assetClass;

    @ServiceElement(xpath = MPF_HEAD_PATH + "mpf_cat/val")
    private String category;

    @NotNull
    @ServiceElement(xpath = MPF_HEAD_PATH + "mpf_prod_type_id/val", staticCodeCategory = CODE_CATEGORY_PRODUCT_TYPE)
    private String productType;

    @NotNull
    @ServiceElement(xpath = MPF_HEAD_PATH + "mpf_status_id/val", staticCodeCategory = CODE_CATEGORY_STATUS)
    private String status;

    @ServiceElement(xpath = MPF_HEAD_PATH + "ips_cnt/val", converter = BigIntegerConverter.class)
    private BigInteger ipsCount;

    @NotNull
    @ServiceElement(xpath = MPF_HEAD_PATH + "last_mdf_timestp/val", converter = DateTimeTypeConverter.class)
    private DateTime lastModifiedDate;

    @NotNull
    @ServiceElement(xpath = MPF_HEAD_PATH + "last_mdf_user/val")
    private String lastModifiedBy;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getInvestmentManagerId() {
        return investmentManagerId;
    }

    public void setInvestmentManagerId(String investmentManagerId) {
        this.investmentManagerId = investmentManagerId;
    }

    @Override
    public String getAssetClass() {
        return assetClass;
    }

    public void setAssetClass(String assetClass) {
        this.assetClass = assetClass;
    }

    @Override
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    @Override
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public BigInteger getIpsCount() {
        return ipsCount;
    }

    public void setIpsCount(BigInteger ipsCount) {
        this.ipsCount = ipsCount;
    }

    @Override
    public DateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(DateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

}
