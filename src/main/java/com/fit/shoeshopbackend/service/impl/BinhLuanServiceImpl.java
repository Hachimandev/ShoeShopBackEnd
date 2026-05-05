package com.fit.shoeshopbackend.service.impl;

import com.fit.shoeshopbackend.dto.BinhLuanRequest;
import com.fit.shoeshopbackend.model.BinhLuan;
import com.fit.shoeshopbackend.model.KhachHang;
import com.fit.shoeshopbackend.model.SanPham;
import com.fit.shoeshopbackend.repository.BinhLuanRepository;
import com.fit.shoeshopbackend.repository.KhachHangRepository;
import com.fit.shoeshopbackend.repository.SanPhamRepository;
import com.fit.shoeshopbackend.service.BinhLuanService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BinhLuanServiceImpl implements BinhLuanService {

    private final BinhLuanRepository binhLuanRepository;
    private final SanPhamRepository sanPhamRepository;
    private final KhachHangRepository khachHangRepository;

    @Override
    public List<BinhLuan> getCommentsByProductId(String maSanPham) {
        return binhLuanRepository.findBySanPhamIdWithKhachHang(maSanPham);
    }

    @Override
    @Transactional
    public BinhLuan addComment(BinhLuanRequest request) {
        SanPham sanPham = sanPhamRepository.findById(request.getMaSanPham())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại."));

        KhachHang khachHang = khachHangRepository.findByTaiKhoan_TenDangNhap(request.getUsername());
        if (khachHang == null) {
            throw new RuntimeException("Khách hàng không tồn tại.");
        }

        BinhLuan binhLuan = BinhLuan.builder()
                .noiDung(request.getNoiDung())
                .diemDanhGia(request.getDiemDanhGia())
                .ngayTao(LocalDateTime.now())
                .sanPham(sanPham)
                .khachHang(khachHang)
                .build();

        return binhLuanRepository.save(binhLuan);
    }

    @Override
    @Transactional
    public BinhLuan updateComment(Long id, BinhLuanRequest request) {
        BinhLuan binhLuan = binhLuanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bình luận không tồn tại."));

        binhLuan.setNoiDung(request.getNoiDung());
        binhLuan.setDiemDanhGia(request.getDiemDanhGia());

        return binhLuanRepository.save(binhLuan);
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        BinhLuan binhLuan = binhLuanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bình luận không tồn tại."));

        binhLuanRepository.delete(binhLuan);
    }
}
