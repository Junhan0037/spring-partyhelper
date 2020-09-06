package com.partyhelper.modules.event;

import com.partyhelper.modules.account.domain.Account;
import com.partyhelper.modules.event.domain.Enrollment;
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

    List<Event> findByTag(Set<Tag> tags);

    List<Event> findByZone(Set<Zone> zones);

    List<Event> findByEnrolledEvent(Account account, Set<Enrollment> enrollments);

    List<Event> findByEnrollingEvent(Set<Enrollment> enrollments);

}
