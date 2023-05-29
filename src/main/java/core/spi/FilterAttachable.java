package core.spi;

import core.filter.Filter;

import java.util.List;

public interface FilterAttachable<E> {

    void addFilter(Filter<E> newFilter);

    void clearAllFilters();

    List<Filter<E>> getCopyOfAttachedFiltersList();

    FilterReply getFilterChainDecision(E event);
}
