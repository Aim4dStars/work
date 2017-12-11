package com.bt.nextgen.service.avaloq.collection;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Suppress warning added as the fields are not supposed to be serialized and
 * the valid parameters list provide information about the parameters that can be supplied to a particular template.
 */
@SuppressWarnings({"squid:S1171", "squid:S1948"})
public enum CollectionTemplate implements AvaloqTemplate {

    COLLECTION_ASSETS("BTFG$UI_COLLECT_LIST.COLLECT#ASSET", new ArrayList<AvaloqParameter>() {{
        add(CollectionParams.COLLECTION_LIST_ID);
    }});

    private List<AvaloqParameter> validParameters;
    private String templateName;

    CollectionTemplate(String templateName, ArrayList<AvaloqParameter> validParameters) {
        this.templateName = templateName;
        this.validParameters = validParameters;
    }

    @Override
    public String getTemplateName() {
        return templateName;
    }

    @Override
    public List<AvaloqParameter> getValidParamters() {
        return validParameters;
    }
}
