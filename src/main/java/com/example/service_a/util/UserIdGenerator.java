/**
 * Utility class for generating unique user IDs.
 * The generated ID includes a timestamp and a random number to ensure uniqueness.
 * @author Obed Patient
 * @version 1.0
 * @since 1.0
 */
package com.example.service_a.util;

import java.util.Random;

public class UserIdGenerator {

    /**
     * Generates a unique user ID based on a timestamp and a random number.
     *
     * @return a String representing the generated user ID
     */
    public static String generateId() {
        return String.format(
                "USR_%s_%s",
                DateTimeUtil.getTimeStamp().replaceAll("[^a-zA-Z0-9]", ""),
                (new Random()).nextLong(1, 10_000_000)
        );
    }
}