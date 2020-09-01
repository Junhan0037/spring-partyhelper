package com.partyhelper.modules.event;

import com.partyhelper.modules.event.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event, Long> {

    Event findByPath(String path);

}
