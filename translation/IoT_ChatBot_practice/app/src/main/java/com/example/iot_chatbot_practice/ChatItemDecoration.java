package com.example.iot_chatbot_practice;
import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class ChatItemDecoration extends RecyclerView.ItemDecoration {

    // 聊天消息之間的間距
    private final int spacing;

    // 建構函式，用於初始化ChatItemDecoration的間距
    public ChatItemDecoration(int spacing) {
        this.spacing = spacing;
    }

    // 設置每個項目的間距，包括項目的上邊和下邊
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        // 設置項目的上邊和下邊的間距為指定的間距值
        outRect.top = spacing;
        outRect.bottom = spacing;
    }
}
