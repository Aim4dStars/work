package com.bt.nextgen.service.avaloq.transaction;

import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.bt.nextgen.service.integration.transaction.ParList;
import com.bt.nextgen.service.integration.transaction.TransactionResponse;
import com.bt.nextgen.service.integration.transaction.TransactionValidation;
import com.btfin.abs.trxservice.base.v1_0.Ovr;
import com.btfin.abs.trxservice.base.v1_0.OvrList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TransactionValidationConverter {
    private static final Pattern ERR_PATTERN = Pattern.compile("\\[([^]]+)\\]");

    @Autowired
    protected CmsService cmsService;

    /**
     * Convert the specified collection of transactionValidation to ValidationError items. The ValidationError is typically the
     * class used by front-end for Avaloq exception handling e.g. in the case of vetting.
     * 
     * @param response
     * @param validationList
     * @return
     */
    public List<ValidationError> toValidationError(TransactionResponse response, List<TransactionValidation> validationList) {

        List<ValidationError> validations = new ArrayList<>();
        if (validationList != null && !validationList.isEmpty()) {
            // process error
            for (TransactionValidation err : validationList) {
                String errorMsg = getErrorMessage(err);
                String errorId = err.getExternalKey() == null ? err.getErrorId() : err.getExternalKey();

                // Get detail level errors if any (e.g. asset level)
                List<String> assetIds = getAssetList(response, err);

                // Create validation error
                if (assetIds.isEmpty()) {
                    validations.add(new ValidationError(errorId, null, errorMsg == null ? err.getErrorMessage() : errorMsg, err
                            .getType()));
                } else {
                    for (String assetId : assetIds) {
                        validations.add(new ValidationError(errorId, assetId,
                                errorMsg == null ? err.getErrorMessage() : errorMsg, err.getType()));
                    }
                }
            }
        }

        return validations;
    }

    public OvrList toWarningList(TransactionResponse response) {
        List<ValidationError> validationList = response.getValidationErrors();
        if (validationList != null && !validationList.isEmpty()) {
            OvrList ovrList = AvaloqObjectFactory.getTransactionBaseObjectFactory().createOvrList();
            for (ValidationError validation : validationList) {
                Ovr ovr = AvaloqObjectFactory.getTransactionBaseObjectFactory().createOvr();
                ovr.setOvrId(AvaloqGatewayUtil.createExtlIdVal(validation.getErrorId()));
                BigInteger warningLoc = response.getLocItemIndex(validation.getField());
                if (warningLoc != null) {
                    ovr.setLoc(AvaloqGatewayUtil.createNumberVal(warningLoc));
                }
                ovrList.getOvr().add(ovr);
            }

            return ovrList;
        }
        return null;
    }

    private List<String> getAssetList(TransactionResponse response, TransactionValidation err) {
        List<String> assetIds = new ArrayList<>();
        if (response == null || err.getLocList() != null) {
            for (String loc : err.getLocList()) {
                Matcher matcher = ERR_PATTERN.matcher(loc);
                if (matcher.find()) {
                    Integer assetIndex = Integer.parseInt(matcher.group(1));
                    if (assetIndex.intValue() > 0)
                        assetIds.add(response.getLocListItem(assetIndex - 1));
                }
            }
        }
        return assetIds;
    }

    protected String getErrorMessage(TransactionValidation err) {
        String errorMsg = Properties.get("errorcode." + err.getExternalKey());
        if (errorMsg == null) {
            errorMsg = Properties.get("errorcode." + err.getErrorId());
        }

        if (errorMsg == null) {
            return err.getErrorMessage();
        }

        String[] params = null;
        List<ParList> paramList = err.getParamList();
        if (paramList != null && !paramList.isEmpty()) {
            params = new String[paramList.size()];
            int i = 0;
            for (ParList par : paramList) {
                params[i] = par.getParam();
                ++i;
            }

        }
        return cmsService.getDynamicContent(errorMsg, params);
    }
}
