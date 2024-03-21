package com.example.order.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Order {
    private String item_name;
    private int item_quantity;
}
