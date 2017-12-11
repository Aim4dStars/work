package com.bt.nextgen.serviceops.controller;

import static com.bt.nextgen.api.draftaccount.LoggingConstants.ONBOARDING_SEND;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bt.nextgen.serviceops.model.ProvisionMFARequestData;
import com.bt.nextgen.serviceops.model.ProvisionMFARequestDataBuilder;
import com.bt.nextgen.serviceops.service.ProvisionMFADeviceService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bt.nextgen.api.draftaccount.model.ClientApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.ServiceOpsClientApplicationDto;
import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.api.statements.model.DocumentKey;
import com.bt.nextgen.api.statements.service.DocumentDtoService;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiError;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.Dto;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.bt.nextgen.core.service.CredentialService;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.core.util.LogMarkers;
import com.bt.nextgen.core.web.binding.EnumPropertyEditor;
import com.bt.nextgen.core.web.model.AjaxResponse;
import com.bt.nextgen.core.web.util.View;
import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.domain.PhoneImpl;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.prm.service.PrmService;
import com.bt.nextgen.serviceops.model.DocumentFilterModel;
import com.bt.nextgen.serviceops.model.ServiceOpsModel;
import com.bt.nextgen.serviceops.model.UserAccountStatusModel;
import com.bt.nextgen.serviceops.service.DeviceArrangementService;
import com.bt.nextgen.serviceops.service.ModifyChannelAccessCredentialService;
import com.bt.nextgen.serviceops.service.ResendRegistrationEmailService;
import com.bt.nextgen.serviceops.service.ResetPasswordService;
import com.bt.nextgen.serviceops.service.ServiceOpsService;
import com.bt.nextgen.serviceops.service.UserAccountStatusService;
import com.bt.nextgen.serviceops.util.AustralianMobileNumberFormatter;
import com.bt.nextgen.userauthority.web.Action;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.bt.nextgen.web.validator.ValidationErrorCode;
import com.btfin.panorama.core.security.UserAccountStatus;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.util.StringUtil;
import com.btfin.panorama.service.client.error.ServiceErrorImpl;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Controller
public class ServiceOpsController {
	private static final Logger logger = LoggerFactory.getLogger(ServiceOpsController.class);
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd");
	private static final String BP_NUMBER = "bpNumber";
	private static final String DOCUMENT_MODELS = "documents";
	private static final String DOC_LIB_FILTERS = "filters";
	private static final String ACCOUNT_MODELS = "accounts";
	private static final String DOC_LIB_SEARCH_CRITERIA = "searchCriteria";
	private static final String DOC_LIB_SEARCH_TYPE = "searchType";
	private static final String DOC_LIB_SEARCH_FOR = "searchFor";
	private static final String ACCOUNT_KEY = "accountKey";
	private static final String GCM_ID = "gcmId";
	private static final String SEARCH_PERSON = "Person";
	private static  final String  STRING_STATEMENT_PREF = "1234";

	@Autowired
	private ServiceOpsService serviceOpsService;

	@Autowired
	private UserAccountStatusService userAccountStatusService;

	@Autowired
	private ResetPasswordService resetPasswordService;

	@Autowired
	private ModifyChannelAccessCredentialService blockService;

	@Autowired
	private DeviceArrangementService deviceArrangementService;

	@Autowired
	private CmsService cmsService;

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	private ResendRegistrationEmailService resendRegistrationEmailService;

	@Autowired
	private DocumentDtoService documentDtoService;

	@Autowired
	private PrmService prmService;

	@Autowired
	private FeatureTogglesService featureTogglesService;

