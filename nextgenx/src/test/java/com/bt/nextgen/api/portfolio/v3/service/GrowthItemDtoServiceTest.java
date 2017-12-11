package com.bt.nextgen.api.portfolio.v3.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.portfolio.v3.model.movement.GrowthItemDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.portfolio.movement.GrowthItemImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.options.model.OptionValue;
import com.bt.nextgen.service.integration.options.model.OptionValueKey;
import com.bt.nextgen.service.integration.options.model.StringOptionValueImpl;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import com.bt.nextgen.service.integration.portfolio.movement.GrowthItem;

@RunWith(MockitoJUnitRunner.class)
public class GrowthItemDtoServiceTest {

    @InjectMocks
    private GrowthItemDtoServiceImpl growthItemDtoService;

    @Mock
    private OptionsService optionsService;

    private AccountKey key = AccountKey.valueOf("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0");

    private List<GrowthItem> items;

    @Before
    public void setup() throws Exception {
        items = new ArrayList<GrowthItem>();
        GrowthItemImpl parent1 = new GrowthItemImpl(new BigDecimal("1000.11"), "incPortfVal", "Inflows",
                new ArrayList<GrowthItem>());
        GrowthItemImpl child1 = new GrowthItemImpl(new BigDecimal("700.09"), "depot", "Deposits to BT Cash",
                new ArrayList<GrowthItem>());
        GrowthItemImpl child2 = new GrowthItemImpl(new BigDecimal("300.02"), "assetXferIn", "Asset transfers in",
                new ArrayList<GrowthItem>());

        GrowthItemImpl parent2 = new GrowthItemImpl(new BigDecimal("-74622"), "expns", "Expenses", new ArrayList<GrowthItem>());

        GrowthItemImpl child3 = new GrowthItemImpl(new BigDecimal("-9632"), "admFee", "Administration fees",
                new ArrayList<GrowthItem>());

        GrowthItemImpl child4 = new GrowthItemImpl(new BigDecimal("-9632"), "advFee", "Advice fees", new ArrayList<GrowthItem>());

        parent1.getGrowthItems().add(child1);
        parent1.getGrowthItems().add(child2);
        parent2.getGrowthItems().add(child3);
        parent2.getGrowthItems().add(child4);

        items.add(parent1);
        items.add(parent2);

        Collection<OptionValue<String>> options = new ArrayList();

        StringOptionValueImpl option1 = new StringOptionValueImpl(new OptionValueKey(null, null, "movement.category.deposit"),
                "Deposits to BT Cash");
        StringOptionValueImpl option2 = new StringOptionValueImpl(new OptionValueKey(null, null,
                "movement.category.asset_xfer_in"), "Asset transfers in");
        StringOptionValueImpl option3 = new StringOptionValueImpl(new OptionValueKey(null, null, "movement.category.inflows"),
                "Inflows");
        StringOptionValueImpl option4 = new StringOptionValueImpl(new OptionValueKey(null, null, "movement.category.adm_fee"),
                "Administration fees");
        StringOptionValueImpl option5 = new StringOptionValueImpl(new OptionValueKey(null, null, "movement.category.adv_fee"),
                "Advice fees");
        StringOptionValueImpl option6 = new StringOptionValueImpl(new OptionValueKey(null, null, "movement.categoty.expense"),
                "Expenses");
        options.add(option1);
        options.add(option2);
        options.add(option3);
        options.add(option4);
        options.add(option5);
        options.add(option6);

        Mockito.when(optionsService.getOptions(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class))).thenReturn(
                options);
    }

    @Test
    public void testGrowthItemDto(){
        List<GrowthItemDto> growthItems = growthItemDtoService.loadGrowthItems(key, items, new ServiceErrorsImpl());
        Assert.assertEquals(2, growthItems.size());
        GrowthItemDto growthItemDto = growthItems.get(0);
        Assert.assertEquals("Inflows", growthItemDto.getDisplayName());
        Assert.assertEquals(new BigDecimal("1000.11"), growthItemDto.getBalance());
    }
}
