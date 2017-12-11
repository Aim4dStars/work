package com.bt.nextgen.api.notification.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.notification.model.AccountTypeConverter;
import com.bt.nextgen.api.notification.model.NotificationCategoryConverter;
import com.bt.nextgen.api.notification.model.NotificationDto;
import com.bt.nextgen.api.notification.model.NotificationDtoKey;
import com.bt.nextgen.api.notification.model.NotificationSubCategoryConverter;
import com.bt.nextgen.api.util.ApiConstants;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.broker.BrokerWrapper;
import com.bt.nextgen.service.integration.messages.NotificationIntegrationService;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.integration.messages.Notification;
import com.btfin.panorama.core.security.integration.messages.NotificationCategory;
import com.btfin.panorama.core.security.integration.messages.NotificationStatus;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static com.bt.nextgen.api.util.ApiConstants.ACCOUNT;
import static com.bt.nextgen.api.util.ApiConstants.ADVISER;
import static com.bt.nextgen.api.util.ApiConstants.CATEGORIES;
import static org.hamcrest.Matchers.isIn;

/**
 * This service searches all notifications for either user or client notifications
 * and returns a list which has been filtered out according to the given criteria
 */
@Service
public class NotificationSearchDtoServiceImpl implements NotificationSearchDtoService {

	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationSearchDtoServiceImpl.class);

	@Autowired
	private NotificationIntegrationService notificationService;

	@Autowired
	@Qualifier("avaloqAccountIntegrationService")
	private AccountIntegrationService accountService;

	@Autowired
	private ProductIntegrationService productService;

	@Autowired
	private BrokerIntegrationService brokerIntegrationService;

	@Autowired
	private UserProfileService userProfileService;

	@Override
	public List<NotificationDto> search(NotificationDtoKey key, List<ApiSearchCriteria> criteriaList,
										ServiceErrors serviceErrors) {
		LocalDate startDate = null;
		LocalDate endDate = null;

		for (Iterator<ApiSearchCriteria> iterator = criteriaList.iterator(); iterator.hasNext(); ) {
			ApiSearchCriteria criteria = iterator.next();
			if (criteria.getProperty().equalsIgnoreCase(ApiConstants.START_DATE)) {
				startDate = LocalDate.parse(criteria.getValue(), DateTimeFormat.forPattern(ApiConstants.DATE_FORMAT));
			}
			if (criteria.getProperty().equalsIgnoreCase(ApiConstants.END_DATE)) {
				endDate = LocalDate.parse(criteria.getValue(), DateTimeFormat.forPattern(ApiConstants.DATE_FORMAT));
			}
		}
		LocalTime startTime = new LocalTime("00:00:00");
		LocalTime endTime = new LocalTime("23:59:59");
		if (startDate == null) {
			startDate = new LocalDate().minusMonths(1);

		}
		if (endDate == null) {
			endDate = new LocalDate();
		}
		final UserProfile profile = userProfileService.getActiveProfile();
		List<Notification> responseList = notificationService.loadNotifications(getProfileIdList(profile,
				serviceErrors), startDate.toDateTime(startTime), endDate.toDateTime(endTime), serviceErrors);
		List<NotificationDto> notificationList = toNotificationDto(key, responseList, profile, serviceErrors);
		List<NotificationDto> filteredList = new ArrayList<>();
		try {
			filteredList = notificationFilter(notificationList, criteriaList);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new BadRequestException(ApiVersion.CURRENT_VERSION, "Unable to filter notification list", e);
		}
		return filteredList;
	}

	private List getProfileIdList(UserProfile profile, ServiceErrors serviceErrors) {
		List<String> resultList = new ArrayList<>();
		// Get the Job Profile ID for the Adviser or Investor
		if (profile.getJobRole() == JobRole.ADVISER || profile.getJobRole() == JobRole.INVESTOR
				|| profile.getJobRole() == JobRole.ACCOUNTANT) {
			resultList.add(profile.getProfileId());
			LOGGER.info("Notification to be fetched for: {} ", profile.getProfileId());
		} else if (profile.getJobRole() == JobRole.ACCOUNTANT_SUPPORT_STAFF) {
			Collection<BrokerIdentifier> adviserList = brokerIntegrationService.getAdvisersForUser(profile,
					serviceErrors);
			List<BrokerKey> brokerKeys = extract(adviserList, on(BrokerIdentifier.class).getKey());
    		List<BrokerUser> advisers = brokerIntegrationService.getAccountantBrokerUsers(brokerKeys, serviceErrors);
			for(BrokerUser adviser: advisers){
				resultList.add(adviser.getProfileId());
			}
			// Add the Profile Id of Logged in User
			resultList.add(profile.getProfileId());
		}
		// Get the list of associated Advisers under him eg; if he is a paraplanner or manager
		else {
			Collection<BrokerIdentifier> adviserList = brokerIntegrationService.getAdvisersForUser(profile,
					serviceErrors);
			List<BrokerKey> brokerKeys = extract(adviserList, on(BrokerIdentifier.class).getKey());
			Map<BrokerKey, BrokerWrapper> brokerWrapperMap = brokerIntegrationService.getAdviserBrokerUser(brokerKeys, serviceErrors);
			for(BrokerWrapper brokerWrapper : brokerWrapperMap.values()){
				BrokerUser adviser = brokerWrapper.getBrokerUser();
				LOGGER.debug("Adviser Notification to be fetched for: {} is {} ", profile.getProfileId(),
						adviser.getProfileId());
				resultList.add(adviser.getProfileId());
			}
			// Add the Profile Id of Logged in User
			resultList.add(profile.getProfileId());
		}
		return resultList;
	}

	protected List<NotificationDto> toNotificationDto(NotificationDtoKey key, List<Notification> responseList,
													  UserProfile profile, ServiceErrors serviceErrors) {
		List<NotificationDto> notificationList = new ArrayList<>();
		Map<BrokerKey, BrokerWrapper> brokerWrapperMap = new HashMap<>();
		Map<AccountKey, WrapAccount> accounts = new HashMap<>();
		if (!responseList.isEmpty()) {
			Map<ProductKey, Product> productMap = new HashMap<>();
			accounts = accountService.loadWrapAccountWithoutContainers(serviceErrors);
			//If Role is not Adviser
			if (profile.getJobRole() != JobRole.ADVISER) {
				//Extract AccountKeys from Notification List.
				List<AccountKey> accountKeys = Lambda.extract(responseList, Lambda.on(Notification.class).getAccount().getAccountKey());
				//Get list of WrapAccount from map which matches accountKeys extracted from Notification list.
				List<WrapAccount> wrapAccounts = Lambda.select(accounts.values(), having(Lambda.on(WrapAccount.class).getAccountKey(), isIn(accountKeys)));
				//Extract the BrokerKeys from WrapAccount list.
				List<BrokerKey> brokerKeys = Lambda.extract(wrapAccounts, Lambda.on(WrapAccount.class).getAdviserPositionId());
				LOGGER.info("Number of BrokerKeys in getAdviserBrokerUser for Notification Api: {}", brokerKeys.size());
				brokerWrapperMap = brokerIntegrationService.getAdviserBrokerUser(brokerKeys, serviceErrors);
			}
			for (Notification notification : responseList) {
				NotificationDto notificationDto = null;
				//Client Messages
				if (!notification.isMyMessage() && key.isClientNotification()) {
					notificationDto = processNotification(notification, profile.getJobRole(), accounts,
							productMap, brokerWrapperMap, serviceErrors);
				}

				//My Messages
				if ((notification.isMyMessage() && !key.isClientNotification())
						&& (profile.getProfileId().equals(notification.getRecipientId().toString()))) {
					notificationDto = processNotification(notification, profile.getJobRole(), accounts,
							productMap, brokerWrapperMap, serviceErrors);
				}
				if (notificationDto != null) {
					notificationList.add(notificationDto);
				}
			}
		}
		return notificationList;
	}

	private NotificationDto processNotification(Notification notification, JobRole jobRole, Map<AccountKey,
			WrapAccount> accounts, Map<ProductKey, Product> productMap, Map<BrokerKey, BrokerWrapper> brokerWrapperMap, ServiceErrors serviceErrors) {
		return populateNotificationDto(notification, jobRole, accounts, productMap, brokerWrapperMap, serviceErrors);
	}

	private NotificationDto populateNotificationDto(Notification notification, JobRole role, Map<AccountKey,
			WrapAccount> accounts, Map<ProductKey, Product> productMap, Map<BrokerKey, BrokerWrapper> brokerWrapperMap, ServiceErrors serviceErrors) {

		NotificationStatus status = notification.getNotificationStatus();
		if (status != NotificationStatus.UNKNOWN) {

			NotificationDto notificationDto = setNotificationFields(notification, role, accounts, productMap, brokerWrapperMap, serviceErrors, status);
			return notificationDto;
		}
		return null;
	}

	private NotificationDto setNotificationFields(Notification notification,
												  JobRole role, Map<AccountKey, WrapAccount> accounts,
												  Map<ProductKey, Product> productMap,
												  Map<BrokerKey, BrokerWrapper> brokerWrapperMap,
												  ServiceErrors serviceErrors,
												  NotificationStatus status) {

		NotificationDto notificationDto = new NotificationDto();
		notificationDto.setUnread(status == NotificationStatus.UNREAD ? true : false);
		notificationDto.setId(notification.getNotificationId());
		notificationDto.setAccountType(AccountTypeConverter.convert(notification.getOwnerAccountType()));
		notificationDto.setCategory(NotificationCategoryConverter.convert(notification.getNotificationCategoryId()));
		notificationDto.setCategoryCode( null != notification.getNotificationCategoryId() ? notification.getNotificationCategoryId().getCategoryValue() : "");
		notificationDto.setDate(notification.getNotificationTimeStamp());
		notificationDto.setDetails(notification.getNotificationMessage());
		notificationDto.setHighPriority(notification.getEventPriority() == 1 ? true : false);
		notificationDto.setUnread(NotificationStatus.UNREAD.equals(notification.getNotificationStatus())
                ? true : false);
		notificationDto.setSubCategory(NotificationSubCategoryConverter.convert(notification
                .getNotificationSubCategoryId()));
		notificationDto.setDocumentUrl(notification.getDocumentUrl());
		notificationDto.setAdviserDshBd(notification.isAdviserDashBoadFlag());

		notificationDto.setUrl(notification.getUrl());
		notificationDto.setUrlText(notification.getUrlText());
		notificationDto.setMessageType(notification.getType());
		notificationDto.setPersonalizedMessage(notification.getPersonalizedMessage());

		if (role == JobRole.INVESTOR || !notification.isMyMessage()) {
			getAccountDetails(role, accounts.get(notification.getAccount().getAccountKey()),
					notificationDto, serviceErrors, productMap, brokerWrapperMap);
		}

		if (StringUtils.isNotEmpty(notification.getUrl()) || StringUtils.isNotEmpty(notification.getUrlText()) ||
				StringUtils.isNotEmpty(notification.getPersonalizedMessage()) || StringUtils.isNotEmpty(notification.getType())) {
			if ( NotificationCategory.ASX_ANNOUNCEMENTS.equals(notification.getNotificationCategoryId())) {
                notificationDto.setDetails(
                        "Your adviser has shared an ASX announcement with you, which you can view on the full Panorama website under ‘Messages’.");
			} else if (NotificationCategory.MARKET_NEWS.equals(notification.getNotificationCategoryId())) {
                notificationDto.setDetails(
                        "Your adviser has shared a news article with you, which you can view on the full Panorama website under ‘Messages’.");
			} else {
				notificationDto.setDetails("");
			}
		}

		return notificationDto;
	}

	private void getAccountDetails(JobRole role, WrapAccount account, NotificationDto notificationDto,
								   ServiceErrors serviceErrors, Map<ProductKey, Product> productMap, Map<BrokerKey, BrokerWrapper> brokerWrapperMap) {
		if (account != null) {
			notificationDto.setAccountId(EncodedString.fromPlainText(account.getAccountKey().getId()).toString());

			if (account.getAccountStatus() != AccountStatus.PEND_OPN) {
				notificationDto.setAccountNumber(account.getAccountNumber());
			}
			if (StringUtils.isNotBlank(account.getAccountName())) {
				notificationDto.setAccountName(account.getAccountName());
			}
			if (account.getProductKey() != null) {
				setProductDetails(productMap, account, serviceErrors, notificationDto);
			}
			if (!JobRole.ADVISER.equals(role) && account.getAdviserPositionId() != null) {
				BrokerUser adviser = brokerWrapperMap.get(account.getAdviserPositionId()).getBrokerUser();
				if (adviser != null) {
					notificationDto.setAdviserName(adviser.getFirstName() + " " + adviser.getLastName());
				}
			}
		}
	}

	private void setProductDetails(Map<ProductKey, Product> productMap, WrapAccount account,
								   ServiceErrors serviceErrors, NotificationDto notificationDto) {
		Product productDetails = productMap.get(account.getProductKey());
		if (productDetails == null) {
			productDetails = productService.getProductDetail(account.getProductKey(), serviceErrors);
			productMap.put(account.getProductKey(), productDetails);
		}

		if (productDetails != null && StringUtils.isNotBlank(productDetails.getProductName())) {
			notificationDto.setProductName(productDetails.getProductName());
		}
	}

	private List<NotificationDto> notificationFilter(List<NotificationDto> notificationList,
													 List<ApiSearchCriteria> criteriaList)
			throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		Map<String, LocalDate> dateCriteriaMap = new HashMap<String, LocalDate>();

		//Create criteria map for start and end date and remove from original criteria list
		for (Iterator<ApiSearchCriteria> iterator = criteriaList.iterator(); iterator.hasNext(); ) {
			ApiSearchCriteria criteria = iterator.next();
			if (criteria.getProperty().equalsIgnoreCase(ApiConstants.START_DATE) || criteria.getProperty().
					equalsIgnoreCase(ApiConstants.END_DATE)) {
				dateCriteriaMap.put(criteria.getProperty(),
						LocalDate.parse(criteria.getValue(), DateTimeFormat.forPattern(ApiConstants.DATE_FORMAT)));
				iterator.remove();
			}
		}

		for (Iterator<NotificationDto> iterator = notificationList.iterator(); iterator.hasNext(); ) {
			NotificationDto notificationDto = iterator.next();
			LocalDate notificationDate =
					notificationDto.getDate() != null ? notificationDto.getDate().toLocalDate() : new LocalDate();
			//Filter list based on start and end date
			if (dateCriteriaMap.size() == 2) {
				if (notificationDate.isBefore(dateCriteriaMap.get(ApiConstants.START_DATE))
						|| notificationDate.isAfter(dateCriteriaMap.get(ApiConstants.END_DATE))) {
					iterator.remove();
					continue;
				}
			}
			//Filter list with criteria other than start and end date
			for (ApiSearchCriteria criteria : criteriaList) {
				if (filterNotifications(criteria, notificationDto)) {
					iterator.remove();
					break;
				}
			}
		}
		return notificationList;
	}

	private boolean filterNotifications(ApiSearchCriteria criteria, NotificationDto notificationDto)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		boolean isFilter = false;
		switch (criteria.getProperty()) {
			case CATEGORIES:
				String[] splitCriteria = criteria.getValue().split(",");
				for (String value : splitCriteria) {
					isFilter = !Matchers.equalTo(value).matches(notificationDto.getCategoryCode());
					//Where a criteria has multiple values, the first match will do nothing and move onto the next
					// criteria
					if (!isFilter) {
						break;
					}
				}
				break;
			case ACCOUNT:
				if (notificationDto.getAccountId() != null) {
					isFilter = !Matchers.equalTo(EncodedString.toPlainText(criteria.getValue()))
							.matches(EncodedString.toPlainText(notificationDto.getAccountId()));
				} else {
					isFilter = true;
				}
				break;
			case ADVISER:
				isFilter = !(Matchers.equalTo(criteria.getValue()).matches(notificationDto.getAdviserName()) ||
						notificationDto.getCategoryCode().equals(NotificationCategory.CORPORATE_ACTIONS.getCategoryValue()));
				break;
            case "adviserDshBd":
                isFilter = !(Matchers.equalTo(Boolean.valueOf(criteria.getValue())).matches(notificationDto.isAdviserDshBd()));
                break;
		}
		return isFilter;
	}

}
