package com.partyhelper.modules.event;

import com.partyhelper.modules.account.annotation.CurrentAccount;
import com.partyhelper.modules.account.domain.Account;
import com.partyhelper.modules.event.form.EventForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class EventController {

    @GetMapping("/new-event")
    public String newEventForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new EventForm());
        return "event/form";
    }

}
