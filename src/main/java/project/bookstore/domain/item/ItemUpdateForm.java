package project.bookstore.domain.item;

import lombok.Data;
import project.bookstore.domain.member.Member;

@Data
public class ItemUpdateForm {

    private Long id;
    private String itemName;
    private int price;
    private int quantity;
    private String image;
    private String attach;
}
