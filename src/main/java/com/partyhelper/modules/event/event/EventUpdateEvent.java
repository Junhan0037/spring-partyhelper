package com.partyhelper.modules.event.event;

import com.partyhelper.modules.event.domain.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EventUpdateEvent {

    private final Event event;

    private final String message;

}
