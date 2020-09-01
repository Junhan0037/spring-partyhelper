package com.partyhelper.modules.event;

import com.partyhelper.modules.account.annotation.CurrentAccount;
import com.partyhelper.modules.account.domain.Account;
import com.partyhelper.modules.event.domain.Event;
import com.partyhelper.modules.event.form.EventForm;
import com.partyhelper.modules.event.validator.EventValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;
    private final EventRepository eventRepository;

    @InitBinder("eventform")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventValidator);
    }


    @GetMapping("/new-event")
    public String newEventForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new EventForm());
        return "event/form";
    }

    @PostMapping("/new-event")
    public String newEventSubmit(@CurrentAccount Account account, @Valid EventForm eventForm, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "event/form";
        }

        Event event = eventService.createEvent(modelMapper.map(eventForm, Event.class), account);
        return "redirect:/event/" + URLEncoder.encode(event.getPath(), StandardCharsets.UTF_8);
    }

    @GetMapping("/event/{path}")
    public String getEvent(@CurrentAccount Account account, @PathVariable String path, Model model) {
        model.addAttribute(account);
        model.addAttribute(eventRepository.findByPath(path));
        return "event/view";
    }

    @GetMapping("/event/{path}/edit")
    public String updateEventForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Event event = eventRepository.findByPath(path);
        model.addAttribute(event);
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(event, EventForm.class));
        return "event/update-form";
    }

    @PostMapping("/event/{path}/edit")
    public String updateEventSubmit(@CurrentAccount Account account, @PathVariable String path, @Valid EventForm eventForm,
                                    Errors errors, Model model) {
        Event event = eventRepository.findByPath(path);
        eventForm.setEventType(event.getEventType()); // 기존의 모집 방식 유지
        eventValidator.validateUpdateForm(eventForm, event, errors);

        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(event);
            return "event/update-form";
        }

        eventService.updateEvent(event, eventForm);
        return "redirect:/event/" + event.getPath();
    }

    @PostMapping("/event/{path}/delete")
    public String cancelEvent(@CurrentAccount Account account, @PathVariable String path) {
        Event event = eventRepository.findByPath(path);
        eventService.deleteEvent(event);
        return "redirect:/";
    }

    @PostMapping("/event/{path}/enroll")
    public String newEnrollment(@CurrentAccount Account account, @PathVariable String path) { // 참가 신청
        Event event = eventRepository.findByPath(path);
        eventService.newEnrollment(event, account);
        return "redirect:/event/" + event.getPath();
    }

    @PostMapping("/event/{path}/disenroll")
    public String cancelEnrollment(@CurrentAccount Account account, @PathVariable String path) { // 참가 취소
        Event event = eventRepository.findByPath(path);
        eventService.cancelEnrollment(event, account);
        return "redirect:/event/" + event.getPath();
    }

}
