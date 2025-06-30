package ru.sidey383.twitch.utils;

import java.util.Objects;

public final class StringUtils {

    private StringUtils() {}

    public static String normalize(String s) {
        return (s == null || s.isEmpty()) ? "" : s;
    }

    public static boolean normalizeEquals(String s1, String s2) {
        return Objects.equals(normalize(s1), normalize(s2));
    }

}
