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
                    String adTitle = item.getAdvertisementTitle() != null ? item.getAdvertisementTitle() : "بدون عنوان";
                    String otherUser = item.getOtherPartyUsername() != null ? item.getOtherPartyUsername() : "کاربر";

                    String lastMsg = item.getLastMessagePreview() != null ? item.getLastMessagePreview() : "پیامی ارسال نشده";
                    if (lastMsg.length() > 25) lastMsg = lastMsg.substring(0, 22) + "...";

                    // فرمت‌دهی زمان آخرین پیام
                    String rawTime = item.getLastMessageTime();
                    String timeFormatted = "-";
                    if (rawTime != null && !rawTime.isBlank()) {
                        timeFormatted = rawTime.replace("T", " ");
                        if (timeFormatted.length() > 16) {
                            timeFormatted = timeFormatted.substring(0, 16);
                        }
                    }

                    setText(String.format("آگهی: %s\nطرف مقابل: %s\nآخرین پیام: %s (%s)",
                            adTitle,
                            otherUser,
                            lastMsg,
                            timeFormatted));
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
                    String currentUserId = SessionManager.getInstance().getUserId();
                    String currentUsername = SessionManager.getInstance().getUsername();

                    // فرمت‌دهی زمان پیام
                    String rawTime = item.getSendAt();
                    String timeStr = "";
                    if (rawTime != null && !rawTime.isBlank()) {
                        timeStr = rawTime.replace("T", " ");
                        if (timeStr.length() > 16) {
                            timeStr = timeStr.substring(0, 16);
                        }
                    }

                    // بررسی هوشمند اینکه آیا پیام متعلق به کاربر جاری است یا خیر
                    boolean isMyMessage = item.getSenderId() != null && (
                            item.getSenderId().equalsIgnoreCase(currentUserId) ||
                                    item.getSenderId().equalsIgnoreCase(currentUsername)
                    );

                    if (isMyMessage) {
                        // پیام خود کاربر
                        setText("[شما]: " + item.getContent() + (timeStr.isEmpty() ? "" : " \n(" + timeStr + ")"));
                        setStyle("-fx-text-fill: #1565C0; -fx-padding: 5;");
                    } else {
                        // پیام طرف مقابل
                        String otherName = (selectedConversation != null && selectedConversation.getOtherPartyUsername() != null)
                                ? selectedConversation.getOtherPartyUsername()
                                : "طرف مقابل";
                        setText("[" + otherName + "]: " + item.getContent() + (timeStr.isEmpty() ? "" : " \n(" + timeStr + ")"));
                        setStyle("-fx-text-fill: #37474F; -fx-padding: 5;");
                    }
                }
            }
        });

        // ۳. شنونده انتخاب گفت‌وگو
        conversationsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedConversation = newVal;
                String adTitle = newVal.getAdvertisementTitle() != null ? newVal.getAdvertisementTitle() : "آگهی";
                String otherUser = newVal.getOtherPartyUsername() != null ? newVal.getOtherPartyUsername() : "کاربر";

                activeChatTitleLabel.setText("گفت‌وگو با: " + otherUser + " در رابطه با آگهی " + adTitle);
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

    private void loadMessages(String conversationId) {
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
            String adId = selectedConversation.getAdvertisementId().toString();
            // ارسال پیام به بک‌اند و ذخیره سازی
            chatService.sendMessage(adId, content);
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