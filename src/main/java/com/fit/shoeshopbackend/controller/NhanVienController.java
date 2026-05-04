package com.fit.shoeshopbackend.controller;


import com.fit.shoeshopbackend.model.NhanVien;
import com.fit.shoeshopbackend.model.TrangThaiLamViec;
import com.fit.shoeshopbackend.service.NhanVienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/staffs")
@CrossOrigin(origins = "*")
public class NhanVienController {

    private final NhanVienService nhanVienService;

    @Autowired
    public NhanVienController(NhanVienService nhanVienService) {
        this.nhanVienService = nhanVienService;
    }

    @GetMapping
    public ResponseEntity<Page<NhanVien>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String phongBan,
            @RequestParam(required = false) String trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<NhanVien> result = nhanVienService.search(search, phongBan, trangThai, PageRequest.of(page, size));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NhanVien> get(@PathVariable String id) {
        return nhanVienService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<NhanVien> create(@RequestBody NhanVien nhanVien) {
        validateNhanVien(nhanVien, true);
        if (nhanVienService.findById(nhanVien.getMaNhanVien()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã nhân viên đã tồn tại");
        }
        // Set default status: DangLam (Đang làm việc)
        if (nhanVien.getTrangThaiLamViec() == null) {
            nhanVien.setTrangThaiLamViec(TrangThaiLamViec.DangLam);
        }
        NhanVien saved = nhanVienService.save(nhanVien);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NhanVien> update(@PathVariable String id, @RequestBody NhanVien nhanVien) {
        validateNhanVien(nhanVien, false);
        if (nhanVien.getMaNhanVien() != null && !nhanVien.getMaNhanVien().equals(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã nhân viên không khớp");
        }
        return nhanVienService.update(id, nhanVien)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        nhanVienService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export")
    public void exportCsv(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String phongBan,
            @RequestParam(required = false) String trangThai,
            HttpServletResponse response
    ) throws IOException {
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=staffs.csv");

        List<NhanVien> list = nhanVienService.searchAll(search, phongBan, trangThai);

        // write header
        String header = "maNhanVien,hoTen,sdt,cccd,ngaySinh,trangThaiLamViec,gioiTinh,chucVu,phongBan\n";
        response.getWriter().write(header);

        for (NhanVien nv : list) {
            String line = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                    nv.getMaNhanVien(),
                    nv.getHoTen() == null ? "" : nv.getHoTen().replaceAll(",", " "),
                    nv.getSdt() == null ? "" : nv.getSdt(),
                    nv.getCccd() == null ? "" : nv.getCccd(),
                    nv.getNgaySinh() == null ? "" : nv.getNgaySinh().toString(),
                    nv.getTrangThaiLamViec() == null ? "" : nv.getTrangThaiLamViec().name(),
                    nv.getGioiTinh() == null ? "" : nv.getGioiTinh().name(),
                    nv.getChucVu() == null ? "" : nv.getChucVu().name(),
                    nv.getPhongBan() == null ? "" : nv.getPhongBan().name()
            );
            response.getWriter().write(line);
        }

        response.getWriter().flush();
    }

    private void validateNhanVien(NhanVien nv, boolean isCreate) {
        if (nv == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dữ liệu nhân viên trống");

        // Mã nhân viên validation
        if (nv.getMaNhanVien() == null || !nv.getMaNhanVien().matches("^NV\\d{3}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã nhân viên phải có dạng NVXXX (VD: NV001)");
        }

        // Họ tên validation (Vietnamese name standardization)
        if (nv.getHoTen() == null || nv.getHoTen().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Họ tên không được để trống");
        }
//		if (!nv.getHoTen().matches("^[a-zA-ZÀÁẢÃẠĂẰẮẲẳẴÂẦẤẨẫẬĐèÉẺẼẸêềếểễệìíỉĩịòóỏõọôồốổỗộơờớởỡợùúủũụưừứửữựỳýỷỹỵżźżŕ\\s]+$")) {
//			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Họ tên chỉ được chứa chữ cái và khoảng trắng");
//		}

        // Email validation
        if (nv.getEmail() == null || nv.getEmail().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email không được để trống");
        }
        if (!nv.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email không hợp lệ");
        }

        // Số điện thoại validation (10 digits starting with 0)
        if (nv.getSdt() != null && !nv.getSdt().isEmpty()) {
            if (!nv.getSdt().matches("^0\\d{9}$")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số điện thoại phải gồm 10 chữ số bắt đầu từ 0");
            }
        }

        // CCCD validation (12 digits starting with 0)
        if (nv.getCccd() != null && !nv.getCccd().isEmpty()) {
            if (!nv.getCccd().matches("^0\\d{11}$")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CCCD phải gồm 12 chữ số bắt đầu từ 0");
            }
        }

        // Ngày sinh validation (not in future, age >= 15)
        if (nv.getNgaySinh() != null) {
            LocalDate today = LocalDate.now();
            LocalDate birthDate = nv.getNgaySinh().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            if (birthDate.isAfter(today)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày sinh không được là ngày trong tương lai");
            }

            int age = today.getYear() - birthDate.getYear();
            if (today.getMonthValue() < birthDate.getMonthValue() ||
                    (today.getMonthValue() == birthDate.getMonthValue() && today.getDayOfMonth() < birthDate.getDayOfMonth())) {
                age--;
            }

            if (age < 15) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tuổi phải từ 15 trở lên");
            }
        }

        // Chức vụ validation
        if (nv.getChucVu() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chức vụ không được để trống");
        }

        // Phòng ban validation
        if (nv.getPhongBan() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phòng ban không được để trống");
        }

        // Giới tính validation
        if (nv.getGioiTinh() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giới tính không được để trống");
        }
    }

}
