package com.bt.nextgen.web.controller;

import ch.qos.logback.classic.Level;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.core.session.SessionUtils;
import com.bt.nextgen.core.web.model.AjaxResponse;
import com.bt.nextgen.util.Environment;
import com.bt.nextgen.web.model.ClientLogInformation;
import com.bt.nextgen.web.model.ClientLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.validation.Valid;

@Controller
public class LoggingController {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final String DEFAULT_ORIGIN = "Panorama";

    @Autowired
    private ClientLogger clientLogger;

    /**
     * This API logs the client-side event information to server.<br/>
     * Format for Request:<br/>
     * {"clientType":"WEB", "clientVersion":"web-client-1.0-b1102",
     * "performanceLogs":[{"type":"NAV","location":"#ng/clientlist","intiatorType":"bootstrap","duration":"100"}],
     * "errorLogs":[{"type":"AJAX","location":"#ng/clientlist","name":"some url","statusCode":"200"}]}
     *
     * @param clientLogInformation - For details. See <a href="http://dwgps0026/twiki/bin/view/NextGen/CoreSinglePageAppLogging">CoreSinglePageAppLogging</a>
     * @return AjaxResponse<br/> On success : {"success":true,"data":"Success"}<br/> On Failure:
     * {"success":false,"data":"Failed"}
     */
    @RequestMapping(value = {"secure/api/log/client-info", "onboard/api/log/client-info", "secure/api/log/v1_0/client-info",
            "onboard/api/log/v1_0/client-info" }, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AjaxResponse logMessage(@Valid @RequestBody final ClientLogInformation clientLogInformation) throws JsonProcessingException {

        long timeStart = System.currentTimeMillis();
        logger.info("logClientInfo called at {}", timeStart);

        clientLogInformation.setOriginatingSystem(getOriginatingSystem());

        //Making Client Logging Asynchronous.
        Thread clientLoggerThread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                try {
                    clientLogger.logClientInfo(clientLogInformation);
                }
                catch(JsonProcessingException e)
                {
                    logger.error("Error while loggin {}",e);
                }

            }
        });
        clientLoggerThread.setName("CLIENT_LOGGER");
        clientLoggerThread.start();
        logger.info("Thread created with Id {} " , clientLoggerThread.getId());

        long timeEnd = System.currentTimeMillis();
        logger.info("logClientInfo called completed {}s", (timeEnd-timeStart)/1000);


        return new AjaxResponse("Success");
    }

    @ExceptionHandler({MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            JsonProcessingException.class
    })
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AjaxResponse handleException(Exception exception) {
        logger.error("Unable to parse request. Error: {}", exception.getMessage());
        return new AjaxResponse(false, "Failed");
    }

    @RequestMapping(value = "/public/api/log/v1_0/logger/name/{name}/level/{level}", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResponse updateSettings(@PathVariable String name, @PathVariable String level) {
        if (Environment.isProduction()) {
            throw new AccessDeniedException("You are not authorised");
        }

        logger.info("Updating logging settings : {} to {}", name, level);

        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(name);
        logger.setLevel(Level.toLevel(level));

        logger.info("Logging settings updated to : {} to {}", name, logger.getLevel().toString());
        // if something bogus gets set, this will have a default, play back what was actually set no asked for
        return new AjaxResponse(new SettingsResponse(name, logger.getLevel().toString()));
    }

    @RequestMapping(value = "/public/api/log/v1_0/logger/name/{name}", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResponse querySettings(@PathVariable String name) {
        if (Environment.isProduction()) {
            throw new AccessDeniedException("You are not authorised");
        }

        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(name);
        return new AjaxResponse(new SettingsResponse(name, logger.getLevel().toString()));
    }

    /**
     * @return Originating system for the user (WPL/Panorama)
     */
    private String getOriginatingSystem() {
        final ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final String originatingSystem = (String) attr.getRequest().getSession().getAttribute(SessionUtils.ORIGINATING_SYSTEM);
        return originatingSystem != null ? originatingSystem : DEFAULT_ORIGIN;
    }

    /**
     * Render stuff to ajax easier
     */
    public static class SettingsResponse {
        public SettingsResponse(String name, String level) {
            this.level = level;
            this.name = name;
        }

        private final String name;
        private final String level;

        public String getName() {
            return name;
        }

        public String getLevel() {
            return level;
        }
    }
}
