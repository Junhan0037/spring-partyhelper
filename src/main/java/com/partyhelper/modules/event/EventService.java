package com.partyhelper.modules.event;

import com.partyhelper.modules.account.domain.Account;
import com.partyhelper.modules.event.domain.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Event createEvent(Event event, Account account) {
        event.setCreatedBy(account);
        return eventRepository.save(event);
    }

}
