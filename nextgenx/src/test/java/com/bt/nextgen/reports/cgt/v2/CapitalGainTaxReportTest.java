package com.bt.nextgen.reports.cgt.v2;

import static com.bt.nextgen.core.api.UriMappingConstants.ACCOUNT_ID_URI_MAPPING;
import static com.bt.nextgen.core.api.UriMappingConstants.EFFECTIVE_DATE_PARAMETER_MAPPING;
import static com.bt.nextgen.core.api.UriMappingConstants.END_DATE_PARAMETER_MAPPING;
import static com.bt.nextgen.core.api.UriMappingConstants.START_DATE_PARAMETER_MAPPING;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.bt.nextgen.api.cgt.model.CgtDto;
import com.bt.nextgen.api.cgt.model.CgtGroupDto;
import com.bt.nextgen.api.cgt.model.CgtKey;
import com.bt.nextgen.api.cgt.model.CgtSecurity;
import com.bt.nextgen.api.cgt.model.CgtSecurityDto;
import com.bt.nextgen.api.cgt.service.RealisedCgtDtoService;
import com.bt.nextgen.api.cgt.service.UnrealisedCgtDtoService;
import com.bt.nextgen.reports.account.cgt.v2.AssetCgtGroupData;
import com.bt.nextgen.reports.account.cgt.v2.AssetParcelsData;
import com.bt.nextgen.reports.account.cgt.v2.AssetTypeCgtGroupData;
import com.bt.nextgen.reports.account.cgt.v2.CgtGroupReportData;
import com.bt.nextgen.reports.account.cgt.v2.CgtSecurityData;
import com.bt.nextgen.reports.account.cgt.v2.RealisedCapitalGainsTaxReport;
import com.bt.nextgen.reports.account.cgt.v2.UnrealisedCapitalGainsTaxReport;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.integration.asset.AssetType;

@RunWith(MockitoJUnitRunner.class)
public class CapitalGainTaxReportTest {
    @InjectMocks
    private RealisedCapitalGainsTaxReport realisedCgtReport;

    @InjectMocks
    private UnrealisedCapitalGainsTaxReport unrealisedCgtReport;

    @Mock
    private RealisedCgtDtoService realisedCgtDtoService;

    @Mock
    private UnrealisedCgtDtoService unrealisedCgtDtoService;

    public static final String SORT_PARAMETER = "order-by";

    @Test
    public void whenRealisedReportCalled_thenRealisedServiceInvoked() {
        String accountId = "accountId";
        String startDateStr = "2015-01-01";
        String endDateStr = "2016-01-01";
        String orderBy = "assetName,asc";

        final Map<String, Object> params = new HashMap<>();
        params.put(ACCOUNT_ID_URI_MAPPING, accountId);
        params.put(START_DATE_PARAMETER_MAPPING, startDateStr);
        params.put(END_DATE_PARAMETER_MAPPING, endDateStr);
        params.put(SORT_PARAMETER, orderBy);

        final CgtKey key = new CgtKey(accountId, new DateTime(startDateStr), new DateTime(endDateStr), "ASSET_TYPE");
        final CgtGroupDto simpleGroup = createSimpleCgtDto();
        final CgtDto dto = new CgtDto(key, Collections.singletonList(simpleGroup));

        Mockito.when(realisedCgtDtoService.find(Mockito.any(CgtKey.class), Mockito.any(ServiceErrors.class))).then(
                new Answer<CgtDto>() {
                    @Override
                    public CgtDto answer(InvocationOnMock invocation) throws Throwable {
                        Assert.assertEquals(key, invocation.getArguments()[0]);
                        return dto;
                    }
                });

        final Map<String, Object> dataCollections = new HashMap<>();

        realisedCgtReport.getData(params, dataCollections);
    }

    @Test
    public void whenUnrealisedReportCalled_thenUnrealisedServiceInvoked() {
        String accountId = "accountId";
        String effectiveDateStr = "2015-01-01";
        String orderBy = "assetName,asc";

        final Map<String, Object> params = new HashMap<>();
        params.put(ACCOUNT_ID_URI_MAPPING, accountId);
        params.put(EFFECTIVE_DATE_PARAMETER_MAPPING, effectiveDateStr);
        params.put(SORT_PARAMETER, orderBy);

        final CgtKey key = new CgtKey(accountId, new DateTime(effectiveDateStr), new DateTime(effectiveDateStr), "ASSET_TYPE");

        final CgtGroupDto simpleGroup = createSimpleCgtDto();
        final CgtDto dto = new CgtDto(key, Collections.singletonList(simpleGroup));

        Mockito.when(unrealisedCgtDtoService.find(Mockito.any(CgtKey.class), Mockito.any(ServiceErrors.class))).then(
                new Answer<CgtDto>() {
                    @Override
                    public CgtDto answer(InvocationOnMock invocation) throws Throwable {
                        Assert.assertEquals(key, invocation.getArguments()[0]);
                        return dto;
                    }
                });

        final Map<String, Object> dataCollections = new HashMap<>();

        unrealisedCgtReport.getData(params, dataCollections);
    }

