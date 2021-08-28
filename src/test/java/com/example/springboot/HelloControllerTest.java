package com.example.springboot;


import com.example.springboot.config.auth.SecurityConfig;
import com.example.springboot.web.HelloController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
 * @RunWith: SpringRunner라는 스프링 실행자를 사용하여 스프링부트 테스트와 JUnit 사이의 연결 역할
 * @WebMvcTest: Web(Spring MVC)에 집중할 수 있는 어노테이션, 컨트롤러 관련만 사용가능
 * excludeFilters.. 스캔 대상에서 제외
 */
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = HelloController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})
public class HelloControllerTest {

    // @Autowired: 스프링이 관리하는 빈(Bean)을 주입 받는다
    @Autowired
    private MockMvc mvc; // 웹 API를 테스트할때 사용

    @Test
    @WithMockUser(roles = "USER")
    public void test_hello() throws Exception {
        String hello = "hello";

        // mvc.perform: 체이닝이 지원되어 여러 검증 기능을 이어서 선언 가능
        mvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string(hello));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void test_helloDto() throws Exception {
        String name = "hello";
        int amount = 1000;

        mvc.perform(get("/hello/dto")
                .param("name", name).param("amount", String.valueOf(amount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(name)))
                .andExpect(jsonPath("$.amount", is(amount)));
    }

}
