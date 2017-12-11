// Commented out to stop fortify from flagging.  Will reinstate and fix when ROA work resumes (no eta).

//package com.bt.nextgen.api.corporateaction.v1.controller;
//
//import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDto;
//import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDtoKey;
//import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
//import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionPersistenceDto;
//import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionNotificationDtoService;
//import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionRoaPersistenceDtoService;
//import com.bt.nextgen.core.api.UriMappingConstants;
//import com.bt.nextgen.core.api.model.ApiResponse;
//import com.bt.nextgen.core.api.model.KeyedApiResponse;
//import com.bt.nextgen.core.api.operation.Submit;
//import com.btfin.panorama.core.security.encryption.EncodedString;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.joda.time.DateTime;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RequestPart;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.List;
//
//@Controller
//@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
//public class CorporateActionRoaApiController {
//	private static final Logger logger = LoggerFactory.getLogger(CorporateActionRoaApiController.class);
//
//	private static final String CURRENT_VERSION = "v1_0";
//	private static final String CA_ID = "ca-id";
//	private static final String ATTACHMENTS = "attachments";
//	private static final String CLOSE_DATE = "closeDate";
//	private static final String OPTIONS = "options";
//	private static final String ACCOUNTS = "accounts";
//
//	@Autowired
//	private CorporateActionNotificationDtoService notificationDtoService;
//
//	@Autowired
//	private CorporateActionRoaPersistenceDtoService roaPersistenceDtoService;
//
//	@PreAuthorize("isAuthenticated() and ((hasPermission(null, 'View_intermediary_reports')) OR (hasPermission(null, 'View_model_portfolios')))")
//	@RequestMapping(method = RequestMethod.POST, value = "${api.corporateaction.v1.uri.roaCorporateActionSend}")
//	@ResponseBody
//	public ApiResponse sendROAs(@PathVariable(CA_ID) String corporateActionIdString,
//								@RequestParam(value = ACCOUNTS, required = true) String accounts,
//								@RequestPart(value = ATTACHMENTS, required = false) MultipartFile[] attachments) {
//
//		final ObjectMapper mapper = new ObjectMapper();
//		List<CorporateActionAccountDetailsDto> accountDetailsDtoList;
//
//		try {
//			accountDetailsDtoList = mapper.readValue(accounts, new TypeReference<List<CorporateActionAccountDetailsDto>>() {
//			});
//		} catch (IOException e) {
//			logger.error("IOException", e);
//			throw new IllegalArgumentException("Unable to map corporate action account fields: " + e);
//		}
//
//		return new ApiResponse(CURRENT_VERSION, notificationDtoService.sendRoas(new CorporateActionDtoKey(
//				EncodedString.toPlainText(corporateActionIdString)), null, accountDetailsDtoList, attachments));
//	}
//
//	/**
//	 * Save corporate action elections.
//	 *
//	 * @param corporateActionIdString the corporate action ID
//	 * @param closeDate               the panorama close date
//	 * @param options                 the list of options
//	 * @param accounts                the list of accounts
//	 * @return CorporateActionPersistenceDto with status
//	 */
//	@PreAuthorize("isAuthenticated() and hasPermission(null, 'Submit_trade_to_executed')")
//	@RequestMapping(method = RequestMethod.POST, value = "${api.corporateaction.v1.uri.roaCorporateActionGenerate}")
//	@ResponseBody
//	public KeyedApiResponse<CorporateActionDtoKey> generateROAs(
//			@PathVariable(CA_ID) String corporateActionIdString,
//			@RequestParam(value = CLOSE_DATE, required = true) String closeDate,
//			@RequestParam(value = OPTIONS, required = true) String options,
//			@RequestParam(value = ACCOUNTS, required = true) String accounts) {
//
//		final CorporateActionPersistenceDto persistenceDto =
//				createCorporateActionPersistenceDto(corporateActionIdString, closeDate, options, accounts, true);
//
//		return new Submit<>(CURRENT_VERSION, roaPersistenceDtoService, null, persistenceDto).performOperation();
//	}
//
//	/**
//	 * Common method to create CorporateActionPersistenceDto object
//	 *
//	 * @param corporateActionIdString the corporate action ID
//	 * @param closeDate               the panorama close date
//	 * @param options                 the list of options
//	 * @param accounts                the list of accounts
//	 * @param updateMode              set to true if update instead of delete-insert
//	 * @return CorporateActionPersistenceDto with status
//	 */
//	private CorporateActionPersistenceDto createCorporateActionPersistenceDto(String corporateActionIdString, String closeDate,
//																			  String options, String accounts, boolean updateMode) {
//		final String corporateActionId = EncodedString.toPlainText(corporateActionIdString);
//
//		final ObjectMapper mapper = new ObjectMapper();
//		List<CorporateActionOptionDto> optionDtoList;
//		List<CorporateActionAccountDetailsDto> accountDetailsDtoList;
//
//		try {
//			optionDtoList = mapper.readValue(options, new TypeReference<List<CorporateActionOptionDto>>() {
//			});
//			accountDetailsDtoList = mapper.readValue(accounts, new TypeReference<List<CorporateActionAccountDetailsDto>>() {
//			});
//		} catch (IOException e) {
//			logger.error("IOException", e);
//			throw new IllegalArgumentException("Unable to map corporate action fields: " + e);
//		}
//
//		return new CorporateActionPersistenceDto(corporateActionId, new DateTime(closeDate), optionDtoList, accountDetailsDtoList,
//				updateMode);
//	}
//}
