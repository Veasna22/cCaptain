package io.veasna.ccaptain.event;

import io.veasna.ccaptain.enumeration.EventType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 22/3/24 09:15
 */

@Getter
@Setter
public class NewUserEvent extends ApplicationEvent {
    private EventType type;

    private String email;

    public NewUserEvent( String email,EventType type ) {
        super(email);
        this.type = type;
        this.email = email;
    }




}
