package com.example.iot_chatbot_assistantapi;

public class OpenAIResponseModel {

    public static class Threads {
        private String id;

        public String getId() {
            return id;
        }
    }

    public static class Assistants {
        private String id;

        public String getId() {
            return id;
        }
    }

    public static class Run {
        private String id;

        public String getId() { return id; }

    }

    public static class RunStatus {
        private String status;

        public String getStatus() { return status; }

    }

}
