package com.rest1.domain.post.post.controller;

import com.rest1.domain.post.post.entity.Post;
import com.rest1.domain.post.post.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.Reader;
import java.util.List;

@Controller
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }


    record PostWriteForm(
            @NotBlank(message = "01-title-제목을 입력해주세요.")
            @Size(min = 2, max = 10, message = "02-title-제목은 2글자 이상 10글자 이하로 입력해주세요.")
            String title,
            @NotBlank(message = "03-content-내용을 입력해주세요.")
            @Size(min = 2, max = 100, message = "04-content-내용은 2글자 이상 100글자 이하로 입력해주세요.")
            String content
    ) {
    }

    @GetMapping("/posts/write")
    public String write(@ModelAttribute("form") PostWriteForm form) {
        return "post/post/write";
    }

    @PostMapping("/posts/write")
    public String doWrite(
            @ModelAttribute("form") @Valid PostWriteForm form, BindingResult bindingResult,
            Model model
    ) {

        if (bindingResult.hasErrors()) {
            return "post/post/write";
        }

        Post post = postService.write(form.title, form.content);
        model.addAttribute("id", post.getId());
        return "redirect:/posts/%d".formatted(post.getId()); // 주소창을 바꿔
    }


    record PostModifyForm(
            @NotBlank(message = "01-title-제목을 입력해주세요.")
            @Size(min = 2, max = 10, message = "02-title-제목은 2글자 이상 10글자 이하로 입력해주세요.")
            String title,
            @NotBlank(message = "03-content-내용을 입력해주세요.")
            @Size(min = 2, max = 100, message = "04-content-내용은 2글자 이상 100글자 이하로 입력해주세요.")
            String content
    ) {
    }

    @GetMapping("/posts/{id}/modify")
    public String modify(
            @PathVariable Long id,
            @ModelAttribute("form") PostModifyForm form,
            Model model
    ) {

        Post post = postService.findById(id).get();

        PostModifyForm form2 = new PostModifyForm(post.getTitle(), post.getContent());
        model.addAttribute("form", form2);
        model.addAttribute("post", post);
        return "post/post/modify";
    }

    @PutMapping("/posts/{id}")
    @Transactional
    public String doModify(
            @PathVariable Long id,
            @ModelAttribute("form") @Valid PostModifyForm form,
            BindingResult bindingResult,
            Model model
    ) {

        Post post = postService.findById(id).get();

        if (bindingResult.hasErrors()) {
            model.addAttribute("post", post);
            return "post/post/modify";
        }

        postService.modify(post, form.title, form.content);

        return "redirect:/posts/%d".formatted(post.getId());
    }

    @DeleteMapping("/posts/{id}")
    @Transactional
    public String doDelete(
            @PathVariable Long id
    ) {

        Post post = postService.findById(id).get();
        postService.delete(post);

        return "redirect:/posts";

    }


    @GetMapping("/posts/{id}")
    @Transactional(readOnly = true)
    public String detail(@PathVariable Long id, Model model, Reader reader) {

        Post post = postService.findById(id).get();
        model.addAttribute("post", post);

        return "post/post/detail";
    }

    @GetMapping("/posts")
    @Transactional(readOnly = true)
    public String list(Model model) {

        List<Post> posts = postService.findAll();
        model.addAttribute("posts", posts);
        return "post/post/list";
    }
}
