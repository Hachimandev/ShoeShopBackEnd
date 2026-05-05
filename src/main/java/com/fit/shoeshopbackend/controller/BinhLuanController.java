package com.fit.shoeshopbackend.controller;

import com.fit.shoeshopbackend.dto.BinhLuanRequest;
import com.fit.shoeshopbackend.model.BinhLuan;
import com.fit.shoeshopbackend.service.BinhLuanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/binhluan")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BinhLuanController {

    private final BinhLuanService binhLuanService;

    @GetMapping("/by-product/{maSanPham}")
    public ResponseEntity<List<BinhLuan>> getCommentsByProductId(@PathVariable String maSanPham) {
        List<BinhLuan> comments = binhLuanService.getCommentsByProductId(maSanPham);
        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody BinhLuanRequest request) {
        try {
            BinhLuan newComment = binhLuanService.addComment(request);
            return ResponseEntity.status(201).body(newComment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable Long id, @RequestBody BinhLuanRequest request) {
        try {
            BinhLuan updatedComment = binhLuanService.updateComment(id, request);
            return ResponseEntity.ok(updatedComment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        try {
            binhLuanService.deleteComment(id);
            return ResponseEntity.ok().body("Xóa bình luận thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
