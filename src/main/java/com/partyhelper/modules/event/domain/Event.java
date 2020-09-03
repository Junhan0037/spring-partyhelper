package com.partyhelper.modules.event.domain;

import com.partyhelper.modules.account.domain.Account;
import com.partyhelper.modules.event.EventType;
import com.partyhelper.modules.settings.domain.Tag;
import com.partyhelper.modules.settings.domain.Zone;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    private int memberCount = 0;

    public boolean isEnrollableFor(Account account) { // 참가 가능
        return isNotClosed() && !isAlreadyEnrolled(account);
    }

    public boolean isDisenrollableFor(Account account) { // 참가 불가능
        return isNotClosed() && isAlreadyEnrolled(account);
    }

    public LocalDateTime getEnrollmentTime() { // 파티 신청 마감 일
        return this.startDateTime.minusDays(1); // 파티 시작 1일 전
    }

    public boolean isNotClosed() {
        return getEnrollmentTime().isAfter(LocalDateTime.now());
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

    public void accept(Enrollment enrollment) {
        if (this.eventType == EventType.CONFIRMATIVE && this.limitOfEnrollments > this.getNumberOfAcceptedEnrollments()) {
            enrollment.setAccepted(true);
        }
    }

    public void reject(Enrollment enrollment) {
        if (this.eventType == EventType.CONFIRMATIVE) {
            enrollment.setAccepted(false);
        }
    }

    public void addMember() {
        this.memberCount++;
    }

    public void removeMember() {
        this.memberCount--;
    }

}
