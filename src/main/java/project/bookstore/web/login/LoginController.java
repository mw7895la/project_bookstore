package project.bookstore.web.login;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import project.bookstore.domain.member.Member;
import project.bookstore.web.SessionConst;
import project.bookstore.web.service.LoginService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;

@Controller
@Slf4j
public class LoginController {

    private final LoginService service;


    @Autowired
    public LoginController(LoginService service) {
        this.service = service;
    }

    @GetMapping("/login")
    public String loginForm(@ModelAttribute LoginForm loginForm) {
        return "login/loginForm";
    }

    /**
     * 순서대로 리팩토링 해볼 것.  bindingResult -> rejectValue-> @Validated 어노테이션
     */
    @PostMapping("/login")
    public String login(@ModelAttribute LoginForm loginForm, BindingResult bindingResult,
                        @RequestParam(name = "redirectURL",defaultValue = "/") String location,
                        HttpServletRequest request) throws SQLException {
        if (!StringUtils.hasText(loginForm.getLogin_id())) {
            bindingResult.addError(new FieldError("loginForm", loginForm.getLogin_id(), "아이디를 입력해주세요"));
        }

        if (!StringUtils.hasText(loginForm.getPassword())) {
            bindingResult.addError(new FieldError("loginForm", loginForm.getPassword(), "패스워드를 입력해주세요"));
        }

        if (bindingResult.hasErrors()) {
            log.info("bindingResult ={}", bindingResult);
            return "login/loginForm";
        }

        Member loginMember = service.login(loginForm.getLogin_id(), loginForm.getPassword());

        if (loginMember == null) {
            bindingResult.addError(new ObjectError("loginForm", "아이디 혹은 패스워드가 일치하지 않습니다."));
            return "login/loginForm";
        }
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);       //세션의 이름은 SessionConst.LOGIN_MEMBER

        return "redirect:"+location;
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session != null){
            session.invalidate();
        }

        return "redirect:/";
    }
}
