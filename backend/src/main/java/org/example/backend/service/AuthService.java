package org.example.backend.service;

import org.example.backend.model.User;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {

    // private final UserRepository userRepository;

    public AuthService() {
        // Constructor
    }

    /**
     * سناریوی ثبت‌نام کاربر (User Registration):
     * ۵. بررسی اینکه نام کاربری یا شماره تماس قبلاً ثبت نشده باشد.
     * ۷. نقش پیش‌فرض کاربر برابر با کاربر عادی (e.g., "USER") قرار می‌گیرد.
     * ۸. وضعیت حساب کاربر برابر با فعال (e.g., "ACTIVE") قرار می‌گیرد.
     */
    public boolean register(User newUser) {
        // TODO: بررسی تکراری نبودن نام کاربری در فایل JSON
        // TODO: بررسی تکراری نبودن شماره تماس در فایل JSON
        // TODO: newUser.setRole("USER");
        // TODO: newUser.setStatus("ACTIVE");
        // TODO: ذخیره در فایل مخزن (UserRepository)
        return true;
    }

    /**
     * سناریوی ورود کاربر (User Login):
     * ۴. وجود کاربر را بررسی می‌کند.
     * ۵. رمز عبور وارد شده با رمز عبور ذخیره شده مقایسه می‌شود.
     * ۶. وضعیت حساب کاربر بررسی می‌شود (مسدود نباشد).
     */
    public Optional<User> login(String username, String password) {
        // TODO: پیدا کردن کاربر بر اساس نام کاربری
        // TODO: چک کردن مسدود بودن (Status != "BANNED")
        // TODO: تطبیق پسورد
        return Optional.empty();
    }
}