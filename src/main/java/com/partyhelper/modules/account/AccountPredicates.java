package com.partyhelper.modules.account;

import com.partyhelper.modules.account.domain.QAccount;
import com.partyhelper.modules.settings.domain.Tag;
import com.partyhelper.modules.settings.domain.Zone;
import com.querydsl.core.types.Predicate;

import java.util.Set;

public class AccountPredicates { // Querydsl

    public static Predicate findByTagsAndZones(Set<Tag> tags, Set<Zone> zones) {
        QAccount account = QAccount.account;
        return account.zones.any().in(zones).and(account.tags.any().in(tags));
    }

}
