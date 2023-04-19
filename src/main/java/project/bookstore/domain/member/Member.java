package project.bookstore.domain.member;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Member {

    private Long id;
    //@NotNull(message = "아이디는 필수입니다.")
    private String user_id;
    //@NotNull(message="비밀번호는 필수입니다.")
    private String password;
    //@NotNull(message = "닉네임을 입력해주세요.")
    private String nickName;
    private String shopping_basket;

    public Member() {
    }

    public Member(Long id, String user_id, String password, String nickName) {
        this.id = id;
        this.user_id = user_id;
        this.password = password;
        this.nickName = nickName;
    }
}
