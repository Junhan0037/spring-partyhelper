package com.partyhelper.modules.event;

import com.partyhelper.modules.account.domain.Account;
import com.partyhelper.modules.event.domain.Event;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event, Long>, EventRepositoryExtension {

    Event findByPath(String path);

    @EntityGraph(attributePaths = {"zones", "tags"})
    List<Event> findFirst9ByOrderByStartDateTime();

    List<Event> findFirst5ByCreatedByOrderByStartDateTime(Account account);

    List<Event> findFirst5ByMembersContainingOrderByStartDateTime(Account account);

}
