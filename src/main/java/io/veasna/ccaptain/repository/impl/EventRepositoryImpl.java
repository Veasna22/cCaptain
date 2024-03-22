package io.veasna.ccaptain.repository.impl;

import io.veasna.ccaptain.domain.UserEvent;
import io.veasna.ccaptain.enumeration.EventType;
import io.veasna.ccaptain.repository.EventRepository;
import io.veasna.ccaptain.rowmapper.UserEventRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import static java.util.Map.of;
import java.util.Collection;

import static io.veasna.ccaptain.query.EventQuery.INSERT_EVENT_BY_USER_EMAIL_QUERY;
import static io.veasna.ccaptain.query.EventQuery.SELECT_EVENTS_BY_USER_ID_QUERY;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 22/3/24 08:50
 */

@Repository
@RequiredArgsConstructor
@Slf4j
public class EventRepositoryImpl implements EventRepository {
    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Collection<UserEvent> getEventsByUserId(Long userId) {
        return jdbc.query(SELECT_EVENTS_BY_USER_ID_QUERY, of("id", userId), new UserEventRowMapper());
    }

    @Override
    public void addUserEvent(String email, EventType eventType, String device, String ipAddress) {
        jdbc.update(INSERT_EVENT_BY_USER_EMAIL_QUERY, of("email", email, "type", eventType.toString(), "device", device, "ipAddress", ipAddress));
    }

    @Override
    public void addUserEvent(Long userId, EventType eventType, String device, String ipAddress) {

    }
}
