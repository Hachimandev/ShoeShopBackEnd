package com.fit.shoeshopbackend.service.impl;



import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import com.fit.shoeshopbackend.dto.Cart;
import com.fit.shoeshopbackend.dto.HoaDonResponseDTO;
import com.fit.shoeshopbackend.dto.OrderRequest;
import com.fit.shoeshopbackend.model.*;
import com.fit.shoeshopbackend.repository.*;
import com.fit.shoeshopbackend.service.DonHuyTraHangService;
import com.fit.shoeshopbackend.service.EmailService;
import com.fit.shoeshopbackend.service.HoaDonService;
import com.fit.shoeshopbackend.service.KhachHangService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;



import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HoaDonServiceImpl implements HoaDonService {

    private final HoaDonRepository hoaDonRepository;
    private final KhachHangRepository khachHangRepo;
    private final KhuyenMaiRepository khuyenMaiRepo;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final ChiTietHoaDonRepository chiTietHoaDonRepository;

    private final EmailService emailService;
    private final KhachHangService khachHangService;
    private final DonHuyTraHangService donHuyTraHangService;

    @Override
    public String getKhachHangIdByUsername(String username) {
        // Ủy quyền (Delegate) logic tìm kiếm cho KhachHangService
        return khachHangService.getKhachHangIdByUsername(username);
    }

    @Override
    public List<HoaDon> getAllHoaDon() {
        return hoaDonRepository.findAll();
    }

    @Override
    public Optional<HoaDon> getHoaDonById(String id) {
        return hoaDonRepository.findById(id);
    }

    @Override
    public HoaDon addHoaDon(HoaDon hoaDon) {
        return hoaDonRepository.save(hoaDon);
    }

    @Override
    public HoaDon updateHoaDon(String id, HoaDon hoaDon) {
        hoaDon.setMaHoaDon(id);
        return hoaDonRepository.save(hoaDon);
    }

    @Override
    public void deleteHoaDon(String id) {
        hoaDonRepository.deleteById(id);
    }

    @Override
    public double calculateFinalPrice(Cart cart) {
        return 0;
    }

    @Override
    public Object getCartSummary(Cart cart) {
        return null;
    }

    @Override
    public HoaDon updateOrderStatus(String id, TrangThaiHoaDon newStatus) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));
        hoaDon.setTrangThaiHoaDon(newStatus);
        return hoaDonRepository.save(hoaDon);
    }

    @Transactional
    @Override
    public HoaDon handleCancellationRequest(String maHoaDon, boolean approve) {
        HoaDon hoaDon = hoaDonRepository.findById(maHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn " + maHoaDon));

        if (hoaDon.getTrangThaiHoaDon() != TrangThaiHoaDon.CHO_HUY) {
            throw new RuntimeException("Đơn hàng không ở trạng thái Chờ hủy.");
        }

        if (approve) {
            String maKhachHang = hoaDon.getKhachHang().getMaKhachHang();
            return donHuyTraHangService.cancelOrder(maHoaDon, maKhachHang);

        } else {
            hoaDon.setTrangThaiHoaDon(TrangThaiHoaDon.DANG_GIAO);
            return hoaDonRepository.save(hoaDon);
        }
    }

    @Override
    @Transactional
    public List<HoaDon> getRecentOrders(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return hoaDonRepository.findRecentWithCustomer(pageable);
    }

    @Transactional
    @Override
    public HoaDonResponseDTO createHoaDonFromCart(OrderRequest request) {
        HoaDon hoaDon = new HoaDon();

        hoaDon.setMaHoaDon(generateMaHoaDon());
        hoaDon.setNgayDat(LocalDateTime.now());
        hoaDon.setThanhTien(request.getThanhTien());
        hoaDon.setDiemSuDung(request.getCart().getDiemSuDung());
        hoaDon.setTrangThaiHoaDon(TrangThaiHoaDon.CHO_XAC_NHAN);

        try {
            hoaDon.setPhuongThucThanhToan(
                    PhuongThucThanhToan.valueOf(request.getUserInfo().getPhuongThucThanhToan())
            );
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Phương thức thanh toán không hợp lệ");
        }

        KhachHang kh = khachHangRepo.findByEmail(request.getUserInfo().getEmail())
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
        hoaDon.setKhachHang(kh);

        if (request.getCart().getMaKhuyenMai() != null) {
            KhuyenMai km = khuyenMaiRepo.findById(request.getCart().getMaKhuyenMai())
                    .orElse(null);
            hoaDon.setKhuyenMai(km);
        }

        Pageable limitOne = PageRequest.of(0, 1);
        List<ChiTietHoaDon> list = chiTietHoaDonRepository.findAllOrderByMaChiTietHoaDonDesc(limitOne);
        AtomicInteger nextCTHD = new AtomicInteger(1);
        if (!list.isEmpty()) {
            String lastMa = list.get(0).getMaChiTietHoaDon();
            if (lastMa.startsWith("CTHD")) {
                try {
                    nextCTHD.set(Integer.parseInt(lastMa.substring(4)) + 1);
                } catch (NumberFormatException ignored) {}
            }
        }

        List<ChiTietHoaDon> chiTiets = request.getCart().getItems().stream().map(item -> {
            ChiTietHoaDon ct = new ChiTietHoaDon();
            ct.setMaChiTietHoaDon(String.format("CTHD%04d", nextCTHD.getAndIncrement()));
            ct.setSoLuong(item.getSoLuong());
            ct.setTongTien(item.getGiaBan() * item.getSoLuong());

            ChiTietSanPham ctsp = chiTietSanPhamRepository.findById(item.getMaChiTiet())
                    .orElseThrow(() -> new RuntimeException("Chi tiết sản phẩm không tồn tại"));

            ct.setChiTietSanPham(ctsp);
            ct.setSanPham(ctsp.getSanPham());
            ct.setHoaDon(hoaDon);

            if (item.getSoLuong() > ctsp.getSoLuongTonKho()) {
                throw new RuntimeException("Số lượng đặt vượt quá tồn kho: " + ctsp.getMau());
            }
            ctsp.setSoLuongTonKho(ctsp.getSoLuongTonKho() - item.getSoLuong());
            chiTietSanPhamRepository.save(ctsp);

            return ct;
        }).toList();

        hoaDon.setChiTietHoaDons(chiTiets);
        HoaDon savedHoaDon = hoaDonRepository.save(hoaDon);

        HoaDonResponseDTO dto = new HoaDonResponseDTO();
        dto.setMaHoaDon(savedHoaDon.getMaHoaDon());
        dto.setThanhTien(savedHoaDon.getThanhTien());
        dto.setTrangThaiHoaDon(savedHoaDon.getTrangThaiHoaDon().name());
        emailService.sendOrderEmail(kh.getEmail(), savedHoaDon);

        return dto;
    }


    private String generateMaHoaDon() {
        LocalDate today = LocalDate.now();
        String datePart = today.format(DateTimeFormatter.ofPattern("ddMMyyyy"));
        Pageable limitOne = PageRequest.of(0, 1);
        List<HoaDon> list = hoaDonRepository.findByMaHoaDonStartingWithOrderByMaHoaDonDesc("HD" + datePart, limitOne);

        int next = 1;
        if (!list.isEmpty()) {
            String lastMa = list.get(0).getMaHoaDon();
            String numPart = lastMa.substring(10);
            try {
                next = Integer.parseInt(numPart) + 1;
            } catch (NumberFormatException e) {
                next = 1;
            }
        }

        return String.format("HD%s%04d", datePart, next);
    }

    private String generateMaChiTietHoaDon() {
        Pageable limitOne = PageRequest.of(0, 1);
        List<ChiTietHoaDon> list = chiTietHoaDonRepository.findAllOrderByMaChiTietHoaDonDesc(limitOne);
        int next = 1;
        if (!list.isEmpty()) {
            String lastMa = list.get(0).getMaChiTietHoaDon();
            if (lastMa.startsWith("CTHD")) {
                try {
                    next = Integer.parseInt(lastMa.substring(4)) + 1;
                } catch (NumberFormatException e) {
                    next = 1;
                }
            }
        }
        return String.format("CTHD%04d", next);
    }

    @Override
    public byte[] exportToExcel() throws IOException {
        List<HoaDon> hoaDons = hoaDonRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Hóa đơn");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Mã hóa đơn");
        headerRow.createCell(1).setCellValue("Khách hàng");
        headerRow.createCell(2).setCellValue("Ngày đặt");
        headerRow.createCell(3).setCellValue("Tổng tiền");
        headerRow.createCell(4).setCellValue("Thanh toán");
        headerRow.createCell(5).setCellValue("Trạng thái");

        // Set column widths
        sheet.setColumnWidth(0, 15 * 256);
        sheet.setColumnWidth(1, 20 * 256);
        sheet.setColumnWidth(2, 20 * 256);
        sheet.setColumnWidth(3, 15 * 256);
        sheet.setColumnWidth(4, 20 * 256);
        sheet.setColumnWidth(5, 18 * 256);

        // Fill data
        int rowNum = 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (HoaDon hoaDon : hoaDons) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(hoaDon.getMaHoaDon());
            row.createCell(1).setCellValue(hoaDon.getKhachHang() != null ? hoaDon.getKhachHang().getHoTen() : "Ẩn danh");
            row.createCell(2).setCellValue(hoaDon.getNgayDat() != null ? hoaDon.getNgayDat().format(formatter) : "");
            row.createCell(3).setCellValue(hoaDon.getThanhTien() != null ? hoaDon.getThanhTien() : 0);
            row.createCell(4).setCellValue(hoaDon.getPhuongThucThanhToan() != null ? hoaDon.getPhuongThucThanhToan().toString() : "");
            row.createCell(5).setCellValue(hoaDon.getTrangThaiHoaDon() != null ? hoaDon.getTrangThaiHoaDon().toString() : "");
        }

        // Convert to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }
}
