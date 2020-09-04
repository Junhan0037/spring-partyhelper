package com.partyhelper.modules.main;

import com.partyhelper.modules.account.AccountRepository;
import com.partyhelper.modules.account.domain.Account;
import com.partyhelper.modules.account.annotation.CurrentAccount;
import com.partyhelper.modules.event.EnrollmentRepository;
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
    private final AccountRepository accountRepository;
    private final EnrollmentRepository enrollmentRepository;

    @GetMapping("/")
    public String index(@CurrentAccount Account account, Model model) {
        if (account != null) {
            Account accountLoaded = accountRepository.findAccountWithTagsAndZonesById(account.getId());
            model.addAttribute(accountLoaded);
            model.addAttribute("enrollmentList", enrollmentRepository.findByAccountAndAcceptedOrderByEnrolledAtDesc(account, true));
            model.addAttribute("eventList", eventRepository.findByAccount(
                    accountLoaded.getTags(),
                    accountLoaded.getZones()));
            model.addAttribute("eventManagerOf",
                    eventRepository.findFirst5ByCreatedByOrderByStartDateTime(account));
            model.addAttribute("eventMemberOf",
                    eventRepository.findFirst5ByMembersContainingOrderByStartDateTime(account));
            return "index-after-login";
        }
        model.addAttribute("eventList", eventRepository.findFirst9ByOrderByStartDateTime());
        return "index";
    }

    @GetMapping("/search/event")
    public String searchEvent(String keyword, Model model,
                              @PageableDefault(size = 9, sort = "startDateTime", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<Event> eventPage = eventRepository.findByKeyword(keyword, pageable);
        model.addAttribute("eventPage", eventPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortProperty", pageable.getSort().toString().contains("startDateTime") ? "startDateTime" : "memberCount");
        return "search";
    }

}
