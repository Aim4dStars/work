package com.bt.nextgen.api.smsf.constants;


import static com.bt.nextgen.service.integration.domain.PersonTitle.DR;
import static com.bt.nextgen.service.integration.domain.PersonTitle.MISS;
import static com.bt.nextgen.service.integration.domain.PersonTitle.MR;
import static com.bt.nextgen.service.integration.domain.PersonTitle.MRS;
import static com.bt.nextgen.service.integration.domain.PersonTitle.MS;
import static com.bt.nextgen.service.integration.domain.PersonTitle.PROF;
import static com.bt.nextgen.service.integration.domain.PersonTitle.REV;

import java.util.HashMap;
import java.util.Map;

import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationSubtype;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.domain.PersonTitle;

public final class SortableCategories
{
    private static final Map<String, Map<String, Integer>> lookup = new HashMap<>();

    private SortableCategories() {
    }

    static {
        lookup.put(CodeCategory.POS_PROPERTY_TYPE.toString().toLowerCase(), PropertyType.getSortOrderLookup());
        lookup.put(CodeCategory.CASH_CATEGORY_SUB_TYPE.getCode(), CashCategorisationSubtype.getSortOrderLookup());
        lookup.put(CodeCategory.PERSON_TITLE.getCode(), titleSortOrderLookup());
    }

    private static Map<String, Integer> titleSortOrderLookup() {
        final Map<String, Integer> sortOrder = new HashMap<>();
        final PersonTitle[] orderedTitles = { MR, MS, MRS, MISS, DR, PROF, REV };
        for (int i = 0; i < orderedTitles.length; i++) {
            sortOrder.put(orderedTitles[i].getId(), i + 1);
        }
        return sortOrder;
    }

    public static Map<String, Integer> getSortOrder(String category) {
        return lookup.get(category);
    }
}
