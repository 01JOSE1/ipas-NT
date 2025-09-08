package com.ipas.ipas.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WebController {
    
    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
    
    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }
    
    @GetMapping("/clients")
    public String clients() {
        return "clients";
    }
    
    @GetMapping("/policies")
    public String policies() {
        return "policies";
    }
    
    @GetMapping("/users")
    public String users() {
        return "users";
    }
    
    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }
    
    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam String token) {
        return "reset-password";
    }

    @GetMapping("/waiting-for-authorization")
    public String waitingForAuthorization() {
        return "waiting-for-authorization";
    }
}