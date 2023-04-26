package project.bookstore.repository;

import project.bookstore.domain.item.UploadFile;

import java.util.List;
import java.util.Optional;


public interface ImageRepository {

    UploadFile addAttach(UploadFile uploadFile);

//    Optional<UploadFile> findFileById(Long item_id);

    List<UploadFile> findAll(Long item_id);
}
