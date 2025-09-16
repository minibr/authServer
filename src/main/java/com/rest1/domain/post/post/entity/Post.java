package com.rest1.domain.post.post.entity;

import com.rest1.domain.post.comment.entity.Comment;
import com.rest1.global.jpa.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@Getter
@Entity
public class Post extends BaseEntity {
    private String title;
    private String content;

    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval=true, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Comment addComment(String content) {
        Comment comment = new Comment(content, this);
        this.comments.add(comment);

        return comment;
    }

    public void deleteComment(Long commentId) {
        Comment comment = findCommentById(commentId).get();
        this.comments.remove(comment);
    }

    public Comment updateComment(Long commentId, String content) {
        Comment comment = findCommentById(commentId).get();
        comment.update(content);
        return comment;
    }

    public Optional<Comment> findCommentById(Long commentId) {
        return comments.stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst();
    }
}
