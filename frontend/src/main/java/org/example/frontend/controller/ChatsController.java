package org.example.frontend.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.example.frontend.model.ChatMessage;
import org.example.frontend.model.ChatSession;
import org.example.frontend.network.NetworkService; // اضافه شدن سرویس شبکه واقعی
import org.example.frontend.network.SessionManager;

import java.util.List;

public class ChatsController {

    @FXML private ListView<ChatSession> chatListView;
    @FXML private Label chatTitleLabel;
    @FXML private Label adTitleLabel;
    @FXML private ScrollPane chatScrollPane;
    @FXML private VBox messagesContainer;
    @FXML private TextField messageInputField;
    @FXML private Button btnSendMessage;

    private ChatSession selectedSession = null;
    private final NetworkService networkService = new NetworkService(); // تعریف پستچی شبکه واقعی

    @FXML
    public void initialize() {
        setupChatListView();
        loadUserChats();
    }

    /**
     * تنظیم نحوه نمایش چت‌ها در منوی سمت راست
     */
    private void setupChatListView() {
        chatListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(ChatSession item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    int currentUserId = SessionManager.getUserId(); //
                    // مشخص کردن نام مخاطب (اگر کاربر خریدار است، نام فروشنده و بالعکس)
                    String contactName = (currentUserId == item.getBuyerId()) ? item.getSellerName() : item.getBuyerName();

                    VBox cellContainer = new VBox(5);
                    cellContainer.setPadding(new Insets(8, 10, 8, 10));

                    HBox topRow = new HBox(10);
                    Label nameLbl = new Label("👤 " + contactName);
                    nameLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2C3E50;");

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    // زمان آخرین پیام
                    Label timeLbl = new Label(item.getLastMessageTimeFormatted());
                    timeLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #95A5A6;");

                    topRow.getChildren().addAll(nameLbl, spacer, timeLbl);

                    // پیش‌نمایش آخرین پیام و عنوان آگهی
                    ChatMessage lastMsg = item.getLastMessage(); //
                    String previewText = (lastMsg != null) ? lastMsg.getText() : "هنوز پیامی ارسال نشده است.";
                    if (previewText.length() > 30) {
                        previewText = previewText.substring(0, 27) + "...";
                    }

                    Label lastMsgLbl = new Label(previewText);
                    lastMsgLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");

                    Label adLbl = new Label("🎯 آگهی: " + item.getAdTitle()); //
                    adLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #3498DB; -fx-font-style: italic;");

                    cellContainer.getChildren().addAll(topRow, lastMsgLbl, adLbl);
                    setGraphic(cellContainer);
                }
            }
        });

        // شنونده کلیک روی هر گفتگو در لیست
        chatListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                openChatSession(newVal);
            }
        });
    }

    /**
     * بارگذاری چت‌های کاربر به صورت زنده از بک‌اَند
     */
    private void loadUserChats() {
        chatListView.getItems().clear();
        try {
            // دریافت لیست چت‌های واقعی از سرور
            List<ChatSession> userChats = networkService.getUserChats();
            chatListView.getItems().addAll(userChats);

            if (!chatListView.getItems().isEmpty()) {
                chatListView.getSelectionModel().selectFirst();
            } else {
                chatTitleLabel.setText("گفتگویی وجود ندارد");
                adTitleLabel.setText("");
            }
        } catch (Exception e) {
            chatTitleLabel.setText("خطا در بارگذاری چت‌ها");
            adTitleLabel.setText(e.getMessage());
        }
    }

    private void openChatSession(ChatSession session) {
        this.selectedSession = session;
        int currentUserId = SessionManager.getUserId(); //
        String contactName = (currentUserId == session.getBuyerId()) ? session.getSellerName() : session.getBuyerName(); //

        chatTitleLabel.setText("گفتگو با " + contactName);
        adTitleLabel.setText("آگهی: " + session.getAdTitle()); //

        renderMessages();
    }

    private void renderMessages() {
        messagesContainer.getChildren().clear();
        if (selectedSession == null) return;

        int currentUserId = SessionManager.getUserId(); //

        // رندر کردن تک‌تک پیام‌های موجود در گفتگو
        for (ChatMessage msg : selectedSession.getMessages()) { //
            HBox bubbleWrapper = new HBox();
            bubbleWrapper.setPadding(new Insets(5, 10, 5, 10));
            bubbleWrapper.setMaxWidth(Double.MAX_VALUE);

            Label messageBubble = new Label(msg.getText()); //
            messageBubble.setWrapText(true);
            messageBubble.setMaxWidth(300);

            // تشخیص اینکه پیام ارسالی از سمت خودمان است یا مخاطب
            if (msg.getSenderId() == currentUserId) {
                // پیام‌های ارسالی خودمان (سمت راست - آبی)
                bubbleWrapper.setAlignment(Pos.CENTER_RIGHT);
                messageBubble.setStyle(
                        "-fx-background-color: #0084FF; " +
                                "-fx-text-fill: white; " +
                                "-fx-background-radius: 12 12 0 12; " +
                                "-fx-padding: 8 12 8 12; " +
                                "-fx-font-size: 13px;"
                );
            } else {
                // پیام‌های دریافتی از مخاطب (سمت چپ - طوسی)
                bubbleWrapper.setAlignment(Pos.CENTER_LEFT);
                messageBubble.setStyle(
                        "-fx-background-color: #E4E6EB; " +
                                "-fx-text-fill: #1C1E21; " +
                                "-fx-background-radius: 12 12 12 0; " +
                                "-fx-padding: 8 12 8 12; " +
                                "-fx-font-size: 13px;"
                );
            }

            bubbleWrapper.getChildren().add(messageBubble);
            messagesContainer.getChildren().add(bubbleWrapper);
        }

        // اسکرول اتوماتیک به پایین‌ترین پیام بعد از رندر
        chatScrollPane.setVvalue(1.0);
    }

    /**
     * ارسال پیام جدید درون گفتگو به سرور واقعی
     */
    @FXML
    private void handleSendMessage() {
        String text = messageInputField.getText().trim();
        if (text.isEmpty() || selectedSession == null) return; //

        try {
            // ۱. ارسال پیام به بک‌اَند و گرفتن خروجی تایید شده
            ChatMessage savedMsg = networkService.sendMessage(selectedSession.getChatId(), text); //

            // ۲. اضافه کردن پیام تایید شده به شیء محلی گفتگو برای به‌روزرسانی آنی صفحه
            selectedSession.addMessage(savedMsg);

            // ۳. پاک کردن فیلد ورودی و بازنویسی پیام‌ها روی صفحه
            messageInputField.clear();
            renderMessages();

            // به‌روزرسانی لیست سمت راست برای نشان دادن آخرین پیام جدید
            chatListView.refresh();

        } catch (Exception e) {
            // نمایش خطا در صورت عدم ارسال پیام
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("خطا در ارسال");
            alert.setHeaderText(null);
            alert.setContentText("پیام ارسال نشد: " + e.getMessage());
            alert.showAndWait();
        }
    }
}