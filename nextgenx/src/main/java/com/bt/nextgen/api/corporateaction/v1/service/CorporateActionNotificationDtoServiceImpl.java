package com.bt.nextgen.api.corporateaction.v1.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionNotificationDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionIntegrationService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;


@Service
public class CorporateActionNotificationDtoServiceImpl implements CorporateActionNotificationDtoService {
	//private static final Logger logger = LoggerFactory.getLogger(CorporateActionNotificationDtoServiceImpl.class);

	@Autowired
	private CorporateActionIntegrationService caIntegrationService;

	//	@Autowired
	//	@Qualifier("avaloqAccountIntegrationService")
	//	private AccountIntegrationService accountIntegrationService;
	//
	//	@Autowired
	//	private BrokerHelperService brokerHelperService;

	@Autowired
	private CorporateActionDirectAccountService corporateActionDirectAccountService;

	// NOTE: work-in-progress.  Commented out for now.
	//    @Override
	//    public CorporateActionNotificationDto sendRoas(CorporateActionDtoKey key, CorporateActionNotificationDto dto,
	//                                                   List<CorporateActionAccountDetailsDto> accountDetailsDtoList,
	//                                                   MultipartFile[] attachments) {
	//        List<CorporateActionAttachment> caAttachments = null;
	//
	//        if (attachments != null) {
	//            caAttachments = new ArrayList<>();
	//            for (MultipartFile file : attachments) {
	//                logger.info("File: " + file.getOriginalFilename() + " => " + file.getSize() + " bytes");
	//                caAttachments.add(new CorporateActionAttachment("0", file.getOriginalFilename(), file.getSize(),
	//                        CorporateActionAttachmentStatus.OK));
	//            }
	//        }
	//
	//        return new CorporateActionNotificationDto(CorporateActionSendNotificationStatus.OK, caAttachments);
	//    }

	public List<CorporateActionNotificationDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
		List<AccountKey> accountIds =
				Lambda.collect(corporateActionDirectAccountService.getDirectAccounts(), Lambda.on(WrapAccount.class).getAccountKey());
		if (!accountIds.isEmpty()) {
			DateTime fromDate = null;
			DateTime toDate = null;
			for (ApiSearchCriteria criteria : criteriaList) {
				if (Attribute.START_DATE.equalsIgnoreCase(criteria.getProperty())) {
					fromDate = new DateTime(criteria.getValue());
				}
				if (Attribute.END_DATE.equalsIgnoreCase(criteria.getProperty())) {
					toDate = new DateTime(criteria.getValue());
				}
			}
			CorporateActionNotificationDto notificationDto = new CorporateActionNotificationDto();
			notificationDto.setNotificationCount(
					caIntegrationService.getCountForPendingCorporateEvents(accountIds, fromDate, toDate, serviceErrors)
							.getNotificationCnt());
			return Collections.singletonList(notificationDto);
		} else {
			throw new AccessDeniedException("You do not have the permission!");
		}
	}
}
