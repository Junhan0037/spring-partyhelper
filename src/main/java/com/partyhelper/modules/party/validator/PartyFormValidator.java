package com.partyhelper.modules.party.validator;

import com.partyhelper.modules.party.PartyRepository;
import com.partyhelper.modules.party.form.PartyForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class PartyFormValidator implements Validator {

    private final PartyRepository partyRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return PartyForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PartyForm partyForm = (PartyForm) target;
        if (partyRepository.existsByPath(partyForm.getPath())) {
            errors.rejectValue("path", "wrong.path", "해당 파티 경로값을 사용할 수 없습니다.");
        }
    }

}
