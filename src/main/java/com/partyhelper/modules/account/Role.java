package com.partyhelper.modules.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    ADMIN("ROLE_ADMIN", "관리자"),
    PROVIDER("ROLE_PROVIDER", "이벤트 업체"),
    USER("ROLE_USER", "일반 로그인"),
    OAUTH("ROLE_OAUTH", "소셜 로그인"); // 스프링 시큐리티에서는 권한 코드에 항상 ROLE_이 앞에 있어야만 한다.

    private final String key;
    private final String title;

}
