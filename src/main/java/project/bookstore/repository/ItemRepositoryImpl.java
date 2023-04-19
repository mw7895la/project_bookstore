package project.bookstore.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import project.bookstore.domain.item.Item;
import project.bookstore.domain.item.ItemSaveForm;
import project.bookstore.domain.item.ItemUpdateForm;
import project.bookstore.domain.member.Member;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final MemberRepository memberRepository;
    private final JdbcTemplate template;

    public ItemRepositoryImpl(DataSource dataSource, MemberRepository memberRepository) {
        this.template = new JdbcTemplate(dataSource);
        this.memberRepository = memberRepository;
    }

    @Override
    public Item addItem(Item item) {
        String sql = "insert into item(item_name,member_id,price,quantity,image,attach,register) values(?,?,?,?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        template.update(connection->{
            PreparedStatement pstmt = connection.prepareStatement(sql, new String[]{"id"});
            pstmt.setString(1,item.getItemName());
            pstmt.setLong(2, item.getMember().getId());
            pstmt.setInt(3, item.getPrice());
            pstmt.setInt(4, item.getQuantity());
            pstmt.setString(5, item.getImage());
            pstmt.setString(6, item.getAttach());
            pstmt.setString(7, String.valueOf(item.getRegister()));
            return pstmt;
        },keyHolder);

        long key = keyHolder.getKey().longValue();
        item.setId(key);
        return item;
    }

    @Override
    public void updateItem(Long id, ItemUpdateForm itemUpdateForm) {

    }

    @Override
    public Optional<Item> findById(Long id) {
        String sql = "select * from item where id=?";

        try {
            Item item = template.queryForObject(sql, memberRowMapper(), id);
            return Optional.of(item);
        }catch(EmptyResultDataAccessException e){
            return Optional.empty();
        }


    }

    private RowMapper<Item> memberRowMapper() {
        return (rs,rowNum)->{
            Item item = new Item();
            item.setId(rs.getLong("id"));
            item.setItemName(rs.getString("item_name"));
            long memberId = rs.getLong("member_id");
            Member findMember = memberRepository.findByIdWithKey(memberId);
            item.setMember(findMember);
            item.setPrice(rs.getInt("price"));
            item.setQuantity(rs.getInt("quantity"));
            item.setImage(rs.getString("image"));
            item.setAttach(rs.getString("attach"));
            item.setRegister(rs.getString("register"));
            return item;
        };
    }

    @Override
    public List<Item> findAll() {
        return null;
    }

}