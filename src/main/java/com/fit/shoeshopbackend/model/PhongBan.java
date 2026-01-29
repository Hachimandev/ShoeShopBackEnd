package com.fit.shoeshopbackend.model;


public enum PhongBan {
    BanHang("Bán hàng"),
    Kho("Kho"),
    KyThuat("Kỹ thuật"),
    HanhChinh("Hành chính"),
    NhanSu("Nhân sự"),
    TaiChinhKeToan("Tài chính kế toán"),
    Marketing("Marketing");

    private final String displayName;

    PhongBan(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
