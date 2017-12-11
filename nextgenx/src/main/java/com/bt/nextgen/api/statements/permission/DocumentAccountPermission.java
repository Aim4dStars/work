package com.bt.nextgen.api.statements.permission;

import com.bt.nextgen.core.security.api.model.PermissionsDto;
import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;
import com.bt.nextgen.service.integration.account.BlockCode;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class DocumentAccountPermission {
    private PermissionsDto permissionsDto;
    private WrapAccountDetail account;
    private Set<BlockCode> blockCodeSet = new HashSet<>();

    public DocumentAccountPermission(PermissionsDto permissionsDto, WrapAccountDetail account) {
        this.permissionsDto = permissionsDto;
        this.account = account;
    }

    public void applyPermissions() {
        if (permissionsDto != null && account != null && account.getBlockedReason() != null && account.getBlockedReason().values() != null) {
            for (List<BlockCode> blockCodes : account.getBlockedReason().values()) {
                blockCodeSet.addAll(blockCodes);
            }
            checkForBlockCode();
        }
    }

    private void checkForBlockCode() {
        if (blockCodeSet.contains(BlockCode.Blocked_for_Trades_Buy_and_Sell_and_Outgoing_Payment) ||
                blockCodeSet.contains(BlockCode.Blocked_for_All_except_Interest_Payment_and_Tax) ||
                blockCodeSet.contains(BlockCode.Blocked_for_All) ||
                blockCodeSet.contains(BlockCode.Blocked_for_All_Trade_Sanctions)) {
            disablePermissionForBlockedAccount();
        }
    }

    private void disablePermissionForBlockedAccount() {
        List<String> uiRoles = new ArrayList<>();
        uiRoles.addAll(FunctionalRole.Maintain_document_attributes.getUiRoles());
        uiRoles.addAll(FunctionalRole.Update_document_Audit.getUiRoles());
        uiRoles.addAll(FunctionalRole.Upload_Document.getUiRoles());
        uiRoles.addAll(FunctionalRole.Delete_Document.getUiRoles());
        for (String uiRole : uiRoles) {
            permissionsDto.setPermission(uiRole, false);
        }
    }
}
