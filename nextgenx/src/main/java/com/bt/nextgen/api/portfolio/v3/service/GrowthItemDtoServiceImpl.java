package com.bt.nextgen.api.portfolio.v3.service;

import com.bt.nextgen.api.portfolio.v3.model.movement.GrowthItemDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.options.model.OptionValue;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import com.bt.nextgen.service.integration.portfolio.movement.GrowthItem;
import com.bt.nextgen.service.integration.portfolio.movement.GrowthItemType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("GrowthItemDtoServiceV3")
@Transactional(value = "springJpaTransactionManager")
public class GrowthItemDtoServiceImpl implements GrowthItemDtoService {

    @Autowired
    private OptionsService optionService;

    public List<GrowthItemDto> loadGrowthItems(AccountKey key, List<GrowthItem> items, ServiceErrors serviceErrors) {
        Collection<OptionValue<String>> options = optionService.getOptions(key, serviceErrors);
        Map<String, String> map = new HashMap<>();
        for (OptionValue<String> val : options) {
            map.put(val.getOptionValueKey().getOptionKey().getOptionName(), val.getValue());
        }

        List<GrowthItemDto> growthItems = new ArrayList<>();
        for (GrowthItem growthItem : items) {
            GrowthItemDto growthItemDto = getGrowthItem(growthItem, map);
            growthItems.add(growthItemDto);
        }
        return growthItems;
    }

    private GrowthItemDto getGrowthItem(GrowthItem growthItem, Map<String, String> optionMap) {
        GrowthItemType type = GrowthItemType.forCode(growthItem.getCode());
        final String opnName = "movement.category." + type.name().toLowerCase();
        String displayName = optionMap.get(opnName);

        GrowthItemDto growthItemDto = new GrowthItemDto(growthItem.getBalance(), growthItem.getCode(), displayName,
                new ArrayList<GrowthItemDto>());

        for (GrowthItem children : growthItem.getGrowthItems()) {
            if (!isTaxOrExpense(children.getCode())
                    || (isTaxOrExpense(children.getCode()) && children.getBalance().compareTo(BigDecimal.ZERO) != 0)) {
                growthItemDto.getGrowthItems().add(getGrowthItem(children, optionMap));
            }
        }

        return growthItemDto;
    }

    private boolean isTaxOrExpense(String code) {
        GrowthItemType[] expenses = { GrowthItemType.ESTAB_FEE, GrowthItemType.LICENSEE_FEE, GrowthItemType.ONE_OFF_ADV_FEE,
                GrowthItemType.ONG_ADV_FEE, GrowthItemType.TFN_WTAX, GrowthItemType.FRN_WTAX, GrowthItemType.STAMP_DUTY,
                GrowthItemType.SMSF_FEES, GrowthItemType.SMSF_ADM_FEE, GrowthItemType.SMSF_ESTBMT_FEE,
                GrowthItemType.SMSF_ACTRL_FEE, GrowthItemType.SMSF_ADT_FEE, };

        GrowthItemType type = GrowthItemType.forCode(code);

        for (GrowthItemType expense : expenses) {
            if (type == expense)
                return true;
        }
        return false;
    }
}
