package com.partyhelper.modules.party;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface PartyRepository extends JpaRepository<Party, Long> {

    boolean existsByPath(String path);

}
