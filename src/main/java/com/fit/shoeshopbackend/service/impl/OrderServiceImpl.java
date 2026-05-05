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
import com.fit.shoeshopbackend.dto.OrderResponseDTO;
import com.fit.shoeshopbackend.dto.OrderRequest;
import com.fit.shoeshopbackend.model.*;
import com.fit.shoeshopbackend.repository.*;
import com.fit.shoeshopbackend.service.ReturnOrderService;
import com.fit.shoeshopbackend.service.EmailService;
import com.fit.shoeshopbackend.service.OrderService;
import com.fit.shoeshopbackend.service.CustomerService;
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
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final PromotionRepository promotionRepository;
    private final ProductDetailRepository productDetailRepository;
    private final OrderDetailRepository orderDetailRepository;

    private final EmailService emailService;
    private final CustomerService customerService;
    private final ReturnOrderService returnOrderService;

    @Override
    public String getCustomerIdByUsername(String username) {
        return customerService.getCustomerIdByUsername(username);
    }

    @Override
    public List<Order> getAllOrder() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> getOrderById(String id) {
        return orderRepository.findById(id);
    }

    @Override
    public Order addOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Order updateOrder(String id, Order order) {
        order.setOrderId(id);
        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(String id) {
        orderRepository.deleteById(id);
    }

    @Override
    public double calculateFinalPrice(Cart cart) {
        // Implementation logic for cart price calculation
        return 0;
    }

    @Override
    public Object getCartSummary(Cart cart) {
        return null;
    }

    @Override
    public Order updateOrderStatus(String id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderStatus(newStatus);
        return orderRepository.save(order);
    }

    @Transactional
    @Override
    public Order handleCancellationRequest(String orderId, boolean approve) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (order.getOrderStatus() != OrderStatus.AWAITING_CANCELLATION) {
            throw new RuntimeException("Order is not in AWAITING_CANCELLATION status.");
        }

        if (approve) {
            String customerId = order.getCustomer().getCustomerId();
            return returnOrderService.cancelOrder(orderId, customerId);
        } else {
            order.setOrderStatus(OrderStatus.SHIPPING);
            return orderRepository.save(order);
        }
    }

    @Override
    @Transactional
    public List<Order> getRecentOrders(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return orderRepository.findAll(pageable).getContent(); // Simple implementation for now
    }

    @Transactional
    @Override
    public OrderResponseDTO createOrderFromCart(OrderRequest request) {
        Order order = new Order();

        order.setOrderId(generateOrderId());
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(request.getTotalAmount());
        order.setUsedPoints(request.getCart().getUsedPoints());
        order.setOrderStatus(OrderStatus.PENDING);

        try {
            order.setPaymentMethod(
                    PaymentMethod.valueOf(request.getUserInfo().getPaymentMethod())
            );
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid payment method");
        }

        Customer customer = customerRepository.findByEmail(request.getUserInfo().getEmail())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        order.setCustomer(customer);

        if (request.getCart().getPromotionId() != null) {
            Promotion promotion = promotionRepository.findById(request.getCart().getPromotionId())
                    .orElse(null);
            order.setPromotion(promotion);
        }

        // Logic to generate Order Details
        List<OrderDetail> details = request.getCart().getItems().stream().map(item -> {
            OrderDetail detail = new OrderDetail();
            detail.setOrderDetailId(generateOrderDetailId());
            detail.setQuantity(item.getQuantity());
            detail.setTotalPrice(item.getPrice() * item.getQuantity());

            ProductDetail productDetail = productDetailRepository.findById(item.getProductDetailId())
                    .orElseThrow(() -> new RuntimeException("Product detail not found"));

            detail.setProductDetail(productDetail);
            detail.setProduct(productDetail.getProduct());
            detail.setOrder(order);

            if (item.getQuantity() > productDetail.getStockQuantity()) {
                throw new RuntimeException("Quantity exceeds stock: " + productDetail.getColor());
            }
            productDetail.setStockQuantity(productDetail.getStockQuantity() - item.getQuantity());
            productDetailRepository.save(productDetail);

            return detail;
        }).toList();

        order.setOrderDetails(details);
        Order savedOrder = orderRepository.save(order);

        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(savedOrder.getOrderId());
        dto.setTotalAmount(savedOrder.getTotalAmount());
        dto.setOrderStatus(savedOrder.getOrderStatus().name());
        
        emailService.sendOrderEmail(customer.getEmail(), savedOrder);

        return dto;
    }

    private String generateOrderId() {
        LocalDate today = LocalDate.now();
        String datePart = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "ORD-" + datePart + "-" + System.currentTimeMillis() % 10000;
    }

    private String generateOrderDetailId() {
        return "ORDD-" + System.nanoTime() % 1000000;
    }

    @Override
    public byte[] exportToExcel() throws IOException {
        List<Order> orders = orderRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Orders");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Order ID");
        headerRow.createCell(1).setCellValue("Customer");
        headerRow.createCell(2).setCellValue("Date");
        headerRow.createCell(3).setCellValue("Total Amount");
        headerRow.createCell(4).setCellValue("Payment");
        headerRow.createCell(5).setCellValue("Status");

        int rowNum = 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Order order : orders) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(order.getOrderId());
            row.createCell(1).setCellValue(order.getCustomer() != null ? order.getCustomer().getFullName() : "Anonymous");
            row.createCell(2).setCellValue(order.getOrderDate() != null ? order.getOrderDate().format(formatter) : "");
            row.createCell(3).setCellValue(order.getTotalAmount() != null ? order.getTotalAmount() : 0);
            row.createCell(4).setCellValue(order.getPaymentMethod() != null ? order.getPaymentMethod().toString() : "");
            row.createCell(5).setCellValue(order.getOrderStatus() != null ? order.getOrderStatus().toString() : "");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }
}
