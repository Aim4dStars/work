package com.bt.nextgen.service.avaloq.modelportfolio.rebalance;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqTemplate;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({ "squid:S1171", "squid:S1948", "serial"})
public enum ModelPortfolioRebalanceTemplate implements AvaloqTemplate {

    REBALANCE_SUMMARY("btfg$ui_doc_rebal_trig.im#doc", new ArrayList<AvaloqParameter>() {
        {
            add(ModelPortfolioRebalanceParams.PARAM_INVESTMENT_MANAGER_ID);
        }
    }),
    REBALANCE_ACCOUNTS("btfg$ui_doc_rebal_smry.doc#doc_det", new ArrayList<AvaloqParameter>() {
        {
            add(ModelPortfolioRebalanceParams.PARAM_IPS_ID);
        }
    }),
    REBALANCE_ORDERS("btfg$ui_doc_rebal_trig.doc#doc_det", new ArrayList<AvaloqParameter>() {
        {
            add(ModelPortfolioRebalanceParams.PARAM_IPS_ID);
            add(ModelPortfolioRebalanceParams.PARAM_DOC_DET_LIST);
        }
    });

    private List<AvaloqParameter> validParams;
    private String templateName;

    ModelPortfolioRebalanceTemplate(String templateName, List<AvaloqParameter> validParams) {
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