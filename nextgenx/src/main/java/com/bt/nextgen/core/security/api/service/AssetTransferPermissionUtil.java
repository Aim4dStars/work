package com.bt.nextgen.core.security.api.service;

import com.bt.nextgen.core.security.api.model.PermissionsDto;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;

public class AssetTransferPermissionUtil {

    private AssetTransferPermissionUtil() {
        // hide public constructor
    }

    public static void retrieveAssetTransferPermission(PermissionsDto permissionsDto, WrapAccountDetail account) {
        boolean canSubmit = permissionsDto.hasPermission("account.inspecie.transfer.submit");
        boolean canView = permissionsDto.hasPermission("account.order.view");
        boolean productAllowsInternalTransfer = permissionsDto.hasPermission("option.assettransfer.internal");
        boolean productAllowsExternalTransfer = permissionsDto.hasPermission("option.assettransfer.external");

        boolean displayTransferStatus = false;
        boolean displayInspecieTransfer = false;
        boolean displayIntraAccountTransfer = false;
        boolean displayInternalAssetTransferMenu = false;
        boolean displayExternalAssetTransferMenu = false;

        if (productAllowsExternalTransfer) {
            if (canView) {
                displayTransferStatus = true;
            }

            if (canSubmit) {
                displayIntraAccountTransfer = true;

                boolean inTransition = false;
                if (account instanceof WrapAccountImpl) {
                    inTransition = ((WrapAccountImpl) account).getIsInTransition();
                }
                displayInspecieTransfer = !inTransition;
            }

            if (displayTransferStatus || displayInspecieTransfer || displayIntraAccountTransfer) {
                displayExternalAssetTransferMenu = true;
            }
        } else if (productAllowsInternalTransfer && canSubmit) {
            displayIntraAccountTransfer = true;
            displayInternalAssetTransferMenu = true;
        }

        permissionsDto.setPermission("account.transfer.inspecie.view", displayInspecieTransfer);
        permissionsDto.setPermission("account.transfer.intraaccount.view", displayIntraAccountTransfer);
        permissionsDto.setPermission("account.transfer.status.view", displayTransferStatus);
        permissionsDto.setPermission("account.transfer.internal.menu.view", displayInternalAssetTransferMenu);
        permissionsDto.setPermission("account.transfer.external.menu.view", displayExternalAssetTransferMenu);
    }
}