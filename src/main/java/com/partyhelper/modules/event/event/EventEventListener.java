package com.partyhelper.modules.event.event;

import com.partyhelper.infra.config.AppProperties;
import com.partyhelper.infra.mail.EmailMessage;
import com.partyhelper.infra.mail.EmailService;
import com.partyhelper.modules.account.AccountPredicates;
import com.partyhelper.modules.account.AccountRepository;
import com.partyhelper.modules.account.domain.Account;
import com.partyhelper.modules.event.EnrollmentRepository;
import com.partyhelper.modules.event.EventRepository;
import com.partyhelper.modules.event.domain.Enrollment;
import com.partyhelper.modules.event.domain.Event;
import com.partyhelper.modules.notification.NotificationRepository;
import com.partyhelper.modules.notification.NotificationType;
import com.partyhelper.modules.notification.domain.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class EventEventListener { // 비동기적 처리

    private final EventRepository eventRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleEventCreatedEvent(EventCreatedEvent eventCreatedEvent) {
        Event event = eventRepository.findEventWithTagsAndZonesById(eventCreatedEvent.getEvent().getId());
        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByTagsAndZones(event.getTags(), event.getZones()));
        accounts.forEach(account -> {
            if (account.isEventCreatedByEmail()) {
                sendEventCreatedEmail(event, account, "관련된 파티가 존재합니다.", "PartyHelper, '" + event.getTitle() + "' 파티를 추천합니다.");
            }

            if (account.isEventCreatedByWeb()) {
                createNotification(event, account, "관련된 파티가 존재합니다.", NotificationType.EVENT_CREATED);
            }
        });
    }

    @EventListener
    public void handleEventUpdateEvent(EventUpdateEvent eventUpdateEvent) {
        Event event = eventRepository.findEventById(eventUpdateEvent.getEvent().getId());
        Set<Account> accounts = new HashSet<>();
        accounts.add(event.getCreatedBy());
        event.getEnrollments().forEach(enrollment -> accounts.add(enrollment.getAccount()));

        accounts.forEach(account -> {
            if (account.isEventUpdatedByEmail()) {
                sendEventCreatedEmail(event, account, eventUpdateEvent.getMessage(), "PartyHelper, '" + event.getTitle() + "' 파티에 새소식이 있습니다.");
            }

            if (account.isEventUpdatedByWeb()) {
                createNotification(event, account, eventUpdateEvent.getMessage(), NotificationType.EVENT_UPDATED);
            }
        });
    }

    private void sendEventCreatedEmail(Event event, Account account, String contextMessage, String emailSubject) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/event/" + event.getEncodedPath());
        context.setVariable("linkName", event.getTitle());
        context.setVariable("message", contextMessage);
        context.setVariable("host", appProperties.getHost()); // 오류 조심
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject(emailSubject)
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

    private void createNotification(Event event, Account account, String message, NotificationType notificationType) {
        Notification notification = new Notification();
        notification.setTitle(event.getTitle());
        notification.setLink("/event/" + event.getEncodedPath());
        notification.setChecked(false);
        notification.setCreatedDateTime(LocalDateTime.now());
        notification.setMessage(message);
        notification.setAccount(account);
        notification.setNotificationType(notificationType);
        notificationRepository.save(notification);
    }

}
