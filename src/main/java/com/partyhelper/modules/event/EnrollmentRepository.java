package com.partyhelper.modules.event;

import com.partyhelper.modules.account.domain.Account;
import com.partyhelper.modules.event.domain.Enrollment;
import com.partyhelper.modules.event.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByEventAndAccount(Event event, Account account);

    Enrollment findByEventAndAccount(Event event, Account account);

}