    @Test
    public void whenSimpleCgtParcelData_thenParcelConvertedCorrectly() {
        String accountId = "accountId";
        String effectiveDateStr = "2015-01-01";
        String orderBy = "assetName,asc";

        final Map<String, Object> params = new HashMap<>();
        params.put(ACCOUNT_ID_URI_MAPPING, accountId);
        params.put(EFFECTIVE_DATE_PARAMETER_MAPPING, effectiveDateStr);
        params.put(SORT_PARAMETER, orderBy);

        final CgtKey key = new CgtKey(accountId, new DateTime(effectiveDateStr), new DateTime(effectiveDateStr), "ASSET_TYPE");

        final CgtGroupDto simpleGroup = createSimpleCgtDto();
        final CgtDto dto = new CgtDto(key, Collections.singletonList(simpleGroup));

        Mockito.when(unrealisedCgtDtoService.find(Mockito.any(CgtKey.class), Mockito.any(ServiceErrors.class))).then(
                new Answer<CgtDto>() {
                    @Override
                    public CgtDto answer(InvocationOnMock invocation) throws Throwable {
                        Assert.assertEquals(key, invocation.getArguments()[0]);
                        return dto;
                    }
                });

        final Map<String, Object> dataCollections = new HashMap<>();

        Collection<CgtGroupReportData> converted = (Collection<CgtGroupReportData>) unrealisedCgtReport.getData(params,
                dataCollections);

        CgtGroupReportData cgtGroupData = converted.iterator().next();
        Assert.assertEquals("$0.00", cgtGroupData.getTotalCostBase());

        Collection<AssetTypeCgtGroupData> assetTypeCgtGroupData = cgtGroupData.getCgtAssetGroups();

        Assert.assertEquals(1, assetTypeCgtGroupData.size());
        AssetTypeCgtGroupData group = assetTypeCgtGroupData.iterator().next();
        Assert.assertEquals("$1.00", group.getAmount());
        Assert.assertEquals(AssetType.SHARE, group.getAssetType());
        Assert.assertEquals("Listed securities", group.getGroupDescription());
        Assert.assertEquals("$3.00", group.getTaxAmount());
        Assert.assertEquals("$4.00", group.getGrossGain());
        Assert.assertEquals("$0.00", group.getCostBase());
        List<AssetCgtGroupData> groups = group.getAssetGroups();
        Assert.assertEquals(1, groups.size());
        AssetCgtGroupData assetGroup = groups.iterator().next();
        Assert.assertEquals("$1.00", assetGroup.getAmount());
        Assert.assertEquals(null, assetGroup.getAssetName());
        Assert.assertEquals(null, assetGroup.getAssetCode());
        Assert.assertEquals("$3.00", assetGroup.getTaxAmount());
        Assert.assertEquals("$4.00", assetGroup.getGrossGain());
        Assert.assertEquals("$0.00", assetGroup.getCostBase());

        List<AssetParcelsData> parcels = assetGroup.getAssetParcels();
        Assert.assertEquals(1, parcels.size());
        AssetParcelsData parcel = parcels.iterator().next();

        Assert.assertEquals("$1.00", parcel.getAmount());
        Assert.assertEquals("LSAssetName", parcel.getAssetName());
        Assert.assertEquals("LSAssetCode", parcel.getAssetCode());
        Assert.assertEquals("$3.00", parcel.getTaxAmount());
        Assert.assertEquals("$0.00", parcel.getCostBase());
        Assert.assertEquals("$7.00", parcel.getReducedCostBase());
        Assert.assertEquals("$6.00", parcel.getIndexedCostBase());
        Assert.assertEquals("2", parcel.getQuantity());
        Assert.assertEquals("$4.00", parcel.getGrossGain());
        List<CgtSecurityData> parcelsData = parcel.getParcels();
        CgtSecurityData parcelData = parcelsData.iterator().next();

        Assert.assertEquals("$3.00", parcelData.getTaxAmount());
        Assert.assertEquals("$4.00", parcelData.getGrossGain());
        Assert.assertEquals("-", parcelData.getCostBase());
        Assert.assertEquals("$6.00", parcelData.getIndexedCostBase());
        Assert.assertEquals("$8.00", parcelData.getCostBaseGain());
        Assert.assertEquals("$7.00", parcelData.getReducedCostBase());
        Assert.assertEquals("2", parcelData.getQuantity());
        Assert.assertEquals("02 Jul 2016", parcelData.getDate());
        Assert.assertEquals("02 Jul 2016", parcelData.getDate());
        Assert.assertEquals("2", parcelData.getDaysHeld());
    }

