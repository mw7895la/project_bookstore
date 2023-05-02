package project.bookstore.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.bookstore.domain.item.UploadFile;
import project.bookstore.repository.ImageRepository;

import java.util.List;
import java.util.Optional;
@Transactional
@Service
public class ImageService {

    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public UploadFile addAttach(UploadFile uploadFile) {

        return imageRepository.addAttach(uploadFile);
    }

    @Transactional(readOnly = true)
    public List<UploadFile> findAll(Long item_id) {
        return imageRepository.findAll(item_id);
    }
}
