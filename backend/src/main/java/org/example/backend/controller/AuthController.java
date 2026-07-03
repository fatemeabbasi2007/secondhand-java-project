package org.example.backend.controller;

import org.example.backend.model.User;
import org.example.backend.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * سناریوی ثبت‌نام کاربر جدید
     * فرانت‌اند داده‌ها را به آدرس POST http://localhost:8080/api/auth/register می‌فرستد
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        // لاجیک ثبت‌نام در سرویس اجرا می‌شود
        boolean success = authService.register(user);

        if (success) {
            // ۹. پیام موفقیت به Frontend برگردانده می‌شود.
            Map<String, String> response = new HashMap<>();
            response.put("message", "ثبت نام کاربر جدید با موفقیت انجام شد.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            // در صورتی که نام کاربری تکراری باشد یا اطلاعات معتبر نباشد
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 400);
            errorResponse.put("message", "نام کاربری یا شماره تماس تکراری است.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * سناریوی ورود کاربر به سیستم
     * فرانت‌اند داده‌ها را به آدرس POST http://localhost:8080/api/auth/login می‌فرستد
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestParam String username, @RequestParam String password) {
        Optional<User> authenticatedUser = authService.login(username, password);

        if (authenticatedUser.isPresent()) {
            User user = authenticatedUser.get();
            // ۸. اطلاعات لازم کاربر به Frontend برگردانده می‌شود.
            // ۹. بر اساس نقش کاربر (مدیر یا عادی)، صفحه مناسب در JavaFX نمایش داده می‌شود.
            return ResponseEntity.ok(user);
        } else {
            // مدیریت خطاها در صورت اشتباه بودن رمز، عدم وجود کاربر یا مسدود بودن
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 401);
            errorResponse.put("message", "اطلاعات ورود نادرست است یا حساب کاربری مسدود شده است.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}