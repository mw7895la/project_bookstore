package project.bookstore.domain.item;

import lombok.Data;

@Data
public class UploadFile {

    private Long id;
    private Item item;
    private String uploadFileName;
    private String storeFileName;

    public UploadFile(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }

    public UploadFile() {
    }
}
