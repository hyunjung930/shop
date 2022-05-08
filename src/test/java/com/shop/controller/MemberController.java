package com.shop.controller;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import com.shop.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;

@SpringBootTest
@AutoConfigureMockMvc   //MockMvc 테스트
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MockMvc mockMvc;    //실제 객체와 비슷하지만 테스트에 필요한 기능만 가지는 가짜 객체. 웹 브라우저에 요청하는 것처럼 테스트할 수 있음.

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("로그인 실패 테스트")
    public void loginFailTest() throws Exception{
        String email = "test@email.com";
        String password = "1234";
        this.createMember(email,password);
        mockMvc.perform(formLogin().userParameter("email")
                .loginProcessingUrl("/members/login")
                .user(email).password("12345"))
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated());
    }

    public Member createMember(String email, String password){  //로그인 예제 진행을 위해 로그인 전 회원 등록 메서드
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail(email);
        memberFormDto.setName("홍길동");
        memberFormDto.setAddress("서울시 마포구");
        memberFormDto.setPassword(password);
        Member member = Member.createMember(memberFormDto, passwordEncoder);
        return memberService.saveMember(member);
    }

    //@Test
    @DisplayName("로그인 성공 테스트")
    public void loginSuccessTest() throws Exception{
        String email = "test@email.com";
        String password = "1234";
        this.createMember(email,password);

        mockMvc.perform(formLogin().userParameter("email")
                .loginProcessingUrl("/members/login")   //회원 가입 메소드 실행 후 회원정보로 로그인 되는지 테스트 진행
                .user(email).password(password))
                .andExpect(SecurityMockMvcResultMatchers.authenticated());  // 로그인 성공하여 인증되었다면 테스트 코드 통과
    }
}
