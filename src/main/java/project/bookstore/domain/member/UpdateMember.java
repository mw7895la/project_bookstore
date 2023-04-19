package project.bookstore.domain.member;

import lombok.Data;

@Data
public class UpdateMember {

    private String user_id;
    private String oldPassword;
    private String newPassword;
    private String newCheckPassword;

    public UpdateMember() {
    }
}
