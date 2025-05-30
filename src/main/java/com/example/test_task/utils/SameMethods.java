package com.example.test_task.utils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class SameMethods {

    public static String urlDecode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    public static Map<String, String> parseInitData(String initData) {
        return Arrays.stream(initData.split("&"))
                .map(entry -> entry.split("=", 2))
                .collect(Collectors.toMap(
                        arr -> arr[0],
                        arr -> arr.length > 1 ? urlDecode(arr[1]) : ""
                ));
    }
}
