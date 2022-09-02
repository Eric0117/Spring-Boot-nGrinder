package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Eric
 * @Description
 * @Since 22. 9. 2.
 **/
@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    @GetMapping
    public String testMethod() {
        System.out.println("test Method Called");
        return "Hello World!";
    }

}
