package org.example.backend.controller;

import org.example.backend.model.Review;
import org.example.backend.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * سناریوی ثبت امتیاز و نظر جدید برای فروشنده
     * POST http://localhost:8080/api/reviews/submit
     */
    @PostMapping("/submit")
    public ResponseEntity<String> submitReview(@RequestBody Review review) {

        // بررسی اولیه محدوده امتیاز برای رعایت قرارداد خطاها
        if (review.getRating() < 1 || review.getRating() > 5) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("خطا: امتیاز باید بین ۱ تا ۵ باشد.");
        }

        if (review.getReviewerId().equals(review.getTargetUserId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("خطا: کاربر نمی‌تواند به خودش امتیاز دهد.");
        }

        boolean success = reviewService.submitReview(review);
        if (success) {
            return ResponseEntity.status(HttpStatus.CREATED).body("امتیاز و نظر شما با موفقیت ثبت شد.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("خطا: شما قبلاً برای این آگهی نظر ثبت کرده‌اید.");
    }

    /**
     * مشاهده نظرات و امتیازهای یک فروشنده در صفحه پروفایل او
     * GET http://localhost:8080/api/reviews/user/{username}
     */
    @GetMapping("/user/{username}")
    public ResponseEntity<List<Review>> getUserReviews(@PathVariable String username) {
        List<Review> reviews = reviewService.getReviewsForUser(username);
        return ResponseEntity.ok(reviews);
    }
}