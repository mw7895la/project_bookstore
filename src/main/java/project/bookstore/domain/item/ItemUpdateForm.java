package project.bookstore.domain.item;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import project.bookstore.domain.member.Member;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemUpdateForm {

    private long id;
    @NotBlank(message = "공백은 입력할 수 없습니다.")
    private String itemName;
    @NotNull
    @Range(min=1000, max=1000000)
    private Integer price;
    @NotNull
    @Max(9999)
    private Integer quantity;
    private Member member;
    private String image;
    private String attach;
}
