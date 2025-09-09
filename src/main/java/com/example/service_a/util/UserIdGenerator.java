package com.example.service_a.util;

import java.util.Random;

public class UserIdGenerator {
    public static String generateId() {
        return String.format(
                "USR_%s_%s",
                DateTimeUtil.getTimeStamp().replaceAll("[^a-zA-Z0-9]", ""),
                (new Random()).nextLong(1, 10_000_000)
        );
    }
}
