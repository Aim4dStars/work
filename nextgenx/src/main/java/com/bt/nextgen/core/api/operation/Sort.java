package com.bt.nextgen.core.api.operation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.lang3.tuple.Pair;

import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.Dto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.core.api.model.ResultListDto;

/**
 * Decorates the another operation by providing sorting of the data
 */
public class Sort<K, T extends KeyedDto<K>> extends ChainedControllerOperation {
    public static final String SORT_PARAMETER = "order-by";
    public static final String SORT_ASC = "asc";
    public static final String SORT_DESC = "desc";
    private List<Pair<String, Boolean>> sortOrder;

    public Sort(ControllerOperation chained, String orderBy) {
        super(chained);
        if (orderBy != null) {
            sortOrder = parseOrderBy(orderBy);
        } else {
            sortOrder = new ArrayList<>();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public ApiResponse performChainedOperation(ApiResponse chainedResponse) {
        Dto data = chainedResponse.getData();

        if (!(data instanceof ResultListDto)) {
            throw new IllegalArgumentException("Sort only supports list operations");
        }

        @SuppressWarnings("rawtypes")
        List<Comparator> comparators = new ArrayList<>();

        for (Pair<String, Boolean> sortTupple : sortOrder) {
            Comparator<?> comparator = new BeanComparator(sortTupple.getKey(), new SortComparator());
            if (!sortTupple.getValue())
                comparator = new ReverseComparator(comparator);
            comparators.add(comparator);
        }

        if (comparators.size() > 0) {
            Collections.sort(((ResultListDto<T>) data).getResultList(), new ComparatorChain(comparators));
        }

        return chainedResponse;
    }

    //TODO - this should be changed to be json like query and paging.

    /**
     * parses order string eg "prop1;prop2,desc;prop3,asc" into a list of pairs to sort by eg [prop1 true, prop2 false, prop3 true]
     *
     * @param orderBy
     * @return
     */
    private static final List<Pair<String, Boolean>> parseOrderBy(String orderBy) {
        List<Pair<String, Boolean>> parsedOrderingList = new ArrayList<Pair<String, Boolean>>();

        for (String orderingEntry : orderBy.split(";")) {
            boolean ascending = true;
            String orderingProperty = orderingEntry;

            String[] splitOrderingEntry = orderingEntry.split(",");

            if (splitOrderingEntry.length > 1) {
                orderingProperty = splitOrderingEntry[0];
                if (SORT_DESC.equals(splitOrderingEntry[1])) {
                    ascending = false;
                }
            }

            Pair<String, Boolean> parsedOrdering = Pair.of(orderingProperty, ascending);
            parsedOrderingList.add(parsedOrdering);
        }
        return parsedOrderingList;
    }

    /**
     * Creates a custom comparator for sort operation
     */
    class SortComparator implements Comparator<Object> {
        @Override
        public int compare(Object o1, Object o2) {
            if (o1 == null && o2 == null)
                return 0;
            if (o1 == null || o2 == null)
                return (o1 == null) ? -1 : 1;

            if (o1 instanceof String && o2 instanceof String) {
                int i = ((String) o1).compareToIgnoreCase((String) o2);
                if (i == 0) {
                    i = ((String) o1).compareTo((String) o2);
                }
                return i;
            } else {
                return ((Comparable) o1).compareTo(o2);
            }
        }
    }
}
