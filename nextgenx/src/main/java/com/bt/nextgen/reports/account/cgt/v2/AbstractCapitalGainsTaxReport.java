
package com.bt.nextgen.reports.account.cgt.v2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.bt.nextgen.api.cgt.model.CgtGroupDto;
import com.bt.nextgen.api.cgt.model.CgtSecurity;
import com.bt.nextgen.reports.account.common.AccountReportV2;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

public abstract class AbstractCapitalGainsTaxReport extends AccountReportV2 {

    public static final String SORT_PARAMETER = "order-by";

    public static final String SORTING_KEY_ASSETNAME = "assetName";
    public static final String SORTING_KEY_GROSSGAIN = "grossGain";
    public static final String SORT_ASC = "asc";
    public static final String SORT_DESC = "desc";

    public AbstractCapitalGainsTaxReport() {
        super();
    }

    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {

        Pair<String, Boolean> sortOrder = parseOrderBy(params.get(SORT_PARAMETER).toString());

        List<CgtGroupDto> groups = getCgtData(params);
        List<AssetTypeCgtGroupData> result = convertData(groups, sortOrder);
        Collections.sort(result, new Comparator<AssetTypeCgtGroupData>() {

            @Override
            public int compare(AssetTypeCgtGroupData o1, AssetTypeCgtGroupData o2) {
                return o1.getAssetType().getSortOrder() - o2.getAssetType().getSortOrder();
            }

        });

        BigDecimal amount = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;
        BigDecimal grossGain = BigDecimal.ZERO;
        BigDecimal grossCostBase = BigDecimal.ZERO;
        BigDecimal calculatedGrossGain = null;
        BigDecimal calculatedCostBase = null;

        for (AssetTypeCgtGroupData group : result) {
            amount = amount.add(group.getCalculatedAmount());
            taxAmount = taxAmount.add(group.getCalculatedTaxAmount());
            calculatedGrossGain = group.getCalculatedGrossGain();
            calculatedCostBase = group.getCalculatedCostBase();
            if (calculatedGrossGain != null) {
                grossGain = grossGain.add(calculatedGrossGain);
            }
            grossCostBase = grossCostBase.add(calculatedCostBase);
        }
        String reportTitle = getReportTitle();
        CgtGroupReportData cgtGroupReportData = new CgtGroupReportData(result, amount, taxAmount, grossGain, grossCostBase,
                reportTitle);

        return Collections.singletonList(cgtGroupReportData);
    }

    private Pair<String, Boolean> parseOrderBy(String orderBy) {
        boolean ascending = true;
        String orderingProperty = SORTING_KEY_ASSETNAME;

        if (!StringUtils.isBlank(orderBy)) {
            String[] splitOrderingEntry = orderBy.split(",");

            if (splitOrderingEntry.length > 1) {
                orderingProperty = splitOrderingEntry[0];
                if (SORT_DESC.equals(splitOrderingEntry[1])) {
                    ascending = false;
                }
            }
        }

        return Pair.of(orderingProperty, ascending);
    }

    protected abstract String getReportTitle();

    protected abstract List<CgtGroupDto> getCgtData(Map<String, Object> params);

    protected List<AssetTypeCgtGroupData> convertData(List<CgtGroupDto> groups, Pair<String, Boolean> sortOrder) {
        List<AssetTypeCgtGroupData> result = new ArrayList<>();

        Map<String, List<CgtGroupDto>> compositeGroups = new HashMap<>();
        Map<String, List<CgtGroupDto>> simpleGroups = new HashMap<>();

        for (CgtGroupDto group : groups) {
            String groupId = group.getGroupId();

            if (NumberUtils.isDigits(groupId)) {
                String groupType = group.getGroupType();
                List<CgtGroupDto> groupList = compositeGroups.get(groupType);
                if (groupList == null) {
                    groupList = new ArrayList<>();
                    compositeGroups.put(groupType, groupList);
                }
                groupList.add(group);

            } else {
                if (isShareEquivalent(groupId)) {
                    groupId = AssetType.SHARE.name();
                }
                List<CgtGroupDto> groupList = simpleGroups.get(groupId);
                if (groupList == null) {
                    groupList = new ArrayList<>();
                    simpleGroups.put(groupId, groupList);
                }
                groupList.add(group);
            }
        }
        if (!compositeGroups.isEmpty()) {
            for (List<CgtGroupDto> compositeGroupList : compositeGroups.values()) {
                result.add(buildCompositeAssetTypeGroup(compositeGroupList, sortOrder));
            }
        }
        if (!simpleGroups.isEmpty()) {
            for (List<CgtGroupDto> simpleGroupList : simpleGroups.values()) {
                result.add(buildSimpleAssetTypeGroup(simpleGroupList, sortOrder));
            }
        }

        return result;
    }

