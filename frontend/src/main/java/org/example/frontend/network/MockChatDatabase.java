package org.example.frontend.network;

import org.example.frontend.model.ChatSession;
import org.example.frontend.model.ChatMessage;
import java.util.ArrayList;
import java.util.List;

public class MockChatDatabase {
    // لیست تمام گفتگوهای کل سیستم به صورت شبیه‌سازی شده
    private static final List<ChatSession> activeChats = new ArrayList<>();
    private static int chatCounter = 1;

    static {
        // ساخت یک چت پیش‌فرض برای تست
        ChatSession defaultChat = new ChatSession(chatCounter++, 103, "کتاب مهندسی اینترنت", 2, "نگار", 150, "علی رضایی");
        defaultChat.addMessage(new ChatMessage(150, "علی رضایی", "سلام خانم نگار، کتاب هنوز موجوده. تمایل به خرید دارید؟"));
        defaultChat.addMessage(new ChatMessage(2, "نگار", "سلام بله، تخفیف هم داره؟"));
        activeChats.add(defaultChat);
    }

    /**
     * پیدا کردن یا ساخت یک گفتگوی جدید (پیاده‌سازی مراحل ۵، ۶، ۷، ۸ و ۹ سناریو)
     */
    public static ChatSession startOrGetChat(int adId, String adTitle, int buyerId, String buyerName, int sellerId, String sellerName, String initialMessage) throws Exception {

        // مرحله ۵: بررسی اینکه به خودش پیام ندهد
        if (buyerId == sellerId) {
            throw new Exception("شما نمی‌توانید برای آگهی خودتان پیام ارسال کنید!");
        }

        // مرحله ۶: شبیه‌سازی بررسی مسدود نبودن کاربران
        if (sellerId == 666) { // فرض کنیم آیدی ۶۶۶ مسدود است
            throw new Exception("این کاربر مسدود شده است و امکان ارسال پیام به او وجود ندارد.");
        }

        // مرحله ۷: بررسی وجود گفتگوی قبلی برای همین خریدار و فروشنده روی همین آگهی
        for (ChatSession chat : activeChats) {
            if (chat.getAdId() == adId && chat.getBuyerId() == buyerId && chat.getSellerId() == sellerId) {
                if (initialMessage != null && !initialMessage.trim().isEmpty()) {
                    chat.addMessage(new ChatMessage(buyerId, buyerName, initialMessage));
                }
                return chat;
            }
        }

        // مرحله ۸: اگر وجود نداشت، ساخت چت جدید
        ChatSession newChat = new ChatSession(chatCounter++, adId, adTitle, buyerId, buyerName, sellerId, sellerName);

        // مرحله ۹: ذخیره پیام اولیه
        if (initialMessage != null && !initialMessage.trim().isEmpty()) {
            newChat.addMessage(new ChatMessage(buyerId, buyerName, initialMessage));
        }

        activeChats.add(newChat);
        return newChat;
    }

    /**
     * مرحله ۱۰: دریافت تمام گفتگوهای مربوط به کاربر لاگین شده (چه خریدار باشد چه فروشنده)
     */
    public static List<ChatSession> getChatsForUser(int userId) {
        List<ChatSession> userChats = new ArrayList<>();
        for (ChatSession chat : activeChats) {
            if (chat.getBuyerId() == userId || chat.getSellerId() == userId) {
                userChats.add(chat);
            }
        }
        return userChats;
    }
}