package com.bt.nextgen.service.avaloq.drawdownstrategy;

import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.drawdownstrategy.AssetExclusionDetails;
import com.bt.nextgen.service.integration.drawdownstrategy.AssetPriorityDetails;
import com.bt.nextgen.service.integration.drawdownstrategy.DrawdownStrategy;
import com.bt.nextgen.service.integration.transaction.TransactionValidation;
import com.btfin.abs.trxservice.cont.v1_0.ContReq;
import com.btfin.abs.trxservice.cont.v1_0.DrawDwnExclPrefItem;
import com.btfin.abs.trxservice.cont.v1_0.DrawDwnPrefItem;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class DrawdownStrategyConverterTest {

    @InjectMocks
    private DrawdownStrategyConverter converter;

    @Test
    public void testToSubmitDrawdownStrategyRequest() {
        SubAccountKey directContainerKey = SubAccountKey.valueOf("directContainer");

        DrawdownStrategyDetailsImpl drawdownStrategy = new DrawdownStrategyDetailsImpl();
        drawdownStrategy.setAccountKey(AccountKey.valueOf("accountId"));
        drawdownStrategy.setDrawdownStrategy(DrawdownStrategy.ASSET_PRIORITY);
        drawdownStrategy.setAssetPriorityDetails(null);
        drawdownStrategy.setAssetExclusionDetails(null);

        ContReq request = converter.toSubmitDrawdownStrategyRequest(drawdownStrategy, directContainerKey);

        Assert.assertEquals(Constants.DO, request.getReq().getExec().getAction().getGenericAction());
        Assert.assertEquals("directContainer", request.getData().getCont().getVal());
        Assert.assertEquals(DrawdownStrategy.ASSET_PRIORITY.getIntlId(), request.getData().getDrawDwn().getStrat().getExtlVal()
                .getVal());
        Assert.assertNull(request.getData().getDrawDwnPrefList());
        Assert.assertNull(request.getData().getDrawDwnExclPrefList());
    }

    @Test
    public void testToValidateDrawdownAssetPreferences() {
        SubAccountKey directContainerKey = SubAccountKey.valueOf("directContainer");

        AssetPriorityDetailsImpl assetPriority = new AssetPriorityDetailsImpl();
        assetPriority.setAssetId("assetId");
        assetPriority.setDrawdownPriority(1);
        assetPriority.setDrawdownPercentage(BigDecimal.TEN);

        AssetPriorityDetailsImpl assetPriority2 = new AssetPriorityDetailsImpl();
        assetPriority2.setAssetId("assetId2");
        assetPriority2.setDrawdownPriority(2);
        assetPriority2.setDrawdownPercentage(BigDecimal.ONE);

        List<AssetPriorityDetails> assetPriorities = new ArrayList<>();
        assetPriorities.add(assetPriority);
        assetPriorities.add(assetPriority2);

        TransactionValidation warning = Mockito.mock(TransactionValidation.class);
        List<TransactionValidation> warnings = new ArrayList<>();
        warnings.add(warning);

        DrawdownStrategyDetailsImpl drawdownStrategy = new DrawdownStrategyDetailsImpl();
        drawdownStrategy.setAccountKey(AccountKey.valueOf("accountId"));
        drawdownStrategy.setAssetPriorityDetails(assetPriorities);
        drawdownStrategy.setWarnings(warnings);

        ContReq request = converter.toValidateAssetPreferencesRequest(drawdownStrategy, directContainerKey);

        Assert.assertEquals(Constants.DO, request.getReq().getValid().getAction().getGenericAction());
        Assert.assertEquals("directContainer", request.getData().getCont().getVal());

        List<DrawDwnPrefItem> priorities = request.getData().getDrawDwnPrefList().getDrawDwnPrefItem();
        Assert.assertEquals(2, priorities.size());

        Assert.assertEquals("assetId", priorities.get(0).getAssetId().getVal());
        Assert.assertEquals(BigDecimal.ONE, priorities.get(0).getPrio().getVal());

        Assert.assertEquals("assetId2", priorities.get(1).getAssetId().getVal());
        Assert.assertEquals(BigDecimal.valueOf(2), priorities.get(1).getPrio().getVal());

        Assert.assertNull(request.getData().getDrawDwn());
        Assert.assertNull(request.getData().getDrawDwnExclPrefList());
    }

    @Test
    public void testToSubmitDrawdownAssetPreferences() {
        SubAccountKey directContainerKey = SubAccountKey.valueOf("directContainer");

        AssetPriorityDetailsImpl assetPriority = new AssetPriorityDetailsImpl();
        assetPriority.setAssetId("assetId");
        assetPriority.setDrawdownPriority(1);
        assetPriority.setDrawdownPercentage(BigDecimal.TEN);

        AssetPriorityDetailsImpl assetPriority2 = new AssetPriorityDetailsImpl();
        assetPriority2.setAssetId("assetId2");
        assetPriority2.setDrawdownPriority(2);

        List<AssetPriorityDetails> assetPriorities = new ArrayList<>();
        assetPriorities.add(assetPriority);
        assetPriorities.add(assetPriority2);

        TransactionValidation warning = Mockito.mock(TransactionValidation.class);
        List<TransactionValidation> warnings = new ArrayList<>();
        warnings.add(warning);

        DrawdownStrategyDetailsImpl drawdownStrategy = new DrawdownStrategyDetailsImpl();
        drawdownStrategy.setAccountKey(AccountKey.valueOf("accountId"));
        drawdownStrategy.setAssetPriorityDetails(assetPriorities);
        drawdownStrategy.setWarnings(warnings);

        ContReq request = converter.toSubmitAssetPreferencesRequest(drawdownStrategy, directContainerKey);

        Assert.assertEquals(Constants.DO, request.getReq().getExec().getAction().getGenericAction());
        Assert.assertEquals("directContainer", request.getData().getCont().getVal());

        List<DrawDwnPrefItem> priorities = request.getData().getDrawDwnPrefList().getDrawDwnPrefItem();
        Assert.assertEquals(2, priorities.size());

        Assert.assertEquals("assetId", priorities.get(0).getAssetId().getVal());
        Assert.assertEquals(BigDecimal.ONE, priorities.get(0).getPrio().getVal());

        Assert.assertEquals("assetId2", priorities.get(1).getAssetId().getVal());
        Assert.assertEquals(BigDecimal.valueOf(2), priorities.get(1).getPrio().getVal());

        Assert.assertNull(request.getData().getDrawDwn());
        Assert.assertNull(request.getData().getDrawDwnExclPrefList());
    }

    @Test
    public void testToValidateDrawdownAssetExclusions() {
        SubAccountKey directContainerKey = SubAccountKey.valueOf("directContainer");

        AssetExclusionDetailsImpl assetExclusion = new AssetExclusionDetailsImpl();
        assetExclusion.setAssetId("excludedAssetId");

        AssetExclusionDetailsImpl assetExclusion2 = new AssetExclusionDetailsImpl();
        assetExclusion2.setAssetId("excludedAssetId2");

        List<AssetExclusionDetails> assetExclusions = new ArrayList<>();
        assetExclusions.add(assetExclusion);
        assetExclusions.add(assetExclusion2);

        DrawdownStrategyDetailsImpl drawdownStrategy = new DrawdownStrategyDetailsImpl();
        drawdownStrategy.setAccountKey(AccountKey.valueOf("accountId"));
        drawdownStrategy.setAssetExclusionDetails(assetExclusions);

        ContReq request = converter.toValidateAssetExclusionsRequest(drawdownStrategy, directContainerKey);

        List<DrawDwnExclPrefItem> exclusions = request.getData().getDrawDwnExclPrefList().getDrawDwnExclPrefItem();
        Assert.assertEquals(2, exclusions.size());

        Assert.assertEquals("excludedAssetId", exclusions.get(0).getAssetId().getVal());
        Assert.assertEquals("excludedAssetId2", exclusions.get(1).getAssetId().getVal());

        Assert.assertNull(request.getData().getDrawDwn());
        Assert.assertNull(request.getData().getDrawDwnPrefList());
    }

    @Test
    public void testToSubmitDrawdownAssetExclusions() {
        SubAccountKey directContainerKey = SubAccountKey.valueOf("directContainer");

        AssetExclusionDetailsImpl assetExclusion = new AssetExclusionDetailsImpl();
        assetExclusion.setAssetId("excludedAssetId");

        AssetExclusionDetailsImpl assetExclusion2 = new AssetExclusionDetailsImpl();
        assetExclusion2.setAssetId("excludedAssetId2");

        List<AssetExclusionDetails> assetExclusions = new ArrayList<>();
        assetExclusions.add(assetExclusion);
        assetExclusions.add(assetExclusion2);

        DrawdownStrategyDetailsImpl drawdownStrategy = new DrawdownStrategyDetailsImpl();
        drawdownStrategy.setAccountKey(AccountKey.valueOf("accountId"));
        drawdownStrategy.setAssetExclusionDetails(assetExclusions);

        ContReq request = converter.toSubmitAssetExclusionsRequest(drawdownStrategy, directContainerKey);

        List<DrawDwnExclPrefItem> exclusions = request.getData().getDrawDwnExclPrefList().getDrawDwnExclPrefItem();
        Assert.assertEquals(2, exclusions.size());

        Assert.assertEquals("excludedAssetId", exclusions.get(0).getAssetId().getVal());
        Assert.assertEquals("excludedAssetId2", exclusions.get(1).getAssetId().getVal());

        Assert.assertNull(request.getData().getDrawDwn());
        Assert.assertNull(request.getData().getDrawDwnPrefList());
    }
}
