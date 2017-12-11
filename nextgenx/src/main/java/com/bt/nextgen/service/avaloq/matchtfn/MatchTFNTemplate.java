package com.bt.nextgen.service.avaloq.matchtfn;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqTemplate;

import java.util.List;

/**
 * Created by L070589 on 12/06/2015.
 */
@SuppressWarnings("squid:S1948")
public enum MatchTFNTemplate implements AvaloqTemplate {

    MATCH_TFN("BTFG$COM.BTFIN.TRXSVC_DATA_VALID_V1");


    private List<AvaloqParameter> validParams;
    private String templateName;

    MatchTFNTemplate(String templateName) {

        this.templateName = templateName;
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
