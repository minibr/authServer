package com.rest1.domain.post.comment.controller;

import com.rest1.domain.post.comment.entity.Comment;
import com.rest1.domain.post.post.entity.Post;
import com.rest1.domain.post.post.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final PostService postService;

    record CommentWriteForm(
            @NotBlank(message = "댓글 내용을 입력해주세요.")
            @Size(min = 2, max = 100, message = "댓글 내용은 2글자 이상 100글자 이하로 입력해주세요.")
            String content
    ) {}

    @PostMapping("/posts/{postId}/comments/write")
    @Transactional
    public String write(
            @PathVariable Long postId,
            @Valid CommentWriteForm form
    ) {
        Post post = postService.findById(postId).get();

        postService.writeComment(post, form.content);
        return "redirect:/posts/" + postId;
    }


    record CommentModifyForm(
            @NotBlank(message = "댓글 내용을 입력해주세요.")
            @Size(min = 2, max = 100, message = "댓글 내용은 2글자 이상 100글자 이하로 입력해주세요.")
            String content
    ) {}

    @GetMapping("/posts/{postId}/comments/{commentId}/modify")
    public String modify(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            CommentModifyForm form,
            Model model
    ) {

        Post post = postService.findById(postId).get();
        Comment comment = post.findCommentById(commentId).get();

        model.addAttribute("comment", comment);
        model.addAttribute("post", post);

        return "post/comment/modify";
    }

    @PutMapping("/posts/{postId}/comments/{commentId}")
    @Transactional
    public String doModify(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @Valid CommentModifyForm form
    ) {

        Post post = postService.findById(postId).get();
        postService.modifyComment(post, commentId, form.content);

        return "redirect:/posts/" + postId;
    }


    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    @Transactional
    public String doDelete(
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {

        Post post = postService.findById(postId).get();
        postService.deleteComment(post, commentId);

        return "redirect:/posts/" + postId;
    }
}