    private boolean isShareEquivalent(String groupId) {
        return AssetType.valueOf(groupId) == AssetType.OPTION || AssetType.valueOf(groupId) == AssetType.BOND;
    }

    private AssetTypeCgtGroupData buildCompositeAssetTypeGroup(List<CgtGroupDto> groups, final Pair<String, Boolean> sortOrder) {

        AssetType assetType = AssetType.valueOf(groups.iterator().next().getGroupType());
        List<AssetCgtGroupData> assetGroups = new ArrayList<>();

        for (CgtGroupDto group : groups) {
            Map<String, List<CgtSecurityData>> assetSecurityMap = buildAssetSecurityMap(group);
            assetGroups.add(buildAssetGroup(group.getGroupCode(), group.getGroupName(), assetSecurityMap,
                    Pair.of(SORTING_KEY_ASSETNAME, true)));
        }

        if (assetGroups.size() > 1) {
            Collections.sort(assetGroups, new Comparator<AssetCgtGroupData>() {

                @Override
                public int compare(AssetCgtGroupData o1, AssetCgtGroupData o2) {

                    if (sortOrder.getKey().equals(SORTING_KEY_GROSSGAIN)) {
                        return o1.getCalculatedGrossGain().compareTo(o2.getCalculatedGrossGain());
                    }
                    return o1.getAssetName().compareToIgnoreCase(o2.getAssetName());
                }
            });

            if (!sortOrder.getValue()) {
                Collections.reverse(assetGroups);
            }
        }

        return new AssetTypeCgtGroupData(assetType, assetGroups);
    }

    private AssetTypeCgtGroupData buildSimpleAssetTypeGroup(List<CgtGroupDto> groups, Pair<String, Boolean> sortOrder) {
        AssetType assetType = AssetType.valueOf(groups.iterator().next().getGroupId());
        if (assetType == AssetType.OPTION || assetType == AssetType.BOND) {
            assetType = AssetType.SHARE;
        }
        Map<String, List<CgtSecurityData>> assetSecurityMap = new HashMap<>();
        for (CgtGroupDto group : groups) {
            assetSecurityMap.putAll(buildAssetSecurityMap(group));
        }
        return new AssetTypeCgtGroupData(assetType, Collections.singletonList(buildAssetGroup(null, null, assetSecurityMap,
                sortOrder)));
    }

    private AssetCgtGroupData buildAssetGroup(String assetCode, String assetName,
            Map<String, List<CgtSecurityData>> assetSecurityMap, final Pair<String, Boolean> sortOrder) {
        List<AssetParcelsData> assetParcelsList = new ArrayList<>();
        for (List<CgtSecurityData> assetSecurities : assetSecurityMap.values()) {

            if (assetSecurities.size() > 1) {
                // Secondary parcel sorting
                Collections.sort(assetSecurities, secondarySortDateComparator);
            }

            assetParcelsList.add(new AssetParcelsData(assetSecurities.get(0).getSecurityCode(), assetSecurities.get(0)
                    .getSecurityName(), assetSecurities));
        }

        if (assetParcelsList.size() > 1) {
            Collections.sort(assetParcelsList, new Comparator<AssetParcelsData>() {

                @Override
                public int compare(AssetParcelsData o1, AssetParcelsData o2) {

                    if (sortOrder.getKey().equals(SORTING_KEY_GROSSGAIN)) {
                        return o1.getCalculatedGrossGain().compareTo(o2.getCalculatedGrossGain());
                    }
                    return o1.getAssetName().compareToIgnoreCase(o2.getAssetName());
                }
            });

            if (!sortOrder.getValue()) {
                Collections.reverse(assetParcelsList);
            }
        }

        return new AssetCgtGroupData(assetCode, assetName, assetParcelsList);
    }

    private Map<String, List<CgtSecurityData>> buildAssetSecurityMap(CgtGroupDto group) {

        Map<String, List<CgtSecurityData>> assetSecurityMap = new HashMap<>();
        for (CgtSecurity security : group.getCgtSecurities()) {
            List<CgtSecurityData> secList = assetSecurityMap.get(security.getSecurityCode());
            if (secList == null) {
                secList = new ArrayList<>();
                assetSecurityMap.put(security.getSecurityCode(), secList);
            }
            secList.add(new CgtSecurityData(security));
        }
        return assetSecurityMap;
    }

    private static Comparator<CgtSecurityData> secondarySortDateComparator = new Comparator<CgtSecurityData>() {

        // Sort by descending sale date, then ascending tax date
        @Override
        public int compare(CgtSecurityData o1, CgtSecurityData o2) {
            return ComparisonChain.start()
                    .compare(o2.getCgtSecurity().getDate(), o1.getCgtSecurity().getDate(), Ordering.natural().nullsLast())
                    .compare(o1.getCgtSecurity().getTaxDate(), o2.getCgtSecurity().getTaxDate()).result();
        }
    };

}