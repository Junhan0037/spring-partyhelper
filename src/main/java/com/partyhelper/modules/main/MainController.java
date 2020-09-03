package com.partyhelper.modules.main;

import com.partyhelper.modules.account.domain.Account;
import com.partyhelper.modules.account.annotation.CurrentAccount;
import com.partyhelper.modules.event.EventRepository;
import com.partyhelper.modules.event.domain.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final EventRepository eventRepository;

    @GetMapping("/")
    public String index(@CurrentAccount Account account, Model model) {
        if (account != null) {
            model.addAttribute(account);
        }
        return "index";
    }

    @GetMapping("/search/event")
    public String searchEvent(String keyword, Model model,
                              @PageableDefault(size = 9, sort = "startDateTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Event> eventPage = eventRepository.findByKeyword(keyword, pageable);
        model.addAttribute("eventPage", eventPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortProperty", pageable.getSort().toString().contains("startDateTime") ? "startDateTime" : "memberCount");
        return "search";
    }

}
