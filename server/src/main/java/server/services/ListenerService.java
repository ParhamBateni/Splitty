package server.services;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;

@Service
public class ListenerService {
    private ConcurrentMap<Long, Set<Consumer<Object>>> listeners;

    public ListenerService() {
        listeners = new ConcurrentHashMap<>();
    }

    public void addListener(Long eventId, Consumer<Object> consumer) {
        listeners.computeIfAbsent(eventId, k -> new ConcurrentSkipListSet<>(
                Comparator.comparingInt(System::identityHashCode))).add(consumer);
    }

    public void removeListener(Long eventId, Consumer<Object> consumer) {
        listeners.get(eventId).remove(consumer);
    }

    public void notify(Long eventId) {
        if (!listeners.containsKey(eventId)) {
            return;
        }
        for (var consumer : listeners.get(eventId)) {
            consumer.accept(null);
        }
    }
}
