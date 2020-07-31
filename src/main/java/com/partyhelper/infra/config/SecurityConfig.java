package com.partyhelper.infra.config;

import com.partyhelper.modules.account.Role;
import com.partyhelper.modules.account.oauth.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@RequiredArgsConstructor
@EnableWebSecurity // 스프링 시큐리티 설정 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers().frameOptions().disable() // h2-console 화면을 사용하기 위해 해당 옵션을 disable
                .and()
                    .authorizeRequests() // URL별 권한 관리를 설정하는 옵션의 시작점
                    .antMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**", "/profile", "/login", "/sign-up",
                            "/check-email", "/check-email-token", "/email-login", "/check-email-login", "/login-link").permitAll() // 전체 열람 권한
                    .antMatchers("/api/v1/**").hasRole(Role.USER.name()) // USER 권한을 가진 사람만 가능
                    .antMatchers(HttpMethod.GET, "/profile/*").permitAll()
                    .anyRequest().authenticated() // anyRequest() : 설정한 값들 이외 나머지 URL. => 나머지 URL들은 모두 인증과정을 거친다
                .and()
                    .formLogin()
                        .loginPage("/login").permitAll()
                .and()
                    .logout()
                        .logoutSuccessUrl("/") // 로그아웃 성공 시 "/" 주소로 이동
                .and()
                    .oauth2Login() // OAuth2 로그인 기능에 대한 여러 설정의 진입점
                        .loginPage("/oauth")
                        .userInfoEndpoint() // OAuth2 로그인 성공 이후 사용자 정보를 가져올 때의 설정들 담당
                            .userService(customOAuth2UserService); // userService() : 소셜 로그인 성공 시 후속 조치를 진행할 구현체 등록
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .mvcMatchers("/node_modules/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()); // static resource 인증 X (이미지 등)
    }

}
