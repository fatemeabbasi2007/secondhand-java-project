package org.example.backend.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Data
public class AdvertisementDetailDTO {
    // مرحله ۴: اطلاعات پایه آگهی
    private String id;
    private String title;
    private String description;
    private double price;
    private String city;
    private String categoryId;
    private String categoryName; // ارسال نام دسته بندی علاوه بر ID کار فرانت را راحت می‌کند
    private List<String> imageUrls;
    private LocalDateTime createdAt;

    // مرحله ۴: ویژگی‌های اختصاصی (مثلا کیلومتر برای ماشین، متراژ برای ملک)
    private List<AttributeRenderDTO> specificAttributes;
    public static class AttributeRenderDTO {
        private String label; // مثلاً: "کارکرد (کیلومتر)"
        private String value; // مثلاً: "50000"
        public AttributeRenderDTO(String label, String value) {
            this.label = label;
            this.value = value;
        }
        public String getLabel() {
            return label;
        }
        public String getValue() {
            return value;
        }
    }

    // مرحله ۵ و ۷: اطلاعات فروشنده برای نمایش و شروع گفت‌وگو
    private String ownerId;
    private String ownerName;
    private Double ownerAverageRating; // میانگین امتیاز فروشنده

    // مرحله ۸: فلگ کمکی برای کنترل وضعیت دکمه‌های UI در فرانت‌اند
    private boolean isOwner;

    // Constructor ها، Getter ها و Setter ها
    public AdvertisementDetailDTO() {}

    // ... (Getter/Setter ها در این بخش قرار می‌گیرند)
}