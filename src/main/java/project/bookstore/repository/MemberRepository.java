package project.bookstore.repository;

import project.bookstore.domain.member.Member;
import project.bookstore.domain.member.UpdateMember;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    //가입
    void join(Member member) throws SQLException;

    //조회
    Member findById(String userId) throws SQLException;

    // pk로 조회
    Member findByIdWithKey(Long id) throws SQLException;

    //수정
    void update(UpdateMember member);

    //전체 조회
    List<Member> findAll() throws SQLException;

    //삭제
    void delete(Member member);

    Optional<Member> findByLoginId(String loginId) throws SQLException;

}
