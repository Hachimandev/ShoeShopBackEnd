package com.fit.shoeshopbackend.service;

import com.fit.shoeshopbackend.dto.BinhLuanRequest;
import com.fit.shoeshopbackend.model.BinhLuan;

import java.util.List;

public interface BinhLuanService {
    List<BinhLuan> getCommentsByProductId(String maSanPham);
    BinhLuan addComment(BinhLuanRequest request);
    BinhLuan updateComment(Long id, BinhLuanRequest request);
    void deleteComment(Long id);
}
