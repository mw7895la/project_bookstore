package project.bookstore.web.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.bookstore.domain.member.Member;
import project.bookstore.repository.MemberRepository;

import java.sql.SQLException;

@Service
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void join(Member member) throws SQLException {

        memberRepository.join(member);
    }

    public Member findById(String userId){
        try {
            return memberRepository.findById(userId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
