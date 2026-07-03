package org.example.backend.controller;

import org.example.backend.model.Advertisement;
import org.example.backend.service.AdvertisementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/advertisements")
public class AdvertisementController {

    private final AdvertisementService advertisementService;

    public AdvertisementController(AdvertisementService advertisementService) {
        this.advertisementService = advertisementService;
    }

    // ==========================================
    // ۱. عملیات کاربران عادی (صفحه اصلی و بازارچه)
    // ==========================================

    /**
     * سناریوی مشاهده، جستجو و فیلتر آگهی‌ها (فقط آگهی‌های ACTIVE)
     * GET http://localhost:8080/api/advertisements/search
     */
    @GetMapping("/search")
    public ResponseEntity<List<Advertisement>> searchAds(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {

        List<Advertisement> results = advertisementService.searchAndFilterActiveAds(keyword, category, city, minPrice, maxPrice);
        return ResponseEntity.ok(results);
    }

    /**
     * سناریوی مشاهده جزئیات کامل یک آگهی
     * GET http://localhost:8080/api/advertisements/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getAdDetail(@PathVariable String id) {
        Optional<Advertisement> ad = advertisementService.getActiveAdvertisementDetail(id);
        if (ad.isPresent()) {
            return ResponseEntity.ok(ad.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("خطا: آگهی یافت نشد یا هنوز تایید نشده است.");
    }

    /**
     * سناریوی ثبت آگهی جدید (همراه با وضعیت پیش‌فرض در انتظار بررسی)
     * POST http://localhost:8080/api/advertisements/create
     */
    @PostMapping("/create")
    public ResponseEntity<String> createAd(@RequestBody Advertisement advertisement, @RequestParam List<String> imageUrls) {
        // اعتبار سنجی قیمت منفی در این بخش کنترل می‌شود
        if (advertisement.getPrice() != null && advertisement.getPrice() < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("خطا: قیمت وارد شده معتبر نیست.");
        }
        advertisementService.createNewAdvertisement(advertisement, imageUrls);
        return ResponseEntity.status(HttpStatus.CREATED).body("آگهی با موفقیت ثبت شد و در انتظار بررسی مدیر است.");
    }

    /**
     * ویرایش آگهی توسط خود کاربر
     */
    @PutMapping("/own/{adId}")
    public ResponseEntity<String> editOwnAd(@PathVariable String adId, @RequestParam String userId, @RequestBody Advertisement updatedAd) {
        boolean success = advertisementService.updateOwnAdvertisement(adId, userId, updatedAd);
        if (success) return ResponseEntity.ok("آگهی با موفقیت ویرایش شد.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("خطا: شما اجازه ویرایش این آگهی را ندارید.");
    }

    /**
     * حذف آگهی توسط خود کاربر
     */
    @DeleteMapping("/own/{adId}")
    public ResponseEntity<String> deleteOwnAd(@PathVariable String adId, @RequestParam String userId) {
        boolean success = advertisementService.deleteOwnAdvertisement(adId, userId);
        if (success) return ResponseEntity.ok("آگهی با موفقیت حذف شد.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("خطا: شما اجازه حذف این آگهی را ندارید.");
    }

    /**
     * تغییر وضعیت آگهی به فروخته شده توسط مالک
     * PATCH http://localhost:8080/api/advertisements/own/{adId}/sold
     */
    @PatchMapping("/own/{adId}/sold")
    public ResponseEntity<String> markAsSold(@PathVariable String adId, @RequestParam String userId) {
        boolean success = advertisementService.changeAdStatusToSold(adId, userId);
        if (success) return ResponseEntity.ok("وضعیت آگهی به فروخته‌شده تغییر کرد.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("خطا: عملیات غیرمجاز.");
    }

    // ==========================================
    // ۲. عملیات پنل مدیریت (Admin Dashboard)
    // ==========================================

    /**
     * مشاهده لیست آگهی‌های در انتظار بررسی توسط مدیر
     * GET http://localhost:8080/api/advertisements/admin/pending
     */
    @GetMapping("/admin/pending")
    public ResponseEntity<List<Advertisement>> getPendingAdsForAdmin() {
        List<Advertisement> pendingAds = advertisementService.getPendingAdvertisementsForAdmin();
        return ResponseEntity.ok(pendingAds);
    }

    /**
     * تایید آگهی توسط مدیر
     */
    @PostMapping("/admin/{adId}/approve")
    public ResponseEntity<String> approveAd(@PathVariable String adId) {
        boolean success = advertisementService.approveAdvertisement(adId);
        if (success) return ResponseEntity.ok("آگهی تایید شد و به لیست عمومی اضافه گردید.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("آگهی پیدا نشد.");
    }

    /**
     * رد آگهی توسط مدیر همراه با ثبت دلیل رد
     */
    @PostMapping("/admin/{adId}/reject")
    public ResponseEntity<String> rejectAd(@PathVariable String adId, @RequestParam String reason) {
        boolean success = advertisementService.rejectAdvertisement(adId, reason);
        if (success) return ResponseEntity.ok("آگهی رد شد و دلیل آن ثبت گردید.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("آگهی پیدا نشد.");
    }

    /**
     * حذف آگهی نامناسب توسط مدیر
     */
    @DeleteMapping("/admin/{adId}")
    public ResponseEntity<String> deleteInappropriateAd(@PathVariable String adId) {
        boolean success = advertisementService.deleteInappropriateAdByAdmin(adId);
        if (success) return ResponseEntity.ok("آگهی نامناسب با موفقیت توسط مدیریت حذف شد.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("آگهی پیدا نشد.");
    }
}