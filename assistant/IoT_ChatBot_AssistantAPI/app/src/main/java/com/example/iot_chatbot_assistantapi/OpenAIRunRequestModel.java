package com.example.iot_chatbot_assistantapi;
import com.google.gson.annotations.SerializedName;

public class OpenAIRunRequestModel {
    @SerializedName("assistant_id")
    private String assistantId;

    public OpenAIRunRequestModel(String assistantId) {
        this.assistantId = assistantId;
    }
}

