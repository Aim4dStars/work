package com.bt.nextgen.service.avaloq.modelportfolio;

import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.ips.IpsKeyConverter;
import com.bt.nextgen.service.avaloq.transaction.TransactionErrorDetailsImpl;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationImpl;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioAssetAllocation;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioUpload;
import com.bt.nextgen.service.integration.transaction.TransactionResponse;
import com.bt.nextgen.service.integration.transaction.TransactionValidation;

import java.math.BigInteger;
import java.util.List;

@ServiceBean(xpath = "/", type = ServiceBeanType.CONCRETE)
public class ModelPortfolioUploadImpl extends TransactionErrorDetailsImpl implements ModelPortfolioUpload, TransactionResponse {
    public static final String XML_HEADER = "//data/";

    @ServiceElement(xpath = XML_HEADER + "mp_doc/mp_key/val", converter = IpsKeyConverter.class)
    private IpsKey modelKey;

    private String modelCode;

    @ServiceElement(xpath = XML_HEADER + "mp_doc/mp_key/annot/displ_text")
    private String modelName;

    @ServiceElement(xpath = XML_HEADER + "mp_doc/remark/val")
    private String commentary;

    @ServiceElement(xpath = XML_HEADER + "asset_list/asset", type = ModelPortfolioAssetAllocationImpl.class)
    private List<ModelPortfolioAssetAllocation> assetAllocations;

    @ServiceElement(xpath = "//rsp/valid/err_list/err | //rsp/exec/err_list/err", type = TransactionValidationImpl.class)
    private List<TransactionValidation> warnings;

    private List<ValidationError> validationErrors;

    @Override
    public IpsKey getModelKey() {
        return modelKey;
    }

    public void setModelKey(IpsKey modelKey) {
        this.modelKey = modelKey;
    }

    @Override
    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    @Override
    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @Override
    public String getCommentary() {
        return commentary;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    @Override
    public List<ModelPortfolioAssetAllocation> getAssetAllocations() {
        return assetAllocations;
    }

    public void setAssetAllocations(List<ModelPortfolioAssetAllocation> assetAllocations) {
        this.assetAllocations = assetAllocations;
    }

    @Override
    public List<TransactionValidation> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<TransactionValidation> warnings) {
        this.warnings = warnings;
    }

    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public String getLocListItem(Integer index) {
        if (assetAllocations != null) {
            ModelPortfolioAssetAllocation inAsset = assetAllocations.get(index);
            return inAsset.getAssetCode();
        }
        return null;
    }

    @Override
    public BigInteger getLocItemIndex(String itemId) {
        int i = 1;
        if (assetAllocations != null) {
            for (ModelPortfolioAssetAllocation asset : assetAllocations) {
                if (asset.getAssetCode().equals(itemId)) {
                    return BigInteger.valueOf(i);
                }
                i++;
            }
        }
        return null;
    }

}
