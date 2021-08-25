package com.example.springboot.dto;

import com.example.springboot.web.dto.HelloResponseDto;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HelloResponseDtoTest {

    @Test
    public void test_lombok() {
        // given
        String name = "test";
        int amount = 1000;

        // when
        HelloResponseDto dto = new HelloResponseDto(name, amount);

        // then
        assertThat(dto.getName()).isEqualTo(name);
        assertThat(dto.getAmount()).isEqualTo(amount);

        // junit의 assertThat이 아닌 assertj의 assertThat을 사용한 이유
        // 1. CoreMatchers와 달리 추가적으로 라이브러리가 필요하지 않음
        // 2. 자동완성 지원 강화
    }

}
