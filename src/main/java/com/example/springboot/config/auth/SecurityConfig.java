package com.example.springboot.config.auth;

import com.example.springboot.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/*
 * @EnableWebSecurity: Spring Security 설정들 활성화
 */
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;

    /*
     * .csrf().disable().headers().frameOptions().disable() 
     * h2-console 화면을 사용하기 위해 해당 옵션들 disable
     */

    /*
     * .authorizeRequests()
     * url별 관리를 설정하는 시작점
     */

    /*
     * .antMatchers()
     * 권한 관리 대상을 지정하는 옵션
     */

    /*
     * .anyRequest()
     * 설정된 값을 이외의 나머지 url들
     */
    
    /*
     * .oauth2Login()
     * OAuth2 로그인 기능에 대한 여러 설정의 진입점
     * 
     * .userInfoEndpoint()
     * OAuth2 로그인 성공 후 사용자 정보를 가져올 때 설정들
     */
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable().headers().frameOptions().disable()
            .and()
                .authorizeRequests()
                    .antMatchers("/","/css/**", "/images/**", "/js/**", "/h2-console/**", "/profile").permitAll()
                    .mvcMatchers("/api/v1/**").hasRole(Role.USER.name())
                    .anyRequest().authenticated()
            .and().logout().logoutSuccessUrl("/")
            .and().oauth2Login().userInfoEndpoint().userService(customOAuth2UserService);
    }

}
