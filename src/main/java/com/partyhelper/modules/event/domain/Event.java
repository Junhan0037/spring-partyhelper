package com.partyhelper.modules.event.domain;

import com.partyhelper.modules.account.domain.Account;
import com.partyhelper.modules.event.EventType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Event { // 이벤트(파티)

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String path;

    @ManyToOne
    private Account createBy;

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
    private List<Enrollment> enrollments; // 참가 업체

    @Enumerated(EnumType.STRING)
    private EventType eventType; // 선착순, 이용자 선택

    @Column(nullable = false)
    private Integer personnel; // 총 인원

}
