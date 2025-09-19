/**
 * Interface for logging observers that handle log messages.
 * @author Obed Patient
 * @version 1.0
 * @since 1.0
 */
package com.example.service_a.util.logging.observer;

public interface ILogObserver {
    /**
     * Logs a message.
     * @param message the message to be logged
     * @throws Exception if an error occurs during logging
     */
    void log(String message) throws Exception;
}