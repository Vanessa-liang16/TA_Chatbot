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

// MainActivity.java

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler_view_chat);
        chatAdapter = new ChatAdapter(new ArrayList<>());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatAdapter);

        int spacingBetweenBubbles = getResources().getDimensionPixelSize(R.dimen.spacing_between_bubbles);
        recyclerView.addItemDecoration(new ChatItemDecoration(spacingBetweenBubbles));

        Button sendButton = findViewById(R.id.button_send);
        EditText inputEditText = findViewById(R.id.edit_text_input);

        sendButton.setOnClickListener(v -> {
            String userMessage = inputEditText.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                addMessageToChat(new ChatMessage(userMessage, true));
                askChatGpt(userMessage);
                inputEditText.setText("");
            }
        });
    }

    private void addMessageToChat(ChatMessage chatMessage) {
        chatAdapter.addMessage(chatMessage);
        recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
    }

    private void askChatGpt(String userPrompt) {
        OpenAIAPIClient.OpenAIAPIService apiService = OpenAIAPIClient.create();

        // Add the system message to guide the model
        Message systemMessage = new Message("system",
                "You are an order processing bot. When the user gives an order, respond with a JSON object in the following format: \n" +
                        "{\n" +
                        "   \"date\" : \"<date>\",\n" +
                        "   \"order\" : \"<order details>\",\n" +
                        "   \"to_address\" : \"<delivery address>\",\n" +
                        "   \"notes\" : \"<any additional notes>\"\n" +
                        "}.\n" +
                        "Make sure the response matches this exact format without any code blocks or extra characters.");
        Message message = new Message("user", userPrompt);
        List<Message> messageList = new ArrayList<>();
        messageList.add(systemMessage);
        messageList.add(message);

        OpenAIRequestModel requestModel = new OpenAIRequestModel("gpt-4o-mini", messageList, 0.7f);

        Call<OpenAIResponseModel> call = apiService.getCompletion(requestModel);
        call.enqueue(new Callback<OpenAIResponseModel>() {
            @Override
            public void onResponse(Call<OpenAIResponseModel> call, Response<OpenAIResponseModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OpenAIResponseModel responseBody = response.body();
                    String generatedText = responseBody.getChoices()[0].getMessage().getContent();

                    // 移除可能出现的 ```json 标记
                    generatedText = generatedText.replaceAll("```json", "").replaceAll("```", "").trim();

                    addMessageToChat(new ChatMessage(generatedText, false));
                } else {
                    addMessageToChat(new ChatMessage("API error", false));
                }
            }

            @Override
            public void onFailure(Call<OpenAIResponseModel> call, Throwable t) {
                addMessageToChat(new ChatMessage("API onFailure: " + t.getMessage(), false));
            }
        });
    }
}

