package com.fit.shoeshopbackend.controller;


import com.fit.shoeshopbackend.dto.Cart;
import com.fit.shoeshopbackend.dto.CartItemDTO;
import com.fit.shoeshopbackend.model.ChiTietSanPham;
import com.fit.shoeshopbackend.service.ChiTietSanPhamService;
import com.fit.shoeshopbackend.service.HoaDonService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final ChiTietSanPhamService chiTietSanPhamService;
    private final HoaDonService hoaDonService;
    private static final String CART_SESSION_KEY = "CART";

    private Cart getCartFromSession(HttpSession session) {
        Cart cart = (Cart) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new Cart();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@RequestBody CartItemDTO item, HttpSession session) {
        Cart cart = getCartFromSession(session);

        ChiTietSanPham chiTiet = chiTietSanPhamService.getChiTietSanPhamById(item.getMaChiTiet())
                .orElseThrow(() -> new RuntimeException("Chi tiết sản phẩm không tồn tại"));

        item.setGiaBan(chiTiet.getSanPham().getGiaBan());
        item.setTenSanPham(chiTiet.getSanPham().getTenSanPham());
        item.setHinhAnh(chiTiet.getSanPham().getHinhAnh());
        item.setSize(chiTiet.getSize());
        item.setMau(chiTiet.getMau());
        cart.addItem(item, chiTiet.getSoLuongTonKho());

        session.setAttribute(CART_SESSION_KEY, cart);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/remove")
    public ResponseEntity<Cart> removeFromCart(@RequestBody CartItemDTO item, HttpSession session) {
        Cart cart = getCartFromSession(session);
        cart.removeItem(item.getMaChiTiet());
        session.setAttribute(CART_SESSION_KEY, cart);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/update-quantity")
    public ResponseEntity<Cart> updateQuantity(@RequestBody CartItemDTO item, HttpSession session) {
        Cart cart = getCartFromSession(session);

        ChiTietSanPham chiTiet = chiTietSanPhamService.getChiTietSanPhamById(item.getMaChiTiet())
                .orElseThrow(() -> new RuntimeException("Chi tiết sản phẩm không tồn tại"));

        cart.updateQuantity(item.getMaChiTiet(), item.getSoLuong(), chiTiet.getSoLuongTonKho());
        session.setAttribute(CART_SESSION_KEY, cart);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/apply-promo")
    public ResponseEntity<Cart> applyPromo(@RequestParam String maKhuyenMai, HttpSession session) {
        Cart cart = getCartFromSession(session);
        cart.setMaKhuyenMai(maKhuyenMai);
        session.setAttribute(CART_SESSION_KEY, cart);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/use-points")
    public ResponseEntity<Cart> usePoints(@RequestParam int diem, HttpSession session) {
        Cart cart = getCartFromSession(session);
        cart.setDiemSuDung(diem);
        session.setAttribute(CART_SESSION_KEY, cart);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/clear")
    public ResponseEntity<Cart> clearCart(HttpSession session) {
        Cart cart = getCartFromSession(session);
        cart.clear();
        session.setAttribute(CART_SESSION_KEY, cart);
        return ResponseEntity.ok(cart);
    }

    @GetMapping("/summary")
    public ResponseEntity<Object> getCartSummary(HttpSession session) {
        Cart cart = getCartFromSession(session);
        Object summary = hoaDonService.getCartSummary(cart);
        return ResponseEntity.ok(summary);
    }
}
