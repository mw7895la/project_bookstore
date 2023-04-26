package project.bookstore.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import project.bookstore.domain.item.UploadFile;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class ImageRepositoryImpl implements ImageRepository{

    private final JdbcTemplate template;

    public ImageRepositoryImpl(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public UploadFile addAttach(UploadFile uploadFile) {
        String sql = "insert into image(item_id,upload_name,store_name) values(?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        template.update(connection ->{
            PreparedStatement pstmt = connection.prepareStatement(sql, new String[]{"id"});
            pstmt.setLong(1,uploadFile.getItem().getId());
            pstmt.setString(2, uploadFile.getUploadFileName());
            pstmt.setString(3,uploadFile.getStoreFileName());

            return pstmt;
        },keyHolder);

        long key = keyHolder.getKey().longValue();
        uploadFile.setId(key);
        return uploadFile;
    }



    @Override
    public List<UploadFile> findAll(Long item_id) {
        String sql = "select * from image where item_id=?";

        return template.query(sql, FileRowMapper(), item_id);
    }

    private RowMapper<UploadFile> FileRowMapper() {
        return ((rs,rowNum)->{
            UploadFile uploadFile = new UploadFile();
            uploadFile.setUploadFileName(rs.getString("upload_name"));
            uploadFile.setStoreFileName(rs.getString("store_name"));
            uploadFile.setId(rs.getLong("id"));
            return uploadFile;
        });
    }
}
