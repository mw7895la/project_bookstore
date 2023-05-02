package project.bookstore.web.service;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import project.bookstore.domain.exception.JoinException;
import project.bookstore.domain.member.Member;
import project.bookstore.domain.member.UpdateMember;
import project.bookstore.repository.MemberRepository;
import project.bookstore.repository.MemberRepositoryImpl;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;


@Slf4j
@SpringBootTest
class MemberServiceTest {

    public static final String MEMBER_A = "memberA";

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    DataSource dataSource;

    @BeforeEach
    void before() {

        memberRepository = new MemberRepositoryImpl(dataSource);
        memberService = new MemberService(memberRepository, dataSource);
    }


    @Test
    @DisplayName("회원가입 로직 정상동작")
    void memberJoin_commit() {
        Member member = new Member();
        member.setUser_id("JDBC woojin");
        member.setPassword("qwe");

        memberService.join(member);
    }

    @Test
    @DisplayName("회원가입 로직 중 예외 발생시 롤백")
    void memberJoin_rollback() {
        Member member = new Member();
        member.setUser_id(MEMBER_A);
        member.setPassword("qwe1234");

        Assertions.assertThatThrownBy(() -> memberService.join(member)).isInstanceOf(JoinException.class);
    }


    @Test
    @DisplayName("회원 비밀번호 변경이 잘 되는지 확인")
    void updatePassword(){
        Member member = new Member();
        member.setUser_id("memberA");
        member.setPassword("12345");

        memberRepository.join(member);

        Member findMember = memberRepository.findById(member.getUser_id());

        Assertions.assertThat(member.getUser_id()).isEqualTo(findMember.getUser_id());

        UpdateMember updateMember = new UpdateMember();
        updateMember.setUser_id(findMember.getUser_id());
        updateMember.setNewPassword("54321");

        memberRepository.update(updateMember);

        Member updatedMember = memberRepository.findById(updateMember.getUser_id());

        /*log.info("{}", updatedMember.getPassword());
        log.info("{}", member.getPassword());*/
        Assertions.assertThat(updatedMember.getPassword()).isNotEqualTo(member.getPassword());
    }
}