package com.fit.shoeshopbackend.controller;

import com.fit.shoeshopbackend.dto.Cart;
import com.fit.shoeshopbackend.dto.CartItemDTO;
import com.fit.shoeshopbackend.model.ProductDetail;
import com.fit.shoeshopbackend.service.ProductDetailService;
import com.fit.shoeshopbackend.service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final ProductDetailService productDetailService;
    private final OrderService orderService;
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

        ProductDetail detail = productDetailService.getProductDetailById(item.getProductDetailId())
                .orElseThrow(() -> new RuntimeException("Product detail not found"));

        item.setPrice(detail.getProduct().getPrice());
        item.setProductName(detail.getProduct().getProductName());
        item.setImage(detail.getProduct().getImage());
        item.setSize(detail.getSize());
        item.setColor(detail.getColor());
        cart.addItem(item, detail.getStockQuantity());

        session.setAttribute(CART_SESSION_KEY, cart);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/remove")
    public ResponseEntity<Cart> removeFromCart(@RequestBody CartItemDTO item, HttpSession session) {
        Cart cart = getCartFromSession(session);
        cart.removeItem(item.getProductDetailId());
        session.setAttribute(CART_SESSION_KEY, cart);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/update-quantity")
    public ResponseEntity<Cart> updateQuantity(@RequestBody CartItemDTO item, HttpSession session) {
        Cart cart = getCartFromSession(session);

        ProductDetail detail = productDetailService.getProductDetailById(item.getProductDetailId())
                .orElseThrow(() -> new RuntimeException("Product detail not found"));

        cart.updateQuantity(item.getProductDetailId(), item.getQuantity(), detail.getStockQuantity());
        session.setAttribute(CART_SESSION_KEY, cart);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/apply-promo")
    public ResponseEntity<Cart> applyPromo(@RequestParam String promotionId, HttpSession session) {
        Cart cart = getCartFromSession(session);
        cart.setPromotionId(promotionId);
        session.setAttribute(CART_SESSION_KEY, cart);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/use-points")
    public ResponseEntity<Cart> usePoints(@RequestParam int points, HttpSession session) {
        Cart cart = getCartFromSession(session);
        cart.setUsedPoints(points);
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
        Object summary = orderService.getCartSummary(cart);
        return ResponseEntity.ok(summary);
    }
}
