package com.poc.web;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poc.model.Item;

@RestController
public class ItemsController {

    private static final String template = "The item is, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/getItem")
    public Item information(@RequestParam(value="item", defaultValue="Milk") String name) {
        return new Item(counter.incrementAndGet(), String.format(template, name));
    }

    @RequestMapping("/coupon")
    public String greeting(@RequestParam(value="item") String name) {
        return "The coupon code is: XASFDA";
    }
}
