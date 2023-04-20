package project.bookstore.web.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import project.bookstore.domain.member.Member;
import project.bookstore.repository.MemberRepository;

import java.sql.SQLException;
import java.util.List;

@Service
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void join(Member member)  {

        memberRepository.join(member);
    }

    public Member findById(String userId){
        try {
            return memberRepository.findById(userId);
        } catch (DataAccessException e) {
            log.info("DataAccessException ", e);
            throw e;
        }
    }

    public Member findByIdWithKey(Long id)  {
        Member findMember = memberRepository.findByIdWithKey(id);
        return findMember;
    }


    public boolean findAll(String userId) {
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            if (member.getUser_id().equals(userId)) {
                return true;
            }
        }
        return false;
    }
}
