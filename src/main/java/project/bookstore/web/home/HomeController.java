package project.bookstore.web.home;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import project.bookstore.domain.member.Member;
import project.bookstore.web.SessionConst;

@Slf4j
@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember, Model model) {
        /** name 이 SessionConst.LOGIN_MEMBER로 되어있는 세션을 얻어서 그 value 객체를 loginMember에 바인딩한다. */
        if (loginMember == null) {
            return "home";
        }
        //log.info("home()진입");
        model.addAttribute("member", loginMember);
        return "loginHome";
    }
}
