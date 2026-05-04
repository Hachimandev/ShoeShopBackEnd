package com.fit.shoeshopbackend.dto;


import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class Cart {
    private List<CartItemDTO> items = new ArrayList<>();
    private String maKhuyenMai;
    private int diemSuDung;

    public Cart() {}

    public Cart(List<CartItemDTO> items) {
        this.items = items;
    }

    public double getSubtotal() {
        return items.stream()
                .mapToDouble(item -> item.getGiaBan() * item.getSoLuong())
                .sum();
    }

    public void addItem(CartItemDTO newItem, int soLuongTonKho) {
        boolean found = false;
        for (CartItemDTO item : items) {
            if (item.getMaChiTiet().equals(newItem.getMaChiTiet())) {
                int updatedQty = item.getSoLuong() + newItem.getSoLuong();
                item.setSoLuong(Math.min(updatedQty, soLuongTonKho));
                found = true;
                break;
            }
        }
        if (!found) {
            newItem.setSoLuong(Math.min(newItem.getSoLuong(), soLuongTonKho));
            items.add(newItem);
        }
    }

    public void updateQuantity(String maChiTiet, int soLuong, int soLuongTonKho) {
        for (CartItemDTO item : items) {
            if (item.getMaChiTiet().equals(maChiTiet)) {
                item.setSoLuong(Math.min(soLuong, soLuongTonKho));
                break;
            }
        }
    }

    public void removeItem(String maChiTiet) {
        items.removeIf(item -> item.getMaChiTiet().equals(maChiTiet));
    }

    public void clear() {
        items.clear();
        maKhuyenMai = null;
        diemSuDung = 0;
    }
}
