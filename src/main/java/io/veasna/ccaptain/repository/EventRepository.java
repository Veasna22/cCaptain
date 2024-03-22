package io.veasna.ccaptain.repository;

import io.veasna.ccaptain.domain.UserEvent;
import io.veasna.ccaptain.enumeration.EventType;

import java.util.Collection;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 22/3/24 08:49
 */
public interface EventRepository {
    Collection<UserEvent> getEventsByUserId(Long userId);
    void addUserEvent(String email, EventType eventType, String device , String ipAddress);
    void addUserEvent(Long userId, EventType eventType, String device , String ipAddress);

}
