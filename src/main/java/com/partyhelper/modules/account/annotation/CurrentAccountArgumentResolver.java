package com.partyhelper.modules.account.annotation;

import com.partyhelper.modules.account.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Component
public class CurrentAccountArgumentResolver implements HandlerMethodArgumentResolver {

    private final HttpSession httpSession;

    @Override
    public boolean supportsParameter(MethodParameter parameter) { // 컨트롤러 메서드의 특정 파라미터를 지원하는지 판단한다.
        boolean isLoginUserAnnotation = parameter.getParameterAnnotation(CurrentAccount.class) != null; // 파라미터에 @CurrentAccount 어노테이션이 붙어있는가?
        boolean isUserClass = Account.class.equals(parameter.getParameterType()); // 파라미터 클래스 타입이 Account.class 인가?
        return isLoginUserAnnotation && isUserClass;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception { // 파라미터에 전달할 객체를 생성.
        return httpSession.getAttribute("account");
    }

}
