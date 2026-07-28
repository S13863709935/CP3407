package com.example.common.enums;

/**
 * Canonical values persisted in {@code goods.sale_status}.
 *
 * The normaliser keeps the API compatible with legacy UI/database values while
 * ensuring all new writes use one consistent representation.
 */
public enum ListingStatusEnum {
    LISTED("Listed"),
    OFF_SHELF("Off-shelf");

    public final String value;

    ListingStatusEnum(String value) {
        this.value = value;
    }

    public static String normalise(String value) {
        if (value == null) {
            return null;
        }
        String candidate = value.trim();
        if ("Listed".equalsIgnoreCase(candidate)
                || "On-shelf".equalsIgnoreCase(candidate)
                || "On shelf".equalsIgnoreCase(candidate)
                || "上架".equals(candidate)) {
            return LISTED.value;
        }
        if ("Off-shelf".equalsIgnoreCase(candidate)
                || "Off shelf".equalsIgnoreCase(candidate)
                || "Unlisted".equalsIgnoreCase(candidate)
                || "下架".equals(candidate)) {
            return OFF_SHELF.value;
        }
        throw new IllegalArgumentException("Unsupported listing status: " + value);
    }
}
