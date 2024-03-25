package com.example.order.controller;

import com.example.order.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
public class OrderController {

    private final KafkaTemplate<String, Order> kafkaTemplate;
    private static final String ORDER_SERVICE_URL = "http://localhost:8081/";

    @Autowired
    public OrderController(KafkaTemplate<String, Order> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/placeOrder")
    public String orderNewItem(@RequestBody Order order) {
        try {
//            // Check if connected to Kafka
//            if (!isKafkaConnected()) {
//                return "Not connected to Kafka. Failed to place order.";
//            }

            String topic = "orders";
            kafkaTemplate.send(topic, order);
            return "Order placed successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to place order.";
        }
    }

    @GetMapping("/{orderId}/state")
    public String checkOrderState(@PathVariable String orderId) {
        try {
            // Send GET request to retrieve order state
            RestTemplate restTemplate = new RestTemplate();
            String url = ORDER_SERVICE_URL + "orders/" + orderId + "/state";
            String orderState = restTemplate.getForObject(url, String.class);
            return "Order state: " + orderState;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to check order state.";
        }
    }
}