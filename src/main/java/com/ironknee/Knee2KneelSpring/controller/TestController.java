package com.ironknee.Knee2KneelSpring.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.text.html.HTML;

@RestController
public class TestController {
    @GetMapping("/healthcheck")
    public String healthcheck() {
        return "ok";
    }
}