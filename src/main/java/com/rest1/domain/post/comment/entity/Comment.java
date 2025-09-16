package com.rest1.domain.post.comment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rest1.domain.member.member.entity.Member;
import com.rest1.domain.post.post.entity.Post;
import com.rest1.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Comment extends BaseEntity {

    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Post post;
    @ManyToOne(fetch = FetchType.LAZY)
    private Member author;

    public Comment(Member author, String content, Post post) {
        this.author = author;
        this.content = content;
        this.post = post;
    }

    public void update(String content) {
        this.content = content;
    }
}
