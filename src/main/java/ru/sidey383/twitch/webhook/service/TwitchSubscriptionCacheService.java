package ru.sidey383.twitch.webhook.service;

import com.github.twitch4j.eventsub.EventSubSubscription;
import com.github.twitch4j.eventsub.condition.EventSubCondition;
import com.github.twitch4j.eventsub.subscriptions.SubscriptionType;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class TwitchSubscriptionCacheService {

    private final Map<String, EventSubSubscription> cacheById = new ConcurrentHashMap<>();
    private final Map<SubscriptionType<?, ?, ?>, Set<EventSubSubscription>> cacheByType = new ConcurrentHashMap<>();

    synchronized void put(EventSubSubscription subscription) {
        if (subscription != null && subscription.getId() != null && subscription.getType() != null) {
            cacheById.put(subscription.getId(), subscription);
            cacheByType.computeIfAbsent(subscription.getType(), k -> ConcurrentHashMap.newKeySet()).add(subscription);
        }
    }

    synchronized void remove(String id) {
        EventSubSubscription sub = cacheById.remove(id);
        if (sub != null && sub.getType() != null) {
            cacheByType.get(sub.getType()).removeIf(c -> c.getId().equals(id));
        }
    }

    public EventSubSubscription getById(String id) {
        return cacheById.get(id);
    }


    public Collection<EventSubSubscription> findByType(@Nonnull SubscriptionType<?, ?, ?> type) {
        return Collections.unmodifiableSet(cacheByType.getOrDefault(type, Collections.emptySet()));
    }

    public <C extends EventSubCondition, B, S extends SubscriptionType<C, B, ?>>
    List<EventSubSubscription> findByTypeAndConditionFilter(
            @Nonnull
            S type,
            @Nonnull
            Predicate<? super C> conditionFilter
    ) {
        Collection<EventSubSubscription> byType = findByType(type);
        return byType.stream()
                .filter(sub -> {
                    try {
                        @SuppressWarnings("unchecked")
                        C cond = (C) sub.getCondition();
                        return conditionFilter.test(cond);
                    } catch (ClassCastException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }
}