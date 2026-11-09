package com.we13ailab.websocket.controller;

import java.time.Instant;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NotificationController {

    private volatile OrderStatusUpdate latestStatus =
            new OrderStatusUpdate("pending", Instant.now().toString());

    @GetMapping("/restaurant")
    public String restaurant() {
        return "restaurant";
    }

    @GetMapping("/customer")
    public String customer() {
        return "customer";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/client1")
    public String client1() {
        return "client1";
    }

    @GetMapping("/client2")
    public String client2() {
        return "client2";
    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/notifications")
    public NotificationMessage sendMessage(String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("A notification message is required.");
        }
        return new NotificationMessage(message.trim(), Instant.now().toString());
    }

    @MessageMapping("/order-status")
    @SendTo("/topic/order-status")
    public OrderStatusUpdate updateOrderStatus(OrderStatusRequest request) {
        if (request == null || request.status() == null) {
            throw new IllegalArgumentException("A status is required.");
        }

        String status = request.status().toLowerCase();
        if (!status.equals("prepared") && !status.equals("pickedup") && !status.equals("delivered")) {
            throw new IllegalArgumentException("Unsupported order status.");
        }

        latestStatus = new OrderStatusUpdate(status, Instant.now().toString());
        return latestStatus;
    }

    @SubscribeMapping("/order-status")
    public OrderStatusUpdate currentOrderStatus() {
        return latestStatus;
    }

    public record OrderStatusRequest(String status) {
    }

    public record OrderStatusUpdate(String status, String timestamp) {
    }

    public record NotificationMessage(String message, String timestamp) {
    }
}
