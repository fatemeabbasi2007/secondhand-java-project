package org.example.backend.service;

import org.example.backend.model.User;
import org.example.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // --- سناریوهای کاربر عادی ---

    /**
     * سناریوی ثبت‌نام کاربر: بررسی تکراری نبودن نام کاربری/شماره تماس، تنظیم نقش پیش‌فرض (عادی) و وضعیت فعال
     */
    public boolean registerUser(User newUser) {
        // TODO: بک‌اند باید بررسی کند نام کاربری یا شماره تماس تکراری نباشد
        // TODO: تنظیم نقش به عادی و وضعیت به فعال و سپس ذخیره در مخزن
        return true;
    }

    /**
     * سناریوی ورود کاربر: بررسی وجود کاربر، صحت رمز عبور و مسدود نبودن حساب
     */
    public Optional<User> loginUser(String username, String password) {
        // TODO: بررسی نام کاربری، پسورد و وضعیت مسدود بودن کاربر
        return Optional.empty();
    }

    // --- سناریوهای لیست علاقه‌مندی‌ها (Favorites) ---

    public boolean addAdvertisementToFavorites(String userId, String adId) {
        // TODO: اضافه کردن آیدی آگهی به لیست علاقه‌مندی‌های کاربر
        return true;
    }

    public boolean removeAdvertisementFromFavorites(String userId, String adId) {
        // TODO: حذف آگهی از لیست علاقه‌مندی‌ها
        return true;
    }

    public List<String> getUserFavoriteAdIds(String userId) {
        // TODO: بازگرداندن لیست آیدی آگهی‌های مورد علاقه کاربر
        return Collections.emptyList();
    }

    // --- سناریوهای مدیر سیستم (Admin Panel) ---

    /**
     * مشاهده لیست تمام کاربران سیستم توسط مدیر
     */
    public List<User> getAllUsersForAdmin() {
        // TODO: واکشی تمام کاربران از فایل JSON برای ادمین
        return userRepository.findAll();
    }

    /**
     * مسدود کردن کاربر متخلف
     */
    public boolean blockUser(String userId) {
        // TODO: تغییر وضعیت حساب کاربر به "BANNED" یا "BLOCKED"
        return true;
    }

    /**
     * فعال‌سازی مجدد کاربر مسدود شده
     */
    public boolean unblockUser(String userId) {
        // TODO: تغییر وضعیت حساب کاربر به "ACTIVE"
        return true;
    }
}