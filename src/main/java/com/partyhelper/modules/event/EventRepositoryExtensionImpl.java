package com.partyhelper.modules.event;

import com.partyhelper.modules.account.domain.Account;
import com.partyhelper.modules.event.domain.Enrollment;
import com.partyhelper.modules.event.domain.Event;
import com.partyhelper.modules.event.domain.QEnrollment;
import com.partyhelper.modules.event.domain.QEvent;
import com.partyhelper.modules.settings.domain.QTag;
import com.partyhelper.modules.settings.domain.QZone;
import com.partyhelper.modules.settings.domain.Tag;
import com.partyhelper.modules.settings.domain.Zone;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Set;

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

    @Override
    public List<Event> findByTag(Set<Tag> tags) {
        QEvent event = QEvent.event;
        JPQLQuery<Event> query = from(event).where(
                event.tags.any().in(tags))
                .leftJoin(event.tags, QTag.tag).fetchJoin()
                .orderBy(event.startDateTime.asc())
                .distinct()
                .limit(9);
        return query.fetch();
    }

    @Override
    public List<Event> findByZone(Set<Zone> zones) {
        QEvent event = QEvent.event;
        JPQLQuery<Event> query = from(event).where(
                event.zones.any().in(zones))
                .leftJoin(event.zones, QZone.zone).fetchJoin()
                .orderBy(event.startDateTime.asc())
                .distinct()
                .limit(9);
        return query.fetch();
    }

    @Override
    public List<Event> findByEnrolledEvent(Account account, Set<Enrollment> enrollments) {
        QEvent event = QEvent.event;
        JPQLQuery<Event> query = from(event).where(
                event.enrollments.any().in(enrollments)
                .and(event.createdBy.eq(account)))
                .leftJoin(event.enrollments, QEnrollment.enrollment).fetchJoin()
                .orderBy(event.startDateTime.asc())
                .distinct()
                .limit(9);
        return query.fetch();
    }

    @Override
    public List<Event> findByEnrollingEvent(Set<Enrollment> enrollments) {
        QEvent event = QEvent.event;
        JPQLQuery<Event> query = from(event).where(
                event.enrollments.any().in(enrollments))
                .leftJoin(event.enrollments, QEnrollment.enrollment).fetchJoin()
                .orderBy(event.startDateTime.asc())
                .distinct()
                .limit(9);
        return query.fetch();
    }

    //    @Override
//    public List<Event> findByAccount(Set<Tag> tags, Set<Zone> zones) {
//        QEvent event = QEvent.event;
//        JPQLQuery<Event> query = from(event).where(event.tags.any().in(tags)
//                .and(event.zones.any().in(zones)))
//                .leftJoin(event.tags, QTag.tag).fetchJoin()
//                .leftJoin(event.zones, QZone.zone).fetchJoin()
//                .orderBy(event.startDateTime.asc())
//                .distinct()
//                .limit(9);
//        return query.fetch();
//    }

}
