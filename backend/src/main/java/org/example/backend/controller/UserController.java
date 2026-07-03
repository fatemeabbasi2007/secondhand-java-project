package org.example.backend.controller;

import org.example.backend.model.User;
import org.example.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ==========================================
    // ۱. بخش احراز هویت و حساب کاربری (Authentication)
    // ==========================================

    /**
     * سناریوی ثبت نام کاربر جدید
     * فرانت‌اند داده‌ها را به آدرس POST http://localhost:8080/api/users/register می‌فرستد
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        boolean isRegistered = userService.registerUser(user);
        if (isRegistered) {
            return ResponseEntity.status(HttpStatus.CREATED).body("ثبت‌نام با موفقیت انجام شد.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("خطا: نام کاربری یا شماره تماس تکراری است.");
        }
    }

    /**
     * سناریوی ورود کاربر به سیستم
     * فرانت‌اند داده‌ها را به آدرس POST http://localhost:8080/api/users/login می‌فرستد
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        Optional<User> loggedInUser = userService.loginUser(username, password);

        if (loggedInUser.isPresent()) {
            // فرانت‌باید بر اساس نقش کاربر (مدیر یا عادی) صفحه مناسب را باز کند
            return ResponseEntity.ok(loggedInUser.get());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("خطا: اطلاعات ورود نادرست است یا حساب شما مسدود شده است.");
        }
    }

    /**
     * خروج از حساب کاربری
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // در ساختار ساده، فرانت‌اند با پاک کردن توکن/اطلاعات کاربر از حافظه خارج می‌شود
        return ResponseEntity.ok("خروج از سیستم موفقیت‌آمیز بود.");
    }

    // ==========================================
    // ۲. بخش علاقه‌مندی‌ها (Favorites)
    // ==========================================

    /**
     * افزودن آگهی به علاقه‌مندی‌ها
     */
    @PostMapping("/{userId}/favorites/{adId}")
    public ResponseEntity<String> addToFavorites(@PathVariable String userId, @PathVariable String adId) {
        boolean success = userService.addAdvertisementToFavorites(userId, adId);
        if (success) {
            return ResponseEntity.ok("آگهی به لیست علاقه‌مندی‌ها اضافه شد.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("عملیات ناموفق بود.");
    }

    /**
     * حذف آگهی از علاقه‌مندی‌ها
     */
    @DeleteMapping("/{userId}/favorites/{adId}")
    public ResponseEntity<String> removeFromFavorites(@PathVariable String userId, @PathVariable String adId) {
        boolean success = userService.removeAdvertisementFromFavorites(userId, adId);
        if (success) {
            return ResponseEntity.ok("آگهی از لیست علاقه‌مندی‌ها حذف شد.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("آگهی پیدا نشد.");
    }

    /**
     * مشاهده لیست علاقه‌مندی‌ها
     */
    @GetMapping("/{userId}/favorites")
    public ResponseEntity<List<String>> getFavorites(@PathVariable String userId) {
        List<String> favoriteIds = userService.getUserFavoriteAdIds(userId);
        return ResponseEntity.ok(favoriteIds);
    }

    // ==========================================
    // ۳. پنل مدیریت سیستم (Admin Operations)
    // ==========================================

    /**
     * مشاهده لیست تمام کاربران توسط مدیر
     * GET http://localhost:8080/api/users/admin/all
     */
    @GetMapping("/admin/all")
    public ResponseEntity<List<User>> getAllUsersForAdmin() {
        // TODO: در آینده باید بررسی شود که درخواست دهنده حتماً نقش ADMIN داشته باشد.
        List<User> users = userService.getAllUsersForAdmin();
        return ResponseEntity.ok(users);
    }

    /**
     * مسدود کردن کاربر متخلف توسط مدیر
     */
    @PutMapping("/admin/block/{userId}")
    public ResponseEntity<String> blockUser(@PathVariable String userId) {
        boolean success = userService.blockUser(userId);
        if (success) {
            return ResponseEntity.ok("کاربر با موفقیت مسدود شد.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("کاربر یافت نشد.");
    }

    /**
     * فعال‌سازی مجدد کاربر مسدود شده توسط مدیر
     */
    @PutMapping("/admin/unblock/{userId}")
    public ResponseEntity<String> unblockUser(@PathVariable String userId) {
        boolean success = userService.unblockUser(userId);
        if (success) {
            return ResponseEntity.ok("حساب کاربر مجدداً فعال شد.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("کاربر یافت نشد.");
    }
}