package com.partyhelper.modules.party;

import com.partyhelper.modules.account.annotation.CurrentAccount;
import com.partyhelper.modules.account.domain.Account;
import com.partyhelper.modules.party.form.PartyForm;
import com.partyhelper.modules.party.validator.PartyFormValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class PartyController {

    private final PartyService partyService;
    private final PartyFormValidator partyFormValidator;
    private final PartyRepository partyRepository;
    private final ModelMapper modelMapper;

    @InitBinder("partyForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(partyFormValidator);
    }

    @GetMapping("/new-party")
    public String newPartyForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new PartyForm());
        return "party/form";
    }

    @PostMapping("/new-party")
    public String newPartySubmit(@CurrentAccount Account account, @Valid PartyForm partyForm, Errors errors) {
        if (errors.hasErrors()) {
            return "party/form";
        }
        Party newParty = partyService.createNewParty(modelMapper.map(partyForm, Party.class), account);
        return "redirect:/party/" + URLEncoder.encode(newParty.getPath(), StandardCharsets.UTF_8);
    }

    @GetMapping("/party/{path}")
    public String viewParty(@CurrentAccount Account account, @PathVariable String path, Model model) {
        model.addAttribute(account);
        model.addAttribute(partyRepository.findByPath(path));
        return "party/view";
    }

}
