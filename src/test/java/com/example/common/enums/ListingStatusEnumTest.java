package com.example.common.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ListingStatusEnumTest {

    @Test
    void normalisesLegacyAndCanonicalListedValues() {
        assertEquals("Listed", ListingStatusEnum.normalise("Listed"));
        assertEquals("Listed", ListingStatusEnum.normalise("On-shelf"));
        assertEquals("Listed", ListingStatusEnum.normalise("上架"));
    }

    @Test
    void normalisesLegacyAndCanonicalOffShelfValues() {
        assertEquals("Off-shelf", ListingStatusEnum.normalise("Off-shelf"));
        assertEquals("Off-shelf", ListingStatusEnum.normalise("Unlisted"));
        assertEquals("Off-shelf", ListingStatusEnum.normalise("下架"));
    }

    @Test
    void rejectsUnknownValuesWithoutChangingMissingOptionalUpdates() {
        assertNull(ListingStatusEnum.normalise(null));
        assertThrows(IllegalArgumentException.class,
                () -> ListingStatusEnum.normalise("Reserved"));
    }
}
