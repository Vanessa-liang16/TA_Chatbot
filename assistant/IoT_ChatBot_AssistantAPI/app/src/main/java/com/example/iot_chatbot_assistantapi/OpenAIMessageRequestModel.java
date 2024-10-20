package com.example.iot_chatbot_assistantapi;
import com.google.gson.annotations.SerializedName;

public class OpenAIMessageRequestModel {
    @SerializedName("role")
    private String role;

    @SerializedName("content")
    private String content;

    public OpenAIMessageRequestModel(String role, String content) {
        this.role = role;
        this.content = content;
    }
}
