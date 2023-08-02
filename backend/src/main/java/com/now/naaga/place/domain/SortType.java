package com.now.naaga.place.domain;

import com.now.naaga.common.domain.OrderType;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;

public enum SortType {
    TIME ((places, orderType) -> {
        if (orderType == OrderType.ASCENDING) {
            places.sort(Comparator.comparing(Place::getCreatedAt));
        }
        if (orderType == OrderType.DESCENDING) {
            places.sort(Comparator.comparing(Place::getCreatedAt).reversed());
        }
    }),
    ;

    final BiConsumer<List<Place>, OrderType> sorting;

    SortType(final BiConsumer<List<Place>, OrderType> sorting) {
        this.sorting = sorting;
    }

    public void sort(final List<Place> places,
                     final OrderType orderType) {
        sorting.accept(places, orderType);
    }
}
