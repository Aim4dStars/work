package com.bt.nextgen.service.avaloq;

import com.bt.nextgen.service.ServiceError;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import org.slf4j.helpers.MessageFormatter;

/**
 * Created by m035652 on 28/03/14.
 */
public class PortfolioParseError extends ServiceErrorImpl{



    public PortfolioParseError(String reason) {
        super();
        this.reason = reason;
        this.message = reason;
        id="portfolio";
    }

    public PortfolioParseError(String template, Object... substitutions)
    {
        super(template,substitutions);
    }

}
