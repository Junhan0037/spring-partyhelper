package com.partyhelper.modules.event;

import com.partyhelper.modules.account.domain.Account;
import com.partyhelper.modules.event.domain.Event;
import com.partyhelper.modules.event.form.EventForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

    public Event createEvent(Event event, Account account) {
        event.setCreatedBy(account);
        return eventRepository.save(event);
    }

    public void updateEvent(Event event, EventForm eventForm) {
        modelMapper.map(eventForm, event);
        event.acceptWaitingList(); // 모집인원을 늘릴 경우, 자동으로 대기 인원을 확정 상태로 변경해야한다.
//        eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(), "'" + event.getTitle() + "' 모임 정보를 수정했으니 확인하세요."));
    }

}
