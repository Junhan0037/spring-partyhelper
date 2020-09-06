package com.partyhelper.modules.main;

import com.partyhelper.modules.account.AccountRepository;
import com.partyhelper.modules.account.domain.Account;
import com.partyhelper.modules.account.annotation.CurrentAccount;
import com.partyhelper.modules.event.EnrollmentRepository;
import com.partyhelper.modules.event.EventRepository;
import com.partyhelper.modules.event.domain.Enrollment;
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
import java.util.Set;

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

            Set<Enrollment> newEnrollment = enrollmentRepository.findByAcceptedOrderByEnrolledAtDesc(true);
            model.addAttribute("enrollmentEvent", eventRepository.findByEnrolledEvent(account, newEnrollment)); // 업체가 확정된 파티 (사용자)

            model.addAttribute("enrollmentList", enrollmentRepository.findByAccountAndAcceptedOrderByEnrolledAtDesc(account, true)); // 업체로 확정된 파티 (업체)

            model.addAttribute("tagEventList", eventRepository.findByTag(
                    accountLoaded.getTags())); // 태그와 관련된 이벤트

            model.addAttribute("zoneEventList", eventRepository.findByZone(
                    accountLoaded.getZones())); // 지역과 관련된 이벤트

            model.addAttribute("eventManagerOf",
                    eventRepository.findFirst5ByCreatedByOrderByStartDateTime(account)); // 관리중인 이벤트 (사용자)


//            model.addAttribute("enrollmentAccountOf", enrollmentRepository.findFirst5ByAccountOrderByEnrolledAtDesc(account)); // 업체로 지원중인 이벤트 (업체)
            Set<Enrollment> newEnrollingList  = enrollmentRepository.findByAccountOrderByEnrolledAtDesc(account);
            model.addAttribute("enrollingEvent", eventRepository.findByEnrollingEvent(newEnrollingList));


            return "index-after-login";
        }
        model.addAttribute("eventList", eventRepository.findFirst9ByOrderByStartDateTime());
        return "index";
    }

    @GetMapping("/search/event")
    public String searchEvent(@CurrentAccount Account account, String keyword, Model model,
                              @PageableDefault(size = 9, sort = "startDateTime", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<Event> eventPage = eventRepository.findByKeyword(keyword, pageable);
        model.addAttribute(account);
        model.addAttribute("eventPage", eventPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortProperty", pageable.getSort().toString().contains("startDateTime") ? "startDateTime" : "memberCount");
        return "search";
    }

}
