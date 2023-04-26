package project.bookstore.domain.item;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import project.bookstore.domain.member.Member;

import javax.swing.plaf.multi.MultiListUI;
import java.util.List;

@Data
public class ItemSaveForm {

    private String itemName;
    private Member member;      //해당 유저의 아이디 알려고.
    private Integer price;
    private Integer quantity;

    private List<MultipartFile> imageFiles;
    private MultipartFile attachFile;
}
