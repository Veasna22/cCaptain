package io.veasna.ccaptain.listener;

import io.veasna.ccaptain.event.NewUserEvent;
import io.veasna.ccaptain.service.EventService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static io.veasna.ccaptain.utils.RequestUtils.getDevice;
import static io.veasna.ccaptain.utils.RequestUtils.getIpAddress;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 22/3/24 09:18
 */
@Component
@RequiredArgsConstructor
public class NewUserEventListener {
    private final EventService eventService;
    private final HttpServletRequest request;

    @EventListener
    public void onNewUserEvent(NewUserEvent event) {
        eventService.addUserEvent(event.getEmail(), event.getType(), getDevice(request), getIpAddress(request));
    }
}