    @Autowired
    private ProvisionMFADeviceService provisionMFADeviceService;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		dataBinder.registerCustomEditor(Action.class, new EnumPropertyEditor(Action.class));
	}

	@SuppressFBWarnings(value = "squid:S00112")
	@SuppressWarnings("squid:S00112")
	@RequestMapping(value = "/secure/page/serviceOps/{clientId}/detail", method = RequestMethod.GET)
	public String detail(@PathVariable("clientId") EncodedString clientId, ModelMap map) throws Exception {
		logger.info("Loading  detail of client Id {}", clientId);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		ServiceOpsModel serviceOps = serviceOpsService.getUserDetail(clientId.plainText(), true, serviceErrors);
		map.put(Attribute.SERVICE_OPS_MODEL, serviceOps);
		map.put("isRestricted", serviceOpsService.isServiceOpsRestricted());
		if (serviceOps.getInformationMessage() != null && !serviceOps.getInformationMessage().isEmpty()) {
			map.put(Attribute.MESSAGE, serviceOps.getInformationMessage());
		}
		return View.CLIENT_DETAIL;
	}

	@SuppressFBWarnings(value = "squid:S00112")
	@SuppressWarnings("squid:S00112")
	@RequestMapping(value = "/secure/page/serviceOps/{accountId}/accountDetail", method = RequestMethod.GET)
	public String accountDetail(@PathVariable("accountId") String accountId, ModelMap map) throws Exception {
		logger.info("Loading  detail of account Id {}", accountId);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		ServiceOpsModel serviceOps = serviceOpsService.getAccountDetail(accountId, serviceErrors);
		map.put(Attribute.SERVICE_OPS_MODEL, serviceOps);
		map.put("isRestricted", serviceOpsService.isServiceOpsRestricted());
		if (serviceOps != null && serviceOps.getInformationMessage() != null && !serviceOps.getInformationMessage().isEmpty()) {
			map.put(Attribute.MESSAGE, serviceOps.getInformationMessage());
		}
		return View.ACCOUNT_DETAIL;
	}

	@RequestMapping(value = "/secure/page/serviceOps/applicationDetails/account/{accountNumber}", method = RequestMethod.GET)
	public String getClientApplicationDetailsByAccountNumber(@PathVariable("accountNumber") String accountNumber, ModelMap map,
			@RequestParam(value = "status", required = false) String status) throws Exception {
		logger.info("Loading  detail of account {}", accountNumber);
		ClientApplicationDetailsDto applications = serviceOpsService.getClientApplicationDetailsByAccountNumber(accountNumber);

		if (AccountStatus.ACTIVE.getStatus().equalsIgnoreCase(status) && !isApprovedApplication(applications)) {
			applications = null;
		}

		map.put(Attribute.CLIENTAPPLICATION, applications);
		return View.APPLICATION_DETAIL;
	}

	@RequestMapping(value = "/secure/page/serviceOps/directApplicationDetails/account/{accountNumber}", method = RequestMethod.GET)
	public String getDirectClientApplicationDetailsByAccountNumber(@PathVariable("accountNumber") String accountNumber, ModelMap map) {
		logger.info("Loading  detail of account {}", accountNumber);
		ClientApplicationDetailsDto applications = serviceOpsService.getClientApplicationDetailsByAccountNumber(accountNumber);
		map.put(Attribute.CLIENTAPPLICATION, applications);
		return View.APPLICATION_DETAIL;
	}

	/**
	 * Applications are approved if their status is active or fund establishment
	 * in progress
	 * 
	 * @param application
	 * @return
	 */
	private boolean isApprovedApplication(ClientApplicationDetailsDto application) {
		return application != null
				&& (AccountStatus.ACTIVE.getStatus().equalsIgnoreCase(application.getAccountAvaloqStatus()) || AccountStatus.FUND_ESTABLISHMENT_IN_PROGRESS
						.getStatus().equalsIgnoreCase(application.getAccountAvaloqStatus()));
	}

	@RequestMapping(value = "/secure/page/serviceOps/applicationDetails/id/{clientApplicationId}", method = RequestMethod.GET)
	public String getClientApplicationDetails(@PathVariable("clientApplicationId") String clientApplicationId, ModelMap map) {
		logger.info("Loading  detail of Application Id {}", clientApplicationId);
		ClientApplicationDetailsDto clientApplicationDetailsDto = serviceOpsService.getClientApplicationDetails(clientApplicationId);
		map.put(Attribute.CLIENTAPPLICATION, clientApplicationDetailsDto);
		return View.APPLICATION_DETAIL;
	}

	@RequestMapping(value = "/secure/page/serviceOps/searchApplication", method = RequestMethod.GET)
	public String searchApplication() {
		return View.APPLICATIONSEARCH;
	}

	@RequestMapping(value = "/secure/page/serviceOps/downloadApplication", method = RequestMethod.GET)
	public String downloadApplicationsAsCsv(ModelMap model, @RequestParam(required = false) String fromDate,
			@RequestParam(required = false) String toDate) {
		if (fromDate != null && toDate != null) {
			Date formattedFromDate = dateTimeFormatter.parseDateTime(fromDate).toDate();
			Date formattedToDate = dateTimeFormatter.parseDateTime(toDate).plusDays(1).minusSeconds(1).toDate();
			if (serviceOpsService.countOfApplicationIdsForUnapprovedApplications(formattedFromDate, formattedToDate) > 1000) {
				model.put(Attribute.ERROR_MESSAGE, "There are greater than 1000 unapproved applications.Please reduce your date range and try again.");
			} else {
				return "redirect:/secure/page/serviceOps/downloadApplicationAsCsv?fromDate=" + fromDate + "&toDate=" + toDate;
			}
		}
		return View.DOWNLOAD_CSV;
	}

	@RequestMapping(value = "/secure/api/searchFailedApplication", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	ApiResponse searchFailedApplication(@RequestParam String applicationId) {
		logger.info("Loading  detail of Application reference Id {}", applicationId);
		ServiceOpsClientApplicationDto failedApplicationDetails = serviceOpsService.getFailedApplicationDetails(applicationId);
		if (failedApplicationDetails != null) {
			ResultListDto<ServiceOpsClientApplicationDto> failedApplicationListDto = new ResultListDto<>(Arrays.asList(failedApplicationDetails));
			return new ApiResponse(ApiVersion.CURRENT_VERSION, failedApplicationListDto);
		}
		// TODO should return an empty list, not null
		return new ApiResponse(ApiVersion.CURRENT_VERSION, new ResultListDto<>((ServiceOpsClientApplicationDto) null));
	}

	@RequestMapping(value = "/secure/api/searchFailedDirectApplication", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	ApiResponse searchFailedDirectApplication(@RequestParam String cisKey) {
		logger.info("Loading  detail of Application for cisKey {}", cisKey);
		ResultListDto<ServiceOpsClientApplicationDto> failedApplicationListDto = new ResultListDto<>(
				serviceOpsService.getFailedDirectApplications(cisKey));
		return new ApiResponse(ApiVersion.CURRENT_VERSION, failedApplicationListDto);
	}

	@RequestMapping(value = "/secure/api/directApprovedApplications", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	ApiResponse getClientApplicationsByCISKey(@RequestParam String cisKey) {
		logger.info("Loading  applications for CIS Key {}", cisKey);
		ResultListDto<ServiceOpsClientApplicationDto> resultListDto = new ResultListDto<>(
				serviceOpsService.getApprovedClientApplicationsByCISKey(cisKey));
		return new ApiResponse(ApiVersion.CURRENT_VERSION, resultListDto);
	}

	@RequestMapping(value = "/secure/api/moveFailedApplicationToDraft", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	ApiResponse moveFailedApplicationToDraft(@RequestParam String applicationId) {
		try {
			serviceOpsService.moveFailedApplicationToDraft(applicationId);
			return new ApiResponse(ApiVersion.CURRENT_VERSION, (Dto) null);
		} catch (IllegalStateException e) {
			logger.error("Error during creating a new draft account based on an existing failed one.", e);
			return new ApiResponse(ApiVersion.CURRENT_VERSION, new ApiError(null,
					"Error during creating a new draft account based on an existing failed one"));
		}
	}

	@RequestMapping(value = "/secure/page/serviceOps/downloadApplicationAsCsv", method = RequestMethod.GET)
	public void downloadCsvOfAllUnapprovedApplications(HttpServletResponse response, @RequestParam String fromDate, @RequestParam String toDate) {
		try {
			final String filename = "unapproved_applications_report.csv";
			String csvContent = serviceOpsService.downloadCsvOfAllUnapprovedApplications(dateTimeFormatter.parseDateTime(fromDate).toDate(),
					dateTimeFormatter.parseDateTime(toDate).plusDays(1).minusSeconds(1).toDate(), new ServiceErrorsImpl());
			response.setContentType("text/csv; charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment; filename=" + filename);
			PrintWriter writer = response.getWriter();
			writer.append(csvContent);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			logger.error("Exception during creating a csv report of unapproved applications.", e);
		}
	}

	@SuppressWarnings("squid:S00112")
	@RequestMapping(value = "/secure/api/searchClients", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	AjaxResponse searchPersons(@RequestParam String searchCriteria) {
		logger.info("Searching {}", searchCriteria);
		// roletype for client search
		ServiceOpsModel serviceOps = serviceOpsService.getUsers(searchCriteria, "btfg$invstr");
		if (serviceOps != null) {
			return new AjaxResponse(serviceOps);
		} else {
			return new AjaxResponse(false, serviceOps);
		}
	}

	@SuppressWarnings({ "squid:S00112" })
	@RequestMapping(value = "/secure/page/serviceOps/home", method = RequestMethod.GET)
	public ModelAndView search(@RequestParam(required = false) String searchCriteria, @RequestParam(required = false) String selection) {
		logger.info("Loading to search {}", searchCriteria);
		ServiceOpsModel serviceOps = new ServiceOpsModel();
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		if (StringUtil.isNotNullorEmpty(searchCriteria)) {
			if (StringUtil.isNotNullorEmpty(selection) && selection.contains("accountsSearch")) {
				serviceOps = serviceOpsService.getSortedAccounts(searchCriteria, serviceErrors);
			} else {
				serviceOps = serviceOpsService.getSortedUsers(searchCriteria);
			}
		}
		return new ModelAndView(View.SERVICE_OP_HOME, Attribute.SERVICE_OPS_MODEL, serviceOps).addObject("isRestricted",
				serviceOpsService.isServiceOpsRestricted());
	}

	@RequestMapping(value = "/secure/page/serviceOps/user/role", method = RequestMethod.GET)
	public @ResponseBody
	AjaxResponse checkServiceOpsSupperRole() {
		logger.info("getting service ops login user role.");
		return new AjaxResponse(true, serviceOpsService.getLeftNavPermissions());
	}

	@RequestMapping(value = "/secure/page/serviceOps/accountSearch", method = RequestMethod.GET)
	public String documentLibrary() {
		return View.ACCOUNT_SEARCH;
	}

	@SuppressWarnings("squid:S00112")
	// specific exception warning
	@RequestMapping(value = "/secure/page/serviceOps/client/accounts/{search-token}", method = RequestMethod.GET)
	public ModelAndView getAccounts(@PathVariable("search-token") String searchCriteria, @RequestParam(required = false) String searchType,
			@RequestParam(required = false) String searchFor) {

		logger.info("Loading  all accounts of client {}", searchCriteria);
		ModelAndView modelAndView = null;
		if (validateRequestAccountSearchView(searchCriteria, searchType)) {
			if (StringUtil.isNotNullorEmpty(searchType) && searchType.equalsIgnoreCase(BP_NUMBER)) {
				modelAndView = new ModelAndView(View.ACCOUNT_SEARCH, ACCOUNT_MODELS, serviceOpsService.findWrapAccountDetail(searchCriteria));

			} else if (StringUtil.isNotNullorEmpty(searchType) && searchType.equalsIgnoreCase(GCM_ID) && !searchFor.equalsIgnoreCase(SEARCH_PERSON)) {
				modelAndView = new ModelAndView(View.ACCOUNT_SEARCH, ACCOUNT_MODELS, serviceOpsService.findWrapAccountDetailsByGcm(searchCriteria));
			} else {
				ServiceOpsModel serviceOps = new ServiceOpsModel();
				if (StringUtil.isNotNullorEmpty(searchCriteria)) {
					serviceOps = serviceOpsService.getSortedUsers(searchCriteria);
				}
				modelAndView = new ModelAndView(View.ACCOUNT_SEARCH, Attribute.SERVICE_OPS_MODEL, serviceOps);
			}
		} else {
			ServiceOpsModel serviceOps = new ServiceOpsModel();
			modelAndView = new ModelAndView(View.ACCOUNT_SEARCH, Attribute.SERVICE_OPS_MODEL, serviceOps);
		}
		modelAndView.addObject(DOC_LIB_SEARCH_CRITERIA, searchCriteria);
		modelAndView.addObject(DOC_LIB_SEARCH_TYPE, searchType);
		modelAndView.addObject(DOC_LIB_SEARCH_FOR, searchFor);
		return modelAndView;
	}

	@SuppressWarnings("squid:S00112")
	// specific exception warning
	@RequestMapping(value = "/secure/page/serviceOps/account/{account-Id}/documents", method = RequestMethod.GET)
	public ModelAndView getAllDocuments(@PathVariable("account-Id") String accountId, @ModelAttribute DocumentFilterModel documentFilterDto)
			throws Exception {
		logger.info("Loading  all documents of account {}", accountId);
		DocumentKey key = new DocumentKey();
		key.setAccountId(accountId);
		List<DocumentDto> documentModels = null;
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		List<ApiSearchCriteria> criteria = ServiceOpsConverter.toApiSearchCriteria(documentFilterDto);
		if (StringUtil.isNotNullorEmpty(documentFilterDto.getNameSearchToken())) {
			documentModels = ServiceOpsConverter.sortDocumentsByUploadedDate(documentDtoService.getFilteredValue(key, criteria,
					documentFilterDto.getNameSearchToken(), serviceErrors));
		} else {
			documentModels = ServiceOpsConverter.sortDocumentsByUploadedDate(documentDtoService.search(key, criteria, serviceErrors));
		}

		ModelAndView modelAndView = new ModelAndView(View.DOCUMENT_LIBRARY, DOCUMENT_MODELS, documentModels);
		modelAndView.addObject(DOC_LIB_FILTERS, documentFilterDto);
		modelAndView.addObject(ACCOUNT_KEY, accountId);
		return modelAndView;
	}

	@RequestMapping(value = "/secure/page/serviceOps/document/{document-Id}/load", method = RequestMethod.GET)
	public @ResponseBody
	ApiResponse getDocumentMetaData(@PathVariable("document-Id") String documentId) {
		logger.info("Loading  meta data for document {}", documentId);
		DocumentKey key = new DocumentKey();
		key.setDocumentId(documentId);

		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		DocumentDto documentModel = documentDtoService.find(key, serviceErrors);
		return new ApiResponse(ApiVersion.CURRENT_VERSION, documentModel);
	}

	@RequestMapping(value = "/secure/page/serviceOps/documents/{document-Id}", method = RequestMethod.GET)
	public void downloadDocument(@PathVariable("document-Id") String documentId, HttpServletResponse response) throws IOException {
		logger.info("downloading  document {}", documentId);
		DocumentKey key = new DocumentKey();
		key.setDocumentId(documentId);
		DocumentDto doc = documentDtoService.loadDocumentVersion(key);
		if (doc == null) {
			return;
		} else {
			response.setContentType(doc.getFileType());
			response.setHeader("Content-Disposition", "attachment; filename=\"" + doc.getDocumentName() + "\"");
			response.getOutputStream().write(doc.getDocumentBytes());
		}
	}

	@RequestMapping(value = "/secure/page/serviceOps/document/delete/{document-Id}", method = RequestMethod.GET)
	public @ResponseBody
	AjaxResponse deleteDocument(@PathVariable("document-Id") String documentId) throws IOException {
		logger.info("deleting  document {}", documentId);
		boolean isDeleted = documentDtoService.deleteDocument(documentId);
		if (isDeleted) {
			return new AjaxResponse(isDeleted);
		} else {
			return new AjaxResponse(false, isDeleted);
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.GET, value = "/secure/page/serviceOps/documents/versions/{document-Id}")
	public @ResponseBody
	ApiResponse getDocumentVersions(@PathVariable("document-Id") String documentId) throws IOException {
		DocumentKey key = new DocumentKey();
		key.setDocumentId(documentId);
		@SuppressWarnings("rawtypes")
		ResultListDto<DocumentDto> listDto = new ResultListDto(documentDtoService.getVersions(key));
		return new ApiResponse(ApiVersion.CURRENT_VERSION, listDto);
	}

	@SuppressWarnings({ "squid:S00112" })
	@RequestMapping(value = "/secure/page/serviceOps/{clientId}/requireMobileConfirmation", method = RequestMethod.GET)
	public ModelAndView requireMobileConfirmation(@PathVariable("clientId") EncodedString clientId, @RequestParam(value = "mobile") String mobile)
			throws Exception {
		AustralianMobileNumberFormatter mobileFormatter = new AustralianMobileNumberFormatter();
		String formattedMobileNumber = mobileFormatter.formatMobileNumber(mobile);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		ServiceOpsModel serviceOps = serviceOpsService.getUserDetail(clientId.plainText(), true, serviceErrors);
		serviceOps.setMobileNumber(formattedMobileNumber);
		// serviceOps.setSecretKey("secret");
		return new ModelAndView(View.CLIENT_DETAIL, Attribute.SERVICE_OPS_MODEL, serviceOps);
	}

	@SuppressWarnings({ "squid:S00112" })
	@RequestMapping(value = "/secure/page/serviceOps/{clientId}/updatePPIDModal", method = RequestMethod.GET)
	public ModelAndView updatePPIdModal(@PathVariable("clientId") EncodedString clientId) throws Exception {
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		ServiceOpsModel serviceOps = serviceOpsService.getUserDetail(clientId.plainText(), true, serviceErrors);
		return new ModelAndView(View.CLIENT_DETAIL, Attribute.SERVICE_OPS_MODEL, serviceOps);
	}


	@RequestMapping(value = "/secure/page/serviceOps/{accountNumber}/updateStatementPrefModal", method = RequestMethod.GET)
	public ModelAndView updateStatementPrefModal(@PathVariable("accountNumber") EncodedString clientId) {
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		ServiceOpsModel serviceOps = serviceOpsService.getAccountDetail(clientId.toString(), serviceErrors);
		return new ModelAndView(View.ACCOUNT_DETAIL, Attribute.SERVICE_OPS_MODEL, serviceOps);
	}

	// Action Item onclick of UpdatePPID button in dailog box
	@RequestMapping(value = "/secure/page/serviceOps/{clientId}/updateppid", method = RequestMethod.POST)
	public String updateppid(@PathVariable("clientId") EncodedString clientId, @RequestParam String ppid, final RedirectAttributes redirectAttributes) {
		if (!ppid.matches("\\d+")) {
			logger.error("Only numeric values are allowed for PPID {}", ppid);
			redirectAttributes.addFlashAttribute(Attribute.STATUS, Attribute.ERROR_MESSAGE);
			redirectAttributes.addFlashAttribute(Attribute.ERR_MESSAGE, "Only numeric values are allowed for PPID");
			return "redirect:/secure/page/serviceOps/" + clientId + "/detail";
		} else {
			ServiceErrors serviceErrors = new ServiceErrorsImpl();
			boolean result = serviceOpsService.updatePPID(ppid, clientId.plainText(), serviceErrors);
			logger.info("Update ppid in abs and eam operation  {}", (result) ? "success" : "failure");

			if (!result) {
				if (serviceErrors.hasErrors()) {
					setRedirectAttributesForServiceErrors(redirectAttributes, serviceErrors);
				} else {
					redirectAttributes.addFlashAttribute(Attribute.STATUS, Attribute.ERROR_MESSAGE);
					redirectAttributes.addFlashAttribute(Attribute.ERR_MESSAGE, "There was some problem in updating the PPID");
				}
			} else {
				redirectAttributes.addFlashAttribute(Attribute.MESSAGE, "The PPID has been successfully updated");
			}
			return "redirect:/secure/page/serviceOps/" + clientId + "/detail";
		}
	}



	@RequestMapping(value = "/secure/page/serviceOps/{clientId}/updateStatementPref", method = RequestMethod.POST)
	public String updateStatementPref(@PathVariable("clientId") EncodedString clientId, @RequestParam String preference, final RedirectAttributes redirectAttributes) {

			ServiceErrors serviceErrors = new ServiceErrorsImpl();
			boolean result = serviceOpsService.updatePreference(preference, clientId.toString(), serviceErrors);
			logger.info("Update ppid in abs and eam operation  {}", result ? "success" : "failure");

			if (!result) {
				if (serviceErrors.hasErrors()) {
					setRedirectAttributesForServiceErrors(redirectAttributes, serviceErrors);
				} else {
					redirectAttributes.addFlashAttribute(Attribute.STATUS, Attribute.ERROR_MESSAGE);
					redirectAttributes.addFlashAttribute(Attribute.ERR_MESSAGE, "There was some problem in updating the statementPreference");
				}
			} else {
				   redirectAttributes.addFlashAttribute(Attribute.STATUS ,Attribute.SUCCESS_MESSAGE);
				   redirectAttributes.addFlashAttribute(Attribute.SUCCESS_MESSAGE, "The statementpreference has been successfully updated");
			}
			return "redirect:/secure/page/serviceOps/" + clientId + "/accountDetail";

	}




	@SuppressWarnings({ "squid:S00112", "squid:S1166" })
	@RequestMapping(value = "/secure/page/serviceOps/{clientId}/submitConfirmMobile", method = RequestMethod.POST)
	public String confirmMobile(@PathVariable("clientId") EncodedString clientId, @RequestParam String mobile,
			final RedirectAttributes redirectAttributes) throws Exception {
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		if (!validateRequestSubmitConfirmMobile(mobile)) {
			ServiceError serviceError = new ServiceErrorImpl();
			serviceError.setReason("Validation failed for mobile number");
			serviceErrors.addError(serviceError);
			setRedirectAttributesForServiceErrors(redirectAttributes, serviceErrors);
			redirectAttributes.addFlashAttribute(Attribute.ACTION_PERFORMED, Action.CONFIRM_SECURITY_MOBILE_NUMBER.name());
			return "redirect:/secure/page/serviceOps/" + clientId + "/detail";
		}
		
		ServiceOpsModel serviceOpsModel = serviceOpsService.getUserDetail(clientId.plainText(), true, serviceErrors);
		String userId= serviceOpsModel.getUserId();
		UserAccountStatusModel userEAMStatus = credentialService.lookupStatus(serviceOpsModel.getGcmId(), serviceErrors);
		boolean mobileNumFound = false;
		String mobileNumber = Constants.EMPTY_STRING;

		try {
			AustralianMobileNumberFormatter mobileFormatter = new AustralianMobileNumberFormatter();
			mobileNumber = mobileFormatter.formatMobileNumber(mobile);

			List<Phone> mobilePhones = serviceOpsModel.getMobilePhones();
			Iterator<Phone> itr = mobilePhones.iterator();
			while (itr.hasNext()) {
				PhoneImpl phone = (PhoneImpl) itr.next();
				if (mobileNumber.equals(mobileFormatter.formatMobileNumber(phone.getNumber()))) {
					mobileNumFound = true;
					break;
				}
			}

		} catch (IllegalStateException e) {
			logger.error("Exception occured " + e.getStackTrace());
			setRedirectAttributesForErrorMsg(redirectAttributes, e.getMessage());
		}

		if (!mobileNumFound) {
			// Adding redirect attributes to display error on screen in regular
			// info-box (ajax response) rather than throwing unchecked exception
			setRedirectAttributesForErrorMsg(redirectAttributes, "Mobile Number " + mobileNumber
					+ " specified during Confirmation process is not found in Client details");
			logger.error("Mobile Number " + mobileNumber + " specified during Confirmation process is not found in Client details");
		} else {
			UserProfileAdapterImpl userProfileAdapter = (UserProfileAdapterImpl) userProfileService.getActiveProfile();

			boolean status = deviceArrangementService.confirmMobileNumber(mobileNumber, userId, serviceOpsModel.getSafiDeviceId(),
					userProfileAdapter.getBankReferenceId(), clientId.plainText(), userEAMStatus, serviceErrors);
			logger.info("The Response From Confirm Mobile Operation is {}", status);
			if (status) {
				redirectAttributes.addFlashAttribute(Attribute.MESSAGE, cmsService.getContent("uim0130") + mobileNumber + ".");
				logger.info("The Message to be displayed on the serviceops Screen is  {}", cmsService.getContent("uim0130"));
			} else {
				redirectAttributes.addFlashAttribute(Attribute.STATUS, Attribute.ERROR_MESSAGE);
				redirectAttributes.addFlashAttribute(Attribute.ERR_MESSAGE, cmsService.getContent("Err.IP-0631") + mobileNumber + ".");
				logger.info("The Message to be displayed on the serviceops Screen is  {}", cmsService.getContent("Err.IP-0631"));
			}
			LogMarkers.audit_serviceOperation(logger, Action.CONFIRM_SECURITY_MOBILE_NUMBER.name(), clientId.plainText(),
					serviceOpsModel.getFirstName(), serviceOpsModel.getLastName());
			// Enabling feature toggling for Prm SMS 2FA Active event.
			logger.info("Checking for feature toggling for 2FA SMS Active PRM event:Service Ops Confirm Mobile scenario");
			if (featureTogglesService.findOne(new FailFastErrorsImpl()).getFeatureToggle(FeatureToggles.PRM_VIEW)) {
				// 2FA Active event trigger for PRM
				serviceOpsModel.setMobileNumber(mobileNumber);
				prmService.triggerMobileChangeServiceOpsPrmEvent(serviceOpsModel);
			}
		}
		if (serviceErrors.hasErrors()) {
			setRedirectAttributesForServiceErrors(redirectAttributes, serviceErrors);
		}
		redirectAttributes.addFlashAttribute(Attribute.ACTION_PERFORMED, Action.CONFIRM_SECURITY_MOBILE_NUMBER.name());
		return "redirect:/secure/page/serviceOps/" + clientId + "/detail";
	}

	@SuppressWarnings({ "squid:S1151", "squid:MethodCyclomaticComplexity",
			"checkstyle:com.puppycrawl.tools.checkstyle.checks.metrics.NPathComplexityCheck" })
	@RequestMapping(value = "/secure/page/serviceOps/{clientId}/submitAction", method = RequestMethod.POST)
	public String submitAction(@PathVariable("clientId") EncodedString clientId, @RequestParam("actionValue") Action action,
			@ModelAttribute(Attribute.SERVICE_OPS_MODEL) ServiceOpsModel serviceOpsModel, final RedirectAttributes redirectAttributes)
			throws Exception {
		String credentialId = null;
        ServiceOpsModel serviceOps = null;
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		logger.info("Submitting action {}", action);
		ServiceOpsModel serviceOpsclientModel = serviceOpsService.getUserDetail(clientId.plainText(), true, serviceErrors);
		try {
			credentialId = credentialService.getCredentialId(serviceOpsclientModel.getGcmId(), serviceErrors);
			if (serviceErrors.hasErrors()) {
				logger.info("getCredentialId for gcm {} has service errors", serviceOpsclientModel.getGcmId());
			}
		} catch (Exception e) {
			logger.error("Exception occured: " + e);
			logger.info("The user {} does not have a valid credential id in EAM", clientId.plainText());
		}
		switch (action) {
		// Emulation
		case SIGN_IN_AS_USER:
			return handleEmulation(serviceOpsModel, serviceOpsclientModel, clientId.plainText(), true, redirectAttributes, serviceErrors);
		case RESET_PASSWORD:
			// US853
			// EAM service call
			String tmpCreatedPassword = resetPasswordService.resetPassword(credentialId, serviceOpsclientModel.getGcmId(), serviceErrors);

			String cmsContent = cmsService.getContent("uim0126");
			if (StringUtil.isNotNullorEmpty(cmsContent) && StringUtil.isNotNullorEmpty(tmpCreatedPassword)) {
				cmsContent = cmsContent.replace("#{password}", tmpCreatedPassword);
				if (featureTogglesService.findOne(new FailFastErrorsImpl()).getFeatureToggle(FeatureToggles.PRM_VIEW) && !serviceErrors.hasErrors()) {
					prmService.triggerIssuePwdServiceOpsPrmEvent(serviceOpsclientModel);
				}
			}
			redirectAttributes.addFlashAttribute(Attribute.MESSAGE, cmsContent);
			redirectAttributes.addFlashAttribute(Attribute.ACTION_PERFORMED, Action.RESET_PASSWORD.name());
			break;
		case CONFIRM_SECURITY_MOBILE_NUMBER:
			// US940
			// SAFI service call

			AustralianMobileNumberFormatter mobileFormatter = new AustralianMobileNumberFormatter();
			try {
				String formattedMobileNumber = mobileFormatter.formatMobileNumber(serviceOpsclientModel.getPrimaryMobileNumber());
				return "redirect:/secure/page/serviceOps/{clientId}/requireMobileConfirmation?mobile=" + formattedMobileNumber + "#showModal";
			} catch (IllegalArgumentException e) {
				setRedirectAttributesForErrorMsg(redirectAttributes, e.getMessage());
				redirectAttributes.addFlashAttribute(Attribute.ACTION_PERFORMED, Action.CONFIRM_SECURITY_MOBILE_NUMBER.name());
				break;
			}

		case UPDATE_PPID:

			return "redirect:/secure/page/serviceOps/{clientId}/updatePPIDModal" + "#UpdatePPID";

		case UNLOCK_SECURITY_MOBILE_NUMBER:
			unlockSecurityMobNoCase(serviceOpsclientModel, redirectAttributes, serviceErrors);
			break;
		case BLOCK_ACCESS:
			blockAccessCase(serviceOpsclientModel, redirectAttributes, serviceErrors, credentialId, clientId.plainText());
			break;
		case UNBLOCK_ACCESS:
			unblockAccessCase(serviceOpsModel, redirectAttributes, serviceErrors, credentialId, clientId.plainText());
			if (featureTogglesService.findOne(new FailFastErrorsImpl()).getFeatureToggle(FeatureToggles.PRM_VIEW) && !serviceErrors.hasErrors()) {
				prmService.triggerAccessUnblockPrmEvent(serviceOpsclientModel);
			}
			break;
		case RESEND_REGISTRATION_EMAIL:
			// US851
			// email service call
			logger.info(ONBOARDING_SEND + "RESEND_REGISTRATION_EMAIL start");

            if(CollectionUtils.isEmpty(serviceOpsclientModel.getEmail())){

                logger.info(ONBOARDING_SEND + "REGISTRATION CODE GENERATION FAILED");
                redirectAttributes.addFlashAttribute(Attribute.STATUS, Attribute.ERROR_MESSAGE);
                redirectAttributes.addFlashAttribute(Attribute.ERR_MESSAGE, "Unable to resend registration email.Email is missing.");
                break;
            }
			try {

				String status;

				if (Attribute.INVESTOR.equals(serviceOpsclientModel.getRole())) {
					logger.info(ONBOARDING_SEND + "RESEND_REGISTRATION_EMAIL FOR INVESTOR");
					status = resendRegistrationEmailService.resendRegistrationEmailForInvestor(clientId.plainText(),
							serviceOpsclientModel.getGcmId(), serviceOpsclientModel.getRole(), serviceErrors);
				} else {
					logger.info(ONBOARDING_SEND + "RESEND_REGISTRATION_EMAIL ADVISER");
					status = resendRegistrationEmailService.resendRegistrationEmailForAdviser(clientId.plainText(), serviceOpsclientModel.getGcmId(),
							serviceOpsclientModel.getRole(), serviceErrors);
				}

				logger.info(ONBOARDING_SEND + "RESEND_REGISTRATION_EMAIL STATUS:  " + status);
				if (Attribute.SUCCESS_MESSAGE.equals(status)) {
					redirectAttributes.addFlashAttribute(Attribute.MESSAGE, cmsService.getContent("uim0135"));
				}

				redirectAttributes.addFlashAttribute(Attribute.ACTION_PERFORMED, Action.RESEND_REGISTRATION_EMAIL.name());
			} catch (Exception e) {
				logger.info(ONBOARDING_SEND + "RESEND_REGISTRATION_EMAIL", e);
				redirectAttributes.addFlashAttribute(Attribute.STATUS, Attribute.ERROR_MESSAGE);
				redirectAttributes.addFlashAttribute(Attribute.ERR_MESSAGE, "Unable to resend registration email");
			}
			logger.info(ONBOARDING_SEND + "RESEND_REGISTRATION_EMAIL end");
			break;

		case RESEND_EXISTING_REGISTRATION_CODE:
			logger.info(ONBOARDING_SEND + "RESEND_EXISTING_REGISTRATION_CODE start");

            if(CollectionUtils.isEmpty(serviceOpsclientModel.getEmail())){

                logger.info(ONBOARDING_SEND + "REGISTRATION CODE GENERATION FAILED");
                redirectAttributes.addFlashAttribute(Attribute.STATUS, Attribute.ERROR_MESSAGE);
                redirectAttributes.addFlashAttribute(Attribute.ERR_MESSAGE, "Unable to resend registration email.Email is missing.");
                break;
            }
			try {

				String status;

				if (Attribute.INVESTOR.equals(serviceOpsclientModel.getRole())) {
					logger.info(ONBOARDING_SEND + "RESEND_EXISTING_REGISTRATION_CODE FOR INVESTOR");
					status = resendRegistrationEmailService.resendRegistrationEmailWithExistingCodeForInvestor(clientId.plainText(),
							serviceOpsclientModel.getGcmId(), serviceOpsclientModel.getRole(), serviceErrors);
				} else {
					logger.info(ONBOARDING_SEND + "RESEND_EXISTING_REGISTRATION_CODE ADVISER");
					status = resendRegistrationEmailService.resendRegistrationEmailWithExistingCodeForAdviser(clientId.plainText(),
							serviceOpsclientModel.getGcmId(), serviceOpsclientModel.getRole(), serviceErrors);
				}

				logger.info(ONBOARDING_SEND + "RESEND_EXISTING_REGISTRATION_CODE STATUS:  " + status);
				if (Attribute.SUCCESS_MESSAGE.equals(status)) {
					redirectAttributes.addFlashAttribute(Attribute.MESSAGE, cmsService.getContent("uim0139"));
				}

				redirectAttributes.addFlashAttribute(Attribute.ACTION_PERFORMED, Action.RESEND_REGISTRATION_EMAIL.name());
			} catch (Exception e) {
				logger.info(ONBOARDING_SEND + "RESEND_EXISTING_REGISTRATION_CODE", e);
				redirectAttributes.addFlashAttribute(Attribute.STATUS, Attribute.ERROR_MESSAGE);
				redirectAttributes.addFlashAttribute(Attribute.ERR_MESSAGE, "Unable to resend registration email");
			}
			logger.info(ONBOARDING_SEND + "RESEND_EXISTING_REGISTRATION_CODE end");
			break;
		case CREATE_ACCOUNT:
			// US895
			// HACK by Andy Barker - Not all of the object is preserved on
			// submission, reload this to make the activate call
			serviceOps = serviceOpsService.getUserDetail(clientId.plainText(), true, serviceErrors);
			String createAccStatus = userAccountStatusService.createAccount(serviceOps);
			if (Attribute.SUCCESS_MESSAGE.equalsIgnoreCase(createAccStatus)) {
				redirectAttributes.addFlashAttribute(Attribute.MESSAGE, cmsService.getContent("uim0133"));
				redirectAttributes.addFlashAttribute(Attribute.ACTION_PERFORMED, Action.CREATE_ACCOUNT.name());
				// ignoring all the errors for create Account since the
				// createAccStatus is Success
				serviceErrors = new ServiceErrorsImpl();
			} else {
				redirectAttributes.addFlashAttribute(Attribute.STATUS, Attribute.ERROR_MESSAGE);
				redirectAttributes.addFlashAttribute(Attribute.ERR_MESSAGE, createAccStatus);
			}
			break;
        case PROVISION_MFA_DEVICE:
            ProvisionMFARequestData provisionMFARequestData = ProvisionMFARequestDataBuilder.make().withCanonicalProductName("3fb3e732d5c5429d97af392ab18e998b")
                    .withRole(serviceOpsclientModel.getRole()).withPrimaryMobileNumber(serviceOpsclientModel.getPrimaryMobileNumber()).withGcmId(serviceOpsclientModel.getGcmId()).withCISKey(serviceOpsclientModel.getCisId()).
                            withCustomerNumber(serviceOpsclientModel.getWestpacCustomerNumber()).collect();
            boolean deviceUpdateStatus = provisionMFADeviceService.provisionMFADevice(provisionMFARequestData,serviceErrors);
            if (deviceUpdateStatus) {
                redirectAttributes.addFlashAttribute(Attribute.MESSAGE, "MFA Device is setup successfully");
                redirectAttributes.addFlashAttribute(Attribute.ACTION_PERFORMED, Action.PROVISION_MFA_DEVICE.name());
                serviceErrors = new ServiceErrorsImpl();
            } else {
                redirectAttributes.addFlashAttribute(Attribute.STATUS, Attribute.ERROR_MESSAGE);
                redirectAttributes.addFlashAttribute(Attribute.ERR_MESSAGE, "Update MFA Device Failed");
            }
            break;
            case MOBILE_SECURITY_EXEMPTION:
			// US1062
			// todo: Call SAFI service
			String time = getCurrentTimePlusMinutes(30);
			String cmsText = cmsService.getContent("uim0137");
			if (StringUtil.isNotNullorEmpty(cmsText)) {
				cmsText = cmsText.trim() + " " + time + " AEST";
			}
			redirectAttributes.addFlashAttribute(Attribute.MESSAGE, cmsText);
			redirectAttributes.addFlashAttribute(Attribute.ACTION_PERFORMED, Action.MOBILE_SECURITY_EXEMPTION.name());
		}

		if (serviceErrors.hasErrors()) {
			setRedirectAttributesForServiceErrors(redirectAttributes, serviceErrors);
		}
		logger.info(ONBOARDING_SEND + "RESEND_REGISTRATION_EMAIL SUCCESS REDIRECT ATTRIBUTES:   "
				+ redirectAttributes.getFlashAttributes().get(Attribute.MESSAGE));
		LogMarkers.audit_serviceOperation(logger, action.getName(), clientId.plainText(), serviceOpsModel.getFirstName(),
				serviceOpsModel.getLastName());
		return "redirect:/secure/page/serviceOps/{clientId}/detail";
	}

	@SuppressWarnings({ "squid:S1151", "squid:MethodCyclomaticComplexity",
			"checkstyle:com.puppycrawl.tools.checkstyle.checks.metrics.NPathComplexityCheck" })
	@RequestMapping(value = "/secure/page/serviceOps/{clientId}/submitAccountDetailAction", method = RequestMethod.POST)
	public String submitAccountDetailAction(@RequestParam("clientId") String clientId ,
			@ModelAttribute(Attribute.SERVICE_OPS_MODEL) ServiceOpsModel serviceOpsModel, final RedirectAttributes redirectAttributes) {

		if((STRING_STATEMENT_PREF).equalsIgnoreCase(EncodedString.toPlainText(clientId))){ //statementpref ya cmastatemetnpref
			redirectAttributes.addAttribute("accountNumber", serviceOpsModel.getAccountNumber());
			return "redirect:/secure/page/serviceOps/{accountNumber}/updateStatementPrefModal" + "#UpdateStatementPref";
		}
		logger.info("Submitting Sign in as adviser / client action");
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		return handleEmulation(serviceOpsModel, null, EncodedString.toPlainText(clientId), false, redirectAttributes, serviceErrors);
	}

	/**
	 * Refactored method to handle emulation whether signing in from clientDetail page or accountDetail page
	 * @param serviceOpsModel
	 * @param serviceOpsclientModel
	 * @param clientId
	 * @param clientDetailFlag
	 * @param redirectAttributes
	 * @param serviceErrors
	 * @return
	 * @throws Exception
	 */
	private String handleEmulation(ServiceOpsModel serviceOpsModel,ServiceOpsModel serviceOpsclientModel, String clientId, boolean clientDetailFlag, RedirectAttributes redirectAttributes, ServiceErrors serviceErrors) {

		ServiceOpsModel serviceOpsModelLocal = serviceOpsclientModel;
		if (!clientDetailFlag) {
			try {
				serviceOpsModelLocal = serviceOpsService.getUserDetail(clientId, clientDetailFlag, serviceErrors);
			} catch (Exception e) {
				logger.error("Exception while retrieving user detail for emulation", e);
				setRedirectAttributesForErrorMsg(redirectAttributes, "Unable to retrieve user details for emulation. Please select a different user for emulation.");
				return "redirect:/secure/page/serviceOps/{clientId}/accountDetail";
			}
		}

		String gcmId = serviceOpsModelLocal.getGcmId();
		String profileId = "";

		if (!StringUtils.isEmpty(serviceOpsModelLocal.getGcmId())) {
			if (serviceOpsModelLocal.getJobProfiles() != null) {
				profileId = serviceOpsModelLocal.getJobProfiles().iterator().next().getProfileId();
			}
			logger.info("Profile id: {} for user {} found. ", profileId, serviceOpsModelLocal.getGcmId());
		}

		if (StringUtils.isEmpty(gcmId) || StringUtils.isEmpty(profileId)) {
			logger.warn("Trying to start emulation for user: {}, but gcm_id '{}', or profile_id '{}' is not present for the user!",
					serviceOpsModelLocal.getGcmId(), serviceOpsModelLocal.getGcmId(), profileId);
			setRedirectAttributesForErrorMsg(redirectAttributes, "There was a problem starting emulation for this user");
		}

		redirectAttributes.addAttribute("gcmId", StringUtils.defaultIfBlank(serviceOpsModelLocal.getUserId(), ""));
		redirectAttributes.addAttribute("emulating", profileId);

		LogMarkers.audit_serviceOperation(logger, Action.SIGN_IN_AS_USER.name(), clientId, serviceOpsModelLocal.getFirstName(),
				serviceOpsModelLocal.getLastName());

		if (serviceErrors.hasErrors()) {
			setRedirectAttributesForServiceErrors(redirectAttributes, serviceErrors);
			if (clientDetailFlag) {
				return "redirect:/secure/page/serviceOps/{clientId}/detail";
			} else {
				return "redirect:/secure/page/serviceOps/{clientId}/accountDetail";
			}
		}

		if (!clientDetailFlag && serviceOpsModelLocal.getLoginStatus() != null && !UserAccountStatus.Group.ACTIVE.equals(serviceOpsModelLocal.getLoginStatus().getGroup())) {
			setRedirectAttributesForErrorMsg(redirectAttributes, "User is not in ACTIVE status. Emulation will not work. Please select a different user for emulation.");
			return "redirect:/secure/page/serviceOps/{clientId}/accountDetail";
		}

		return "redirect:/secure/page/serviceOps/startEmulation";
	}

	private void setRedirectAttributesForServiceErrors(RedirectAttributes redirectAttributes, ServiceErrors serviceErrors) {
		logger.info("Inside setRedirectAttributesForServiceErrors(...) method");
		String errorMessage = Constants.EMPTY_STRING;
		for (ServiceError serviceError : serviceErrors.getErrorList()) {
			String messagid = serviceError.getCorrelationId();
			logger.debug("Service error message id {} for service error {}", messagid, serviceError);
			if (StringUtils.isEmpty(messagid)) {
				messagid = Constants.UNKNOWN;
			}
			if (messagid != Constants.UNKNOWN) {
				errorMessage = cmsService.getDynamicContent(ValidationErrorCode.ERROR_MSG_WITH_CORRELATIONID, new String[] { messagid });
			}
		}
		redirectAttributes.addFlashAttribute(Attribute.STATUS, Attribute.ERROR_MESSAGE);
		redirectAttributes.addFlashAttribute(Attribute.ERR_MESSAGE, errorMessage);
	}

	private void setRedirectAttributesForErrorMsg(RedirectAttributes redirectAttributes, String errorMessage) {
		redirectAttributes.addFlashAttribute(Attribute.STATUS, Attribute.ERROR_MESSAGE);
		redirectAttributes.addFlashAttribute(Attribute.ERR_MESSAGE, errorMessage);
	}

	// returns local time plus minutes in format HH:MM:SS
	private String getCurrentTimePlusMinutes(int minutes) {
		LocalTime localTime = new LocalTime().plusMinutes(minutes);
		String hoursMinutesSeconds = String.format("%02d:%02d:%02d", localTime.getHourOfDay(), localTime.getMinuteOfHour(),
				localTime.getSecondOfMinute());
		return hoursMinutesSeconds;
	}

	private boolean validateRequestAccountSearchView(String SearchCriteria, String searchType) {
		// this line was added keep existing functionality as it is
		if (null == searchType) {
			return true;
		}
		if (SearchCriteria.length() < 2) {
			return false;
		} else if (StringUtil.isNotNullorEmpty(searchType)) {
			if (BP_NUMBER.equals(searchType) || GCM_ID.equals(searchType)) {

				return Pattern.matches("\\d+", SearchCriteria);
			} else if (StringUtil.isNotNullorEmpty(searchType) && searchType.equalsIgnoreCase("name")) {
				return Pattern.matches("[A-Za-z0-9\\s]+", SearchCriteria);
			}
		}
		return false;
	}

	private boolean validateRequestSubmitConfirmMobile(String mobile) {
		return Pattern.matches("\\d+", mobile);
	}

	private String unblockAccessCase(ServiceOpsModel serviceOpsModel, RedirectAttributes redirectAttributes, ServiceErrors serviceErrors,
			String credentialId, String clientId) {
		String message = cmsService.getContent("uim0128");
		String strBreak = "break";
		if (serviceOpsModel.getAction().equalsIgnoreCase(UserAccountStatus.SUSP_TP_PW_XP.getValue())) {
			String newPassword = blockService.unblockUserAccessWithResetPassword(credentialId, serviceErrors);
			if (newPassword != null) {
				message = cmsService.getContent("uim0126");
				if (StringUtil.isNotNullorEmpty(message)) {
					message = message.replace("#{password}", newPassword);
				}
			}
			redirectAttributes.addFlashAttribute(Attribute.MESSAGE, message);
			redirectAttributes.addFlashAttribute(Attribute.ACTION_PERFORMED, Action.RESET_PASSWORD.name());
			return strBreak;
		} else {
			// EAM service call
			blockService.unblockUserAccess(credentialId, serviceErrors);
			// TODO: Need to move this code to Service Layer but Service layer
			// does not have access to serviceOpsModel.
			redirectAttributes.addFlashAttribute(Attribute.ACTION_PERFORMED, Action.UNBLOCK_ACCESS.name());
			redirectAttributes.addFlashAttribute(Attribute.MESSAGE, message);
			return strBreak;
		}
	}

	private void blockAccessCase(ServiceOpsModel serviceOpsClientModel, RedirectAttributes redirectAttributes, ServiceErrors serviceErrors,
			String credentialId, String clientId) {
		boolean isAccessBlocked = blockService.blockUserAccess(credentialId, serviceErrors);
		logger.info("Checking for feature toggling for BLOCK_ACCESS PRM event:Block Users Account via Service Desktop");
		if (featureTogglesService.findOne(new FailFastErrorsImpl()).getFeatureToggle(FeatureToggles.PRM_VIEW) && !serviceErrors.hasErrors()) {
			prmService.triggerAccessBlockPrmEvent(serviceOpsClientModel, isAccessBlocked);
		}
		redirectAttributes.addFlashAttribute(Attribute.MESSAGE, cmsService.getContent("uim0127"));
		redirectAttributes.addFlashAttribute(Attribute.ACTION_PERFORMED, Action.BLOCK_ACCESS.name());

	}

	private void unlockSecurityMobNoCase(ServiceOpsModel serviceOpsclientModel, RedirectAttributes redirectAttributes, ServiceErrors serviceErrors) {

		// US855
		// SAFI service call
		String mobileNumber = deviceArrangementService.unBlockMobile(serviceOpsclientModel, userProfileService.getAvaloqId(), serviceErrors);
		logger.info("mobileNumber{}", mobileNumber);
		String cmsValue = cmsService.getContent("uim0131");
		if (StringUtil.isNotNullorEmpty(cmsValue)) {
			cmsValue = cmsValue.replace("#{mobile}", serviceOpsclientModel.getPrimaryMobileNumber());
		}
		redirectAttributes.addFlashAttribute(Attribute.MESSAGE, cmsValue);
		redirectAttributes.addFlashAttribute(Attribute.ACTION_PERFORMED, Action.UNLOCK_SECURITY_MOBILE_NUMBER.name());
	}

	public static String getClientIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
}