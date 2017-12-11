package com.bt.nextgen.service.avaloq.transfer;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Suppress warning added as the fields are not supposed to be serialized and the valid parameters list provide information about
 * the parameters that can be supplied to a particular template.
 */
@SuppressWarnings({ "squid:S1171", "squid:S1948" })
public enum TransferTemplate implements AvaloqTemplate {

    INSPECIE_TRANSFER("BTFG$UI_DOC_LIST.BP#XFER_STATUS", new ArrayList<AvaloqParameter>() {
        {
            add(TransferParams.PARAM_ACCOUNT_ID);
        }
    });

    private List<AvaloqParameter> validParams;
    private String templateName;

    TransferTemplate(String templateName, List<AvaloqParameter> validParams) {
        this.templateName = templateName;
        this.validParams = validParams;
    }

    @Override
    public String getTemplateName() {
        return this.templateName;
    }

    @Override
    public List<AvaloqParameter> getValidParamters() {
        return this.validParams;
    }
}
