package com.instantsolutions.larimarpharma.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fe")
public class FEController {
    @GetMapping("/test")
    public String fe() {
        return "OK";
    }
}
