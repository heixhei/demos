package com.baiyuechu.controller;

import com.baiyuechu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/user"})
public class HelloController {
    private Integer stock = 1;
    @Autowired
    private UserService userService;

    public HelloController() {
    }

    @GetMapping({"/hello"})
    public String hel1lo() {
        Integer var1 = this.stock;
        Integer var2 = this.stock = this.stock - 1;
        System.out.println(this.stock);
        return "hello";
    }
}
