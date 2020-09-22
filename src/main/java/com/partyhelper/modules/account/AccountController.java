package com.partyhelper.modules.account;

import com.partyhelper.modules.account.annotation.CurrentAccount;
import com.partyhelper.modules.account.domain.Account;
import com.partyhelper.modules.account.form.SignUpForm;
import com.partyhelper.modules.account.validator.SignUpFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/login")
    public String login() {
        return "account/login";
    }

    @GetMapping("/login-user")
    public String loginUserForm() {
        return "account/login-user";
    }

    @GetMapping("/sign-up")
    public String signUpForm() {
        return "account/sign-up";
    }

    @GetMapping("/sign-up-user")
    public String signUpUserForm(Model model) {
        model.addAttribute("signUpForm", new SignUpForm());
        return "account/sign-up-user";
    }

    @PostMapping("/sign-up-user")
    public String signUpUserSubmit(@Valid @ModelAttribute SignUpForm signUpForm, Errors errors) {
        if(errors.hasErrors()) {
            return "account/sign-up-user";
        }

        Account account = accountService.processNewAccount(signUpForm);
        accountService.login(account);
        return "redirect:/";
    }

    @GetMapping("/sign-up-provider")
    public String signUpProviderForm(Model model) {
        model.addAttribute("signUpForm", new SignUpForm());
        return "account/sign-up-provider";
    }

    @PostMapping("/sign-up-provider")
    public String signUpProviderSubmit(@Valid @ModelAttribute SignUpForm signUpForm, Errors errors) {
        if(errors.hasErrors()) {
            return "account/sign-up-provider";
        }

        Account account = accountService.processNewProvider(signUpForm);
        accountService.login(account);
        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model) {
        Account account = accountRepository.findByEmail(email);
        String view = "account/checked-email";

        if (account == null) {
            model.addAttribute(account);
            model.addAttribute("error", "wrong.email");
            return view;
        }

        if (!account.isValidToken(token)) {
            model.addAttribute(account);
            model.addAttribute("error", "wrong.token");
            return view;
        }

        accountService.completeSignUp(account);
        model.addAttribute(account);
        model.addAttribute("numberOfUser", accountRepository.count());
        model.addAttribute("nickname", account.getNickname());
        return view;
    }

    @GetMapping("/check-email")
    public String checkEmail(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        return "account/check-email";
    }

    @GetMapping("/resend-confirm-email")
    public String resendConfirmEmail(@CurrentAccount Account account, Model model) {
        if (!account.canSendConfirmEmail()) {
            model.addAttribute("error", "인증 이메일은 1시간에 한번만 전송할 수 있습니다.");
            model.addAttribute(account);
            return "account/check-email";
        }

        accountService.sendSignUpConfirmEmail(account);
        return "redirect:/";
    }

    @GetMapping("/profile/{emailCheckToken}")
    public String viewProfile(@CurrentAccount Account account, @PathVariable String emailCheckToken, Model model) {
        Account byEmail = accountRepository.findByEmailCheckToken(emailCheckToken);
        if (byEmail == null) {
            throw new IllegalArgumentException(emailCheckToken + "에 해당하는 사용자가 없습니다.");
        }

        model.addAttribute(byEmail);
//        model.addAttribute("isOwner", byEmail.equals(account));
        model.addAttribute("isOwner", byEmail.getEmailCheckToken().equals(account.getEmailCheckToken()));
        return "account/profile";
    }

    @GetMapping("/email-login")
    public String emailLoginForm() {
        return "account/email-login";
    }

    @PostMapping("/email-login")
    public String sendEmailLoginLink(String email, Model model, RedirectAttributes attributes) {
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            model.addAttribute("error", "유요한 이메일 주소가 아닙니다.");
            return "account/email-login";
        }

        if (!account.canSendConfirmEmail()) {
            model.addAttribute("error", "이메일 로그인은 1시간 뒤에 사용할 수 있습니다.");
            return "account/email-login";
        }

        accountService.sendLoginLink(account);
        attributes.addFlashAttribute("message", "이메일 인증 메일을 발송했습니다.");
        return "redirect:/email-login";
    }

    @GetMapping("/login-by-email")
    public String loginByEmail(String token, String email, Model model) {
        Account account = accountRepository.findByEmail(email);
        String view = "account/logged-in-by-email";

        if (account == null || !account.isValidToken(token)) {
            model.addAttribute(account);
            model.addAttribute("error", "로그인할 수 없습니다.");
            return view;
        }

        model.addAttribute(account);
        accountService.login(account);
        return view;
    }

}
