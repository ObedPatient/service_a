package com.example.service_a.util.logging.target;



import com.example.service_a.util.logging.observer.ILogObserver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Manages observers of log levels
@Component
public class LoggerTarget {
    private final Map<String, List<ILogObserver>> logObservers = new ConcurrentHashMap<>();

    public void addObserver(String level, ILogObserver observer) {
        logObservers.computeIfAbsent(level, k -> Collections.synchronizedList(new ArrayList<>())).add(observer);
    }

    public void removeObserver(String level, ILogObserver observer) {
        logObservers.values().forEach(list -> list.remove(observer));
    }

    public void notifyObservers(String level, String message) throws Exception {
        List<ILogObserver> observers = logObservers.get(level);
        if (observers != null) {
            for (ILogObserver observer : observers) {
                observer.log(message);
            }
        }
    }
}