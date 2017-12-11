package com.bt.nextgen.api.fundpayment.service;

import com.bt.nextgen.api.fundpayment.model.FundPaymentNoticeDto;
import com.bt.nextgen.api.fundpayment.model.FundPaymentNoticeSearchDtoKey;
import com.bt.nextgen.api.util.ApiConstants;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.ManagedFundAsset;
import com.bt.nextgen.service.integration.fundpaymentnotice.DistributionDetails;
import com.bt.nextgen.service.integration.fundpaymentnotice.FundPaymentNotice;
import com.bt.nextgen.service.integration.fundpaymentnotice.FundPaymentNoticeIntegrationService;
import com.bt.nextgen.service.integration.fundpaymentnotice.FundPaymentNoticeRequest;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FundPaymentNoticeSearchDtoServiceTest {
    @InjectMocks
    FundPaymentNoticeSearchDtoServiceImpl dtoService;

    @Mock
    FundPaymentNoticeIntegrationService fundPaymentService;

    @Mock
    AssetIntegrationService assetService;

    private ServiceErrors serviceErrors;
    private List<FundPaymentNotice> response;
    private Map<String, Asset> assetList;
    private List<ApiSearchCriteria> criteriaList = new ArrayList<>();
    private FundPaymentNoticeSearchDtoKey key;

    @Before
    public void setup() {
        response = getFundPaymentList();
        assetList = getAssetList();
        key = new FundPaymentNoticeSearchDtoKey();
        key.setEndDate(new DateTime());
        key.setStartDate(new DateTime());
    }

    @Test
    public void testFindAllSuccess() {
        Mockito.when(
                fundPaymentService.getFundPaymentNoticeDetails(Mockito.any(FundPaymentNoticeRequest.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(response);
        Mockito.when(assetService.loadAssets(Mockito.any(List.class), Mockito.any(ServiceErrors.class))).thenReturn(assetList);
        List<FundPaymentNoticeDto> result = dtoService.search(key, new ArrayList<ApiSearchCriteria>(), serviceErrors);
        assertEquals(result.size(), 6);
        assertEquals(result.get(0).getCode(), "code 1");
        assertEquals(result.get(0).getMitWhtAmount(), "51.000051");
        assertEquals(result.get(0).getDistributionList().size(), 4);
        assertEquals(result.get(0).getDistributionList().get(0).getAmount(), "1.000049");
        assertEquals(result.get(0).getDistributionList().get(1).getAmount(), "2222.000000");
        assertEquals(result.get(0).getDistributionList().get(2).getAmount(), "50.000002");
        assertEquals(result.get(0).getDistributionList().get(3).getAmount(), "3.940000");
        assertEquals(result.get(0).isAmitNotice(), false);

        assertEquals(result.get(5).getCode(), "code 6");
        assertEquals(result.get(5).getMitWhtAmount(), "51.000051");
        assertEquals(result.get(5).isAmitNotice(), true);
    }

    @Test
    public void testFilterOneSearchWordFundNameSuccess() {
        criteriaList.add(new ApiSearchCriteria(ApiConstants.FUND_NAME, ApiSearchCriteria.SearchOperation.STARTS_WITH, "cat",
                ApiSearchCriteria.OperationType.STRING));
        Mockito.when(
                fundPaymentService.getFundPaymentNoticeDetails(Mockito.any(FundPaymentNoticeRequest.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(response);
        Mockito.when(assetService.loadAssets(Mockito.any(List.class), Mockito.any(ServiceErrors.class))).thenReturn(assetList);
        List<FundPaymentNoticeDto> result = dtoService.search(key, criteriaList, serviceErrors);
        assertEquals(result.size(), 4);
        assertEquals(result.get(0).getFundName(), "cat name 1");
        assertEquals(result.get(3).getFundName(), "cat 6");
        assertEquals(result.get(0).getDistributionList().size(), 4);
    }

    @Test
    public void testFilterTwoSearchWordsFundNameSuccess() {
        criteriaList.add(new ApiSearchCriteria(ApiConstants.FUND_NAME, ApiSearchCriteria.SearchOperation.STARTS_WITH, "cat na",
                ApiSearchCriteria.OperationType.STRING));
        Mockito.when(
                fundPaymentService.getFundPaymentNoticeDetails(Mockito.any(FundPaymentNoticeRequest.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(response);
        Mockito.when(assetService.loadAssets(Mockito.any(List.class), Mockito.any(ServiceErrors.class))).thenReturn(assetList);
        List<FundPaymentNoticeDto> result = dtoService.search(key, criteriaList, serviceErrors);
        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getFundName(), "cat name 1");
        assertEquals(result.get(1).getFundName(), "cat name 3");
    }

    @Test
    public void testFilterOneSearchWordFundManagerSuccess() {
        criteriaList.add(new ApiSearchCriteria(ApiConstants.FUND_MANAGER, ApiSearchCriteria.SearchOperation.STARTS_WITH, "fun",
                ApiSearchCriteria.OperationType.STRING));
        Mockito.when(
                fundPaymentService.getFundPaymentNoticeDetails(Mockito.any(FundPaymentNoticeRequest.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(response);
        Mockito.when(assetService.loadAssets(Mockito.any(List.class), Mockito.any(ServiceErrors.class))).thenReturn(assetList);
        List<FundPaymentNoticeDto> result = dtoService.search(key, criteriaList, serviceErrors);
        assertEquals(result.size(), 6);
        assertEquals(result.get(0).getFundManager(), "fund manager1");
    }

    @Test
    public void testFilterTwoSearchWordsFundManagerSuccess() {
        criteriaList.add(new ApiSearchCriteria(ApiConstants.FUND_MANAGER, ApiSearchCriteria.SearchOperation.STARTS_WITH,
                "fun ma", ApiSearchCriteria.OperationType.STRING));
        Mockito.when(
                fundPaymentService.getFundPaymentNoticeDetails(Mockito.any(FundPaymentNoticeRequest.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(response);
        Mockito.when(assetService.loadAssets(Mockito.any(List.class), Mockito.any(ServiceErrors.class))).thenReturn(assetList);
        List<FundPaymentNoticeDto> result = dtoService.search(key, criteriaList, serviceErrors);
        assertEquals(result.size(), 6);
        assertEquals(result.get(0).getFundManager(), "fund manager1");
    }

    @Test
    public void testFindNoResultsForFundName() {
        criteriaList.add(new ApiSearchCriteria(ApiConstants.FUND_NAME, ApiSearchCriteria.SearchOperation.STARTS_WITH,
                "bird name", ApiSearchCriteria.OperationType.STRING));
        Mockito.when(
                fundPaymentService.getFundPaymentNoticeDetails(Mockito.any(FundPaymentNoticeRequest.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(response);
        Mockito.when(assetService.loadAssets(Mockito.any(List.class), Mockito.any(ServiceErrors.class))).thenReturn(assetList);
        List<FundPaymentNoticeDto> result = dtoService.search(key, criteriaList, serviceErrors);
        assertEquals(result.size(), 0);
    }

    @Test
    public void testFindNoResultsForFundManagerName() {
        criteriaList.add(new ApiSearchCriteria(ApiConstants.FUND_MANAGER, ApiSearchCriteria.SearchOperation.STARTS_WITH,
                "non name", ApiSearchCriteria.OperationType.STRING));
        Mockito.when(
                fundPaymentService.getFundPaymentNoticeDetails(Mockito.any(FundPaymentNoticeRequest.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(response);
        Mockito.when(assetService.loadAssets(Mockito.any(List.class), Mockito.any(ServiceErrors.class))).thenReturn(assetList);
        List<FundPaymentNoticeDto> result = dtoService.search(key, criteriaList, serviceErrors);
        assertEquals(result.size(), 0);
    }

    @Test
    public void testFilterOurBlankApirCode() {
        Mockito.when(
                fundPaymentService.getFundPaymentNoticeDetails(Mockito.any(FundPaymentNoticeRequest.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(response);
        Map<String, Asset> blankCodeAssetList = new HashMap<>();
        blankCodeAssetList.put("id 1", getAsset("id 1", "", "cat name 1", "fund manager1"));
        blankCodeAssetList.put("id 2", getAsset("id 2", "code 2", "cat 2", "fund manager2"));
        blankCodeAssetList.put("id 3", getAsset("id 3", "code 3", "cat name 3", "fund manager3"));
        blankCodeAssetList.put("id 4", getAsset("id 4", "code 4", "dog 4", "fund manager4"));
        blankCodeAssetList.put("id 5", getAsset("id 5", "code 5", "dog name 5", "fund manager5"));
        blankCodeAssetList.put("id 6", getAsset("id 6", "code 6", "cat 6", "fund manager6"));
        Mockito.when(assetService.loadAssets(Mockito.any(List.class), Mockito.any(ServiceErrors.class))).thenReturn(
                blankCodeAssetList);
        List<FundPaymentNoticeDto> result = dtoService.search(key, new ArrayList<ApiSearchCriteria>(), serviceErrors);
        assertEquals(result.size(), 5);
    }

    private List<FundPaymentNotice> getFundPaymentList() {
        List<FundPaymentNotice> list = new ArrayList<FundPaymentNotice>();
        list.add(getFundPaymentNotice("2013/2014", new DateTime(2014, 9, 2, 0, 0, 0, 0), BigDecimal.TEN, "id 1", "cat name 1", false));
        list.add(getFundPaymentNotice("2013/2014", new DateTime(2013, 9, 2, 0, 0, 0, 0), BigDecimal.TEN, "id 2", "cat 2", false));
        list.add(getFundPaymentNotice("2013/2014", new DateTime(2013, 9, 2, 0, 0, 0, 0), BigDecimal.TEN, "id 3", "cat name 3", false));
        list.add(getFundPaymentNotice("2013/2014", new DateTime(2012, 9, 2, 0, 0, 0, 0), BigDecimal.TEN, "id 4", "dog 4", false));
        list.add(getFundPaymentNotice("2013/2014", new DateTime(2014, 8, 2, 0, 0, 0, 0), BigDecimal.TEN, "id 5", "dog name 5", false));
        list.add(getFundPaymentNotice("2013/2014", new DateTime(2014, 7, 2, 0, 0, 0, 0), BigDecimal.TEN, "id 6", "cat 6", true));
        return list;
    }

    @Test
    public void testFindAllNullResponse() {
        Mockito.when(
                fundPaymentService.getFundPaymentNoticeDetails(Mockito.any(FundPaymentNoticeRequest.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(new ArrayList<FundPaymentNotice>());
        List<FundPaymentNoticeDto> result = dtoService.search(key, criteriaList, serviceErrors);
        assertEquals(result.size(), 0);
    }

    @Test
    public void testFindAllNullListResponse() {
        Mockito.when(
                fundPaymentService.getFundPaymentNoticeDetails(Mockito.any(FundPaymentNoticeRequest.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(null);
        List<FundPaymentNoticeDto> result = dtoService.search(key, criteriaList, serviceErrors);
        assertEquals(result.size(), 0);
    }

    private FundPaymentNotice getFundPaymentNotice(final String taxYear, final DateTime distributionDate,
                                                   final BigDecimal distributionAmt, final String assetCode,
                                                   final String assetName, Boolean isAmit) {
        final List<DistributionDetails> detailsList = new ArrayList<DistributionDetails>();
        detailsList.add(getDistributionDetails("1.000049", "TARP - Other Non-Discountable CG"));
        detailsList.add(getDistributionDetails("2222", "Foreign Interest"));
        detailsList.add(getDistributionDetails("0", "Other Income"));
        detailsList.add(getDistributionDetails("50.0000019", "TARP - Discounted CG"));
        detailsList.add(getDistributionDetails("3.94", "FITO - Other Non-Discountable CG"));

        Asset asset = mock(Asset.class);
        when(asset.getAssetId()).thenReturn(assetCode);
        when(asset.getAssetCode()).thenReturn(assetCode);
        when(asset.getAssetName()).thenReturn(assetName);

        FundPaymentNotice fundPayment = mock(FundPaymentNotice.class);
        when(fundPayment.getTaxYear()).thenReturn(taxYear);
        when(fundPayment.getAsset()).thenReturn(asset);
        when(fundPayment.getDistributions()).thenReturn(detailsList);
        when(fundPayment.getDistributionDate()).thenReturn(distributionDate);
        when(fundPayment.getDistributionAmount()).thenReturn(distributionAmt);
        when(fundPayment.isAmitNotice()).thenReturn(isAmit);

        return fundPayment;
    }

    private DistributionDetails getDistributionDetails(final String amount, final String component) {
        DistributionDetails details = new DistributionDetails() {

            @Override
            public String getDistributionComponentAmount() {
                return amount;
            }

            @Override
            public String getDistributionComponent() {
                return component;
            }
        };
        return details;
    }

    public Map<String, Asset> getAssetList() {
        Map<String, Asset> assets = new HashMap<>();
        assets.put("id 1", getAsset("id 1", "code 1", "cat name 1", "fund manager1"));
        assets.put("id 2", getAsset("id 2", "code 2", "cat 2", "fund manager2"));
        assets.put("id 3", getAsset("id 3", "code 3", "cat name 3", "fund manager3"));
        assets.put("id 4", getAsset("id 4", "code 4", "dog 4", "fund manager4"));
        assets.put("id 5", getAsset("id 5", "code 5", "dog name 5", "fund manager5"));
        assets.put("id 6", getAsset("id 6", "code 6", "cat 6", "fund manager6"));

        return assets;
    }

    private Asset getAsset(final String id, final String code, final String name, final String fundManager) {
        ManagedFundAsset asset = Mockito.mock(ManagedFundAsset.class);
        Mockito.when(asset.getAssetId()).thenReturn(id);
        Mockito.when(asset.getAssetCode()).thenReturn(code);
        Mockito.when(asset.getAssetName()).thenReturn(name);
        Mockito.when(asset.getFundManager()).thenReturn(fundManager);
        return asset;
    }
}
