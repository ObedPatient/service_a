/**
 * Provides constant values for log levels, archive strategies,
 * and metadata types used across the service.
 *
 * @author Obed Patient
 * @version 1.0
 * @since 1.0
 */
package com.example.service_a.util;

import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;

public class ServiceConstant {

    public static final String SERVER_USER = System.getProperty("user.name");

    /**
     * List of valid log levels that can be used for logging.
     */
    public static final List<String> VALID_LOG_LEVELS = Arrays.asList(
            "INFO",
            "ERROR",
            "WARNING",
            "DEBUG"
    );

    /**
     * List of valid archive strategies for handling logs.
     */
    public static final List<String> VALID_ARCHIVE_STRATEGIES = Arrays.asList(
            "DELETE",
            "ARCHIVE",
            "NEVER"
    );

    /**
     * List of valid metadata types associated with logs.
     */
    public static final List<String> VALID_METADATA_TYPES = Arrays.asList(
            "QUERY",
            "STACKTRACE",
            "OBJECT",
            "MESSAGE",
            "ERROR MESSAGE"
    );

    /**
     * Archive time in minutes for ERROR log level.
     */
    public static final int ARCHIVE_TIME_ERROR = 3;      // days
    /**
     * Archive time in minutes for WARNING log level.
     */
    public static final int ARCHIVE_TIME_WARNING = 2;    // days
    /**
     * Default archive time in minutes for INFO and DEBUG log levels.
     */
    public static final int ARCHIVE_TIME_DEFAULT = 1;
}