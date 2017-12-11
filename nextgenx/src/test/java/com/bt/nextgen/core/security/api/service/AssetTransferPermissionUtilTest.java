package com.bt.nextgen.core.security.api.service;

import com.bt.nextgen.core.security.api.model.PermissionsDto;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class AssetTransferPermissionUtilTest extends PermissionAccountDtoServiceBase {

    private final static String PERMISSION_SUBMIT = "account.inspecie.transfer.submit";
    private final static String PERMISSION_VIEW_ORDER = "account.order.view";
    private final static String OPTION_INTERNAL = "option.assettransfer.internal";
    private final static String OPTION_EXTERNAL = "option.assettransfer.external";

    private final static String VIEW_STATUS = "account.transfer.status.view";
    private final static String VIEW_INSPECIE = "account.transfer.inspecie.view";
    private final static String VIEW_INTRA = "account.transfer.intraaccount.view";
    private final static String VIEW_INTERNAL_MENU = "account.transfer.internal.menu.view";
    private final static String VIEW_EXTERNAL_MENU = "account.transfer.external.menu.view";

    @Test
    public void testGetPermissions_whenInternalAndExternalTransfersNotSupported_thenNoPermissions() {
        WrapAccountDetail account = Mockito.mock(WrapAccountDetail.class);

        PermissionsDto permissions = new PermissionsDto(false);
        permissions.setPermission(PERMISSION_SUBMIT, true);
        permissions.setPermission(PERMISSION_VIEW_ORDER, true);
        permissions.setPermission(OPTION_INTERNAL, false);
        permissions.setPermission(OPTION_EXTERNAL, false);

        AssetTransferPermissionUtil.retrieveAssetTransferPermission(permissions, account);
        Assert.assertFalse(permissions.hasPermission(VIEW_STATUS));
        Assert.assertFalse(permissions.hasPermission(VIEW_INSPECIE));
        Assert.assertFalse(permissions.hasPermission(VIEW_INTRA));
        Assert.assertFalse(permissions.hasPermission(VIEW_INTERNAL_MENU));
        Assert.assertFalse(permissions.hasPermission(VIEW_EXTERNAL_MENU));
    }

    @Test
    public void testGetPermissions_whenInternalAndExternalTransfersSupported_thenExternalTransferPermissionsSet() {
        WrapAccountDetailImpl account = Mockito.mock(WrapAccountDetailImpl.class);
        Mockito.when(account.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        Mockito.when(account.getIsInTransition()).thenReturn(Boolean.FALSE);

        PermissionsDto permissions = new PermissionsDto(false);
        permissions.setPermission(PERMISSION_SUBMIT, true);
        permissions.setPermission(PERMISSION_VIEW_ORDER, true);
        permissions.setPermission(OPTION_INTERNAL, true);
        permissions.setPermission(OPTION_EXTERNAL, true);

        AssetTransferPermissionUtil.retrieveAssetTransferPermission(permissions, account);
        Assert.assertTrue(permissions.hasPermission(VIEW_STATUS));
        Assert.assertTrue(permissions.hasPermission(VIEW_INSPECIE));
        Assert.assertTrue(permissions.hasPermission(VIEW_INTRA));
        Assert.assertFalse(permissions.hasPermission(VIEW_INTERNAL_MENU));
        Assert.assertTrue(permissions.hasPermission(VIEW_EXTERNAL_MENU));
    }

    @Test
    public void testGetPermissions_whenOnlyExternalTransfersSupported_thenExternalTransferPermissionsSet() {
        WrapAccountDetail account = mock(WrapAccountDetail.class);

        PermissionsDto permissions = new PermissionsDto(false);
        permissions.setPermission(PERMISSION_SUBMIT, true);
        permissions.setPermission(PERMISSION_VIEW_ORDER, true);
        permissions.setPermission(OPTION_INTERNAL, false);
        permissions.setPermission(OPTION_EXTERNAL, true);

        AssetTransferPermissionUtil.retrieveAssetTransferPermission(permissions, account);
        Assert.assertTrue(permissions.hasPermission(VIEW_STATUS));
        Assert.assertTrue(permissions.hasPermission(VIEW_INSPECIE));
        Assert.assertTrue(permissions.hasPermission(VIEW_INTRA));
        Assert.assertFalse(permissions.hasPermission(VIEW_INTERNAL_MENU));
        Assert.assertTrue(permissions.hasPermission(VIEW_EXTERNAL_MENU));
    }

    @Test
    public void testGetPermissions_whenOnlyInternalTransfersSupported_thenInternalTransferPermissionsSet() {
        WrapAccountDetailImpl account = mock(WrapAccountDetailImpl.class);

        PermissionsDto permissions = new PermissionsDto(false);
        permissions.setPermission(PERMISSION_SUBMIT, true);
        permissions.setPermission(PERMISSION_VIEW_ORDER, true);
        permissions.setPermission(OPTION_INTERNAL, true);
        permissions.setPermission(OPTION_EXTERNAL, false);

        AssetTransferPermissionUtil.retrieveAssetTransferPermission(permissions, account);
        Assert.assertFalse(permissions.hasPermission(VIEW_STATUS));
        Assert.assertFalse(permissions.hasPermission(VIEW_INSPECIE));
        Assert.assertTrue(permissions.hasPermission(VIEW_INTRA));
        Assert.assertTrue(permissions.hasPermission(VIEW_INTERNAL_MENU));
        Assert.assertFalse(permissions.hasPermission(VIEW_EXTERNAL_MENU));
    }

    @Test
    public void testGetPermissions_whenExternalTransfersSupportedButNoPermissions_thenNoTransferPermissionsSet() {
        WrapAccountDetailImpl account = mock(WrapAccountDetailImpl.class);

        PermissionsDto permissions = new PermissionsDto(false);
        permissions.setPermission(PERMISSION_SUBMIT, false);
        permissions.setPermission(PERMISSION_VIEW_ORDER, false);
        permissions.setPermission(OPTION_INTERNAL, false);
        permissions.setPermission(OPTION_EXTERNAL, true);

        AssetTransferPermissionUtil.retrieveAssetTransferPermission(permissions, account);
        Assert.assertFalse(permissions.hasPermission(VIEW_STATUS));
        Assert.assertFalse(permissions.hasPermission(VIEW_INSPECIE));
        Assert.assertFalse(permissions.hasPermission(VIEW_INTRA));
        Assert.assertFalse(permissions.hasPermission(VIEW_INTERNAL_MENU));
        Assert.assertFalse(permissions.hasPermission(VIEW_EXTERNAL_MENU));
    }

    @Test
    public void testGetPermissions_whenInternalTransfersSupportedButNoPermissions_thenNoTransferPermissionsSet() {
        WrapAccountDetailImpl account = mock(WrapAccountDetailImpl.class);

        PermissionsDto permissions = new PermissionsDto(false);
        permissions.setPermission(PERMISSION_SUBMIT, false);
        permissions.setPermission(PERMISSION_VIEW_ORDER, false);
        permissions.setPermission(OPTION_INTERNAL, true);
        permissions.setPermission(OPTION_EXTERNAL, false);

        AssetTransferPermissionUtil.retrieveAssetTransferPermission(permissions, account);
        Assert.assertFalse(permissions.hasPermission(VIEW_STATUS));
        Assert.assertFalse(permissions.hasPermission(VIEW_INSPECIE));
        Assert.assertFalse(permissions.hasPermission(VIEW_INTRA));
        Assert.assertFalse(permissions.hasPermission(VIEW_INTERNAL_MENU));
        Assert.assertFalse(permissions.hasPermission(VIEW_EXTERNAL_MENU));
    }

    @Test
    public void testGetPermissions_whenExternalTransfersSupportedAndPermissionButIsInTransition_thenInspecieTransferPermissionFalse() {
        WrapAccountDetailImpl account = mock(WrapAccountDetailImpl.class);
        Mockito.when(account.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        Mockito.when(account.getIsInTransition()).thenReturn(Boolean.TRUE);

        PermissionsDto permissions = new PermissionsDto(false);
        permissions.setPermission(PERMISSION_SUBMIT, true);
        permissions.setPermission(PERMISSION_VIEW_ORDER, true);
        permissions.setPermission(OPTION_INTERNAL, false);
        permissions.setPermission(OPTION_EXTERNAL, true);

        AssetTransferPermissionUtil.retrieveAssetTransferPermission(permissions, account);
        Assert.assertTrue(permissions.hasPermission(VIEW_STATUS));
        Assert.assertFalse(permissions.hasPermission(VIEW_INSPECIE));
        Assert.assertTrue(permissions.hasPermission(VIEW_INTRA));
        Assert.assertFalse(permissions.hasPermission(VIEW_INTERNAL_MENU));
        Assert.assertTrue(permissions.hasPermission(VIEW_EXTERNAL_MENU));
    }
}
