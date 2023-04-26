package project.bookstore.domain.item;

import lombok.Data;
import project.bookstore.domain.member.Member;

import java.time.LocalDate;
import java.util.List;

@Data
public class Item {
    private Long id;
    private String itemName;
    private Member member;      //해당 유저의 아이디 알려고.
    private String register;
    private Integer price;
    private Integer quantity;

    private UploadFile attachFile;
    private List<UploadFile> imageFiles;



    public Item() {
    }

    //이미지

}
