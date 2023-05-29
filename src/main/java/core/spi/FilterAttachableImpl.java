package core.spi;


import core.filter.Filter;
import core.util.COWArrayList;

import java.util.ArrayList;
import java.util.List;

final public class FilterAttachableImpl<E> implements FilterAttachable<E> {

    @SuppressWarnings("unchecked")
    COWArrayList<Filter<E>> filterList = new COWArrayList<>(new Filter[0]);

    public void addFilter(Filter<E> newFilter) {
        filterList.add(newFilter);
    }

    public void clearAllFilters() {
        filterList.clear();
    }

    public FilterReply getFilterChainDecision(E event) {

        final Filter<E>[] filterArray = filterList.asTypedArray();
        final int len = filterArray.length;

        for (int i = 0; i < len; i++) {
            final FilterReply r = filterArray[i].decide(event);
            if (r == FilterReply.DENY || r == FilterReply.ACCEPT) {
                return r;
            }
        }

        return FilterReply.NEUTRAL;
    }

    public List<Filter<E>> getCopyOfAttachedFiltersList() {
        return new ArrayList<>(filterList);
    }

}