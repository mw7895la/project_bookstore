package project.bookstore.web.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import project.bookstore.domain.item.Item;
import project.bookstore.domain.item.ItemSaveForm;
import project.bookstore.domain.member.Member;
import project.bookstore.web.SessionConst;
import project.bookstore.web.service.ItemService;
import project.bookstore.web.service.MemberService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;

@Slf4j
@Controller
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final MemberService memberService;

    public ItemController(ItemService itemService, MemberService memberService) {
        this.itemService = itemService;
        this.memberService = memberService;
    }

    @GetMapping
    public String addForm(@ModelAttribute Item item) {
        return "items/addForm";
    }

    @PostMapping
    public String addItem(@ModelAttribute("item") ItemSaveForm item, BindingResult bindingResult, HttpServletRequest request, RedirectAttributes redirectAttributes) {

        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(),false,null,null,"등록할 상품의 이름을 입력해주세요."));
        }
        if (item.getPrice() == null || item.getPrice() <1000) {
            //bindingResult.addError(new FieldError("item", "price",item.getPrice(),false,null,new String[]{"typeMismatch.price"}, "가격은 1,000 ~ 1,000,000원만 가능합니다."));
            bindingResult.rejectValue("price", "range",new Object[]{1000,1000000},null);
        }

        if (item.getQuantity() == null || item.getQuantity() < 1) {
            bindingResult.addError(new FieldError("item", "quantity",item.getQuantity(),false,null,null, "수량은 최소 1개 이상입니다."));
        }

        if (bindingResult.hasErrors()) {
            log.info("error ={}", bindingResult);
            return "items/addForm";
        }

        HttpSession session = request.getSession(false);
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Member findMember = memberService.findById(member.getUser_id());



        Item newItem = new Item();
        newItem.setItemName(item.getItemName());
        newItem.setPrice(item.getPrice());
        newItem.setQuantity(item.getQuantity());
        newItem.setImage(item.getImage());
        newItem.setAttach(item.getAttach());
        newItem.setMember(findMember);
        newItem.setRegister(String.valueOf(LocalDate.now()));

        Item savedItem = itemService.addItem(newItem);

        redirectAttributes.addAttribute("id", savedItem.getId());
        redirectAttributes.addAttribute("status", true);

        return "redirect:/items/{id}";
    }

    @GetMapping("/{id}")
    public String item(@PathVariable("id") long id, Model model) {
        log.info("id ={}",id);
        Item item = itemService.findItem(id);

        model.addAttribute("item", item);

        return "items/item";
    }
}







