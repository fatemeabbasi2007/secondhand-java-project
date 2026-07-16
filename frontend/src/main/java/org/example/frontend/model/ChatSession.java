package org.example.frontend.model;

import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;

public class ChatSession {
    private int chatId;
    private int adId;
    private String adTitle;
    private int buyerId;
    private int sellerId;
    private String sellerName;
    private String buyerName;
    private List<ChatMessage> messages;

    public ChatSession(){}

    public ChatSession(int chatId, int adId, String adTitle, int buyerId, String buyerName, int sellerId, String sellerName) {
        this.chatId = chatId;
        this.adId = adId;
        this.adTitle = adTitle;
        this.buyerId = buyerId;
        this.buyerName = buyerName;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.messages = new ArrayList<>();
    }

    // متد کمکی برای اضافه کردن پیام
    public void addMessage(ChatMessage msg) {
        this.messages.add(msg);
    }

    // متد جدید برای سناریوی دوم: گرفتن آخرین پیام برای نمایش در لیست
    public ChatMessage getLastMessage() {
        if (messages == null || messages.isEmpty()) {
            return null;
        }
        return messages.get(messages.size() - 1);
    }

    // متد جدید برای سناریوی دوم: گرفتن زمان آخرین پیام به فرمت ساعت و دقیقه
    public String getLastMessageTimeFormatted() {
        ChatMessage lastMsg = getLastMessage();
        if (lastMsg == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return lastMsg.getTimestamp().format(formatter);
    }

    // Getter ها
    public int getChatId() { return chatId; }
    public int getAdId() { return adId; }
    public String getAdTitle() { return adTitle; }
    public int getBuyerId() { return buyerId; }
    public int getSellerId() { return sellerId; }
    public String getSellerName() { return sellerName; }
    public String getBuyerName() { return buyerName; }
    public List<ChatMessage> getMessages() { return messages; }
    public void setChatId(int chatId) {this.chatId = chatId;}
    public void setAdId(int adId) {this.adId = adId;}
    public void setAdTitle(String adTitle) {this.adTitle = adTitle;}
    public void setBuyerId(int buyerId) {this.buyerId = buyerId;}
    public void setBuyerName(String buyerName) {this.buyerName = buyerName;}
    public void setMessages(List<ChatMessage> messages) {this.messages = messages;}
    public void setSellerId(int sellerId) {this.sellerId = sellerId;}
    public void setSellerName(String sellerName) {this.sellerName = sellerName;}

}