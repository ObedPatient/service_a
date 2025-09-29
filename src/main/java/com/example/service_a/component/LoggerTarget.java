/**
 * Manages logging observers for different log levels and notifies them with log messages.
 * @author Obed Patient
 * @version 1.0
 * @since 1.0
 */
package com.example.service_a.component;

import com.example.service_a.util.logging.observer.ILogObserver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoggerTarget {
    private final Map<String, List<ILogObserver>> logObservers = new ConcurrentHashMap<>();

    /**
     * Adds an observer for a specific log level.
     * @param level the log level to observe
     * @param observer the observer to add
     */
    public void addObserver(String level, ILogObserver observer) {
        logObservers.computeIfAbsent(level, k -> Collections.synchronizedList(new ArrayList<>())).add(observer);
    }

    /**
     * Notifies all observers for a specific log level with a message.
     * @param level the log level to notify
     * @param message the message to send to observers
     * @throws Exception if an error occurs during notification
     */
    public void notifyObservers(String level, String message) throws Exception {
        List<ILogObserver> observers = logObservers.get(level);
        if (observers != null) {
            for (ILogObserver observer : observers) {
                if (observer != null) {
                    observer.log(message);
                } else {
                    // Optionally log a warning for debugging
                    System.err.println("Null observer found for level: " + level);
                }
            }
        }
    }
}