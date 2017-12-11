package com.bt.nextgen.reports.managedfunds;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.model.ManagedFundAssetDto;
import com.bt.nextgen.api.asset.service.AvailableAssetDtoService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.reporting.BaseReport;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.AssetStatus;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.bt.nextgen.core.api.UriMappingConstants.EFFECTIVE_DATE_PARAMETER_MAPPING;

@Report("availableManagedFundsReport")
public class AvailableManagedFundReport extends BaseReport {

	private static final String DISCLAIMER_CONTENT = "DS-IP-0199";
	@Autowired
	private ContentDtoService contentService;
	@Autowired
	private UserProfileService userProfileService;
	@Autowired
	private AvailableAssetDtoService availableAssetDtoService;

	@ReportBean("disclaimer")
	@SuppressWarnings("squid:S1172")
	public String getDisclaimer(Map<String, String> params) {
		ServiceErrors serviceErrors = new FailFastErrorsImpl();
		ContentKey key = new ContentKey(DISCLAIMER_CONTENT);
		ContentDto content = contentService.find(key, serviceErrors);
		return content.getContent();
	}

	@ReportBean("effectiveDate")
	@SuppressWarnings("squid:S1172")
	public DateTime getStartDate(Map<String, String> params) {
		String effDate = params.get(EFFECTIVE_DATE_PARAMETER_MAPPING);
		if (effDate == null) {
			return new DateTime();
		} else {
			return new DateTime(effDate);
		}
	}

	@ReportBean("managedFunds")
	@SuppressWarnings("squid:S1172")
	public List<ManagedFundAssetDto> getManagedFunds(Map<String, String> params) {
		List<ApiSearchCriteria> criteria = new ArrayList<ApiSearchCriteria>();

		criteria.add(new ApiSearchCriteria(Attribute.ASSET_TYPE,
				SearchOperation.EQUALS, AssetType.MANAGED_FUND.name(), OperationType.STRING));
		criteria.add(new ApiSearchCriteria(Attribute.ASSET_STATUS,
				SearchOperation.EQUALS, AssetStatus.OPEN.name(), OperationType.STRING));

		ServiceErrors serviceErrors = new FailFastErrorsImpl();
		List<AssetDto> response = availableAssetDtoService.getFilteredValue(
				null, criteria, serviceErrors);
		List<AssetDto> managedFundAssetsList = Lambda.filter(
				new LambdaMatcher<AssetDto>() {
					@Override
					protected boolean matchesSafely(AssetDto assetDto) {
						if (assetDto instanceof ManagedFundAssetDto) {
							return true;
						}
						return false;
					}
				}, response);

		List<ManagedFundAssetDto> managedFundAssets = new ArrayList<>();
		for (AssetDto assetDto : managedFundAssetsList) {
			managedFundAssets.add((ManagedFundAssetDto) assetDto);
		}

		managedFundAssets = Lambda.sort(managedFundAssets,
				Lambda.on(ManagedFundAssetDto.class).getAssetClass());

		return managedFundAssets;
	}

	@ReportBean("reportType")
	@SuppressWarnings("squid:S1172")
	public String getReportName(Map<String, String> params) {
		return "Available funds list";
	}

	@ReportBean("dealerGroupName")
	@SuppressWarnings("squid:S1172")
	public String getDelearGroupName(Map<String, String> params) {
		//TODO - UPS REFACTOR1 - This should ideally be based on either OE position (in the context of an adviser) or Account (in the case of an investor or if this is account specific).
		String dealerName = userProfileService.getDealerGroupBroker().getPositionName();
		int index = dealerName.indexOf("(");
		if (index == -1) {
			return dealerName;
		} else {
			return dealerName.substring(0, index).trim();
		}
	}
}
