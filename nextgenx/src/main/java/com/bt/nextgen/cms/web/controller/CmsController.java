package com.bt.nextgen.cms.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This controller responses to TAM redirection when CMS server is unavailable.
 */
@Controller
public class CmsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmsController.class);

    /**
     * Returns Service Unavailable when (CMS Server is down) this service is called.
     *
     * @return {@link HttpStatus#SERVICE_UNAVAILABLE}
     */
    @RequestMapping(value = "/public/api/cms/unavailable", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> cmsServerUnavailable() {
        LOGGER.info("CMS Server is unavailable.");
        return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }
}
