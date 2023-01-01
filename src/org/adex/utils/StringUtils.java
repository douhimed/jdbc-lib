package org.adex.utils;

import java.util.Objects;

public final class StringUtils {

    public static final String EMPTY = "";

    public static boolean isBlank(String value) {
        return Objects.isNull(value) || value.trim().length() == 0;
    }

}
