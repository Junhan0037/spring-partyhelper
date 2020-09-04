package com.partyhelper.modules.event;

import com.partyhelper.modules.event.domain.Event;
import com.partyhelper.modules.settings.domain.Tag;
import com.partyhelper.modules.settings.domain.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public interface EventRepositoryExtension {

    Page<Event> findByKeyword(String keyword, Pageable pageable);

    List<Event> findByAccount(Set<Tag> tags, Set<Zone> zones);

}
