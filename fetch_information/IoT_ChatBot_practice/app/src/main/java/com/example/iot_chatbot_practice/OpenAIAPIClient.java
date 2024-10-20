package com.example.iot_chatbot_practice;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public class OpenAIAPIClient {

    // 定義OpenAI服務的基本URL
    private static final String BASE_URL = "https://api.openai.com/v1/";

    // 定義OpenAI服務的API接口，使用Retrofit的方式進行HTTP請求
    public interface OpenAIAPIService {
        @Headers("Authorization: Bearer OpenAI_API_key")
        @POST("chat/completions")
        Call<OpenAIResponseModel> getCompletion(@Body OpenAIRequestModel requestModel);
    }

    // 創建OpenAI服務的實例
    public static OpenAIAPIService create() {
        // 使用Retrofit建立Retrofit實例，並設定基本URL和JSON轉換器
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 返回OpenAI服務的實例
        return retrofit.create(OpenAIAPIService.class);
    }
}