    @Test
    public void whenSimpleMPCgtParcelData_thenParcelConvertedCorrectly() {
        String accountId = "accountId";
        String effectiveDateStr = "2015-01-01";
        String orderBy = "assetName,asc";

        final Map<String, Object> params = new HashMap<>();
        params.put(ACCOUNT_ID_URI_MAPPING, accountId);
        params.put(EFFECTIVE_DATE_PARAMETER_MAPPING, effectiveDateStr);
        params.put(SORT_PARAMETER, orderBy);

        final CgtKey key = new CgtKey(accountId, new DateTime(effectiveDateStr), new DateTime(effectiveDateStr), "ASSET_TYPE");

        final CgtGroupDto simpleGroup = createSimpleMPCgtDto();
        final CgtDto dto = new CgtDto(key, Collections.singletonList(simpleGroup));

        Mockito.when(unrealisedCgtDtoService.find(Mockito.any(CgtKey.class), Mockito.any(ServiceErrors.class))).then(
                new Answer<CgtDto>() {
                    @Override
                    public CgtDto answer(InvocationOnMock invocation) throws Throwable {
                        Assert.assertEquals(key, invocation.getArguments()[0]);
                        return dto;
                    }
                });

        final Map<String, Object> dataCollections = new HashMap<>();

        Collection<CgtGroupReportData> converted = (Collection<CgtGroupReportData>) unrealisedCgtReport.getData(params,
                dataCollections);

        CgtGroupReportData cgtGroupData = converted.iterator().next();
        Assert.assertEquals("$5.00", cgtGroupData.getTotalCostBase());
        Collection<AssetTypeCgtGroupData> assetTypeCgtGroupData = cgtGroupData.getCgtAssetGroups();

        Assert.assertEquals(1, assetTypeCgtGroupData.size());
        AssetTypeCgtGroupData group = assetTypeCgtGroupData.iterator().next();
        Assert.assertEquals("$5.00", group.getCostBase());
    }

    @Test
    public void whenSimpleTMPCgtParcelData_thenParcelConvertedCorrectly() {
        String accountId = "accountId";
        String effectiveDateStr = "2015-01-01";
        String orderBy = "assetName,asc";

        final Map<String, Object> params = new HashMap<>();
        params.put(ACCOUNT_ID_URI_MAPPING, accountId);
        params.put(EFFECTIVE_DATE_PARAMETER_MAPPING, effectiveDateStr);
        params.put(SORT_PARAMETER, orderBy);

        final CgtKey key = new CgtKey(accountId, new DateTime(effectiveDateStr), new DateTime(effectiveDateStr), "ASSET_TYPE");

        final CgtGroupDto simpleGroup = createSimpleTMPCgtDto();
        final CgtDto dto = new CgtDto(key, Collections.singletonList(simpleGroup));

        Mockito.when(unrealisedCgtDtoService.find(Mockito.any(CgtKey.class), Mockito.any(ServiceErrors.class))).then(
                new Answer<CgtDto>() {
                    @Override
                    public CgtDto answer(InvocationOnMock invocation) throws Throwable {
                        Assert.assertEquals(key, invocation.getArguments()[0]);
                        return dto;
                    }
                });

        final Map<String, Object> dataCollections = new HashMap<>();

        Collection<CgtGroupReportData> converted = (Collection<CgtGroupReportData>) unrealisedCgtReport.getData(params,
                dataCollections);

        CgtGroupReportData cgtGroupData = converted.iterator().next();
        Assert.assertEquals("$5.00", cgtGroupData.getTotalCostBase());
        Collection<AssetTypeCgtGroupData> assetTypeCgtGroupData = cgtGroupData.getCgtAssetGroups();

        Assert.assertEquals(1, assetTypeCgtGroupData.size());
        AssetTypeCgtGroupData group = assetTypeCgtGroupData.iterator().next();
        Assert.assertEquals("$5.00", group.getCostBase());
    }

