package com.rest1.domain.post.comment.dto;

import com.rest1.domain.post.comment.entity.Comment;

import java.time.LocalDateTime;

public record CommentDto(
        Long id,
        LocalDateTime createDate,
        LocalDateTime modifyDate,
        String content
) {
    public CommentDto(Comment comment) {
        this(
                comment.getId(),
                comment.getCreateDate(),
                comment.getModifyDate(),
                comment.getContent()
        );
    }
}
