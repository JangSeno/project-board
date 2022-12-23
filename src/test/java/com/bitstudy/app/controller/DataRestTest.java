package com.bitstudy.app.controller;
/*
* 슬라이스 테스트 : 기능별(레이어별)로 잘라서 특정 부분(기능)만 테스트 하는 것
*
* - 통합 테스트 에너테이션
*   @SpringBootTest - 스프링이 관리하는 모든 빈을 등록시켜서 테스트하기 떄문에 무겁다
*                   * 테스트 할 때 가볍게 하기 위해서 @WebMvcTest를 사용해서 web레이어 관련 빈들만 등록한 상태로 테스트 가능
*                     단, web레이어 관련된 빈들만 등록되므로 Service는 등록되지 않는다. 그래서 Mok관련 어노테이션을 이용해서 가짜로 만들어줘야 한다.
*
*
* - 슬라이스 테스트 에너테이션
*   1) @WebMvcTest - 슬라이스 테스트에서 대표적인 어노테이션
*                  - Controller를 테스트 할 수 있도록 관련 설정을 제공해준다.
*                  - @WebMvcTest를 선언하면 web과 관련된 Bean만 주입되고, MockMvc를 알아볼 수 있게 된다.
*
*                  * MockMvc는 웹 어플리케이션을 어플리케이션 서버에 배포하지 않고 가짜로 테스트용 MVC환경을 만들어 요청 및 전송, 응답기능을 제공하는 유틸리티 클래스.
*                  * 컨트롤러 슬라이스 테스트 한다고 하면 대부분 이것.
*   2) @DataJpaTest - JPA 레포지토리 테스트 할 떄 사용
*                   - @Entity가 있는 엔티티 클래스들을 스캔해 테스트를 위한 JAP 레포지토리들을 설정
*                   * @Component나 @ConfigurationProperties 들은 무시
*   3) @RestClientTest - (클라이언트 입장에서의)API 연동 테스트
*                      - 테스트 코드 내에서 Mock 서버를 띄울 수 있다. (response, request에 대한 사정 정의가 가능)
*
* */

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
public class DataRestTest {

    //MockMvc 테스트 방법
    //1) MockMvc생성 (빈 준비)
    //2) MockMvc에게 요청에 대한 정보를 입력
    //3) 요청에 대한 응답값을 expect를 이용해서 테스트한다.
    //4) exprxt 다 통과하면 테스트 통과
    private final MockMvc mvc; //bean 준비

    public DataRestTest(MockMvc mvc) { //2)
        this.mvc = mvc;
    }

    // api를 이용해서 게시글 리스트 전체 조회
    @DisplayName("전체 조회")
    @Test
    void getArticlesAll() throws Exception {

        //이 테스트는 실패해야 정상임. 이유는 해당 api를 찾을 수 없기 떄문
        //하지만 콘솔창 MockHttpServletRequest 부분에 URL="/api/articles 를 복사해서 따로 브라우저에 넣어보면 제대로 나온다.
        //@WebMvcTest는 슬라이스 테스트이기 떄문에 contriller외의 빈들은 로드하지 않았기 때문이다.
        //그래서 일단 @WebMvcTest대신 통합 테스트(spring~)로 돌릴 것

        mvc.perform(get("/api/articles")) // 컨트롤 스페이스로 딥다이브, 알트 엔터로 스태틱(짧게) 임포트
        .andExpect(status().isOk()) // 서버에 갔다 옴?
        .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
        /* 특별한 import!! (딥다이브)
        *   1) perform() 안에 get 치고 -> 딥다이브 -> 다른 방식의 추천들이 엄청 나오는데
        *      MockMvcRequestBuilders.get 선택 후 알트 엔터로 스태틱 임포트
        *   * 스태틱 임포트란 필드나 메서드 클래스를 지정하지 않고도 코드에서 사용할 수 있도록 하는 기능.
        *
        *
        *   3) andExpect(content().contentType()) 부분 설명
        *      content 검사는 contentType으로 하고 MediaType 사용함.
        *      valueOf안에 들어길 ontent-type은 할 익스플로러의 Response Header에 있는 것.
        *
        *  기타 - 컨트롤 + . 누르면 선택한 부분을 말줌임표로 줄임. 다시 클릭하면 확장
        * */

    }



}
