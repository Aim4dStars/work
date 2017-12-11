package com.bt.nextgen.service.wrap.integration.util;

import org.junit.Test;
import org.mockito.InjectMocks;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by L067221 on 25/09/2017.
 */
public class WrapAssetUtilTest {

    @InjectMocks
    WrapAssetUtil assetUtil;

    @Test
    public void testIsWrapTermDeposit_whenWrapTD(){
        assertTrue(WrapAssetUtil.isWrapTermDeposit("WBC123TD"));
    }

    @Test
    public void testIsWrapTermDeposit_whenNONWrapTD(){
        assertFalse(WrapAssetUtil.isWrapTermDeposit("bttd"));
        assertFalse(WrapAssetUtil.isWrapTermDeposit("WBC123"));
        assertFalse(WrapAssetUtil.isWrapTermDeposit(null));
    }

    @Test
    public void testIsWrapCash_whenWrapCash(){
        assertTrue(WrapAssetUtil.isWrapCash("WRAPWCA"));
    }

    @Test
    public void testIsWrapCash_whenNonWrapCash(){
        assertFalse(WrapAssetUtil.isWrapCash("btcash"));
        assertFalse(WrapAssetUtil.isWrapCash("wrapwc"));
        assertFalse(WrapAssetUtil.isWrapCash(null));
    }

}
