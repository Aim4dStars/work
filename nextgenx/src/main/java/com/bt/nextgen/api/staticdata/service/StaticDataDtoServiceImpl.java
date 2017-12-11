package com.bt.nextgen.api.staticdata.service;

import static ch.lambdaj.Lambda.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.smsf.constants.SortableCategories;
import com.bt.nextgen.api.staticdata.model.StaticCodeDto;
import com.bt.nextgen.api.staticdata.model.StaticCodeOrderedDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.Field;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;

/**
 * Dto service implementation which returns static codes from avaloq services based on List <ApiSearchCriteria>
 */
@Service
public class StaticDataDtoServiceImpl implements StaticDataDtoService
{
    /**
     * If ABS places under Xpath code_head_list/code_head/extn_fld_list/extn_fld
     * btfg$order_by -> unique counting number (as string), nullable eg “1”, “10” etc
     * we'll sort every static code category with this present, on the order. It is somewhat robust
     * in that it falls back to a default if any are missing.
     * See also btfg$is_panorama_val -> +/- which is in the same structure and used for <strong>filtering</strong>.
     */
	public static final String ABS_SORT_ORDER = "btfg$order_by";

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticDataDtoServiceImpl.class);

    private static final String FIELD_PREFIX = "field:";

    private static final String CATEGORY = "category";

    private static final int DEF_ORDER = 999999;

    /** External field name for IM code. */
    public static final String IM_CODE = "btfg$im_code";

    private static final Comparator<StaticCodeOrderedDto> BY_ORDER = new Comparator<StaticCodeOrderedDto>() {
        @Override
        public int compare(StaticCodeOrderedDto o1, StaticCodeOrderedDto o2) {
            if (o1.getOrder() > o2.getOrder()) {
                return 1;
            } else {
                return -1;
            }
        }
    };

    @Autowired
	private StaticIntegrationService staticIntegrationService;

	/**
	 * returns list of static code for all the criteria list passed as parameter
	 * Returns List <StaticCodeDto>
	 */
	@Override
	public List <StaticCodeDto> search(final List <ApiSearchCriteria> criteriaList, final ServiceErrors serviceErrors)
	{
		final List<StaticCodeDto> returnCodes = new ArrayList<>();
        final List<String> categoryNames = new ArrayList<>(criteriaList.size());
        final List<Matcher<Code>> predicates = new ArrayList<>();

		determineCategoryNamesAndPredicatesFromCriteriaList(criteriaList, categoryNames, predicates);

        for (String categoryName : categoryNames) {
            final CodeCategory category = CodeCategory.valueOf(StringUtils.upperCase(categoryName));
            Collection<Code> codes = staticIntegrationService.loadCodes(category, serviceErrors);
            for (Matcher<Code> predicate : predicates) {
                codes = filter(predicate, codes);
            }
            List<StaticCodeDto> staticCodes = new ArrayList<>(codes.size());
            
            Map<String, Integer> absSortOrder = new HashMap<>();
            boolean hasAbsSortOrder = false;
            if ("COUNTRY".equals(StringUtils.upperCase(categoryName))) {
                for (Code code : codes) {
                    Integer absSortPosition = determinePossibleABSOrdering(code);
                    if (!hasAbsSortOrder && absSortPosition < DEF_ORDER) {
                        hasAbsSortOrder = true; // ie at lease one valid sort order comes from ABS.
                    }
                    absSortOrder.put(code.getIntlId(), absSortPosition);
                    if (code.getField(IM_CODE) != null) {
                        staticCodes.add(new StaticCodeDto(code.getCodeId(), code.getName(), code.getField(IM_CODE).getValue(), code.getIntlId(), categoryName));
                    }
                }
            } else {
                for (Code code : codes) {
                    Integer absSortPosition = determinePossibleABSOrdering(code);
                    if (!hasAbsSortOrder && absSortPosition < DEF_ORDER) {
                        hasAbsSortOrder = true; // ie at lease one valid sort order comes from ABS.
                    }
                    absSortOrder.put(code.getIntlId(), absSortPosition);
                    staticCodes.add(new StaticCodeDto(code.getCodeId(), code.getName(), code.getUserId(), code.getIntlId(), categoryName));
                }
            }

            // Check whether any sorting is required.
            // If an entry for this static code category exist in the {@Link SortableCategories} lookup then sort on defined order
            Map<String, Integer> sortOrder = SortableCategories.getSortOrder(category.getCode());
            // If we have no special lookup, yet ABS is sending us one, use that
            if (sortOrder == null && hasAbsSortOrder) {
                sortOrder = absSortOrder;
            }
            if (sortOrder != null) {
                staticCodes = orderStaticCodes(staticCodes, sortOrder);
            }

			returnCodes.addAll(staticCodes);
		}
		return returnCodes;
	}


    private @Nonnull Integer determinePossibleABSOrdering(@Nonnull Code code) {
        Field sortOrderField = code.getField(ABS_SORT_ORDER);
        Integer sortPosition = DEF_ORDER;
        if (sortOrderField != null) {
            try {
                sortPosition = Integer.parseInt(sortOrderField.getValue());
            } catch (NumberFormatException nfe) {
                LOGGER.error("could not parse this " + ABS_SORT_ORDER + " field" + sortOrderField + " using default");
            }
        }
        return sortPosition;
    }


    private void determineCategoryNamesAndPredicatesFromCriteriaList(@Nonnull final List<ApiSearchCriteria> criteriaList, @Nonnull final List<String> categoryNames,
            @Nonnull final List<Matcher<Code>> predicates) {
        for (ApiSearchCriteria criteria : criteriaList)
		{
            final String property = criteria.getProperty();
            final String value = criteria.getValue();
            if (property.startsWith(FIELD_PREFIX)) {
                final String fieldName = property.substring(FIELD_PREFIX.length());
                predicates.add(new CodeFieldPredicate(fieldName, value, criteria.getOperation()));
            } else if (CATEGORY.equalsIgnoreCase(property)) {
                categoryNames.add(value);
            } else {
                // Old way - the category is the actual property name, because... yeah.
                categoryNames.add(property);
            }
        }
    }


    /**
     * <p>Order static codes based on a lookup table of internal_id to order.</p>
     *
     * <p>If a sort order cannot be found for a static code it is assigned sort order value 999.</p>
     *
     * @param codes Static code list that needs to be ordered
     * @param mapping Lookup (int_code to order) which specifies the ordering for a specific static code.
     * @return Ordered list of static codes
     */
    private List<StaticCodeDto> orderStaticCodes(List<StaticCodeDto> codes, Map<String, Integer> mapping)
    {
        // Empty order mapping table -- return unsorted static codes
        if (mapping.isEmpty()) {
            return codes;
        }

        List<StaticCodeOrderedDto> orderedStaticCodeList = new ArrayList<>(codes.size());
        for (StaticCodeDto staticCode : codes)
        {
            Integer order = mapping.get(staticCode.getIntlId());
            if (order == null) {
                order = DEF_ORDER;
            }
            orderedStaticCodeList.add(new StaticCodeOrderedDto(staticCode, order));
        }
        return performOrdering(orderedStaticCodeList);
    }

    /**
     * <p>Perform actual ordering on a list of static codes (they should have their order variable set).</p>
     *
     * @param orderedStaticCodeList
     * @return
     */
    private List<StaticCodeDto> performOrdering(List<StaticCodeOrderedDto> orderedStaticCodeList)
    {
        Collections.sort(orderedStaticCodeList, BY_ORDER);
        final List<StaticCodeDto> staticCodeList = new ArrayList<>(orderedStaticCodeList.size());
        for (StaticCodeOrderedDto orderedDto : orderedStaticCodeList)
        {
            staticCodeList.add(new StaticCodeDto(orderedDto));
        }
        return staticCodeList;
    }
}
