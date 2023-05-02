package project.bookstore.web.member;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import project.bookstore.domain.member.Member;
import project.bookstore.domain.member.UpdateMember;
import project.bookstore.web.SessionConst;
import project.bookstore.web.service.MemberService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;

@Slf4j
@Controller
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    //회원 가입
    @GetMapping("/add")
    public String join(@ModelAttribute Member member) {

        return "members/addMemberForm";
    }

    /**
     * 순서대로 리팩토링 해볼 것.  bindingResult -> rejectValue-> @Validated 어노테이션
     */
    @PostMapping("/add")
    public String joinMember(/*@Validated*/ Member member, BindingResult bindingResult) {


        //user_id
        if (!StringUtils.hasText(member.getUser_id())) {
            bindingResult.addError(new FieldError("member", "user_id", "아이디는 필수로 입력해주세요."));
        }

        //password
        if (!StringUtils.hasText(member.getPassword()) || member.getPassword().length() < 8) {
            bindingResult.addError(new FieldError("member", "password", "비밀번호는 8자리 이상으로 입력해주세요."));
        }

        //nickName
        if (!StringUtils.hasText(member.getNickName())) {
            bindingResult.addError(new FieldError("member", "nickName", "닉네임을 입력해주세요."));
        }

        if (bindingResult.hasErrors()) {
            log.info("bindingResult = {} ", bindingResult);
            return "members/addMemberForm";
        }
        boolean check = memberService.findAll(member.getUser_id());

        if (check) {
            bindingResult.reject("sameId");
            return "members/addMemberForm";
        }

        memberService.join(member);
        return "redirect:/";
    }

    @GetMapping("/update")
    public String updateForm(@ModelAttribute("memberUpdateForm") UpdateMember memberUpdateForm) {
        return "members/updateMemberForm";
    }

    @PostMapping("/update")
    public String updateMember(@Validated @ModelAttribute("memberUpdateForm")UpdateMember updateMember, BindingResult bindingResult, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            return "members/updateMemberForm";
        }
        HttpSession session = httpServletRequest.getSession(false);
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        Member findMember = memberService.findById(member.getUser_id());

        if (!findMember.getPassword().equals(updateMember.getOldPassword())) {
            bindingResult.rejectValue("oldPassword", "password.mismatch", null);
            return "members/updateMemberForm";
        }

        if (!updateMember.getNewPassword().equals(updateMember.getNewCheckPassword())) {
            bindingResult.rejectValue("newPassword", "new.password.mismatch", null);
            bindingResult.rejectValue("newCheckPassword", "new.password.mismatch", null);
            return "members/updateMemberForm";
        }

        memberService.update(updateMember);

        return "redirect:/";

    }
}
