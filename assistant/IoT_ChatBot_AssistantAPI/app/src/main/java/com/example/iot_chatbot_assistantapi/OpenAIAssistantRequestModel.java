package com.example.iot_chatbot_assistantapi;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OpenAIAssistantRequestModel {

    @SerializedName("name")
    private String name;

    @SerializedName("instructions")
    private String instructions;

    @SerializedName("tools")
    private List<Tool> tools;

    @SerializedName("model")
    private String model;

    public OpenAIAssistantRequestModel(String name, String instructions, List<Tool> tools, String model) {
        this.name = name;
        this.instructions = instructions;
        this.tools = tools;
        this.model = model;
    }

    public static class Tool {
        @SerializedName("type")
        private String type;

        public Tool(String type) {
            this.type = type;
        }
    }
}

