package com.partyhelper.modules.event.event;

import com.partyhelper.modules.event.domain.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EventCreatedEvent {

    private final Event event;

}
