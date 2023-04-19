package project.bookstore.domain.item;

import lombok.Data;
import project.bookstore.domain.member.Member;

@Data
public class ItemSaveForm {

    private String itemName;
    private Member member;      //해당 유저의 아이디 알려고.
    private Integer price;
    private Integer quantity;
    private String image;
    private String attach;
}
