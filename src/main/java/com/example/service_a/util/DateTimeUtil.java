/**
 * Utility class for generating timestamps in the e-procurement system.
 * Provides methods to obtain formatted timestamps for use in ID generation.
 */
package com.example.service_a.util;

import java.time.LocalDateTime;


public class DateTimeUtil {

    /**
     * Generates a timestamp representing the current date and time.
     * @return          - A formatted timestamp as a String.
     */
    public static String getTimeStamp() {
        return LocalDateTime.now().toString();
    }
}