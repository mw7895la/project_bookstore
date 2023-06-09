package project.bookstore.web.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import project.bookstore.domain.exception.UserException;
import project.bookstore.domain.item.*;
import project.bookstore.domain.member.Member;

import project.bookstore.web.SessionConst;
import project.bookstore.web.file.FileStore;
import project.bookstore.web.service.ImageService;
import project.bookstore.web.service.ItemService;
import project.bookstore.web.service.MemberService;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.util.List;


@Slf4j
@Controller
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final MemberService memberService;
    private final FileStore fileStore;
    private final ImageService imageService;

    @Autowired
    public ItemController(ItemService itemService, MemberService memberService, FileStore fileStore, ImageService imageService) {
        this.itemService = itemService;
        this.memberService = memberService;
        this.fileStore = fileStore;
        this.imageService = imageService;
    }

    @GetMapping("/add")
    public String addForm(@ModelAttribute Item item) {
        return "items/addForm";
    }


    /**
     * 나중에 검증부분 리팩토링
     */
    @PostMapping("/add")
    public String addItem(@ModelAttribute("item") ItemSaveForm item, BindingResult bindingResult, HttpServletRequest request, RedirectAttributes redirectAttributes) throws IOException {

        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, null, null, "등록할 상품의 이름을 입력해주세요."));
        }
        if (item.getPrice() == null || item.getPrice() < 1000) {
            //bindingResult.addError(new FieldError("item", "price",item.getPrice(),false,null,new String[]{"typeMismatch.price"}, "가격은 1,000 ~ 1,000,000원만 가능합니다."));
            bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000}, null);
        }

        if (item.getQuantity() == null || item.getQuantity() < 1) {
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, null, null, "수량은 최소 1개 이상입니다."));
        }

        if (bindingResult.hasErrors()) {
            log.info("error ={}", bindingResult);
            return "items/addForm";
        }

        //현재 회원 아이디인지 확인 및 등록된 상품의 아이디를 뷰에 등록하기 위한 로직.
        HttpSession session = request.getSession(false);
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Member findMember = memberService.findById(member.getUser_id());


        Item newItem = new Item();
        newItem.setItemName(item.getItemName());
        newItem.setPrice(item.getPrice());
        newItem.setQuantity(item.getQuantity());
        newItem.setMember(findMember);
        newItem.setRegister(String.valueOf(LocalDate.now()));

        UploadFile uploadFile = fileStore.singleFile(item.getAttachFile());
        newItem.setAttachFile(uploadFile);
        Item savedItem = itemService.addItem(newItem);


        List<UploadFile> uploadFiles = fileStore.manyFiles(item.getImageFiles());
        for (UploadFile file : uploadFiles) {
            file.setItem(savedItem);
            imageService.addAttach(file);
        }


        redirectAttributes.addAttribute("id", savedItem.getId());
        redirectAttributes.addAttribute("status", true);

        return "redirect:/items/{id}";
    }

    @GetMapping("/{id}")
    public String item(@PathVariable("id") long id, Model model) {
        log.info("id ={}", id);
        Item item = itemService.findItem(id);

        model.addAttribute("item", item);

        return "items/item";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable("itemId") long id, HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);

        Item findItem = itemService.findItem(id);
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if (!findItem.getMember().getUser_id().equals(member.getUser_id())) {
            /** 예외 추가해야 함. */
            log.info("상품을 등록한 유저와 다른 유저입니다.");
            throw new UserException("상품을 등록한 유저와 다른 유저입니다.");
        }
        model.addAttribute("item", findItem);


        return "items/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String editItem(@PathVariable("itemId") long id,
                           @Validated @ModelAttribute("item") ItemUpdateForm form,
                           BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "items/editForm";
        }
        itemService.update(id, form);

        redirectAttributes.addAttribute("id", id);

        return "redirect:/items/{id}";
    }

    @GetMapping
    public String listItem(@ModelAttribute("itemSearch") ItemSearchCond cond, Model model) {
        log.info("{} {}", cond.getItemName(), cond.getMaxPrice());
        List<Item> items = itemService.findAll(cond);
        //imageService.findAll()

        model.addAttribute("items", items);

        return "items/items";
    }

    //delete
    @GetMapping("/delete/{user_id}/{item_id}")
    public String delete(@PathVariable("user_id") String user_id, @PathVariable("item_id") Long item_id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Member member = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        if (!member.getUser_id().equals(user_id)) {
            throw new UserException("잘못된 사용자");
        }

        itemService.delete(item_id);
        return "redirect:/items";
    }


    //첨부파일명 클릭시 다운로드 가능.
    @GetMapping("/attach/{itemId}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable Long itemId) throws MalformedURLException {
        //Item findItem = itemService.findItem(itemId);

        UploadFile uploadFile = itemService.findItem(itemId).getAttachFile();

        String storeFileName = uploadFile.getStoreFileName();
        String uploadFileName = uploadFile.getUploadFileName();

        UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(storeFileName));

        log.info("uploadFileName ={}", uploadFileName);

        String contentDisposition = "attachment; filename=\"" + uploadFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);

    }

    //웹 이미지 출력
    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource images(@PathVariable String filename) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }
}








