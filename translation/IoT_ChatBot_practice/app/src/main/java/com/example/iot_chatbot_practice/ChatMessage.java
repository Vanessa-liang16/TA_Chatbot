package com.example.iot_chatbot_practice;

public class ChatMessage {

    // 聊天消息的內容
    private String message;

    // 聊天消息的發送者角色，用戶或助手
    private boolean isUser;

    // 建構函式，用於初始化ChatMessage的屬性
    public ChatMessage(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
    }

    // 取得聊天消息的內容
    public String getMessage() {
        return message;
    }

    // 判斷聊天消息的發送者是否為用戶
    public boolean isUser() {
        return isUser;
    }
}
