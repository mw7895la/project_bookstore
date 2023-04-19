package project.bookstore.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;
import project.bookstore.domain.member.Member;
import project.bookstore.domain.member.UpdateMember;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Repository
public class MemberRepositoryImpl implements MemberRepository {

    private final DataSource dataSource;

    @Autowired
    public MemberRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void join(Member member) throws SQLException {
        String sql = "insert into Member(user_id,password,nickName) values(?,?,?)";
        Connection con = null;
        PreparedStatement pstmt =null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,member.getUser_id());
            pstmt.setString(2, member.getPassword());
            pstmt.setString(3, member.getNickName());
            pstmt.executeUpdate();
        }catch(SQLException e){
            log.info("error ={}", e);
            throw e;
        }finally{
            close(con, pstmt, null);
        }

    }

    @Override
    public Member findByIdWithKey(Long id) throws SQLException {
        String sql = "select * from Member where id=?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setUser_id(rs.getString("user_id"));
                member.setShopping_basket(rs.getString("shopping_basket"));
                member.setNickName(rs.getString("nickname"));
                return member;
            }else{
                throw new NoSuchElementException("not found user_id with primary key");
            }
        }catch(SQLException e){
            throw e;
        }finally{
            close(con, pstmt, rs);
        }
    }

    @Override
    public Member findById(String userId) throws SQLException {
        String sql = "select * from Member where user_id=?";

        Connection con =null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setUser_id(rs.getString("user_id"));
                member.setPassword(rs.getString("password"));
                member.setNickName(rs.getString("nickname"));
                member.setShopping_basket(rs.getString("shopping_basket"));
                return member;
            }else{
                throw new NoSuchElementException("not found user ID");
            }

        }catch(SQLException e){
            log.error("error ={}",e);
            throw e;
        }finally{
            close(con, pstmt, rs);
        }
    }

    @Override
    public List<Member> findAll() throws SQLException {
        String sql = "select * from Member";
        ArrayList<Member> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pstmt =null;
        ResultSet rs = null;
        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Member member = new Member();
                member.setUser_id(rs.getString("user_id"));
                member.setPassword(rs.getString("password"));
                member.setNickName(rs.getString("nickname"));
                member.setShopping_basket(rs.getString("shopping_basket"));

                list.add(member);
            }
            return list;
        }catch(SQLException e){
            log.error("error ={}", e);
            throw e;
        }finally{
            close(con, pstmt, rs);
        }
    }

    @Override
    public void update(UpdateMember member) {

    }


    @Override
    public void delete(Member member) {

    }

    @Override
    public Optional<Member> findByLoginId(String loginId) throws SQLException {
        List<Member> list = findAll();

        for (Member member : list) {
            if (member.getUser_id().equals(loginId)) {
                return Optional.of(member);
            }
        }
        return Optional.empty();
    }

    private void close(Connection con, Statement stmt, ResultSet resultSet) {
        JdbcUtils.closeResultSet(resultSet);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con);
    }


    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("get connection ={}, class ={}", con, con.getClass());
        return con;
    }
}
