package com.bt.nextgen.reports.asset;

import com.bt.nextgen.api.asset.model.AssetHoldersDto;
import com.bt.nextgen.api.asset.service.AssetHoldersDtoService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.reporting.BaseReportV2;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.bt.nextgen.api.asset.util.AssetConstants.*;
import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType.*;
import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation.*;
import static org.joda.time.DateTime.*;

@Report("AssetHoldersCsvReport")
@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
public class AssetHoldersCsvReport extends BaseReportV2 {

    private static final String DISCLAIMER_CONTENT = "DS-IP-0119";
    private static final String REPORT_NAME = "Client holdings report";
    private static final String ASSET_NAME_PARAM = "assetName";
    private static final String ASSET_CODE_PARAM = "assetCode";
    private static final String ASSET_PRICE_PARAM = "assetPrice";
    private static final String ASSET_PRICE_DATE_PARAM = "priceDate";

    @Autowired
    private AssetHoldersDtoService assetHoldersDtoService;

    @Autowired
    private AssetIntegrationService assetIntegrationService;

    @Autowired
    private ContentDtoService contentService;

    /**
     * Returns the list of all the asset holders
     *
     * @param params          - input parameters
     * @param dataCollections - data collection map
     * @return
     */
    @ReportBean("assetHoldersList")
    public List<AssetHoldersDto> getAssetHolders(Map<String, Object> params, Map<String, Object> dataCollections) {
        final String assetId = (String) params.get(ASSET_ID);
        final String priceDate = (String) params.get(PRICE_DATE);

        // For Managed Portfolios we have multiple MODEL portfolios (with same name and asset codes) associated to different OFFERS,
        // hence appear as duplicates, so pickup the first one.
        final String[] assetIds = assetId.split(",");
        final Asset asset = assetIntegrationService.loadAsset(assetIds[0], new ServiceErrorsImpl());
        if (asset != null) {
            dataCollections.put(ASSET_NAME_PARAM, asset.getAssetName());
            dataCollections.put(ASSET_CODE_PARAM, asset.getAssetCode());

            final List<ApiSearchCriteria> criteria = new ArrayList<>();
            criteria.add(new ApiSearchCriteria(ASSET_IDS, EQUALS, assetId, STRING));
            criteria.add(new ApiSearchCriteria(PRICE_DATE, EQUALS, priceDate != null ? priceDate : now().toString(), DATE));

            final List<AssetHoldersDto> assetHoldersList = assetHoldersDtoService.search(criteria, new ServiceErrorsImpl());
            if (CollectionUtils.isNotEmpty(assetHoldersList)) {
                final AssetHoldersDto assetHolder = assetHoldersList.get(0);
                dataCollections.put(ASSET_PRICE_PARAM, assetHolder.getAssetPrice());
                dataCollections.put(ASSET_PRICE_DATE_PARAM, priceDate != null ? new DateTime(priceDate) : now());
                return assetHoldersList;
            }
        }
        return new ArrayList<>();
    }

    /**
     * Returns report name
     *
     * @param params - input parameters
     * @return
     */
    @ReportBean("reportName")
    public String getReportName(Map<String, Object> params) {
        return REPORT_NAME;
    }

    /**
     * Returns disclaimer
     *
     * @param params- input parameters
     * @return
     */
    @ReportBean("disclaimer")
    public String getDisclaimer(Map<String, Object> params) {
        final ServiceErrors serviceErrors = new FailFastErrorsImpl();
        final ContentKey key = new ContentKey(DISCLAIMER_CONTENT);
        final ContentDto content = contentService.find(key, serviceErrors);
        return content != null ? content.getContent() : "";
    }
}
