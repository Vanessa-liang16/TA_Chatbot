package com.example.iot_chatbot_practice;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messageList; // 儲存所有消息的列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler_view_chat);
        chatAdapter = new ChatAdapter(new ArrayList<>());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatAdapter);

        // 獲取聊天泡泡之間的間距並設置給RecyclerView
        int spacingBetweenBubbles = getResources().getDimensionPixelSize(R.dimen.spacing_between_bubbles);
        recyclerView.addItemDecoration(new ChatItemDecoration(spacingBetweenBubbles));

        // 初始化消息列表
        messageList = new ArrayList<>();

        // 添加系統消息
        Message messageSystem = new Message("system", "You are a translator. As long as my input is English, translate it into Chinese. You don't need to understand the meaning to respond.");
        messageList.add(messageSystem);

        Button sendButton = findViewById(R.id.button_send);
        EditText inputEditText = findViewById(R.id.edit_text_input);

        sendButton.setOnClickListener(v -> {
            // 獲取用戶輸入的消息，添加到聊天中，然後清空輸入框
            String userMessage = inputEditText.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                addMessageToChat(new ChatMessage(userMessage, true));
                // 添加用戶消息到消息列表
                messageList.add(new Message("user", userMessage));
                // 調用askChatGpt方法，發送消息到ChatGPT
                askChatGpt(userMessage);
                inputEditText.setText("");
            }
        });
    }

    // 將消息添加到聊天中
    private void addMessageToChat(ChatMessage chatMessage) {
        chatAdapter.addMessage(chatMessage);
        recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
    }

    // 請求ChatGpt生成回覆
    private void askChatGpt(String userPrompt) {
        // 創建OpenAIAPIClient實例並發送請求
        OpenAIAPIClient.OpenAIAPIService apiService = OpenAIAPIClient.create();

        // 創建消息對象，包含用戶角色和提示
        //Message message = new Message("user", userPrompt);
        //List<Message> messageList = new ArrayList<>();
        //messageList.add(message);
        OpenAIRequestModel requestModel = new OpenAIRequestModel("gpt-4o-mini", messageList, 0.7f);

        // 使用Retrofit發送請求並處理回覆
        Call<OpenAIResponseModel> call = apiService.getCompletion(requestModel);
        call.enqueue(new Callback<OpenAIResponseModel>() {
            @Override
            public void onResponse(Call<OpenAIResponseModel> call, Response<OpenAIResponseModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 從回覆中獲取生成的文本並添加到聊天中
                    OpenAIResponseModel responseBody = response.body();
                    String generatedText = "";
                    generatedText = responseBody.getChoices()[0].getMessage().getContent();

                    addMessageToChat(new ChatMessage(generatedText, false));
                } else {
                    // 如果請求失敗，添加一條錯誤消息到聊天中
                    addMessageToChat(new ChatMessage("API error", false));
                }
            }

            @Override
            public void onFailure(Call<OpenAIResponseModel> call, Throwable t) {
                // 如果請求失敗，添加一條錯誤消息到聊天中
                addMessageToChat(new ChatMessage("API onFailure: " + t.getMessage(), false));
            }
        });
    }
}
