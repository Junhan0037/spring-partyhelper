package com.partyhelper.modules.event;

import com.partyhelper.modules.event.domain.Event;
import com.partyhelper.modules.event.domain.QEvent;
import com.partyhelper.modules.settings.domain.QTag;
import com.partyhelper.modules.settings.domain.QZone;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class EventRepositoryExtensionImpl extends QuerydslRepositorySupport implements EventRepositoryExtension {

    public EventRepositoryExtensionImpl() {
        super(Event.class);
    }

    @Override
    public Page<Event> findByKeyword(String keyword, Pageable pageable) {
        QEvent event = QEvent.event;
//        JPQLQuery<Event> query = from(event).where(event.title.containsIgnoreCase(keyword));
        JPQLQuery<Event> query = from(event).where(event.title.containsIgnoreCase(keyword)
                .or(event.tags.any().title.containsIgnoreCase(keyword))
                .or(event.zones.any().localNameOfCity.containsIgnoreCase(keyword)))
                .leftJoin(event.tags, QTag.tag).fetchJoin()
                .leftJoin(event.zones, QZone.zone).fetchJoin()
                .distinct();
        JPQLQuery<Event> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Event> fetchResults = pageableQuery.fetchResults();
        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());
    }

}
