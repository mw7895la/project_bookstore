package project.bookstore.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
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
    private final SQLExceptionTranslator exTranslator;


    @Autowired
    public MemberRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
    }

    @Override
    public void join(Member member)  {
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
            throw exTranslator.translate("save", sql, e);
        }finally{
            close(con, pstmt, null);
        }

    }

    @Override
    public Member findByIdWithKey(Long id)  {
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

            DataAccessException ex = exTranslator.translate("select", sql, e);
            throw ex;
        }finally{
            close(con, pstmt, rs);
        }
    }

    @Override
    public Member findById(String userId){
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
            DataAccessException ex = exTranslator.translate("select", sql, e);
            throw ex;
        }finally{
            close(con, pstmt, rs);
        }
    }

    @Override
    public List<Member> findAll()  {
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
            DataAccessException ex = exTranslator.translate("select", sql, e);
            throw ex;
        }finally{
            close(con, pstmt, rs);
        }
    }

    @Override
    public void update(UpdateMember member) {
        String sql = "update member set password=? where user_id=?";

        Connection con = null;
        PreparedStatement pstmt =null;

        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getNewPassword());
            pstmt.setString(2, member.getUser_id());
            pstmt.executeUpdate();
        }catch(SQLException e){
            DataAccessException ex = exTranslator.translate("update", sql, e);
            throw ex;
        }finally{
            close(con, pstmt, null);
        }
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
        DataSourceUtils.releaseConnection(con, dataSource);
        //트랜잭션 동기화를 이용하려면 DataSourceUtils.를 사용해야 한다.  관리 안하는 커넥션이면 닫아버린다.
    }


    private Connection getConnection() throws SQLException {
        //Connection con = dataSource.getConnection();
        Connection con = DataSourceUtils.getConnection(dataSource);
        log.info("get connection ={}, class ={}", con, con.getClass());
        return con;
    }
}
