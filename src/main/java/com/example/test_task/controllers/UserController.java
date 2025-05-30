package com.example.test_task.controllers;


import com.example.test_task.exeptions.InvalidInitDataException;
import com.example.test_task.exeptions.UserDataProcessingException;
import com.example.test_task.models.User;
import com.example.test_task.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/")
    public String home() {
        return "webapp-init";
    }

    @PostMapping("/user-info")
    public String handleUserInfo(@RequestParam String initData, Model model) {
        try {
            User user = userService.processTelegramInitData(initData);
            model.addAttribute("user", user);
            return "user-info";
        } catch (InvalidInitDataException exception) {
            log.warn("Invalid initData received: {}", exception.getMessage());
            model.addAttribute("error", "Authentication failed");
            return "errors/error-auth";
        } catch (UserDataProcessingException | JsonProcessingException exception) {
            log.error("Data processing error: {}", exception.getMessage(), exception);
            model.addAttribute("error", "Error processing your data");
            return "errors/error-processing";
        } catch (Exception exception) {
            log.error("Unexpected error: {}", exception.getMessage(), exception);
            model.addAttribute("error", "Internal server error");
            return "errors/error-general";
        }
    }
}