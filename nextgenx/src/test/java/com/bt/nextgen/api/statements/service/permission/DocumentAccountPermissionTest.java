package com.bt.nextgen.api.statements.service.permission;

import com.bt.nextgen.api.statements.permission.DocumentAccountPermission;
import com.bt.nextgen.core.security.api.model.PermissionsDto;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.integration.account.BlockCode;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

;

/**
 * Created by L075208 on 3/09/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class DocumentAccountPermissionTest {


    @InjectMocks
    DocumentAccountPermission documentAccountPermission;

    @Test
    public void testApplyPermission() {

        ClientKey key = ClientKey.valueOf("123");

        PermissionsDto permission = new PermissionsDto(false);
        permission.setPermission("account.document.upload", true);
        WrapAccountDetailImpl wrapAccount = new WrapAccountDetailImpl();

        wrapAccount.setAccountStatus(AccountStatus.ACTIVE);
        documentAccountPermission = new DocumentAccountPermission(permission, wrapAccount);
        documentAccountPermission.applyPermissions();
        Assert.assertTrue(permission.hasPermission("account.document.upload"));

        wrapAccount.setAccountStatus(AccountStatus.CLOSE);
        documentAccountPermission = new DocumentAccountPermission(permission, wrapAccount);
        documentAccountPermission.applyPermissions();
        Assert.assertTrue(permission.hasPermission("account.document.upload"));

        wrapAccount.setAccountStatus(AccountStatus.ACTIVE);
        permission.setPermission("account.document.upload", true);
        List<BlockCode> blockCodes = new ArrayList<>();
        blockCodes.add(BlockCode.Blocked_for_All_except_Interest_Payment_and_Tax);
        blockCodes.add(BlockCode.Blocked_for_All_Trade_Sanctions);
        blockCodes.add(BlockCode.Blocked_for_All);
        Map<ClientKey, List<BlockCode>> blockCodeMap = new HashMap<>();
        blockCodeMap.put(key, blockCodes);
        wrapAccount.setBlockedReason(blockCodeMap);
        documentAccountPermission = new DocumentAccountPermission(permission, wrapAccount);
        documentAccountPermission.applyPermissions();
        Assert.assertFalse(permission.hasPermission("account.document.upload"));

        wrapAccount.setAccountStatus(AccountStatus.DISCARD);
        permission.setPermission("account.document.upload", true);
        blockCodes = new ArrayList<>();
        blockCodes.add(BlockCode.Blocked_for_All);
        blockCodeMap = new HashMap<>();
        blockCodeMap.put(key, blockCodes);
        wrapAccount.setBlockedReason(blockCodeMap);
        documentAccountPermission = new DocumentAccountPermission(permission, wrapAccount);
        documentAccountPermission.applyPermissions();
        Assert.assertFalse(permission.hasPermission("account.document.upload"));

        key = ClientKey.valueOf("1234");
        permission.setPermission("account.document.upload", true);
        wrapAccount.setAccountStatus(AccountStatus.DISCARD);
        blockCodes = new ArrayList<>();
        blockCodes.add(BlockCode.Blocked_for_Trades_Buy_and_Outgoing_Payment);
        blockCodeMap = new HashMap<>();
        blockCodeMap.put(key, blockCodes);
        wrapAccount.setBlockedReason(blockCodeMap);
        documentAccountPermission = new DocumentAccountPermission(permission, wrapAccount);
        documentAccountPermission.applyPermissions();
        Assert.assertTrue(permission.hasPermission("account.document.upload"));


        key = ClientKey.valueOf("1234");
        permission.setPermission("account.document.upload", true);
        wrapAccount.setAccountStatus(AccountStatus.DISCARD);
        blockCodes = new ArrayList<>();
        blockCodeMap = new HashMap<>();
        blockCodeMap.put(key, blockCodes);
        wrapAccount.setBlockedReason(blockCodeMap);
        documentAccountPermission = new DocumentAccountPermission(permission, wrapAccount);
        documentAccountPermission.applyPermissions();
        Assert.assertTrue(permission.hasPermission("account.document.upload"));

    }


}
