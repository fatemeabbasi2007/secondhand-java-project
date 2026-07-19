package org.example.frontend.controller;

import org.example.frontend.model.ConversationResponse;
import org.example.frontend.model.MessageResponse;
import org.example.frontend.service.ChatService;
import org.example.frontend.security.SessionManager;
import org.example.frontend.util.NavigationService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import java.util.List;

public class ChatController {

    @FXML private ListView<ConversationResponse> conversationsListView;
    @FXML private ListView<MessageResponse> messagesListView;
    @FXML private Label activeChatTitleLabel;
    @FXML private TextArea messageInputArea;

    private final ChatService chatService = new ChatService();
    private final ObservableList<ConversationResponse> conversationsList = FXCollections.observableArrayList();
    private final ObservableList<MessageResponse> messagesList = FXCollections.observableArrayList();

    private ConversationResponse selectedConversation;

    @FXML
    public void initialize() {
        conversationsListView.setItems(conversationsList);
        messagesListView.setItems(messagesList);

        // ۱. شخصی‌سازی سلول گفت‌وگوها (نمایش عنوان آگهی، کاربر، آخرین پیام و زمان آن)
        conversationsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(ConversationResponse item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String lastMsg = item.getLastMessage() != null ? item.getLastMessage() : "پیامی ارسال نشده";
                    if (lastMsg.length() > 25) lastMsg = lastMsg.substring(0, 22) + "...";

                    setText(String.format("آگهی: %s\nطرف مقابل: %s\nآخرین پیام: %s (%s)",
                            item.getAdvertisementTitle(),
                            item.getOtherPartyUsername(),
                            lastMsg,
                            item.getLastMessageTime() != null ? item.getLastMessageTime() : "-"));
                }
            }
        });

        // ۲. شخصی‌سازی سلول پیام‌ها
        messagesListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(MessageResponse item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    String currentUsername = SessionManager.getInstance().getUsername();
                    if (item.getSenderUsername().equalsIgnoreCase(currentUsername)) {
                        // پیام خود کاربر (چینش یا رنگ متفاوت)
                        setText("[شما]: " + item.getContent() + " \n(" + item.getTimestamp() + ")");
                        setStyle("-fx-text-fill: #1565C0; -fx-padding: 5;");
                    } else {
                        // پیام طرف مقابل
                        setText("[" + item.getSenderUsername() + "]: " + item.getContent() + " \n(" + item.getTimestamp() + ")");
                        setStyle("-fx-text-fill: #37474F; -fx-padding: 5;");
                    }
                }
            }
        });

        // شنونده انتخاب گفت‌وگو
        conversationsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedConversation = newVal;
                activeChatTitleLabel.setText("گفت‌وگو با: " + newVal.getOtherPartyUsername() + " در رابطه با آگهی " + newVal.getAdvertisementTitle());
                loadMessages(newVal.getConversationId());
            }
        });

        // بارگذاری اولیه صندوق ورودی پیام‌ها
        loadConversations();
    }

    private void loadConversations() {try {
        List<ConversationResponse> list = chatService.getConversations();
        conversationsList.setAll(list);
    } catch (Exception e) {
        showAlert(Alert.AlertType.ERROR, "خطا در دریافت گفت‌وگوها", e.getMessage());
    }
    }

    private void loadMessages(Long conversationId) {
        try {
            List<MessageResponse> messages = chatService.getMessages(conversationId);
            messagesList.setAll(messages);
            // اسکرول اتوماتیک به آخرین پیام ارسال شده
            messagesListView.scrollTo(messagesList.size() - 1);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "خطا در دریافت پیام‌ها", e.getMessage());
        }
    }

    // ۳. ارسال پیام جدید
    @FXML
    public void onSendMessageClick(ActionEvent event) {
        if (selectedConversation == null) {
            showAlert(Alert.AlertType.WARNING, "انتخاب چت", "لطفاً ابتدا یک گفت‌وگو را انتخاب کنید.");
            return;
        }

        String content = messageInputArea.getText().trim();
        if (content.isEmpty()) {
            messageInputArea.setStyle("-fx-border-color: red; -fx-border-width: 1.5px;");
            return;
        }
        messageInputArea.setStyle("");

        try {
            // ارسال پیام به بک‌اند و ذخیره سازی
            chatService.sendMessage(selectedConversation.getConversationId(), content);
            messageInputArea.clear();

            // به‌روزرسانی پویای پیام‌های چت انتخابی برای نمایش فوری پیام ارسال شده به کاربر
            loadMessages(selectedConversation.getConversationId());

            // به‌روزرسانی لیست گفت‌وگوها برای اصلاح فیلد آخرین پیام
            loadConversations();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "خطا در ارسال پیام", e.getMessage());
        }
    }

    @FXML
    public void onBackClick(ActionEvent event) {
        NavigationService.navigate(event, "main-view.fxml", "صفحه اصلی آگهی‌ها");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}