package com.jadli.bookstore.level1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class IsbnUtilTest {

    @Test
    public void acceptsValidIsbn() {
        assertTrue(IsbnUtil.isValidIsbn13("9780132350884"));
    }

    @Test
    public void rejectsWrongChecksum() {
        assertFalse(IsbnUtil.isValidIsbn13("9780132350885"));
    }

    @Test
    public void rejectsNullAndShortValues() {
        assertFalse(IsbnUtil.isValidIsbn13(null));
        assertFalse(IsbnUtil.isValidIsbn13("123"));
    }

    @Test
    public void normalizeStripsDashes() {
        assertEquals("9780132350884", IsbnUtil.normalize(" 978-0-13-235088-4 "));
        assertEquals("", IsbnUtil.normalize(null));
    }
}
