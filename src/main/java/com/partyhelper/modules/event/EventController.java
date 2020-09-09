package com.partyhelper.modules.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.partyhelper.modules.account.annotation.CurrentAccount;
import com.partyhelper.modules.account.domain.Account;
import com.partyhelper.modules.event.domain.Enrollment;
import com.partyhelper.modules.event.domain.Event;
import com.partyhelper.modules.event.form.EventForm;
import com.partyhelper.modules.event.validator.EventValidator;
import com.partyhelper.modules.settings.TagRepository;
import com.partyhelper.modules.settings.TagService;
import com.partyhelper.modules.settings.ZoneRepository;
import com.partyhelper.modules.settings.domain.Tag;
import com.partyhelper.modules.settings.domain.Zone;
import com.partyhelper.modules.settings.form.TagForm;
import com.partyhelper.modules.settings.form.ZoneForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.partyhelper.modules.account.Role.PROVIDER;

@Controller
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;
    private final EventRepository eventRepository;
    private final TagRepository tagRepository;
    private final TagService tagService;
    private final ZoneRepository zoneRepository;
    private final ObjectMapper objectMapper;

    @InitBinder("eventForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventValidator);
    }

    @GetMapping("/new-event")
    public String newEventForm(@CurrentAccount Account account, Model model) {
        if (!account.isEmailVerified()) {
            model.addAttribute(account);
            return "/account/check-email";
        }
        if (account.getRole().equals(PROVIDER)) {
            return "/error";
        }
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
        return "redirect:/event/" + event.getEncodedPath();
    }

    @GetMapping("/event/{path}")
    public String getEvent(@CurrentAccount Account account, @PathVariable String path, Model model) throws JsonProcessingException {
        if (!account.isEmailVerified()) {
            model.addAttribute(account);
            return "/account/check-email";
        }
        Event event = eventRepository.findByPath(path);
        model.addAttribute(account);
        model.addAttribute(event);
        model.addAttribute("enrollmentSize", eventService.getEnrollmentSize(event));

        model.addAttribute("tags", event.getTags().stream().map(Tag::getTitle).collect(Collectors.toList()));
        List<String>allTagTitles = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("taglist", objectMapper.writeValueAsString(allTagTitles));

        model.addAttribute("zones", event.getZones().stream().map(Zone::toString).collect(Collectors.toList()));
        List<String>allZones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
        model.addAttribute("zonelist", objectMapper.writeValueAsString(allZones));

        return "event/view";
    }

    @PostMapping("/event/{path}/tags/add")
    public ResponseEntity addTag(@CurrentAccount Account account, @PathVariable String path, @RequestBody TagForm tagForm) {
        Event event = eventRepository.findByPath(path);
        Tag tag = tagService.findOrCreateNew(tagForm.getTagTitle());
        eventService.addTag(event, tag);
        eventService.addNotification(event); // 스터디 개설 알림
        return ResponseEntity.ok().build();
    }

    @PostMapping("/event/{path}/tags/remove")
    @ResponseBody
    public ResponseEntity removeTag(@CurrentAccount Account account, @PathVariable String path, @RequestBody TagForm tagForm) {
        Event event = eventRepository.findByPath(path);
        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle());
        if (tag == null) {
            return ResponseEntity.badRequest().build();
        }
        eventService.removeTag(event, tag);
        eventService.addNotification(event); // 스터디 개설 알림
        return ResponseEntity.ok().build();
    }

    @PostMapping("/event/{path}/zones/add")
    public ResponseEntity addZone(@CurrentAccount Account account, @PathVariable String path, @RequestBody ZoneForm zoneForm) {
        Event event = eventRepository.findByPath(path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }
        eventService.addZone(event, zone);
        eventService.addNotification(event); // 스터디 개설 알림
        return ResponseEntity.ok().build();
    }

    @PostMapping("/event/{path}/zones/remove")
    public ResponseEntity removeZone(@CurrentAccount Account account, @PathVariable String path, @RequestBody ZoneForm zoneForm) {
        Event event = eventRepository.findByPath(path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }
        eventService.removeZone(event, zone);
        eventService.addNotification(event); // 스터디 개설 알림
        return ResponseEntity.ok().build();
    }

    @GetMapping("/event/{path}/edit")
    public String updateEventForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Event event = eventRepository.findByPath(path);
        if (event.getCreatedBy().getId() != account.getId()) {
            return "/error";
        }
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
        return "redirect:/event/" + event.getEncodedPath();
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
//        eventService.addMember(event, account);
        eventService.newEnrollment(event, account);
        return "redirect:/event/" + event.getEncodedPath();
    }

    @PostMapping("/event/{path}/disenroll")
    public String cancelEnrollment(@CurrentAccount Account account, @PathVariable String path) { // 참가 취소
        Event event = eventRepository.findByPath(path);
//        eventService.removeMember(event, account);
        eventService.cancelEnrollment(event, account);
        return "redirect:/event/" + event.getEncodedPath();
    }

    @PostMapping("/event/{path}/enrollment/{enrollmentId}/accept")
    public String acceptEnrollment(@CurrentAccount Account account, @PathVariable String path, @PathVariable("enrollmentId") Enrollment enrollment) {
        Event event = eventRepository.findByPath(path);
        eventService.acceptEnrollment(event, enrollment);
        return "redirect:/event/" + event.getEncodedPath();
    }

    @PostMapping("/event/{path}/enrollment/{enrollmentId}/reject")
    public String rejectEnrollment(@CurrentAccount Account account, @PathVariable String path, @PathVariable("enrollmentId") Enrollment enrollment) {
        Event event = eventRepository.findByPath(path);
        eventService.rejectEnrollment(event, enrollment);
        return "redirect:/event/" + event.getEncodedPath();
    }



}
