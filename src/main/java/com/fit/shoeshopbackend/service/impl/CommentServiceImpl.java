package com.fit.shoeshopbackend.service.impl;

import com.fit.shoeshopbackend.dto.CommentRequest;
import com.fit.shoeshopbackend.model.Comment;
import com.fit.shoeshopbackend.model.Customer;
import com.fit.shoeshopbackend.model.Product;
import com.fit.shoeshopbackend.repository.CommentRepository;
import com.fit.shoeshopbackend.repository.CustomerRepository;
import com.fit.shoeshopbackend.repository.ProductRepository;
import com.fit.shoeshopbackend.service.CommentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    @Override
    public List<Comment> getCommentsByProductId(String productId) {
        return commentRepository.findByProduct_ProductIdOrderByCreatedAtDesc(productId);
    }

    @Override
    @Transactional
    public Comment addComment(CommentRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found."));

        Customer customer = customerRepository.findByAccount_Username(request.getUsername());
        if (customer == null) {
            throw new RuntimeException("Customer not found.");
        }

        Comment comment = Comment.builder()
                .content(request.getContent())
                .rating(request.getRating())
                .createdAt(LocalDateTime.now())
                .product(product)
                .customer(customer)
                .build();

        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment updateComment(Long id, CommentRequest request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found."));

        comment.setContent(request.getContent());
        comment.setRating(request.getRating());

        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found."));

        commentRepository.delete(comment);
    }
}
