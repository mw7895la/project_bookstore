package project.bookstore.web.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.bookstore.domain.member.Member;
import project.bookstore.repository.MemberRepository;

import java.sql.SQLException;
import java.util.Optional;

@Service
@Slf4j
public class LoginService {

    private final MemberRepository memberRepository;

    @Autowired
    public LoginService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member login(String loginId, String password) throws SQLException {

        Optional<Member> byLoginId = memberRepository.findByLoginId(loginId);

        return byLoginId.filter(member -> member.getPassword().equals(password)).orElse(null);

    }
}
