package com.partyhelper.modules.event.validator;

import com.partyhelper.modules.event.EventRepository;
import com.partyhelper.modules.event.domain.Event;
import com.partyhelper.modules.event.form.EventUpdateForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EventUpdateValidator implements Validator {

    private final EventRepository eventRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return EventUpdateForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EventUpdateForm eventUpdateForm = (EventUpdateForm) target;
        if (isNotValidEndDateTime(eventUpdateForm)) {
            errors.rejectValue("endDateTime", "wrong.datetime", "모임 종료 일시를 정확히 입력하세요.");
        }
        if (isNotValidStartDateTime(eventUpdateForm)) {
            errors.rejectValue("startDateTime", "wrong.datetime", "모임 시작 일시를 정확히 입력하세요.");
        }
    }

    private boolean isNotValidStartDateTime(EventUpdateForm eventForm) {
        return eventForm.getStartDateTime().isBefore(LocalDateTime.now());
    }

    private boolean isNotValidEndDateTime(EventUpdateForm eventForm) {
        LocalDateTime endDateTime = eventForm.getEndDateTime();
        return endDateTime.isBefore(eventForm.getStartDateTime());
    }

    public void validateUpdateForm(EventUpdateForm eventForm, Event event, Errors errors) {
        if (eventForm.getLimitOfEnrollments() < event.getNumberOfAcceptedEnrollments()) {
            errors.rejectValue("limitOfEnrollments", "wrong.value", "확인된 참가 신청 업체 수 보다 모집 업체 수가 커야 합니다.");
        }
    }

}
