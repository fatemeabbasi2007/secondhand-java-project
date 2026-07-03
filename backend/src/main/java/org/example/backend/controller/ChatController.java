package org.example.backend.controller;

import org.example.backend.model.Conversation;
import org.example.backend.model.Message;
import org.example.backend.service.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * سناریوی شروع یک چت جدید یا ارسال پیام درون یک آگهی
     * POST http://localhost:8080/api/chats/send
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(
            @RequestParam String advertisementId,
            @RequestParam String buyerId,
            @RequestParam String sellerId,
            @RequestParam String messageText) {

        // بررسی اولیه برای قرارداد (کاربر نمی‌تواند به آگهی خودش پیام دهد)
        if (buyerId.equals(sellerId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("خطا: شما نمی‌توانید به آگهی خودتان پیام ارسال کنید.");
        }

        Optional<Conversation> conversation = chatService.startOrSendMessage(advertisementId, buyerId, sellerId, messageText);

        // در حالت ماک (قرارداد)، یک پاسخ فرضی موفق می‌دهیم تا فرانت‌اندمعطل نماند
        return ResponseEntity.ok("پیام با موفقیت ارسال و ثبت شد.");
    }

    /**
     * سناریوی مشاهده لیست گفتگوهای خود (صندوق ورودی چت‌ها)
     * فرانت‌اند لیست تمام چت‌های کاربر آنلاین را می‌گیرد تا در لیست چت‌ها نشان دهد.
     * GET http://localhost:8080/api/chats/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Conversation>> getUserInbox(@PathVariable String userId) {
        List<Conversation> conversations = chatService.getUserConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    /**
     * سناریوی مشاهده پیام‌ها (باز کردن چت اختصاصی)
     * دریافت تمام پیام‌های داخل یک گفتگوی خاص به ترتیب زمانی
     * GET http://localhost:8080/api/chats/{conversationId}/messages
     */
    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<List<Message>> getChatHistory(@PathVariable String conversationId) {
        List<Message> messages = chatService.getMessagesInConversation(conversationId);
        return ResponseEntity.ok(messages);
    }
}