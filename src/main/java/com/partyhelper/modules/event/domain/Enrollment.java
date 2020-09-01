package com.partyhelper.modules.event.domain;

import com.partyhelper.modules.account.domain.Account;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Enrollment { // 참가 업체

    @Id @GeneratedValue
    private Long id;

    @ManyToOne // 양방향 관계
    private Event event;

    @ManyToOne
    private Account account;

    private boolean accepted; // 승인 여부

    private LocalDateTime enrolledAt;

}
