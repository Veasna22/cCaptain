package io.veasna.ccaptain.service;

import io.veasna.ccaptain.domain.UserEvent;
import io.veasna.ccaptain.enumeration.EventType;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 18/12/23 20:55
 */
public interface EventService {
    Collection<UserEvent> getEventsByUserId(Long userId);
    void addUserEvent(String email, EventType eventType, String device, String ipAddress);
    void addUserEvent(Long userId, EventType eventType, String device, String ipAddress);
}

