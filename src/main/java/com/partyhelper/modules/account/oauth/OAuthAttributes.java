package com.partyhelper.modules.account.oauth;

import com.partyhelper.modules.account.domain.Account;
import com.partyhelper.modules.account.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class OAuthAttributes {

    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final String name;
    private final String email;
    private final String picture;

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if("naver".equals(registrationId)) {
            return ofNaver("id", attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        System.out.println("attributes = " + attributes.values());
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return OAuthAttributes.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .picture((String) response.get("profile_image"))
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public Account toEntity() { // User 엔티티를 생성한다.
        return Account.builder()
                .name(name)
                .nickname(name)
                .email(email)
                .picture(picture)
                .role(Role.OAUTH) // 가입할 때의 기본 권한을 USER로 준다.
                .emailVerified(true)
                .eventCreatedByWeb(true)
                .eventEnrollmentResultByWeb(true)
                .eventUpdatedByWeb(true)
                .eventEnrollmentResultByWeb(true)
                .build();
    }

}
