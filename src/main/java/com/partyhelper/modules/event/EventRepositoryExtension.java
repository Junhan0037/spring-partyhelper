package com.partyhelper.modules.event;

import com.partyhelper.modules.event.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface EventRepositoryExtension {

    Page<Event> findByKeyword(String keyword, Pageable pageable);

}
