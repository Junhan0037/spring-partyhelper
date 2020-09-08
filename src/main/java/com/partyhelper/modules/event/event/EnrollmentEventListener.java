package com.partyhelper.modules.event.event;

import com.partyhelper.infra.config.AppProperties;
import com.partyhelper.infra.mail.EmailMessage;
import com.partyhelper.infra.mail.EmailService;
import com.partyhelper.modules.account.domain.Account;
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

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class EnrollmentEventListener {

    private final NotificationRepository notificationRepository;
    private final AppProperties appProperties;
    private final TemplateEngine templateEngine;
    private final EmailService emailService;

    @EventListener
    public void handleEnrollmentEvent(EnrollmentEvent enrollmentEvent) {
        Enrollment enrollment = enrollmentEvent.getEnrollment();
        Account account = enrollment.getAccount();
        Event event = enrollment.getEvent();

        if (account.isEventEnrollmentResultByEmail()) {
            sendEmail(enrollmentEvent, account, event);
        }

        if (account.isEventEnrollmentResultByWeb()) {
            createNotification(enrollmentEvent, account, event);
        }
    }

    @EventListener
    public void handleEnrollmentExistingEvent(EnrollmentExistingEvent enrollmentEvent) {
        Enrollment enrollment = enrollmentEvent.getEnrollment();
        Event event = enrollment.getEvent();
        Account account = event.getCreatedBy();

        if (account.isEventExistingEnrollmentByEmail()) {
            sendCreatedByEmail(enrollmentEvent, account, event);
        }

        if (account.isEventExistingEnrollmentByWeb()) {
            createNotification(enrollmentEvent, account, event);
        }

    }

    private void sendEmail(EnrollmentEvent enrollmentEvent, Account account, Event event) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/event/" + event.getEncodedPath());
        context.setVariable("linkName", event.getTitle());
        context.setVariable("message", enrollmentEvent.getMessage());
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject("PartyHelper, " + event.getTitle() + " 업체 참가 신청 결과입니다.")
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

    private void createNotification(EnrollmentEvent enrollmentEvent, Account account, Event event) {
        Notification notification = new Notification();
        notification.setTitle(event.getTitle());
        notification.setLink("/event/" + event.getEncodedPath());
        notification.setChecked(false);
        notification.setCreatedDateTime(LocalDateTime.now());
        notification.setMessage(enrollmentEvent.getMessage());
        notification.setAccount(account);
        notification.setNotificationType(NotificationType.EVENT_ENROLLMENT);
        notificationRepository.save(notification);
    }

    private void sendCreatedByEmail(EnrollmentExistingEvent enrollmentEvent, Account account, Event event) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/event/" + event.getEncodedPath());
        context.setVariable("linkName", event.getTitle());
        context.setVariable("message", enrollmentEvent.getMessage());
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject("PartyHelper, " + event.getTitle() + " 파티에 업체가 참가 신청했습니다.")
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

    private void createNotification(EnrollmentExistingEvent enrollmentEvent, Account account, Event event) {
        Notification notification = new Notification();
        notification.setTitle(event.getTitle());
        notification.setLink("/event/" + event.getEncodedPath());
        notification.setChecked(false);
        notification.setCreatedDateTime(LocalDateTime.now());
        notification.setMessage(enrollmentEvent.getMessage());
        notification.setAccount(account);
        notification.setNotificationType(NotificationType.EVENT_ENROLLMENT);
        notificationRepository.save(notification);
    }

}
