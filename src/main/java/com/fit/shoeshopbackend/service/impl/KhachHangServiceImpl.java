package com.fit.shoeshopbackend.service.impl;



import com.fit.shoeshopbackend.model.KhachHang;
import com.fit.shoeshopbackend.model.TrangThaiHoaDon;
import com.fit.shoeshopbackend.repository.KhachHangRepository;
import com.fit.shoeshopbackend.service.KhachHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KhachHangServiceImpl implements KhachHangService {

    private final KhachHangRepository khachHangRepository;

    @Override
    public int layDiemTichLuyTheoUsername(String username) {
        KhachHang kh = khachHangRepository.findByTaiKhoan_TenDangNhap(username);
        return kh != null ? kh.getDiemTichLuy() : 0;
    }

    @Override
    public String getKhachHangIdByUsername(String username) {
        KhachHang kh = findByTaiKhoan_TenDangNhap(username);
        return kh.getMaKhachHang();
    }

    @Override
    public KhachHang findByTaiKhoan_TenDangNhap(String username) {
        return khachHangRepository.findByTaiKhoan_TenDangNhap(username);
    }

    @Override
    public KhachHang save(KhachHang kh) {
        return khachHangRepository.save(kh);
    }

    @Override
    public List<KhachHang> getAllCustomers() {
        return khachHangRepository.findAll();
    }

    @Override
    public Page<KhachHang> searchCustomers(String search, Double minSpend, Double maxSpend, String startDate, String endDate, Pageable pageable) {
        Page<KhachHang> page = khachHangRepository.findAll(pageable);

        List<KhachHang> filtered = page.getContent().stream()
                .filter(kh -> matchesSearch(kh, search, minSpend, maxSpend, startDate, endDate))
                .collect(Collectors.toList());

        return new PageImpl<>(filtered, pageable, filtered.size());
    }

    private boolean matchesSearch(KhachHang kh, String search, Double minSpend, Double maxSpend, String startDate, String endDate) {
        // Search by name or maKhachHang
        boolean searchOk = true;
        if (search != null && !search.trim().isEmpty()) {
            String s = search.trim().toLowerCase();
            searchOk = (kh.getHoTen() != null && kh.getHoTen().toLowerCase().contains(s))
                    || (kh.getMaKhachHang() != null && kh.getMaKhachHang().toLowerCase().contains(s));
        }

        // Filter by total spending (tongChiTieu)
        boolean spendOk = true;
        if (minSpend != null || maxSpend != null) {
            double spend = kh.getTongChiTieu();
            if (minSpend != null && spend < minSpend) spendOk = false;
            if (maxSpend != null && spend > maxSpend) spendOk = false;
        }

        // Filter by registration date (ngayThamGia)
        boolean dateOk = true;
        if ((startDate != null && !startDate.isEmpty()) || (endDate != null && !endDate.isEmpty())) {
            if (kh.getNgayThamGia() != null) {
                LocalDateTime khDate = kh.getNgayThamGia();
                if (startDate != null && !startDate.isEmpty()) {
                    LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
                    if (khDate.isBefore(start)) dateOk = false;
                }
                if (endDate != null && !endDate.isEmpty()) {
                    LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");
                    if (khDate.isAfter(end)) dateOk = false;
                }
            }
        }

        return searchOk && spendOk && dateOk;
    }

    @Override
    public long countNewCustomersThisMonth() {
        LocalDateTime now = LocalDateTime.now();
        YearMonth currentMonth = YearMonth.from(now);

        return khachHangRepository.findAll().stream()
                .filter(kh -> kh.getNgayThamGia() != null && YearMonth.from(kh.getNgayThamGia()).equals(currentMonth))
                .count();
    }

    @Override
    public double calculateTotalSpendingByCustomer(String maKhachHang) {
        KhachHang kh = khachHangRepository.findById(maKhachHang).orElse(null);
        if (kh == null || kh.getHoaDons() == null) return 0;

        return kh.getHoaDons().stream()
                .filter(hd -> hd.getTrangThaiHoaDon() == TrangThaiHoaDon.DA_GIAO)
                .mapToDouble(hd -> hd.getThanhTien())
                .sum();
    }

}

