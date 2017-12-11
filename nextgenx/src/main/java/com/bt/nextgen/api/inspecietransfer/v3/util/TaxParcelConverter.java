package com.bt.nextgen.api.inspecietransfer.v3.util;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferDto;
import com.bt.nextgen.api.inspecietransfer.v3.model.SponsorDetailsDto;
import com.bt.nextgen.api.inspecietransfer.v3.model.TaxParcelDto;
import com.bt.nextgen.api.inspecietransfer.v3.model.TransferAssetDto;
import com.bt.nextgen.api.inspecietransfer.v3.util.vetting.TaxParcelDetailedRow;
import com.bt.nextgen.api.inspecietransfer.v3.util.vetting.TaxParcelRow;
import com.bt.nextgen.service.avaloq.transfer.TaxParcelImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.transfer.TaxParcel;
import com.bt.nextgen.service.integration.transfer.TransferType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TaxParcelConverter {

    private static final Logger logger = LoggerFactory.getLogger(TaxParcelConverter.class);

    private TaxParcelConverter() {
        // hide public constructor
    }

    public static List<TaxParcel> fromDtoList(List<TaxParcelDto> taxParcelDtos) {
        List<TaxParcel> taxParcels = new ArrayList<>();

        for (TaxParcelDto taxParcelDto : taxParcelDtos) {
            TaxParcelImpl taxParcel = new TaxParcelImpl(taxParcelDto.getAssetId(), taxParcelDto.getTaxRelevanceDate(),
                    taxParcelDto.getTaxVisibilityDate(), taxParcelDto.getQuantity(), taxParcelDto.getCostBase(),
                    taxParcelDto.getReducedCostBase(), taxParcelDto.getIndexedCostBase());
            taxParcel.setOriginalCostBase(taxParcelDto.getOriginalCostBase());
            taxParcels.add(taxParcel);
        }
        return taxParcels;
    }

    public static List<TaxParcelDto> toDtoList(List<TaxParcel> taxParcels, AssetDto assetDto) {
        List<TaxParcelDto> taxParcelDtos = new ArrayList<>();
        if (taxParcels != null) {
            String assetCode = assetDto == null ? null : assetDto.getAssetCode();

            for (TaxParcel taxParcel : taxParcels) {
                taxParcelDtos.add(new TaxParcelDto(taxParcel, assetCode));
            }
        }
        return taxParcelDtos;
    }

    public static List<TransferAssetDto> constructTransferAssetFromTaxParcels(List<TaxParcelRow> parcelRows, String pid,
            InspecieTransferDto transferDto) {

        Map<String, TransferAssetDto> assetDtoMap = new HashMap<>();
        for (TaxParcelRow row : parcelRows) {
            SponsorDetailsDto sponsorDto = null;
            TransferType transferType = TransferType.forDisplay(transferDto.getTransferType());
            switch (transferType) {
                case LS_BROKER_SPONSORED:
                    sponsorDto = new SponsorDetailsDto(pid, null, row.getOwner());
                    break;
                case LS_OTHER:
                    sponsorDto = new SponsorDetailsDto(pid, null, row.getOwner(), row.getCustodian());
                    break;
                case LS_ISSUER_SPONSORED:
                    sponsorDto = new SponsorDetailsDto(row.getOwner());
                    break;
                case OTHER_PLATFORM:
                    sponsorDto = getOtherPlatformSponsorDetails(pid, row);
                    break;
                case MANAGED_FUND:
                    sponsorDto = new SponsorDetailsDto(row.getCustodian(), row.getOwner());
                    break;
                default:
                    break;
            }
            TaxParcelImpl tax = new TaxParcelImpl(convert(row.getQuantity()));
            if (row instanceof TaxParcelDetailedRow) {
                // In the case of NCBO (i.e. tax details have been provided)
                TaxParcelDetailedRow detailRow = (TaxParcelDetailedRow) row;
                tax = new TaxParcelImpl(null, detailRow.getAcquisitionDate(), null, convert(row.getQuantity()),
                        convert(detailRow.getCostBase()), convert(detailRow.getReducedCostBase()),
                        convert(detailRow.getIndexedCostBase()));
                tax.setOriginalCostBase(convert(detailRow.getOriginalCostBase()));
            }
            TaxParcelDto taxDto = new TaxParcelDto(tax, row.getAssetCode());

            // Aggregate tax details, group by asset-code.
            StringBuilder builder = new StringBuilder();
            builder.append(row.getAssetCode());
            builder.append("_");
            builder.append(sponsorDto.getKey());

            TransferAssetDto xferAssetDto = assetDtoMap.get(builder.toString());
            if (xferAssetDto != null) {
                xferAssetDto.getTaxParcels().add(taxDto);
                // Triggers a calculation of the overall quantity
                xferAssetDto.updateQuantity();
            } else {
                List<TaxParcelDto> taxDtoList = new ArrayList<>();
                taxDtoList.add(taxDto);
                AssetDto assetDto = new AssetDto(row.getAsset(), row.getAsset().getAssetName(), row.getAsset().getAssetType()
                        .name());
                assetDtoMap.put(builder.toString(), new TransferAssetDto(convert(row.getQuantity()), assetDto, sponsorDto,
                        taxDtoList));
            }
        }

        return new ArrayList<>(assetDtoMap.values());
    }

    private static SponsorDetailsDto getOtherPlatformSponsorDetails(String pid, TaxParcelRow row) {
        if (row != null && row.getAsset() != null) {
            if (AssetType.MANAGED_FUND.equals(row.getAsset().getAssetType())) {
                return new SponsorDetailsDto(row.getCustodian(), row.getOwner());
            } else {
                return new SponsorDetailsDto(pid, null, row.getOwner(), row.getCustodian());
            }
        }
        return null;
    }

    private static BigDecimal convert(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        try {
            return new BigDecimal(value.replaceAll(", ", "").trim());
        } catch (NumberFormatException err) {
            logger.error("Exception when converting tax parcel quantity", err);
        }
        return null;
    }
}
