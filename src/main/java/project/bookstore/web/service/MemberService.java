package project.bookstore.web.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import project.bookstore.domain.exception.JoinException;
import project.bookstore.domain.member.Member;
import project.bookstore.domain.member.UpdateMember;
import project.bookstore.repository.MemberRepository;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@Transactional(readOnly = true)
@Service
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PlatformTransactionManager txManager;

    @Autowired
    public MemberService(MemberRepository memberRepository,DataSource dataSource) {
        this.memberRepository = memberRepository;
        this.txManager = new DataSourceTransactionManager(dataSource);
    }

    @Transactional
    public void join(Member member)  {
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
        try{

            memberRepository.join(member);
            if (member.getPassword().equals("qwe1234")) {
                throw new IllegalStateException("가입중 예외 발생");
            }
            //성공시 커밋
            txManager.commit(status);
        }catch(Exception e){
            txManager.rollback(status);
            throw new JoinException(e);
        }
    }

    @Transactional
    public void update(UpdateMember updateMember){
        memberRepository.update(updateMember);
    }

    public Member findById(String userId){
        try {
            return memberRepository.findById(userId);
        } catch (RuntimeException e) {
            log.info("DataAccessException ", e);
            throw new RuntimeException(e);
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
