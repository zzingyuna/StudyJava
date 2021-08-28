package com.example.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/*
 * SpringBootApplication이 있는 위치부터 설정을 읽어가므로 프로젝트 최상단에 위치 필수!!
 */
//@EnableJpaAuditing // JPA Auditing 활성화, JpaConfig를 통해 test는 제외하고 활성화 하도록 수정
@SpringBootApplication
public class Application {
    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }
}
