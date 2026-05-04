package com.fit.shoeshopbackend.service;



import com.fit.shoeshopbackend.dto.Cart;
import com.fit.shoeshopbackend.dto.HoaDonResponseDTO;
import com.fit.shoeshopbackend.dto.OrderRequest;
import com.fit.shoeshopbackend.model.HoaDon;
import com.fit.shoeshopbackend.model.TrangThaiHoaDon;

import java.io.IOException;
import java.util.List;
import java.util.Optional;



public interface HoaDonService {
    List<HoaDon> getAllHoaDon();
    Optional<HoaDon> getHoaDonById(String id);
    HoaDon addHoaDon(HoaDon hoaDon);
    HoaDon updateHoaDon(String id, HoaDon hoaDon);
    void deleteHoaDon(String id);

    double calculateFinalPrice(Cart cart);

    Object getCartSummary(Cart cart);

    HoaDonResponseDTO createHoaDonFromCart(OrderRequest request);
    HoaDon updateOrderStatus(String maHoaDon, TrangThaiHoaDon newStatus);
    String getKhachHangIdByUsername(String username);
    HoaDon handleCancellationRequest(String maHoaDon, boolean approve);
    List<HoaDon> getRecentOrders(int limit);

    byte[] exportToExcel() throws IOException;
}

