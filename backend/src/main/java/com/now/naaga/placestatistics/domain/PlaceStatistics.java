package com.now.naaga.placestatistics.domain;

import com.now.naaga.common.domain.BaseEntity;
import com.now.naaga.place.domain.Place;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class PlaceStatistics extends BaseEntity {

    public static final long LIKE_COUNT_DEFAULT_VALUE = 0L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    private Long likeCount;

    protected PlaceStatistics() {

    }

    public PlaceStatistics(final Place place,
                           final Long likeCount) {
        this(null, place, likeCount);
    }


    public PlaceStatistics(final Long id,
                           final Place place,
                           final Long likeCount) {
        this.id = id;
        this.place = place;
        this.likeCount = likeCount;
    }

    public void subtractLike() {
        likeCount--;
        resetIfUnderZeroValue();
    }

    private void resetIfUnderZeroValue() {
        if (likeCount < 0) {
            likeCount = LIKE_COUNT_DEFAULT_VALUE;
        }
    }

    public Long getId() {
        return id;
    }

    public Place getPlace() {
        return place;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PlaceStatistics that = (PlaceStatistics) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PlaceStatistics{" +
                "id=" + id +
                ", placeId=" + place.getId() +
                ", likeCount=" + likeCount +
                '}';
    }
}
