package com.example.iot_chatbot_assistantapi;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.ResponseBody;
import com.google.gson.Gson;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private String assistantId;
    private String threadId;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable checkStatusRunnable;

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

        Button sendButton = findViewById(R.id.button_send);
        EditText inputEditText = findViewById(R.id.edit_text_input);

        // 創建並取得Assistant id & Thread id
        createAssistant();

        sendButton.setOnClickListener(v -> {
            // 獲取用戶輸入的消息，添加到聊天中，然後清空輸入框
            String userMessage = inputEditText.getText().toString().trim();
            if (!userMessage.isEmpty() && threadId != null) {
                addMessageToChat(new ChatMessage(userMessage, true));
                // 調用askChatGpt方法，發送消息到ChatGPT
                askChatGpt(userMessage);
                inputEditText.setText("");
            } else if (threadId == null) {
                addMessageToChat(new ChatMessage("API error", false));
            }
        });
    }

    @Override
    protected void onDestroy() {
        // 在Activity被銷毀時刪除助手
        if (assistantId != null) {
            deleteAssistant(assistantId);
        }
        if (threadId != null) {
            deleteThread(threadId);
        }
        super.onDestroy();
    }

    private void createAssistant() {
        // 創建Assistant請求模型
        OpenAIAPIClient.OpenAIAPIService apiService = OpenAIAPIClient.create(this);
        OpenAIAssistantRequestModel assistantRequest = new OpenAIAssistantRequestModel(
                "Travel Assistant", // Update the name
                "You are a travel assistant. Provide very brief introductions about two sentences to places and recommend popular destinations when asked.", // Update the role
                Collections.emptyList(), //選擇工具
                "gpt-4o-mini" // Model version
        );
        // 發送創建Assistant的請求
        apiService.createAssistant(assistantRequest).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseJson = response.body().string();
                        Log.i("CreateAssistantResponse", responseJson);

                        // 獲取Assistant ID
                        Gson gson = new Gson();
                        OpenAIResponseModel.Assistants openAIResponse = gson.fromJson(responseJson, OpenAIResponseModel.Assistants.class);
                        assistantId = openAIResponse.getId();
                        //addMessageToChat(new ChatMessage("Assistant ID:" + assistantId, false));

                        // 創建並取得Thread id
                        getThreads();

                    } catch (Exception e) {
                        Log.e("CreateAssistantJson", e.getMessage());
                    }

                } else {
                    Log.w("CreateAssistantResponse", "Failed.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("CreateAssistantResponse", t.getMessage());
            }
        });
    }


    private void deleteAssistant(String assistantId) {
        // 發送刪除Assistant的請求
        OpenAIAPIClient.OpenAIAPIService apiService = OpenAIAPIClient.create(this);
        apiService.deleteAssistant(assistantId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.i("DeleteAssistantResponse","Success. " + assistantId);
                } else {
                    Log.w("DeleteAssistantResponse", "Failed. " + assistantId);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("DeleteAssistantResponse",t.getMessage());
            }
        });
    }

    private void getThreads() {
        // 發送創建Thread的請求
        OpenAIAPIClient.OpenAIAPIService apiService = OpenAIAPIClient.create(this);
        apiService.getThreads().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseJson = response.body().string();
                        Log.i("CreateThreadResponse",responseJson);

                        //取得Thread id
                        Gson gson = new Gson();
                        OpenAIResponseModel.Threads openAIResponse = gson.fromJson(responseJson, OpenAIResponseModel.Threads.class);
                        threadId = openAIResponse.getId();
                        //addMessageToChat(new ChatMessage("Thread ID:" + threadId, false));

                    } catch (Exception e) {
                        Log.e("CreateThreadJson", e.getMessage());
                    }


                } else {
                    Log.w("CreateThreadResponse", "Failed.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("DeleteResponse",t.getMessage());
            }
        });
    }

    private void deleteThread(String threadId) {
        // 發送刪除Thread的請求
        OpenAIAPIClient.OpenAIAPIService apiService = OpenAIAPIClient.create(this);
        apiService.deleteThread(threadId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.i("DeleteThreadResponse","Success. "+ threadId);
                } else {
                    Log.w("DeleteThreadResponse", "Failed." + threadId);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("DeleteThreadResponse",t.getMessage());
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
         OpenAIAPIClient.OpenAIAPIService apiService = OpenAIAPIClient.create(this);
         OpenAIMessageRequestModel messageRequest = new OpenAIMessageRequestModel("user", userPrompt);

         apiService.postMessageToThread(threadId, messageRequest).enqueue(new Callback<ResponseBody>() {
             @Override
             public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                 if (response.isSuccessful() && response.body() != null) {
                     try {
                         String responseJson = response.body().string();
                         Log.i("PostMessageJson", responseJson);

                         // 发起运行请求
                         getRun();

                     } catch (Exception e) {
                         Log.e("PostMessageJson", e.getMessage());
                     }
                 } else {
                     Log.w("PostMessageResponse", "Failed.");
                 }
             }

             @Override
             public void onFailure(Call<ResponseBody> call, Throwable t) {
                 Log.e("PostMessageResponse", t.getMessage());
             }
         });
     }

    private void getRun() {
        OpenAIAPIClient.OpenAIAPIService apiService = OpenAIAPIClient.create(this);
        OpenAIRunRequestModel runRequest = new OpenAIRunRequestModel(assistantId);

        apiService.createRun(threadId, runRequest).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseJson = response.body().string();
                        Log.i("RunJson", responseJson);

                        // 获取Run ID
                        Gson gson = new Gson();
                        OpenAIResponseModel.Run openAIResponse = gson.fromJson(responseJson, OpenAIResponseModel.Run.class);
                        String runId = openAIResponse.getId();

                        // 检查运行状态
                        checkRunStatus(runId);

                    } catch (Exception e) {
                        Log.e("RunJson", e.getMessage());
                    }

                } else {
                    Log.w("RunResponse", "Failed.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("RunResponse", t.getMessage());
            }
        });
    }

    private void checkRunStatus(final String runId) {
        checkStatusRunnable = new Runnable() {
            @Override
            public void run() {
                OpenAIAPIClient.OpenAIAPIService apiService = OpenAIAPIClient.create(MainActivity.this);

                apiService.getRun(threadId, runId).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                String responseJson = response.body().string();
                                Log.i("RunStatusJson", responseJson);

                                Gson gson = new Gson();
                                OpenAIResponseModel.RunStatus openAIResponse = gson.fromJson(responseJson, OpenAIResponseModel.RunStatus.class);
                                String status = openAIResponse.getStatus();

                                if ("completed".equals(status)) {
                                    getResponseMessage(); // 当status为completed时，获取回复消息
                                } else if ("requires_action".equals(status)) {
                                    // Handle cases where further action might be needed
                                    Log.i("RunStatus", "Action required. Implement handling here.");
                                    // Implement additional handling if needed
                                } else {
                                    handler.postDelayed(checkStatusRunnable, 3000); // 当status未进入completed，则每3秒检查一次运行(Run)状态
                                }

                            } catch (Exception e) {
                                Log.e("RunStatusJson", e.getMessage());
                            }

                        } else {
                            Log.w("RunStatusResponse", "Failed.");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("RunStatusResponse", t.getMessage());
                    }
                });
            }
        };

        handler.post(checkStatusRunnable);
    }

    private void getResponseMessage(){
        OpenAIAPIClient.OpenAIAPIService apiService = OpenAIAPIClient.create(MainActivity.this);

        apiService.getMessage(threadId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseJson = response.body().string();
                        Log.i("GetMessageJson",responseJson);

                        Gson gson = new Gson();
                        OpenAIResponseMessageModel responseModel = gson.fromJson(responseJson, OpenAIResponseMessageModel.class);

                        // 處理並顯示回覆文本
                        if (responseModel != null && responseModel.getData() != null) {
                            for (Message message : responseModel.getData()) {
                                if (message.getContent() != null && !message.getContent().isEmpty()) {
                                    Content content = message.getContent().get(0);
                                    if (content.getText() != null) {
                                        String messageValue = content.getText().getValue();
                                        
                                        addMessageToChat(new ChatMessage(messageValue, false));
                                    }
                                }
                            }
                        }

                    } catch (Exception e) {
                        Log.e("GetMessageJson", e.getMessage());
                    }

                } else {
                    Log.w("GetMessageResponse", "Failed.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("GetMessageResponse",t.getMessage());
            }
        });
    }

}
