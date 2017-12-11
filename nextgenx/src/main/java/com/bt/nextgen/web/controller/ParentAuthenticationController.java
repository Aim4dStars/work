package com.bt.nextgen.web.controller;

import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.core.web.RequestQuery;
import com.bt.nextgen.login.web.model.PasswordResetModel;
import com.bt.nextgen.login.web.model.RegistrationModel;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.bt.nextgen.core.util.SETTINGS.SECURITY_BRAND_PARAM;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_DEFAULT_BRAND;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_HALGM_PARAM;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_JS_OBFUSCATION_URL_DEV;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_JS_OBFUSCATION_URL_PRD;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_PASSWORD_PARAM;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_USERNAME_PARAM;

public class ParentAuthenticationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParentAuthenticationController.class);

    @Autowired
    protected RequestQuery requestQuery;

    @Autowired
    protected CmsService cmsService;

    protected static final String MSG_KEY = "message";
    protected static final String MSG_TYPE = "messageType";

    /**
     * These attributes are core to the login theme pages
     *
     * @param model to contain the attributes
     */
    protected void prepareModel(ModelMap model) {
        model.addAttribute(Attribute.PASSWORD_RESET_MODEL, new PasswordResetModel());
        model.addAttribute(Attribute.REGISTRATION_MODEL, new RegistrationModel());
        model.addAttribute(Attribute.LOGON_BRAND, SECURITY_DEFAULT_BRAND.value());
        boolean isWebSealRequest = requestQuery.isWebSealRequest();
        model.addAttribute(Attribute.OBFUSCATION_URL, isWebSealRequest
                ? SECURITY_JS_OBFUSCATION_URL_PRD.value()
                : SECURITY_JS_OBFUSCATION_URL_DEV.value());
        model.addAttribute(Attribute.PASSWORD_FIELD_NAME, SECURITY_PASSWORD_PARAM.value());
        model.addAttribute(Attribute.USERNAME_FIELD_NAME, SECURITY_USERNAME_PARAM.value());
        model.addAttribute(Attribute.BRAND_FIELD_NAME, SECURITY_BRAND_PARAM.value());
        model.addAttribute(Attribute.HALGM_FIELD_NAME, SECURITY_HALGM_PARAM.value());
    }

    /**
     * This will fill in the details for the logon page with the passed string displayed as an error
     *
     * @param model         to be populated with common data
     * @param logoutMessage the message to show, if it is null, no message information will be included.
     */
    protected void prepareModelWithErrorMessage(HttpServletRequest request, HttpServletResponse response, ModelMap model, String logoutMessage) {
        prepareModel(model);
        addErrorMessage(request, response, model, logoutMessage);
    }

    protected void addErrorMessage(HttpServletRequest request, HttpServletResponse response, ModelMap model, String errorMessage) {
        if (errorMessage != null) {
            model.addAttribute(MSG_KEY, errorMessage);
            model.addAttribute(MSG_TYPE, "ERROR");
        }
    }

    public List<Object> checkForBindingErrorOnField(BindingResult bindingResult, String errorType) {
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            if (errorType.equalsIgnoreCase(fieldError.getField())) {
                LOGGER.error("validation errors : " + fieldError.getObjectName());
                List<Object> formattedResults = new ArrayList<>();
                formattedResults.add(new ValidationError(fieldError.getField(),
                        cmsService.getContent(fieldError.getDefaultMessage())));
                return formattedResults;
            }
        }
        return Collections.emptyList();
    }

    public List<Object> checkForBindingErrorExcludingField(BindingResult bindingResult, String excludedErrorType) {
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            if (!excludedErrorType.equalsIgnoreCase(fieldError.getField())) {
                LOGGER.error("validation errors : " + fieldError.getObjectName());
                List<Object> formattedResults = new ArrayList<>();
                formattedResults.add(new ValidationError(fieldError.getField(),
                        cmsService.getContent(fieldError.getDefaultMessage())));
                return formattedResults;
            }
        }
        return Collections.emptyList();
    }
}
