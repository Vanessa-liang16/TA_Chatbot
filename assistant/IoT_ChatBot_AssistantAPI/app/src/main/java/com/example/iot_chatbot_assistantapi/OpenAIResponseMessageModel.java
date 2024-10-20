package com.example.iot_chatbot_assistantapi;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class OpenAIResponseMessageModel {
    @SerializedName("data")
    List<Message> data;

    List<Message> getData() {
        return data;
    }
}

class Message {
    @SerializedName("content")
    List<Content> content;

    // Getter
    List<Content> getContent() {
        return content;
    }
}

class Content {
    @SerializedName("text")
    TextContent text;

    // Getter
    TextContent getText() {
        return text;
    }
}

class TextContent {
    @SerializedName("value")
    String value;

    // Getter
    String getValue() {
        return value;
    }
}

