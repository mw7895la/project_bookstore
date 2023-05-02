package project.bookstore.domain.member;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UpdateMember {

    @NotEmpty(message = "아이디 입력은 필수입니다.")
    private String user_id;
    @NotEmpty(message = "기존 비밀번호 입력은 필수입니다.")
    private String oldPassword;
    @NotEmpty(message = "신규 비밀번호 입력은 필수입니다.")
    private String newPassword;
    @NotEmpty(message = "신규 비밀번호를 재 확인 해주세요.")
    private String newCheckPassword;

    public UpdateMember() {
    }
}
