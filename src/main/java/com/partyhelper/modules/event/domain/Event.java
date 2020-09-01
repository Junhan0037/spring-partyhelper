package com.partyhelper.modules.event.domain;

import com.partyhelper.modules.account.domain.Account;
import com.partyhelper.modules.event.EventType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Event { // 이벤트(파티)

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String path;

    @ManyToOne
    private Account createdBy;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Column(nullable = false)
    private Integer limitOfEnrollments; // 참가 업체 제한 수

    @OneToMany(mappedBy = "event") // 양방향 관계
    @OrderBy("enrolledAt")
    private List<Enrollment> enrollments = new ArrayList<>(); // 참가 업체

    @Enumerated(EnumType.STRING)
    private EventType eventType; // 선착순, 이용자 선택

    @Column(nullable = false)
    private Integer personnel; // 총 인원

    public boolean isEnrollableFor(Account account) { // 참가 가능
        return isNotClosed() && !isAlreadyEnrolled(account);
    }

    public boolean isDisenrollableFor(Account account) { // 참가 불가능
        return isNotClosed() && isAlreadyEnrolled(account);
    }

    public boolean canEnrollment() {
        return isNotClosed() && this.startDateTime.isBefore(LocalDateTime.now());
    }

    private boolean isNotClosed() {
        return this.endDateTime.isAfter(LocalDateTime.now());
    }

    public int numberOfRemainSpots() {
        return this.limitOfEnrollments - (int) this.enrollments.stream().filter(Enrollment::isAccepted).count();
    }

    private boolean isAlreadyEnrolled(Account account) {
        for (Enrollment e : this.enrollments) {
            if (e.getAccount().getId().equals(account.getId())) {
                return true;
            }
        }
        return false;
    }

    public long getNumberOfAcceptedEnrollments() {
        return this.enrollments.stream().filter(Enrollment::isAccepted).count();
    }

    public boolean isManager(Account account) {
        return this.createdBy.getId().equals(account.getId());
    }

    public boolean canAccept(Enrollment enrollment) {
        return this.eventType == EventType.CONFIRMATIVE
                && this.enrollments.contains(enrollment)
                && this.limitOfEnrollments > this.getNumberOfAcceptedEnrollments()
                && !enrollment.isAccepted();
    }

    public boolean canReject(Enrollment enrollment) {
        return this.eventType == EventType.CONFIRMATIVE
                && this.enrollments.contains(enrollment)
                && enrollment.isAccepted();
    }

    public void acceptWaitingList() {
        if (this.isAbleToAcceptWaitingEnrollment()) {
            var waitingList = getWaitingList();
            int numberToAccept = (int) Math.min(this.limitOfEnrollments - this.getNumberOfAcceptedEnrollments(), waitingList.size());
            waitingList.subList(0, numberToAccept).forEach(e -> e.setAccepted(true));
        }
    }

    public boolean isAbleToAcceptWaitingEnrollment() {
        return this.eventType == EventType.FCFS && this.limitOfEnrollments > this.getNumberOfAcceptedEnrollments();
    }

    private List<Enrollment> getWaitingList() {
        return this.enrollments.stream().filter(enrollment -> !enrollment.isAccepted()).collect(Collectors.toList());
    }

    public void addEnrollment(Enrollment enrollment) { // 연관관계
        this.enrollments.add(enrollment);
        enrollment.setEvent(this);
    }

    public void removeEnrollment(Enrollment enrollment) {
        this.enrollments.remove(enrollment);
        enrollment.setEvent(null);
    }

    public void acceptNextWaitingEnrollment() {
        if (this.isAbleToAcceptWaitingEnrollment()) {
            Enrollment enrollmentToAccept = this.getTheFirstWaitingEnrollment();
            if (enrollmentToAccept != null) {
                enrollmentToAccept.setAccepted(true);
            }
        }
    }

    private Enrollment getTheFirstWaitingEnrollment() {
        for (Enrollment e : this.enrollments) {
            if (!e.isAccepted()) {
                return e;
            }
        }
        return null;
    }

}
