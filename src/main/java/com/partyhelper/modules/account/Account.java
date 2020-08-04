package com.partyhelper.modules.account;

import com.partyhelper.modules.main.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

//    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    @Column
    private String picture;

    @Enumerated(EnumType.STRING) // JPA로 데이터베이스로 저장할 때 Enum값을 어떤 형대토 저장할지를 결정한다. (기본값은 int)
    @Column(nullable = false)
    private Role role;

    public Account update(String name, String picture) {
        this.name = name;
        this.picture = picture;
        return this;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }

    // 여기까지 Oauth2 정보

    @Column(unique = true)
    private String nickname;

    private String password;

    private boolean emailVerified; // 이메일 인증

    private String emailCheckToken; // 이메일 토큰

    private LocalDateTime emailCheckTokenGeneratedAt;

    private String bio;

    private String url;

    private String occupation;

    private String location;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private boolean studyCreatedByEmail;

    private boolean studyCreatedByWeb = true;

    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb = true;

    private boolean studyUpdatedByEmail;

    private boolean studyUpdatedByWeb = true;

    public void generateEmailCheckToken() { // 이메일 인증 토큰 생성
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public void completeSignUp() {
        this.emailVerified = true;
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail() {
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1)); // 이메일을 전송한지 한시간이 지났는지 확인
    }

}
