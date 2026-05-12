package com.fit.shoeshopbackend.service.impl;


import com.fit.shoeshopbackend.model.OrderDetail;
import com.fit.shoeshopbackend.model.Order;
import com.fit.shoeshopbackend.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendOrderEmail(String to, Order Order) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Xác nhận đơn hàng " + Order.getOrderId());

            StringBuilder content = new StringBuilder();
            content.append("<h2>Xin chào ").append(Order.getCustomer().getFullName()).append("</h2>");
            content.append("<p>Cảm ơn bạn đã đặt hàng tại shop!</p>");
            content.append("<p><strong>Mã đơn hàng:</strong> ").append(Order.getOrderId()).append("</p>");
            content.append("<p><strong>Ngày đặt:</strong> ").append(Order.getOrderDate()).append("</p>");
            content.append("<p><strong>Trạng thái:</strong> ").append(Order.getOrderStatus()).append("</p>");

            content.append("<h3>Chi tiết đơn hàng:</h3>");
            content.append("<table border='1' cellpadding='5' cellspacing='0'>");
            content.append("<tr><th>Sản phẩm</th><th>Số lượng</th><th>Giá</th></tr>");

            for (OrderDetail ct : Order.getOrderDetails()) {
                content.append("<tr>")
                        .append("<td>").append(ct.getProduct().getProductId()).append("</td>")
                        .append("<td>").append(ct.getQuantity()).append("</td>")
                        .append("<td>").append(ct.getTotalPrice()).append("</td>")
                        .append("</tr>");
            }

            content.append("</table>");
            content.append("<p><strong>Tổng tiền:</strong> ").append(Order.getTotalAmount()).append(" VND</p>");
            content.append("<br><p>Trân trọng,<br>Shop Giày Dép</p>");

            helper.setText(content.toString(), true);

            mailSender.send(message);
            System.out.println("✔ Email đã được gửi đến " + to);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Gửi email thất bại, nhưng đơn hàng vẫn được tạo.");
        }
    }
}










