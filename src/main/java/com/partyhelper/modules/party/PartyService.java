package com.partyhelper.modules.party;

import com.partyhelper.modules.account.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PartyService {

    private final PartyRepository repository;

    public Party createNewParty(Party party, Account account) {
        Party newParty = repository.save(party);
        newParty.addManager(account);
        return newParty;
    }

}
