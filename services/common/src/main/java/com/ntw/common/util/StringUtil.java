package com.ntw.common.util;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringUtil {
    public static String capitalize(String sentence) {
        return Stream.of(sentence.trim().split("\\s"))
                .filter(word -> word.length() > 0)
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                .collect(Collectors.joining(" "));
    }
}
