package org.example.backend.service;

import org.example.backend.model.Review;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ReviewService {

    public ReviewService() {
        // Constructor
    }

    /**
     * سناریوی امتیازدهی به فروشنده:
     * ۶. بررسی می‌کند که کاربر به خودش امتیاز ندهد.
     * ۷. بررسی می‌کند امتیاز در بازه ۱ تا ۵ باشد.
     * ۸. بررسی می‌کند این کاربر قبلاً برای همین آگهی به فروشنده امتیاز نداده باشد.
     */
    public boolean submitReview(Review review) {
        // TODO: بررسی شروط بالا از روی ریپازیتوری‌ها
        // TODO: ذخیره نظر در فایل reviews.json و به‌روزرسانی میانگین امتیاز فروشنده
        return true;
    }

    /**
     * دریافت تمام نظرات و امتیازهای ثبت شده برای یک کاربر (فروشنده)
     */
    public List<Review> getReviewsForUser(String targetUsername) {
        // TODO: فیلتر کردن نظرات بر اساس نام کاربری فروشنده
        return Collections.emptyList();
    }
}