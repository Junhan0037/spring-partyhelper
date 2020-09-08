package com.partyhelper.modules.event.event;

import com.partyhelper.modules.event.domain.Enrollment;

public class EnrollmentAcceptedEvent extends EnrollmentEvent {

    public EnrollmentAcceptedEvent(Enrollment enrollment) {
        super(enrollment, "해당 파티의 업체로 선정되었습니다. 확인해주세요.");
    }

}
