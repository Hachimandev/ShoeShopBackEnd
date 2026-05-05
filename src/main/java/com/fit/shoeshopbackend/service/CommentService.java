package com.fit.shoeshopbackend.service;

import com.fit.shoeshopbackend.dto.CommentRequest;
import com.fit.shoeshopbackend.model.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> getCommentsByProductId(String productId);
    Comment addComment(CommentRequest request);
    Comment updateComment(Long id, CommentRequest request);
    void deleteComment(Long id);
}









