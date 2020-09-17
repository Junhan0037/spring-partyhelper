package com.partyhelper.modules.event.form;

import com.partyhelper.modules.event.EventType;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
public class EventUpdateForm {

    public static final String VALID_PATH_PATTERN = "^[ㄱ-ㅎ가-힣a-z0-9_-]{2,20}$";

    @NotBlank
    @Length(max = 50)
    private String title;

    @NotBlank
    @Length(min = 2, max = 20)
    @Pattern(regexp = VALID_PATH_PATTERN)
    private String path;

    private String description;

    private EventType eventType = EventType.CONFIRMATIVE;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDateTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDateTime;

    @Min(1)
    private Integer limitOfEnrollments = 1;

    @Min(1)
    private Integer personnel = 1;

}