    private CgtGroupDto createSimpleCgtDto() {
        CgtSecurityDto parcel = new CgtSecurityDto("LSAssetCode", "LSAssetName", AssetType.SHARE.name());
        parcel.setAmount(BigDecimal.valueOf(1));
        parcel.setQuantity(2);
        parcel.setTaxAmount(BigDecimal.valueOf(3));
        parcel.setGrossGain(BigDecimal.valueOf(4));
        parcel.setCostBase(null);
        parcel.setIndexedCostBase(BigDecimal.valueOf(6));
        parcel.setReducedCostBase(BigDecimal.valueOf(7));
        parcel.setCostBaseGain(BigDecimal.valueOf(8));
        parcel.setDaysHeld(2);
        parcel.setDate(new DateTime("2016-07-02"));
        parcel.setTaxDate(new DateTime("2016-07-03"));

        List<CgtSecurity> parcels = new ArrayList<CgtSecurity>();
        parcels.add(parcel);

        CgtGroupDto ls = new CgtGroupDto(AssetType.SHARE.name(), "LSAssetName", "LSAssetCode",
 AssetType.SHARE.name(),
                BigDecimal.valueOf(1), 2, BigDecimal.valueOf(3), BigDecimal.valueOf(4), BigDecimal.valueOf(5),
                BigDecimal.valueOf(6), BigDecimal.valueOf(7), parcels, BigDecimal.valueOf(8));
        return ls;
    }

    private CgtGroupDto createSimpleMPCgtDto() {
        CgtSecurityDto parcel = new CgtSecurityDto("LSAssetCode", "LSAssetName", AssetType.SHARE.name());
        parcel.setAmount(BigDecimal.valueOf(1));
        parcel.setQuantity(2);
        parcel.setTaxAmount(BigDecimal.valueOf(3));
        parcel.setGrossGain(BigDecimal.valueOf(4));
        parcel.setCostBase(BigDecimal.valueOf(5));
        parcel.setIndexedCostBase(BigDecimal.valueOf(6));
        parcel.setReducedCostBase(BigDecimal.valueOf(7));
        parcel.setCostBaseGain(BigDecimal.valueOf(8));
        parcel.setDaysHeld(2);
        parcel.setDate(new DateTime("2016-07-02"));
        parcel.setTaxDate(new DateTime("2016-07-03"));

        List<CgtSecurity> parcels = new ArrayList<CgtSecurity>();
        parcels.add(parcel);

        CgtGroupDto ls = new CgtGroupDto(AssetType.MANAGED_PORTFOLIO.name(), "LSAssetName", "LSAssetCode",
                AssetType.MANAGED_PORTFOLIO.name(),
                BigDecimal.valueOf(1), 2, BigDecimal.valueOf(3), BigDecimal.valueOf(4), BigDecimal.valueOf(5),
                BigDecimal.valueOf(6), BigDecimal.valueOf(7), parcels, BigDecimal.valueOf(8));
        return ls;
    }

    private CgtGroupDto createSimpleTMPCgtDto() {
        CgtSecurityDto parcel = new CgtSecurityDto("LSAssetCode", "LSAssetName", AssetType.SHARE.name());
        parcel.setAmount(BigDecimal.valueOf(1));
        parcel.setQuantity(2);
        parcel.setTaxAmount(BigDecimal.valueOf(3));
        parcel.setGrossGain(BigDecimal.valueOf(4));
        parcel.setCostBase(BigDecimal.valueOf(5));
        parcel.setIndexedCostBase(BigDecimal.valueOf(6));
        parcel.setReducedCostBase(BigDecimal.valueOf(7));
        parcel.setCostBaseGain(BigDecimal.valueOf(8));
        parcel.setDaysHeld(2);
        parcel.setDate(new DateTime("2016-07-02"));
        parcel.setTaxDate(new DateTime("2016-07-03"));

        List<CgtSecurity> parcels = new ArrayList<CgtSecurity>();
        parcels.add(parcel);

        CgtGroupDto ls = new CgtGroupDto(AssetType.TAILORED_PORTFOLIO.name(), "LSAssetName", "LSAssetCode",
                AssetType.TAILORED_PORTFOLIO.name(),
                BigDecimal.valueOf(1), 2, BigDecimal.valueOf(3), BigDecimal.valueOf(4), BigDecimal.valueOf(5),
                BigDecimal.valueOf(6), BigDecimal.valueOf(7), parcels, BigDecimal.valueOf(8));
        return ls;
    }

}