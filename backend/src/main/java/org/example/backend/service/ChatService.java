package org.example.backend.service;

import org.example.backend.model.Conversation;
import org.example.backend.model.Message;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    // فیلدهای ریپازیتوری در آینده اینجا اینجکت می‌شوند:
    // private final ConversationRepository conversationRepository;
    // private final UserRepository userRepository;

    public ChatService() {
        // Constructor
    }

    /**
     * سناریوی شروع گفتگو یا ارسال پیام:
     * ۱. بررسی اینکه خریدار به آگهی خودش پیام ندهد.
     * ۲. بررسی اینکه خریدار یا فروشنده مسدود نباشند.
     * ۳. اگر چت از قبل وجود دارد، پیام به همان اضافه شود؛ در غیر این صورت چت جدید ساخته شود.
     */
    public Optional<Conversation> startOrSendMessage(String advertisementId, String buyerId, String sellerId, String messageText) {
        // TODO: چک کردن اینکه buyerId == sellerId نباشد (خطا: کاربر نمی‌تواند به آگهی خودش پیام دهد)
        // TODO: چک کردن وضعیت مسدود نبودن کاربران از طریق UserRepository
        // TODO: بررسی اینکه آیا از قبل Conversation با این مشخصات وجود دارد یا خیر
        // TODO: ذخیره پیام جدید و بازگرداندن آبجکت کانورزیشن به روز شده
        return Optional.empty();
    }

    /**
     * سناریوی مشاهده لیست گفتگوهای خود:
     * واکشی تمام گفتگوهایی که کاربر در آن‌ها یا "خریدار" است یا "فروشنده".
     */
    public List<Conversation> getUserConversations(String userId) {
        // TODO: فیلتر کردن لیست کل چت‌ها بر اساس اینکه buyerId یا sellerId برابر با userId باشد
        return Collections.emptyList();
    }

    /**
     * سناریوی دریافت پیام‌های یک گفتگو:
     * دریافت تاریخچه پیام‌های یک چت خاص به ترتیب زمان ارسال.
     */
    public List<Message> getMessagesInConversation(String conversationId) {
        // TODO: پیدا کردن چت بر اساس آیدی و بازگرداندن لیست پیام‌های آن (مرتب شده بر اساس تاریخ)
        return Collections.emptyList();
    }
}