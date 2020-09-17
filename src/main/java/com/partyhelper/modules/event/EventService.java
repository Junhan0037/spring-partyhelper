package com.partyhelper.modules.event;

import com.partyhelper.modules.account.domain.Account;
import com.partyhelper.modules.event.domain.Enrollment;
import com.partyhelper.modules.event.domain.Event;
import com.partyhelper.modules.event.event.*;
import com.partyhelper.modules.event.form.EventForm;
import com.partyhelper.modules.event.form.EventUpdateForm;
import com.partyhelper.modules.settings.domain.Tag;
import com.partyhelper.modules.settings.domain.Zone;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EnrollmentRepository enrollmentRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void addNotification(Event event) {
        eventPublisher.publishEvent(new EventCreatedEvent(event));
    }

    public Event createEvent(Event event, Account account) {
        event.setCreatedBy(account);
        return eventRepository.save(event);
    }

    public void updateEvent(Event event, EventUpdateForm eventUpdateForm) {
        modelMapper.map(eventUpdateForm, event);
        event.acceptWaitingList(); // 모집인원을 늘릴 경우, 자동으로 대기 인원을 확정 상태로 변경해야한다.
        eventPublisher.publishEvent(new EventUpdateEvent(event, "해당 파티를 수정했습니다."));
    }

    public void deleteEvent(Event event) {
        eventPublisher.publishEvent(new EventUpdateEvent(event, "해당 파티를 취소했습니다."));
        eventRepository.delete(event);
    }

    public void newEnrollment(Event event, Account account) {
        if (!enrollmentRepository.existsByEventAndAccount(event, account)) {
            Enrollment enrollment = new Enrollment();
            enrollment.setEnrolledAt(LocalDateTime.now());
            enrollment.setAccepted(event.isAbleToAcceptWaitingEnrollment());
            enrollment.setAccount(account);
            event.addEnrollment(enrollment);
            enrollmentRepository.save(enrollment);

            eventPublisher.publishEvent(new EnrollmentExistingEvent(enrollment));
            if (enrollment.isAccepted()) { // 선착순으로 바로 확정되었을 시
                eventPublisher.publishEvent(new EnrollmentAcceptedEvent(enrollment));
            }
        }
    }

    public void cancelEnrollment(Event event, Account account) {
        Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event, account);
        event.removeEnrollment(enrollment); // 관계 끊기
        enrollmentRepository.delete(enrollment); // 삭제
        event.acceptNextWaitingEnrollment(); // 대기상태를 확정상태로 변환
    }

    public void acceptEnrollment(Event event, Enrollment enrollment) {
        event.accept(enrollment);
        eventPublisher.publishEvent(new EnrollmentAcceptedEvent(enrollment));
    }

    public void rejectEnrollment(Event event, Enrollment enrollment) {
        event.reject(enrollment);
        eventPublisher.publishEvent(new EnrollmentRejectedEvent(enrollment));
    }

    public void addTag(Event event, Tag tag) {
        event.getTags().add(tag);
    }

    public void removeTag(Event event, Tag tag) {
        event.getTags().remove(tag);
    }

    public void addZone(Event event, Zone zone) {
        event.getZones().add(zone);
    }

    public void removeZone(Event event, Zone zone) {
        event.getZones().remove(zone);
    }

    public void addMember(Event event, Account account) {
        event.addMember(account);
    }

    public void removeMember(Event event, Account account) {
        event.removeMember(account);
    }

    public int getEnrollmentSize(Event event) {
        return event.getEnrollments().size();
    }

}
