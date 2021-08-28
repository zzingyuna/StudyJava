https://docs.gradle.org/current/userguide/userguide.html

https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#managing-dependencies

# 프로젝트 설정 파일 자동 생성  
https://start.spring.io/  

open jdk 16  

Gradle Version 7.0.2  
https://gradle.org/install/  
(setting에서 gradle 외부폴더 참조로 변경함)  


error: variable name not initialized in the default constructor  
[https://github.com/jojoldu/freelec-springboot2-webservice/issues/2](https://github.com/jojoldu/freelec-springboot2-webservice/issues/2)  


데이터 확인  
[http://localhost:8080/h2-console](http://localhost:8080/h2-console)  
JDBC URL값을 아래와 같이 변경  
jdbc:h2:mem:testdb  


resources/application-oauth.properties 파일 생성, 아래 내용 입력  
> spring.security.oauth2.client.registration.google.client-id=클라이언트id  
> spring.security.oauth2.client.registration.google.client-secret=클라이언트 보안비밀  
> spring.security.oauth2.client.registration.google.scope=profile,email  

