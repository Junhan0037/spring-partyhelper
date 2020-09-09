package com.partyhelper.modules.settings.domain;

import lombok.Data;

@Data
public class Notifications {

    private boolean eventCreatedByEmail;;

    private boolean eventCreatedByWeb;

    private boolean eventEnrollmentResultByEmail;

    private boolean eventEnrollmentResultByWeb;

    private boolean eventUpdatedByEmail;

    private boolean eventUpdatedByWeb;

    private boolean eventExistingEnrollmentByEmail;

    private boolean eventExistingEnrollmentByWeb;

}
