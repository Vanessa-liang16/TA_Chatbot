package com.example.iot_chatbot_assistantapi;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.DELETE;
import retrofit2.http.Path;
import retrofit2.http.GET;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import okhttp3.ResponseBody;

import android.content.Context;

// 定義OpenAI API客戶端類別
public class OpenAIAPIClient {

    // 定義OpenAI服務的基礎URL
    private static final String BASE_URL = "https://api.openai.com/v1/";

    // 定義OpenAI API服務的接口，這裡利用Retrofit來描述RESTful API的HTTP請求
    public interface OpenAIAPIService {

        // 定義創建助手的POST請求
        @Headers("OpenAI-Beta: assistants=v2")
        @POST("assistants")
        Call<ResponseBody> createAssistant(@Body OpenAIAssistantRequestModel createRequestModel);

        // 定義刪除助手的DELETE請求
        @Headers("OpenAI-Beta: assistants=v2")
        @DELETE("assistants/{assistant_id}")
        Call<Void> deleteAssistant(@Path("assistant_id") String assistantId);

        // 定義獲取對話線程的POST請求
        @Headers("OpenAI-Beta: assistants=v2")
        @POST("threads")
        Call<ResponseBody> getThreads();

        // 定義刪除對話線程的DELETE請求
        @Headers("OpenAI-Beta: assistants=v2")
        @DELETE("threads/{thread_id}")
        Call<Void> deleteThread(@Path("thread_id") String assistantId);

        // 定義發送消息到對話線程的POST請求
        @Headers("OpenAI-Beta: assistants=v2")
        @POST("threads/{thread_id}/messages")
        Call<ResponseBody> postMessageToThread(
                @Path("thread_id") String threadId,
                @Body OpenAIMessageRequestModel messageRequest
        );

        // 定義創建運行請求的POST請求
        @Headers("OpenAI-Beta: assistants=v2")
        @POST("threads/{thread_id}/runs")
        Call<ResponseBody> createRun(
                @Path("thread_id") String threadId,
                @Body OpenAIRunRequestModel runRequest
        );

        // 定義獲取運行結果的GET請求
        @Headers("OpenAI-Beta: assistants=v2")
        @GET("threads/{thread_id}/runs/{run_id}")
        Call<ResponseBody> getRun(
                @Path("thread_id") String threadId,
                @Path("run_id") String runId
        );

        // 定義獲取對話消息的GET請求
        @Headers("OpenAI-Beta: assistants=v2")
        @GET("threads/{thread_id}/messages?order=desc&limit=1")
        Call<ResponseBody> getMessage(
                @Path("thread_id") String threadId
        );
    }

    // 創建並配置Retrofit客戶端，用於與OpenAI API進行通信
    public static OpenAIAPIService create(Context context) {
        // 從應用資源獲取API密鑰
        String apiKey = context.getString(R.string.openai_api_key);

        // 創建攔截器以添加API密鑰到請求頭中
        Interceptor authInterceptor = new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer " + apiKey)
                        .build();
                return chain.proceed(newRequest);
            }
        };

        // 使用Retrofit構建器配置和創建Retrofit實例
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL) // 設定基礎URL
                .addConverterFactory(GsonConverterFactory.create()) // 設定Gson轉換器
                .client(new OkHttpClient.Builder().addInterceptor(authInterceptor).build()) // 設定OkHttp客戶端和攔截器
                .build();

        // 返回配置好的OpenAI API服務實例
        return retrofit.create(OpenAIAPIService.class);
    }
}
