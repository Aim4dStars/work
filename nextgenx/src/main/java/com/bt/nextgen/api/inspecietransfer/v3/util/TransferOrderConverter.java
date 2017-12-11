package com.bt.nextgen.api.inspecietransfer.v3.util;

import com.bt.nextgen.api.inspecietransfer.v3.model.TransferDest;
import com.bt.nextgen.api.inspecietransfer.v3.model.TransferItemDto;
import com.bt.nextgen.api.inspecietransfer.v3.model.TransferOrderDto;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.transfer.TransferItem;
import com.bt.nextgen.service.integration.transfer.TransferOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class TransferOrderConverter {

    private TransferOrderConverter() {
        // hide public constructor
    }

    public static List<TransferOrderDto> toDtoList(List<TransferOrder> transferOrders, Map<String, Asset> contMap,
            Map<String, Asset> assetMap) {
        List<TransferOrderDto> dtos = new ArrayList<>();
        for (TransferOrder transferOrder : transferOrders) {
            if (transferOrder != null) {
                List<TransferItemDto> transferItems = toTransferItemDtoList(transferOrder.getTransferItems(), assetMap);
                if (!transferItems.isEmpty()) {
                    dtos.add(new TransferOrderDto(transferOrder, getTransferStatus(transferOrder), getContainerDetails(
                            transferOrder, contMap), SponsorDetailsConverter.toDto(transferOrder.getSponsorDetails(),
                            transferOrder.getTransferType()), transferItems));
                }
            }
        }
        return dtos;
    }

    private static TransferDest getContainerDetails(TransferOrder transferOrder, Map<String, Asset> contMap) {
        Asset asset = contMap.get(transferOrder.getDestContainerId());
        if (asset != null) {
            TransferDest dest = new TransferDest(transferOrder.getDestContainerId(), asset.getAssetId(), asset.getAssetName(),
                    asset.getAssetType().name(), asset.getAssetCode());
            return dest;
        }
        return new TransferDest(transferOrder.getDestContainerId(), null, null, null, null);
    }

    private static String getTransferStatus(TransferOrder transferOrder) {
        // Take order status from parent if available, otherwise take it from the first child order
        String transferStatus = null;
        if (transferOrder.getStatus() != null) {
            transferStatus = transferOrder.getStatus().name();
        } else if (transferOrder.getTransferItems() != null && !transferOrder.getTransferItems().isEmpty()) {
            transferStatus = transferOrder.getTransferItems().get(0).getTransferStatus().name();
        }
        return transferStatus;
    }

    private static List<TransferItemDto> toTransferItemDtoList(List<TransferItem> transferItems, Map<String, Asset> assetMap) {
        List<TransferItemDto> dtos = new ArrayList<>();
        if (transferItems != null) {
            for (TransferItem item : transferItems) {
                String assetId = item.getAssetId();
                if (assetId != null) {
                    String status = item.getTransferStatus() == null ? null : item.getTransferStatus().name();
                    TransferItemDto dto = new TransferItemDto(assetMap.get(assetId), item.getQuantity(), status,
                            item.getTransactionDateTime());
                    dtos.add(dto);
                }
            }
        }
        return dtos;
    }
}
