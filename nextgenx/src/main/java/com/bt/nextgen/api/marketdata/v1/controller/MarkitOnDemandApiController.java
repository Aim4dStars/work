package com.bt.nextgen.api.marketdata.v1.controller;


import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.marketdata.v1.model.EncryptionFailedException;
import com.bt.nextgen.api.marketdata.v1.model.ServerUrlDto;
import com.bt.nextgen.api.marketdata.v1.model.ShareNotificationsDto;
import com.bt.nextgen.api.marketdata.v1.service.MarkitOnDemandDtoServerService;
import com.bt.nextgen.api.marketdata.v1.service.MarkitOnDemandKeyService;
import com.bt.nextgen.api.marketdata.v1.service.MarkitOnDemandPodCastDtoService;
import com.bt.nextgen.api.marketdata.v1.service.MarkitOnDemandShareDtoService;
import com.bt.nextgen.api.marketdata.v1.service.SsoKeyService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.Create;
import com.bt.nextgen.core.api.operation.FindOne;
import com.bt.nextgen.core.web.model.AjaxResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@SuppressWarnings("checkstyle:com.puppycrawl.tools.checkstyle.checks.annotation.AnnotationLocationCheck")
public class MarkitOnDemandApiController
{

	private static final Logger logger  = LoggerFactory.getLogger(MarkitOnDemandApiController.class);

	@Autowired
	private SsoKeyService paramEncryptionService;

    @Autowired
    private MarkitOnDemandDtoServerService markitOnDemandDtoServerService;

    @Autowired
    private MarkitOnDemandPodCastDtoService markitOnDemandPodCastDtoService;

    @Autowired
    private MarkitOnDemandShareDtoService markitOnDemandShareDtoService;

	@RequestMapping(value = { "/secure/api/marketdata/v1/mod/communicationkey", "/onboard/api/marketdata/v1/mod/communicationkey"})
    @PreAuthorize("isAuthenticated() and @permissionBaseService.hasProductPermission('marketinformation.view')")
	public @ResponseBody AjaxResponse getSsoKey() throws EncryptionFailedException {
		String encryptedResult = paramEncryptionService.getEncryptedKey();
		return new AjaxResponse(encryptedResult);
	}

	@RequestMapping(value = { "/secure/api/marketdata/v2/mod/communicationkey", "/onboard/api/marketdata/v2/mod/communicationkey"})
	@PreAuthorize("isAuthenticated() and @permissionBaseService.hasProductPermission('marketinformation.view')")
    public @ResponseBody AjaxResponse get256bitSsoKey(@RequestParam(value = "accountId", required = false) String accountId)
            throws EncryptionFailedException {
        AccountKey accountKey = null;
        if (StringUtils.isNotEmpty(accountId)) {
            accountKey = new AccountKey(accountId);
        }
        String encryptedResult = paramEncryptionService.getEncryptedKey(MarkitOnDemandKeyService.EncryptionStrength.AES_256_BIT,
                accountKey);
		return new AjaxResponse(encryptedResult);
	}

	@RequestMapping(method = RequestMethod.GET, value = { "/secure/api/marketdata/v1/mod/podCasts", "/onboard/api/marketdata/v1/mod/podCasts"})
    @PreAuthorize("isAuthenticated() and @permissionBaseService.hasProductPermission('marketinformation.view')")
	public @ResponseBody ApiResponse getPodCasts() {
        return new FindOne<>(ApiVersion.CURRENT_VERSION, markitOnDemandPodCastDtoService).performOperation();
	}

    @RequestMapping(method = RequestMethod.GET, value = { "/secure/api/marketdata/v1/mod/server", "/onboard/api/marketdata/v1/mod/server"})
    @PreAuthorize("isAuthenticated() and @permissionBaseService.hasProductPermission('marketinformation.view')")
    public @ResponseBody ApiResponse getServerUrl() {
        return new FindOne<ServerUrlDto>(ApiVersion.CURRENT_VERSION, markitOnDemandDtoServerService).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = { "/secure/api/marketdata/v1/share", "/onboard/api/marketdata/v1/share"})
    @PreAuthorize("isAuthenticated() and @permissionBaseService.hasProductPermission('marketinformation.view')")
    public @ResponseBody ApiResponse shareStory(@RequestBody ShareNotificationsDto directNotificationDto) {
        return new Create<>(ApiVersion.CURRENT_VERSION, markitOnDemandShareDtoService,directNotificationDto).performOperation();
    }

	@ExceptionHandler({
		MethodArgumentNotValidException.class,
		HttpMessageNotReadableException.class

	})
	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public AjaxResponse handleException(Exception exception)
	{
		logger.error("Unable to parse request. Error: {}", exception.getMessage());
		return new AjaxResponse(false, "Failed");
	}

	@ExceptionHandler({
		EncryptionFailedException.class

	})
	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public AjaxResponse handleEncryptionException(Exception exception)
	{
		logger.error("Unable to encrypt result Error: {}", exception.getMessage(),exception);
		return new AjaxResponse(false, "Failed");
	}

}
