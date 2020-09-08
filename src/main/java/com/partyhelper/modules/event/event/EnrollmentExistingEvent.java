package com.partyhelper.modules.event.event;

import com.partyhelper.modules.event.domain.Enrollment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EnrollmentExistingEvent {

    protected final Enrollment enrollment;

    protected final String message = "해당 파티에 업체가 참가 신청했습니다. 확인해주세요.";

}
