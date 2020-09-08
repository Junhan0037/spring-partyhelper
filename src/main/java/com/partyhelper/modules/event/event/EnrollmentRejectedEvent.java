package com.partyhelper.modules.event.event;

import com.partyhelper.modules.event.domain.Enrollment;

public class EnrollmentRejectedEvent extends EnrollmentEvent {

    public EnrollmentRejectedEvent(Enrollment enrollment) {
        super(enrollment, "해당 파티 업체 참가를 거절했습니다.");
    }

}
