package project.bookstore.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import project.bookstore.domain.item.*;
import project.bookstore.domain.member.Member;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final MemberRepository memberRepository;
    private final JdbcTemplate template;
    private final ImageRepository imageRepository;

    public ItemRepositoryImpl(DataSource dataSource, MemberRepository memberRepository,ImageRepository imageRepository) {
        this.template = new JdbcTemplate(dataSource);
        this.memberRepository = memberRepository;
        this.imageRepository = imageRepository;
    }

    @Override
    public Item addItem(Item item) {
        String sql = "insert into item(item_name,member_id,register,price,quantity,upload_name,store_name) values(?,?,?,?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        template.update(connection->{
            PreparedStatement pstmt = connection.prepareStatement(sql, new String[]{"id"});
            pstmt.setString(1,item.getItemName());
            pstmt.setLong(2, item.getMember().getId());
            pstmt.setString(3, String.valueOf(item.getRegister()));
            pstmt.setInt(4, item.getPrice());
            pstmt.setInt(5, item.getQuantity());
            pstmt.setString(6, item.getAttachFile().getUploadFileName());
            pstmt.setString(7, item.getAttachFile().getStoreFileName());

            return pstmt;
        },keyHolder);

        long key = keyHolder.getKey().longValue();
        item.setId(key);
        return item;
    }

    @Override
    public void updateItem(Long id, ItemUpdateForm form) {
        String sql = "update item set itemName=?, price=?, quantity=? where id=?";
        template.update(sql, form.getItemName(), form.getPrice(), form.getQuantity(), id);


    }

    @Override
    public Optional<Item> findById(Long id) {
        String sql = "select * from item where id=?";

        try {
            Item item = template.queryForObject(sql, itemRowMapper(), id);
            return Optional.of(item);
        }catch(EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    private RowMapper<Item> itemRowMapper() {
        return (rs,rowNum)->{
            Item item = new Item();
            item.setId(rs.getLong("id"));
            UploadFile uploadFile = new UploadFile(rs.getString("upload_name"),rs.getString("store_name"));
            item.setAttachFile(uploadFile);
            item.setItemName(rs.getString("item_name"));
            long memberId = rs.getLong("member_id");
            Member findMember = memberRepository.findByIdWithKey(memberId);
            item.setMember(findMember);
            item.setPrice(rs.getInt("price"));
            item.setQuantity(rs.getInt("quantity"));
            item.setRegister(rs.getString("register"));
            return item;
        };
    }


    private RowMapper<Item> itemListRowMapper() {
        return (rs,rowNum)->{
            Item item = new Item();
            item.setId(rs.getLong("id"));
            List<UploadFile> uploadFiles = imageRepository.findAll(item.getId());
            item.setImageFiles(uploadFiles);
            item.setItemName(rs.getString("item_name"));
            long memberId = rs.getLong("member_id");
            Member findMember = memberRepository.findByIdWithKey(memberId);
            item.setMember(findMember);
            item.setPrice(rs.getInt("price"));
            item.setQuantity(rs.getInt("quantity"));
            item.setRegister(rs.getString("register"));
            return item;
        };
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        String sql = "select id,item_name,member_id,price,quantity,register from item";

        if (StringUtils.hasText(itemName) || maxPrice != null) {
            sql += " where";
        }
        boolean andFlag = false;

        List<Object> param = new ArrayList<>();

        if (StringUtils.hasText(itemName)) {
            sql += " item_name like concat('%',?,'%')";
            param.add(itemName);
            andFlag= true;
        }

        if(maxPrice !=null){
            if (andFlag) {
                sql += " and";
            }
            sql += " price <= ?";
            param.add(maxPrice);
        }
        log.info("sql ={}", sql);

        return template.query(sql, param.toArray(), itemListRowMapper());
    }


    @Override
    public void delete(Long id) {
        String sql = "delete from item where id=?";
        template.update(sql, id);
    }
}
